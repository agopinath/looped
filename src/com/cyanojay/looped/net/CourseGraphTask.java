package com.cyanojay.looped.net;

import android.content.Context;
import android.os.AsyncTask;

import com.cyanojay.looped.portal.Course;

public class CourseGraphTask extends AsyncTask<String, String, Boolean> {
	private Context parent;
	private Course course;

	public CourseGraphTask(Context parent, Course course) {
		this.parent = parent;
		this.course = course;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		
		return null;
	}
}
