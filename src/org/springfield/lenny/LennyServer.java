/* 
 * LennyServer.java
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
package org.springfield.lenny;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springfield.lenny.homer.LazyHomer;
import org.springfield.lenny.homer.LennyProperties;

/**
 * Main momar class
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @author Daniel Ockeloen <daniel@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2012
 * @package com.noterik.springfield.momar
 * @access private
 * @version $Id: LennyServer.java,v 1.13 2012-08-05 13:58:39 daniel Exp $
 *
 */
public class LennyServer {
	/** The LennyServer's log4j Logger */
	private final static Logger LOG = Logger.getLogger(LennyServer.class);

	/** base uri of the file system */
	private static String DOMAIN_URI = "/domain";

	/** service type of this service */
	private static final String SERVICE_TYPE = "ticketservice";

	/** instance */
	private static LennyServer instance = new LennyServer();

	/** Root path: the path the webservice is running in */
	private String rootPath;
	private String scriptsPath;

	/** configuration properties */
	private Properties configuration;

	private Boolean running = false;

	/**
	 * Sole constructor
	 */
	public LennyServer() {
		instance = this;
	}

	/**
	 * Return MomarConfiguration instance
	 * 
	 * @return MomarConfiguration instance.
	 */
	public static LennyServer instance() {
		return instance;
	}

	public Boolean isRunning() {
		if (running) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the configuration.
	 * 
	 * @return The configuration.
	 */
	public Properties getConfiguration() {
		return configuration;
	}

	/**
	 * Set root path
	 */
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
		this.scriptsPath = rootPath + "/scripts/";
		// init();
	}

	/**
	 * Initializes the configuration
	 */
	public void init() {
		Thread t = new Thread() {
			public void run() {
				// init properties xml
				initConfigurationXML();

			}
		};
		t.start();

		running = true;
	}

	/**
	 * Loads configuration file.
	 */
	private void initConfigurationXML() {
		System.out.println("Initializing configuration file.");

		// configuration file
		configuration = new Properties();
		
		LennyProperties mp = LazyHomer.getMyLennyProperties();
		if (mp!=null) {
			configuration.put("apiKey", mp.getApiKey());
		} else {
			System.out.println("Loading from configuration failed.");
		}
	}

	/**
	 * Shutdown
	 */
	public void destroy() {
		instance = null;
		running = false;
	}
}
