package com.cyanojay.looped.portal;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.cyanojay.looped.API;
import com.cyanojay.looped.R;

public class GradesActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);
        
        ScrapeGradesTask task = new ScrapeGradesTask();
        task.execute();
    }

    private class ScrapeGradesTask extends AsyncTask<String, Void, List<Course>> {
		@Override
		protected List<Course> doInBackground(String... params) {
	        List<Course> courses = API.get().getCourses();
	        
			return courses;
		}
		
		@Override
	    protected void onPostExecute(List<Course> result) {
	        super.onPostExecute(result);
	        TableLayout table = (TableLayout) findViewById(R.id.grades_table);
	        
	        TableRow headers = new TableRow(GradesActivity.this);
	        
	        TextView subj_head = new TextView(GradesActivity.this);
	        TextView lett_head = new TextView(GradesActivity.this);
	        TextView pct_head = new TextView(GradesActivity.this);
	        TextView zero_head = new TextView(GradesActivity.this);
	        
	        subj_head.setTextSize(20.0f);
	        subj_head.setText("  Course Name  \n");
	        lett_head.setTextSize(20.0f);
	        lett_head.setText("  Letter Grade  \n");
	        pct_head.setTextSize(20.0f);
	        pct_head.setText("  % Grade  \n");
	        zero_head.setTextSize(20.0f);
	        zero_head.setText("  # of Zeroes  \n");
	        
	        headers.addView(subj_head);
	        headers.addView(lett_head);
	        headers.addView(pct_head);
	        headers.addView(zero_head);
	        
	        table.addView(headers, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	        
	        for(Course currCourse: result) {
		        TableRow row = new TableRow(GradesActivity.this);
		         
		        TextView subject = new TextView(GradesActivity.this);
		        TextView lettGrade = new TextView(GradesActivity.this);
		        TextView pctGrade = new TextView(GradesActivity.this);
		        TextView numZero = new TextView(GradesActivity.this);
		        
		        subject.setTextSize(18.0f);
		        lettGrade.setTextSize(18.0f);
		        pctGrade.setTextSize(18.0f);
		        numZero.setTextSize(18.0f);
		        
		        subject.setText("  " + currCourse.getName() + "  ");
		        lettGrade.setText("  " + currCourse.getLetterGrade() + "  ");
		        pctGrade.setText("  " + currCourse.getPercentGrade() + "  ");
		        numZero.setText("  " + currCourse.getNumZeros() + "  ");

		        row.addView(subject);
		        row.addView(lettGrade);
		        row.addView(pctGrade);
		        row.addView(numZero);
		        
		        table.addView(row, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		}
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_grades, menu);
        return true;
    }
}
