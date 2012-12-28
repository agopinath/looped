package com.cyanojay.looped.portal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.cyanojay.looped.R;

public class TestFragment extends SherlockFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab_list, null);
		String text = "" + Math.random();
		((TextView) view.findViewById(R.id.tabby_text)).setText(text);

		return view;
	}
	
	public static Bundle createBundle(String title) {
        Bundle bundle = new Bundle();
        return bundle;
    }
}
