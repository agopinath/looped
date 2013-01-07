package com.cyanojay.looped.graph;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalActivity;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.util.MathHelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.cyanojay.looped.Constants;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.debug.RemoteDebug;
import com.cyanojay.looped.net.API;
import com.cyanojay.looped.portal.grades.Course;
import com.cyanojay.looped.portal.grades.GradeCategory;
import com.cyanojay.looped.portal.grades.GradeDetail;

public class CourseGraphTask extends AsyncTask<CourseGraphTask.GraphTaskType, Void, XYMultipleSeriesDataset> {
	private Context parent;
	private Course course;
	private ProgressDialog progressDialog;
	
	private GraphTaskType taskType;
	private Set<GraphTaskWarningType> warnings;
	private List<GradeDetail> details;
	
	public enum GraphTaskType {
		ASSIGNMENTS, COURSE
	}
	
	public enum GraphTaskWarningType {
		INSUFFICIENT_DATA, EC_CATEGORY_PRESENT
	}
	
	public CourseGraphTask(Context parent, Course course) {
		this.parent = parent;
		this.course = course;
		
		this.warnings = new HashSet<CourseGraphTask.GraphTaskWarningType>();
	}
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        
        progressDialog = ProgressDialog.show(parent, "Looped", "Building graph...");
    }

    @Override
    protected XYMultipleSeriesDataset doInBackground(GraphTaskType... args) {
    	this.taskType = args[0];
    	
    	List<TimeSeries> categSeries = null;
    	Set<GradeCategory> categWeights = null;
    	
    	XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
    	
    	try {
			details = API.get().getGradeDetails(course);
			categWeights = API.get().getCourseCategories(course);
		} catch (Exception e) {
			RemoteDebug.debugException(e);
		}
    	
    	if(details == null || categWeights == null) return null;
    	
    	categSeries = new ArrayList<TimeSeries>(categWeights.size());
    	
    	for(GradeCategory categName : categWeights) {
    		categSeries.add(new TimeSeries(categName.getName()));
    	}
    	
    	switch(taskType) {
	    	case ASSIGNMENTS:
	    		fillAssignmentsGraph(categSeries, details, data);
	    		break;
	    	case COURSE:
	    		fillCourseGradeGraph(categSeries, details, categWeights, data);
	    		break;
    	}
    	
		return data;
    }

    @Override
    protected void onPostExecute(XYMultipleSeriesDataset graphData) {
        super.onPostExecute(graphData);
        
        Utils.safelyDismissDialog(progressDialog);
        
        Intent chartIntent = null;
        
        if(graphData == null) {
        	Toast.makeText(parent, "Failed to create graph. Check your network connection and try again", Toast.LENGTH_LONG).show();
        	return;
        }
        
        if(taskType == GraphTaskType.ASSIGNMENTS) {
        	chartIntent = getCustomDatedScatterChart(
	        		parent, 
	        		graphData, 
	        		ChartUtil.getMultiSeriesRenderer(graphData.getSeriesCount(), false, null, null), 
	        		"MMM dd, yy", 
	        		"Graph for " + course.getName());
    	} else if(taskType == GraphTaskType.COURSE) {
    		Date min = null;
    		Date max = null;
    		
    		for(GradeDetail detail : details) {
    			Date curr = parseDate(detail.getDueDate(), Constants.LOOPED_DATE_FORMAT);
    			
    			if(min == null || curr.before(min)) 
    				min = curr;
    			
    			if(max == null || curr.after(max))
    				max = curr;
    		}
    		
    		XYMultipleSeriesRenderer renderer = ChartUtil.getMultiSeriesRenderer(graphData.getSeriesCount(), true, min, max);
    		
    		chartIntent = new Intent(parent, LoopedGraphActivity.class);
    		
    		chartIntent.putExtra(LoopedGraphActivity.GRAPH_TITLE, "Graph for " + course.getName());
    		
    		Bundle extras = new Bundle();
    		
    		TimeChart chart = new TimeChart(graphData, renderer);
    		chart.setDateFormat("MMM dd, yy");
    		
    		extras.putSerializable(LoopedGraphActivity.GRAPH_CHART, chart);
    		extras.putSerializable(LoopedGraphActivity.GRADE_DETAILS, (Serializable) details);
    		
    		chartIntent.putExtras(extras);
    	}
        
        handleWarnings();
        
        parent.startActivity(chartIntent);
    }
    
	private void handleWarnings() {
		for(GraphTaskWarningType warning : warnings) {
			switch(warning) {
			case EC_CATEGORY_PRESENT:
				Toast.makeText(parent, "Warning: an 'Extra Credit' category may be present, and is " +
										"not considered for the 'Overall grade'", Toast.LENGTH_LONG).show();
				break;
			case INSUFFICIENT_DATA:
				Toast.makeText(parent, "Warning: insufficient (less than 3) data points when making graph", Toast.LENGTH_LONG).show();
			}
		}
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
	
	private void fillAssignmentsGraph(List<TimeSeries> categSeries, List<GradeDetail> details, XYMultipleSeriesDataset dataToFill) {
		for(TimeSeries series : categSeries) {
    		
	    	for(GradeDetail detail : details) {
	    		Date gradeDate = parseDate(detail.getDueDate(), Constants.LOOPED_DATE_FORMAT);
	    		if(gradeDate == null) continue;
	    				
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
		    		
		    		//System.out.println(detail.getDueDate() + " --> " + percent);
		    		
		    		series.add(gradeDate, percent);
	    		}
	    	}
    	
	    	dataToFill.addSeries(series);
    	}
	}
	
	private void fillCourseGradeGraph(List<TimeSeries> categSeries, List<GradeDetail> details, 
									Set<GradeCategory> categWeights, XYMultipleSeriesDataset dataToFill) {
		
		for(TimeSeries series : categSeries) {
			TimeSeries categoryGradeSeries = getCourseGradeCategorySeries(series, details);
			dataToFill.addSeries(categoryGradeSeries);
		}
		
		if(categWeights.size() == 1 && categSeries.get(0).getItemCount() < 3) 
			warnings.add(GraphTaskWarningType.INSUFFICIENT_DATA);
		
		if(categWeights.size() > 1) {
			TimeSeries courseGradeSeries = getOverallCourseGradeSeries(categSeries, details, categWeights);
			dataToFill.addSeries(courseGradeSeries);
		}
	}
	
	private TimeSeries getCourseGradeCategorySeries(TimeSeries series, List<GradeDetail> details) {
		double grade = 0.0d;
		double total = 0.0d;
		double earned = 0.0d;

		for(GradeDetail detail : details) {
			Date gradeDate = parseDate(detail.getDueDate(), Constants.LOOPED_DATE_FORMAT);
			
			if(gradeDate == null) continue;
			
			if(detail.getCategory().equalsIgnoreCase(series.getTitle())) {
				earned += detail.getPointsEarned();
				total += detail.getTotalPoints();

				try {
					grade = (earned / total) * 100.0d;
				} catch(ArithmeticException e) {
					grade = MathHelper.NULL_VALUE;
					e.printStackTrace();
				}

				if(Double.isInfinite(grade) || Double.isNaN(grade))
					grade = 100.0d;

				//System.out.println(detail.getDetailName() + " --> " + grade);

				series.add(gradeDate, grade);
			}
		}
		
		return series;
	}
	
	private TimeSeries getOverallCourseGradeSeries(List<TimeSeries> categSeries, List<GradeDetail> details, Set<GradeCategory> categWeights) {
		
		TimeSeries courseGradeSeries = new TimeSeries("Overall grade");
		Set<GradeCategory> movingWeights = categWeights;
		
		boolean isUnweighted = true;
		
		for(GradeCategory currCateg : movingWeights) {
			if(currCateg.getWeight() != GradeCategory.UNWEIGHTED) isUnweighted = false;
		}
		
		//System.out.println("UNWEIGHTED? " + isUnweighted);
		
		if(!isUnweighted) {
			for(GradeDetail detail : details) {
	    		Date gradeDate = parseDate(detail.getDueDate(), Constants.LOOPED_DATE_FORMAT);
	    		
	    		if(gradeDate == null) continue;
	    		
	    		GradeCategory categ = getCategoryByName(detail.getCategory(), movingWeights);
	    		double overallGrade = 0.0d;
	
	    		categ.incrementAssignmentCount(1);
	
	    		scaleCourseGradeWeights(categWeights);
	    		
	    		for(GradeCategory currCateg : movingWeights) {
	    			if(currCateg.getAssignmentCount() == 0) continue;
	    			if(currCateg.getWeight() == 0.0d) warnings.add(GraphTaskWarningType.EC_CATEGORY_PRESENT);
	    			
	    			double categGrade = 0.0d;
		    		double total = 0.0d;
		    		double earned = 0.0d;
		    		
			    	for(GradeDetail currDetail : details) {
			    		if(currDetail.getCategory().equalsIgnoreCase(currCateg.getName())) {
			    			earned += currDetail.getPointsEarned();
			    			total += currDetail.getTotalPoints();
			    		}
			    		
			    		if(detail.getDetailName().equals(currDetail.getDetailName()) &&
			    			detail.getDueDate().equals(currDetail.getDueDate())) 
					    		break;
			    	}
	    			
			    	try {
		    			categGrade = (earned / total) * 100.0d;
		    		} catch(ArithmeticException e) {
		    			categGrade = MathHelper.NULL_VALUE;
		    			e.printStackTrace();
		    		}
		    		
		    		if(Double.isInfinite(categGrade) || Double.isNaN(categGrade))
		    			categGrade = 100.0d;
		    			
	    			overallGrade += currCateg.getScaledWeight() * categGrade;
	    		}
	    		
	    		/*//System.out.println("============================");
	    		//System.out.println("Category: " + categ.getName());
	    		//System.out.println("Weight Sum: " + weightSum);
	    		//System.out.println("Scaled Weight: " + categ.getScaledWeight());
	    		//System.out.println("Assign. Count: " + categ.getAssignmentCount());
	    		//System.out.println("Overall Grade: " + overallGrade);
	    		//System.out.println("============================");*/
	    		
	    		courseGradeSeries.add(gradeDate, overallGrade);
	    	}
		} else {
			fillOverallUnweightedCourseGradeSeries(categSeries, details, categWeights, courseGradeSeries);
		}
		
		return courseGradeSeries;
	}
	
	private void fillOverallUnweightedCourseGradeSeries(List<TimeSeries> categSeries, List<GradeDetail> details, 
													Set<GradeCategory> categWeights, TimeSeries courseGradeSeries) {
		
		double overallGrade = 0.0d;
		
		for(GradeDetail detail : details) {
    		Date gradeDate = parseDate(detail.getDueDate(), Constants.LOOPED_DATE_FORMAT);
    		
    		if(gradeDate == null) continue;
    		
    		double overallEarned = 0.0d;
    		double overallTotal = 0.0d;
    		
    		for(GradeDetail currDetail : details) {
    			overallEarned += currDetail.getPointsEarned();
    			overallTotal += currDetail.getTotalPoints();
	    		
	    		if(detail.getDetailName().equals(currDetail.getDetailName()) &&
		    		detail.getDueDate().equals(currDetail.getDueDate())) 
				    	break;
    		}
    		
	    	try {
	    		overallGrade = (overallEarned / overallTotal) * 100.0d;
    		} catch(ArithmeticException e) {
    			overallGrade = MathHelper.NULL_VALUE;
    			e.printStackTrace();
    		}
    		
    		if(Double.isInfinite(overallGrade) || Double.isNaN(overallGrade))
    			overallGrade = 100.0d;
    		
    		courseGradeSeries.add(gradeDate, overallGrade);
    	}
	}
	
	private void scaleCourseGradeWeights(Set<GradeCategory> categWeights) {
		double weightSum = 0.0d;
		
		for(GradeCategory currCateg : categWeights) {
			if(currCateg.getAssignmentCount() > 0) weightSum += currCateg.getWeight();
		}
    		
		for(GradeCategory currCateg : categWeights) {
    		if(currCateg.getAssignmentCount() > 0) currCateg.setScaledWeight(currCateg.getWeight()/weightSum);
    		else currCateg.setScaledWeight(0.0d);
    	}
	}
	
	private Date parseDate(String dateStr, SimpleDateFormat format) {
		Date date = null;
		
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	private GradeCategory getCategoryByName(String name, Set<GradeCategory> categs) {
		for(GradeCategory categ : categs) {
			if(categ.getName().equals(name)) return categ;
		}
		
		return null;
	}
}
