package com.cyanojay.looped.net;
import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;


public class KeepAliveService extends IntentService  {
	private static final int UPDATE_INTERVAL = (int) (60 * 1000 * 12); // set to update every 12 minutes
	private volatile boolean isToRun;
	
	public KeepAliveService() {
		super("KeepAliveService");
		isToRun = true;
	}

	public void stopKeepAlive() {
		System.out.println("---------- STOPPING KEEP ALIVE ------------");
		isToRun = false;
	}
	
	private boolean doKeepAlive() {
		try {
			System.out.println("---------- REFRESHING PORTAL ------------");
			API.get().refreshPortal();
		} catch (IOException e) {
			//Toast.makeText(context, "Failed to refresh School Loop. Please check your " +
			//						"Internet connection and re-login.", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		new Thread(
		new Runnable() {
			public void run() {
				while(isToRun) {
				    try {
				    	Thread.sleep(UPDATE_INTERVAL);
				    } catch (InterruptedException e) {
				    	e.printStackTrace();
				    }
				    
				    doKeepAlive();
				}
			}
		}).start();
	}
}
