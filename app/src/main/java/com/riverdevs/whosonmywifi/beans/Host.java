package com.riverdevs.whosonmywifi.beans;

public class Host {

	private String mac;
	private String name;
	private int networkId;
	private String hostname;
	
	public Host(String mac, String name, int networkId, String hostname) {
		super();
		this.mac = mac;
		this.name = name;
		this.networkId = networkId;
		this.hostname = hostname;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
}
