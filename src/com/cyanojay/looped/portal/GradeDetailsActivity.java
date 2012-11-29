package com.cyanojay.looped.portal;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
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

public class GradeDetailsActivity extends BaseListActivity {
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_details);
        
        Course currCourse = (Course) getIntent().getSerializableExtra("COURSE_SELECTED");
        
        setTitle(currCourse.getName());
        
        int apiVer = Build.VERSION.SDK_INT;
        
        if(apiVer >= Build.VERSION_CODES.HONEYCOMB)
        	getActionBar().setSubtitle(currCourse.getPercentGrade() + " " + currCourse.getLetterGrade());
        
        ScrapeGradeDetailsTask task = new ScrapeGradeDetailsTask();
        task.execute(currCourse);
    }
    
    private class ScrapeGradeDetailsTask extends AsyncTask<Course, Void, List<GradeDetail>> {
    	@Override
		protected List<GradeDetail> doInBackground(Course... params) {
			try {
				return API.get().getGradeDetails(params[0]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
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
	  		  TextView percent = (TextView) rowView.findViewById(R.id.gradedet_pct);
	  		  TextView score = (TextView) rowView.findViewById(R.id.gradedet_score);
	  		
	  		  GradeDetail detail = values[position];
	  		  
	  		  name.setText(detail.getDetailName());
	  		  category.setText(detail.getCategory());
	  		  date.setText(detail.getDueDate());	  
	  		
	  		  if(!((int) detail.getTotalPoints() == 0)) {
		  		  percent.setText(detail.getDisplayPercent());
		  		  score.setText(detail.getDisplayScore());
	  		  } else {
	  			  percent.setText("--");
	  			  score.setText("");
	  		  }
	  		  
	  		  return rowView;
	  	}
	}
}
