package com.cyanojay.looped;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class PortalActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal); 

		setTitle(API.get().getPortalTitle());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_portal, menu);
        return true;
    }
}
