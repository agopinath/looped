package com.cyanojay.looped.portal;

import java.io.Serializable;

public class NewsArticle implements Serializable {
	private String articleName;
	private String author;
	private String datePosted;
	private String authorType;
	private String articleUrl;
	
	public NewsArticle() {
		this.articleName = "";
		this.author = "";
		this.author = "";
		this.datePosted = "";
		this.setArticleUrl("");
	}
	
	public NewsArticle(String articleName, String author, String authorType, String datePosted, String articleUrl) {
		this.articleName = articleName;
		this.author = author;
		this.author = "";
		this.datePosted = datePosted;
		this.setArticleUrl(articleUrl);
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

	public String getArticleUrl() {
		return articleUrl;
	}

	public void setArticleUrl(String articleUrl) {
		this.articleUrl = articleUrl;
	}
}
