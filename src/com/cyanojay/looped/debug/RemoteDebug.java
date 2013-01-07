package com.cyanojay.looped.debug;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.cyanojay.looped.Utils;

public final class RemoteDebug {
	private static final String REMOTE = "http://50.116.13.217";
	private static final DefaultHttpClient httpclient = new DefaultHttpClient();
	
	public static void debug(final String meta, final String error) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String debugInfo = "INFO: " + meta + "\n" +
							"VERSION: 0.81" + "\n" +
							"ERROR: " + error + "\n\n\n";
					
					HttpPost post = new HttpPost(REMOTE + "/looped_debug.php");
					
					System.out.println(debugInfo);
					
					
					ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
					postParameters.add(new BasicNameValuePair("data", debugInfo));
					
					post.setEntity(new UrlEncodedFormEntity(postParameters));
					
					httpclient.execute(post);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void debug(String error) {
		debug("", error);
	}
	
	public static void debugException(Exception e) {
		debug("", Utils.getExceptionString(e));
	}
	
	public static void debugException(String meta, Exception e) {
		debug(meta, Utils.getExceptionString(e));
	}
}
