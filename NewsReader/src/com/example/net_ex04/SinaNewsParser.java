package com.example.net_ex04;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

class SinaNews{
	private String mTitle;
	private String mLink;
	private String mDscr;
	public void setTitle(String title){
		mTitle = title;
	}
	public String getTitle(){
		return mTitle.trim();
	}
	public void setLink(String link){
		mLink = link;
	}
	public String getLink(){
		return mLink;
	}
	public void setDscr(String dscr){
		mDscr = dscr;
	}
	public String getDscr(){
		return mDscr.trim();
	}
}
public class SinaNewsParser {
	private String mFilePath;
	//private List<SinaNews> sinaNewsArr = null;
	SinaNewsParser(String filePath){
		mFilePath = filePath;
	}
	public List<SinaNews> parser() throws Exception{
		List<SinaNews> sinaNewsArr = null;
		SinaNews sinaNews = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(mFilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "utf-8");
		int event = parser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch(event){
			case XmlPullParser.START_DOCUMENT:
				sinaNewsArr = new ArrayList<SinaNews>();
				break;
			case XmlPullParser.START_TAG:
				Log.i("SinaNewsParser", "XmlPullParser.START_TAG!!!" + parser.getName());
				if("item".equalsIgnoreCase(parser.getName())){
					sinaNews = new SinaNews();
				}else if(sinaNews != null){
					if("title".equalsIgnoreCase(parser.getName())){
						sinaNews.setTitle(parser.nextText());
					}else if("link".equalsIgnoreCase(parser.getName())){
						sinaNews.setLink(parser.nextText());
					}else if("description".equalsIgnoreCase(parser.getName())){
						sinaNews.setDscr(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if("item".equalsIgnoreCase(parser.getName())){
					sinaNewsArr.add(sinaNews);
					sinaNews = null;
				}
				break;
			default:
				break;
			}
			event = parser.next();
		}
		if(inputStream != null){
			inputStream.close();
		}
		return sinaNewsArr;
	}
}
