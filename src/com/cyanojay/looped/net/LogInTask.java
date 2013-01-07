package com.cyanojay.looped.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;

import org.apache.http.client.CookieStore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cyanojay.looped.Constants;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.debug.RemoteDebug;
import com.cyanojay.looped.portal.PortalActivity;

public class LogInTask extends AsyncTask<String, String, LogInTask.LoginStatus> {
	
	public enum LoginStatus {
		LOGIN_SUCCESS, LOGIN_FAIL, SERVER_ERROR, CONNECTION_ABORTED
	}
	
	private ProgressDialog progressDialog;
	private String username;
	private String pass;
	private String loginUrl;
	private Activity parent;
	
	public LogInTask(String username, String password, String loginPrefix, Activity parent) {
		this.username = username;
		this.pass = password;
		this.loginUrl = Utils.convertPrefixToAddress(loginPrefix);
		this.parent = parent;
		
		API.get().setLoginTestUrl(this.loginUrl + "/student/prior_schedule");
	}
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(parent, "Looped", "Logging in...");
    }

    @Override
    protected LoginStatus doInBackground(String... args) {
    	CookieStore cookies = Utils.getCookies(loginUrl + "/portal/login");
    	
    	API.get().setCredentials(username, pass, loginUrl);
    	API.get().setAuthCookies(cookies);
		
    	try {
    		API.get().logIn();
    		API.get().refreshPortal();
    		
    		// check if this page, which is only accessible by logged-in sessions, returns a valid response
    		if(API.get().isLoggedIn(false))
    			return LoginStatus.LOGIN_SUCCESS;
    		else 
    			return LoginStatus.LOGIN_FAIL;
    		
    	} catch(ConnectException e) {
    		e.printStackTrace();
    		
    		return LoginStatus.SERVER_ERROR;
    	} catch(IOException e) {
    		e.printStackTrace();
    		
    		return LoginStatus.CONNECTION_ABORTED;
    	} finally {
    		parent.runOnUiThread(new Runnable() {
				@Override
				public void run() {
			        // in case dialog does no longer exist, catch the error
					Utils.safelyDismissDialog(progressDialog);
				}
    		});
    		
    		Utils.unlockOrientation(parent);
    	}
    }

    @Override
    protected void onPostExecute(LoginStatus loginStatus) {
        super.onPostExecute(loginStatus);
        
        switch(loginStatus) {
	        case LOGIN_SUCCESS:
	        	//System.out.println("\n\nLOG IN SUCCESS\n\n");
	        	Constants.SCHOOL_URL = loginUrl;
	        	
	        	new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Utils.ensureLogin(loginUrl);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
	        	}).start();
	        	
	        	Intent showPortalIntent = new Intent(parent, PortalActivity.class);
	        	parent.startActivity(showPortalIntent);
	        	break;
	        case LOGIN_FAIL:
	        	//System.out.println("\n\nLOG IN FAIL\n\n");
	        	
	        	Toast.makeText(parent, "Incorrect username/password/login URL prefix, please try again.", Toast.LENGTH_LONG).show();
	        	break;
	        case SERVER_ERROR:
	        	//System.out.println("\n\nSERVER ERROR...LOG IN FAIL\n\n");
	        	
	        	Toast.makeText(parent, "Problems connecting to server, please try again later.", Toast.LENGTH_LONG).show();
	        	break;
	        case CONNECTION_ABORTED:
	        	//System.out.println("\n\nCONNECTION ABORTED...LOG IN FAIL\n\n");
	        	
	        	Toast.makeText(parent, "Network connection lost while logging in, please re-connect and try again.", Toast.LENGTH_LONG).show();
	        	break;
        }
    }
}
