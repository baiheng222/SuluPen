package com.hanvon.bluetooth;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.ScanNoteActivity;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.datas.ScanRecordInfo;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.helper.PreferHelper;
import com.hanvon.sulupen.login.LoginActivity;
import com.hanvon.sulupen.pinyin.PinyinIME;
import com.hanvon.sulupen.utils.ConnectionDetector;
import com.hanvon.sulupen.utils.CustomDialog;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.TimeUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 
 * @desc 速录笔服务器类  用于接收笔端发送的消息
 * @author chenxzhuang
 * @date 2015-10-20 下午5:39:59
 */
public class BluetoothService extends Service{

	private static BluetoothService mHanVonService = null;
	private BluetoothChatService blueService;
	public int curBatteryPower = -1;
	public int curBatteryStatus = -1;
	// 1为盲扫模式，2为校对模式,3.输入法模式
	public static int scanRecordMode = 1;
	
	private static AlertDialog d;
	private static Timer sendFileTimer;
	private static Timer dialogShowTimer;
	
	public static byte[] unGZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bais);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
			baos.close();
			gzip.close();
			bais.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.i("---unzip error:"+ex.getMessage());
		}
		return b;
	}
	/**
	 * Handler 消息接收器
	 * 
	 * @param msg
	 * @throws UnsupportedEncodingException 
	 */
	public void handlerMsg(Message msg) throws UnsupportedEncodingException {
		switch (msg.what) {
		/**
		 * 蓝牙升级文件传输时间限制
		 */
		case BluetoothChatService.BLUETOOTH_MESSAGE_SEND_TIME:
			int time = msg.arg1;
			if (time <= 0){
				sendFileTimer.cancel();
				
				d.setMessage("升级失败");
				dialogShowTimer = new Timer();
				TimerTask timerTask = new TimerTask() {
					 int i = 5;
	                 @Override 
	                 public void run() {
	                	 Message msg = new Message(); 
	                     msg.what = BluetoothChatService.BLUETOOTH_DIALOG_SHOW_TIME; 
	                     msg.arg1 = i--;
	                     msgHandler.sendMessage(msg);
	                 }
	             };
	             dialogShowTimer.schedule(timerTask, 1000, 1000);// 1秒后开始倒计时，倒计时间隔为1秒  
			}
			break;
			
		case BluetoothChatService.BLUETOOTH_DIALOG_SHOW_TIME:
			int showtime = msg.arg1;
			if (showtime <= 0){
				d.dismiss();
			//	Toast.makeText(this, "升级失败", Toast.LENGTH_LONG).show();
				dialogShowTimer.cancel();
			}
			break;
		/**
		 * 蓝牙模块
		 */
		case BluetoothChatService.MESSAGE_STATE_CHANGE:
			LogUtil.i("MESSAGE_STATE_CHANGE: " + msg.arg1);
			switch (msg.arg1) {
			case BluetoothChatService.STATE_CONNECTED:
				Intent intent = new Intent(
						BluetoothIntenAction.ACTION_EPEN_BT_CONNECTED);
				sendBroadcast(intent);
				if (!BluetoothSearch.curBtAddress
						.equals(BluetoothChatService.curDeviceAddress)) {
					BluetoothSearch.curBtAddress = BluetoothChatService.curDeviceAddress;
					BluetoothSetting.setBlueAddress(BluetoothSearch.curBtAddress);
				}
				
				SharedPreferences mSharedPreferences=getSharedPreferences("Blue", Activity.MODE_MULTI_PROCESS);
				Editor mEditor=	mSharedPreferences.edit();
				HanvonApplication.isDormant = false;
				mEditor.putBoolean("isDormant", HanvonApplication.isDormant);
				mEditor.putString("address", BluetoothSearch.curBtAddress);
				mEditor.commit();
				break;
			case BluetoothChatService.STATE_CONNECTING:
				// mChatService.showProgressDialog();
				LogUtil.i("MESSAGE_STATE_CONNECTING: " + msg.arg1);
				break;
			case BluetoothChatService.STATE_LISTEN:
				LogUtil.i("MESSAGE_STATE_LISTEN: " + msg.arg1);
			case BluetoothChatService.STATE_NONE:
				Intent disconnectIntent = new Intent(
						BluetoothIntenAction.ACTION_EPEN_BT_DISCONNECT);
				sendBroadcast(disconnectIntent);
				LogUtil.i("MESSAGE_STATE_NONE: " + msg.arg1);
				if (d != null){
					d.dismiss();
				}
				break;
			}
			break;
		case BluetoothChatService.MESSAGE_WRITE:
			int dataType1 = msg.arg1;
			// 文件类型
			if (dataType1 == 1) {
				int sendPercent = msg.arg2;
				if (sendPercent == 105) {
					LogUtil.i("文件发送完成105");
				} else {
					LogUtil.i("send file percent:" + sendPercent + "%");
				}

			}
			// 文本类型
			if (dataType1 == 2) {

			}
			// mConversationArrayAdapter.add("Me:  " + writeMessage);
			break;
			
		case BluetoothChatService.BLUETOOTH_MESSAGE_READ:
			int dataType = msg.arg1;
			// 文件类型
			if (dataType == 1) {
				int sendPercent = msg.arg2;
				if (sendPercent != -1){
					
				}
			//		UiUtil.showToast(this, "" + sendPercent);
			}
			// 文本类型
			if (dataType == 2) {
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject == null || jsonObject.equals("")){
					break;
				}
				 LogUtil.i("接收包:"+jsonObject.toString());
				if (jsonObject != null) {
					try {
						int type = jsonObject.getInt("type");
						LogUtil.v("type:" + type);
						JSONObject jsonData = new JSONObject();

						switch (type) {
						/**
						 * 设备基本信息
						 */
						case 101:

							jsonData = jsonObject.getJSONObject("data");
							jsonData.put("device_btName",
									BluetoothChatService.curDeviceName);
							LogUtil.i("tong========&&&&&&&&&=======add db"+jsonData.toString());
							LogUtil.i("----deviceinfo:"+jsonData.toString());
							String serialNum = jsonData.getString("device_serialNum");
							String isSendImage = jsonData.getString("device_isSendImage");
							if (jsonData.isNull("SuluPen_Hardware")){
							    HanvonApplication.HardSid = "SuluPen_Hardware_001";
							}else{
								HanvonApplication.HardSid = jsonData.getString("SuluPen_Hardware");
							}
							if(isSendImage.equals("0")){
								BluetoothSetting.setBlueIsSendImage(false);
							}else{
								BluetoothSetting.setBlueIsSendImage(true);
							}
							String device_language = "ch-eng";
							if (jsonData.isNull("device_language")){
							}else{
								device_language = jsonData.getString("device_language");
							}
							
							BluetoothSetting.setIdentCoreCode(device_language);
							String sleep_time = jsonData.getString("device_sleepTime");
							int index = 1;
							if (sleep_time.equals("2")){
								index = 0;
							}else if (sleep_time.equals("5")){
								index = 1;
							}else if (sleep_time.equals("10")){
								index = 2;
							}else if (sleep_time.equals("-1")){
								index = 3;
							}
							BluetoothSetting.setSleepTime(index);

							BluetoothSetting.setSeralNumber(serialNum);
							if (jsonData.getString("device_scanDirection").equals("1")){
								BluetoothSetting.setBlueScanDir(1);
							}else{
								BluetoothSetting.setBlueScanDir(0);
							}
							/********************add by chenxzhuang*****************/
							String device_version = jsonData.getString("device_version");
						    String version = device_version.substring(device_version.indexOf(".")+1, device_version.lastIndexOf("."));
						    LogUtil.i("----device_version:"+version);
						    if (HanvonApplication.HardSid.contains("001")){
						    	BluetoothSetting.setBlueVersion("v1."+version);
						    }else{
						    	BluetoothSetting.setBlueVersion("v2."+version);
						    }
						    
							if (new ConnectionDetector(this).isConnectingTOInternet()) {
							  //  String device_version = jsonData.getString("device_version");
							 //   String version = device_version.substring(device_version.indexOf(".")+1, device_version.lastIndexOf("."));
							  //  LogUtil.i("----device_version:"+version);
							    HardUpdate hardUpdate = new HardUpdate(BluetoothService.this);
							    hardUpdate.checkVersionUpdate(version);
							}else{
								Toast.makeText(this, "网络不通，暂时无法进行硬件版本检查!", Toast.LENGTH_SHORT).show();
							}
							/**********************add end*************************/
							break;

						/**
						 * 设备电量信息
						 */
						case 102:
							LogUtil.i("--------get battery-----102------");
							jsonData = jsonObject.getJSONObject("data");
							int power = Integer.parseInt(jsonData
									.getString("battery_power"));
							int status = Integer.parseInt(jsonData
									.getString("battery_status"));
							curBatteryPower = power;
							curBatteryStatus = status;
							LogUtil.i("============power:" + power);
							Intent intent = new Intent();
							intent.setAction(BluetoothIntenAction.ACTION_EPEN_BATTERY_CHANGE);
							intent.putExtra("epen_power", power);
							intent.putExtra("epen_status", status);
							sendBroadcast(intent);
							if (power == 8 || power == 6){
								Toast.makeText(getApplicationContext(), "蓝牙速录笔电池电量过低,请充电!", Toast.LENGTH_SHORT).show();
							}else if (power <= 5){
								Toast.makeText(getApplicationContext(), "蓝牙速录笔电池电量过低,5秒后将自动关机!", Toast.LENGTH_SHORT).show();
							}

							break;

						/**
						 * 设备扫描录入数据
						 */
						case 103:
							jsonData = jsonObject.getJSONObject("data");
							/**
							 * three kind of mode 1.is inputmethod 2.is
							 * uncheckmode 3.is checkmode
							 */
							LogUtil.i("data:" + jsonData.toString());
							LogUtil.i("mode:" + scanRecordMode);
							/*
							if (scanRecordMode == 1) {
								String scanContent = jsonData.getString("scan_text");
								String imageStr = jsonData.getString("scan_image");
								
								NoteRecord info = new NoteRecord();
								long nowTime = TimeUtil.getNowTime();

							//	NoteRecordDao mScanRecordAccess = new NoteRecordDao(
							//			BluetoothService.this);
				
									info.setNoteTitle("");
									info.setNoteContent(scanContent);
									info.setInputType(1);
								//	info.setCreateAddr(""
								//			+ ((HanvonApplication) getApplication()).curAddress);
								//	mScanRecordAccess.add(info);
								// 校对模式
							} else */if (scanRecordMode == 2) {
								String scanContent = jsonData.getString("scan_text");
								String imageStr = jsonData.getString("scan_image");
								// LogUtil.i("scanRecordMode"+PreferHelper.getBoolean(DeviceDetailActivity.isReceiveImg,
								// false));
								byte[] scanByte = Base64.decode(scanContent, Base64.DEFAULT);
								String content = new String(scanByte, "unicode");
								if (ScanNoteActivity.scanNoteAct != null)
									ScanNoteActivity.scanNoteAct
											.appendText(content);

								if (ScanNoteActivity.scanNoteAct
										.getIsSendScanImage()) {
									if(imageStr.length() != 0)
									{
										LogUtil.i("----imageStr:--"+imageStr);
										byte[] imagebyte = Base64.decode(imageStr,
												Base64.DEFAULT);

										//ungzip bmpByte
										byte[] imagebyte_ungziped= unGZip(imagebyte);
										if (imagebyte_ungziped != null){
										    Bitmap bmp = BitmapFactory.decodeByteArray(
												    imagebyte_ungziped, 0, imagebyte_ungziped.length);
										    if (bmp != null)
											    ScanNoteActivity.scanNoteAct.setScanImage(bmp);
										    //saveBitmapFile(bmp);
										}
									}
						
								}
							}
							// 输入法模式
							else if(scanRecordMode == 3){
//								if (ScanNoteActivity.scanNoteAct != null)
//								{
//									ScanNoteActivity.scanNoteAct.setInputFlag(1);
//								}
								
								PinyinIME.pinyinIME.mHandler
										.obtainMessage(
												BluetoothChatService.BLUETOOTH_MESSAGE_READ,
												jsonData).sendToTarget();
							}
							break;

						case 104:
							int result = jsonObject.getInt("result");
							Intent sleepIntent = new Intent();
							sleepIntent
									.setAction(BluetoothIntenAction.ACTION_EPEN_SLEEPTIME_CHANGE);
							sleepIntent.putExtra("result", result);
							sendBroadcast(sleepIntent);
							break;

						case 105:
							int resultCode = jsonObject.getInt("result");
							LogUtil.i("------------resultcode-------"+resultCode);
							if (resultCode == 11) {
								d = new AlertDialog.Builder(this).
										setMessage("正在进行硬件升级，大概需要4-5分钟，请不要进行按键操作，请耐心等候!").
										create();
								d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
								d.show();
								sendFileTimer = new Timer();
								TimerTask timerTask = new TimerTask() {
									 int i = 60*60;
					                 @Override 
					                 public void run() {
					                	 Message msg = new Message(); 
					                     msg.what = BluetoothChatService.BLUETOOTH_MESSAGE_SEND_TIME; 
					                     msg.arg1 = i--;
					                     msgHandler.sendMessage(msg);
					                 }
					             };
					             sendFileTimer.schedule(timerTask, 3000, 1000);// 3秒后开始倒计时，倒计时间隔为1秒  
								// create a thread and start it to send upgrade file.
								new Thread(new Runnable() {
									@Override
									public void run() {
										getBluetoothChatService().sendBTData(1,
												"/sdcard/"+HanvonApplication.HardUpdateName);
											//	"/storage/sdcard0/aa/upgradePackage.0008.zip");
										LogUtil.i("Hard send name:"+"/sdcard/"+HanvonApplication.HardUpdateName);
									}
								}).start();
							} else if (resultCode == 31){
								d.setMessage("扫描笔升级即将完成，将在5s后重启!");
								sendFileTimer.cancel();
								dialogShowTimer = new Timer();
								TimerTask timerTask = new TimerTask() {
									 int i = 5;
					                 @Override 
					                 public void run() {
					                	 Message msg = new Message(); 
					                     msg.what = BluetoothChatService.BLUETOOTH_DIALOG_SHOW_TIME; 
					                     msg.arg1 = i--;
					                     msgHandler.sendMessage(msg);
					                 }
					             };
					             dialogShowTimer.schedule(timerTask, 1000, 1000);// 1秒后开始倒计时，倒计时间隔为1秒  
							}
							break;
						case 106:
							int isReceiveImgResult = jsonObject.getInt("result");
							Intent isReceiveImgIntent = new Intent();
							isReceiveImgIntent.setAction(BluetoothIntenAction.ACTION_EPEN_RECEIVEIMG_CHANGE);
							isReceiveImgIntent.putExtra("result",isReceiveImgResult);
							sendBroadcast(isReceiveImgIntent);

							break;
						/**
						 * 功能键
						 */
						case 107:
							//校对模式
							 if (scanRecordMode == 2) {
								if (ScanNoteActivity.scanNoteAct != null)
									ScanNoteActivity.scanNoteAct
											.appendText(funcKeyCode());
								//输入法模式
							} else if(scanRecordMode == 3){
								if (ScanNoteActivity.scanNoteAct != null)
									ScanNoteActivity.scanNoteAct
											.appendText(funcKeyCode());
							}
							break;
						case 108:
							//蓝牙返回的值
							int isReceiveResult = jsonObject.getInt("result");
							Intent defaultSettingIntent = new Intent();
							defaultSettingIntent.setAction(BluetoothIntenAction.ACTION_EPEN_DEFAULTSET_CHANGE);
							defaultSettingIntent.putExtra("result", isReceiveResult);
							sendBroadcast(defaultSettingIntent);
							break;

						case 109:
							// 蓝牙返回的值
							int setLanguageResult = jsonObject.getInt("result");
							Intent languageIntent = new Intent();
							languageIntent.setAction(BluetoothIntenAction.ACTION_EPEN_LANGUAGE_CHANGE);
							languageIntent.putExtra("result", setLanguageResult);
							sendBroadcast(languageIntent);
							break;
							/**
							 * 笔端电量不足5%时,笔端的关机指示;5s后笔端自动关机提示
							 */
						case 110:
						//	Toast.makeText(getApplicationContext(), "蓝牙速录笔电池电量过低,5秒后将自动关机!", Toast.LENGTH_SHORT).show();
							break;
						case 111:
							//关机时间设置
							int resultC = jsonObject.getInt("result");
							LogUtil.i("tong----------resultC:"+resultC);
							Intent closeIntent = new Intent();
							closeIntent.setAction(BluetoothIntenAction.ACTION_EPEN_CLOSETIME_CHANGE);
							closeIntent.putExtra("result", resultC);
							sendBroadcast(closeIntent);
							break;
						case 112:
							//扫描方向
							int resultS = jsonObject.getInt("result");
							LogUtil.i("tong----------resultC:"+resultS);
							Intent scanIntent = new Intent();
							scanIntent.setAction(BluetoothIntenAction.ACTION_EPEN_SCANDIR_CHANGE);
							scanIntent.putExtra("result", resultS);
							sendBroadcast(scanIntent);
							break;
						case 113:
							//扫描笔睡眠状态更改
							jsonData = jsonObject.getJSONObject("data");
							int sleepState = jsonData.getInt("sleep_state");
							LogUtil.i("tong----------sleepState:"+sleepState);
							if (sleepState == 0){
								Toast.makeText(getApplicationContext(), "蓝牙速录笔休眠状态已唤醒!", Toast.LENGTH_SHORT).show();
								HanvonApplication.isDormant = false;
							}else if (sleepState == 1){
								Toast.makeText(getApplicationContext(), "蓝牙速录笔已进入休眠状态!", Toast.LENGTH_SHORT).show();
								HanvonApplication.isDormant = true;
							}
							SharedPreferences mSharedPreferences=getSharedPreferences("Blue", Activity.MODE_MULTI_PROCESS);
							Editor mEditor=	mSharedPreferences.edit();
							mEditor.putBoolean("isDormant", HanvonApplication.isDormant);
							mEditor.commit();
							
							Intent sleepSatateIntent = new Intent();
							sleepSatateIntent.setAction(BluetoothIntenAction.ACTION_EPEN_SLEEP_STATE_CHANGE);
							sleepSatateIntent.putExtra("result", sleepState);
							sendBroadcast(sleepSatateIntent);
							break;
						default:
							break;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogUtil.e("解析出错了");
					}

				}

			}

			break;
		case BluetoothChatService.MESSAGE_DEVICE_NAME:
			LogUtil.i("MESSAGE_DEVICE_NAME：" + msg.arg1);
			break;
		case BluetoothChatService.MESSAGE_TOAST:
			Toast.makeText(getApplicationContext(),
					msg.getData().getString(BluetoothChatService.TOAST),
					Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
	
	public void saveBitmapFile(Bitmap bitmap){
        File file=new File("/sdcard/1111111.jpg");//将要保存图片的路径  头像文件
        try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.i("-----------------1--------------------------");
		mHanVonService = this;
		PreferHelper.init(this);
		blueService = new BluetoothChatService(this, msgHandler);
		BluetoothSetting.getInstance(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
	}
	/**
	 * 开启服务
	 * 
	 * @param context
	 * @return
	 */
	public static boolean startService(Context context) {
		if (mHanVonService != null)
			return false;
		
		LogUtil.i("-----------------2--------------------------");
		Intent intent = new Intent();
		intent.setClass(context, BluetoothService.class);
		context.startService(intent);
		return true;
	}
	
	
	private Handler msgHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				handlerMsg(msg);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		};
	};
	
	private String funcKeyCode() {
		String funcKeyCode = BluetoothSetting.getFuncKeyCode();
		String textAppend = "";
		if(funcKeyCode!=null&&funcKeyCode.length()>0){
			textAppend = funcKeyCode;
		}
//		if (("0").equals(funcId)) {
//			textAppend = "\n";
//		}
//		if (("1").equals(funcId)) {
//			textAppend = "。";
//		}
		return textAppend;
	}

	/**
	 * 获取服务实例
	 * 
	 * @return
	 */
	public static BluetoothService getServiceInstance() {
		return mHanVonService;
	}
	
	public BluetoothChatService getBluetoothChatService() {
		return blueService;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
