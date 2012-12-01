package com.cyanojay.looped.portal;

import java.util.ArrayList;
import java.util.List;

public class MailDetail {
	private List<String> details;
	private String content;

	public MailDetail() {
		this.details = new ArrayList<String>();
		this.content = "";
	}

	public MailDetail(List<String> details, String content) {
		this.details = details;
		this.content = content;
	}
	
	public void addDetail(String detail) {
		details.add(detail);
	}

	public List<String> getDetails() {
		return details;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
