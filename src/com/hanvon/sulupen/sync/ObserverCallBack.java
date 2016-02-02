package com.hanvon.sulupen.sync;

import org.json.JSONException;

public interface ObserverCallBack {

	public void back(String data, int type,int total) throws JSONException;
}
