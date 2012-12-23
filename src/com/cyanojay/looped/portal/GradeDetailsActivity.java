package com.cyanojay.looped.portal;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cyanojay.looped.Constants;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;

public class GradeDetailsActivity extends BaseListActivity {
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_details);
        
        Course currCourse = (Course) getIntent().getSerializableExtra(GradesActivity.COURSE_SELECTED);
        
        setTitle(currCourse.getName());
        
        if(Utils.getApiVer() >= Build.VERSION_CODES.HONEYCOMB)
        	getActionBar().setSubtitle(currCourse.getPercentGrade() + " " + currCourse.getLetterGrade());
        else setTitle(currCourse.getName() + ": " + currCourse.getPercentGrade() + " " + currCourse.getLetterGrade());
        
        ScrapeGradeDetailsTask task = new ScrapeGradeDetailsTask();
        task.execute(currCourse);
    }
    
    @Override
    public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);
    }
    
    private class ScrapeGradeDetailsTask extends AsyncTask<Course, Void, List<GradeDetail>> {
    	private ProgressDialog load;
    	
    	@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        load = ProgressDialog.show(GradeDetailsActivity.this, "Looped", "Retrieving grades...");
		}
    	
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
	        
	        try {
	        	load.dismiss();
	        	load = null;
	        } catch (Exception e) {}
	        
	        GradeDetail[] values = result.toArray(new GradeDetail[result.size()]);
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
	  		  
	  		  percent.setText(detail.getDisplayPercent());
		  	  score.setText(detail.getDisplayScore());
	  		  
	  		  return rowView;
	  	}
	}
}
