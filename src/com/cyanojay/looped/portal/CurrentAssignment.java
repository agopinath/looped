package com.cyanojay.looped.portal;



public class CurrentAssignment {
	private String name;
	private String courseName;
	private String assignedDate;
	private String dueDate;
	private int maxPoints;

	public CurrentAssignment() {
		this.name = "";
		this.courseName = "";
		this.assignedDate = "";
		this.dueDate = "";
		this.maxPoints = 0;
	}
	
	public CurrentAssignment(String name, String courseName, String assignedDate, String dueDate, int maxPoints) {
		this.name = name;
		this.courseName = courseName;
		this.assignedDate = assignedDate;
		this.dueDate = dueDate;
		this.maxPoints = maxPoints;
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

	public String getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(String assignedDate) {
		this.assignedDate = assignedDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}
	
	public String toString() {
		return name + "  " + courseName + "  " + dueDate;
	}
}
