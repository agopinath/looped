package com.cyanojay.looped.net;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class SessionKeepAliveService extends Service {
	private static final int UPDATE_INTERVAL = (int) (8 * 1000);
	private Timer timer = new Timer();  
	
	public SessionKeepAliveService() {}
	
	@Override
	public IBinder onBind(Intent intent) {
	    return null;
	}

	@Override   
	public void onDestroy() {   
	    if (timer != null){
	        timer.cancel();
	    } 
	}

	public void stopService() {
	    if(timer != null)
	        timer.cancel();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {  
	    timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	doKeepAlive();
	        }       
	    }, 0, UPDATE_INTERVAL);

	    return START_STICKY;
	}
	
	private void doKeepAlive() {
		try {
			System.out.println("RUNNING KEEP ALIVE");
			API.get().refreshPortal();
		} catch (IOException e) {
			//Toast.makeText(parent, "Failed to refresh School Loop. Please check your " +
			//						"Internet connection and re-login.", Toast.LENGTH_LONG).show();
		}
	}
}
