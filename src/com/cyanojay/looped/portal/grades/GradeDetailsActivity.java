package com.cyanojay.looped.portal.grades;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import com.cyanojay.looped.Constants;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.debug.RemoteDebug;
import com.cyanojay.looped.net.API;
import com.cyanojay.looped.net.RefreshTask;
import com.cyanojay.looped.portal.BaseListActivity;
import com.cyanojay.looped.portal.common.Refreshable;
import com.cyanojay.looped.portal.common.SortType;
import com.cyanojay.looped.portal.common.Sortable;

public class GradeDetailsActivity extends BaseListActivity implements Refreshable, Sortable {
	private GradeDetailsAdapter adapter;
	
	private static final Comparator<GradeDetail> DATE_COMPARATOR = new Comparator<GradeDetail>() {
		@Override
		public int compare(GradeDetail lhs, GradeDetail rhs) {
			Date d1 = null;
			Date d2 = null;
			
			try {
				d1 = Constants.LOOPED_DATE_FORMAT.parse(lhs.getDueDate());
				d2 = Constants.LOOPED_DATE_FORMAT.parse(rhs.getDueDate());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(d1 != null && d2 != null)
				return d1.compareTo(d2);
			
			return 0;
		}
	};
	
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
            case R.id.menu_sort:
            	this.sort(SortType.DATE);
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
			} catch (Exception e) {
				RemoteDebug.debugException(e);
			}
			
			return null;
		}
		
		@Override
	    protected void onPostExecute(List<GradeDetail> result) {
	        super.onPostExecute(result);
	        
	        if(!fromRefresh)
	        	Utils.safelyDismissDialog(load);
	        
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
	  	  private boolean sortDir;
	  	  
	  	  public GradeDetailsAdapter(Context context, GradeDetail[] values) {
	  		  super(context, R.layout.grade_detail_row, values);
	  		  this.context = context;
	  		  
	  		  Collections.sort(Arrays.asList(values), Collections.reverseOrder(DATE_COMPARATOR));
	  		  
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
	  	  
	  	  public GradeDetail[] getValues() {
	  		  return values;
	  	  }

	  	  public boolean getSortOrder() {
	  		  return sortDir;
	  	  }

	  	  public void toggleSortOrder() {
	  		  sortDir = !sortDir;
	  	  }
	}

	@Override
	public void refresh(FragmentManager manager) {
		if(Utils.isNetworkOffline(this)) return;
		
		preRefresh();
		
		//System.out.println("Refreshing Grade Details");
		final ProgressDialog progressDialog = ProgressDialog.show(this, "Looped", "Refreshing...");
		
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
					Utils.safelyDismissDialog(progressDialog);
				}
		        
		        //System.out.println("Finished refreshing Grade Details");
				postRefresh();
			}
		};
		
		RefreshTask refreshTask = new RefreshTask(firstJob, secondJob);
		refreshTask.execute();
	}

	@Override
	public void sort(SortType type) {
		if(adapter != null) {
			adapter.toggleSortOrder();
			
			Collections.sort(Arrays.asList(adapter.getValues()), 
					adapter.getSortOrder() ? DATE_COMPARATOR : Collections.reverseOrder(DATE_COMPARATOR));
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void preRefresh() {
		Utils.lockOrientation(this);
	}

	@Override
	public void postRefresh() {
		Utils.unlockOrientation(this);
	}
}
