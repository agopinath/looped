package com.cyanojay.looped.portal;

import java.io.IOException;
import java.util.List;

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
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
    protected void onListItemClick(ListView list, View view, int position, long id) {
        Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight(); 
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView flow = (ScrollView) inflater.inflate(R.layout.news_details_popup, null, false);
        LinearLayout contwrap = (LinearLayout) flow.findViewById(R.id.newsdet_contwrap);
    	ProgressBar load = (ProgressBar) flow.findViewById(R.id.newsdet_prog);
        
        LinearLayout wrapper = (LinearLayout) flow.findViewById(R.id.newsdet_wrapper);
        
        final PopupWindow pw = new PopupWindow(flow, width-((int)(0.1*width)), LayoutParams.WRAP_CONTENT, true);
        
        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        
        load.setVisibility(View.VISIBLE);
        contwrap.setVisibility(View.GONE);
        
        pw.showAtLocation(flow, Gravity.CENTER, 0, 0);
        
        NewsArticle selected = (NewsArticle) getListAdapter().getItem(position);
    	ScrapeNewsDetailsTask task = new ScrapeNewsDetailsTask(flow, contwrap, load);
    	task.execute(selected);
    }
    
    private class ScrapeNewsDetailsTask extends AsyncTask<NewsArticle, Void, NewsDetail> {
    	private View flow;
    	private View contwrap;
    	private ProgressBar bar;
    	
    	public ScrapeNewsDetailsTask(View flow, View contwrap, ProgressBar bar) {
    		this.flow = flow;
    		this.bar = bar;
    		this.contwrap = contwrap;
    	}
    	
		@Override
		protected NewsDetail doInBackground(NewsArticle... params) {
			try {
				return API.get().getNewsDetails(params[0]);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
	    protected void onPostExecute(NewsDetail newsDetail) {
	        super.onPostExecute(newsDetail);

	        TextView title = (TextView) flow.findViewById(R.id.newsdet_title);
	        TextView info = (TextView) flow.findViewById(R.id.newsdet_info);
	        WebView content = (WebView) flow.findViewById(R.id.newsdet_content);
	        
	        title.setText(newsDetail.getTitle());
	        
	        if(newsDetail.getContent().length() != 0)
	        	content.loadDataWithBaseURL(null, newsDetail.getContent(), "text/html", "UTF-8", null);
	        else ((LinearLayout) content.getParent()).removeView(content);
	        	
	        String infoStr = "";
	        for(String detail : newsDetail.getDetails()) {
	        	String parts[] = detail.split(":");
        		infoStr += "<b>" + parts[0] + "</b>: " + parts[1] + "<br />";
	        }
	        
	        info.setText(Html.fromHtml(infoStr));
	        bar.setVisibility(View.GONE);
	        
	        contwrap.setVisibility(View.VISIBLE);
		}
    };
}
