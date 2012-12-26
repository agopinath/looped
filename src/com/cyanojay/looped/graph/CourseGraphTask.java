package com.cyanojay.looped.graph;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.cyanojay.looped.net.API;
import com.cyanojay.looped.portal.Course;
import com.cyanojay.looped.portal.GradeDetail;

public class CourseGraphTask extends AsyncTask<Void, Void, List<GradeDetail>> {
	private Context parent;
	private Course course;
	private ProgressDialog progressDialog;
	
	public CourseGraphTask(Context parent, Course course) {
		this.parent = parent;
		this.course = course;
	}
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        
        progressDialog = ProgressDialog.show(parent, "Looped", "Building graph...");
    }

    @Override
    protected List<GradeDetail> doInBackground(Void... args) {
    	try {
			return API.get().getGradeDetails(course);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
    }

    @Override
    protected void onPostExecute(List<GradeDetail> graphData) {
        super.onPostExecute(graphData);
        
        progressDialog.dismiss();
        
        Intent intent = new AverageTemperatureChart().execute(parent);
        parent.startActivity(intent);
    }
}
