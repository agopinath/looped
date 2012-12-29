package com.cyanojay.looped.graph;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.util.MathHelper;
import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.cyanojay.looped.net.API;
import com.cyanojay.looped.portal.grades.Course;
import com.cyanojay.looped.portal.grades.GradeDetail;

public class CourseGraphTask extends AsyncTask<Void, Void, XYMultipleSeriesDataset> {
	private Context parent;
	private Course course;
	private ProgressDialog progressDialog;
	
	public CourseGraphTask(Context parent, Course course) {
		this.parent = parent;
		this.course = course;
	}
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        
        progressDialog = ProgressDialog.show(parent, "Looped", "Building graph...");
    }

    @Override
    protected XYMultipleSeriesDataset doInBackground(Void... args) {
    	List<GradeDetail> details = null;
    	List<TimeSeries> categSeries = null;
    	Map<String, Double> categWeights = null;
    	
    	XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
    	
    	try {
			details = API.get().getGradeDetails(course);
			categWeights = API.get().getCourseCategories(course);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	if(details == null || categWeights == null) return null;
    	
    	categSeries = new ArrayList<TimeSeries>(categWeights.size());
    	
    	for(String categName : categWeights.keySet()) {
    		categSeries.add(new TimeSeries(categName));
    	}
    	
    	//TimeSeries dataSeries = new TimeSeries("Grade Percentages Over Time");
    	SimpleDateFormat gradeDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
    	
    	for(TimeSeries series : categSeries) {
    		
	    	for(GradeDetail detail : details) {
	    		Date gradeDate = null;

	    		try {
	    			gradeDate = gradeDateFormat.parse(detail.getDueDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
	    		
	    		if(detail.getCategory().equalsIgnoreCase(series.getTitle())) {
	    			double percent = 0.0d;
		    		
		    		try {
		    			percent = (detail.getPointsEarned() / detail.getTotalPoints()) * 100.0d;
		    		} catch(ArithmeticException e) {
		    			percent = MathHelper.NULL_VALUE;
		    			e.printStackTrace();
		    		}
		    		
		    		System.out.println(detail.getCategory() + " --> " + percent);
		    		
		    		series.add(gradeDate, percent);
	    		}
	    	}
    	
	    	data.addSeries(series);
    	}
    	
		return data;
    }

    @Override
    protected void onPostExecute(XYMultipleSeriesDataset graphData) {
        super.onPostExecute(graphData);
        
        progressDialog.dismiss();
        
        String[] titles = new String[] { "Grade" };
        //Intent intent = ChartFactory.getTimeChartIntent(parent, graphData, ChartUtil.getDemoRenderer(graphData.getSeriesCount()),
        //												null, "Graph for " + course.getName());
        Intent intent = ChartFactory.getScatterChartIntent(parent, graphData, 
        		ChartUtil.getDemoRenderer(graphData.getSeriesCount()), "Graph for " + course.getName());
        
        parent.startActivity(intent);
    }
}
