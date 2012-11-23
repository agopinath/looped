package com.cyanojay.looped.portal;

public class NewsArticle {
	private String articleName;
	private String author;
	private String datePosted;
	private String authorType;

	public NewsArticle() {
		this.articleName = "";
		this.author = "";
		this.setAuthorType("");
		this.datePosted = "";
	}
	
	public NewsArticle(String articleName, String author, String authorType, String datePosted) {
		this.articleName = articleName;
		this.author = author;
		this.setAuthorType(authorType);
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

	public String getAuthorType() {
		return authorType;
	}

	public void setAuthorType(String authorType) {
		this.authorType = authorType;
	}
}
