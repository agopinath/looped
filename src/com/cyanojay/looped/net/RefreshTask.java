package com.cyanojay.looped.net;

import android.os.AsyncTask;

public class RefreshTask extends AsyncTask<Void, Void, Void> {
	private Runnable toRunBefore;
	private Runnable toRunAfter;
	
	public RefreshTask(Runnable toRunBefore, Runnable toRunAfter) {
		this.toRunBefore = toRunBefore;
		this.toRunAfter = toRunAfter;
	}
	
	@Override
	protected Void doInBackground(Void... noArgs) {
		toRunBefore.run();
		
		return null;
	}
	
	@Override
    protected void onPostExecute(Void noResults) {
        super.onPostExecute(noResults);
        
        toRunAfter.run();
    }
}
