package com.cyanojay.looped;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.widget.EditText;
import android.widget.Toast;

import com.cyanojay.looped.net.LogInTask;
import com.cyanojay.looped.portal.BaseActivity;

public class MainActivity extends BaseActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setUpKeys(((EditText) findViewById(R.id.sl_uname)));
        setUpKeys(((EditText) findViewById(R.id.sl_pass)));
        setUpKeys(((EditText) findViewById(R.id.sl_prefix)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void logIn(View view) {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(findViewById(R.id.sl_prefix).getWindowToken(), 0);

    	String username = ((EditText) findViewById(R.id.sl_uname)).getText().toString().trim();
    	String pass = ((EditText) findViewById(R.id.sl_pass)).getText().toString().trim();
    	String loginPrefix = ((EditText) findViewById(R.id.sl_prefix)).getText().toString().trim();
    	
    	if(username.length() == 0 || 
    		pass.length() == 0 || 
    		loginPrefix.length() == 0)
    		Toast.makeText(this, "One or more fields are empty Please correct and try again.", Toast.LENGTH_LONG).show();
    		
    	if(!isOnline()) {
    		Toast.makeText(this, "This app requires Internet access. Please connect to the Internet and try again.", 
    					   Toast.LENGTH_LONG).show();
    	} else if(hasWhitespace(loginPrefix)) {
    		Toast.makeText(this, "The login prefix cannot contain whitespaces. Please try again.", 
					   Toast.LENGTH_LONG).show();
    	} else {
	    	LogInTask logInTask = new LogInTask(username, pass, loginPrefix, this); 
	    	logInTask.execute();
    	}
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
