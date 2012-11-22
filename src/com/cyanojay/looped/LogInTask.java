package com.cyanojay.looped;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class LogInTask extends AsyncTask<String, String, Boolean> {

	ProgressDialog progressDialog;
	private String username;
	private String pass;
	private String loginUrl;
	private Activity parent;
	
	public LogInTask(String username, String password, String loginUrl, Activity parent) {
		this.username = username;
		this.pass = password;
		this.loginUrl = "https://" + loginUrl + ".schoolloop.com";
		this.parent = parent;
		
		API.get().setLoginTestUrl(this.loginUrl + "/student/prior_schedule");
	}
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(parent, "Looped", "Logging in...");
    }

    @Override
    protected Boolean doInBackground(String... args) {
    	CookieStore cookies = getLogInCookies(loginUrl + "/portal/login");
    	
    	API.get().setAuthCookies(cookies);
    	
		API.get().logIn(username, pass, loginUrl);
		
		// check if this page, which is only accessible by logged-in sessions, returns a valid response
        return API.get().isLoggedIn();
    }
    
    private CookieStore getLogInCookies(String url) {
    	DefaultHttpClient client = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(url);
    	
    	try {
    		client.execute(httpGet);
		} catch (Exception e) {
			e.printStackTrace();
		}

    	return client.getCookieStore();
    }
    
    @Override
    protected void onProgressUpdate(String...values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean loginSuccess) {
        super.onPostExecute(loginSuccess);
        
        progressDialog.dismiss();
        
        if(loginSuccess) {
        	Log.v("", "\n\nLOG IN SUCCESS\n\n");
        	
        	Intent showPortalIntent = new Intent(parent, PortalActivity.class);
            parent.startActivity(showPortalIntent);
        } else {
        	Log.v("", "\n\nLOG IN FAIL\n\n");
        	// TODO notify user of login fail
        }
    }
}
