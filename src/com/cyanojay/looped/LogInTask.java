package com.cyanojay.looped;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class LogInTask extends AsyncTask<String, String, Void> {

	ProgressDialog progressDialog;
	private String username;
	private String pass;
	private String loginUrl;
	private Activity parent;
	
	public LogInTask(String username, String password, String loginUrl, Activity parent) {
		this.username = username;
		this.pass = password;
		this.loginUrl = "https://" + loginUrl + ".schoolloop.com/portal/login";
		this.parent = parent;
	}
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(parent, "Looped", "Logging in...");
    }

    @Override
    protected Void doInBackground(String... args) {
    	CookieStore cookies = getCookies(loginUrl);
    	boolean success = logIn(cookies);
    	
    	if(success) Log.v("", "\n\nLOG IN SUCCESS\n\n");
    	else Log.v("", "\n\nLOG IN FAIL\n\n");
    	
		return null; 
    }
    
    private CookieStore getCookies(String url) {
    	DefaultHttpClient client = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(url);
    	
    	try {
			client.execute(httpGet);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return client.getCookieStore();
    }
    
    private boolean logIn(CookieStore cookies) {
    	HttpClient client = new DefaultHttpClient();
    	BasicHttpContext context = new BasicHttpContext();
    	HttpPost httpPost = new HttpPost(loginUrl + "?etarget=login_form");
    	HttpResponse response = null;
    	
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("login_name", username));
        nameValuePairs.add(new BasicNameValuePair("password", pass));
        nameValuePairs.add(new BasicNameValuePair("event.login.x", "0"));
        nameValuePairs.add(new BasicNameValuePair("event.login.y", "0"));
        
        context.setAttribute(ClientContext.COOKIE_STORE, cookies);
        
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
            response = client.execute(httpPost, context);
            
            if (response.getEntity() != null) {
               Utils.printHTTPResponse(response.getEntity().getContent());
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }
        
        BasicStatusLine responseStatus = (BasicStatusLine) response.getStatusLine();
        return (responseStatus.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) ? true : false;
    }
    
    @Override
    protected void onProgressUpdate(String...values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
    }
}
