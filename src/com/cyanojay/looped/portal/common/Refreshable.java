package com.cyanojay.looped.portal.common;

import android.support.v4.app.FragmentManager;

public interface Refreshable {
	public void preRefresh();
	public void refresh(FragmentManager manager);
	public void postRefresh();
}
