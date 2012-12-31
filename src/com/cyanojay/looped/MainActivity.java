package com.cyanojay.looped;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.cyanojay.looped.net.LogInTask;
import com.cyanojay.looped.portal.BaseActivity;

public class MainActivity extends BaseActivity {
	public static final String PREFS_NAME = "Looped";
	public static final String IS_FROM_LOGOUT = "FROM_LOGOUT";
	public static final String SHOULD_SAVE_LOGIN = "SAVE_LOGIN";
	public static final String USERNAME = "USERNAME";
	public static final String PASS = "PASS";
	public static final String PREFIX = "PREFIX";
	
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

        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        
        user = ((EditText) findViewById(R.id.sl_uname));
        pass = ((EditText) findViewById(R.id.sl_pass));
        prefix = ((EditText) findViewById(R.id.sl_prefix));
        rem_me = ((CheckBox) findViewById(R.id.remember_me));
        
    	boolean fromLogout = getIntent().getBooleanExtra(IS_FROM_LOGOUT, false);
    	
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
        
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.main_activity_layout);
        
        for(int i = 0; i < myLayout.getChildCount(); i++) {
        	myLayout.clearChildFocus(myLayout.getChildAt(i));
        }
        
        myLayout.clearFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        
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
    		editor.putBoolean(SHOULD_SAVE_LOGIN, true);
    	} else {
    		editor.putBoolean(SHOULD_SAVE_LOGIN, false);
    		editor.clear();
    	}
    	
    	editor.commit();
    	
    	startLogIn(username, passwd, loginPrefix);
    }
    
    private String fixLoginPrefix(String pref) {
    	return pref.toLowerCase().replace(" ", "").replace("\n", "").replace("\r", "");
    }
    
    private void startLogIn(String username, String passwd, String prefix) {
    	if(username.length() == 0 || pass.length() == 0 || prefix.length() == 0) {
    		Toast.makeText(this, "One or more fields are empty. Please correct and try again.", Toast.LENGTH_LONG).show();
    		return;
    	}
    		
    	if(!Utils.isOnline(this)) {
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
    
    private boolean isLoginSaved() {
    	return settings.getBoolean(SHOULD_SAVE_LOGIN, false);
    }
    
	private void savePreferences() {
        editor.putString(USERNAME, user.getText().toString().trim());
        editor.putString(PASS, pass.getText().toString().trim());
        editor.putString(PREFIX, fixLoginPrefix(prefix.getText().toString().trim()));
        
        editor.commit();
    }
    
    private void loadPreferences() {
    	user.setText(settings.getString(USERNAME, ""));
    	pass.setText(settings.getString(PASS, ""));
    	prefix.setText(settings.getString(PREFIX, ""));
    }
    
    public void showHelpUsername(View v) {
    	showHelp(1);
    }
    
    public void showHelpPassword(View v) {
    	showHelp(2);
    }
    
    public void showHelpPrefix(View v) {
    	showHelp(3);
    }
    
    public void showHelp(int helpNum) {
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.popup_main_help, null, false);
    	
    	TextView helpTitle = ((TextView) flow.findViewById(R.id.main_help_title));
        TextView helpDesc = ((TextView) flow.findViewById(R.id.main_help_desc));
        
        switch(helpNum) {
	        case 1:
	        	helpTitle.setText("Username");
	        	helpDesc.setText(getString(R.string.main_help_username));
	        	break;
	        case 2:
	        	helpTitle.setText("Password");
	        	helpDesc.setText(getString(R.string.main_help_password));
	        	break;
	        case 3:
	        	helpTitle.setText("School Loop Site Prefix");
	        	helpDesc.setText(getString(R.string.main_help_prefix));
	        	break;
        }
        
        Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        
        final Dialog popup = Utils.createLoopedDialog(this, flow, width, R.id.main_help_btn);
        
        popup.setCancelable(true);
        
        popup.show();
    }
}
