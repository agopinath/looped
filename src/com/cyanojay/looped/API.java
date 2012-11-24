package com.cyanojay.looped;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cyanojay.looped.portal.Course;
import com.cyanojay.looped.portal.CurrentAssignment;
import com.cyanojay.looped.portal.GradeDetail;
import com.cyanojay.looped.portal.NewsArticle;


public final class API {
	private static API instance;
	
	private CookieStore authCookies;
	private Document portal;
	
	private String username;
	private String password;
	private String portalUrl;
	private String loginTestUrl;
	
	private boolean loginStatus;
	
	// instantiation is prevented
	private API() {}
	
	public static API get() {
		return (instance != null) ? instance : (instance = new API());
	}

	public void setAuthCookies(CookieStore authCookies) {
		this.authCookies = authCookies;
	}

	public void setLoginTestUrl(String testUrl) {
		this.loginTestUrl = testUrl;
	}
	
	public void setCredentials(String username, String password, String portalUrl) {
		this.username = username;
		this.password = password;
		this.portalUrl = portalUrl;
	}
	
	public boolean logIn() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	HttpPost httpPost = new HttpPost(portalUrl + "/portal/login?etarget=login_form");
    	
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("login_name", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("event.login.x", "0"));
        nameValuePairs.add(new BasicNameValuePair("event.login.y", "0"));
        
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        client.execute(httpPost, context);
        
        return (loginStatus = isLoggedIn(true));
	}

	public boolean isLoggedIn(boolean deep) {
		if(!deep) return loginStatus;
		
		HttpClient client = new DefaultHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	HttpGet httpGet = new HttpGet(loginTestUrl);
    	HttpResponse response = null;
    	
    	client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
    	
    	try {
    		response = client.execute(httpGet, context);
    	} catch(Exception e) { 
    		return false; 
    	}
        
        BasicStatusLine responseStatus = (BasicStatusLine) response.getStatusLine();
        System.out.println("CODE: " + responseStatus.getStatusCode() + " " + responseStatus.getReasonPhrase());
        return (loginStatus = (responseStatus.getStatusCode() == HttpStatus.SC_OK)) ? true : false;
	}
	
	public void refreshPortal() throws IOException {
		if(!loginStatus) return;
		
		HttpClient client = new DefaultHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	HttpGet httpGet = new HttpGet(portalUrl);
    	
		HttpResponse response = client.execute(httpGet, context);
		portal = Jsoup.parse((InputStream) response.getEntity().getContent(), null, portalUrl);
	}
	
	public String getPortalTitle() {
		return portal.title();
	}
	
	public List<Course> getCourses() {
		List<Course> courses = new ArrayList<Course>();
		
		// select everything in the table holding the grades
	    Elements courseBlock = portal.body().select("tbody.hub_general_body tr");
	    
	    for(Element courseRow: courseBlock) {
	    	// create new empty course to store course information
	    	Course newCourse = new Course();
	    	
	    	// only single element present for subject, select the name
	    	Elements subject = courseRow.select("td.left a");
	    	
	    	// select the first three grade elements (percent, letter grade, num of zeroes), exclude 'Progress Report'
	    	Elements grades = courseRow.select("td.list_text");
	    	
	    	newCourse.setName(subject.text());
	    	
	    	// if no grades listed, grade must be listed as 'None Published', so select that
	    	if(grades.size() == 0)
	    		grades = courseRow.select("td.list_text_light");
	    	
	    	// if grades are still empty, they are invalid, so skip
	    	if(grades.size() == 0) continue;
	    	
	    	for(int i = 0; i < grades.size(); i++) {
	    		Element currGrade = grades.get(i);
	    		
	    		if(i == 0) newCourse.setLetterGrade(currGrade.text());
	    		if(i == 1) newCourse.setPercentGrade(currGrade.text());
	    		if(i == 2) newCourse.setNumZeros(Integer.parseInt(currGrade.text()));
	    		if(i == 3) {
	    			String detailsUrl = portalUrl + currGrade.child(0).attr("href");
	    			newCourse.setDetailsUrl(detailsUrl);
	    		}
	    	}
	    	
	    	courses.add(newCourse);
	    }
	    
	    return courses;
	}
	
	public List<CurrentAssignment> getCurrentAssignments() {
		List<CurrentAssignment> assignments = new ArrayList<CurrentAssignment>();
		
		// select everything in the table holding the assignments
	    Elements assignmentsBlock = portal.body().select("tbody.hub_general_body tr");
	    
	    for(Element assignmentRow: assignmentsBlock) {
	    	// create new empty assignment to store course information
	    	CurrentAssignment assignment = new CurrentAssignment();
	    	
	    	// select the assignment details that include the title, respective course, and due date 
	    	Elements details = assignmentRow.select("div.list_text");
	    	
	    	// if assignments are  empty, they are invalid, so skip
	    	if(details.size() == 0) continue;
	    	
	    	for(int i = 0; i < details.size(); i++) {
	    		Element currAssignment = details.get(i);

	    		if(i == 0) assignment.setName(currAssignment.text());
	    		if(i == 1) assignment.setCourseName(currAssignment.text());
	    		if(i == 2) assignment.setDueDate(currAssignment.text());
	    	}
	    	
	    	assignments.add(assignment);
	    }
	    
	    return assignments;
	}
	
	public List<NewsArticle> getNews() {
		List<NewsArticle> news = new ArrayList<NewsArticle>();
		
		// select everything in the div holding the article names
	    Elements articleNames = portal.body().select("div.os a.module_link");
	    
	    // select everything in the div holding the article info (date posted and author)
	    Elements articleInfo = portal.body().select("div.os td.list_text");
	    
	    for(int i = 0; i < articleNames.size(); i++) {
	    	// create new empty news article to store the article info
	    	NewsArticle article = new NewsArticle();
	    	
	    	// select info according to how it is ordered/structured in the HTML
	    	Element title = articleNames.get(i);
	    	Element author = articleInfo.get(2*i);
	    	Element date = articleInfo.get((2*i)+1);
	    	
	    	// split the author field into the author's name and the author's type
	    	String authorData[] = author.text().split(" - ");
	    	
	    	article.setArticleName(title.text());
	    	article.setAuthor(authorData[0]);
	    	article.setAuthorType(authorData[1]);
	    	article.setDatePosted(date.text());
	    	
	    	news.add(article);
	    }
	    
	    return news;
	}

	public List<GradeDetail> getGradeDetails(Course course) throws ClientProtocolException, IOException {
		List<GradeDetail> detailsList = new ArrayList<GradeDetail>();
		
		// construct and send a GET request to the URL where the Course grade details are stored
		HttpClient client = new DefaultHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	HttpGet httpGet = new HttpGet(course.getDetailsUrl());
    	
		HttpResponse response = client.execute(httpGet, context);
		Document detailsPage = Jsoup.parse((InputStream) response.getEntity().getContent(), null, portalUrl);
		
		// select all rows of the table containing grade details
	    Elements details = detailsPage.body().select("tbody.general_body tr");

	    for(Element detail: details) {
	    	GradeDetail newDetail = new GradeDetail();
	    	
	    	// select all individual elements in each row
	    	Elements data = detail.select("td");
	    	
	    	newDetail.setCategory(data.get(0).text());
	    	newDetail.setDueDate(data.get(1).text());
	    	newDetail.setDetailName(data.get(2).text());
	    	newDetail.setComment(data.get(5).text());
	    	newDetail.setSubmissions(data.get(6).text());
	    	
	    	String scoreData = data.get(4).text().trim();
	    	
	    	// if the score is entered/valid, split the grade into total points and earned points
	    	if(scoreData.length() > 5) {
	    		// remove trailing '=' and percentage data
	    		scoreData = scoreData.substring(0, scoreData.indexOf('='));
	    		
	    		// split into respective parts and parse
	    		String[] scoreParts = scoreData.split(" / ");
	    		newDetail.setPointsEarned(Double.parseDouble(scoreParts[0].trim()));
	    		newDetail.setTotalPoints(Double.parseDouble(scoreParts[1].trim()));
	    	}
	    	
	    	detailsList.add(newDetail);
	    }
		
	    return detailsList;
	}
}
