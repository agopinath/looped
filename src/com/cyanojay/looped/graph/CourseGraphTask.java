package com.cyanojay.looped.graph;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.MathHelper;
import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;

import com.cyanojay.looped.net.API;
import com.cyanojay.looped.portal.Course;
import com.cyanojay.looped.portal.GradeDetail;

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
    	XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
    	
    	try {
			details = API.get().getGradeDetails(course);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	if(details == null) return null;
    	
    	
    	TimeSeries dataSeries = new TimeSeries("Grade Percentages Over Time");
    	SimpleDateFormat gradeDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
    	
    	for(GradeDetail detail : details) {
    		Date gradeDate = null;
    		double percent = 0.0d;
    		
    		try {
    			gradeDate = gradeDateFormat.parse(detail.getDueDate());
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		
    		try {
    			percent = (detail.getPointsEarned() / detail.getTotalPoints()) * 100.0d;
    		} catch(ArithmeticException e) {
    			percent = MathHelper.NULL_VALUE;
    			e.printStackTrace();
    		}
    		
    		System.out.println(gradeDate + " --> " + percent);
    		
    		dataSeries.add(gradeDate, percent);
    	}
    	
    	data.addSeries(dataSeries);
    	
		return data;
    }

    @Override
    protected void onPostExecute(XYMultipleSeriesDataset graphData) {
        super.onPostExecute(graphData);
        
        progressDialog.dismiss();
        
        String[] titles = new String[] { "Grade" };
        Intent intent = ChartFactory.getTimeChartIntent(parent, graphData, ChartUtil.getDemoRenderer(),
        												"Graph for " + course.getName(), null);

        parent.startActivity(intent);
    }
}
