package com.cyanojay.looped.portal;

public class MailEntry {
	private String timestamp;
	private String involvedParties;
	private String subject;
	
	public MailEntry() {
		this.timestamp = "";
		this.involvedParties = "";
		this.subject = "";
	}
	
	public MailEntry(String timestamp, String parties, String subject) {
		this.timestamp = timestamp;
		this.involvedParties = parties;
		this.subject = subject;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getInvolvedParties() {
		return involvedParties;
	}

	public void setInvolvedParties(String involvedParties) {
		this.involvedParties = involvedParties;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
