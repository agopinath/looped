package com.cyanojay.looped.portal.grades;

import java.io.Serializable;

public class Course implements Serializable {
	private String name;
	private String percentGrade;
	private String letterGrade;
	private String detailsUrl;
	private String period;
	private int numZeros;
	
	public Course() {
		this.name = "";
		this.percentGrade = "";
		this.letterGrade = "";
		this.numZeros = 0;
		this.detailsUrl = "";
	}
	
	public Course(String name, String grade, String letterGrade, int numZeros, String detailsUrl) {
		this.name = name;
		this.percentGrade = grade;
		this.letterGrade = letterGrade;
		this.numZeros = numZeros;
		this.detailsUrl = detailsUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPercentGrade() {
		return percentGrade;
	}

	public void setPercentGrade(String grade) {
		this.percentGrade = grade;
	}

	public String getLetterGrade() {
		return letterGrade;
	}

	public void setLetterGrade(String letterGrade) {
		this.letterGrade = letterGrade;
	}

	public int getNumZeros() {
		return numZeros;
	}

	public void setNumZeros(int numZeros) {
		this.numZeros = numZeros;
	}

	public String getDetailsUrl() {
		return detailsUrl;
	}

	public void setDetailsUrl(String detailsUrl) {
		this.detailsUrl = detailsUrl;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
}
