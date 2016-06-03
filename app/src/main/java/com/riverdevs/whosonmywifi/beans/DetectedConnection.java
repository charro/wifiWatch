package com.riverdevs.whosonmywifi.beans;

public class DetectedConnection {

	private long timestamp;
	private String ip;
	private String mac;
	private String manufacturer;
	
	public DetectedConnection(long timestamp, String ip, String mac,
			String manufacturer) {
		this.timestamp = timestamp;
		this.ip = ip;
		this.mac = mac;
		this.manufacturer = manufacturer;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
}
