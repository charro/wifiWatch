package com.riverdevs.whosonmywifi;

import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

import com.riverdevs.whosinmywifi.R;
import com.riverdevs.whosonmywifi.beans.DetectedConnection;
import com.riverdevs.whosonmywifi.beans.PingResult;
import com.riverdevs.whosonmywifi.beans.WifiConnectionInfo;
import com.riverdevs.whosonmywifi.dao.DataManager;
import com.riverdevs.whosonmywifi.utils.NetUtils;

public class MonitorConnectionThread extends Thread{

	public static int CONNECTED_CHANGED = 666;
	public static int MONITOR_FINISHED = 667;
	public static int NEW_CONNECTION_DETECTED = 668;
	
	public boolean stopFlag = false;
	private List<PingResult> resultList;
	private Handler handler;
	private long delayTime;
	private Context context;
//	private Scan scanner;
	
//	public MonitorConnectionThread(List<PingResult> resultList, 
//			Handler handler, long delayTime, 
//			Scan scanner) {
	public MonitorConnectionThread(Context context, List<PingResult> resultList, 
				Handler handler, long delayTime) {
		this.resultList = resultList;
		this.handler = handler;
		this.delayTime = delayTime;
//		this.scanner = scanner;
		this.context = context;
		
		Toast.makeText(context, 
				context.getString(R.string.checking_new_connections), 
				Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void run() {
		while(stopFlag == false){
			try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
//			List<String> foundByNmap = null;
//			if(scanner != null){
//				foundByNmap = scanner.performScan();
//			}
			
			for(PingResult result : resultList){
				if(stopFlag){
					break;
				}
				
				boolean isConnected = false;
//				
//				// First method
//				if(foundByNmap != null && !foundByNmap.isEmpty()){
//					isNotConnected = !foundByNmap.contains(result.getIp());
//				}
//				// Alternative method
//				else{
//					isNotConnected = !NetUtils.ping(result.getIp());
//				}
//				
//				// Another try, this time with Netbios
//				if(isNotResponding){
//					isNotConnected = !NetBiosCheck.sendToHost(result.getIp()).isSuccess();
//				}
				
				int MAX_RETRIES = 15;
				
				// Look for this device in ARP files (except if it's my own one)
				if(!result.getIp().equals(NetUtils.getMyWifiInfo(context).getMyIp())){
					
					int retry = 0;
					while(retry<MAX_RETRIES && !isConnected){
						isConnected = NetUtils.ping(result.getIp());
						retry++;
					}
					
//					isNotConnected = !NetUtils.isMacInArpFile(result.getMacAddress());
									
					boolean isNOTConnected = !isConnected;
					
					if(isNOTConnected != result.isNotCurrentlyConnected()){
						result.setNotCurrentlyConnected(isNOTConnected);

						Message message = handler.obtainMessage(CONNECTED_CHANGED, result);
						handler.sendMessage(message);
					}
				}

			}
			
			checkForNewConnections();
		}
		
		Message endThreadMessage = handler.obtainMessage(MONITOR_FINISHED);
		handler.sendMessage(endThreadMessage);
	}

	public void stopWork(){
		stopFlag = true;
	}
	
	public void setNewResultList(List<PingResult> resultList){
		this.resultList = resultList;
	}
	
	private void checkForNewConnections(){
		WifiConnectionInfo connectionInfo = NetUtils.getMyWifiInfo(context);
		String networkPrefix = NetUtils.getNetPrefix(connectionInfo.getMyIp());
		
		// Look for new reachable devices
		for(int i=1; i<NetUtils.MAX_IP_ENDING && !stopFlag; i++){
			String ip = networkPrefix+i;
			// Getting the MAC reads the ARP file after a short ping
			String macAddress = NetUtils.getMacAddress(ip);
			if(macAddress != null){
				// Check if it's new
				boolean isNew = true;
				
				for(PingResult previousResult: resultList){
					if(macAddress.equals(previousResult.getMacAddress())){
						isNew = false;
					}
				}

				if(isNew){
					PingResult newDeviceFound = new PingResult(true);
					newDeviceFound.setIp(ip);
//					newDeviceFound.setMacAddress(macAddress);
//					newDeviceFound.setManufacturer(NetUtils.getManufacturerNameFromMAC(macAddress));
					
					try {
						NetUtils.completeData(newDeviceFound, connectionInfo.getGatewayIp(), false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					DetectedConnection newDetectedConnection = 
							new DetectedConnection(new Date().getTime(), 
									newDeviceFound.getIp(), 
									newDeviceFound.getMacAddress(),
									newDeviceFound.getManufacturer());
					
					DataManager.insertDetectedConnection(newDetectedConnection);
					
					if(NetUtils.getBooleanPreference(SettingsActivity.KEY_PREF_SHOW_NOTIFICATION, true)){
						Intent resultIntent = new Intent(context, ConnectedHistoryActivity.class);
						
						NotificationCompat.Builder builder = new Builder(context);
						builder.
							setContentTitle(context.getString(R.string.app_name)).
							setContentText(context.getString(R.string.connection_detected) + 
									(newDeviceFound.getGivenName()==null ? 
											newDeviceFound.getIp() : newDeviceFound.getGivenName())).
							setSmallIcon(R.drawable.connected).
							setContentIntent(PendingIntent.getActivity(
								    context,
								    0,
								    resultIntent,
								    PendingIntent.FLAG_UPDATE_CURRENT
								));
						
						NotificationManager mNotifyMgr = 
						        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						// Builds the notification and issues it.
						Notification notification = builder.build();
						// Cancel the notification after its selected
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
								
						mNotifyMgr.notify(1, notification);	
					}
					
//					resultList.add(newDeviceFound);

					Message newDeviceMessage = handler.obtainMessage(NEW_CONNECTION_DETECTED, newDeviceFound);
					handler.sendMessage(newDeviceMessage);
//					handler.sendMessage(handler.obtainMessage(NEW_CONNECTION_DETECTED));
				}
			}
		}
	}
}
