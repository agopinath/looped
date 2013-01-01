package com.cyanojay.looped.graph;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.cyanojay.looped.R;
import com.cyanojay.looped.portal.grades.GradeDetail;

public class LoopedGraphActivity extends Activity {
	public static final GraphicalView GRAPH_VIEW = null;
	
	private List<GradeDetail> gradeDetails;
	private GraphicalView mChartView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_looped_graph);
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
			LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_looped_graph, null);
			
			layout.addView(mChartView, new LayoutParams
					(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}

}
