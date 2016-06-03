package com.riverdevs.whosonmywifi.beans;

public class NetBiosResult {

	private boolean success;
	private String netBiosName;
	
	public NetBiosResult(boolean success, String netBiosName) {
		super();
		this.success = success;
		this.netBiosName = netBiosName;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getNetBiosName() {
		return netBiosName;
	}

	public void setNetBiosName(String netBiosName) {
		this.netBiosName = netBiosName;
	}
	
}
