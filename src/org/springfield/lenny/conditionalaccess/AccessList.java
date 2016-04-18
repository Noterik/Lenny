package org.springfield.lenny.conditionalaccess;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class AccessList extends ConcurrentHashMap<String, AccessListEntry> {
	private static Logger logger = Logger.getLogger(AccessList.class);
	
	static final long serialVersionUID = 0l;
	private static int capacity = 1000;
	private String bartUri = "";
	
	public static AccessList instance;
	
	public static AccessList getInstance() {
		if (instance == null) {
			instance = new AccessList();
		}
		return instance;
	}
		
	public AccessList() {
		super(capacity + 1, 1.1f, 6);
	}
	
	protected boolean removeEldestEntry(Map.Entry eldest) {
	    return size() > capacity;
	}
	
	public void cleanUp() {
		Set<String> keys = this.keySet();	
		long currentTimestamp = new Date().getTime() / 1000;
		logger.debug("Current timestamp = "+currentTimestamp);
		
		for (String key : keys) {
			if (currentTimestamp > this.get(key).getExpiry()) {
				logger.info("Deleting ticket "+key+" for file "+Arrays.toString(this.get(key).getUri())+" for ip "+this.get(key).getIP());
				this.remove(key);
			}
		}		
	}
	
	public boolean hasAccess(String clientUri, String clientIP, String ticket) {
		logger.info("request for "+clientUri+" with ip: "+clientIP+" and ticket: "+ticket);	
		
		AccessListEntry entry = this.get(ticket);
		long currentTimestamp = new Date().getTime() / 1000;

		if (entry == null) {
			return false;
		}
		
		//entry.getIP().equals(clientIP) gives internal ip, so don't use yet
		if (inEntryArray(clientUri, entry) && currentTimestamp <= entry.getExpiry()) {
			int maxRequests = entry.getMaxRequest();
			maxRequests--;
			if (maxRequests <= 0) {
				this.remove(ticket);
			} else {
				entry.setMaxRequests(maxRequests);
				this.put(ticket,entry);				
			}
			return true;
		}
	
		return false;
	}
	
	public void setBartUri(String uri) {
		this.bartUri = uri;
	}
	
	public String getBarturi() {
		return bartUri;
	}
	
	private boolean inEntryArray(String clientUri, AccessListEntry entry) {
		String[] uris = entry.getUri();
		
		for (String uri : uris) {
			if (clientUri.startsWith(uri)) {
				return true;
			}
		}
		return false;
	}
}
