package com.cyanojay.looped.portal;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cyanojay.looped.API;
import com.cyanojay.looped.R;

public class GradeDetailsActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_details);
        
        Course currCourse = (Course) getIntent().getSerializableExtra("COURSE_SELECTED");
        
        ScrapeGradeDetailsTask task = new ScrapeGradeDetailsTask();
        task.execute(currCourse);
    }
    
    private class ScrapeGradeDetailsTask extends AsyncTask<Course, Void, List<GradeDetail>> {
    	@Override
		protected List<GradeDetail> doInBackground(Course... params) {
			return API.get().getGradeDetails(params[0]);
		}
		
		@Override
	    protected void onPostExecute(List<GradeDetail> result) {
	        super.onPostExecute(result);
	        
	        GradeDetail[] values = result.toArray(new GradeDetail[0]);
	        GradeDetailsAdapter adapter = new GradeDetailsAdapter(GradeDetailsActivity.this, values);
	        
	        GradeDetailsActivity.this.setListAdapter(adapter);
		}
    };
    
    private class GradeDetailsAdapter extends ArrayAdapter<GradeDetail> {
	  	  private final Context context;
	  	  private final GradeDetail[] values;
	  	  
	  	  public GradeDetailsAdapter(Context context, GradeDetail[] values) {
	  		  super(context, R.layout.grade_detail_row, values);
	  		  this.context = context;
	  		  this.values = values;
	  	  }
	
	  	  @Override
	  	  public View getView(int position, View convertView, ViewGroup parent) {
	  		  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  		  View rowView = inflater.inflate(R.layout.grade_detail_row, parent, false);
	  		  
	  		  TextView name = (TextView) rowView.findViewById(R.id.gradedet_name);
	  		  TextView category = (TextView) rowView.findViewById(R.id.gradedet_categ);
	  		  TextView date = (TextView) rowView.findViewById(R.id.gradedet_date);
	  		  TextView percent = (TextView) rowView.findViewById(R.id.grades_pct_grade);
	  		  TextView score = (TextView) rowView.findViewById(R.id.gradedet_score);
	  		
	  		  GradeDetail detail = values[position];
	  		  
	  		  // TODO: fill information into Views accordingly
	  		  
	  		  return rowView;
	  	} 
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_grade_details, menu);
        return true;
    }
}
