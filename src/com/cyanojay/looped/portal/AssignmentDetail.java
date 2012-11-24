package com.cyanojay.looped.portal;

import java.util.ArrayList;
import java.util.List;

public class AssignmentDetail {
	private String name;
	private String targetAudience;
	private String explanation;
	private List<String> details;
	
	public AssignmentDetail() {
		this.name = "";
		this.targetAudience = "";
		this.setExplanation("");
		this.details = new ArrayList<String>();
	}
	
	public AssignmentDetail(String name, String audience, String explanation, List<String> details) {
		this.name = name;
		this.targetAudience = audience;
		this.details = details;
		this.setExplanation(explanation);
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
}
