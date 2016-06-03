package com.riverdevs.whosonmywifi.beans;

public class PingResult {

	private boolean success;
	private String ip;
	private String hostname;
	private String errorMessage;
	private String macAddress;
	private boolean notCurrentlyConnected = false;
	private String givenName;
	private String manufacturer;
	
	public PingResult() {
		super();
	}
	
	public PingResult(boolean success) {
		super();
		this.success = success;
	}
	
//	public PingResult(boolean success, String ip, String hostname) {
//		super();
//		this.success = success;
//		this.ip = ip;
//		this.hostname = hostname;
//	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isNotCurrentlyConnected() {
		return notCurrentlyConnected;
	}

	public void setNotCurrentlyConnected(boolean notCurrentlyConnected) {
		this.notCurrentlyConnected = notCurrentlyConnected;
	}
	
	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof PingResult){
			return ip.equals(((PingResult)o).getIp());	
		}
		else{
			return false;
		}
	}
}
