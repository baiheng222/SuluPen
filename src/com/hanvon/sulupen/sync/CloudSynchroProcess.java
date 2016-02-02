package com.hanvon.sulupen.sync;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.common.logging.Log;
import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.login.LoginActivity;
import com.hanvon.sulupen.utils.ConnectionDetector;
import com.hanvon.sulupen.utils.LogUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


/**
 * 
 * @desc 云同步的入口函数 
 * @author chenxzhuang
 * @date 2015-12-14 下午5:30:54
 */

public class CloudSynchroProcess{

	private Context mcontext;
	private int uploadCount = 0;
	private Timer syncFileTimer;
	private int syncFileTimerId = 0;
    private ProgressDialog pd;
    private MyBroadcastReceiver receiver = null;
    private int flag = 0;//0  代表 同步    1 代表单个文件操作

	public CloudSynchroProcess(Context context,int flag){
		this.mcontext = context;
		this.flag = flag;
		SyncInfo.mContext = context;

		SyncDataUtils data = new SyncDataUtils(context);
		
		RegisterBroadCast();
		SyncInfo.downZipCount = 0;
	}
	
	public void RegisterBroadCast(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		receiver = new MyBroadcastReceiver();
		mcontext.registerReceiver(receiver, intentFilter);
	}

	private ObserverCallBack callBackData = new ObserverCallBack(){
		public void back(String data, int type,int total) throws JSONException {
			JSONObject json = null;
			json = new JSONObject(data);
			if (json == null || json.equals("0")){
				return;
			}

			switch (type) {
			case SyncInfo.HVN_DELETE_TAGS:
				LogUtil.i("----请求类型："+SyncInfo.HVN_DELETE_TAGS);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0") || json.getString("code").equals("454")){
					///执行本地的笔记本删除操作以及相关的笔记删除操作
					CloudSynchroResponse.DeleteLocalNoteBooks();
					HvnCloudTagsList();
				}
				//HvnCloudTagsList();
				break;
			case SyncInfo.HVN_DELETE_FILES:
				LogUtil.i("----请求类型："+SyncInfo.HVN_DELETE_FILES);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0") || json.getString("code").equals("453")){//云端删除成功或者云端文件不存在
					//进行本地的文件删除操作
					CloudSynchroResponse.DeleteLocalNotes();
					HvnCloudFilesList();
				}
				break;
			case SyncInfo.HVN_UPLOAD_FILE:
				LogUtil.i("----请求类型："+SyncInfo.HVN_UPLOAD_FILE + "   total: "+total);
				LogUtil.i("----response:"+data);
				if (json.get("code").equals("0")){
					if (Integer.valueOf((String) json.get("offset")) == total){
						uploadCount++;
						LogUtil.i("---第 " + uploadCount + " 个文件上传完成!");
					}
				}else{
				//	HvnSyncStatic.pd.dismiss();
				//	HvnSyncStatic.isError = true;
					Toast.makeText(mcontext, "存在未上传的笔记本，请重试!", Toast.LENGTH_LONG).show();
					break;
				}
				if (uploadCount == SyncInfo.uploadTotal){//文件上传完成后，获取需要下载的文件列表
					LogUtil.i("文件上传完成！");
					if (flag == 0){
					    HvnCloudFilesList();
					}
				}
				break;
			case SyncInfo.HVN_UPLOAD_TAGS:
				LogUtil.i("----请求类型："+SyncInfo.HVN_UPLOAD_TAGS + "response:" + data);
				if (json.getString("code").equals("0")){
					if (flag == 0){
					    //上传笔记本成功后，上传文件
					    try {
						    HvnCloudUpload();
					    } catch (IOException e) {
						    // TODO Auto-generated catch block
						    e.printStackTrace();
					    }
					}
				}
				break;
			case SyncInfo.HVN_FILES_LIST:
				LogUtil.i("----请求类型："+SyncInfo.HVN_FILES_LIST);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0")){
					//request.ParseFilesList(json.getString("list"));
					ArrayList<DownFilesMsg> fileMsgList = CloudSynchroResponse.ParseFilesList(json.getString("list"));
					if (fileMsgList == null){
						request.HvnCloudGetSystemTime(1);
					}else{
						int ListSize = fileMsgList.size();
						int i =0;
						for(DownFilesMsg fileMsg:fileMsgList){
							i++;
							request.HvnCloudDownFiles(fileMsg.getFilesList(), i);
						}
					}
				}
				break;
			case SyncInfo.HVN_DOWN_SING_FILE:
				LogUtil.i("----请求类型："+SyncInfo.HVN_DOWN_SING_FILE);
				LogUtil.i("----response:"+data);
				break;
			case SyncInfo.HVN_TAGS_LIST:
				LogUtil.i("----请求类型："+SyncInfo.HVN_TAGS_LIST);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0")){
					CloudSynchroResponse.ParseTagsList(json.getString("list"));
					HvnCloudUploadTags();
				//	HvnCloudDeleteTags();
				}
				//HvnCloudUploadTags();
				break;
			case SyncInfo.HVN_GET_SYSTEM_TIME:
				LogUtil.i("----请求类型："+SyncInfo.HVN_GET_SYSTEM_TIME);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0")){
					SyncInfo.HvnSystemCurTime = json.getString("currentTime");
					if (total == 1){
					//	HvnSyncStatic.pd.dismiss();
					//	HvnSyncStatic.isDiagDimiss = false;
						DismissDialog();
						if (syncFileTimerId == 1){
						    syncFileTimer.cancel();
						    syncFileTimerId = 0;
						}
						SharedPreferences mSharedPreferences=mcontext.getSharedPreferences("syncTime", Activity.MODE_MULTI_PROCESS);
						Editor mEditor=	mSharedPreferences.edit();
						mEditor.putString("OldSyncTime", SyncInfo.HvnSystemCurTime);
						mEditor.commit();
						mcontext.startActivity(new Intent(mcontext, MainActivity.class));
						break;
					}
				}
				HvnCloudDeleteTags();
				break;
			default:
				break;
			}
		}
	};
	
	private Handler ErrorHandler = new Handler() {
		public void handleMessage(Message msg) {
			Errorhandler(msg);
		};
	};
	private CloudSynchroRequest request = new CloudSynchroRequest(mcontext,callBackData,ErrorHandler);
  //  private MyBroadcastReceiver myReceive = new MyBroadcastReceiver(ErrorHandler);
	
	public void QuerySync() throws JSONException{
		/**
		 * 笔记同步步骤说明如下：
		 * 1.第一步，请求服务器当前时间，并记录服务器当前时间，作为本次查询相关列表的结束时间
		 * 2.第二步，请求服务器笔记本列表，同时请求笔记列表，然后对请求到的笔记本的列表进行处理(上午已发邮件)
		 * 3.对请求到的笔记列表进行处理，文件处理分为以下几种情况：
		 * {
		 *     1.服务器端isdelete = 1，本地 isupload = 1，删除
		 *     2.服务器端isdelete = 1，本地isupload = 2，不做处理
		 *     3.服务器端isdelete = 0，本地isupload = 1，但是服务器端的最后修改时间比本地的最后修改时间早，则下载该文件
		 *     
		 * }
		 * 4.查询本地需要删除的笔记本，对服务器进行请求，待请求返回后，删除本地笔记本以及相关，
		 * 5.待删除笔记本成功后，查询本地需要删除的笔记，对服务器进行删除请求，然后删除本地的笔记以及相关
		 * 6.第二步，查询本地需要进行上传的笔记本(包含新建的和修改名称的)，对服务器进行请求，待请求返回后，对本地笔记本的upload状态进行更新
		 * 7.待笔记本上传成功后，查询本地需要上传的笔记(包含新建和修改的)，对服务器进行请求，待返回成功后，更新本地笔记的状态
		 * 8.在文件上传的同时，向服务器请求下载服务器新增的文件，然后对文件进行解析，将图片进行base64转码解析
		 * 9.第三步，在以上步骤处理完成，向服务器进行时间请求，写入文件，作为下次同步的开始时间
		 * 注意：在对笔记以及笔记本的时间处理时，插入的服务器的时间均为第一次请求的服务器时间，下载的文件保存在/sdcard/sulupen/username/contentid/目录下
		 * 在进行笔记的删除时，同时需要删除该文件下的文件目录
		 */
		if (HanvonApplication.hvnName.equals("")){
			if (flag != 0){
				return;
			}
			Toast.makeText(mcontext, "未登录，请先登录!", Toast.LENGTH_SHORT).show();
			mcontext.startActivity(new Intent(mcontext, LoginActivity.class));
			return;
		}
		if(!HanvonApplication.isActivity){
			if (flag != 0){
				return;
			}
			Toast.makeText(mcontext, "账号未激活，请激活后重新登陆!", Toast.LENGTH_SHORT).show();
			return;
		}
			
		if (new ConnectionDetector(mcontext).isConnectingTOInternet()) {
			if (flag == 0){
				pd = ProgressDialog.show(mcontext, "", "正在进行同步......");
			}else if (flag == 1){
				pd = ProgressDialog.show(mcontext, "", "正在文件上传......");
			}
		} else {
			if (flag != 0){
				return;
			}
			Toast.makeText(mcontext, "网络连接不可用，请检查网络后再试!", Toast.LENGTH_SHORT).show();
			return;
		}
			
		if (flag == 0){
		    HvnCloudGetSystemTime(0);
		    SharedPreferences mSharedPreferences=mcontext.getSharedPreferences("syncTime", Activity.MODE_MULTI_PROCESS);
		    if (mSharedPreferences != null){
		    	SyncInfo.HvnOldSynchroTime = mSharedPreferences.getString("OldSyncTime", "");
		    }else{
		    	SyncInfo.HvnOldSynchroTime = "";
		    }

		    //加入时间限制，如果同步5分钟还没有完成，则自动取消同步操作
		    syncFileTimer = new Timer();
		    syncFileTimerId = 1;
			TimerTask timerTask = new TimerTask() {
				int i = 5*60;
	            @Override 
	            public void run() {
	            	Message msg = new Message(); 
	                msg.arg1 = i--;
	                LogUtil.i("--------计时----"+i);
	                msgHandler.sendMessage(msg);
	            }
	        };
	        syncFileTimer.schedule(timerTask, 3000, 1000);// 3秒后开始倒计时，倒计时间隔为1秒  
		}
	}
	
	private Handler msgHandler = new Handler() {
		public void handleMessage(Message msg) {
			handlerMsg(msg);
		};
	};

	public void handlerMsg(Message msg)  {
		int time = msg.arg1;
		if (time <= 0){
			DismissDialog();
			Toast.makeText(mcontext, "同步超时，请检查网络是否连通!", Toast.LENGTH_SHORT).show();
			LogUtil.i("-------chaoshi----------------");
			syncFileTimer.cancel();
			syncFileTimerId = 0;
		}
	}

	public void HvnCloudGetSystemTime(int flag ) throws JSONException{
		request.HvnCloudGetSystemTime(flag);
	}

	/**
	 * 
	 * @desc 下载多个文件
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void HvnCloudDownFiles() throws JSONException, IOException{
		request.HvnCloudDownFiles(null,0);
	}
	/**
	 * 
	 * @desc 删除笔记本 
	 * @throws JSONException
	 */
	public void HvnCloudDeleteTags() throws JSONException{
		request.HvnCloudDeleteTags();
	}

	/**
	 * 
	 * @desc 删除文件
	 * @throws JSONException
	 */
	public void HvnCloudDeleteFiles() throws JSONException{
		request.HvnCloudDeleteFiles();
	}
	
	/**
	 * 
	 * @desc 上传笔记本
	 * @throws JSONException
	 */
	public void HvnCloudUploadTags() throws JSONException{
		request.HvnCloudUploadTags();
	}
	
	/**
	 * 
	 * @desc 获取笔记本列表
	 * @throws JSONException
	 */
	public void HvnCloudTagsList() throws JSONException{
		request.HvnCloudTagsList();
	}

	/**
	 * 
	 * @desc 上传文件
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void HvnCloudUpload() throws JSONException, IOException{
		request.UploadFiles();
	}
	
	/**
	 * 
	 * @desc 获取文件列表
	 * @throws JSONException
	 */
	public void HvnCloudFilesList() throws JSONException{
		request.HvnCloudFilesList();
	}
	
	
	public void HvnCloudUploadSignFile(NoteRecord noteRecord) throws JSONException, IOException{
		//1.首先检查 该笔记所在的笔记本是否上传
		//2.再上传笔记
		request.UploadSignFile(noteRecord);
	}
	
	//取消 正在进行同步的按钮
	public void DismissDialog(){
		if (pd != null){
			pd.dismiss();
		}
		if (receiver != null){
		    mcontext.unregisterReceiver(receiver);
		    receiver = null;
		}
	}
	
	public void Errorhandler(Message msg)  {
		int type = msg.arg1;
		String data = (String)msg.obj;
		LogUtil.i("-------type---"+type);
		DismissDialog();
		if (syncFileTimerId == 1){
		    syncFileTimer.cancel();
		    syncFileTimerId = 0;
		}
		switch (type){
		    case 1:
		    	Toast.makeText(mcontext, "同步出现错误，请稍后再试!", Toast.LENGTH_LONG).show();
			    break;
		    case 2:
		    	break;
		    case 3:
		    	Toast.makeText(mcontext, "网络断开连接，请检测网络是否开启!", Toast.LENGTH_LONG).show();
		    	break;
		}
	}
	
	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		    NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
		    NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
		    NetworkInfo activeInfo = manager.getActiveNetworkInfo();  
		    if (activeInfo == null){
		    	LogUtil.i("-------网络断开连接------------");
		    	DismissDialog();
		    	if (syncFileTimerId == 1){
		    	    syncFileTimer.cancel();
		    	    syncFileTimerId = 0;
		    	}
		    	Toast.makeText(mcontext, "网络断开连接，请检测网络是否开启!", Toast.LENGTH_LONG).show();
		    }else{
		    	LogUtil.i("-------网络已连接------------");   	
		    }
		}
	}

}
