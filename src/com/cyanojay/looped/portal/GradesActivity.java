package com.cyanojay.looped.portal;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.cyanojay.looped.Constants;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.graph.CourseGraphTask;
import com.cyanojay.looped.net.API;

public class GradesActivity extends SherlockListFragment {
	public static final String COURSE_SELECTED = "COURSE_SELECTED";
	
	private GradesAdapter mainAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i("", "ON CREATE EXECUTED");
        
        ScrapeGradesTask task = new ScrapeGradesTask();
        task.execute();
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_grades, container, false);
		
		Log.i("", "ON CREATE VIEW EXECUTED");
		
		return view;
	}
    
   /* @Override
	public void onPause() {
		super.onPause();
		
		Log.i("", "ON PAUSE EXECUTED");
	}
    
    @Override
	public void onStop() {
		super.onStop();
		
		Log.i("", "ON STOP EXECUTED");
	}
    
    @Override
	public void onDestroyView() {
		super.onDestroyView();
		
		Log.i("", "ON DESTROY VIEW EXECUTED");
	}
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.i("", "ON DESTROY EXECUTED");
	}*/
    
    private class ScrapeGradesTask extends AsyncTask<String, Void, List<Course>> {
    	@Override
		protected List<Course> doInBackground(String... params) {
			return API.get().getCourses();
		}
		
		@Override
	    protected void onPostExecute(List<Course> result) {
	        super.onPostExecute(result);
	        
	        Course[] values = result.toArray(new Course[result.size()]);
	        mainAdapter = new GradesAdapter(getSherlockActivity(), values);
	        
	        ListView listView = (ListView) getView().findViewById(android.R.id.list);
	        
	        listView.setOnItemClickListener(new GradesItemClickAdapter(mainAdapter, getSherlockActivity()));
	        listView.setOnItemLongClickListener(new GradesItemLongClickAdapter(mainAdapter, getSherlockActivity()));
	        
	        setListAdapter(mainAdapter);
		}
    };
    
    private class GradesAdapter extends ArrayAdapter<Course> {
    	  private Context context;
    	  private Course[] values;
    	  
    	  public GradesAdapter(Context context, Course[] values) {
    		  super(context, R.layout.curr_grades_row, values);
    		  this.context = context;
    		  this.values = values;
    	  }
    	  
    	  public void setValues(Course[] values) {
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
  
    		  return rowView;
    	} 
    }

    private class GradesItemClickAdapter implements AdapterView.OnItemClickListener {
    	GradesAdapter adapter;
    	Context parent;
    	
    	public GradesItemClickAdapter(GradesAdapter adapter, Context parent) {
    		this.adapter = adapter;
    		this.parent = parent;
    	}

		@Override
		public void onItemClick(AdapterView<?> list, View view, int position, long id) {
			if(!Utils.isOnline(parent)) {
	    		Toast.makeText(parent, "Internet connectivity is lost. Please re-connect and try again.", Toast.LENGTH_LONG).show();
	    		return;
	    	}
	    	
	    	Course selectedCourse = (Course) adapter.getItem(position);
	    	
	    	if(selectedCourse.getDetailsUrl().length() == 0) {
	    		Toast.makeText(parent, "Progress report for course is unpublished/unavailable.", Toast.LENGTH_LONG).show();
	    		return;
	    	}
	    	
	    	Intent detailsIntent = new Intent(parent, GradeDetailsActivity.class);
	    	detailsIntent.putExtra(COURSE_SELECTED, selectedCourse);
	    	
	    	parent.startActivity(detailsIntent);
		}
    }
    
    private class GradesItemLongClickAdapter implements AdapterView.OnItemLongClickListener {
    	GradesAdapter adapter;
    	Context parent;
    	
    	public GradesItemLongClickAdapter(GradesAdapter adapter, Context parent) {
    		this.adapter = adapter;
    		this.parent = parent;
    	}
    	
		@Override
		public boolean onItemLongClick(AdapterView<?> list, View view, int position, long id) {
			System.out.println("on long click received");
			
			Course selected = adapter.getItem(position);
			AlertDialog dialog = getOptionsDialog(selected);
			dialog.show();

			return false;
		}
		
	    private AlertDialog getOptionsDialog(final Course selected) {
	    	String[] options = new String[] { "Graph" };
	    	
	        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
	        builder.setTitle("Choose Action");
			builder.setItems(options, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
						case 0:
							System.out.println("GRAPHING: " + selected.getName());
							
							if(selected.getDetailsUrl().length() == 0) {
								Toast.makeText(parent, "Progress graph for course is unavailable.", Toast.LENGTH_LONG).show();
					    		break;
							}
								
							CourseGraphTask task = new CourseGraphTask(parent, selected);
							task.execute();
					}
				}
			});
			
	        return builder.create();
	    }
    }
}
