package org.springfield.lenny.conditionalaccess;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class AclCleaner {
	/** The AclCleaner's WMSLogger */
	private static Logger logger = Logger.getLogger(AclCleaner.class);
	
	/** Timer */
	private Timer timer;
	
	/** check every minute */
	private static final long TIME_TO_WAIT = 60 * 1000;
	
	public void start() {
		logger.info("CleanupAccessList.start");
		
		timer = new Timer();
		TimerTask timertask = new cleanupTimerTask();
		timer.schedule(timertask, 0, TIME_TO_WAIT);
	}
	
	public void stop() {
		logger.info("CleanupAccessList.stop");
		
		// shutdown timer
		if(timer!=null) {
			timer.cancel();
		}
	}

	class cleanupTimerTask extends TimerTask {		
		public cleanupTimerTask() {
		}
		
		public void run() {
			AccessList.getInstance().cleanUp();
		}
	}	
}
