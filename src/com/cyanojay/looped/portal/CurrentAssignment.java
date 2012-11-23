package com.cyanojay.looped.portal;

import java.util.Date;

public class CurrentAssignment {
	private String name;
	private String courseName;
	private Date assignedDate;
	private Date dueDate;
	private int maxPoints;

	public CurrentAssignment() {
		this.name = "";
		this.courseName = "";
		this.assignedDate = null;
		this.dueDate = null;
		this.maxPoints = 0;
	}
	
	public CurrentAssignment(String name, String courseName, Date assignedDate, Date dueDate, int maxPoints) {
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

	public Date getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}
}
