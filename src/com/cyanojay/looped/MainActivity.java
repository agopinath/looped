package com.cyanojay.looped;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

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
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(findViewById(R.id.sl_prefix).getWindowToken(), 0);
    	
    	if(!isOnline()) {
    		Toast.makeText(this, "This app requires Internet access. Please connect to the Internet and try again.", 
    					   Toast.LENGTH_SHORT).show();
    	}
    	
    	String username = ((EditText) findViewById(R.id.sl_uname)).getText().toString();
    	String pass = ((EditText) findViewById(R.id.sl_pass)).getText().toString();
    	String loginPrefix = ((EditText) findViewById(R.id.sl_prefix)).getText().toString();
    	
    	LogInTask logInTask = new LogInTask(username, pass, loginPrefix, this); 
    	logInTask.execute();
    }
    
    private boolean isOnline() {
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo netInfo = cm.getActiveNetworkInfo();
    	
    	if (netInfo != null && netInfo.isConnectedOrConnecting()) {
    		return true;
    	}
    	
    	return false;
	}

	public void savePreferences(View view) {
    	SharedPreferences settings = getSharedPreferences("Looped", 0);
        SharedPreferences.Editor editor = settings.edit();
        
        editor.putString("username", ((EditText) findViewById(R.id.sl_uname)).getText().toString());
        editor.putString("pass", ((EditText) findViewById(R.id.sl_pass)).getText().toString());
        editor.putString("loginPrefix", ((EditText) findViewById(R.id.sl_prefix)).getText().toString());
        
        editor.commit();
    }
    
    public void loadPreferences(View view) {
    	SharedPreferences settings = getSharedPreferences("Looped", 0);
    	
    	((EditText) findViewById(R.id.sl_uname)).setText(settings.getString("username", ""));
    	((EditText) findViewById(R.id.sl_pass)).setText(settings.getString("pass", ""));
    	((EditText) findViewById(R.id.sl_prefix)).setText(settings.getString("loginPrefix", ""));
    }
}
