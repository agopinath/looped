package com.cyanojay.looped.portal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsDetail implements Serializable {
	private String title;
	private List<String> details;
	private String content;

	public NewsDetail() {
		this.title = "";
		this.details = new ArrayList<String>();
		this.content = "";
	}
	
	public NewsDetail(String title, List<String> details, String content) {
		this.title = title;
		this.details = details;
		this.content = content;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getDetails() {
		return details;
	}

	public void addDetail(String detail) {
		this.details.add(detail);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
