package com.hanvon.sulupen;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.net.JsonData;
import com.hanvon.sulupen.net.RequestResult;
import com.hanvon.sulupen.net.RequestServerData;
import com.hanvon.sulupen.utils.ConnectionDetector;
import com.hanvon.sulupen.utils.LogUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class SoftUpdate extends Activity implements OnClickListener{

	private String UpdateUrl;
	private Context mContext;
	private final int UPDATA_CLIENT = 0;
	private final int GET_UNDATAINFO_ERROR = 1;
	private final int DOWN_ERROR = 2;
	private String version;
	private ProgressDialog pd;
	private int flag;
	
	private SharedPreferences mDefaultPreference;
	
	 /* 下载包安装路径 */
    private static final String savePath = "/sdcard/updatedemo/";
     
    public SoftUpdate(Context context,int flag) {
        this.mContext = context;
        this.flag = flag;
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

	public String getVersion(){
		PackageManager packageManager = mContext.getPackageManager();   
	    PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    version = packInfo.versionName;
	    LogUtil.i(version);
	    return version;
	}

	public void checkVersion(){
		
		if (new ConnectionDetector(mContext).isConnectingTOInternet()) {
			if (flag == 1){
			    pd = ProgressDialog.show(mContext, "", "正在进行版本检查......");
			}
			version = getVersion();
			new RequestTask().execute();
		} else {
			Toast.makeText(mContext, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
		}
	}
	
	class RequestTask  extends AsyncTask<Void, Void, RequestResult>{
	  	   @Override
		    protected void onPreExecute() {
		    	super.onPreExecute();
		    }
			@Override
			protected RequestResult doInBackground(Void... arg0) {
				RequestResult result=null;
				result = getNewVersionFromServer();
				return result;
			}
			 //响应结果
		    protected void onPostExecute(RequestResult result) {
		    	if (result == null){
				    return;
		    	}
		    	if (flag == 1){
		    	    pd.dismiss();
		    	}
		        JsonData data = result.getData();
		        String jsonCode= data.getJson();
		        try {
				    JSONObject json=new JSONObject(jsonCode);
				    LogUtil.i(json.toString());
				    if (json.equals(null)){
				    	return;
				    }
				    if (json.getString("code").equals("0")){
				    	LogUtil.i("有更新的版本，是否需要升级？");
				    	UpdateUrl = json.getString("result");
				    	Message msg = new Message();
			            msg.what = UPDATA_CLIENT; 
			            handler.sendMessage(msg);
			            //保存可以升级标记到DefaultPreference
//			            mDefaultPreference = PreferenceManager
//			    				.getDefaultSharedPreferences(getApplicationContext());
			            Editor editor = Settings.mSharedPref.edit();
			            editor.putBoolean("hasUpdate", true);
			            editor.commit();
				    }else if (json.getString("code").equals("9120")){
				    	if(flag == 1){
				    	    Toast.makeText(mContext, "已是最新版本，不需要升级",Toast.LENGTH_LONG).show();
				    	}
				    	Editor editor = Settings.mSharedPref.edit();
			            editor.putBoolean("hasUpdate", false);
			            editor.commit();
				    }else if (json.getString("code").equals("9100")){
				    	LogUtil.i("请求错误");
				    }
			    } catch (JSONException e) {
				    e.printStackTrace();
			    }
		    }
	    }

	public RequestResult getNewVersionFromServer(){
		JSONObject JSuserInfoJson = new JSONObject();
  	    try {
  	    	JSuserInfoJson.put("uid",HanvonApplication.AppUid);
  	  	    JSuserInfoJson.put("sid", HanvonApplication.AppSid);
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
	
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
		    switch (msg.what) {
	        case UPDATA_CLIENT:  
	            showUpdataDialog();
	            break;
	        case GET_UNDATAINFO_ERROR:
	            //服务器超时
	            Toast.makeText(mContext, "获取服务器更新信息失败",Toast.LENGTH_LONG).show();
	            break;    
	        case DOWN_ERROR: 
	            Toast.makeText(mContext, "下载新版本失败",Toast.LENGTH_LONG).show();
	            break; 
	        }
		};
	};
	public void showUpdataDialog() {
	    AlertDialog.Builder builer = new Builder(mContext);
	    builer.setTitle("版本升级");
	    builer.setMessage("有最新的版本，是否需要下载？"); 
	    builer.setPositiveButton("下载", new OnClickListener() {
	    	 public void onClick(DialogInterface dialog, int which) {
		            LogUtil.i("下载apk,更新"); 
		            new UpdateAppService(mContext,1).CreateInform(UpdateUrl);
		            HanvonApplication.isUpdate = true;
		       
		        }
	    });
	    //当点取消按钮时进行登录
	    builer.setNegativeButton("以后再说", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	           /*****************/
	        	Settings.setKeyVersionUpdate(false);
	        }
	    });
	    AlertDialog dialog = builer.create();
	    dialog.show();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

