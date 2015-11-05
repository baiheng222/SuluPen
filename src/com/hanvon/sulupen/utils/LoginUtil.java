package com.hanvon.sulupen.utils;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.login.LoginActivity;
import com.hanvon.sulupen.net.JsonData;
import com.hanvon.sulupen.net.RequestResult;
import com.hanvon.sulupen.net.RequestServerData;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;


public class LoginUtil {
	public Activity mActivity;
	public Context mContext;
	private UserInfo mInfo;
	private ProgressDialog pd1;
	private String openid;
	private String nickname;
	private String figureurl;
	
	private int flag;

	public LoginUtil(Activity mActivity1,Context mContext1) {
		this.mActivity = mActivity1;
		this.mContext = mContext1;
	}

	public void QQLogin(ProgressDialog pd,int type)
	{
		pd1 = pd;
		flag = type;
		QQLoginOut();
	    if (!HanvonApplication.mTencent.isSessionValid())
	    {
	    	HanvonApplication.mTencent.login(mActivity, "all", loginListener);
	    }
	} 

	IUiListener loginListener = new BaseUiListener() {
		
    };

	public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString("access_token");
            String expires = jsonObject.getString("expires_in");
            String openId = jsonObject.getString("openid");
            LogUtil.i("access_token:"+token);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
            	HanvonApplication.mTencent.setAccessToken(token, expires);
            	HanvonApplication.mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }

	public void onCancel() {
		// TODO Auto-generated method stub
	}

	public void onError(UiError arg0) {
		// TODO Auto-generated method stub
	}
	
	class BaseUiListener implements IUiListener {
		@Override
		public void onComplete(Object response) {
	        if (null != response) {
	        	pd1.dismiss();
	        	LogUtil.i(response.toString());
	        	JSONObject json = (JSONObject)response;
	        	try {
					openid = json.getString("openid");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	        	pd1 = ProgressDialog.show(mContext, "", "正在获取用户资料.......");
	        	updateUserInfo();
	        }
	        JSONObject jsonResponse = (JSONObject) response;
	        if (null != jsonResponse && jsonResponse.length() == 0) {
	        	pd1.dismiss();
	            return;
	        }
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub	
			pd1.dismiss();
			LogUtil.i("Into LoginUtil onCancel************");
		}

		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub
			pd1.dismiss();
			LogUtil.i("Into LoginUtil onError************"+arg0.errorCode);
		}
	}

	private void updateUserInfo() {
		if (HanvonApplication.mTencent != null && HanvonApplication.mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				@Override
				public void onError(UiError e) {
					pd1.dismiss();
				}

				@Override
				public void onComplete(final Object response) {
					new Thread(){
						@Override
						public void run() {
						try {
								JSONObject json = (JSONObject)response;
								nickname=json.getString("nickname");
								figureurl=json.getString("figureurl_qq_1");
								if ((openid != null) && (nickname != null)){
									new RequestTask(1).execute();
								}
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
					}
					}.start();
				}
				@Override
				public void onCancel() {
					pd1.dismiss();
				}
			};
			mInfo = new UserInfo(mActivity, HanvonApplication.mTencent.getQQToken());
			mInfo.getUserInfo(listener);
		} else {
		}
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
				        mEditor.putString("nickname", qqNickname);
				        mEditor.putString("figureurl", figureurl);
				        mEditor.putInt("flag", 1);
				        mEditor.putInt("status", 1);
				        mEditor.commit();
				        
				        SharedPreferences mSharedCloudPreferences=mContext.getSharedPreferences("Cloud_Info", Activity.MODE_MULTI_PROCESS);
						Editor mCloudEditor = mSharedCloudPreferences.edit();
						mCloudEditor.putString("cloudname", qqNickname);
						mCloudEditor.putInt("cloudtype", 1);
					    HanvonApplication.cloudType = 1;
					    mCloudEditor.commit();

				        if (flag == 0){
				            mContext.startActivity(new Intent(mActivity, MainActivity.class));
				        }else if (flag == 1){
				        //	mContext.startActivity(new Intent(mActivity, MyCloudMsg.class));
				        }else if (flag == 2){
				        //	mContext.startActivity(new Intent(mActivity, ScanNoteDetailActivity.class));
				        }
		                pd1.dismiss();
		                LoginActivity.instance.finish();
			    	}else if (json.getString("code").equals("426")){
			    		new RequestTask(2).execute();
			    	}else if(json.getString("code").equals("520")){
			    		Toast.makeText(mContext, "服务器忙，请稍后重试", Toast.LENGTH_SHORT).show();
			    	    pd1.dismiss();
			    	}else{
			    		Toast.makeText(mContext, "注册汉王云失败，请稍后重试", Toast.LENGTH_SHORT).show();
			    	    pd1.dismiss();
			    	}
			    }else if (flagTask == 2){
			        if (json.getString("code").equals("0") || json.getString("code").equals("422")){
			    	    String qqName = json.getString("username");
			    	    HanvonApplication.isActivity = true;
			            SharedPreferences mSharedPreferences=mContext.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
				        Editor mEditor=	mSharedPreferences.edit();
				        mEditor.putString("username", qqName);
				        mEditor.putString("nickname", nickname);
				        mEditor.putString("figureurl", figureurl);
				        mEditor.putInt("flag", 1);
				        mEditor.putInt("status", 1);
				        mEditor.commit();
				        
				        SharedPreferences mSharedCloudPreferences=mContext.getSharedPreferences("Cloud_Info", Activity.MODE_MULTI_PROCESS);
						Editor mCloudEditor = mSharedCloudPreferences.edit();
						mCloudEditor.putString("cloudname", nickname);
						mCloudEditor.putInt("cloudtype", 1);
					    HanvonApplication.cloudType = 1;
					    mCloudEditor.commit();
					    
				        if (flag == 0){
				            mContext.startActivity(new Intent(mActivity, MainActivity.class));
				        }else if (flag == 1){
				        //	mContext.startActivity(new Intent(mActivity, MyCloudMsg.class));
				        }else if (flag == 2){
				        //	mContext.startActivity(new Intent(mActivity, ScanNoteDetailActivity.class));
				        }
		                pd1.dismiss();
		                LoginActivity.instance.finish();
			        }else{
			    	    Toast.makeText(mContext, "注册汉王云失败，请稍后重试", Toast.LENGTH_SHORT).show();
			    	    pd1.dismiss();
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
	    	paramJson.put("user", "qq_"+SHA1Util.encodeBySHA(openid));
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
 	    result=RequestServerData.QQuserToHvn(paramJson);
 	    LogUtil.i(result.toString());
  	    return result;
    }

	public void QQLoginOut(){
		HanvonApplication.mTencent.logout(mContext);
		HanvonApplication.isActivity = false;
	}
}

