package com.cyanojay.looped.net;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.widget.Toast;

public class SessionKeepAliveTask  {
	private static final int UPDATE_INTERVAL = (int) (60 * 1000 * 10);
	private Timer timer;
	private Activity parent;  
	
	public SessionKeepAliveTask(Activity parent) {
		this.parent = parent;
	}
	
	public void startKeepAlive() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	doKeepAlive();
	        }             
	    }, 0, UPDATE_INTERVAL);
	}

	public void stopKeepAlive() {
		if(timer != null){
			timer.cancel(); 
			timer = null;
		}
	}
	
	private boolean doKeepAlive() {
		try {
			API.get().refreshPortal();
			System.out.println("---------- REFRESHING PORTAL------------");
		} catch (IOException e) {
			Toast.makeText(parent, "Failed to refresh School Loop. Please check your " +
									"Internet connection and re-login.", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
}
