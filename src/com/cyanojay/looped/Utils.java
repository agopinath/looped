package com.cyanojay.looped;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cyanojay.looped.net.API;
import com.cyanojay.looped.net.MySSLSocketFactory;

public class Utils {
	public static void printHTTPResponse(InputStream entityStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(entityStream));
        String result = "";
            
        while((result = reader.readLine()) != null)
            System.out.println(result);
	}
	
	public static BasicHttpContext getCookifiedHttpContext(CookieStore cookies) {
		BasicHttpContext cookiedContext = new BasicHttpContext();
		cookiedContext.setAttribute(ClientContext.COOKIE_STORE, cookies);
		
		return cookiedContext;
	}
	
	public static String convertPrefixToAddress(String urlPrefix) {
		return "https://" + urlPrefix + ".schoolloop.com";
	}
	
	public static Document getJsoupDocFromUrl(String url, String baseUrl, CookieStore cookies) throws IllegalStateException, IOException {
		HttpClient client = getNewHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(cookies);
    	HttpGet httpGet = new HttpGet(url);
    	
		HttpResponse response = client.execute(httpGet, context);
		
		return Jsoup.parse((InputStream) response.getEntity().getContent(), null, baseUrl);
	}
	
	public static HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
	public static int getApiVer() {
		return Build.VERSION.SDK_INT;
	}
	
	public static void lockOrientation(Activity act) {
		// lock orientation to avoid crash during login
		int currOrientation = act.getResources().getConfiguration().orientation;
		
		if(currOrientation == Configuration.ORIENTATION_LANDSCAPE)
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		else
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	public static void unlockOrientation(Activity act) {
		// unlock orientation after logging in
		act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}
	
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo netInfo = cm.getActiveNetworkInfo();
    	
    	return (netInfo != null && netInfo.isConnectedOrConnecting());
	}
	
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
		  return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}
	
	public static String getPrintViewifiedUrl(String rootUrl) {
		return rootUrl + "&template=print";
	}
	
	public static CookieStore getCookies(String url) {
    	DefaultHttpClient client = (DefaultHttpClient) Utils.getNewHttpClient();
    	HttpGet httpGet = new HttpGet(url);
    	
    	try {
    		client.execute(httpGet);
		} catch (Exception e) {
			e.printStackTrace();
		}

    	return client.getCookieStore();
    }
	
	public static String getExceptionString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		return sw.toString();
	}
	
	public static void showAbout(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay(); 
	    int width = display.getWidth();
	    
    	LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
        LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.about_popup, null, false);
        TextView about = (TextView) flow.findViewById(R.id.about_text);
        
        about.setText(Html.fromHtml(activity.getString(R.string.about_text)));
					        
    	final PopupWindow pw = new PopupWindow(flow, width-((int)(0.25*width)), LayoutParams.WRAP_CONTENT, true);
        
    	((Button) flow.findViewById(R.id.about_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        
        pw.showAtLocation(flow, Gravity.CENTER, 10, 10);
	}
	
	public static void logOut(Activity activity) {
		Toast.makeText(activity, "Logged out successfully.", Toast.LENGTH_SHORT).show();
    	
    	if(Utils.isOnline(activity)) {
    		API.get().logOut();
    	}
    	
    	Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	intent.putExtra(MainActivity.IS_FROM_LOGOUT, true);
    	
    	activity.startActivity(intent);
	}
	
	public static TextView getCenteredTextView(Context c, String s) {
		TextView text = new TextView(c);
    	
    	text.setPadding(40, 40, 40, 40);
    	text.setGravity(Gravity.CENTER);
    	text.setTextAppearance(c, android.R.attr.textAppearanceLarge);
    	text.setText(s);
    	
    	return text;
	}
	
	public static void showViewOnTop(View a, View b) {
		ViewGroup parent = (ViewGroup) a.getParent();
		
	    parent.addView(b);
	    parent.bringChildToFront(b);
	}
}
