package com.cyanojay.looped.graph;

import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

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
		setContentView(R.layout.activity_looped_graph);
		
		courseGradesChart = (TimeChart) getIntent().getSerializableExtra(GRAPH_CHART);
		gradeDetails = (List<GradeDetail>) getIntent().getSerializableExtra(GRADE_DETAILS);
		
		String title = (String) getIntent().getStringExtra(GRAPH_TITLE);
		
		//mChartView = new GraphicalView(this, courseGradesChart);
		
		setTitle(title);
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
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			
			mChartView = new GraphicalView(this, courseGradesChart);
			XYMultipleSeriesRenderer renderer = courseGradesChart.getRenderer();
			
			renderer.setClickEnabled(true);
			renderer.setSelectableBuffer(30);
			
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
					
					XYMultipleSeriesDataset data = courseGradesChart.getDataset();
					
					double[] xy = mChartView.toRealPoint(0);
					
					if (seriesSelection == null) {
						Toast.makeText(LoopedGraphActivity.this, "No chart element was clicked", Toast.LENGTH_SHORT).show();
					} else {
						int selIdx = seriesSelection.getSeriesIndex();
						int pointIdx = seriesSelection.getPointIndex();
						
						if(pointIdx < 0 || pointIdx >= gradeDetails.size()) return;
						
						if(selIdx != data.getSeriesCount() - 1) return;
						
						GradeDetail selectedDetail = gradeDetails.get(pointIdx);
						
						Toast.makeText(LoopedGraphActivity.this, 
								selectedDetail.getDetailName() + ": " + 
								selectedDetail.getDisplayPercent() + " ~ " +
								selectedDetail.getDueDate(), Toast.LENGTH_LONG).show();
						
						/*Toast.makeText(
								LoopedGraphActivity.this,
								"Chart element in series index " + seriesSelection.getSeriesIndex()
								+ " data point index " + seriesSelection.getPointIndex() + " was clicked"
								+ " closest point value X=" + seriesSelection.getXValue() + ", Y=" + seriesSelection.getValue()
								+ " clicked point value X=" + (float) xy[0] + ", Y=" + (float) xy[1], Toast.LENGTH_SHORT).show();*/
					}
				}
			});
			
			layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			mChartView.repaint();
		}
	}

}
