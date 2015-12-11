package com.hanvon.sulupen.utils;

import org.json.JSONObject;

public class HvnHttpReauest {

	public static void Request(JSONObject jsonStr,String url){
	//	ThreadPoolUtils.execute(new MyRunnable(jsonStr,url));
	}
	
	class MyRunnable implements Runnable{
		final JSONObject jsonstr;
		final String url;
	public MyRunnable(final JSONObject jsonstr,final String url){
         this.jsonstr = jsonstr;
         this.url = url;
	}
    @Override
    public void run() {
    	String data = null;
        }
	}
}
