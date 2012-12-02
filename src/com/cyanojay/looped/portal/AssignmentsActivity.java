package com.cyanojay.looped.portal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.cyanojay.looped.R;
import com.cyanojay.looped.net.API;

public class AssignmentsActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);
        
        ScrapeAssignmentsTask task = new ScrapeAssignmentsTask();
        task.execute();
    }
    
    private class ScrapeAssignmentsTask extends AsyncTask<String, Void, List<CurrentAssignment>> {
		@Override
		protected List<CurrentAssignment> doInBackground(String... params) {
	        return API.get().getCurrentAssignments();
		}
		
		@Override
	    protected void onPostExecute(List<CurrentAssignment> result) {
	        super.onPostExecute(result);
	        
	        CurrentAssignment[] values = result.toArray(new CurrentAssignment[0]);
	        CurrentAssignmentsAdapter adapter = 
	        		new CurrentAssignmentsAdapter(AssignmentsActivity.this, values);
	        
	        AssignmentsActivity.this.setListAdapter(adapter);
		}
    };
    
    private class CurrentAssignmentsAdapter extends ArrayAdapter<CurrentAssignment> {
    	  private final Context context;
    	  private final CurrentAssignment[] values;
    	  private final String TODAY = new SimpleDateFormat("MM/dd/yy").format(Calendar.getInstance().getTime());
    	  
    	  public CurrentAssignmentsAdapter(Context context, CurrentAssignment[] values) {
    		  super(context, R.layout.curr_assignments_row, values);
    		  this.context = context;
    		  this.values = values;
    	  }

    	  @Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    		  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		  View rowView = inflater.inflate(R.layout.curr_assignments_row, parent, false);
    		  
    		  TextView name = (TextView) rowView.findViewById(R.id.assign_name);
    		  TextView courseName = (TextView) rowView.findViewById(R.id.assign_course_name);
    		  TextView dueDate = (TextView) rowView.findViewById(R.id.assign_due_date);
    		  
    		  CurrentAssignment assignment = values[position];
    		  
    		  name.setText(assignment.getName());
    		  courseName.setText(assignment.getCourseName().substring(0,assignment.getCourseName().indexOf("Period")));
    		  dueDate.setText("Due " + assignment.getDueDate());
    		  
    		  if(assignment.getDueDate().equals(TODAY)) {
    			  dueDate.setText(Html.fromHtml(dueDate.getText() + "<font color=\"#FF0000\"> (today)</font>"));
    		  }
    		  
    		  return rowView;
    	  }
    	} 
    
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.assignment_details_popup, null, false);
    	LinearLayout wrapper = (LinearLayout) flow.findViewById(R.id.assigndet_wrapper);
    	LinearLayout content = (LinearLayout) flow.findViewById(R.id.assigndet_content);
    	ProgressBar load = (ProgressBar) flow.findViewById(R.id.assigndet_prog);
    	
    	Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight(); 
        
    	final PopupWindow pw = new PopupWindow(flow, width-((int)(0.1*width)), LayoutParams.WRAP_CONTENT, true);
        
        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        
        load.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE); 
        pw.showAtLocation(flow, Gravity.CENTER, 0, 0);
        
        CurrentAssignment selected = (CurrentAssignment) getListAdapter().getItem(position);
    	ScrapeAssignmentDetailsTask task = new ScrapeAssignmentDetailsTask(flow, content, load);
    	task.execute(selected);
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
	        explanation.loadData(assignDetail.getExplanation(), "text/html", "UTF-8");
	        
	        String attachSeparator = "<br />";
	        String explCont = assignDetail.getExplanation() + attachSeparator + assignDetail.getAttachments();
	        
	        if(explCont.replace(attachSeparator, "").length() > 0)
	        	explanation.loadDataWithBaseURL(null, explCont, "text/html", "UTF-8", null);
	        else ((LinearLayout) explanation.getParent()).removeView(explanation);
	        
	        String infoStr = "";
	        for(String detail : assignDetail.getDetails()) {
	        	if(detail.startsWith("Assigned")) {
	        		int idx = detail.indexOf("Due");
	        		String assigned = detail.substring(0, idx);
	        		String due = detail.substring(idx);
	        		
	        		String parts1[] = assigned.split(": ");
	        		String parts2[] = due.split(": ");
	        		
	        		infoStr += "<b>" + parts1[0] + "</b>: " + parts1[1] + "<br />" +
	        				   "<b>" + parts2[0] + "</b>: " + parts2[1] + "<br />";
	        	} else {
	        		String parts[] = detail.split(": ");
	        		infoStr += "<b>" + parts[0] + "</b>: " + parts[1] + "<br />";
	        	}
	        }
	        
	        info.setText(Html.fromHtml(infoStr));
	        bar.setVisibility(View.GONE);
	        
	        content.setVisibility(View.VISIBLE);
		}
    };
}
