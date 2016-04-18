/* 
* LennyInitialListener.java
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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springfield.lenny.conditionalaccess.AclCleaner;
import org.springfield.lenny.homer.LazyHomer;

/**
 * LennyInitialListener.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lenny.restlet
 * 
 */
public class LennyInitialListener implements ServletContextListener {
	
	private AclCleaner cleaner;
	
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("Lenny: context created");
		
		ServletContext servletContext = event.getServletContext();
		
		//load LazyHomer		
		LazyHomer lh = new LazyHomer();
		lh.init(servletContext.getRealPath("/"));
		
		cleaner = new AclCleaner();
		cleaner.start();
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		//destroy LazyHomer
		LazyHomer.destroy();		
		
		cleaner.stop();
		
		System.out.println("Lenny: context destroyed");
	}
}
