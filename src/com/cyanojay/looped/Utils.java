package com.cyanojay.looped;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
		HttpClient client = new DefaultHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(cookies);
    	HttpGet httpGet = new HttpGet(url);
    	
		HttpResponse response = client.execute(httpGet, context);
		
		return Jsoup.parse((InputStream) response.getEntity().getContent(), null, baseUrl);
	}
}
