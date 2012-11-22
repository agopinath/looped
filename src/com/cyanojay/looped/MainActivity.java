package com.cyanojay.looped;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	
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
    	String username = ((EditText) findViewById(R.id.sl_uname)).getText().toString();
    	String pass = ((EditText) findViewById(R.id.sl_password)).getText().toString();
    	String loginUrl = ((EditText) findViewById(R.id.sl_url)).getText().toString();
    	
    	LogInTask logInTask = new LogInTask(username, pass, loginUrl, this); 
    	logInTask.execute();
    }
}
