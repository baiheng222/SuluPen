package com.hanvon.bluetooth;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanvon.sulupen.UpdateAppService;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.net.JsonData;
import com.hanvon.sulupen.net.RequestResult;
import com.hanvon.sulupen.net.RequestServerData;
import com.hanvon.sulupen.utils.LogUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.widget.Toast;

public class HardUpdate {

	private String version;
	private int dPowerState;
	private String FileUrl;
	Context mContext;
	public HardUpdate() {
		super();
	}

	public HardUpdate(Context context) {
		super();
		mContext = context;
	}
	public void checkVersionUpdate(String version){
		this.version = version;
	//	dPowerState = blueInfo.getdPowerState();
		LogUtil.i("*******hard version: "+this.version+"\r\n*******dPowerState:"+dPowerState);
		
	//	return;
		requestToServer();/*向服务器发起版本更新请求*/
	}
	
	public void requestToServer(){
		new RequestTask().execute();     /*向云端发送版本检查请求，是否有新的版本更新*/
	}
	
	class RequestTask  extends AsyncTask<Void, Void, RequestResult>{
  	  @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    }
		@Override
		protected RequestResult doInBackground(Void... arg0) {
			RequestResult result=null;
			result = requestVesionToServer();
			return result;
		}
		 //响应结果
	    protected void onPostExecute(RequestResult result) {
	        JsonData data = result.getData();
	        String jsonCode= data.getJson();
	        try {
			    JSONObject json=new JSONObject(jsonCode);
			    LogUtil.i(json.toString());
			    if (json.get("code").equals("0")) {
			    	if (BluetoothService.getServiceInstance().curBatteryStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
			    		LogUtil.i("----------系统正在进行充电！----------------");
					} else {
						dPowerState = BluetoothService.getServiceInstance().curBatteryPower;
						LogUtil.i("----------dPowerState:"+dPowerState);
						if (dPowerState < 30){
				    		Toast.makeText(mContext, "有新的版本需要更新，电量过低，请充电后进行文件升级！", Toast.LENGTH_SHORT).show();
				    		/*退出蓝牙的动作并跳转到主界面*/
				    		return;
				    	}
					}
			    		
			    	FileUrl = json.getString("result");
			    	HanvonApplication.HardUpdateName = 	FileUrl.substring(FileUrl.lastIndexOf("/")+1);
			    	LogUtil.i("---------hard update name:"+HanvonApplication.HardUpdateName);
			    /*	
			    	HashMap<String, String> params=new HashMap<String, String>();
	    			 params.put("update_mode", ""+11);
	    			 BluetoothService.getServiceInstance().getBluetoothChatService().sendBTData(2,BluetoothDataPackage.epenUpgradePackage("",
	    			 params));*/
			    	/*进行文件下载--->向蓝牙笔发起更新请求---->发送文件*/	
			    	new UpdateAppService(mContext,2).CreateInform(FileUrl);
				} else if (json.get("code").equals("9120")){
				} else if (json.get("code").equals("110")){
				} else {
				}
		    } catch (JSONException e) {
			    e.printStackTrace();
		    }
	    }
    }

	public RequestResult requestVesionToServer(){
		JSONObject JSuserInfoJson = new JSONObject();
  	    try {
  	    	JSuserInfoJson.put("uid", HanvonApplication.AppUid);
  	  	    JSuserInfoJson.put("sid", HanvonApplication.HardSid);
  	  	    JSuserInfoJson.put("ver", version);
  	  	    JSuserInfoJson.put("type", 1);
  	    } catch (JSONException e) {
  		    e.printStackTrace();
  	    }

  	    LogUtil.i(JSuserInfoJson.toString());
  	    RequestResult result=new RequestResult();
  	    result=RequestServerData.softUpdate(JSuserInfoJson);

  	    return result;
		
	}
	
}
