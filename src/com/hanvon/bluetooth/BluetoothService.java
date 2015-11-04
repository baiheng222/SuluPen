package com.hanvon.bluetooth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;
import com.hanvon.sulupen.ScanNoteActivity;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.datas.ScanRecordInfo;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.helper.PreferHelper;
import com.hanvon.sulupen.login.LoginActivity;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.TimeUtil;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Base64;
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
	public static int scanRecordMode = 2;
	
	
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
		}
		return b;
	}
	/**
	 * Handler 消息接收器
	 * 
	 * @param msg
	 */
	public void handlerMsg(Message msg) {
		switch (msg.what) {
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
				// LogUtil.i("接收包:"+jsonObject.toString());
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
							BluetoothSetting.setSeralNumber(serialNum);
							/********************add by chenxzhuang*****************/
							String version = jsonData.getString("device_version");
							LogUtil.i("----device_version:"+version);
					//		HardUpdate hardUpdate = new HardUpdate(HanVonService.this);
					//		hardUpdate.checkVersionUpdate(version);
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
							LogUtil.i("power:" + power);
							Intent intent = new Intent();
							intent.setAction(BluetoothIntenAction.ACTION_EPEN_BATTERY_CHANGE);
							intent.putExtra("epen_power", power);
							intent.putExtra("epen_status", status);
							sendBroadcast(intent);

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
							} else */if (scanRecordMode == 2 || scanRecordMode == 1) {
								String scanContent = jsonData.getString("scan_text");
								String imageStr = jsonData.getString("scan_image");
								// LogUtil.i("scanRecordMode"+PreferHelper.getBoolean(DeviceDetailActivity.isReceiveImg,
								// false));
								if (ScanNoteActivity.scanNoteAct != null)
									ScanNoteActivity.scanNoteAct
											.appendText(scanContent);

								if (ScanNoteActivity.scanNoteAct
										.getIsSendScanImage()) {
									if(imageStr.length() != 0)
									{
										byte[] imagebyte = Base64.decode(imageStr,
												Base64.DEFAULT);
										
										//ungzip bmpByte
										byte[] imagebyte_ungziped= unGZip(imagebyte);
										Bitmap bmp = BitmapFactory.decodeByteArray(
												imagebyte_ungziped, 0, imagebyte_ungziped.length);
										if (bmp != null)
											ScanNoteActivity.scanNoteAct.setScanImage(bmp);
										//saveBitmapFile(bmp);
									}
						
								}
							}
							// 输入法模式
							else {
						//		PinyinIME.pinyinIME.mHandler
							//			.obtainMessage(
							//					BluetoothChatService.BLUETOOTH_MESSAGE_READ,
							//					jsonData).sendToTarget();
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
							//	mProgressDialog = ProgressDialog.show(this,"提示", "正在进行硬件升级，大概需要4-5分钟，请不要进行按键操作，请耐心等候!",true,false);
								// create a thread and start it to send upgrade file.
								new Thread(new Runnable() {
									@Override
									public void run() {
									//	getBluetoothChatService().sendBTData(1,
									//			"/mnt/sdcard/upgradeTest.zip");
									//			"/sdcard/"+HanvonApplication.HardUpdateName);
									}
								}).start();
							} else if (resultCode == 31){
							//	mProgressDialog.dismiss();
								Toast.makeText(this, "扫描笔升级即将完成，将在5s后重启!", Toast.LENGTH_SHORT).show();
								// UiUtil.showToast(this, "响应结果:"+resultCode);
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
							//盲扫模式
							if (scanRecordMode == 1) {

								//校对模式
							} else if (scanRecordMode == 2) {
								if (ScanNoteActivity.scanNoteAct != null)
									ScanNoteActivity.scanNoteAct
											.appendText(funcKeyCode());
								//输入法模式
							} else {
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

						//	UiUtil.showToast(getApplicationContext(),
						//			"蓝牙速录笔电池电量过低,5秒后将自动关机!");

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
	
	@Override
	public void onCreate() {
		super.onCreate();
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
		Intent intent = new Intent();
		intent.setClass(context, BluetoothService.class);
		context.startService(intent);
		return true;
	}
	
	
	private Handler msgHandler = new Handler() {
		public void handleMessage(Message msg) {
			handlerMsg(msg);

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
