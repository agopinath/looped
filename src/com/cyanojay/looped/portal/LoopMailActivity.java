package com.cyanojay.looped.portal;

import java.io.IOException;
import java.util.List;

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

import com.cyanojay.looped.API;
import com.cyanojay.looped.R;

public class LoopMailActivity extends ListActivity {
	
	private enum LoopMailBoxType {
		INBOX, SENT, ARCHIVE
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop_mail);
        
        ScrapeLoopMailTask task = new ScrapeLoopMailTask();
        task.execute(LoopMailBoxType.INBOX);
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
	        
	        MailEntry[] values = result.toArray(new MailEntry[0]);
	        LoopMailAdapter adapter = new LoopMailAdapter(LoopMailActivity.this, values);
	        
	        LoopMailActivity.this.setListAdapter(adapter);
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
    
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
    	Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight(); 
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView flow = (ScrollView) inflater.inflate(R.layout.mail_details_popup, null, false);
        LinearLayout contwrap = (LinearLayout) flow.findViewById(R.id.maildet_contwrap);
    	ProgressBar load = (ProgressBar) flow.findViewById(R.id.maildet_prog);
        
        LinearLayout wrapper = (LinearLayout) flow.findViewById(R.id.maildet_wrapper);
        
        final PopupWindow pw = new PopupWindow(flow, width-((int)(0.1*width)), height-((int)(0.4*height)), true);
        
        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        
        load.setVisibility(View.VISIBLE);
        contwrap.setVisibility(View.GONE);
        pw.showAtLocation(flow, Gravity.CENTER, 10, 10);
        
        MailEntry selected = (MailEntry) getListAdapter().getItem(position);
        ScrapeMailContentTask task = new ScrapeMailContentTask(flow, contwrap, load);
    	task.execute(selected);
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
	        
	        if(details.get(0).length() == 0) {
	        	((LinearLayout) to.getParent()).removeView(to);
	        }
	        
	        to.setText(Html.fromHtml(details.get(0)));
	        from.setText(Html.fromHtml(details.get(1)));
	        rest.setText(Html.fromHtml(details.get(2)));
	        
	        if(mailDetail.getContent().length() != 0)
	        	content.loadData(mailDetail.getContent(), "text/html", "UTF-8");
	        else ((LinearLayout) content.getParent()).removeView(content);

	        bar.setVisibility(View.GONE);
	        contwrap.setVisibility(View.VISIBLE);
		}
    };
}
