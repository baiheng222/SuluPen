package com.hanvon.sulupen.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
	
	 @Override  
	 public void onReceive(Context context, Intent intent) {  
	    // TODO Auto-generated method stub  
	    //Toast.makeText(context, intent.getAction(), 1).show();  
	    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	    NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	    NetworkInfo activeInfo = manager.getActiveNetworkInfo();  
	    if (activeInfo == null){
	    	LogUtil.i("-------网络断开连接------------");
	     	HvnSyncStatic.isConnNetwork = false;
	 //   	Toast.makeText(HvnSyncStatic.mcontext, "网络连接不可用，同步失败!", Toast.LENGTH_SHORT).show();
	     	if (HvnSyncStatic.pd != null){
			    HvnSyncStatic.pd.dismiss();
	     	}
	    }else{
	    	LogUtil.i("-------网络已连接------------");
	    	HvnSyncStatic.isConnNetwork = true;
	    }
	}  //如果无网络连接activeInfo为null  


}
