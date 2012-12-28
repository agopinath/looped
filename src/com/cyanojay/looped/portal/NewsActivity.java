package com.cyanojay.looped.portal;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.cyanojay.looped.R;
import com.cyanojay.looped.Utils;
import com.cyanojay.looped.net.API;

public class NewsActivity extends SherlockListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ScrapeNewsTask task = new ScrapeNewsTask();
        task.execute();
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_news, container, false);

		return view;
	}
    
    private class ScrapeNewsTask extends AsyncTask<String, Void, List<NewsArticle>> {
    	@Override
		protected List<NewsArticle> doInBackground(String... params) {
			return API.get().getNews();
		}
		
		@Override
	    protected void onPostExecute(List<NewsArticle> result) {
	        super.onPostExecute(result);
	        
	        NewsArticle[] values = result.toArray(new NewsArticle[result.size()]);
	        NewsAdapter adapter = new NewsAdapter(getSherlockActivity(), values);
	        
	        ListView listView = (ListView) getView().findViewById(android.R.id.list);
	        
	        listView.setOnItemClickListener(new NewsItemClickAdapter(adapter, getSherlockActivity()));
	        
	        listView.setAdapter(adapter);
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
    		  
    		  if(article.getDisplayAuthor() == null)
    			  articleAuthor.setText(Html.fromHtml("<b>" + article.getAuthor() + "</b>" +
    				  							  " - " + article.getAuthorType()));
    		  else
    			  articleAuthor.setText(Html.fromHtml("<b>" + article.getDisplayAuthor() + "</b>"));
    		  
    		  articleDate.setText(article.getDatePosted());
    		  
    		  return rowView;
    	} 
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

    private class NewsItemClickAdapter implements AdapterView.OnItemClickListener {
    	NewsAdapter adapter;
    	Activity parent;
    	
    	public NewsItemClickAdapter(NewsAdapter adapter, Activity parent) {
    		this.adapter = adapter;
    		this.parent = parent;
    	}

		@Override
		public void onItemClick(AdapterView<?> list, View view, int position, long id) {
			if(!Utils.isOnline(parent)) {
	    		Toast.makeText(parent, "Internet connectivity is lost. Please re-connect and try again.", Toast.LENGTH_LONG).show();
	    		return;
	    	}
	    	
	        Display display = parent.getWindowManager().getDefaultDisplay(); 
	        int width = display.getWidth();
	        
	        LayoutInflater inflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        LinearLayout flow = (LinearLayout) inflater.inflate(R.layout.news_details_popup, null, false);
	        LinearLayout contwrap = (LinearLayout) flow.findViewById(R.id.newsdet_contwrap);
	    	ProgressBar load = (ProgressBar) flow.findViewById(R.id.popup_prog);
	        
	        final PopupWindow pw = new PopupWindow(flow, width-((int)(0.1*width)), LayoutParams.WRAP_CONTENT, true);
	        
	        ((Button) flow.findViewById(R.id.exit_btn)).setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                pw.dismiss();
	            }
	        });
	        
	        load.setVisibility(View.VISIBLE);
	        contwrap.setVisibility(View.GONE);
	        
	        pw.showAtLocation(flow, Gravity.CENTER, 0, 0);
	        
	        NewsArticle selected = (NewsArticle) adapter.getItem(position);
	    	ScrapeNewsDetailsTask task = new ScrapeNewsDetailsTask(flow, contwrap, load);
	    	task.execute(selected);
		}
    }
}
