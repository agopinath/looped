package com.cyanojay.looped.portal;

public class MailDetail {
	private String info;
	private String content;

	public MailDetail() {
		this.info = "";
		this.content = "";
	}

	public MailDetail(String info, String content) {
		this.info = info;
		this.content = content;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
