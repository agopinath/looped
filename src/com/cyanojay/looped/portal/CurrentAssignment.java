package com.cyanojay.looped.portal;

import java.io.Serializable;



public class CurrentAssignment implements Serializable {
	private String name;
	private String courseName;
	private String dueDate;
	private String detailsUrl;
	
	public CurrentAssignment() {
		this.name = "";
		this.courseName = "";
		this.dueDate = "";
		this.detailsUrl = "";
	}
	
	public CurrentAssignment(String name, String courseName, String dueDate, String detailsUrl) {
		this.name = name;
		this.courseName = courseName;
		this.dueDate = dueDate;
		this.detailsUrl = detailsUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getDetailsUrl() {
		return detailsUrl;
	}

	public void setDetailsUrl(String detailsUrl) {
		this.detailsUrl = detailsUrl;
	}
}
