package com.riverdevs.whosonmywifi.utils;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;

import com.riverdevs.whosonmywifi.SettingsActivity;
import com.riverdevs.whosonmywifi.beans.PingResult;
import com.riverdevs.whosonmywifi.beans.WifiConnectionInfo;
import com.riverdevs.whosonmywifi.dao.DataManager;

/**
 * Just for testing in the emulator and base of the actual NetUtils class
 * (They will share some methods)
 * @author charro
 *
 */
public class BaseNetUtils {

	public static SharedPreferences preferences;
	public static String unknownString;
	
	public static final int MAX_IP_ENDING = 255;

	public static boolean getBooleanPreference(String key, boolean defaultValue){
		return preferences == null ? defaultValue : preferences.getBoolean(key, defaultValue);
	}
	
	public static int getIntPreference(String key, int defaultValue){
		return preferences == null ? defaultValue : Integer.valueOf(preferences.getString(key, String.valueOf(defaultValue)));
	}
	
	public static boolean isWifiConnected(Context context){
		return true;
	}
	
	public static String reverseDns(String ipAddress, String routerIp)
			throws IOException {
		return "Hostname";
	}

//	public static String formatIpAddress(int ip) {
//		return "192.168.1.1";
//	}

	public static PingResult pingAndGetHostname(String url, String gateway, boolean getDisconnected) throws Exception {
		
		Thread.sleep(getIntPreference(SettingsActivity.KEY_PREF_TIMEOUT_TO_PING, 10));
		
		PingResult pingResult = new PingResult(false);
		pingResult.setSuccess(ping(url));
		
		pingResult.setIp(url);
		if(pingResult.isSuccess() || getDisconnected){
			pingResult.setHostname(reverseDns(url, gateway));
			pingResult.setMacAddress(getMacAddress(pingResult.getIp()));
		}
		
		return pingResult;
	}

	public static PingResult completeData(PingResult pingResult, String gateway, boolean getDisconnected) throws Exception{
		if(pingResult.isSuccess() || getDisconnected){
			pingResult.setHostname(reverseDns(pingResult.getIp(), gateway));
			pingResult.setMacAddress(getMacAddress(pingResult.getIp()));
			// Find Manufacturer name
			pingResult.setManufacturer(getManufacturerNameFromMAC(pingResult.getMacAddress()));
		}
		
		return pingResult;
	}
	
	public static WifiConnectionInfo getMyWifiInfo(Context context) {
		WifiConnectionInfo connectionInfo = new WifiConnectionInfo();

		connectionInfo.setMyIp("192.168.1.17");
		connectionInfo.setGatewayIp("192.168.1.1");
		connectionInfo.setSubnetMask("255.255.255.0");
		connectionInfo.setWifiName("MyWifi");
		connectionInfo.setHostname("MyHostname");
		connectionInfo.setMac(getMacAddress(connectionInfo.getMyIp()));

		return connectionInfo;
	}
	
	public static boolean ping(String url){
		if(url.equals("192.168.1.1") ||
		   url.equals("192.168.1.11") ||
		   url.equals("192.168.1.17")){
			return true;
		}
		
		return false;
	}
	
	public static String getMacAddress(String ip){
		if(ip.equals("192.168.1.1")){
			return "XX:XX:XX:XX:XX:AA";
		}
		if(ip.equals("192.168.1.11")){
			return "XX:XX:XX:XX:XX:BB";
		}
		if(ip.equals("192.168.1.17")){
			return "XX:XX:XX:XX:XX:CC";
		}
				
		return null;
	}
	
	public static int getTimeoutTime(){
		return getIntPreference(SettingsActivity.KEY_PREF_TIMEOUT_TO_PING, 100);
	}
	
	public static String getManufacturerNameFromMAC(String mac){
		return DataManager.getManufacturerName(mac.substring(0, 6));	
	}
	
	public static String getNetPrefix(String ipAddress){
		return "192.168.1.";
	}
}
