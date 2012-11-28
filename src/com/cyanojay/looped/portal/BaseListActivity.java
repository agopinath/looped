package com.cyanojay.looped.portal;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cyanojay.looped.R;

public class BaseListActivity extends ListActivity {
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.activity_portal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
        
    private void showAbout() {
    	Display display = getWindowManager().getDefaultDisplay(); 
	    int width = display.getWidth();
	    int height = display.getHeight(); 
	    
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
        LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.about_popup, null, false);
        TextView about = (TextView) flow.findViewById(R.id.about_text);
        
        about.setText(Html.fromHtml(getString(R.string.about_text)));
        
    	final PopupWindow pw = new PopupWindow(flow, width-((int)(0.25*width)), height-((int)(0.8*height)), true);
        
    	flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        
        pw.showAtLocation(flow, Gravity.CENTER, 10, 10);
    }
}
