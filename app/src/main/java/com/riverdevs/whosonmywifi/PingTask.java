package com.riverdevs.whosonmywifi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.riverdevs.whosonmywifi.beans.NetBiosResult;
import com.riverdevs.whosonmywifi.beans.PingResult;
import com.riverdevs.whosonmywifi.beans.WifiConnectionInfo;
import com.riverdevs.whosonmywifi.utils.NetBiosCheck;
import com.riverdevs.whosonmywifi.utils.NetUtils;
import com.riverdevs.whosonmywifi.utils.Utils;

public class PingTask extends AsyncTask<Boolean, PingResult, Void> {
	
	private MainActivity activity;
	private ArrayList<String> foundInArp;
	private long arpFileLastModified;
	private WifiConnectionInfo myConnectionInfo;
	
	public PingTask(MainActivity activity) {
		attach(activity);
		this.myConnectionInfo = activity.getMyConnectionInfo();
		activity.addressesLooked = 0;
	}
	
	@Override
	protected Void doInBackground(Boolean... params) {
		
//		String[] ipParts = myConnectionInfo.getMyIp().split("\\.");
//		String networkPrefix = ipParts[0]+"."+ipParts[1]+"."+ipParts[2]+".";
		String networkPrefix = NetUtils.getNetPrefix(myConnectionInfo.getMyIp());
		
		List<String> foundIpList = new ArrayList<String>();
		foundInArp = new ArrayList<String>();
		arpFileLastModified = 0;
		
		// Scanner null
//		scanner = null;
		
//		if(activity.getPreferences().getBoolean(SettingsActivity.KEY_PREF_INSTALLATION_SUCCESSFUL, false)){
//			// Perform quick scan
//			scanner = new Scan(Utils.getApplicationFolder(activity,
//					"bin"), networkPrefix+"1/24", "-sP", 0, activity);
//			
////			foundIpList = scanner.performScan();
//		}
				
		for(int i=1; i<NetUtils.MAX_IP_ENDING; i++){

			PingResult response = new PingResult(false);
			
			try {
				
				// Check if user cancelled to stop immediately
				if(isCancelled()){
					break;
				}
				
				String checkedIp = networkPrefix+i;
				response.setIp(checkedIp);
				
//				if(foundIpList!=null && foundIpList.contains(checkedIp)){
//					response.setSuccess(true);
//				}

				// Test NetBios
				NetBiosResult netBiosResult = NetBiosCheck.sendToHost(checkedIp);
//				response.setHostname(netBiosResult.getNetBiosName());
//				if(response.isSuccess()==false){
//					response.setSuccess(netBiosResult.isSuccess());
//				}
				if(netBiosResult.getNetBiosName()!=null &&
						TextUtils.isEmpty(netBiosResult.getNetBiosName())){
					for(PingResult result : MainActivity.getConnectedDevicesAdapter().getItemsList()){
						if(result.getIp().equals(checkedIp)){
							result.setHostname(netBiosResult.getNetBiosName());
							MainActivity.getConnectedDevicesAdapter().notifyDataSetChanged();
						}
					}
				}
				checkArp();
				for(String foundIp : foundInArp){
					if(!foundIpList.contains(foundIp)){
						foundIpList.add(foundIp);
						PingResult arpResult = new PingResult(true);
						arpResult.setIp(foundIp);
						NetUtils.completeData(arpResult, 
								myConnectionInfo.getGatewayIp(), params[0]);
						publishProgress(arpResult);
					}
				}
				
				if(checkedIp.equals(myConnectionInfo.getMyIp())){
					response.setIp(checkedIp);
					response.setHostname(myConnectionInfo.getHostname());
					response.setMacAddress(myConnectionInfo.getMac());
					response.setManufacturer(NetUtils.getManufacturerNameFromMAC(myConnectionInfo.getMac()));
					response.setSuccess(true);
					publishProgress(response);
				}
//				if(foundIpList==null || foundIpList.isEmpty()){
//					response = NetUtils.pingAndGetHostname(checkedIp, 
//							myConnectionInfo.getGatewayIp(), params[0]);
//				}
//				else{
//					response = NetUtils.completeData(response, 
//							myConnectionInfo.getGatewayIp(), params[0]);						
//				}

				// Show not connected
				if(params[0] && !response.isSuccess()){
					NetUtils.completeData(response, 
							myConnectionInfo.getGatewayIp(), params[0]);
					if(!TextUtils.isEmpty(response.getHostname()) &&
						!foundIpList.contains(response.getIp()) ){
							response.setNotCurrentlyConnected(true);
							publishProgress(response);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				response = new PingResult();
				response.setErrorMessage("ERROR: " + e.getLocalizedMessage());
			}
			
//			publishProgress(response);
			publishProgress((PingResult)null);
		}
		
		return null;
	}
	
	public void detach() {
       this.activity=null;
    }
      
    void attach(MainActivity activity) {
       this.activity=activity;
    }
      
	@Override
	protected void onProgressUpdate(PingResult... reply) {
		if (activity==null) {
			Log.w("PingTask", "onProgressUpdate() skipped -- no activity");
		}
		else {
			if(reply == null || reply[0]==null){
				//updateList(null);
				activity.updatePingTaskProgress();
			}
			else{
				activity.updateList(reply[0]);	
			}  	  
		}
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if (activity==null) {
			Log.w("PingTask", "onPostExecute() skipped -- no activity");
		}
		else{
//			activity.endSearch(scanner);
			activity.endSearch(false);
		}
	}

	private void checkArp(){
		File arpFile = new File("/proc/net/arp");
		if(arpFile.exists()){
			if(arpFile.lastModified() == arpFileLastModified){
				return;
			}
			else{
				arpFileLastModified = arpFile.lastModified();
			}
			
			try{
			BufferedReader br = 
					new BufferedReader(new FileReader(
							new File("/proc/net/arp")));
			String aLine = br.readLine();
			// Start on the second line (the first one contain just the headers)
			while((aLine=br.readLine()) != null) {
				String[] splitLine = aLine.split("\\s+");
				String ip = splitLine[0];
				if(!foundInArp.contains(ip) &&
						!splitLine[3].equals("00:00:00:00:00:00")){
//					System.out.println("Encontrado: " + ip);
					foundInArp.add(ip);
				}
			}
			br.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

}
