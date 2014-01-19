package com.cyanojay.looped.portal.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.cyanojay.looped.R;

public class OtherFragment extends SherlockFragment {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_other, container, false);
		
		/*getView().findViewById(R.id.rate_btn).setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View arg0) {
	        	System.out.println("RATE CLICKED");
	        }
	     });*/
		
		return view;
	}
}
