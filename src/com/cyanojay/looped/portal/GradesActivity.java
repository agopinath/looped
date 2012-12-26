package com.cyanojay.looped.portal;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyanojay.looped.Constants;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;

public class GradesActivity extends ListActivity {
	public static final String COURSE_SELECTED = "COURSE_SELECTED";

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
	        
	        Course[] values = result.toArray(new Course[result.size()]);
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
    		  
    		  if(!(course.getLetterGrade().length() == 0))
    			  lettGrade.setText(course.getLetterGrade());
    		  else
    			  lettGrade.setText(Constants.EMPTY_INDIC);
    		  
    		  if(!(course.getPercentGrade().length() == 0)) {
    			  char tensPlace = course.getPercentGrade().charAt(0);
    			  
    			  if(Character.isDigit(tensPlace)) {
		    		  String gradeHighlight = "";
  
		    		  if(tensPlace == '9' || tensPlace == '1') {
		    			  gradeHighlight = "#009900";
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
    				  pctGrade.setText(Html.fromHtml("<font color=\"#000000\">" + course.getPercentGrade() + "</font>"));
    			  }
    		  } else {
    			  pctGrade.setText("             ");
    		  }
    		  
    		  if(course.getNumZeros() >= 1) {
    			  TextView numZeros = (TextView) rowView.findViewById(R.id.grades_num_zeros);
    			  numZeros.setText(course.getNumZeros() + " missing assignment(s)");
    		  }
    		  
    		  rowView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						System.out.println("on long click received");
						
						AlertDialog dialog = getOptionsDialog();
						dialog.show();
						
						return false;
					}
		      });
    		  
			  rowView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						System.out.println("on long click received");
						
						AlertDialog dialog = getOptionsDialog();
						dialog.show();
						
						return false;
					}
		      });
    		  
    		  return rowView;
    	} 
    }
    
    public AlertDialog getOptionsDialog() {
    	String[] options = new String[] { "Graph" };
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		
        return builder.create();
    }
    
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
    	if(!Utils.isOnline(this)) {
    		Toast.makeText(this, "Internet connectivity is lost. Please re-connect and try again.", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	Course selectedCourse = (Course) getListAdapter().getItem(position);
    	
    	if(selectedCourse.getDetailsUrl().length() == 0) {
    		Toast.makeText(this, "Progress report for course is unpublished/unavailable.", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	Intent detailsIntent = new Intent(this, GradeDetailsActivity.class);
    	detailsIntent.putExtra(COURSE_SELECTED, selectedCourse);
    	
    	this.startActivity(detailsIntent);
    }
}
