package com.hanvon.sulupen;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.hanvon.bluetooth.BluetoothDataPackage;
import com.hanvon.bluetooth.BluetoothService;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.utils.LogUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
/** 
 * App自动更新之通知栏下载 
 * @author 402-9 
 * 
 */  
@SuppressWarnings("unused")
public class UpdateAppService extends Service{  
    private Context context;  
    private Notification notification;  
    private NotificationManager nManager;  
    private PendingIntent pendingIntent; 
    private String updateUrl;
    private int updateType;
 
    
    public UpdateAppService() {
		super();
	}
    public UpdateAppService(Context context,int flag) {
    	this.context=context;
    	this.updateType = flag;
  	}
	//创建通知 
	public void CreateInform(String Url) {  
		updateUrl = Url;
    	LogUtil.i("--------------------------------");

		Intent intent = new Intent(context,MainActivity.class);  
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);  
        //创建一个通知  
        notification = new Notification(R.drawable.ssdk_oks_ptr_ptr, "开始下载", System.currentTimeMillis());
        if (updateType == 1){
        	notification.setLatestEventInfo(context, "正在下载速记新版本", "点击查看详细内容", pendingIntent);
        }else if (updateType == 2){
        	notification.setLatestEventInfo(context, "正在下载速记硬件新版本", "点击查看详细内容", pendingIntent);
        }
        nManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);  
        nManager.notify(100, notification);
        new Thread(new updateRunnable()).start();
        
    }  
    class updateRunnable implements Runnable{  
        int downnum = 0;//已下载的大小  
        int downcount= 0;//下载百分比  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
            try {  
            	File file = getFileFromServer(updateUrl); 
            //	installApk(file);
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }
        public File getFileFromServer(String path) throws Exception{
    		LogUtil.i("PATH:"+path);
    		File file = null;
    	//    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
    	        URL url = new URL(path);
    	        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
    	        conn.setConnectTimeout(5000);
    	        //获取到文件的大小
    	        int length = conn.getContentLength();
    	        InputStream is = conn.getInputStream();
    	        if (updateType == 1){
    	        	 File f = new File("/sdcard/"+"SuluPen.apk");
    	        	 if (f.exists()){
    	        		 LogUtil.i("---Environment:PATH---"+"/sdcard/"+"SuluPen.apk");
    	        		 f.delete();
    	        	 }
    	        	 file = new File("/sdcard/", "SuluPen.apk");
    	        	 LogUtil.i("---Environment:PATH---"+"/sdcard/"+"SuluPen.apk");
    	        }else if (updateType == 2){
    	        	 File f = new File("/sdcard/"+"/"+HanvonApplication.HardUpdateName);
   	        	     if (f.exists()){
   	        	    	LogUtil.i("---Environment:PATH---"+"/sdcard/"+"/"+HanvonApplication.HardUpdateName);
   	        		    f.delete();
   	        	     }
    	        	 file = new File("/sdcard/", HanvonApplication.HardUpdateName);
    	        	 LogUtil.i("---Environment:PATH---"+"/sdcard/"+HanvonApplication.HardUpdateName);
    	        }
    	        LogUtil.i("---Environment:PATH---"+"/sdcard/");
    	        FileOutputStream fos = new FileOutputStream(file);
    	        BufferedInputStream bis = new BufferedInputStream(is);
    	        byte[] buffer = new byte[1024];
    	        int len = 0;
    	        int total = 0;

    	        while((len =bis.read(buffer))!=-1){
    	            fos.write(buffer, 0, len);
    	            downnum += len;  
                    if((downcount == 0)||(int) (downnum*100/length)-1>downcount){   
                        downcount += 1;
                        if (updateType == 1){
                        	notification.setLatestEventInfo(context, "正在下载速记新版本", "已下载了"+(int)downnum*100/length+"%", null);
                        }else if (updateType == 2){
                        	notification.setLatestEventInfo(context, "正在下载速记硬件新版本", "已下载了"+(int)downnum*100/length+"%", null);
                        }
                        nManager.notify(100, notification);
                    }
    	        }

    	        fos.close();
    	        bis.close();
    	        is.close();
    	        if (downnum==length) {
    	        	if (updateType == 1){
    	        		Intent installIntent = new Intent(Intent.ACTION_VIEW);
    	        	    installIntent.setClassName(context,installApk(file));
                  //    installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        pendingIntent = PendingIntent.getActivity(context, 0, installIntent, 0);
               	        notification.setLatestEventInfo(context, "", "", pendingIntent);
    	        	}else if(updateType == 2){
    	        		/******************************/
    	        		 HashMap<String, String> params=new HashMap<String, String>();
    	    			 params.put("update_mode", ""+11);
    	    			 BluetoothService.getServiceInstance().getBluetoothChatService().sendBTData(2,BluetoothDataPackage.epenUpgradePackage("",
    	    			 params));
    	    		//	 pendingIntent = PendingIntent.getActivity(context, 0, null, 0);
                	 //    notification.setLatestEventInfo(context, "", "", pendingIntent);
    	        	}
                    nManager.notify(100, notification);
        	        stopSelf();
                }
    	        return file;
    	 //   }else{
    	  //  	return null;
    	  //  }
        }
    	/**
    	 * 5.安装下载的Apk软件
    	 * @return 
    	  */
    	protected String installApk(File file) {
    		LogUtil.i("*****************");
    		HanvonApplication.isUpdate = false;
    		HanvonApplication.path = Uri.fromFile(file).toString();
    	    LogUtil.i(HanvonApplication.path);
    		nManager.cancel(100);
    	    Intent intent = new Intent(Intent.ACTION_VIEW);
    	    //执行动作
    	   // intent.setAction(Intent.ACTION_VIEW);
    	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    	    //执行的数据类型
    	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    	    context.startActivity(intent);
    	    android.os.Process.killProcess(android.os.Process.myPid());
    	    return null;
    	}
     }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}  
	
	public void onDestory(){
		nManager.cancel(100);
		super.onDestroy();
	}
 }