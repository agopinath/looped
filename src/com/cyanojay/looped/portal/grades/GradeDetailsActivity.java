package com.cyanojay.looped.portal.grades;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;
import com.cyanojay.looped.net.RefreshTask;
import com.cyanojay.looped.portal.BaseListActivity;
import com.cyanojay.looped.portal.Refreshable;

public class GradeDetailsActivity extends BaseListActivity implements Refreshable {
	private GradeDetailsAdapter adapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_details);
        
        Course currCourse = (Course) getIntent().getSerializableExtra(GradesFragment.COURSE_SELECTED);
        
        getSupportActionBar().setTitle(currCourse.getName());
        getSupportActionBar().setSubtitle(currCourse.getPercentGrade() + " " + currCourse.getLetterGrade());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        ScrapeGradeDetailsTask task = new ScrapeGradeDetailsTask(false);
        task.execute(currCourse);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
        switch (item.getItemId()) {
            case R.id.menu_refresh:
            	this.refresh(null);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
    private class ScrapeGradeDetailsTask extends AsyncTask<Course, Void, List<GradeDetail>> {
    	private ProgressDialog load;
		private boolean fromRefresh;
    	
    	public ScrapeGradeDetailsTask(boolean fromRefresh) {
    		this.fromRefresh = fromRefresh;
    	}
    	
    	@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        if(!fromRefresh)
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
	        	if(!fromRefresh)
	        	load.dismiss();
	        	load = null;
	        } catch (Exception e) {}
	        
	        GradeDetail[] values = result.toArray(new GradeDetail[result.size()]);
	        
	        if(values.length > 0) {
		        adapter = new GradeDetailsAdapter(GradeDetailsActivity.this, values);
		        
		        GradeDetailsActivity.this.setListAdapter(adapter);
	        } else {
	        	ListView listView = (ListView) findViewById(android.R.id.list);
	        	TextView emptyText = Utils.getCenteredTextView(GradeDetailsActivity.this, getString(R.string.empty_course_grades));
	        	
	        	Utils.showViewOnTop(listView, emptyText);
	        }
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
	  		  View rowView = convertView;
	  		  
	  		  if(rowView == null) {
	  			  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  			  rowView = inflater.inflate(R.layout.grade_detail_row, parent, false);
	  		  }
	  		  
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

	@Override
	public void refresh(FragmentManager manager) {
		if(Utils.isNetworkOffline(this)) return;
		
		System.out.println("Refreshing Grade Details");
		final ProgressDialog progressDialog = ProgressDialog.show(this, "Looped", "Refreshing...");
		
		Runnable firstJob = new Runnable() {
			@Override
			public void run() {
				try {
					API.get().refreshCoursePortal();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					progressDialog.dismiss();
				}
				
				ScrapeGradeDetailsTask task = new ScrapeGradeDetailsTask(true);
		        task.execute((Course) getIntent().getSerializableExtra(GradesFragment.COURSE_SELECTED));
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
		        
		        System.out.println("Finished refreshing Grade Details");
			}
		};
		
		RefreshTask refreshTask = new RefreshTask(firstJob, secondJob);
		refreshTask.execute();
	}
}
