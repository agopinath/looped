package com.cyanojay.looped.portal;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import com.cyanojay.looped.R;

public class GradesActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);
        
        ScrapeGradesTask task = new ScrapeGradesTask();
        task.execute();
    }

    private class ScrapeGradesTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			return null;
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
		}
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_grades, menu);
        return true;
    }
}
