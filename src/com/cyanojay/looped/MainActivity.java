package com.cyanojay.looped;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cyanojay.looped.net.LogInTask;
import com.cyanojay.looped.portal.BaseActivity;

public class MainActivity extends BaseActivity {
	private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private EditText user;
    private EditText pass;
    private EditText prefix;
    private CheckBox rem_me;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences("Looped", Context.MODE_PRIVATE);
        editor = settings.edit();
        
        user = ((EditText) findViewById(R.id.sl_uname));
        pass = ((EditText) findViewById(R.id.sl_pass));
        prefix = ((EditText) findViewById(R.id.sl_prefix));
        rem_me = ((CheckBox) findViewById(R.id.remember_me));
        
    	boolean fromLogout = getIntent().getBooleanExtra("FROM_LOGOUT", false);
    	
        if(!fromLogout) {
        	if(isLoginSaved()) {
        		loadPreferences();
        		rem_me.setChecked(true);
        	}
        } else {
        	editor.clear();
        	editor.commit();
        }
        
        setUpKeys(user);
        setUpKeys(pass);
        setUpKeys(prefix);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void logIn(View view) {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(findViewById(R.id.sl_prefix).getWindowToken(), 0);

    	String username = user.getText().toString().trim();
    	String passwd = pass.getText().toString().trim();
    	String loginPrefix = fixLoginPrefix(prefix.getText().toString().trim());
    	
    	if(rem_me.isChecked()) {
    		savePreferences();
    		editor.putBoolean("SAVE_LOGIN", true);
    	} else {
    		editor.putBoolean("SAVE_LOGIN", false);
    		editor.clear();
    	}
    	
    	editor.commit();
    	
    	startLogIn(username, passwd, loginPrefix);
    }
    
    private String fixLoginPrefix(String pref) {
    	return pref.replace(" ", "").replace("\n", "").replace("\r", "");
    }
    
    private void startLogIn(String username, String passwd, String prefix) {
    	if(username.length() == 0 || pass.length() == 0 || prefix.length() == 0) {
    		Toast.makeText(this, "One or more fields are empty. Please correct and try again.", Toast.LENGTH_LONG).show();
    		return;
    	}
    		
    	if(!isOnline()) {
    		Toast.makeText(this, "This app requires Internet access. Please connect to the Internet and try again.", Toast.LENGTH_LONG).show();
    		return;
    	} 
    	
    	Utils.lockOrientation(this);
    	
	    LogInTask logInTask = new LogInTask(username, passwd, prefix, this); 
	    logInTask.execute();
    }
    
    private void setUpKeys(EditText text) {
    	text.addTextChangedListener(new TextWatcher() {
    		
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void afterTextChanged(Editable s) {
                for(int i = s.length(); i > 0; i--){
                    if(s.subSequence(i-1, i).toString().equals("\n"))
                         s.replace(i-1, i, "");
                }
            }
        });
    }
    
    private boolean isOnline() {
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo netInfo = cm.getActiveNetworkInfo();
    	
    	return (netInfo != null && netInfo.isConnectedOrConnecting());
	}
    
    private boolean isLoginSaved() {
    	return settings.getBoolean("SAVE_LOGIN", false);
    }
    
	private void savePreferences() {
        editor.putString("username", user.getText().toString().trim());
        editor.putString("pass", pass.getText().toString().trim());
        editor.putString("loginPrefix", fixLoginPrefix(prefix.getText().toString().trim()));
        
        editor.commit();
    }
    
    private void loadPreferences() {
    	user.setText(settings.getString("username", ""));
    	pass.setText(settings.getString("pass", ""));
    	prefix.setText(settings.getString("loginPrefix", ""));
    }
}
