package com.hanvon.sulupen.wxapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.login.LoginActivity;
import com.hanvon.sulupen.net.JsonData;
import com.hanvon.sulupen.net.RequestResult;
import com.hanvon.sulupen.net.RequestServerData;
import com.hanvon.sulupen.utils.HttpsClient;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.SHA1Util;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;


import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

/** 微信客户端回调activity示例 */
public class WXEntryActivity extends WechatHandlerActivity implements IWXAPIEventHandler{

	/**
	 * 处理微信发出的向第三方应用请求app message
	 * <p>
	 * 在微信客户端中的聊天页面有“添加工具”，可以将本应用的图标添加到其中
	 * 此后点击图标，下面的代码会被执行。Demo仅仅只是打开自己而已，但你可
	 * 做点其他的事情，包括根本不打开任何页面
	 */
	public void onGetMessageFromWXReq(WXMediaMessage msg) {
		Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage(getPackageName());
		startActivity(iLaunchMyself);
	}

	/**
	 * 处理微信向第三方应用发起的消息
	 * <p>
	 * 此处用来接收从微信发送过来的消息，比方说本demo在wechatpage里面分享
	 * 应用时可以不分享应用文件，而分享一段应用的自定义信息。接受方的微信
	 * 客户端会通过这个方法，将这个信息发送回接收方手机上的本demo中，当作
	 * 回调。
	 * <p>
	 * 本Demo只是将信息展示出来，但你可做点其他的事情，而不仅仅只是Toast
	 */
	public void onShowMessageFromWXReq(WXMediaMessage msg) {
		if (msg != null && msg.mediaObject != null
				&& (msg.mediaObject instanceof WXAppExtendObject)) {
			WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
			Toast.makeText(this, obj.extInfo, Toast.LENGTH_SHORT).show();
		}
	}

//}


//public class WXEntryActivity extends Activity { 
	//private ProgressDialog pd1;
	private String openid;
	private String wxName;
	private String figureurl;
	
	private char flag;   // 0 从登陆界面跳转 1 从云信息登陆跳转  2 从上传界面跳转
	@Override  
	protected void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);   
	    HanvonApplication.api.handleIntent(getIntent(), this); 
	}  

	@Override  
	public void onReq(BaseReq arg0) {  
	    // TODO Auto-generated method stub  
	}  

	@Override  
	public void onResp(BaseResp resp) {
	    SendAuth.Resp rep = (SendAuth.Resp) resp;
	    String code = rep.code;
	    LogUtil.i("code:"+rep.code+"   openId:"+rep.openId+"   url:"+rep.url+"  rep.toString:"+rep.toString());
	    switch (resp.errCode) {
	        case BaseResp.ErrCode.ERR_OK: 
	        	flag = rep.state.charAt(rep.state.length()-1);
	        	getAccessToken(code);
	            break;
	        default:
	        	Toast.makeText(WXEntryActivity.this, "微信认证失败", Toast.LENGTH_SHORT).show();
	        	 LoginActivity.instance.finish();
	        	startActivity(new Intent(WXEntryActivity.this, LoginActivity.class));
	        	WXEntryActivity.this.finish();
	            break;
	    }  
	} 

	public void getAccessToken(String code){
	    final List<NameValuePair> parameters=new ArrayList<NameValuePair>();
	    parameters.add(new BasicNameValuePair("appid", "wxdf64ce17dae09860"));
	 	parameters.add(new BasicNameValuePair("secret", "fb726caee828eb656dba6cd4ba30da04"));
	 	parameters.add(new BasicNameValuePair("code", code));
	 	parameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
	    final String url = "https://api.weixin.qq.com/sns/oauth2/access_token?";

	 //	pd1 = ProgressDialog.show(WXEntryActivity.this, "", "正在获取用户资料.......");
	 	new Thread() {
	 		@Override
	 		public void run() {
	 			HttpsClient httpsClient = new HttpsClient();
	 			String result = httpsClient.HttpsRequest(url, parameters);
	 			if (result == null){
	 				Toast.makeText(WXEntryActivity.this, "获取用户凭证失败，请稍后重试", Toast.LENGTH_SHORT).show();
	 				//pd1.dismiss();
	 				 LoginActivity.instance.finish();
	 				startActivity(new Intent(WXEntryActivity.this, LoginActivity.class));
	 				WXEntryActivity.this.finish();
	 				return;
	 			}
	 			LogUtil.i(result);

	 			JSONObject json = null;
	 			String accessToken = null;
				try {
					json = new JSONObject(result);
					accessToken = json.getString("access_token");
					openid = json.getString("openid");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LogUtil.i("accessToken:"+accessToken+"\r\nopenid:"+openid);
				getUserInfo(accessToken,openid);
	 		}
	 	}.start();
	 }
	    
	public void getUserInfo(String accessToken,final String openid){
	    final List<NameValuePair> parameters=new ArrayList<NameValuePair>();
	    parameters.add(new BasicNameValuePair("access_token",accessToken));
	 	parameters.add(new BasicNameValuePair("openid", openid));
	 	final String url = "https://api.weixin.qq.com/sns/userinfo?";

	 	new Thread() {
	 		@Override
	 		public void run() {
	 			HttpsClient httpsClient = new HttpsClient();
	 			String result = httpsClient.HttpsRequest(url, parameters);
	 			if (result == null){
	 				Toast.makeText(WXEntryActivity.this, "获取微信用户资料失败，请稍后重试", Toast.LENGTH_SHORT).show();
	 				//pd1.dismiss();
	 				 LoginActivity.instance.finish();
	 				startActivity(new Intent(WXEntryActivity.this, LoginActivity.class));
	 				WXEntryActivity.this.finish();
	 				return;
	 			}
	 			LogUtil.i(result);

	 			try {
					JSONObject json=new JSONObject(result);
						
					wxName = json.getString("nickname");
					figureurl = json.getString("headimgurl");
					if ((openid != null) && (wxName != null)){
						new RequestTask(1).execute();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	 		}
	 	}.start();
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
	  		if(flagTask == 1){
	  			result = getUserInfoFromHvn();
	  		}else if(flagTask == 2){
	  		    result = registerToHvn();
	  		}
	  		return result;
	  	}
	  	//响应结果
	  	protected void onPostExecute(RequestResult result) {
	  		if (result == null){
	  			if (result == null){
	 				Toast.makeText(WXEntryActivity.this, "注册汉王云失败，请稍后重试", Toast.LENGTH_SHORT).show();
	 				//pd1.dismiss();
	 				startActivity(new Intent(WXEntryActivity.this, LoginActivity.class));
	 				WXEntryActivity.this.finish();
	 				return;
	 			}
	  		}
	  	    JsonData data = result.getData();
	  	    String jsonCode= data.getJson();
	  	    try {
	  			JSONObject json=new JSONObject(jsonCode);
	  			LogUtil.i(json.toString());
	  			
	  			if (flagTask == 1){
	  				if (json.getString("code").equals("0")){
			    		String wxNickname = json.getString("nickname");
			    		String username = json.getString("user");
			            SharedPreferences mSharedPreferences=WXEntryActivity.this.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
				        Editor mEditor=	mSharedPreferences.edit();
				        mEditor.putString("username", username);
				        mEditor.putString("nickname", wxNickname);
				        mEditor.putString("figureurl", figureurl);
				        mEditor.putInt("flag", 2);
				        mEditor.putInt("status", 1);
				        mEditor.commit();
				        
				        SharedPreferences mSharedCloudPreferences=getSharedPreferences("Cloud_Info", Activity.MODE_MULTI_PROCESS);
						Editor mCloudEditor = mSharedCloudPreferences.edit();
						mCloudEditor.putString("cloudname", wxNickname);
						mCloudEditor.putInt("cloudtype", 1);
					    HanvonApplication.cloudType = 1;
					    mCloudEditor.commit();
					    
				        if (flag == '1'){
				      //  	WXEntryActivity.this.startActivity(new Intent(WXEntryActivity.this, MyCloudMsg.class));
				        }else if(flag == '2'){
				        //	WXEntryActivity.this.startActivity(new Intent(WXEntryActivity.this, ScanNoteDetailActivity.class));
				        }else{
				        	WXEntryActivity.this.startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
				        }
		               // pd1.dismiss();
		                LoginActivity.instance.finish();
			    	}else if (json.getString("code").equals("426")){
			    		new RequestTask(2).execute();
			    	}else if(json.getString("code").equals("520")){
			    		Toast.makeText(WXEntryActivity.this, "服务器忙，请稍后重试", Toast.LENGTH_SHORT).show();
			    	  //  pd1.dismiss();
			    	}else{
			    		Toast.makeText(WXEntryActivity.this, "注册汉王云失败，请稍后重试", Toast.LENGTH_SHORT).show();
			    	  //  pd1.dismiss();
			    	}
	  			}else if (flagTask == 2){
	  			    if (json.getString("code").equals("0") || json.getString("code").equals("422")){
	  			        String hvnName = json.getString("username");
	  			        SharedPreferences mSharedPreferences=WXEntryActivity.this.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
	  				    Editor mEditor=	mSharedPreferences.edit();
	  				    mEditor.putString("username", hvnName);
	  				    mEditor.putString("nickname", wxName);
	  				    mEditor.putString("figureurl", figureurl);
	  				    mEditor.putInt("flag", 2);
	  				    mEditor.putInt("status", 1);
	  				    mEditor.commit();
	  				    
	  				    SharedPreferences mSharedCloudPreferences=getSharedPreferences("Cloud_Info", Activity.MODE_MULTI_PROCESS);
						Editor mCloudEditor = mSharedCloudPreferences.edit();
						mCloudEditor.putString("cloudname", wxName);
						mCloudEditor.putInt("cloudtype", 1);
					    HanvonApplication.cloudType = 1;
					    mCloudEditor.commit();
	  				    
					    if (flag == '1'){
				        //	WXEntryActivity.this.startActivity(new Intent(WXEntryActivity.this, MyCloudMsg.class));
				        }else if(flag == '2'){
				        //	WXEntryActivity.this.startActivity(new Intent(WXEntryActivity.this, ScanNoteDetailActivity.class));
				        }else{
				        	WXEntryActivity.this.startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
				        }
	  		           // pd1.dismiss();
	  		            LoginActivity.instance.finish();
	  			}else{
	  				Toast.makeText(WXEntryActivity.this, "注册汉王云失败，请稍后重试", Toast.LENGTH_SHORT).show();
	 				//pd1.dismiss();
	  			}
	  			}
	  		} catch (JSONException e) {
	  			e.printStackTrace();
	  		}
	  	}
	}
	  	
    public RequestResult registerToHvn(){
	    JSONObject paramJson=new JSONObject();
	    try {
	    	paramJson.put("openId", openid);
	  		paramJson.put("nickName", wxName);
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }

	    LogUtil.i(paramJson.toString());
	    RequestResult result=new RequestResult();
	   	result=RequestServerData.WXuserToHvn(paramJson);
	   	LogUtil.i(result.toString());
	    return result;
	}
    
    public RequestResult getUserInfoFromHvn(){
    	JSONObject paramJson=new JSONObject();
	  	try {
	    	paramJson.put("user", "wx_"+SHA1Util.encodeBySHA(openid));
	  	} catch (JSONException e) {
	  		e.printStackTrace();
	  	}

	  	LogUtil.i(paramJson.toString());
	  	RequestResult result=new RequestResult();
	 	result=RequestServerData.getUserInfo(paramJson);
	 	LogUtil.i(result.toString());
	  	return result;
    }
}