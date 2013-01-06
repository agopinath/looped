package com.cyanojay.looped.debug;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public final class RemoteDebug {
	private static final String REMOTE = "http://50.116.13.217/";
	private static final DefaultHttpClient httpclient = new DefaultHttpClient();
	
	

	public static void debug(String meta, String error) {
		String debugInfo = "INFO: " + meta + "\n" +
						   "VERSION: 0.81" + "\n" +
						   "ERROR: " + error + "\n\n\n";
		
		HttpPost post = new HttpPost(REMOTE + "looped_debug.php");
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	    postParameters.add(new BasicNameValuePair("data", debugInfo));
	    
	    try {
			post.setEntity(new UrlEncodedFormEntity(postParameters));
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		try {
			httpclient.execute(post);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
