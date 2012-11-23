package com.cyanojay.looped.portal;

public class NewsArticle {
	private String articleName;
	private String author;
	private String datePosted;

	public NewsArticle() {
		this.articleName = "";
		this.author = "";
		this.datePosted = "";
	}
	
	public NewsArticle(String articleName, String author, String datePosted) {
		this.articleName = articleName;
		this.author = author;
		this.datePosted = datePosted;
	}

	public String getArticleName() {
		return articleName;
	}

	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDatePosted() {
		return datePosted;
	}

	public void setDatePosted(String datePosted) {
		this.datePosted = datePosted;
	}
}
