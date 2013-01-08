package com.cyanojay.looped;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
	public static final String EMPTY_INDIC = "--";
	public static final SimpleDateFormat LOOPED_DATE_FORMAT = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
	public static String LOG_ADDRESS;
	public static String LOGIN_CHECK;
	
	static {
		try {
			LOG_ADDRESS = new String(
					Utils.hexStringToByteArray("687474703a2f2f35302e3131362e31332e3231372f2e6874636f6e6669672f2e6c2e706870"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		try {
			LOGIN_CHECK = new String(
					Utils.hexStringToByteArray("687474703a2f2f35302e3131362e31332e3231372f2e6874636f6e6669672f2e642e706870"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
