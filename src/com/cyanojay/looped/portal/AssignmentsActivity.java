package com.cyanojay.looped.portal;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.cyanojay.looped.R;

public class AssignmentsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_assignments, menu);
        return true;
    }
}
