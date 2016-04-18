/* 
* LennyResource.java
* 
* Copyright (c) 2016 Noterik B.V.
* 
* This file is part of Lenny, related to the Noterik Springfield project.
*
* Lenny is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Lenny is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Lenny.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.lenny.restlet;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.springfield.lenny.conditionalaccess.AccessList;
import org.springfield.lenny.conditionalaccess.AccessListEntry;
import org.springfield.lenny.homer.SmithersProperties;
import org.springfield.lenny.restlet.util.Pair;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * LennyResource.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lenny.restlet
 * 
 */
public class LennyResource extends ServerResource {
	private static Logger logger = Logger.getLogger(LennyResource.class);
	
	private static final String TICKET_URI = "/acl/ticket";
	private static final String TICKET_ACCESS_URI = "/acl/ticketaccess";
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private AccessList accesslist;
	private List<InetAddress> whitelist = new ArrayList<InetAddress>();
	
	public LennyResource() {
		//constructor
		this.whitelist = whitelist;
		accesslist = AccessList.getInstance();
	}
	
	public void doInit(Context context, Request request, Response response) {
        super.init(context, request, response);
        
        // add representational variants allowed
        getVariants().add(new Variant(MediaType.TEXT_XML));
	}
	
	// allowed actions: POST, PUT, GET, DELETE 
	public boolean allowPut() {return true;}
	public boolean allowPost() {return true;}
	public boolean allowGet() {return true;}
	public boolean allowDelete() {return true;}
	
	//TODO: handle ip whitelisting
	
	/**
	 * GET
	 */
	@Get
    public void handleGet() {
        // get request uri
        String uri = getRequestUri();
		
        Status status;
        String responseBody;
        MediaType mediatype = MediaType.TEXT_HTML;
        
        if (uri.equals(TICKET_URI)) {
			Pair<Status, String> response = showList();
			status = response.status;
			responseBody = response.response;			
		} else if (uri.startsWith(TICKET_URI)) {
			Pair<Status, String> response = getTicket(uri);
			status = response.status;
			responseBody = response.response;
			mediatype = MediaType.TEXT_XML;
		} else {
			status = Status.CLIENT_ERROR_NOT_FOUND; 
			responseBody = "404 - Not found";
		}

        getResponse().setStatus(status);
		getResponse().setEntity(responseBody, mediatype);
	}
	
	/**
	 * PUT
	 */
	@Put("xml")
	public void handlePut(Representation representation) {
		// get request uri
        String uri = getRequestUri();
		
        Status status;
        String responseBody;
        MediaType mediatype = MediaType.TEXT_HTML;
        
        String xml = "";
        
        try {
			if (representation == null) {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				getResponse().setEntity("<status>Error: the request data could not be read</status>",
						MediaType.TEXT_XML);
			} else {
				xml = representation.getText();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
        
        if (uri.startsWith(TICKET_ACCESS_URI)) {
        	Pair<Status, String> response = getTicketHasAccess(uri, xml);
        	status = response.status;
        	responseBody = response.response;
        } else {
        	status = Status.CLIENT_ERROR_NOT_FOUND; 
			responseBody = "404 - Not found";
        }
        
        getResponse().setStatus(status);
		getResponse().setEntity(responseBody, mediatype);
	}
	
	/**
	 * POST
	 */
	@Post("xml")
	public void handlePost(Representation representation) {
		// get request uri
        String uri = getRequestUri();
        
        Status status;
        String responseBody;
        MediaType mediatype = MediaType.TEXT_HTML;
        
        String xml = "";
        
        try {
			if (representation == null) {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				getResponse().setEntity("<status>Error: the request data could not be read</status>",
						MediaType.TEXT_XML);
			} else {
				xml = representation.getText();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
        
        if (uri.equals(TICKET_URI)) {
        	Pair<Status, String> response = addTicket(xml);
        	status = response.status;
        	responseBody = response.response;
        	mediatype = MediaType.TEXT_XML;
        } else {
        	status = Status.CLIENT_ERROR_NOT_FOUND; 
			responseBody = "404 - Not found";
        }
		
        getResponse().setStatus(status);
		getResponse().setEntity(responseBody, mediatype);
	}
	
	/**
	 * DELETE
	 */
	@Delete
	public void handleDelete() {
		// get request uri
        String uri = getRequestUri();
		
        Status status;
        String responseBody;
        MediaType mediatype = MediaType.TEXT_HTML;
        
        if (uri.startsWith(TICKET_URI)) {
        	Pair<Status, String> response = deleteTicket(uri);
        	status = response.status;
        	responseBody = response.response;
        	mediatype = MediaType.TEXT_XML;
        } else {
        	status = Status.CLIENT_ERROR_NOT_FOUND; 
			responseBody = "404 - Not found";
        }
        
        getResponse().setStatus(status);
		getResponse().setEntity(responseBody, mediatype);
	}
	
	/**
	 * Get request uri
	 * @return
	 */
	private String getRequestUri() {
		 // get uri
		System.out.println(getRequest().getResourceRef().getPath());
        String reqUri = getRequest().getResourceRef().getPath();
        reqUri = reqUri.substring(reqUri.indexOf("/",1));
        if(reqUri.endsWith("/")) {
        	reqUri = reqUri.substring(0,reqUri.length()-1);
        }
        return reqUri;
	}
	
	/**
	 * Add a ticket 
	 * 
	 * @param body
	 * @return
	 */
	private Pair<Status, String> addTicket(String fsxml) {
		String ticket, uri, ip, role;
		String[] uriArray;
		ticket = uri = ip = role = "";
		long expiry = 0L;
		int maxRequests = Integer.MAX_VALUE;
		
		Pair<Status, String> r;
		
		try {
			Document doc = DocumentHelper.parseText(fsxml);
			ticket = doc.selectSingleNode("/fsxml/properties/ticket") == null ? "" : doc.selectSingleNode("/fsxml/properties/ticket").getText();
			uri = doc.selectSingleNode("/fsxml/properties/uri") == null ? "" : doc.selectSingleNode("/fsxml/properties/uri").getText();
			ip = doc.selectSingleNode("/fsxml/properties/ip") == null ? "" : doc.selectSingleNode("/fsxml/properties/ip").getText();
			role = doc.selectSingleNode("/fsxml/properties/role") == null ? "" : doc.selectSingleNode("/fsxml/properties/role").getText();			
			expiry = doc.selectSingleNode("/fsxml/properties/expiry") == null ? 0L : Long.parseLong(doc.selectSingleNode("/fsxml/properties/expiry").getText());
			maxRequests = doc.selectSingleNode("/fsxml/properties/maxRequests") == null ? Integer.MAX_VALUE : Integer.parseInt(doc.selectSingleNode("/fsxml/properties/maxRequests").getText());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error "+e.getMessage());
			r = new Pair<Status, String>(Status.CLIENT_ERROR_BAD_REQUEST, property2fsxml("status", "Bad request"));

			return r;
		}
		
		uriArray = new String[]{uri};
		
		//correct full uris to relative uris
		if (uri.indexOf(".noterik.com/progressive/") > -1) {
			uri = uri.substring(uri.indexOf("/domain/"));
		}
		
		if (uri.indexOf("/collection/") > -1 && uri.indexOf("/presentation/") > -1) {
			//resolve collection presentation, can contain multiple videos
			logger.info("Getting videos for "+uri);
			
			String[] tmpArray = new String[]{uri};
			
			ServiceInterface smithers = ServiceManager.getService("smithers");
			if (smithers != null) {
				String response = smithers.get(uri, null, null);
				
				logger.debug("collection presentation response = "+response);
				
				// parse
				Document doc = null;		
				try {
					doc = DocumentHelper.parseText(response);
				} catch (DocumentException e) {
					logger.error("Could not parse collection respopnse from smithers",e);
				}
				
				if (doc != null) {
					String presentation = doc.selectSingleNode("//presentation/@referid") == null ? "" : doc.selectSingleNode("//presentation/@referid").getText();
				
					response = smithers.get(presentation, null, null);
					
					logger.debug("presentation response = "+response);
					
					try {
						doc = DocumentHelper.parseText(response);
					} catch (DocumentException e) {
						logger.error("Could not parse presentation respopnse from smithers",e);
					}
					
					if (doc != null) {
						List<Node> videos = doc.selectNodes("//videoplaylist/video/@referid");
						
						tmpArray = new String[videos.size()];
						int j = 0;
						
						for (Iterator<Node> i = videos.iterator(); i.hasNext(); ) {
							tmpArray[j] = i.next().getText();
							logger.debug("Adding "+tmpArray[j]);
							j++;
						}
					}
				}
			
				uriArray = tmpArray;
			}
		}
		
		if (ticket.equals("") || uriArray.length == 0 || ip.equals("") || role.equals("") || expiry == 0L) {
			logger.error("Error in ticket: t="+ticket+" u="+Arrays.toString(uriArray)+" i="+ip+" r="+role+" e="+expiry);
			r = new Pair<Status, String>(Status.CLIENT_ERROR_BAD_REQUEST, property2fsxml("status", "Bad request"));
			return r;
		}	
		
		AccessListEntry entry = new AccessListEntry(uriArray, ip, role, expiry, maxRequests);
		accesslist.put(ticket, entry);
		logger.info("Added ticket ["+ticket+", "+uriArray.toString()+", "+ip+", "+role+", "+expiry+", "+maxRequests+"]");
		
		r = new Pair<Status, String>(Status.SUCCESS_OK, property2fsxml("status", "Successfully added"));
		return r;
	}
	
	/**
	 * Delete a ticket
	 * 
	 * @param uri
	 * @return
	 */
	private Pair<Status, String> deleteTicket(String uri) {
		Pair<Status, String> r;
		
		String ticket = uri.substring(uri.indexOf(TICKET_URI)+TICKET_URI.length()+1);
		
		if (accesslist.containsKey(ticket)) {
			AccessListEntry entry = accesslist.get(ticket);
			logger.info("Deleting ticket ["+ticket+", "+entry.getUri()+", "+entry.getIP()+", "+entry.getRole()+", "+entry.getExpiry()+", "+entry.getMaxRequest()+"]");
			accesslist.remove(ticket);
			r = new Pair<Status, String>(Status.SUCCESS_OK, property2fsxml("status", "Successfully removed"));
		} else {
			r = new Pair<Status, String>(Status.CLIENT_ERROR_NOT_FOUND, property2fsxml("status", "Ticket not found"));
		}		
		return r;
	}
	
	/**
	 * Show all active tickets
	 * 
	 * @return
	 */
	private Pair<Status, String> showList() {
		StringBuilder response = new StringBuilder("<html><head><title>Valid tickets</title></head><body>");
		response.append("<table border='1'><tr><td align='center'>Ticket</td><td align='center'>URI</td><td align='center'>Role</td>");
		response.append("<td align='center'>IP</td><td align='center'>Expiry</td><td>Max requests</td></tr>");
		
		for (Map.Entry<String, AccessListEntry> item: accesslist.entrySet()) {
			String key = item.getKey();
			AccessListEntry entry = item.getValue();
			
			Timestamp time = new Timestamp((long)entry.getExpiry()*1000);
			String expiry = TIME_FORMAT.format(time);
			response.append("<tr><td align='right'>"+key+"</td>");
			response.append("<td align='right'>"+Arrays.toString(entry.getUri())+"</td><td align='right'>"+entry.getRole()+"</td>");
			response.append("<td align='right'>"+entry.getIP()+"</td><td align='right'>"+expiry+"</td>");
			response.append("<td align='right'>"+entry.getMaxRequest()+"</td></tr>");
		}
		response.append("</table></body></html>");
		
		Pair<Status, String> r;
		r = new Pair<Status, String>(Status.SUCCESS_OK, response.toString());
		return r;
	}
	
	/** 
	 * Request data of a ticket
	 * 
	 * @param uri
	 * @return
	 */
	private Pair<Status, String> getTicket(String uri) {
		String ticket = uri.substring(uri.indexOf(TICKET_URI)+TICKET_URI.length()+1);
		
		Pair<Status, String> r;
		
		if (accesslist.containsKey(ticket)) {
			AccessListEntry entry = accesslist.get(ticket);
			StringBuilder response = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?><fsxml><properties>");
			response.append("<ticket>"+ticket+"</ticket><uri>"+entry.getUri()+"</uri><role>"+entry.getRole()+"</role>");
			response.append("<ip>"+entry.getIP()+"</ip><expiry>"+entry.getExpiry()+"</expiry>");
			response.append("<maxRequests>"+entry.getMaxRequest()+"</maxRequests>");
			response.append("</properties></fsxml>");
			r = new Pair<Status, String>(Status.SUCCESS_OK, response.toString());
		} else {
			r = new Pair<Status, String>(Status.CLIENT_ERROR_NOT_FOUND, property2fsxml("status", "Ticket not found"));
		}
		return r;	
	}
	
	/**
	 * 
	 * @param property
	 * @param value
	 * @return
	 */
	private Pair<Status, String> getTicketHasAccess(String uri, String fsxml) {
		Pair<Status, String> r;
		
		String ticket = uri.substring(uri.indexOf(TICKET_ACCESS_URI)+TICKET_ACCESS_URI.length()+1);
		
		String mediaUri = "";
		logger.info(fsxml);
		try {
			Document doc = DocumentHelper.parseText(fsxml);
			mediaUri = doc.selectSingleNode("/fsxml/properties/uri") == null ? "" : doc.selectSingleNode("/fsxml/properties/uri").getText();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error "+e.getMessage());
			r = new Pair<Status,String>(Status.CLIENT_ERROR_BAD_REQUEST, property2fsxml("status", "Bad request"));
			return r;
		}
		
		if (accesslist.containsKey(ticket)) {
			if (accesslist.hasAccess(mediaUri, "", ticket)) {
				StringBuilder response = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?><fsxml><properties>");
				response.append("<allowed>true</allowed>");
				response.append("</properties></fsxml>");
				r = new Pair<Status, String>(Status.SUCCESS_OK, response.toString());
			} else {
				StringBuilder response = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?><fsxml><properties>");
				response.append("<allowed>false</allowed>");
				response.append("</properties></fsxml>");
				r = new Pair<Status, String>(Status.SUCCESS_OK, response.toString());
			}
		}  else {
			r = new Pair<Status, String>(Status.CLIENT_ERROR_NOT_FOUND, property2fsxml("status", "Ticket not found"));
		}
		return r;
	}
	 
	private String property2fsxml(String property, String value) {
		String fsxml ="<?xml version='1.0' encoding='UTF-8'?><fsxml><properties>";
		fsxml += "<"+property+">"+value+"</"+property+">";
		fsxml += "</properties></fsxml>";
		return fsxml;
	}
	
	/*private Document getNode(String uri) {
		Document response = null;
		
		String responseStr = HttpHelper.sendRequest("GET", uri, null, null);
		
		try {	
			response = DocumentHelper.parseText(responseStr);
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error("could not create document from node "+uri+" - "+e.getMessage());
		}
		return response;
	}*/
	
}
