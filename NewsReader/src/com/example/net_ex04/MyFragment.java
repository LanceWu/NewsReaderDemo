package com.example.net_ex04;

import java.io.File;
import java.util.List;

import com.example.net_ex04.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyFragment extends Fragment{
	protected static final String TAG = "MyFragment";
	protected static final int SPORT_TYPE = 100;
	protected static final int TECH_TYPE = 101;
	protected static final int FINCE_TYPE = 102;
	protected static final int ENT_TYPE =103;
	protected static final String SPRTS = "http://rss.sina.com.cn/roll/sports/hot_roll.xml";
	protected static final String TECH = "http://rss.sina.com.cn/tech/rollnews.xml";
	protected static final String FINANCE ="http://rss.sina.com.cn/roll/finance/hot_roll.xml";
	protected static final String ENT = "http://rss.sina.com.cn/ent/hot_roll.xml";
	//List<SinaNews> sinaNewsArr;
	private PullToRefreshListView listView;
	private onPullRefreshListener mListener;
	private int mType;
	public MyFragment(int type){
		mType = type;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_layout, container, false);
		listView = (PullToRefreshListView)root.findViewById(R.id.listView);
		listView.setOnRefreshListener(new OnRefreshListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				switch(mType){
				case SPORT_TYPE:
					mListener.onPullRefreshListener(SPRTS, "sport.xml", listView);
					break;
				case TECH_TYPE:
					mListener.onPullRefreshListener(TECH, "tech.xml", listView);
					break;
				case FINCE_TYPE:
					mListener.onPullRefreshListener(FINANCE, "finace.xml", listView);
					break;
				case ENT_TYPE:
					mListener.onPullRefreshListener(ENT, "ent.xml", listView);
				default:
					break;
				}

			}
			
		});
		listView.setAdapter(null);
		Log.i(TAG, "!!! onCreateView() !!!" + mType);
		return root;

    }
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mListener = (onPullRefreshListener) activity;
	}
}
