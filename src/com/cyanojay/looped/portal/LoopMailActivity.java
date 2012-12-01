package com.cyanojay.looped.portal;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cyanojay.looped.API;
import com.cyanojay.looped.R;

public class LoopMailActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop_mail);
        
        ScrapeLoopMailTask task = new ScrapeLoopMailTask();
        task.execute("inbox");
    }
    
    private class ScrapeLoopMailTask extends AsyncTask<String, Void, List<MailEntry>> {
    	@Override
		protected List<MailEntry> doInBackground(String... params) {
			if(params[0].equals("inbox")) {
				return API.get().getInbox();
			} else {
				// TODO: handle the case if user doesn't request for inbox
				return null;
			}
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
    		  // TODO: return an appropriate list view using MailEntry info
    		  
    		  return null;
    	} 
    }
    
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        // TODO: retrieve the corresponding MailDetail info for the MailEntry selected and display it through a popup
    }
}
