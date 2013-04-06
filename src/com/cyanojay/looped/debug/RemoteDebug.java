package com.cyanojay.looped.debug;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.cyanojay.looped.Constants;
import com.cyanojay.looped.Utils;

public final class RemoteDebug {
	private static final DefaultHttpClient httpclient = new DefaultHttpClient();
	private static final String DEVICE = Utils.getDeviceName();
	
	private static String URL = null;
	
	public static void debug(final String meta, final String error) {
		return;
		/*new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String debugInfo = 
							"TIMESTAMP: " + (new Date()).toString() + "\n" +
							"VERSION: 0.81" + "\n" +
							"DEVICE: " + DEVICE + "\n" +
							"SCHOOL URL: " + URL + "\n" +
							"METAINFO: " + meta + "\n" +
							"ERROR: " + error + "\n\n\n";
					
					HttpPost post = new HttpPost(Constants.LOG_ADDRESS);
					
					System.out.println(Constants.LOG_ADDRESS + "\n" + debugInfo);
					
					ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
					postParameters.add(new BasicNameValuePair("data", debugInfo));
					
					post.setEntity(new UrlEncodedFormEntity(postParameters));
					
					httpclient.execute(post);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();*/
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
	
	public static void setUrl(String url) {
		if(URL == null && url != null)
			URL = url;
	}
}
