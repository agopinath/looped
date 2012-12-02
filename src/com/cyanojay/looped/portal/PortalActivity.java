package com.cyanojay.looped.portal;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.cyanojay.looped.MainActivity;
import com.cyanojay.looped.R;
import com.cyanojay.looped.net.API;

public class PortalActivity extends TabActivity {
	private TabHost tabHost;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        
        setTitle(API.get().getPortalTitle()); 
        
        tabHost = (TabHost) getTabHost();
        
        setupTab("Grades", new Intent(this, GradesActivity.class));
        setupTab("Assignments", new Intent(this, AssignmentsActivity.class));
        setupTab("News", new Intent(this, NewsActivity.class));
        setupTab("Mail", new Intent(this, LoopMailActivity.class));
	}

	private void setupTab(final String tag, Intent e) {
	    View tabview = createTabView(tabHost.getContext(), tag);
	    
	    TabSpec tabSpec = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(e);

	    tabHost.addTab(tabSpec);
	}
	
	private static View createTabView(final Context context, final String text) {
	    View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
	    TextView tv = (TextView) view.findViewById(R.id.tabsText);
	    tv.setMaxLines(1);
	    tv.setText(text);
	    return view;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_portal, menu);
        return true;
    }
    
    @Override
    public void onBackPressed() {
    	logOut();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
    
    private boolean logOut() {
    	boolean status = API.get().logOut();
    	
    	Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
    	
    	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    	
    	return status;
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
        
    	flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        
        pw.showAtLocation(flow, Gravity.CENTER, 10, 10);
    }
}
