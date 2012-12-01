package com.cyanojay.looped.portal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDetail implements Serializable {
	private String name;
	private String targetAudience;
	private String explanation;
	private String attachments;
	private List<String> details;
	
	public AssignmentDetail() {
		this.name = "";
		this.targetAudience = "";
		this.explanation = "";
		this.details = new ArrayList<String>();
		this.attachments = "";
	}
	
	public AssignmentDetail(String name, String audience, String explanation, List<String> details, String attachments) {
		this.name = name;
		this.targetAudience = audience;
		this.details = details;
		this.explanation = explanation;
		this.attachments = attachments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTargetAudience() {
		return targetAudience;
	}

	public void setTargetAudience(String targetAudience) {
		this.targetAudience = targetAudience;
	}

	public List<String> getDetails() {
		return details;
	}

	public void addDetail(String detail) {
		this.details.add(detail);
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}
}
