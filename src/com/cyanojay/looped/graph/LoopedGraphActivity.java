package com.cyanojay.looped.graph;

import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.TimeChart;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.portal.grades.GradeDetail;



public class LoopedGraphActivity extends SherlockActivity {
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
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(title);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_portal, menu);
		
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
        switch (item.getItemId()) {
	        case R.id.menu_about:
	            Utils.showAbout(this);
	            return true;
	        case R.id.menu_logout:
	            Utils.logOut(this);
	            return true;
	        case android.R.id.home:
	            this.onBackPressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
	
	public boolean onPrepareOptionsMenu(Menu menu) {
	    menu.removeItem(R.id.menu_sort);
	    menu.removeItem(R.id.menu_refresh);
	    
	    return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			
			mChartView = new GraphicalView(this, courseGradesChart);
			
			/*final XYMultipleSeriesRenderer renderer = courseGradesChart.getRenderer();
			
			renderer.setClickEnabled(true);
			mChartView.setClickable(true);
			renderer.setSelectableBuffer(30);
			
			mChartView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction() != MotionEvent.ACTION_UP) return false;
					
					System.out.println(event.getX() + " " + event.getY());
					
					SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
					
					XYMultipleSeriesDataset data = courseGradesChart.getDataset();
					
					double[] xy = mChartView.toRealPoint(0);//data.getSeriesAt(data.getSeriesCount() - 1).getScaleNumber());
					System.out.println(xy[0] + " " + xy[1]);
					
					if (seriesSelection != null) {
						
						int selIdx = seriesSelection.getSeriesIndex();
						int pointIdx = seriesSelection.getPointIndex();
						
						if(pointIdx < 0 || pointIdx >= gradeDetails.size()) return false;
						
						if(selIdx != data.getSeriesCount() - 1) return false;
						
						GradeDetail selectedDetail = gradeDetails.get(pointIdx);
						
						Toast.makeText(LoopedGraphActivity.this, 
								selectedDetail.getDetailName() + ": " + 
								selectedDetail.getDisplayPercent() + " ~ " +
								selectedDetail.getDueDate(), Toast.LENGTH_LONG).show();
						
						Toast.makeText(
								LoopedGraphActivity.this,
								"Chart element in series index " + seriesSelection.getSeriesIndex()
								+ " data point index " + seriesSelection.getPointIndex() + " was clicked"
								+ " closest point value X=" + seriesSelection.getXValue() + ", Y=" + seriesSelection.getValue()
								+ " clicked point value X=" + (float) xy[0] + ", Y=" + (float) xy[1], Toast.LENGTH_SHORT).show(); 
					}
					
					return false;
				}
			});*/
			
			layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			mChartView.repaint();
		}
	}

}
