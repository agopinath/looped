package com.cyanojay.schoolloopd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class LogInTask extends AsyncTask<String, String, Void> {

	ProgressDialog progressDialog;
	private String username;
	private String pass;
	private String loginUrl;
	private Activity parent;
	
	public LogInTask(String username, String password, String loginUrl, Activity parent) {
		this.username = username;
		this.pass = password;
		this.loginUrl = loginUrl;
		this.parent = parent;
	}

	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(parent, "SchoolLoop'd", "Logging in...");
    }

    @Override
    protected Void doInBackground(String... arg0) {
		return null; 
    }

    @Override
    protected void onProgressUpdate(String...values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
    }
}
