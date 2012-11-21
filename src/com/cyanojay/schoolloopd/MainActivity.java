package com.cyanojay.schoolloopd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.example.schoolloopd.R;

public class MainActivity extends Activity {
	
	public static final String USERNAME = "com.cyanojay.schoolloopd.USERNAME";
    public static final String PASS = "com.cyanojay.schoolloopd.PASS";
    public static final String LOGIN_URL = "com.cyanojay.schoolloopd.LOGIN_URL";
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void logIn(View view) {
    	Intent loginIntent = new Intent(this, LogInActivity.class);
    	
    	String username = ((EditText) findViewById(R.id.sl_uname)).getText().toString();
    	String pass = ((EditText) findViewById(R.id.sl_password)).getText().toString();
    	String login_url = ((EditText) findViewById(R.id.sl_url)).getText().toString();
    	
    	loginIntent.putExtra(USERNAME, username);
    	loginIntent.putExtra(PASS, pass);
    	loginIntent.putExtra(LOGIN_URL, login_url);
    	
    	startActivity(loginIntent);
    }
}
