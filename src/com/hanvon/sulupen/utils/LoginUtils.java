package com.hanvon.sulupen.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;

import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.login.LoginActivity;
import com.hanvon.sulupen.net.JsonData;
import com.hanvon.sulupen.net.RequestResult;
import com.hanvon.sulupen.net.RequestServerData;


public class LoginUtils {
	
	private Context mContext;
	private int userflag;
	private String openid;
	private String figureurl;
	private String nickname;
	
	public LoginUtils(Context mcontext,int userflag){
		this.mContext = mcontext;
		this.userflag = userflag;
	}
	
	public void setOpenid(String openid){
		this.openid = openid;
	}
	
	public void setFigureurl(String url){
		this.figureurl = url;
	}
	
	public void setNickName(String nickname){
		this.nickname = nickname;
	}
	
	public void LoginToHvn(){
		new RequestTask(1).execute();
	}
	
	class RequestTask  extends AsyncTask<Void, Void, RequestResult>{	
		int flagTask;
        public RequestTask(int flagTask){
    	  this.flagTask=flagTask;
        }
  	  @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    	
	    }
		@Override
		protected RequestResult doInBackground(Void... arg0) {
			RequestResult result=null;
			if (flagTask == 1){
				result = getUserInfo();
			}else if(flagTask == 2){
		        result = registerToHvn();
			}
			return result;
		}
		 //响应结果
	    protected void onPostExecute(RequestResult result) {
	        JsonData data = result.getData();
	        String jsonCode= data.getJson();
	        try {
			    JSONObject json=new JSONObject(jsonCode);
			    LogUtil.i("flagTask:"+flagTask+"    json:"+json.toString());
			    if (flagTask == 1){
			    	if (json.getString("code").equals("0")){
			    		String qqNickname = json.getString("nickname");
			    		String username = json.getString("user");
			    		HanvonApplication.isActivity = true;
			            SharedPreferences mSharedPreferences=mContext.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
				        Editor mEditor=	mSharedPreferences.edit();
				        mEditor.putString("username", username);
				        HanvonApplication.hvnName = username;
				        if (qqNickname.equals("null")){
				        	qqNickname = "";
				        }
				        HanvonApplication.strName = qqNickname;
				        mEditor.putString("nickname", qqNickname);
				        mEditor.putString("figureurl", figureurl);
				        mEditor.putInt("flag", userflag);
				        mEditor.putInt("status", 1);
				        mEditor.commit();
				        
				        mContext.startActivity(new Intent(mContext, MainActivity.class));
		                LoginActivity.instance.finish();
			    	}else if (json.getString("code").equals("426")){
			    		new RequestTask(2).execute();
			    	}else if(json.getString("code").equals("520")){
			    		Toast.makeText(mContext, "服务器忙，请稍后重试", Toast.LENGTH_SHORT).show();
			    	 
			    	}else{
			    		Toast.makeText(mContext, "注册汉王云失败，请稍后重试", Toast.LENGTH_SHORT).show();
			    	  
			    	}
			    }else if (flagTask == 2){
			        if (json.getString("code").equals("0") || json.getString("code").equals("422")){
			    	    String qqName = json.getString("username");
			    	    HanvonApplication.isActivity = true;
			            SharedPreferences mSharedPreferences=mContext.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
				        Editor mEditor=	mSharedPreferences.edit();
				        HanvonApplication.hvnName = qqName;
				        HanvonApplication.strName = nickname;
				        mEditor.putString("username", qqName);
				        mEditor.putString("nickname", nickname);
				        mEditor.putString("figureurl", figureurl);
				        mEditor.putInt("flag", userflag);
				        mEditor.putInt("status", 1);
				        mEditor.commit();
				        
				        mContext.startActivity(new Intent(mContext, MainActivity.class));
		                LoginActivity.instance.finish();
			        }else{
			    	    Toast.makeText(mContext, "注册汉王云失败，请稍后重试", Toast.LENGTH_SHORT).show();
			    	   
			        }
			    }
		    } catch (JSONException e) {
			    e.printStackTrace();
		    }
	    }
    }
	
	 public RequestResult getUserInfo(){
	    JSONObject paramJson=new JSONObject();
	  	try {
	  		if (userflag == 1){
	    	    paramJson.put("user", "qq_"+SHA1Util.encodeBySHA(openid));
	  		}else if (userflag == 2){
	  			paramJson.put("user", "wx_"+SHA1Util.encodeBySHA(openid));
	  		}
	  	} catch (JSONException e) {
	  		e.printStackTrace();
	  	}

	  	LogUtil.i(paramJson.toString());
	  	RequestResult result=new RequestResult();
	 	result=RequestServerData.getUserInfo(paramJson);
	 	LogUtil.i(result.toString());
	  	return result;
	}

    public RequestResult registerToHvn(){
    	JSONObject paramJson=new JSONObject();
  	    try {
  		    paramJson.put("openId", openid);
		    paramJson.put("nickName", nickname);
  	    } catch (JSONException e) {
  		    e.printStackTrace();
  	    }

  	    LogUtil.i(paramJson.toString());
  	    RequestResult result=new RequestResult();
  	    if (userflag == 1){
 	        result=RequestServerData.QQuserToHvn(paramJson);
  	    }else if (userflag == 2){
  	    	result=RequestServerData.WXuserToHvn(paramJson);
  	    }
 	    LogUtil.i(result.toString());
  	    return result;
    }
}
