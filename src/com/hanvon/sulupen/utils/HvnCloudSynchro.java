package com.hanvon.sulupen.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanvon.bluetooth.BluetoothChatService;
import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.login.LoginActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class HvnCloudSynchro{

	private Context mcontext;

	private int uploadCount = 0;
	
	private Timer syncFileTimer;

	public HvnCloudSynchro(Context context){
		this.mcontext = context;
		HvnSyncStatic.mcontext = context;
	}
/*
	public void test() throws JSONException{
		HvnCloudTagsList();      //获取笔记本聊表
		HvnCloudDeleteTags();    //删除笔记本
		HvnCloudDeleteFiles();   //删除文件
		HvnCloudUploadTags();    //上传笔记本
		HvnCloudTagsList();      //获取笔记本列表
		HvnCloudFilesList();     //获取文件聊表
		HvnCloudDownSingleFile(); //下载单个文件
	//	HvnCloudDownFiles();      //下载多个文件
		
		try {
			UploadFiles();        //上传文件
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	private Handler msgHandler = new Handler() {
		public void handleMessage(Message msg) {
			handlerMsg(msg);
		};
	};
	
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
			Toast.makeText(mcontext, "未登录，请先登录!", Toast.LENGTH_SHORT).show();
			mcontext.startActivity(new Intent(mcontext, LoginActivity.class));
			HvnSyncStatic.isConnNetwork = true;
			return;
		}
		
		if (new ConnectionDetector(mcontext).isConnectingTOInternet()) {
			HvnSyncStatic.pd = ProgressDialog.show(mcontext, "", "正在进行同步......");
			HvnSyncStatic.isConnNetwork = true;
		} else {
			Toast.makeText(mcontext, "网络连接不可用，请检查网络后再试!", Toast.LENGTH_SHORT).show();
			HvnSyncStatic.isConnNetwork = false;
			return;
		}

	    HvnCloudGetSystemTime(0);
	    SharedPreferences mSharedPreferences=mcontext.getSharedPreferences("syncTime", Activity.MODE_MULTI_PROCESS);
	    if (mSharedPreferences != null){
		    HvnSyncStatic.HvnOldSynchroTime = mSharedPreferences.getString("OldSyncTime", "");
	    }else{
	    	HvnSyncStatic.HvnOldSynchroTime = "";
	    }

	    //加入时间限制，如果同步5分钟还没有完成，则自动取消同步操作
	    syncFileTimer = new Timer();
		TimerTask timerTask = new TimerTask() {
			 int i = 5*60;
             @Override 
             public void run() {
            	 Message msg = new Message(); 
                 msg.arg1 = i--;
                 msgHandler.sendMessage(msg);
             }
         };
         syncFileTimer.schedule(timerTask, 3000, 1000);// 3秒后开始倒计时，倒计时间隔为1秒  
	}

	public void handlerMsg(Message msg)  {
			int time = msg.arg1;
			if (time <= 0){
				HvnSyncStatic.pd.dismiss();
				Toast.makeText(mcontext, "同步超时，请检查网络是否连通!", Toast.LENGTH_SHORT).show();
				LogUtil.i("-------chaoshi----------------");
				syncFileTimer.cancel();
		    }
	}

	public void HvnCloudGetSystemTime(int flag ) throws JSONException{
		HvnCloudRequest.HvnCloudGetSystemTime(callBackData,flag);
	}

	/**
	 * 
	 * @desc 下载单个文件
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void HvnCloudDownSingleFile() throws JSONException, IOException{
		HvnCloudRequest.HvnCloudDownSingleFile(callBackData);
	}
	/**
	 * 
	 * @desc 下载多个文件
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void HvnCloudDownFiles() throws JSONException, IOException{
		HvnCloudRequest.HvnCloudDownFiles(callBackData,null,0);
	}
	/**
	 * 
	 * @desc 删除笔记本 
	 * @throws JSONException
	 */
	public void HvnCloudDeleteTags() throws JSONException{
		HvnCloudRequest.HvnCloudDeleteTags(callBackData);
	}

	/**
	 * 
	 * @desc 删除文件
	 * @throws JSONException
	 */
	public void HvnCloudDeleteFiles() throws JSONException{

		HvnCloudRequest.HvnCloudDeleteFiles(callBackData);
	}
	
	/**
	 * 
	 * @desc 上传笔记本
	 * @throws JSONException
	 */
	public void HvnCloudUploadTags() throws JSONException{
	    HvnCloudRequest.HvnCloudUploadTags(callBackData);
	}
	
	/**
	 * 
	 * @desc 获取笔记本列表
	 * @throws JSONException
	 */
	public void HvnCloudTagsList() throws JSONException{
		HvnCloudRequest.HvnCloudTagsList(callBackData);
	}
	
	/**
	 * 
	 * @desc 上传文件
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void HvnCloudUpload() throws JSONException, IOException{
		HvnCloudRequest.UploadFiles(callBackData);
	}
	
	/**
	 * 
	 * @desc 获取文件列表
	 * @throws JSONException
	 */
	public void HvnCloudFilesList() throws JSONException{
		HvnCloudRequest.HvnCloudFilesList(callBackData);
	}
	
	public  void DeleteLocalNoteBooks(){
		HvnCloudRequest.DeleteLocalNoteBooks();
	}
	
    public  void DeleteLocalNotes(){
    	HvnCloudRequest.DeleteLocalNotes();
	}
    
	private ObserverCallBack callBackData = new ObserverCallBack(){
		public void back(String data, int type,int total) throws JSONException {
			JSONObject json = null;
			json = new JSONObject(data);
			if (json == null || json.equals("0")){
				return;
			}

			switch (type) {
			case HvnSyncStatic.HVN_DELETE_TAGS:
				LogUtil.i("----请求类型："+HvnSyncStatic.HVN_DELETE_TAGS);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0") || json.getString("code").equals("454")){
					///执行本地的笔记本删除操作以及相关的笔记删除操作
					DeleteLocalNoteBooks();
					HvnCloudTagsList();
				}
				//HvnCloudTagsList();
				break;
			case HvnSyncStatic.HVN_DELETE_FILES:
				LogUtil.i("----请求类型："+HvnSyncStatic.HVN_DELETE_FILES);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0")){
					//进行本地的文件删除操作
					DeleteLocalNotes();
				}
				break;
			case HvnSyncStatic.HVN_UPLOAD_FILE:
				LogUtil.i("----请求类型："+HvnSyncStatic.HVN_UPLOAD_FILE + "   total: "+total);
				LogUtil.i("----response:"+data);
				if (json.get("code").equals("0")){
					if (Integer.valueOf((String) json.get("offset")) == total){
						uploadCount++;
						LogUtil.i("---第 " + uploadCount + " 个文件上传完成!");
					}
				}else{
					HvnSyncStatic.pd.dismiss();
					HvnSyncStatic.isError = true;
					Toast.makeText(mcontext, "存在未上传的笔记本，请重试!", Toast.LENGTH_LONG).show();
					break;
				}
				if (uploadCount == HvnSyncStatic.uploadTotal){//文件上传完成后，获取需要下载的文件列表
					LogUtil.i("文件上传完成！");
					HvnCloudFilesList();
				}
				break;
			case HvnSyncStatic.HVN_UPLOAD_TAGS:
				LogUtil.i("----请求类型："+HvnSyncStatic.HVN_UPLOAD_TAGS + "response:" + data);
				if (json.getString("code").equals("0")){
					//上传笔记本成功后，上传文件
					try {
						HvnCloudUpload();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case HvnSyncStatic.HVN_FILES_LIST:
				LogUtil.i("----请求类型："+HvnSyncStatic.HVN_FILES_LIST);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0")){
					HvnCloudResponse.ParseFilesList(json.getString("list"),callBackData);
				}
				break;
			case HvnSyncStatic.HVN_DOWN_SING_FILE:
				LogUtil.i("----请求类型："+HvnSyncStatic.HVN_DOWN_SING_FILE);
				LogUtil.i("----response:"+data);
				break;
			case HvnSyncStatic.HVN_TAGS_LIST:
				LogUtil.i("----请求类型："+HvnSyncStatic.HVN_TAGS_LIST);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0")){
					HvnCloudResponse.ParseTagsList(json.getString("list"));
					HvnCloudUploadTags();
				//	HvnCloudDeleteTags();
				}
				//HvnCloudUploadTags();
				break;
			case HvnSyncStatic.HVN_GET_SYSTEM_TIME:
				LogUtil.i("----请求类型："+HvnSyncStatic.HVN_GET_SYSTEM_TIME);
				LogUtil.i("----response:"+data);
				if (json.getString("code").equals("0")){
					HvnSyncStatic.HvnSystemCurTime = json.getString("currentTime");
					if ((total == 1) && (HvnSyncStatic.isDiagDimiss)){
						HvnSyncStatic.pd.dismiss();
						HvnSyncStatic.isDiagDimiss = false;
						syncFileTimer.cancel();
						SharedPreferences mSharedPreferences=mcontext.getSharedPreferences("syncTime", Activity.MODE_MULTI_PROCESS);
						Editor mEditor=	mSharedPreferences.edit();
						mEditor.putString("OldSyncTime", HvnSyncStatic.HvnSystemCurTime);
						mEditor.commit();
						mcontext.startActivity(new Intent(mcontext, MainActivity.class));
						break;
					}
				}
				HvnCloudDeleteTags();
			//	HvnCloudTagsList();
			//	HvnCloudFilesList();
				break;
			default:
				break;
			}
		}
	};
}
