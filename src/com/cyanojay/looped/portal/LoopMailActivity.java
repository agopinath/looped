package com.cyanojay.looped.portal;

import java.io.IOException;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        // TODO: retrieve the corresponding MailDetail info for the MailEntry selected and display it through a popup
    }
}
