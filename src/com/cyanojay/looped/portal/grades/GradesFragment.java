package com.cyanojay.looped.portal.grades;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.cyanojay.looped.Constants;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.debug.RemoteDebug;
import com.cyanojay.looped.graph.CourseGraphTask;
import com.cyanojay.looped.graph.CourseGraphTask.GraphTaskType;
import com.cyanojay.looped.net.API;
import com.cyanojay.looped.net.RefreshTask;
import com.cyanojay.looped.portal.common.Refreshable;

public class GradesFragment extends SherlockListFragment implements Refreshable {
	public static final String COURSE_SELECTED = "COURSE_SELECTED";
	private GradesAdapter adapter;
	
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
			try {
				return API.get().getCourses();
			} catch (Exception e) {
				RemoteDebug.debugException(e);
			}
			
			return null;
		}
		
		@Override
	    protected void onPostExecute(List<Course> result) {
	        super.onPostExecute(result);
	        
	        Course[] values = result.toArray(new Course[result.size()]);
	        
	        if(values.length > 0) {
		        adapter = new GradesAdapter(getSherlockActivity(), values);
		        
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
			if(Utils.isNetworkOffline(parent)) return;
	    	
	    	Course selectedCourse = (Course) adapter.getItem(position);
	    	
	    	if(selectedCourse == null ||
	    		selectedCourse.getDetailsUrl() == null ||
	    		selectedCourse.getDetailsUrl().length() == 0) {
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
			////System.out.println("on long click received");
			
			CourseGraphTask task = new CourseGraphTask(parent, toGraph);
			task.execute(GraphTaskType.COURSE);
			
			//AlertDialog dialog = getGraphDialog();
			//dialog.show();
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
							if(Utils.isNetworkOffline(parent)) return;
							
							CourseGraphTask task = new CourseGraphTask(parent, toGraph);

							if(which == 0) {
								task.execute(GraphTaskType.ASSIGNMENTS);
							} else if(which == 1) {
								task.execute(GraphTaskType.COURSE);
							}
						}
					}
				}
			});
			
	        return builder.create();
	    }
    }
    
	@Override
	public void refresh(FragmentManager manager) {
		if(Utils.isNetworkOffline(getSherlockActivity())) return;
		
		preRefresh();
		
    	//System.out.println("Refreshing Grades");
		final ProgressDialog progressDialog = ProgressDialog.show(getSherlockActivity(), "Looped", "Refreshing...");
		
		Runnable firstJob = new Runnable() {
			@Override
			public void run() {
				try {
					API.get().refreshCoursePortal();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					Utils.safelyDismissDialog(progressDialog);
				}
				
				ScrapeGradesTask task = new ScrapeGradesTask();
		        task.execute();
			}
		};
		
		Runnable secondJob = new Runnable() {
			@Override
			public void run() {
				try {
					adapter.notifyDataSetChanged();
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					Utils.safelyDismissDialog(progressDialog);
				}
		        
		        //System.out.println("Finished refreshing Grades");
				postRefresh();
			}
		};
		
		RefreshTask refreshTask = new RefreshTask(firstJob, secondJob);
		refreshTask.execute();
	}

	@Override
	public void preRefresh() {
		Utils.lockOrientation(getSherlockActivity());
	}

	@Override
	public void postRefresh() {
		Utils.unlockOrientation(getSherlockActivity());
	}
}
