package com.cyanojay.looped.portal;



import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;

public class BaseActivity extends SherlockActivity {
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_portal, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Utils.showAbout(this);
                return true;
            case R.id.menu_logout:
                Utils.logOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
