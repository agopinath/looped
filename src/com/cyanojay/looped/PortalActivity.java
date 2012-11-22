package com.cyanojay.looped;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class PortalActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal); 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_portal, menu);
        return true;
    }
    
    public void setNewTitle(View view) {
    	new AsyncTask<String, String, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				if(!API.get().isLoggedIn(false)) return null;
		    	
		    	try {
		        	API.get().refreshPortal();
					System.out.println("PORTAL TITLE: " + API.get().getPortalTitle());
				} catch (IOException e) {
					e.printStackTrace();
				}
		    	
				return null;
			}
		}.execute();
    }
}
