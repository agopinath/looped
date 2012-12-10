package com.cyanojay.looped.debug;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.SimpleEmail;

import com.cyanojay.looped.Utils;

import android.content.Context;

public class DebugMailer {
	public static final String DEBUG_FROM_NAME = "Looped Debugger";
	public static final String DEBUG_TO_NAME = "CyanoJay Works";
	private static final GmailMailer MAILER = new GmailMailer("hidden1123@gmail.com", "Ajay1123");
	private static final String META_INFO = "CRASH -" + " M: " + Utils.getDeviceName() + " V: 0.40";
	
	public static String sendUsingBuiltInAPI(String host, String from, String to, String subject, String message){
	    String results = "";
	    String[] rec = to.split(";");

	    String domain = from.substring(from.indexOf('@') + 1);

	    for (int i = 0; i < rec.length; i++) {
	        try {
	            Socket s = new Socket(host, 25);

	            PrintStream out = new PrintStream(s.getOutputStream());

	            out.print("EHLO " + domain + "\r\n");
	            out.print("MAIL FROM: <" + from + ">\r\n");
	            out.print("RCPT TO: <" + rec[i].trim() + ">\r\n");
	            out.print("DATA\r\n");
	            out.print("From: <" + from + ">\r\n");
	            out.print("To: <" + rec[i].trim() + ">\r\n");
	            out.print("Subject: " + subject + "\r\n");
	            out.print(message + "\r\n");
	            out.print(".\r\n");

	            results += rec[i].trim() + " sent\n";

	            s.close();
	        } catch (Exception ex) {
	            results += rec[i].trim() + " error - not sent\n";
	        }
	    }
	    
	    System.out.println(to + " " + from + " ----- " + results);
	    
	    return results;
	}
	
	public static void sendUsingMailAPI(String host, String from, String to, String subject, String content, Date date) {
		 try {
		     System.out.println(from + " " + to);
		     
		     SimpleEmail email = new SimpleEmail();
		     email.setHostName(host);
		     email.setFrom(from, DEBUG_FROM_NAME);
		     email.addTo(to, DEBUG_TO_NAME);
		     email.setSubject(subject);
		     email.setMsg(content);
		     email.send();
		     
		     System.out.println("Message sent");
		 } catch(Exception e) {
			 e.printStackTrace();
		 }
	 }
	
	public static void sendUsingMailAPI(String host, final String from, String to, final String pass, String subject, String content, Date date) {
	    Properties properties = System.getProperties();

	    properties.setProperty("mail.smtp.host", host);

	    Session session = Session.getInstance(properties, new Authenticator() {
	        @Override
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(from, pass);
	        }
	    });

	    try {
	        MimeMessage message = new MimeMessage(session);

	        message.setFrom(new InternetAddress(from));
	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	        message.setSubject(subject);
	        message.setText(content);

	        Transport.send(message);
	        System.out.println("Sent message successfully....");
	    } catch (MessagingException mex) {
	        mex.printStackTrace();
	    }
	 }
	
	public static void sendDebugMailAPI(String content) {
		try {
			MAILER.sendMail(META_INFO, content, "hidden1123@gmail.com", "cyanojayworks@outlook.com");
		} catch (Exception e) {}
	}
}
