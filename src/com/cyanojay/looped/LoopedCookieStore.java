package com.cyanojay.looped;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.CookieStore;

public class LoopedCookieStore {
	private static final Map<String, CookieStore> cookies = new HashMap<String, CookieStore>();	
	
	public static Map<String, CookieStore> getCookies() {
		return cookies;
	}
	
	public static void addCookie(String id, CookieStore cookie) {
		cookies.put(id, cookie);
	}
	
	public static CookieStore removeCookie(String id) {
		return cookies.remove(id);
	}
	
	public static CookieStore getCookie(String id) {
		return cookies.get(id);
	}
}
