package com.cyanojay.looped.portal;

import java.io.IOException;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;

public class LoopMailActivity extends SherlockListFragment {
	
	private enum LoopMailBoxType {
		INBOX, SENT, ARCHIVE
	}
	
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
	        LoopMailAdapter adapter = new LoopMailAdapter(getSherlockActivity(), values);
	        
	        ListView listView = (ListView) getView().findViewById(android.R.id.list);
	        
	        listView.setOnItemClickListener(new MailItemClickAdapter(adapter, getSherlockActivity()));
	        
	        setListAdapter(adapter);
		}
    };
    
    private class LoopMailAdapter extends ArrayAdapter<MailEntry> {
    	  private final Context context;
    	  private final MailEntry[] values;
    	  
    	  public LoopMailAdapter(Context context, MailEntry[] values) {
    		  super(context, R.layout.curr_mail_row, values);
    		  this.context = context;
    		  this.values = values;
    	  }

    	  @Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    		  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		  View rowView = inflater.inflate(R.layout.curr_mail_row, parent, false);
    		  
    		  TextView timestamp = (TextView) rowView.findViewById(R.id.mail_date);
    		  TextView parties = (TextView) rowView.findViewById(R.id.mail_sender);
    		  TextView subject = (TextView) rowView.findViewById(R.id.mail_subject);
    		  
    		  MailEntry entry = values[position];
    		  
    		  timestamp.setText(entry.getTimestamp());
    		  parties.setText(entry.getInvolvedParties());
    		  subject.setText(entry.getSubject());
    		  
    		  return rowView;
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

	        bar.setVisibility(View.GONE);
	        contwrap.setVisibility(View.VISIBLE);
		}
    }
    
    private class MailItemClickAdapter implements AdapterView.OnItemClickListener {
    	LoopMailAdapter adapter;
    	Context parent;
    	
    	public MailItemClickAdapter(LoopMailAdapter adapter, Context parent) {
    		this.adapter = adapter;
    		this.parent = parent;
    	}

		@Override
		public void onItemClick(AdapterView<?> list, View view, int position, long id) {
			if(!Utils.isOnline(parent)) {
	    		Toast.makeText(parent, "Internet connectivity is lost. Please re-connect and try again.", Toast.LENGTH_LONG).show();
	    		return;
	    	}
	    	
	    	Display display = getSherlockActivity().getWindowManager().getDefaultDisplay(); 
	        int width = display.getWidth();
	        
	        LayoutInflater inflater = (LayoutInflater) getSherlockActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.mail_details_popup, null, false);
	        LinearLayout contwrap = (LinearLayout) flow.findViewById(R.id.maildet_contwrap);
	    	ProgressBar load = (ProgressBar) flow.findViewById(R.id.popup_prog);
	        
	        final PopupWindow pw = new PopupWindow(flow, width-((int)(0.05*width)), LayoutParams.WRAP_CONTENT, true);
	        
	        ((Button) flow.findViewById(R.id.exit_btn)).setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                pw.dismiss();
	            }
	        });
	        
	        load.setVisibility(View.VISIBLE);
	        contwrap.setVisibility(View.GONE);
	        pw.showAtLocation(flow, Gravity.CENTER, 0, 0);
	        
	        MailEntry selected = (MailEntry) adapter.getItem(position);
	        ScrapeMailContentTask task = new ScrapeMailContentTask(flow, contwrap, load);
	    	task.execute(selected);
		}
    }
}
