package com.riverdevs.whosonmywifi.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import jcifs.netbios.NbtAddress;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.riverdevs.whosonmywifi.beans.PingResult;
import com.riverdevs.whosonmywifi.beans.WifiConnectionInfo;
import com.riverdevs.whosonmywifi.dao.DataManager;

public class NetUtils extends BaseNetUtils{
	
	public static boolean isWifiConnected(Context context){
		ConnectivityManager connManager = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		return mWifi.isConnected();
	}
	
	public static String findHostName(String ipAddress, String routerIp)
			throws IOException {
		
		// First, perform a reverse DNS lookup
		String[] splittedIp = ipAddress.split("\\.");

		String reversedIp = "";

		for (int i = splittedIp.length - 1; i >= 0; i--) {
			reversedIp += splittedIp[i];
			if (i > 0) {
				reversedIp += ".";
			}
		}

		String dnsblDomain = "in-addr.arpa";
		Record[] records;
		Lookup lookup = new Lookup(reversedIp + "." + dnsblDomain, Type.PTR);
		SimpleResolver resolver = new SimpleResolver();
		resolver.setAddress(InetAddress.getByName(routerIp));
		lookup.setResolver(resolver);
		records = lookup.run();

		switch(lookup.getResult()){
			case Lookup.SUCCESSFUL:
				for (int i = 0; i < records.length; i++) {
					if (records[i] instanceof PTRRecord) {
						return (records[0].rdataToString());
					}
				}
				break;
			
//			case Lookup.TYPE_NOT_FOUND:
//				return getNetBiosName(ipAddress);
//				String netBiosName = getNetBiosName(ipAddress);
//				if(netBiosName == null){
//					return ipAddress;	
//				}
//				else{
//					return netBiosName;
//				}
			
			// If wasn't found by reverse DNS, use Netbios name
			default:
				return getNetBiosName(ipAddress);			
		}
		
		return null;
	}

	public static String formatIpAddress(int ip) {
		return String.format(Locale.getDefault(), 
				"%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff),
				(ip >> 16 & 0xff), (ip >> 24 & 0xff));
	}

//	public static PingResult pingAndGetHostname(String url, String gateway, boolean getDisconnected) throws Exception {
//		InetAddress in = null;
//		in = InetAddress.getByName(url);
//		
//		PingResult pingResult = 
//				new PingResult(in.isReachable(getTimeoutTime()));	
//		
//		pingResult.setIp(url);
//		if(pingResult.isSuccess() || getDisconnected){
//			pingResult.setHostname(NetUtils.findHostName(url, gateway));
//			pingResult.setMacAddress(getMacAddress(url));
//		}
//		
//		return pingResult;
//	}

	public static PingResult completeData(PingResult pingResult, String gateway, boolean getDisconnected) throws Exception{
		if(pingResult.isSuccess() || getDisconnected){
			if(TextUtils.isEmpty(pingResult.getHostname())){
				pingResult.setHostname(NetUtils.findHostName(pingResult.getIp(), gateway));
			}
			pingResult.setMacAddress(getMacAddress(pingResult.getIp()));
			pingResult.setManufacturer(getManufacturerNameFromMAC(pingResult.getMacAddress()));
			pingResult.setGivenName(DataManager.getGivenNameFromMAC(pingResult.getMacAddress()));
		}
		
		return pingResult;
	}
	
	public static WifiConnectionInfo getMyWifiInfo(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

		WifiConnectionInfo connectionInfo = new WifiConnectionInfo();
		String ipString = NetUtils.formatIpAddress(wifiInfo.getIpAddress());
		String gatewayIpString = NetUtils.formatIpAddress(dhcpInfo.gateway);
		String networkMaskString = NetUtils.formatIpAddress(dhcpInfo.netmask);

		connectionInfo.setMyIp(ipString);
		connectionInfo.setGatewayIp(gatewayIpString);
		connectionInfo.setSubnetMask(networkMaskString);
		connectionInfo.setWifiName(wifiInfo.getSSID().replace("\"", ""));
		connectionInfo.setMac(wifiInfo.getMacAddress());
		connectionInfo.setNetworkId(wifiInfo.getNetworkId());
		try{
//			connectionInfo.setHostname(
//				NetUtils.findHostName(ipString, gatewayIpString));
			connectionInfo.setHostname(NetUtils.getMyHostName(ipString));
		}
		catch(Exception ex){
			ex.printStackTrace();
			connectionInfo.setHostname(ipString);
		}

		return connectionInfo;
	}
	
	public static String getNetPrefix(String ipAddress){
		String[] ipParts = ipAddress.split("\\.");
		return ipParts[0]+"."+ipParts[1]+"."+ipParts[2]+".";
	}
	
	public static boolean ping(String url){
		return ping(url, getTimeoutTime());		
	}
	
	public static boolean ping(String url, int timeout){
		InetAddress in = null;
		try {
			in = InetAddress.getByName(url);
			return in.isReachable(timeout);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getMacAddress(String ip){
		String macAddress = null;
		
		// First make a very short ping to be sure that the ip is on the arp file
		try {
			InetAddress in = InetAddress.getByName(ip);
			in.isReachable(20);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		if(new File("/proc/net/arp").exists()){			
			try{
			BufferedReader br = 
					new BufferedReader(new FileReader(
							new File("/proc/net/arp")));
			String aLine = br.readLine();
			// Start on the second line (the first one contain just the headers)
			while((aLine=br.readLine()) != null) {
				String[] splitLine = aLine.split("\\s+");
				if(splitLine[0].equals(ip) &&
						!splitLine[3].equals("00:00:00:00:00:00")){
					macAddress = splitLine[3];
				}
			}
			br.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		return macAddress;
	}
	
	public static String getManufacturerNameFromMAC(String mac){
		String manufacturer = null;

		try{
			String macIndex = (mac == null ? 
					null : mac.toUpperCase(Locale.getDefault()).substring(0, 8));

			if(macIndex == null){
				Log.d("check_mac", "Mac not found or null");
			}
			else{

				try{
					// First, try to get it from local DB
					manufacturer = DataManager.getManufacturerName(macIndex);
					if(manufacturer != null){
						Log.d("check_mac", "Manufacturer for " + macIndex + 
								" found in DB: " + manufacturer);
					}
					
					// Not found, check on Online service
					if(manufacturer == null){
						final HttpParams httpParams = new BasicHttpParams();
					    HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
						HttpClient httpclient = new DefaultHttpClient(httpParams);
						HttpGet request = 
								new HttpGet("https://macmanufacturer.herokuapp.com/name/"+macIndex);

						HttpResponse response = httpclient.execute(request);
						BufferedReader in = new BufferedReader(new InputStreamReader(
								response.getEntity().getContent()));

						if(response.getStatusLine().getStatusCode() == 200){
							manufacturer = in.readLine();
							if(!TextUtils.isEmpty(manufacturer)){
								DataManager.insertUpdateManufacturer(macIndex, manufacturer);	
							}
							Log.d("check_mac", "Mac index: " + macIndex + " Name:" + manufacturer);
						}
						else{
							Log.e("check_mac", "Request Manufacturer. " +
									"Error " + response.getStatusLine().getStatusCode());
						}
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
				}

			}
		}catch(Exception e){
			Log.e("check_mac", "Error getting name for mac "+ mac +" : "+e.toString());
		}

		return manufacturer;
		}

	public static boolean isMacInArpFile(String mac) {
		
		if(new File("/proc/net/arp").exists()){			
			try{
			BufferedReader br = 
					new BufferedReader(new FileReader(
							new File("/proc/net/arp")));
			String aLine = br.readLine();
			// Start on the second line (the first one contain just the headers)
			while((aLine=br.readLine()) != null) {
				String[] splitLine = aLine.split("\\s+");
				if(splitLine[3].equals(mac)){
					br.close();
					return true;
				}
			}
			br.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static String getNetBiosName(String ipAddress){
		try {
			return NbtAddress.getAllByAddress(ipAddress)[0].getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Retrieves the net.hostname system property
	 * @param defValue the value to be returned if the hostname could
	 * not be resolved
	 */
	public static String getMyHostName(String defValue) {
	    try {
	        Method getString = Build.class.getDeclaredMethod("getString", String.class);
	        getString.setAccessible(true);
	        return getString.invoke(null, "net.hostname").toString();
	    } catch (Exception ex) {
	        return defValue;
	    }
	}
}
