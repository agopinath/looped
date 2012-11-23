package com.cyanojay.looped.portal;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.cyanojay.looped.API;
import com.cyanojay.looped.R;

@SuppressWarnings("deprecation")
public class PortalActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        
        setTitle(API.get().getPortalTitle());
        
        TabHost tabHost = getTabHost();
        
        // Tab for Grades
        TabSpec gradesspec = tabHost.newTabSpec("Grades");
        gradesspec.setIndicator("Grades");
        Intent gradesIntent = new Intent(this, GradesActivity.class);
        gradesspec.setContent(gradesIntent);
 
        // Tab for Assignments
        TabSpec assignmentsspec = tabHost.newTabSpec("Assignments");
        assignmentsspec.setIndicator("Assignments");
        Intent assignmentsIntent = new Intent(this, AssignmentsActivity.class);
        assignmentsspec.setContent(assignmentsIntent);
 
        // Tab for News
        TabSpec newsspec = tabHost.newTabSpec("News");
        newsspec.setIndicator("News");
        Intent newsIntent = new Intent(this, NewsActivity.class);
        newsspec.setContent(newsIntent);
 
        tabHost.addTab(gradesspec); // Adding Grades tab
        tabHost.addTab(assignmentsspec); // Adding Assignments tab
        tabHost.addTab(newsspec); // Adding News tab
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_portal, menu);
        return true;
    }
}
