package org.springfield.lenny.conditionalaccess;

public class AccessListEntry {

	private String[] uri;
	private String ip;
	private String role;
	private long expiry;
	private int maxRequests = Integer.MAX_VALUE;
	
	public AccessListEntry(String[] uri, String ip, String role, long expiry, int maxRequests) {
		this.uri = uri;
		this.ip = ip;
		this.role = role;
		this.expiry = expiry;
		this.maxRequests = maxRequests;
	}
	
	public void setUri(String[] uri) {
		this.uri = uri;
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public void setRole(String role) {
		this.role = role;
	}

	public void setExpiry(long expiry) {
		this.expiry = expiry;
	}
	
	public void setMaxRequests(int maxRequests) {
		this.maxRequests = maxRequests;
	}
	
	public String[] getUri() {
		return uri;
	}
	
	public String getIP() {
		return ip;
	}
	
	public String getRole() {
		return role;
	}
	
	public long getExpiry() {
		return expiry;
	}
	
	public int getMaxRequest() {
		return maxRequests;
	}
}
