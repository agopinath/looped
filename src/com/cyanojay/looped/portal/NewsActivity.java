package com.cyanojay.looped.portal;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.cyanojay.looped.R;

public class NewsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_news, menu);
        return true;
    }
}
