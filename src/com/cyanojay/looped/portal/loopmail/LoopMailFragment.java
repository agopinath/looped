package com.cyanojay.looped.portal.loopmail;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import com.cyanojay.looped.Constants;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;
import com.cyanojay.looped.net.RefreshTask;
import com.cyanojay.looped.portal.common.Refreshable;
import com.cyanojay.looped.portal.common.SortType;
import com.cyanojay.looped.portal.common.Sortable;

public class LoopMailFragment extends SherlockListFragment implements Refreshable, Sortable {
	private LoopMailAdapter adapter;
	
	private enum LoopMailBoxType {
		INBOX, SENT, ARCHIVE
	}
	
	private static final Comparator<MailEntry> DATE_COMPARATOR = new Comparator<MailEntry>() {
		@Override
		public int compare(MailEntry lhs, MailEntry rhs) {
			Date d1 = null;
			Date d2 = null;
			
			try {
				d1 = Constants.LOOPED_DATE_FORMAT.parse(lhs.getTimestamp());
				d2 = Constants.LOOPED_DATE_FORMAT.parse(rhs.getTimestamp());
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
        
        ScrapeLoopMailTask task = new ScrapeLoopMailTask();
        task.execute(LoopMailBoxType.INBOX);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_loop_mail, container, false);

		return view;
	}
    
    private class ScrapeLoopMailTask extends AsyncTask<LoopMailBoxType, Void, List<MailEntry>> {
    	@Override
		protected List<MailEntry> doInBackground(LoopMailBoxType... params) {
    		switch(params[0]) {
    			case INBOX:
					try {
						return API.get().getMailInbox();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
    			default:
    				// TODO: handle the case if user doesn't request for inbox
    		}
    		
    		return null;
		}
		
		@Override
	    protected void onPostExecute(List<MailEntry> result) {
	        super.onPostExecute(result);
	        
	        MailEntry[] values = result.toArray(new MailEntry[result.size()]);
	        if(values.length > 0) {
		        adapter = new LoopMailAdapter(getSherlockActivity(), values);
		        
		        ListView listView = (ListView) getView().findViewById(android.R.id.list);
		        
		        listView.setOnItemClickListener(new MailItemClickAdapter(adapter, getSherlockActivity()));
		        
		        setListAdapter(adapter);
	        } else {
	        	ListView listView = (ListView) getView().findViewById(android.R.id.list);
	        	TextView emptyText = Utils.getCenteredTextView(getSherlockActivity(), getString(R.string.empty_mail));
	        	
	        	Utils.showViewOnTop(listView, emptyText);
	        }
		}
    };
    
    private class LoopMailAdapter extends ArrayAdapter<MailEntry> {
    	  private final Context context;
    	  private final MailEntry[] values;
    	  private boolean sortDir;
    	  
    	  public LoopMailAdapter(Context context, MailEntry[] values) {
    		  super(context, R.layout.curr_mail_row, values);
    		  this.context = context;
    		  this.values = values;
    	  }

    	  @Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    		  View rowView = convertView;
	  		  
	  		  if(rowView == null) {
	  			  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  			  rowView = inflater.inflate(R.layout.curr_mail_row, parent, false);
	  		  }
	  		  
    		  TextView timestamp = (TextView) rowView.findViewById(R.id.mail_date);
    		  TextView parties = (TextView) rowView.findViewById(R.id.mail_sender);
    		  TextView subject = (TextView) rowView.findViewById(R.id.mail_subject);
    		  
    		  MailEntry entry = values[position];
    		  
    		  timestamp.setText(entry.getTimestamp());
    		  parties.setText(entry.getInvolvedParties());
    		  subject.setText(entry.getSubject());
    		  
    		  return rowView;
    	}

		public MailEntry[] getValues() {
			return values;
		}
		
		public boolean getSortOrder() {
			return sortDir;
		}
		
		public void toggleSortOrder() {
			sortDir = !sortDir;
		}
    }
    
    private class ScrapeMailContentTask extends AsyncTask<MailEntry, Void, MailDetail> {
    	private View flow;
    	private View contwrap;
    	private ProgressBar bar;
    	
    	public ScrapeMailContentTask(View flow, View contwrap, ProgressBar bar) {
    		this.flow = flow;
    		this.bar = bar;
    		this.contwrap = contwrap;
    	}
    	
		@Override
		protected MailDetail doInBackground(MailEntry... params) {
			try {
				return API.get().getMailDetails(params[0]);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
	    protected void onPostExecute(MailDetail mailDetail) {
	        super.onPostExecute(mailDetail);

	        TextView to = (TextView) flow.findViewById(R.id.maildet_to);
	        TextView from = (TextView) flow.findViewById(R.id.maildet_from);
	        TextView rest = (TextView) flow.findViewById(R.id.maildet_rest);
	        WebView content = (WebView) flow.findViewById(R.id.maildet_content);
	        
	        List<String> details = mailDetail.getDetails();
	        
	        for(int i = 0; i < details.size(); i++) {
	        	String detail = details.get(i);
	        	
	        	if(i == 0) {
	        		if(details.get(0).length() == 0) {
	    	        	((ViewGroup) to.getParent()).removeView(to);
	    	        } else {
	    	        	to.setText(Html.fromHtml(details.get(0)));
	        		}
	        	} 
	        	else if(i == 1) from.setText(Html.fromHtml(detail));
	        	else if(i == 2) rest.setText(Html.fromHtml(detail));
	        }

	        if(mailDetail.getContent().length() != 0)
	        	content.loadDataWithBaseURL(null, mailDetail.getContent(), "text/html", "UTF-8", null);
	        else 
	        	((LinearLayout) content.getParent()).removeView(content);

	        content.setFocusable(true);
	        content.setFocusableInTouchMode(true);
	        
	        bar.setVisibility(View.GONE);
	        contwrap.setVisibility(View.VISIBLE);
		}
    }
    
    private class MailItemClickAdapter implements AdapterView.OnItemClickListener {
    	LoopMailAdapter adapter;
    	Activity parent;
    	
    	public MailItemClickAdapter(LoopMailAdapter adapter, Activity parent) {
    		this.adapter = adapter;
    		this.parent = parent;
    	}

		@Override
		public void onItemClick(AdapterView<?> list, View view, int position, long id) {
			if(Utils.isNetworkOffline(parent)) return;
	    	
	    	Display display = parent.getWindowManager().getDefaultDisplay(); 
	        int width = display.getWidth();
	        
	        LayoutInflater inflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.mail_details_popup, null, false);
	        LinearLayout contwrap = (LinearLayout) flow.findViewById(R.id.maildet_contwrap);
	    	ProgressBar load = (ProgressBar) flow.findViewById(R.id.popup_prog);
	        
	        Dialog popup = Utils.createLoopedDialog(parent, flow, width);
	        
	        load.setVisibility(View.VISIBLE);
	        contwrap.setVisibility(View.GONE);
	        
	        popup.show();
	        
	        MailEntry selected = (MailEntry) adapter.getItem(position);
	        ScrapeMailContentTask task = new ScrapeMailContentTask(flow, contwrap, load);
	    	task.execute(selected);
		}
    }

	@Override
	public void refresh(FragmentManager manager) {
		if(Utils.isNetworkOffline(getSherlockActivity())) return;
		
		preRefresh();
		
		//System.out.println("Refreshing LoopMail");
		final ProgressDialog progressDialog = ProgressDialog.show(getSherlockActivity(), "Looped", "Refreshing...");
		
		Runnable firstJob = new Runnable() {
			@Override
			public void run() {
				try {
					API.get().refreshLoopMail();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					Utils.safelyDismissDialog(progressDialog);
				}
				
				ScrapeLoopMailTask task = new ScrapeLoopMailTask();
		        task.execute(LoopMailBoxType.INBOX);
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
		        
		        //System.out.println("Finished refreshing LoopMail");
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
		Utils.lockOrientation(getSherlockActivity());
	}

	@Override
	public void postRefresh() {
		Utils.unlockOrientation(getSherlockActivity());
	}
}
