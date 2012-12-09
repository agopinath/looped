package com.cyanojay.looped.net;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class SessionKeepAliveTask  {
	private static final int UPDATE_INTERVAL = (int) (8 * 1000);
	private Timer timer = new Timer();
	private Activity parent;  
	
	public SessionKeepAliveTask(Activity parent) {
		this.parent = parent;
	}
	
	public void startKeepAlive() {
		timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	doKeepAlive();
	        }             
	    }, 0, UPDATE_INTERVAL);
	}

	public void stopKeepAlive() {
		if(timer != null) timer.cancel();
	}
	
	private void doKeepAlive() {
		try {
			System.out.println("RUNNING KEEP ALIVE");
			API.get().refreshPortal();
		} catch (IOException e) {
			Toast.makeText(parent, "Failed to refresh School Loop. Please check your " +
									"Internet connection and re-login.", Toast.LENGTH_LONG).show();
		}
	}
}
