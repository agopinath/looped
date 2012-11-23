package com.cyanojay.looped.portal;

public class Course {
	private String name;
	private String percentGrade;
	private String letterGrade;
	private int numZeros;

	public Course() {
		this.name = "";
		this.percentGrade = "";
		this.letterGrade = "";
		this.numZeros = 0;
	}
	
	public Course(String name, String grade, String letterGrade, int numZeros) {
		this.name = name;
		this.percentGrade = grade;
		this.letterGrade = letterGrade;
		this.numZeros = numZeros;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String setPercentGrade() {
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
}
