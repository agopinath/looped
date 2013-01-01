package com.cyanojay.looped.graph;

import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.TimeChart;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.cyanojay.looped.R;
import com.cyanojay.looped.portal.grades.GradeDetail;

public class LoopedGraphActivity extends Activity {
	public static final String GRAPH_CHART = "GRAPH_CHART";
	public static final String GRADE_DETAILS = "GRADE_DETAILS";
	public static final String GRAPH_TITLE = "GRAPH_TITLE";
	
	private List<GradeDetail> gradeDetails;
	private GraphicalView mChartView;
	private TimeChart courseGradesChart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		courseGradesChart = (TimeChart) getIntent().getSerializableExtra(GRAPH_CHART);
		gradeDetails = (List<GradeDetail>) getIntent().getSerializableExtra(GRADE_DETAILS);
		
		String title = (String) getIntent().getStringExtra(GRAPH_TITLE);
		
		mChartView = new GraphicalView(this, courseGradesChart);
		
		setTitle(title);
		
		setContentView(mChartView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_looped_graph, menu);
		
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (mChartView == null) {
			mChartView = new GraphicalView(this, courseGradesChart);
			setContentView(mChartView);
		} else {
			mChartView.repaint();
		}
	}

}
