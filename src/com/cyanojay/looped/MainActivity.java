package com.cyanojay.looped;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.cyanojay.looped.net.LogInTask;
import com.cyanojay.looped.portal.BaseActivity;
import com.cyanojay.looped.portal.GradeDetailsActivity;

public class MainActivity extends BaseActivity {
	boolean saveInfo = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    	boolean fromLogout = getIntent().getBooleanExtra("FROM_LOGOUT", false);
    	
        if(!fromLogout) checkIfSavedInfo();
        else savePreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void logIn(View view) {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(findViewById(R.id.sl_prefix).getWindowToken(), 0);

    	String username = ((EditText) findViewById(R.id.sl_uname)).getText().toString();
    	String pass = ((EditText) findViewById(R.id.sl_pass)).getText().toString();
    	String loginPrefix = ((EditText) findViewById(R.id.sl_prefix)).getText().toString();
    	
    	logIn(username, pass, loginPrefix);
    }
    
    private void logIn(String user, String pass, String prefix) {
    	if(saveInfo) {
    		savePreferences();
    	}
    	
    	if(!isOnline()) {
    		Toast.makeText(this, "This app requires Internet access. Please connect to the Internet and try again.", 
    					   Toast.LENGTH_LONG).show();
    	} else if(hasWhitespace(prefix)) {
    		Toast.makeText(this, "The login prefix cannot contain whitespaces. Please try again.", 
					   Toast.LENGTH_LONG).show();
    	} else {
	    	LogInTask logInTask = new LogInTask(user, pass, user, this); 
	    	logInTask.execute();
    	}
    }
    
    private boolean hasWhitespace(String s) {
    	Pattern pattern = Pattern.compile("\\s");
    	Matcher matcher = pattern.matcher(s);
    	return matcher.find();
    }
    
    private boolean isOnline() {
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo netInfo = cm.getActiveNetworkInfo();
    	
    	if (netInfo != null && netInfo.isConnectedOrConnecting()) {
    		return true;
    	}
    	
    	return false;
	}
    
    private void checkIfSavedInfo() {
    	SharedPreferences settings = getSharedPreferences("Looped", 0);
    	saveInfo = settings.getBoolean("save_login", false);
    	if(saveInfo) {
    		logIn(settings.getString("username", ""),
    			  settings.getString("pass", ""),
    			  settings.getString("loginPrefix", ""));
    	}
    }
    
    public void toggleSaveInfo(View view) {
    	SharedPreferences settings = getSharedPreferences("Looped", 0);
        SharedPreferences.Editor editor = settings.edit();
        
        editor.putBoolean("save_login", !settings.getBoolean("save_login", false));
        
        editor.commit();
        
        saveInfo = settings.getBoolean("save_login", false);
    }
    
	private void savePreferences() {
    	SharedPreferences settings = getSharedPreferences("Looped", 0);
        SharedPreferences.Editor editor = settings.edit();
        
        editor.putString("username", ((EditText) findViewById(R.id.sl_uname)).getText().toString());
        editor.putString("pass", ((EditText) findViewById(R.id.sl_pass)).getText().toString());
        editor.putString("loginPrefix", ((EditText) findViewById(R.id.sl_prefix)).getText().toString());
        
        editor.commit();
    }
    
    private void loadPreferences() {
    	SharedPreferences settings = getSharedPreferences("Looped", 0);
    	
    	((EditText) findViewById(R.id.sl_uname)).setText(settings.getString("username", ""));
    	((EditText) findViewById(R.id.sl_pass)).setText(settings.getString("pass", ""));
    	((EditText) findViewById(R.id.sl_prefix)).setText(settings.getString("loginPrefix", ""));
    }
}
