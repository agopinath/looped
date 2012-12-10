package com.cyanojay.looped.debug;

import com.cyanojay.looped.Utils;

public class DebugMailer {
	public static final String DEBUG_FROM_NAME = "Looped Debugger";
	public static final String DEBUG_TO_NAME = "CyanoJay Works";
	private static final GmailMailer MAILER = new GmailMailer("hidden1123@gmail.com", "Ajay1123");
	private static final String META_INFO = "CRASH -" + " M: " + Utils.getDeviceName() + " V: 0.50";
	
	public static void sendDebugToDevs(String content) {
		try {
			MAILER.sendMail(META_INFO, content, "hidden1123@gmail.com", "cyanojayworks@outlook.com");
		} catch (Exception e) {}
	}
}
