package com.cyanojay.looped.portal.assignments;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;
import com.cyanojay.looped.net.RefreshTask;
import com.cyanojay.looped.portal.common.Refreshable;

public class AssignmentsFragmnet extends SherlockListFragment implements Refreshable {
	private CurrentAssignmentsAdapter adapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ScrapeAssignmentsTask task = new ScrapeAssignmentsTask();
        task.execute();
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_assignments, container, false);

		return view;
	}
    
    private class ScrapeAssignmentsTask extends AsyncTask<String, Void, List<CurrentAssignment>> {
		@Override
		protected List<CurrentAssignment> doInBackground(String... params) {
	        return API.get().getCurrentAssignments();
		}
		
		@Override
	    protected void onPostExecute(List<CurrentAssignment> result) {
	        super.onPostExecute(result);
	        
	        CurrentAssignment[] values = result.toArray(new CurrentAssignment[result.size()]);
	        
	        if(values.length > 0) {
		        adapter = new CurrentAssignmentsAdapter(getSherlockActivity(), values);
		        
		        ListView listView = (ListView) getView().findViewById(android.R.id.list);
		        
		        listView.setOnItemClickListener(new AssignmentItemClickAdapter(adapter, getSherlockActivity()));
		        
		        setListAdapter(adapter);
	        } else {
	        	ListView listView = (ListView) getView().findViewById(android.R.id.list);
	        	TextView emptyText = Utils.getCenteredTextView(getSherlockActivity(), getString(R.string.empty_assignments));
	        	
	        	Utils.showViewOnTop(listView, emptyText);
	        }
		}
    };
    
    private class CurrentAssignmentsAdapter extends ArrayAdapter<CurrentAssignment> {
    	  private final Context context;
    	  private final CurrentAssignment[] values;
    	  private final String TODAY = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
    	  
    	  public CurrentAssignmentsAdapter(Context context, CurrentAssignment[] values) {
    		  super(context, R.layout.curr_assignments_row, values);
    		  this.context = context;
    		  this.values = values;
    	  }

    	  @Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    		  View rowView = convertView;
	  		  
	  		  if(rowView == null) {
	  			  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  			  rowView = inflater.inflate(R.layout.curr_assignments_row, parent, false);
	  		  }
	  		  
    		  TextView name = (TextView) rowView.findViewById(R.id.assign_name);
    		  TextView courseName = (TextView) rowView.findViewById(R.id.assign_course_name);
    		  TextView dueDate = (TextView) rowView.findViewById(R.id.assign_due_date);
    		  
    		  CurrentAssignment assignment = values[position];
    		  
    		  name.setText(assignment.getName());
    		  courseName.setText(assignment.getCourseName());
    		  dueDate.setText("Due " + assignment.getDueDate());
    		  
    		  if(assignment.getDueDate().equals(TODAY)) {
    			  dueDate.setText(Html.fromHtml(dueDate.getText() + "<font color=\"#FF0000\"> (today)</font>"));
    		  }
    		  
    		  return rowView;
    	  }
    } 
    
    private class ScrapeAssignmentDetailsTask extends AsyncTask<CurrentAssignment, Void, AssignmentDetail> {
    	private View flow;
    	private View content;
    	private ProgressBar bar;
    	
    	public ScrapeAssignmentDetailsTask(View flow, View content, ProgressBar bar) {
    		this.flow = flow;
    		this.bar = bar;
    		this.content = content;
    	}
    	
		@Override
		protected AssignmentDetail doInBackground(CurrentAssignment... params) {
	        try {
				return API.get().getAssignmentDetails(params[0]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
			return null;
		}
		
		@Override
	    protected void onPostExecute(AssignmentDetail assignDetail) {
	        super.onPostExecute(assignDetail);
	        
	        TextView title = (TextView) flow.findViewById(R.id.assigndet_title);
	        TextView audience = (TextView) flow.findViewById(R.id.assigndet_audi);
	        TextView info = (TextView) flow.findViewById(R.id.assigndet_info);
	        WebView explanation = (WebView) flow.findViewById(R.id.assigndet_expl);
	        
	        title.setText(assignDetail.getName());
	        audience.setText(assignDetail.getTargetAudience());
	        
	        String separator = "<br />";
	        String explCont = assignDetail.getExplanation() + separator + assignDetail.getAttachments();
	        
	        if(!(explCont.replace(separator, "").length() == 0))
	        	explanation.loadDataWithBaseURL(null, explCont, "text/html", "UTF-8", null);
	        else 
	        	((LinearLayout) explanation.getParent()).removeView(explanation);
	        
	        String infoStr = "";
	        String colonDelim = ": ";
	        
	        for(String detail : assignDetail.getDetails()) {
	        	if(detail.startsWith("Assigned") && detail.contains("Due") && detail.contains(colonDelim)) {
					int idx = detail.indexOf("Due");
					String assigned = detail.substring(0, idx);
					String due = detail.substring(idx);

					String parts1[] = assigned.split(colonDelim);
					String parts2[] = due.split(colonDelim);

					infoStr += "<b>" + parts1[0] + "</b>: " + parts1[1]
							+ separator + "<b>" + parts2[0] + "</b>: "
							+ parts2[1] + separator;
	        	} else if(detail.contains(colonDelim)) {
	        		String parts[] = detail.split(colonDelim);
	        		infoStr += "<b>" + parts[0] + "</b>: " + parts[1] + separator;
	        	} else {
	        		infoStr += detail + separator;
	        	}
	        }
	        
	        content.setFocusable(true);
	        content.setFocusableInTouchMode(true);
	        
	        info.setText(Html.fromHtml(infoStr));
	        bar.setVisibility(View.GONE);
	        
	        content.setVisibility(View.VISIBLE);
		}
    };
    
    private class AssignmentItemClickAdapter implements AdapterView.OnItemClickListener {
    	CurrentAssignmentsAdapter adapter;
    	Activity parent;
    	
    	public AssignmentItemClickAdapter(CurrentAssignmentsAdapter adapter, Activity parent) {
    		this.adapter = adapter;
    		this.parent = parent;
    	}

		@Override
		public void onItemClick(AdapterView<?> list, View view, int position, long id) {
			if(Utils.isNetworkOffline(parent)) return;
	    	
	    	LayoutInflater inflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.assignment_details_popup, null, false);
	    	LinearLayout content = (LinearLayout) flow.findViewById(R.id.assigndet_content);
	    	ProgressBar load = (ProgressBar) flow.findViewById(R.id.popup_prog);
	    	
	    	Display display = parent.getWindowManager().getDefaultDisplay(); 
	        int width = display.getWidth();
	        
	        Dialog popup = Utils.createLoopedDialog(parent, flow, width);
	        
	        load.setVisibility(View.VISIBLE);
	        content.setVisibility(View.GONE); 
	        
	        popup.show();
	        
	        CurrentAssignment selected = (CurrentAssignment) adapter.getItem(position);
	    	ScrapeAssignmentDetailsTask task = new ScrapeAssignmentDetailsTask(flow, content, load);
	    	task.execute(selected);
		}
    }
    
    @Override
    public void refresh(FragmentManager manager) {
    	if(Utils.isNetworkOffline(getSherlockActivity())) return;
    	
    	System.out.println("Refreshing Assignments");
		final ProgressDialog progressDialog = ProgressDialog.show(getSherlockActivity(), "Looped", "Refreshing...");
		
		Runnable firstJob = new Runnable() {
			@Override
			public void run() {
				try {
					API.get().refreshMainPortal();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					progressDialog.dismiss();
				}
				
				ScrapeAssignmentsTask task = new ScrapeAssignmentsTask();
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
					progressDialog.dismiss();
				}
				
		        System.out.println("Finished refreshing Assignments");
			}
		};
		
		RefreshTask refreshTask = new RefreshTask(firstJob, secondJob);
		refreshTask.execute();
    }
}
