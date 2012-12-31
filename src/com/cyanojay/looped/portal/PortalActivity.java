package com.cyanojay.looped.portal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;
import com.cyanojay.looped.net.KeepAliveService;
import com.cyanojay.looped.portal.assignments.AssignmentsFragmnet;
import com.cyanojay.looped.portal.grades.GradesFragment;
import com.cyanojay.looped.portal.loopmail.LoopMailFragment;
import com.cyanojay.looped.portal.news.NewsFragment;

public class PortalActivity extends TabSwipeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(API.get().getPortalTitle()); 
        
        addTab("Grades", GradesFragment.class, null);
        addTab("Assignments", AssignmentsFragmnet.class, null);
        addTab("News", NewsFragment.class, null);
        addTab("Mail", LoopMailFragment.class, null);
        
        Intent KEEP_ALIVE_TASK = new Intent(this, KeepAliveService.class);
        startService(KEEP_ALIVE_TASK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.activity_portal, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.menu_about:
    		Utils.showAbout(this);
            return true;
        case R.id.menu_logout:
            Utils.logOut(this);
            return true;
        case R.id.menu_refresh:
        	Fragment curr = getCurrentFragment();
        	FragmentManager manager = curr.getFragmentManager();
        	
        	Refreshable refreshable = (Refreshable) curr;
        	
        	refreshable.refresh(manager);
            return true;
        default:
            return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    public void onBackPressed() {
    	setPage(0);
    	
    	/*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	        	Utils.logOut(PortalActivity.this);
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
    	    	.setNegativeButton("No", dialogClickListener).show();*/
    }
}
