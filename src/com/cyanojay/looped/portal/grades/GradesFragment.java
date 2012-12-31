package com.cyanojay.looped.portal.grades;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.cyanojay.looped.Constants;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.graph.CourseGraphTask;
import com.cyanojay.looped.graph.CourseGraphTask.GraphTaskType;
import com.cyanojay.looped.net.API;

public class GradesFragment extends SherlockListFragment {
	public static final String COURSE_SELECTED = "COURSE_SELECTED";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ScrapeGradesTask task = new ScrapeGradesTask();
        task.execute();
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_grades, container, false);
		
		return view;
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
	        
	        if(values.length > 0) {
		        GradesAdapter adapter = new GradesAdapter(getSherlockActivity(), values);
		        
		        ListView listView = (ListView) getView().findViewById(android.R.id.list);
		        
		        listView.setOnItemClickListener(new GradesItemClickAdapter(adapter, getSherlockActivity()));
		        
		        setListAdapter(adapter);
	        } else {
	        	ListView listView = (ListView) getView().findViewById(android.R.id.list);
	        	TextView emptyText = Utils.getCenteredTextView(getSherlockActivity(), getString(R.string.empty_courses));
	        	
	        	Utils.showViewOnTop(listView, emptyText);
	        }
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
    	  
    	  @Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    		  View rowView = convertView;
	  		  
	  		  if(rowView == null) {
	  			  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  			  rowView = inflater.inflate(R.layout.curr_grades_row, parent, false);
	  		  }
	  		  
    		  TextView courseName = (TextView) rowView.findViewById(R.id.grades_course_name);
    		  TextView lettGrade = (TextView) rowView.findViewById(R.id.grades_lett_grade);
    		  TextView pctGrade = (TextView) rowView.findViewById(R.id.grades_pct_grade);
    		  ImageButton graphBtn = (ImageButton) rowView.findViewById(R.id.graph_btn);
    		  
    		  Course course = values[position];
    		  
    		  courseName.setText(course.getName());
    		  
    		  if(!(course.getLetterGrade().length() == 0))
    			  lettGrade.setText(course.getLetterGrade());
    		  else
    			  lettGrade.setText(Constants.EMPTY_INDIC);
    		  
    		  boolean isGraphingDisabled = false;
    		  
    		  if(course.getDetailsUrl().length() == 0) {
    			  graphBtn.setVisibility(View.INVISIBLE);
    			  
    			  isGraphingDisabled = true;
    		  }
    		  
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
    		  
    		  if(!isGraphingDisabled) {
    			  graphBtn.setFocusable(false);
    			  graphBtn.setFocusableInTouchMode(false);
    			  graphBtn.setOnClickListener(new GraphButtonLongClickAdapter(course, getSherlockActivity()));
    		  }
    		  
    		  return rowView;
    	}
    }

    private class GradesItemClickAdapter implements AdapterView.OnItemClickListener {
    	GradesAdapter adapter;
    	Activity parent;
    	
    	public GradesItemClickAdapter(GradesAdapter adapter, Activity parent) {
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
    
    private class GraphButtonLongClickAdapter implements OnClickListener {
    	private final Course toGraph;
    	private Context parent;
    	
    	public GraphButtonLongClickAdapter(Course toGraph, Context parent) {
    		this.toGraph = toGraph;
    		this.parent = parent;
    	}
    	
		@Override
		public void onClick(View v) {
			//System.out.println("on long click received");
			
			AlertDialog dialog = getGraphDialog();
			dialog.show();
		}
		
	    private AlertDialog getGraphDialog() {
	    	String[] options = new String[] { "Graph Assignments", "Graph Course Grade" };
	    	
	        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
	        builder.setTitle("Choose Graph Type");
			builder.setItems(options, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0 || which == 1) {
						if(toGraph.getDetailsUrl().length() == 0) {
							Toast.makeText(parent, "Progress graph for course is unavailable.", Toast.LENGTH_SHORT).show();
						} else {
							if(Utils.isOnline(parent)) {
								CourseGraphTask task = new CourseGraphTask(parent, toGraph);
							
								if(which == 0) {
									task.execute(GraphTaskType.ASSIGNMENTS);
								} else if(which == 1) {
									task.execute(GraphTaskType.COURSE);
								}
							} else {
								Toast.makeText(parent, "Internet connectivity is lost. Please re-connect and try again.", Toast.LENGTH_SHORT).show();
							}
						}
					}
				}
			});
			
	        return builder.create();
	    }
    }
}
