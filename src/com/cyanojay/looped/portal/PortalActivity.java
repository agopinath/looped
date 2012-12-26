package com.cyanojay.looped.portal;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.cyanojay.looped.MainActivity;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;

public class PortalActivity extends SherlockFragmentActivity {
	public static Intent KEEP_ALIVE_TASK;
	
	private TabsAdapter adapter;
	private ViewPager mPager;
	
   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        
        setTitle(API.get().getPortalTitle()); 
        
        tabHost = (TabHost) getTabHost();
        
        setupTab("Grades", new Intent(this, GradesActivity.class));
        setupTab("Assignments", new Intent(this, AssignmentsActivity.class));
        setupTab("News", new Intent(this, NewsActivity.class));
        setupTab("Mail", new Intent(this, LoopMailActivity.class));
        
        KEEP_ALIVE_TASK = new Intent(this, KeepAliveService.class);
        startService(KEEP_ALIVE_TASK);
	}*/
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        
        mPager = new ViewPager(this);
        mPager.setId(R.id.pager);
        
        setContentView(mPager);
        
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        adapter = new TabsAdapter(this, mPager);
        
        setupTab(bar, "Grades", GradesActivity.class);
        setupTab(bar, "Assignments", TestFragment.class);
        setupTab(bar, "News", TestFragment.class);
        setupTab(bar, "Mail", TestFragment.class);
    }
    
	private void setupTab(ActionBar actionBar, String text, Class<?> fragmentClass) {
		adapter.addTab(actionBar.newTab().setText(text), fragmentClass, null);  
	}
	
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_portal, menu);
        return true;
    }*/
    
   /* @Override
    public void onBackPressed() {
    	logOut();
    }*/

	public static class TabsAdapter extends FragmentPagerAdapter implements
											ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getSupportActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
	
    private void logOut() {
    	if(Utils.isOnline(this)) {
    		API.get().logOut();
    	}
    	
    	Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
    	
    	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	intent.putExtra(MainActivity.IS_FROM_LOGOUT, true);
    	
    	startActivity(intent);
	}
        
    private void showAbout() {
    	Display display = getWindowManager().getDefaultDisplay(); 
	    int width = display.getWidth();
	    int height = display.getHeight(); 
	    
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
        LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.about_popup, null, false);
        TextView about = (TextView) flow.findViewById(R.id.about_text);
        
        about.setText(Html.fromHtml(getString(R.string.about_text)));
					        
    	final PopupWindow pw = new PopupWindow(flow, width-((int)(0.25*width)), LayoutParams.WRAP_CONTENT, true);
        
    	((Button) flow.findViewById(R.id.about_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        
        pw.showAtLocation(flow, Gravity.CENTER, 10, 10);
    }
}
