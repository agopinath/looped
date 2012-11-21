package com.cyanojay.schoolloopd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class LogInActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        String data = intent.getStringExtra(MainActivity.USERNAME) + "\n" +
		        		  intent.getStringExtra(MainActivity.PASS) + "\n" + 
		        		  intent.getStringExtra(MainActivity.LOGIN_URL);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(data);

        setContentView(textView);
    }
}