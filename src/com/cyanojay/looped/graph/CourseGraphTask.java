package com.cyanojay.looped.graph;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalActivity;
import org.achartengine.chart.ScatterChart;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.util.MathHelper;
import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.cyanojay.looped.net.API;
import com.cyanojay.looped.portal.grades.Course;
import com.cyanojay.looped.portal.grades.GradeDetail;

public class CourseGraphTask extends AsyncTask<CourseGraphTask.GraphTaskType, Void, List> {
	private Context parent;
	private Course course;
	private ProgressDialog progressDialog;
	
	public enum GraphTaskType {
		ASSIGNMENTS, COURSE
	}
	
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
    protected List doInBackground(GraphTaskType... args) {
    	List<GradeDetail> details = null;
    	List<TimeSeries> categSeries = null;
    	Map<String, Double> categWeights = null;
    	
    	XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
    	SimpleDateFormat gradeDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
    	
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
    	
    	List results = new ArrayList();
    	
    	if(args[0] == GraphTaskType.ASSIGNMENTS) {
	    	for(TimeSeries series : categSeries) {
	    		
		    	for(GradeDetail detail : details) {
		    		Date gradeDate = null;
	
		    		try {
		    			gradeDate = gradeDateFormat.parse(detail.getDueDate());
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
		    		
		    		if(detail.getCategory().equalsIgnoreCase(series.getTitle())) {
		    			double percent = 0.0d;
			    		
			    		try {
			    			percent = (detail.getPointsEarned() / detail.getTotalPoints()) * 100.0d;
			    		} catch(ArithmeticException e) {
			    			percent = MathHelper.NULL_VALUE;
			    			e.printStackTrace();
			    		}
			    		
			    		if(Double.isInfinite(percent) || Double.isNaN(percent))
			    			percent = 100.0d;
			    		
			    		System.out.println(detail.getCategory() + " --> " + percent);
			    		
			    		series.add(gradeDate, percent);
		    		}
		    	}
	    	
		    	data.addSeries(series);
	    	}
	    	
	    	results.add(GraphTaskType.ASSIGNMENTS);
	    	
    	} else if(args[0] == GraphTaskType.COURSE) {
    		/*TimeSeries courseGradeSeries = new TimeSeries("Overall grade");
    		
    		double overallGrade = 0.0d;
    		double overallTotal = 0.0d;
    		double overallEarned = 0.0d;*/
    		
    		for(TimeSeries series : categSeries) {
	    		double grade = 0.0d;
	    		double total = 0.0d;
	    		double earned = 0.0d;
	    		
		    	for(GradeDetail detail : details) {
		    		Date gradeDate = null;
		    		double weight = categWeights.get(detail.getCategory()) / 100.0d;
		    				
		    		try {
		    			gradeDate = gradeDateFormat.parse(detail.getDueDate());
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
		    		
		    		if(detail.getCategory().equalsIgnoreCase(series.getTitle())) {
		    			earned += detail.getPointsEarned();
		    			total += detail.getTotalPoints();
	
			    		try {
			    			grade = (earned / total) * 100.0d;
			    		} catch(ArithmeticException e) {
			    			grade = MathHelper.NULL_VALUE;
			    			e.printStackTrace();
			    		}
			    		
			    		if(Double.isInfinite(grade) || Double.isNaN(grade)) {
			    			grade = 100.0d;
			    		}
			    		
			    		System.out.println(detail.getDetailName() + " --> " + grade);
			    		
			    		series.add(gradeDate, grade);
		    		}
		    	}
		    	
		    	data.addSeries(series);
	    	}
    		
    		//data.addSeries(courseGradeSeries);
    		
    		results.add(GraphTaskType.COURSE);
    	}

    	results.add(data);
    	
		return results;
    }

    @Override
    protected void onPostExecute(List data) {
        super.onPostExecute(data);
        
        progressDialog.dismiss();
        
        GraphTaskType taskType = (GraphTaskType) data.get(0);
        XYMultipleSeriesDataset graphData = (XYMultipleSeriesDataset) data.get(1);
        
        Intent chartIntent = null;
        
        if(taskType == GraphTaskType.ASSIGNMENTS) {
        	chartIntent = getCustomDatedScatterChart(
	        		parent, 
	        		graphData, 
	        		ChartUtil.getMultiSeriesRenderer(graphData.getSeriesCount()), 
	        		"MMM dd", 
	        		"Graph for " + course.getName());
    	} else if(taskType == GraphTaskType.COURSE) {
    		chartIntent = ChartFactory.getTimeChartIntent(
	        		parent, 
	        		graphData, 
	        		ChartUtil.getMultiSeriesRenderer(graphData.getSeriesCount()), 
	        		"MMM dd", 
	        		"Graph for " + course.getName());
    	}
        
        parent.startActivity(chartIntent);
    }
    
	private final Intent getCustomDatedScatterChart(Context context,
			XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer,
			String format, String activityTitle) {
		
		checkParameters(dataset, renderer);
		
		Intent intent = new Intent(context, GraphicalActivity.class);
		DatedScatterChart chart = new DatedScatterChart(dataset, renderer);
		chart.setDateFormat(format);
		
		intent.putExtra(ChartFactory.CHART, chart);
		intent.putExtra(ChartFactory.TITLE, activityTitle);
		
		return intent;
	}
	
	private void checkParameters(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
		if (dataset == null || renderer == null || dataset.getSeriesCount() != renderer.getSeriesRendererCount()) {
			throw new IllegalArgumentException(
					"Dataset and renderer should be not null and should have the same number of series");
		}
	}
}
