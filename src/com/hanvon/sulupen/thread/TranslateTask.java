package com.hanvon.sulupen.thread;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.hanvon.sulupen.net.JsonData;
import com.hanvon.sulupen.net.RequestResult;
import com.hanvon.sulupen.net.RequestServerData;
import com.hanvon.sulupen.datas.TransLateInfo;
import com.hanvon.sulupen.helper.PreferHelper;
import com.hanvon.sulupen.pinyin.PinyinIME;
import com.hanvon.sulupen.R;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.UiUtil;

/**
 * @desc 异步网络获取翻译结果类
 * @author  PengWenCai
 * @time 2015-6-25 上午11:51:25
 * @version
 */
public class TranslateTask extends AsyncTask<Void, Void, RequestResult> {
	private String fromString = "auto";
	private String toString = "auto";
	private String content = "";
	private TransLateInfo transLateInfo;
	private PinyinIME mPinyinIME;
	private Context context;
	private HashMap<String, String> transMap = new HashMap<String, String>();

	public TranslateTask(PinyinIME mPinyinIME) {
		this.context = mPinyinIME.getApplicationContext();
		this.mPinyinIME = mPinyinIME;
		transLateInfo = new TransLateInfo(mPinyinIME);
		PreferHelper.init(context);

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		fromString = PreferHelper.getString(TransLateInfo.Body.from, context
				.getResources().getString(R.string.auto));
		toString = PreferHelper.getString(TransLateInfo.Body.to, context
				.getResources().getString(R.string.auto));

	}

	@Override
	protected RequestResult doInBackground(Void... arg0) {

		content = mPinyinIME.getEditText();
		System.out.println("tong------------in doInBackground content:"+content);
		LogUtil.i(content);
		transMap = transLateInfo.putParams(content, fromString, toString);
		RequestResult result = new RequestResult();
		result = RequestServerData.translate(transMap);
		return result;
	}
	
	
	//加入判断网络是否连接
	public boolean isNetworkConnected(Context context) {  
	     if (context != null) {  
	         ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                 .getSystemService(Context.CONNECTIVITY_SERVICE);  
	         NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	         if (mNetworkInfo != null) {  
	             return mNetworkInfo.isAvailable();  
	         }  
	     }  
	     return false;  
	 }

	@Override
	protected void onPostExecute(RequestResult result) {
		//判断网络连接，如果网络没有连接给用户进行提示
		if (!isNetworkConnected(context)) {
			UiUtil.showToast(context,
					context.getResources().getString(R.string.translate_connect_failed));
			return;
		}
		if (result != null)
			LogUtil.i("tong-------------"+result.getData().getJson());
		if (("").equals(getTransResult(result))) {
			UiUtil.showToast(context,
					context.getResources().getString(R.string.translate_failed));
		} else {
			mPinyinIME.changeEditText(getTransResult(result));
		}

	}

	private String getTransResult(RequestResult result) {
		String resultStr = "";
		String transStr = "";
		resultStr = result.getData().getJson();
		if (resultStr.contains(TransLateInfo.Response.trans_result)) {
			ArrayList<JsonData> resultList = new ArrayList<JsonData>();
			resultList = result.getData()
					.getChild(TransLateInfo.Response.trans_result).getList();
			if (resultList.size() > 0) {
				for (int i = 0; i < resultList.size(); i++) {
					transStr += resultList.get(i).getValues().get("dst");
				}
			}

			return transStr;
		} else if (resultStr.contains(TransLateInfo.Response.error_code)) {

			return "";
		}
		return "";
	}
}