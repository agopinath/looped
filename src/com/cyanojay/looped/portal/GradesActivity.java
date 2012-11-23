package com.cyanojay.looped.portal;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyanojay.looped.API;
import com.cyanojay.looped.R;

public class GradesActivity extends ListActivity {

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
			return API.get().getCourses();
		}
		
		@Override
	    protected void onPostExecute(List<Course> result) {
	        super.onPostExecute(result);
	        
	        Course[] values = result.toArray(new Course[0]);
	        GradesAdapter adapter = new GradesAdapter(GradesActivity.this, values);
	        
	        GradesActivity.this.setListAdapter(adapter);
		}
    };
    
    private class GradesAdapter extends ArrayAdapter<Course> {
    	  private final Context context;
    	  private final Course[] values;
    	  
    	  public GradesAdapter(Context context, Course[] values) {
    		  super(context, R.layout.curr_grades_row, values);
    		  this.context = context;
    		  this.values = values;
    	  }

    	  @Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    		  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		  View rowView = inflater.inflate(R.layout.curr_grades_row, parent, false);
    		  
    		  TextView courseName = (TextView) rowView.findViewById(R.id.grades_course_name);
    		  TextView lettGrade = (TextView) rowView.findViewById(R.id.grades_lett_grade);
    		  TextView pctGrade = (TextView) rowView.findViewById(R.id.grades_pct_grade);
    		  
    		  Course course = values[position];
    		  
    		  courseName.setText(course.getName());
    		  lettGrade.setText(course.getLetterGrade());
    		  
    		  if(course.getPercentGrade().length() > 0) {
	    		  String gradeHighlight = "";
	    		  char tensPlace = course.getPercentGrade().charAt(0);
	    				  
	    		  System.out.println('6');
	    		  if(tensPlace == '9' || tensPlace == '1') {
	    			  gradeHighlight = "#00B300";
	    		  } else if(tensPlace <= '8') { 
	    			  gradeHighlight = "#3333FF";
	    		  } else if(tensPlace <= '7') {
	    			  gradeHighlight = "#D1D100";
	    		  } else if(tensPlace <= '6') {
	    			  gradeHighlight = "#FFB366";
	    		  } else {
	    			  gradeHighlight = "#CC0000";
	    		  }
	    		  
	    		  pctGrade.setText(Html.fromHtml("<font color=\"" + gradeHighlight + "\">" + course.getPercentGrade() + "</font>"));
    		  } else {
    			  pctGrade.setText(course.getPercentGrade());
    		  }
    		  
    		  if(course.getNumZeros() >= 1) {
    			  TextView numZeros = (TextView) rowView.findViewById(R.id.grades_num_zeros);
    			  numZeros.setText(course.getNumZeros() + " missing assignment(s)");
    		  }
    		  
    		  return rowView;
    	} 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_grades, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
    	Course selectedCourse = (Course) getListAdapter().getItem(position);
    	Toast.makeText(this, selectedCourse.getName() + " selected", Toast.LENGTH_SHORT).show();
    	
    	Intent detailsIntent = new Intent(this, GradeDetailsActivity.class);
    	detailsIntent.putExtra("COURSE_SELECTED", selectedCourse);
    	
    	this.startActivity(detailsIntent);
    }
}
