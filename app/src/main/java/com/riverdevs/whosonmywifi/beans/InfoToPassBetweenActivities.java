package com.riverdevs.whosonmywifi.beans;

import com.riverdevs.whosonmywifi.MainActivity;
import com.riverdevs.whosonmywifi.MonitorConnectionThread;
import com.riverdevs.whosonmywifi.PingTask;
import com.riverdevs.whosonmywifi.layout.FoundDevicesAdapter;


public class InfoToPassBetweenActivities {
	
	private PingTask executingPingTask;
	private WifiConnectionInfo wifiConnectionInfo;
	private int addressesLooked;
	private FoundDevicesAdapter listAdapter;
	private MonitorConnectionThread monitorThread;
	private MainActivity.CompleteDeviceDataTask completeDataTask;
	
	public InfoToPassBetweenActivities(PingTask task, WifiConnectionInfo wifiInfo,
									   int addressesLooked, FoundDevicesAdapter listAdapter, MonitorConnectionThread monitorThread,
									   MainActivity.CompleteDeviceDataTask completeDataTask) {
		this.executingPingTask = task;
		this.wifiConnectionInfo = wifiInfo;
		this.addressesLooked = addressesLooked;
		this.listAdapter = listAdapter;
		this.monitorThread = monitorThread;
		this.completeDataTask = completeDataTask;
	}

	public PingTask getExecutingPingTask(){
		return executingPingTask;
	}
	
	public WifiConnectionInfo getWifiConnectionInfo(){
		return wifiConnectionInfo;
	}

	public int getAddressesLooked() {
		return addressesLooked;
	}
	
	public FoundDevicesAdapter getListAdapter() {
		return listAdapter;
	}
	
	public MonitorConnectionThread getMonitorThread() {
		return monitorThread;
	}

	public void setMonitorThread(MonitorConnectionThread monitorThread) {
		this.monitorThread = monitorThread;
	}

	public MainActivity.CompleteDeviceDataTask getCompleteDataTask(){
		return completeDataTask;
	}

	public void getCompleteDataTask(MainActivity.CompleteDeviceDataTask completeDataTask){
		this.completeDataTask = completeDataTask;
	}

	@Override
	public String toString() {
		return "Info: PingTask=" + executingPingTask + " WifiInfo: " + wifiConnectionInfo +
				" ListAdapter: " + listAdapter + " Addresses looked: " + addressesLooked;
	}
}
