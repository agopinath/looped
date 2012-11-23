package com.cyanojay.looped.portal;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
      CurrentAssignment item = (CurrentAssignment) getListAdapter().getItem(position);
      Toast.makeText(this, item.getName() + " selected", Toast.LENGTH_SHORT).show();
    }
}
