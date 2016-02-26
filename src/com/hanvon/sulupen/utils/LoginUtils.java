package com.hanvon.sulupen.utils;

import java.io.File;

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
	
	private String qqOpenId = "";
	private String wxOpenId = "";
	private String wbOpenId = "";
	
	
	private int accountFlag = 0;  //0 主账户  1 从账户qq 2 从账户 wx 3 从账户微博
	private int accountCount = 0;
	
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
			}else if (flagTask == 3){
				result = UploadDeviceStat();
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
			    		if (accountFlag == 0){
			    		    String nickname = json.getString("nickname");
			    		    String username = json.getString("user");
			    		    HanvonApplication.isActivity = true;
			                SharedPreferences mSharedPreferences=mContext.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
				            Editor mEditor=	mSharedPreferences.edit();
				            mEditor.putString("username", username);
				            mEditor.putBoolean("isActivity", HanvonApplication.isActivity);
				            HanvonApplication.hvnName = username;
				            if (nickname.equals("null")){
				        	    nickname = "";
				            }
				            qqOpenId = json.getString("qqOpenId");
				            wxOpenId = json.getString("wxOpenId");
				            wbOpenId = json.getString("wbOpenId");
				            if (qqOpenId.endsWith("null")){
				        	    qqOpenId = "";
				            }else{
				        	    accountFlag = 1;
				        	    accountCount++;
				        	    new RequestTask(1).execute();
				            }
				            if (wxOpenId.equals("null")){
				        	    wxOpenId = "";
				            }else{
				        	    accountFlag = 2;
				        	    accountCount++;
				        	    new RequestTask(1).execute();
				            }
				            if (wbOpenId.equals("null")){
				        	    wbOpenId = "";
				            }else{
				        	    accountFlag = 3;
				        	    accountCount++;
				        	    new RequestTask(1).execute();
				            }
				        
				            HanvonApplication.strName = nickname;
				            mEditor.putString("nickname", nickname);
				            mEditor.putString("figureurl", figureurl);
				            mEditor.putString("qqOpenId", qqOpenId);
				            mEditor.putString("wxOpenId", wxOpenId);
				            mEditor.putString("wbOpenId", wbOpenId);
				            mEditor.putString("qqNickname", "");
				            mEditor.putString("wxNickname", "");
				            mEditor.putString("wbNickname", "");
				            mEditor.putInt("flag", userflag);
				            mEditor.putInt("status", 1);
				            mEditor.commit();
				            String outputDirectory = "/data/data/com.hanvon.sulupen/users/"+username+"/";
							File file = new File(outputDirectory);
							// 创建用户在sdcard上的目录
							if (!file.exists()) {
							    file.mkdirs();
							}
			    		}else if (accountFlag == 1){
			    			String nickname = json.getString("nickname");
			    			SharedPreferences mSharedPreferences=mContext.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
				            Editor mEditor=	mSharedPreferences.edit();
				            mEditor.putString("qqNickname", nickname);
				            mEditor.commit();
			    			accountCount--;
			    		}else if (accountFlag == 2){
			    			String nickname = json.getString("nickname");
			    			SharedPreferences mSharedPreferences=mContext.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
				            Editor mEditor=	mSharedPreferences.edit();
				            mEditor.putString("wxNickname", nickname);
				            mEditor.commit();
			    			accountCount--;
			    		}else if (accountFlag == 3){
			    			String nickname = json.getString("nickname");
			    			SharedPreferences mSharedPreferences=mContext.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
				            Editor mEditor=	mSharedPreferences.edit();
				            mEditor.putString("wbNickname", nickname);
				            mEditor.commit();
			    			accountCount--;
			    		}
				        if (accountCount == 0){
				            mContext.startActivity(new Intent(mContext, MainActivity.class));
		                    LoginActivity.instance.finish();
				        }
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
				        mEditor.putBoolean("isActivity", HanvonApplication.isActivity);
				        mEditor.putString("nickname", nickname);
				        mEditor.putString("figureurl", figureurl);
				        mEditor.putString("qqOpenId", "");
				        mEditor.putString("wxOpenId", "");
				        mEditor.putString("wbOpenId", "");
				        mEditor.putString("qqNickname", "");
			            mEditor.putString("wxNickname", "");
			            mEditor.putString("wbNickname", "");
				        mEditor.putInt("flag", userflag);
				        mEditor.putInt("status", 1);
				        mEditor.commit();
				        
				        String outputDirectory = "/data/data/com.hanvon.sulupen/users/"+qqName+"/";
						File file = new File(outputDirectory);
						// 创建用户在sdcard上的目录
						if (!file.exists()) {
						    file.mkdirs();
						}
						
						new RequestTask(3).execute();
			        }else{
			    	    Toast.makeText(mContext, "注册汉王云失败，请稍后重试", Toast.LENGTH_SHORT).show();
			    	   
			        }
			    }else if (flagTask == 3){
			    	mContext.startActivity(new Intent(mContext, MainActivity.class));
	                LoginActivity.instance.finish();
			    }
		    } catch (JSONException e) {
			    e.printStackTrace();
		    }
	    }
    }
	
	 public RequestResult getUserInfo(){
	    JSONObject paramJson=new JSONObject();
	  	try {
	  		if (accountFlag == 0){
	  		    if (userflag == 1){
	    	        paramJson.put("user", "qq_"+SHA1Util.encodeBySHA(openid));
	  		    }else if (userflag == 2){
	  			    paramJson.put("user", "wx_"+SHA1Util.encodeBySHA(openid));
	  		    }
	  		}else if (accountFlag == 1){
	  			paramJson.put("user", "qq_"+SHA1Util.encodeBySHA(qqOpenId));
	  		}else if (accountFlag == 2){
	  			paramJson.put("user", "wx_"+SHA1Util.encodeBySHA(wxOpenId));
	  		}else if (accountFlag == 3){
	  			paramJson.put("user", "wb_"+SHA1Util.encodeBySHA(wbOpenId));
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
  	    	paramJson.put("devid", HanvonApplication.AppDeviceId);
  		    paramJson.put("openId", openid);
		    paramJson.put("nickName", nickname);
		    paramJson = StatisticsUtils.StatisticsJson(paramJson);
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
    
    public RequestResult UploadDeviceStat(){

	    JSONObject devinfo = new JSONObject();
	  	try {
	  	   	devinfo.put("userid", HanvonApplication.hvnName);
	  	   	devinfo.put("devid", HanvonApplication.AppDeviceId);
	  	   	devinfo.put("devModel", "Android");
	  	   	devinfo.put("softName", HanvonApplication.AppSid);
	  	   	devinfo.put("osName", android.os.Build.MODEL);
	  	   	devinfo.put("osVer",android.os.Build.VERSION.RELEASE);
	  	   	devinfo.put("softVer", HanvonApplication.AppVer);
	  	   	devinfo.put("longitude", HanvonApplication.curLongitude);
	  	   	devinfo.put("latitude", HanvonApplication.curLatitude);
	  	   	devinfo.put("locationCountry", HanvonApplication.curCountry);
	  	   	devinfo.put("locationProvince", HanvonApplication.curProvince);
	  	   	devinfo.put("locationCity", HanvonApplication.curCity);
	  	   	devinfo.put("locationArea", HanvonApplication.curDistrict);
	  	} catch (JSONException e) {
	  	    e.printStackTrace();
	  	}

	  	LogUtil.i(devinfo.toString());
	  	RequestResult result=new RequestResult();
	  	result=RequestServerData.deviceStatUpload(devinfo);
	  	return result;
	}
}
