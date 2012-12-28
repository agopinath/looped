package com.cyanojay.looped.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.cyanojay.looped.MainActivity;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;

public class PortalActivity extends TabSwipeActivity {
	public static Intent KEEP_ALIVE_TASK;
	
   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        
        setTitle(API.get().getPortalTitle()); 
        
        tabHost = (TabHost) getTabHost();
        
        setupTab("Grades", new Intent(this, GradesActivity.class));
        setupTab("Assignments", new Intent(this, AssignmentsActivity.class));
        setupTab("News", new Intent(this, NewsActivity.class));
        setupTab("Mail", new Intent(this, LoopMailActivity.class));
        
        KEEP_ALIVE_TASK = new Intent(this, KeepAliveService.class);
        startService(KEEP_ALIVE_TASK);
	}*/
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addTab("Grades", GradesActivity.class, null);
        addTab("Assignments", AssignmentsActivity.class, null);
        addTab("News", NewsActivity.class, null);
        addTab("Mail", LoopMailActivity.class, null);
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
            showAbout();
            return true;
        case R.id.menu_logout:
            logOut();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    public void onBackPressed() {
    	logOut();
    }
	
    private void logOut() {
    	Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
    	
    	if(Utils.isOnline(this)) {
    		API.get().logOut();
    	}
    	
    	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	intent.putExtra(MainActivity.IS_FROM_LOGOUT, true);
    	
    	startActivity(intent);
	}
        
    private void showAbout() {
    	Display display = getWindowManager().getDefaultDisplay(); 
	    int width = display.getWidth();
	    int height = display.getHeight(); 
	    
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
        LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.about_popup, null, false);
        TextView about = (TextView) flow.findViewById(R.id.about_text);
        
        about.setText(Html.fromHtml(getString(R.string.about_text)));
					        
    	final PopupWindow pw = new PopupWindow(flow, width-((int)(0.25*width)), LayoutParams.WRAP_CONTENT, true);
        
    	((Button) flow.findViewById(R.id.about_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        
        pw.showAtLocation(flow, Gravity.CENTER, 10, 10);
    }
}
