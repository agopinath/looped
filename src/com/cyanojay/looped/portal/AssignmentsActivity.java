package com.cyanojay.looped.portal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
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

public class AssignmentsActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_assignments, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
    	CurrentAssignment selected = (CurrentAssignment) getListAdapter().getItem(position);
    	Toast.makeText(this, selected.getName() + " selected", Toast.LENGTH_SHORT).show();
    	
    	ScrapeAssignmentDetailsTask task = new ScrapeAssignmentDetailsTask();
    	task.execute(selected);
    }
    
    private class ScrapeAssignmentDetailsTask extends AsyncTask<CurrentAssignment, Void, AssignmentDetail> {
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
	    protected void onPostExecute(AssignmentDetail result) {
	        super.onPostExecute(result);
	        
	        // TODO: display the assignment details appropriately in a PopUpWindow
		}
    };
}
