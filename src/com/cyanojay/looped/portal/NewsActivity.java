package com.cyanojay.looped.portal;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cyanojay.looped.API;
import com.cyanojay.looped.R;

public class NewsActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        
        ScrapeNewsTask task = new ScrapeNewsTask();
        task.execute();
    }
    
    private class ScrapeNewsTask extends AsyncTask<String, Void, List<NewsArticle>> {
    	@Override
		protected List<NewsArticle> doInBackground(String... params) {
			return API.get().getNews();
		}
		
		@Override
	    protected void onPostExecute(List<NewsArticle> result) {
	        super.onPostExecute(result);
	        
	        NewsArticle[] values = result.toArray(new NewsArticle[0]);
	        NewsAdapter adapter = new NewsAdapter(NewsActivity.this, values);
	        
	        NewsActivity.this.setListAdapter(adapter);
		}
    };
    
    private class NewsAdapter extends ArrayAdapter<NewsArticle> {
    	  private final Context context;
    	  private final NewsArticle[] values;
    	  
    	  public NewsAdapter(Context context, NewsArticle[] values) {
    		  super(context, R.layout.curr_news_row, values);
    		  this.context = context;
    		  this.values = values;
    	  }

    	  @Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    		  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		  View rowView = inflater.inflate(R.layout.curr_news_row, parent, false);
    		  
    		  TextView articleName = (TextView) rowView.findViewById(R.id.news_post_title);
    		  TextView articleAuthor = (TextView) rowView.findViewById(R.id.news_post_author);
    		  TextView articleDate = (TextView) rowView.findViewById(R.id.news_post_date);
    		  
    		  NewsArticle article = values[position];
    		  
    		  articleName.setText(article.getArticleName());
    		  articleAuthor.setText(Html.fromHtml("<b>" + article.getAuthor() + "</b>" +
    				  							  " - " + article.getAuthorType()));
    		  articleDate.setText(article.getDatePosted());
    		  
    		  return rowView;
    	} 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_news, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
    	NewsArticle selected = (NewsArticle) getListAdapter().getItem(position);
    	Toast.makeText(this, selected.getArticleName() + " selected", Toast.LENGTH_SHORT).show();
    	
    	ScrapeNewsDetailsTask task = new ScrapeNewsDetailsTask();
    	task.execute(selected);
    }
    
    private class ScrapeNewsDetailsTask extends AsyncTask<NewsArticle, Void, NewsDetail> {
		@Override
		protected NewsDetail doInBackground(NewsArticle... params) {
			return API.get().getNewsDetails(params[0]);
		}
		
		@Override
	    protected void onPostExecute(NewsDetail newsDetail) {
	        super.onPostExecute(newsDetail);
	        
	        // TODO: show popup window containing HTML-formatted content and relevant details of a news article
		}
    };
}
