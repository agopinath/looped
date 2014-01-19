package com.cyanojay.looped.net;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cyanojay.looped.Constants;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.debug.RemoteDebug;
import com.cyanojay.looped.portal.assignments.AssignmentDetail;
import com.cyanojay.looped.portal.assignments.CurrentAssignment;
import com.cyanojay.looped.portal.grades.Course;
import com.cyanojay.looped.portal.grades.GradeCategory;
import com.cyanojay.looped.portal.grades.GradeDetail;
import com.cyanojay.looped.portal.loopmail.MailDetail;
import com.cyanojay.looped.portal.loopmail.MailEntry;
import com.cyanojay.looped.portal.news.NewsArticle;
import com.cyanojay.looped.portal.news.NewsDetail;


public final class API {
	private static API instance;
	
	private CookieStore authCookies;
	private Document portal;
	private Document loopMail;
	
	private String username;
	private String password;
	private String portalUrl;
	private String loginTestUrl;

	private boolean loginStatus;
	
	// instantiation is prevented
	private API() {}
	
	public static synchronized API get() {
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
	
	public void logIn() throws ClientProtocolException, IOException {
		// make request to portal URL and store cookies
    	CookieStore cookies = Utils.getCookies(portalUrl); 
    	setAuthCookies(cookies);
    	
    	// make request to login page and store form_data_id value
    	Document loginForm = Utils.getJsoupDocFromUrl(portalUrl + "/portal/login?d=x", portalUrl, authCookies);
		String formDataId = loginForm.select("input#form_data_id").attr("value");
    	
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	
    	HttpPost httpPost = new HttpPost(portalUrl + "/portal/login?etarget=login_form");
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("login_name", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("event.login.x", "0"));
        nameValuePairs.add(new BasicNameValuePair("event.login.y", "0"));
        // supply stored form_data_id value as necessary parameter
        nameValuePairs.add(new BasicNameValuePair("form_data_id", formDataId)); 
        
        HttpClient client = Utils.getNewHttpClient();
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        client.execute(httpPost, context);
	}
	
	public void logOut() {
		HttpClient client = Utils.getNewHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	HttpGet httpGet = new HttpGet(portalUrl + "/portal/logout?d=x");	
    	
    	try {
    		client.execute(httpGet, context);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	setAuthCookies(null);
	}
	
	public boolean isLoggedIn(boolean deep) {
		if(!deep) return loginStatus;
		
		HttpClient client = Utils.getNewHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	HttpGet httpGet = new HttpGet(loginTestUrl);
    	HttpResponse response = null;
    	
    	client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
    	
    	try {
    		response = client.execute(httpGet, context);
    	} catch(Exception e) {
    		e.printStackTrace();
    		return false; 
    	}
        
        BasicStatusLine responseStatus = (BasicStatusLine) response.getStatusLine();
        System.out.println("CODE: " + responseStatus.getStatusCode() + " " + responseStatus.getReasonPhrase());
        return (loginStatus = (responseStatus.getStatusCode() == HttpStatus.SC_OK));
	}
	
	public void refreshPortal() throws IOException {
		if(!isLoggedIn(true)) return;
		
		portal = Utils.getJsoupDocFromUrl(portalUrl, portalUrl, authCookies);
		loopMail = Utils.getJsoupDocFromUrl(portalUrl + "/mail/inbox?d=x", portalUrl, authCookies);
	}
	
	public void refreshMainPortal() throws IOException {
		if(!isLoggedIn(true)) return;
		
		portal = Utils.getJsoupDocFromUrl(portalUrl, portalUrl, authCookies);
	}
	
	public void refreshLoopMail() throws IOException {
		if(!isLoggedIn(true)) return;
		
		loopMail = Utils.getJsoupDocFromUrl(portalUrl + "/mail/inbox?d=x", portalUrl, authCookies);
	}
	
	public void preventCookieExpire() throws IOException {
		if(!isLoggedIn(false)) return;
		
		HttpClient client = Utils.getNewHttpClient();
    	BasicHttpContext context = Utils.getCookifiedHttpContext(authCookies);
    	HttpGet httpGet = new HttpGet(loginTestUrl);	
    	
    	try {
    		client.execute(httpGet, context);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
	}
	
	public String getPortalTitle() {
		return portal.title();
	}
	
	public List<Course> getCourses() throws Exception {
		List<Course> courses = new ArrayList<Course>();
		
		if(portal == null) return courses;
		
		// select everything in the table holding the grades
	    Elements courseBlock = portal.body().select("table.row tr");
	    
	    // select all the progress report links for the courses
	    Elements progReports = portal.body().select("div.course_links a:eq(0)");
	    
	    for(int i = 0; i < courseBlock.size(); i++) {
	    	Element courseRow = courseBlock.get(i);
	    	Element progReport = progReports.get(i);
	    	boolean isNotPublished = false;
	    	
	    	// create new empty course to store course information
	    	Course newCourse = new Course();
	    	
	    	// only single element present for subject, select the name
	    	Elements subject = courseRow.select("td.course");
	    	newCourse.setName(subject.text());
	    	
	    	String period = courseRow.select("td.period").text().trim();
	    	String letterGrade = courseRow.select("td.grade").text().trim();
	    	String pctGrade = courseRow.select("td.percent").text().trim();
	    	String numZeros = courseRow.select("td.zeros").text().replace("Zeros:", "").trim();
	    	/*// if no grades listed, grade must be listed as 'None Published', so select that
	    	if(grades.size() == 0) {
	    		grades = courseRow.select("td.list_text_light");
	    		isNotPublished = true;
	    	}*/
	    	
	    	// if grades is still empty, row is invalid, so skip
	    	//if(courseRow..size() == 0) continue;
	    	
	    	// if grades aren't published, add to the list and skip to the next row
	    	/*if(isNotPublished) {
	    		courses.add(newCourse);
	    		continue;
	    	}*/

			newCourse.setLetterGrade(letterGrade);
			newCourse.setPercentGrade(pctGrade);
			try {
				newCourse.setNumZeros(Integer.parseInt(numZeros));
			} catch (NumberFormatException e) {
				newCourse.setNumZeros(0);
			}

			Elements link = progReport.select("a[href]");

			if (!(link.size() == 0) && link.size() == 1) {
				String detailsUrl = portalUrl + link.first().attr("href");
				newCourse.setDetailsUrl(Utils.getPrintViewifiedUrl(detailsUrl));
				System.out.println("Checking URL: "
						+ Utils.getPrintViewifiedUrl(detailsUrl));
			}

			if (newCourse.getDetailsUrl() == null && link != null)
				RemoteDebug.debug("course details is null", link.outerHtml());
			
			newCourse.setPeriod("P" + period);
			
	    	courses.add(newCourse);
	    }
	    
	    return courses;
	}
	
	public List<CurrentAssignment> getCurrentAssignments() throws Exception {
		List<CurrentAssignment> assignments = new ArrayList<CurrentAssignment>();
		
		if(portal == null) return assignments;
		
		// select everything in the table holding the assignments
	    Elements assignmentsBlock = portal.body().select("table.table_basic");
	    
		for (Element assignmentRow : assignmentsBlock) {
			
			// create new empty assignment to store course information
			CurrentAssignment assignment = new CurrentAssignment();

			// select the assignment details that include the title, respective
			// course, and due date
			Elements details = assignmentRow.select("td.column.padding_5");

			// if assignments are empty, they are invalid, so skip
			if (details.size() == 0) continue;

			for (int i = 2; i <= 4; i++) {
				Element currAssignment = details.get(i);
				String currAssignmentTxt = currAssignment.text().trim();

				if (i == 2) {
					Elements link = currAssignment.select("a[href]");
					
					if(!(link.size() == 0) && link.size() == 1) {
						String detailsUrl = Utils.getPrintViewifiedUrl(portalUrl + link.first().attr("href"));
						assignment.setDetailsUrl(detailsUrl);
					}
					
					if(assignment.getDetailsUrl() == null && link != null) 
						RemoteDebug.debug("mail details is null", link.outerHtml());
					
					assignment.setName(currAssignmentTxt);
				}

				else if (i == 3) assignment.setCourseName(currAssignmentTxt);
				else if (i == 4) assignment.setDueDate(currAssignmentTxt);
			}

			assignments.add(assignment);
		}
	    
	    return assignments;
	}
	
	public List<NewsArticle> getNews() throws Exception {
		List<NewsArticle> news = new ArrayList<NewsArticle>();
		
		if(portal == null) return news;
		
		Elements newsModules = portal.body().select("div.home_right div.module");
		
		if(newsModules.size() == 0) return news;
		
		// get 1 'module' tables from the last one.
		Element newsBlock = newsModules.get(newsModules.size()-2);
		
		if(newsBlock == null) return news;
		
		// select everything in the div holding the article names
		Elements articles = newsBlock.select("div.content_item");
		
	    for(int i = 0; i < articles.size(); i++) {
		    // create new empty news article to store the article info
		    NewsArticle article = new NewsArticle();
		    Element articleDiv = articles.get(i);
		    
		    // select info according to how it is ordered/structured in the HTML
		    Element title = articleDiv.select("a.title_link").first();
		    String meta = articleDiv.ownText();
		    String articleDateText = null;
		    
		    int idx = 0;
		    while (!Character.isDigit(meta.charAt(idx))) idx++;
		    
	    	articleDateText = meta.substring(idx);
	    	
	    	meta = meta.substring(0, idx);
	    	
		    // split the author field into the author's name and the author's type
		    String authorData[] = null;
		    	
		   	try {
		   		authorData = meta.trim().split(" - ");
		   	} catch(NullPointerException e) {
		   		e.printStackTrace();
		    	authorData = null;
		    }
		   	
		    if(authorData != null) {
			   	for(int j = 0; j < authorData.length; j++) {
			   		if(j == 0) article.setAuthor(authorData[0].trim());
			   		else if(j == 1) article.setAuthorType(authorData[1].trim());
			   	}
		   	} else {
		   		article.setDisplayAuthor(meta.trim());
		   	}
		    
		    article.setArticleName(title.text().trim());
		    article.setDatePosted(articleDateText.trim());
			
			if(title.hasAttr("href")) {
				String detailsUrl = Utils.getPrintViewifiedUrl(portalUrl + title.attr("href"));
				article.setArticleUrl(detailsUrl);
			}
			
			if(article.getArticleUrl() == null && title != null) 
				RemoteDebug.debug("mail details is null", title.outerHtml());
			
		    news.add(article);
	    }
	    
	    return news;
	}
	
	public List<MailEntry> getMailInbox() throws IllegalStateException, IOException, Exception {
		List<MailEntry> mail = new ArrayList<MailEntry>();
		
		if(loopMail == null) return mail;
		
		// retrieve the table listing the emails
		Elements tables = loopMail.select("table.list_padding");
		
		if(tables.size() == 0) return mail;
		
		Element mailTable = tables.first();
		
		// select all mail listing rows after the first one because it is a header row
		Elements mailListing = mailTable.select("tr:gt(0)");
		
		for(Element currListing : mailListing) {
			MailEntry currEntry = new MailEntry();
			Elements mailInfo = currListing.select("td.list_text");
			
			for(int i = 0; i < mailInfo.size(); i++) {
				Element currInfo = mailInfo.get(i);
				String currInfoTxt = currInfo.text().trim();
				
				if(i == 0) currEntry.setTimestamp(currInfoTxt);
				else if(i == 1) currEntry.setInvolvedParties(currInfoTxt);
				else if(i == 2) {
					Elements link = currInfo.select("a[href]");
					
					if(!(link.size() == 0) && link.size() == 1) {
						String mailContentUrl = Utils.getPrintViewifiedUrl(portalUrl + link.first().attr("href"));
						currEntry.setContentUrl(mailContentUrl);
					}
					
					if(currEntry.getContentUrl() == null && link != null) 
						RemoteDebug.debug("mail details is null", link.outerHtml());
					
					currEntry.setSubject(currInfoTxt);
				}
			}
			
			mail.add(currEntry);
		}
		
		return mail;
	}
	
	public List<GradeDetail> getGradeDetails(Course course) throws ClientProtocolException, IOException, Exception {
		List<GradeDetail> detailsList = new ArrayList<GradeDetail>();
		
		// construct and send a GET request to the URL where the Course grade details are stored
		Document detailsPage = Utils.getJsoupDocFromUrl(course.getDetailsUrl(), portalUrl, authCookies);
		
		if(detailsPage == null) {
			RemoteDebug.debug("detailsPage is null", "Points data for grade details is weird");
			return detailsList;
		}
		
		// select all rows of the table containing grade details
	    Elements details = detailsPage.body().select("tbody.general_body tr");
	    
	    if(details == null) RemoteDebug.debug("details is null in getGradeDetails", detailsPage.html());
	    
	    for(Element detail: details) {
		    GradeDetail newDetail = new GradeDetail();
		    
		    // select all individual elements in each row
		    Elements data = detail.select("td");
		    
		    for(int i = 0; i < data.size(); i++) {
			   	if(i == 0) newDetail.setCategory(data.get(0).text().trim());
			   	if(i == 1) newDetail.setDueDate(data.get(1).text().trim());
			   	if(i == 2) newDetail.setDetailName(data.get(2).text().trim());
			   	if(i == 5) newDetail.setComment(data.get(5).text().trim());
			   	if(i == 6) newDetail.setSubmissions(data.get(6).text().trim());
		    }
		    
			if (data.size() >= 5) {
				// should be an entry of the form 'n / m = x%', where n, m, and x are numbers
				String scoreData = data.get(4).text().trim();

				// if the score is a numerical and properly formatted entry, split the grade into total
				// points and earned points
				if (!(scoreData.length() == 0)
					&& Character.isDigit(scoreData.charAt(0))
					&& scoreData.indexOf(" = ") != -1
					&& scoreData.indexOf(" / ") != -1) {
					
					// split into 2 halves: one for percent, and other for raw score
					String[] scoreComps = scoreData.split(" = ");
					
					// store the percent value from the appropriate half
					String displayPct = scoreComps[1].trim();
					
					newDetail.setDisplayPercent(displayPct);

					// split the part before the percent and '=' into respective parts and parse
					String[] scoreParts = scoreComps[0].split(" / ");

					try {
						newDetail.setPointsEarned(Double.parseDouble(scoreParts[0].trim()));
						newDetail.setTotalPoints(Double.parseDouble(scoreParts[1].trim()));
					} catch (NumberFormatException e) {
						RemoteDebug.debugException("Points data for grade details is weird", e);
						// if numbers aren't formatted properly, something is weird, so set to empty/invalid to be safe
						newDetail.setPointsEarned(0.0d);
						newDetail.setTotalPoints(0.0d);
						
						newDetail.setDisplayPercent(Constants.EMPTY_INDIC);
						newDetail.setDisplayScore("");
					}

					String displayScore = scoreComps[0].replaceAll(" ", "");
					newDetail.setDisplayScore(displayScore);
				} else {
					boolean isExtraCredit = false;
					
					try {
						newDetail.setTotalPoints(Double.parseDouble(data.get(4).text().trim()));
					} catch(NumberFormatException e) {
						isExtraCredit = true;
					}
					
					if(isExtraCredit) {
						if(data.get(3).text().trim().length() == 0) newDetail.setPointsEarned(0.0d);
						
						try {
							newDetail.setPointsEarned(Double.parseDouble(data.get(3).text().trim()));
						} catch(NumberFormatException e) {
							e.printStackTrace();
							// if numbers aren't formatted properly, something is weird, so set to empty/invalid to be safe
							newDetail.setPointsEarned(0.0d);
							newDetail.setTotalPoints(0.0d);
						}
					}
					
					newDetail.setDisplayPercent(data.get(4).text().trim());
					newDetail.setDisplayScore(data.get(3).text().trim());
				}
			} else {
				newDetail.setDisplayPercent(Constants.EMPTY_INDIC);
				newDetail.setDisplayScore("");
			}
		    
		    detailsList.add(newDetail);
	    }
	    
	    return detailsList;
	}

	public AssignmentDetail getAssignmentDetails(CurrentAssignment assignment) throws ClientProtocolException, IOException, Exception {
		AssignmentDetail details = new AssignmentDetail();
		
		// construct and send a GET request to the URL where the assignment details are stored
		Document detailsPage = Utils.getJsoupDocFromUrl(assignment.getDetailsUrl(), portalUrl, authCookies);
		
		// select the div containing assignment details
		Elements detailBlock = detailsPage.body().select("div.course");
		
		if(detailBlock == null) RemoteDebug.debug("detailBlock is null in getAssignmentDetails", detailsPage.html());
		
		if(detailBlock.size() == 0) return details;
		
		String assignTitle = detailBlock.select("div.title_page").text().trim();
		String assignAudience = detailBlock.select("div.highlight_box").text().trim();
		String assignExplanation = detailBlock.select("div.content").html();
		String assignAttach = detailBlock.select("div.container").html();
		
		details.setName(assignTitle);
		details.setTargetAudience(assignAudience);
		details.setExplanation(assignExplanation);
		details.setAttachments(assignAttach);
		
		Elements assignDetails = detailBlock.select("td.info");
		
		for(Element detail: assignDetails) {
			details.addDetail(detail.text());
		}
		
		return details;
	}
	
	public NewsDetail getNewsDetails(NewsArticle article) throws IllegalStateException, IOException, Exception {
		NewsDetail details = new NewsDetail();

		// construct and send a GET request to the URL where the news article info is stored
		Document detailsPage = Utils.getJsoupDocFromUrl(article.getArticleUrl(), portalUrl, authCookies);

		// select the block containing news article info
		Elements infoBlock = detailsPage.body().select("div.published");
		
		if(infoBlock == null) RemoteDebug.debug("v is null in getNewsDetails", detailsPage.html());
		
		if(infoBlock.size() == 0) return details;
		
		// select block containing more specific details
		Elements newsDetails = infoBlock.select("div.highlight_box td");
		
		String newsTitle = infoBlock.select("div.title_page").text().trim();
		String newsContent = infoBlock.select("div.content").html();
		
		details.setTitle(newsTitle);
		details.setContent(newsContent);
		
		for(Element detail : newsDetails) {
			details.addDetail(detail.text());
		}
		
		return details;
	}
	
	public MailDetail getMailDetails(MailEntry entry) throws IllegalStateException, IOException, Exception {
		MailDetail details = new MailDetail();

		// construct and send a GET request to the URL where the mail content is stored
		Elements mailBlock = Utils.getJsoupDocFromUrl(entry.getContentUrl(), portalUrl, authCookies).select("div:eq(0) table");
		
		if(mailBlock.size() == 0) return details;
		
		Element infoBlock = mailBlock.get(1);
		Element contentBlock = mailBlock.get(2);
		
		String TO_STRING = "<b>To:</b>";
		String FROM_STRING = "<b>Date:</b>";
		
		String info = infoBlock.select("td:eq(0)").html();
		String from = "", to = "";
		
		if(info.indexOf(TO_STRING) != -1 && info.indexOf(FROM_STRING) != -1) {
			from = info
					.substring(0, info.indexOf(TO_STRING))
					.replaceAll("<br />", "");
			
			to = info
					.substring(info.indexOf(TO_STRING), info.indexOf(FROM_STRING))
					.replaceAll("viewed", "<b>viewed</b>")
					.replaceAll("<br />", "");
			
		} else if(info.indexOf(FROM_STRING) != -1) {
			from = info
					.substring(0, info.indexOf(FROM_STRING))
					.replaceAll("<br />", "");
			to = "";
		} else {
			from = Constants.EMPTY_INDIC;
			to = Constants.EMPTY_INDIC;
		}
			
		String rest = info.substring(info.indexOf(FROM_STRING));
		String content = contentBlock.html();
		
		details.addDetail(to);
		details.addDetail(from);
		details.addDetail(rest);
		details.setContent(content);
		
		return details;
	}

	public Set<GradeCategory> getCourseCategories(Course course) throws IllegalStateException, IOException, Exception {
		Set<GradeCategory> categs = new LinkedHashSet<GradeCategory>();
		
		// construct and send a GET request to the URL where the Course grade categories are stored	
		Document categsPage = Utils.getJsoupDocFromUrl(course.getDetailsUrl(), portalUrl, authCookies);
		
		// select everything in the table holding the categories
	    Elements rows = categsPage.body().select("div > table").last()
	    								.select("td").first()
	    								.select("table").last()
	    								.select("tr:gt(0)");
	    
	    boolean weightsPresent = true;
	    
	    // if 'Weight' column is not there (i.e. < 3 columns), then only 'Category' and 'Score' are present, so no weights present
	    if(rows.size() >= 1) {
	    	if(rows.get(0).select("td").size() < 3) weightsPresent = false;
	    }
	    
	    DecimalFormat percentFormat = new DecimalFormat("#.##%");
	    
	    for(Element data : rows) {
	    	Elements catAndWeight = data.select("td:lt(2)");
	    	
	    	String categName = catAndWeight.get(0).text();
	    	String categWeightStr = null;
	    	
	    	double categWeight = -1.0d;
	    	
	    	if(weightsPresent) {
	    		// if weights present, retrieve them and parse accordingly
	    		categWeightStr = catAndWeight.get(1).text();;
	    		
	    		if(categWeightStr.lastIndexOf('%') != -1) {
	    			//categWeight = Double.parseDouble(categWeightStr.substring(0, categWeightStr.indexOf('%'))) / 100.0d;
	    			try {
						categWeight = percentFormat.parse(categWeightStr).doubleValue();
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
	    		}
	    	} else {
	    		// since no weights present, each category is thus 'weighted' evenly
	    		//categWeight = (100.0d / rows.size());
	    		
	    		// set to UNWEIGHTED constant to mark as unweighted category
	    		categWeight = GradeCategory.UNWEIGHTED;
	    	}
	    	
	    	//System.out.println(categName + " => " + categWeight);
	    	
	    	categs.add(new GradeCategory(categName, categWeight));
	    }
	    
	    return categs;
	}
}
