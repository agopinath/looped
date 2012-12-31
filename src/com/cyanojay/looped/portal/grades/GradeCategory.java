package com.cyanojay.looped.portal.grades;

public class GradeCategory {
	public static final double UNWEIGHTED = -5.0;
	
	private final String name;
	private final double weight;
	
	private int assignmentCount;
	private double scaledWeight;
	
	public GradeCategory(String name, double weight) {
		this.name = name;
		this.weight = weight;
		
		this.scaledWeight = weight;
		this.assignmentCount = 0;
	}
	
	public String getName() {
		return name;
	}

	public double getWeight() {
		return weight;
	}
	
	public int getAssignmentCount() {
		return assignmentCount;
	}

	public void setAssignmentCount(int assignmentCount) {
		this.assignmentCount = assignmentCount;
	}
	
	public void incrementAssignmentCount(int increment) {
		this.assignmentCount += increment;
	}
	
	public double getScaledWeight() {
		return scaledWeight;
	}

	public void setScaledWeight(double scaledWeight) {
		this.scaledWeight = scaledWeight;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object other) {
		return name.equals(((GradeCategory) other).getName());
	}
}
