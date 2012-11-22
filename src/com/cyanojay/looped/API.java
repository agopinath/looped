package com.cyanojay.looped;

public class API {
	private static API instance;
	
	// instantiation is prevented
	private API() {}
	
	public static API get() {
		return (instance != null) ? instance : (instance = new API());
	}
}
