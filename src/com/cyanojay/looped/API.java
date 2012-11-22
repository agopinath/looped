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
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;


public final class API {
	private static API instance;
	private CookieStore authCookies;
	private String loginTestUrl;
	
	// instantiation is prevented
	private API() {}
	
	public static API get() {
		return (instance != null) ? instance : (instance = new API());
	}

	public void setAuthCookies(CookieStore authCookies) {
		this.authCookies = authCookies;
	}

	public void setLoginTestUrl(String testUrl) {
		this.loginTestUrl = testUrl;
	}
	
	public void logIn(String username, String password, String loginUrl) {
		HttpClient client = new DefaultHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	HttpPost httpPost = new HttpPost(loginUrl + "/portal/login?etarget=login_form");
    	
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("login_name", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("event.login.x", "0"));
        nameValuePairs.add(new BasicNameValuePair("event.login.y", "0"));
        
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            client.execute(httpPost, context);
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	public boolean isLoggedIn() {
		HttpClient client = new DefaultHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	HttpGet httpGet = new HttpGet(loginTestUrl);
    	HttpResponse response = null;
    	
    	client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
    	
        try {
            response = client.execute(httpGet, context);
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }
        
        BasicStatusLine responseStatus = (BasicStatusLine) response.getStatusLine();
        System.out.println("CODE: " + responseStatus.getStatusCode() + " " + responseStatus.getReasonPhrase());
        return (responseStatus.getStatusCode() == HttpStatus.SC_OK) ? true : false;
	}

}
