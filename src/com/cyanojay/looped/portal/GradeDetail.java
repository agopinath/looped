package com.cyanojay.looped.portal;

import java.io.Serializable;

public class GradeDetail implements Serializable {
	private String detailName;
	private String category;
	private String dueDate;
	private double pointsEarned;
	private double totalPoints;
	private String comment;
	private String submissions;

	public GradeDetail() {
		this.detailName = "";
		this.category = "";
		this.dueDate = "";
		this.pointsEarned = 0.0d;
		this.totalPoints = 0.0d;
		this.comment = "";
		this.submissions = "";
	}

	public GradeDetail(String name,  String category, String dueDate, double pointsEarned,
					   double totalPoints, String comment, String submissions) {
		this.detailName = name;
		this.category = category;
		this.dueDate = dueDate;
		this.pointsEarned = pointsEarned;
		this.totalPoints = totalPoints;
		this.comment = comment;
		this.submissions = submissions;
	}
	
	public String getDetailName() {
		return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public double getPointsEarned() {
		return pointsEarned;
	}

	public void setPointsEarned(double pointsEarned) {
		this.pointsEarned = pointsEarned;
	}

	public double getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(double totalPoints) {
		this.totalPoints = totalPoints;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSubmissions() {
		return submissions;
	}

	public void setSubmissions(String submissions) {
		this.submissions = submissions;
	}

}
