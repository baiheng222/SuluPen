package com.hanvon.Application;

import java.util.List;

import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;

import com.baidu.frontia.FrontiaApplication;
import com.hanvon.bluetooth.BluetoothService;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;


public class HanvonApplication extends FrontiaApplication {
	public String curAddress = "";
	public String addrDetail="";
	public String netState = "";
	public String curCity="";
	
	public static String strName;
	public static String strEmail;
	public static String strPhone;
	public static int userFlag;/* 0  汉王用户   1 QQ账号   2 微信账号   3 微博账号*/
	public static String strToken;
	public static Bitmap BitHeadImage;
	public static int count;//用户扫描记录总数
	public static String hvnName;//第三方登陆时对应于汉王用户名称，汉王用户登陆时对应于邮箱
	public static int cloudType;//云类型，0 未登录 1汉王云 2百度云
	public static String noteCreateTime="";
	public static String AppSid = "EpenAssistant_Alpha";
	public static String AppUid = "";
	public static String AppVer = "";
	public static String AppDeviceId = "";
	public static String HardUpdateName = "";

	public static String lastConnectedTime = "0000-00-00 00:00";//记录应用最近一次连接速录笔的时间

	public static Tencent mTencent;
	public static IWXAPI api;
	private String QQ_APPID = "1104705079";
	private JSONObject obj;
	private String City;
	public static  String mWeather;
		public static String path;

	public void onCreate() {
		
		super.onCreate();
		

		
		api = WXAPIFactory.createWXAPI(this, "wxdf64ce17dae09860", true);
		api.registerApp("wxdf64ce17dae09860");	

		mTencent = Tencent.createInstance(QQ_APPID, HanvonApplication.this);
	//	removeTempFromPref();
		
		/**获取uid**/
		ActivityManager am = (ActivityManager) getSystemService(this.getApplicationContext().ACTIVITY_SERVICE);
        ApplicationInfo appinfo = getApplicationInfo();
        List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
        for (RunningAppProcessInfo runningProcess : run) {
            if ((runningProcess.processName != null) && runningProcess.processName.equals(appinfo.processName)) {
            	AppUid = String.valueOf(runningProcess.uid);
                break;
            }
        }
        /**获取软件版本**/
        PackageManager packageManager = this.getApplicationContext().getPackageManager();   
	    PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(this.getApplicationContext().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppVer = packInfo.versionName;
		/**获取设备sn号**/
		TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService( this.getApplicationContext().TELEPHONY_SERVICE);
		AppDeviceId =  telephonyManager.getSimSerialNumber();
	}
}