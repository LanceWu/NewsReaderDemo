package com.example.net_ex04;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class SinaNewsActivity extends FragmentActivity implements onPullRefreshListener{
	protected static final String TAG = "SinaNewsActivity";
	private TabHost myTabHost;
	private ViewPager viewPager;
	//private ListView listView;
	//List<SinaNews> sinaNewsArr;
	private PullToRefreshListView mListView = null;
	private List<Fragment> mFragments = new ArrayList<Fragment>();
	TabListener tabListener = new TabListener(){

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if(viewPager.getCurrentItem() != tab.getPosition()) {
				viewPager.setCurrentItem(tab.getPosition(),true);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.view_page_layout);
		
		//设置actionBar为tab风格
		final ActionBar bar = getActionBar();         
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);         
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		
		//viewPager设置适配器
		viewPager = (ViewPager)findViewById(R.id.viewPager);		
		FragmentManager fm = getSupportFragmentManager();
		mFragments.add(new MyFragment(MyFragment.SPORT_TYPE));
		mFragments.add(new MyFragment(MyFragment.TECH_TYPE));
		mFragments.add(new MyFragment(MyFragment.FINCE_TYPE));
		mFragments.add(new MyFragment(MyFragment.ENT_TYPE));
		viewPager.setAdapter(new MyViewPagerAdapter(fm, mFragments));
		viewPager.setOffscreenPageLimit(4);
		viewPager.setOnPageChangeListener(new OnPageChangeListener (){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				final ActionBar actionBar = getActionBar();
				actionBar.selectTab(actionBar.getTabAt(arg0));
			}
			
		});				
		
		//添加tab
		setupFragmentTab("体育");
		setupFragmentTab("科技");
		setupFragmentTab("财经");
		setupFragmentTab("娱乐");
	}
	
	private void setupFragmentTab(String string){
		Tab tab = getActionBar().newTab();
		tab.setText(string);
		tab.setTabListener(tabListener);
		getActionBar().addTab(tab);
	}	
	
    private class MyViewPagerAdapter extends FragmentPagerAdapter{

		public MyViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			// TODO Auto-generated constructor stub
			super(fm);
			mFragments = fragments;
			//super(activity.getFragmentManager());
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			if(mFragments != null){
				return mFragments.get(arg0);
			}
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mFragments.size();
		}
    	
    }

    //the following onPullRefreshListener for callback from MyFragment
	@Override
	public void onPullRefreshListener(String url, String fileName,
			PullToRefreshListView listView) {
		// TODO Auto-generated method stub
		mListView = listView;
		String path = getFilesDir().getAbsolutePath() + File.separator + fileName;
		new DownloadTask().execute(url, path);		
	}
	
	
	//put the download and parse work in AsyncTask
	class DownloadTask extends AsyncTask<String, Integer, String>{
		private static final int REQUEST_TIMEOUT = 10 * 1000;
		private static final int SO_TIMEOUT = 10 * 1000;
		List<SinaNews> sinaNewsArr = null;
    	@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result.equals("OK")){
		   		MyAdapter myAdapter = new MyAdapter(getLayoutInflater());
	    		mListView.setAdapter(myAdapter);
				mListView.invalidate();
				mListView.onRefreshComplete();
			}
			else{
				mListView.onRefreshComplete();
			}
		}

		public DownloadTask() {
    		// TODO Auto-generated constructor stub
    	}

    	@Override
    	protected String doInBackground(String... params) {
    		// TODO Auto-generated method stub
    		HttpGet httpRequest = new HttpGet(params[0]);
    		BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
    		HttpClient httpClient = new DefaultHttpClient(httpParams);
    		try{
    			HttpResponse httpResponse = httpClient.execute(httpRequest);
    			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
    			HttpEntity entity = httpResponse.getEntity();
    			InputStream inputStream = entity.getContent();
    			byte[] b = new byte[1024];
    			int readedLength = -1;

    			File file = new File(params[1]);
    			if(!file.exists())
    			{
    			   file.createNewFile();
    			}
    			OutputStream outputStream = new FileOutputStream(file);
    			while((readedLength = inputStream.read(b)) != -1){
    				outputStream.write(b, 0, readedLength);
    			}
    			
	    			//parse the rss
	    			SinaNewsParser parser= new SinaNewsParser(params[1]);
	        		try {
						sinaNewsArr = parser.parser();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		if(sinaNewsArr != null){
	        			return "OK";
	        		}
    			}
    		}catch(ClientProtocolException e){
    			e.printStackTrace();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
 
    		return null;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Integer... values){
    		//progressBar.setProgress(values[0]);
    		super.onProgressUpdate(values);
    	}
    	
    	public class MyAdapter extends BaseAdapter{
			private LayoutInflater mInflater;
			public MyAdapter(LayoutInflater inflater){
				this.mInflater = inflater;//LayoutInflater.from(context);
				//LayoutInflater LayoutInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				Log.i(TAG, "getCount() = " + sinaNewsArr.size());
				return sinaNewsArr == null? 0: sinaNewsArr.size() ;
				//return sinaNewsArr.size();			
			}
	
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return sinaNewsArr.get(position);
			}
	
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}
	
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view;
				if (convertView == null) {
		            view = mInflater.inflate(R.layout.sinanews_item_layout, parent, false);
		        } else {
		            view = convertView;
		        }
				TextView textView1 = (TextView)view.findViewById(R.id.title);
				textView1.setText(sinaNewsArr.get(position).getTitle());
				TextView textView2 = (TextView)view.findViewById(R.id.description);
				textView1.setText(sinaNewsArr.get(position).getDscr());
				return view;
			}
		
    	}
    	
    }
	
	
}



