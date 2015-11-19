package com.hanvon.bluetooth;

import java.util.HashMap;

import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.R;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.utils.LogUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.AlteredCharSequence;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothDetail extends Activity implements OnClickListener{

	private final int FUNC_KEY = 0xf1;
	private final int IDENT_CORE = 0xf2;
	private final int SLEEP_TIME = 0xf3;
	private final int CLOSE_TIME = 0Xf4;
	private final int SCAN_DIR = 0Xf5;
	
	private TextView TVelectricity;
	private TextView TVstatus;
	private ImageView BTbreak;
	private TextView TVsn;
	private TextView TVsleep;
	private TextView TVshutdown;
	private TextView TVfunction;
	private TextView TVrecognition;
	private TextView TVscandir;
	private LinearLayout LLfunction;
	private LinearLayout llrecognition;
	private LinearLayout llsleep;
	private LinearLayout llshutdown;
	private LinearLayout llrestore;
	private LinearLayout llscandir;
	
	private Handler handler;
	private BluetoothMsgReceive btMsgReceiver;
	
	
	private String[] funcKeyStrs;
	private String[] identCoreStrs;
	private String[] sleepTimeStrs;
	private String[] closeTimeStrs;
	private String[] scanDirStrs;
	private int identCoreCheckedItem = 0;
	private int identCoreChooseItem = 0;
	private int funcKeyCheckedItem = 0;
	private int checkedItem = 0;
	private int checkedCloseItem = 0;
	private int funcKeyChooseItem = 0;
	private int chooseItem = -1;
	private int closeChooseItem = -1;
	private int scanDirChoose = 0;
	private int checkedScanDirItem = 0;
	private String[] funcKeyArray = { "", "\n", " ", "\b" };
	private int[] identCoreArray = { 0, 1, 2, 3 };
	private int[] timesArray = { 2, 5, 10, -1 };
	private int[] closesArray = {30,60,90,120};
	
	private AlertDialog alertDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hardware_message);
		findViewById(R.id.hw_shutdown).setVisibility(View.GONE);
		initView();
		initData();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
	//	saveSetting();
		this.unregisterReceiver(btMsgReceiver);
	}
	
	private void registerReceiver() {
		IntentFilter mFilter = new IntentFilter(BluetoothIntenAction.ACTION_EPEN_SLEEPTIME_CHANGE);
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_RECEIVEIMG_CHANGE);
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_BATTERY_CHANGE);
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_LANGUAGE_CHANGE);
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_DEFAULTSET_CHANGE);
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_CLOSETIME_CHANGE);
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_SCANDIR_CHANGE);
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_BT_CONNECTED);
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_BT_DISCONNECT);
		this.registerReceiver(btMsgReceiver, mFilter);
	}

	private void initView() {
		TVelectricity = (TextView) this.findViewById(R.id.hw_electricity);
		TVstatus = (TextView) this.findViewById(R.id.hw_status);
		BTbreak = (ImageView) this.findViewById(R.id.hw_break);
		TVsn = (TextView) findViewById(R.id.hw_sn);
		TVfunction = (TextView)findViewById(R.id.device_function);
		TVrecognition = (TextView)findViewById(R.id.device_recognition);
		TVsleep = (TextView)findViewById(R.id.device_sleep);
		TVshutdown = (TextView)findViewById(R.id.device_shutdown);
		TVscandir = (TextView)findViewById(R.id.device_scandir);
		
		LLfunction = (LinearLayout) this.findViewById(R.id.hw_function);
		llrecognition = (LinearLayout) this.findViewById(R.id.hw_recognition);
		llsleep = (LinearLayout) this.findViewById(R.id.hw_sleep);
		llshutdown = (LinearLayout) this.findViewById(R.id.hw_shutdown);
		llrestore = (LinearLayout) findViewById(R.id.hw_restore);
		llscandir = (LinearLayout) findViewById(R.id.hw_scandir);

		
		BTbreak.setOnClickListener(this);
		LLfunction.setOnClickListener(this);

		llrecognition.setOnClickListener(this);
		llsleep.setOnClickListener(this);
		llshutdown.setOnClickListener(this);
		llrestore.setOnClickListener(this);
		llscandir.setOnClickListener(this);

		initHandler();
		btMsgReceiver = new BluetoothMsgReceive(handler);

	}

	public void initData(){
		funcKeyStrs = getResources().getStringArray(R.array.epen_func_key);
		identCoreStrs = getResources().getStringArray(R.array.epen_ident_core);
		sleepTimeStrs = getResources().getStringArray(R.array.epen_sleep_time);
		closeTimeStrs = getResources().getStringArray(R.array.epen_close_time);
		scanDirStrs = getResources().getStringArray(R.array.epen_scan_dir);

		if (BluetoothService.getServiceInstance().getBluetoothChatService().getState() == BluetoothChatService.STATE_CONNECTED){
			TVstatus.setText("已连接");
		}else{
			TVstatus.setText("已断开");
		}
		TVelectricity.setText(""+BluetoothService.getServiceInstance().curBatteryPower+"%");
		
		String version = BluetoothSetting.getBlueVersion();
		TVsn.setText(version);

		String funcKeySetting = BluetoothSetting.getFuncKeyCode();
		for (int i = 0; i < funcKeyArray.length; i++) {
			if (funcKeyArray[i].equals(funcKeySetting)) {
				funcKeyCheckedItem = i;
				break;
			}
		}
		TVfunction.setText(funcKeyStrs[funcKeyCheckedItem]);

		// 识别核心选择的显示
		String identCoreSetting = BluetoothSetting.getIdentCoreCode();

		if("ch".equals(identCoreSetting)){
			identCoreCheckedItem = 0;
		}else if("eng".equals(identCoreSetting)){
			identCoreCheckedItem = 1;
		}else if("ja".equals(identCoreSetting)){
			identCoreCheckedItem = 2;
		}else if("ko".equals(identCoreSetting)){
			identCoreCheckedItem = 3;
		}

		TVrecognition.setText(identCoreStrs[identCoreCheckedItem]);

		checkedItem = BluetoothSetting.getSleepTime();
		TVsleep.setText(sleepTimeStrs[checkedItem]);
		
		//设置关机时间
		checkedCloseItem = BluetoothSetting.getShutdownTime();
		TVshutdown.setText(closeTimeStrs[checkedCloseItem]);
		
		checkedScanDirItem = BluetoothSetting.getBlueScanDir();
		TVscandir.setText(scanDirStrs[checkedScanDirItem]);
	}
	private void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case BluetoothMsgReceive.DEFAULTSET_CHANGE:
					int resultD = msg.arg1;
					if (resultD == 1) {
						Toast.makeText(BluetoothDetail.this, "恢复默认设置成功", Toast.LENGTH_SHORT).show();
						setFactory();
					} else {
						Toast.makeText(BluetoothDetail.this, "恢复默认设置失败", Toast.LENGTH_SHORT).show();
					}
					break;
				case BluetoothMsgReceive.SLEEPTIME_CHANGE:
					LogUtil.i("tong---SLEEPTIME_CHANGE");
					int result = msg.arg1;
					if (result == 1) {
						checkedItem = chooseItem;
						TVsleep.setText(sleepTimeStrs[checkedItem]);
						Toast.makeText(BluetoothDetail.this, "设置睡眠时间成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(BluetoothDetail.this,"设置睡眠时间失败", Toast.LENGTH_SHORT).show();
					}
					break;
				case BluetoothMsgReceive.CLOSETIME_CHANGE:
					LogUtil.i("tong---CLOSETIME_CHANGE");
					int resultc = msg.arg1;
					if (resultc == 1) {
						checkedCloseItem = closeChooseItem;
						TVshutdown.setText(closeTimeStrs[checkedCloseItem]);
						Toast.makeText(BluetoothDetail.this,"设置关机时间成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(BluetoothDetail.this,"设置关机时间失败", Toast.LENGTH_SHORT).show();
					}
					break;
				case BluetoothMsgReceive.SCANDIR_CHANGE:
					LogUtil.i("tong---SCANDIR_CHANGE");
					int results = msg.arg1;
					if (results == 1) {
						checkedScanDirItem = scanDirChoose;
						TVscandir.setText(scanDirStrs[checkedScanDirItem]);
						Toast.makeText(BluetoothDetail.this,"设置扫描方向成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(BluetoothDetail.this,"设置扫描方向失败", Toast.LENGTH_SHORT).show();
					}
					break;
				case BluetoothMsgReceive.LANGUAGE_CHANGE:
					LogUtil.i("tong---LANGUAGE_CHANGE");
					int resultL = msg.arg1;
					if (resultL == 1) {
						identCoreCheckedItem = identCoreChooseItem;
						TVrecognition.setText(identCoreStrs[identCoreCheckedItem]);
						Toast.makeText(BluetoothDetail.this,"更改识别语言成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(BluetoothDetail.this,"更改识别语言失败", Toast.LENGTH_SHORT).show();
					}
					break;
				case BluetoothMsgReceive.BATTERY_CHANGED:
					LogUtil.i("changed");
					int power = msg.arg1;
					int status = msg.arg2;
					TVelectricity.setText(power+"%");
					break;
				case BluetoothMsgReceive.BT_CONNECTED:
					TVstatus.setText("已连接");
					break;
				case BluetoothMsgReceive.BT_DISCONNECT:
					TVstatus.setText("已断开");
					break;
				default:
					break;
				}
			}
		};
	}
	
	public void setFactory(){
		
		BluetoothSetting.setFuncKeyCode("");
		funcKeyCheckedItem = 0;
		BluetoothSetting.setIdentCoreCode("ch");
		identCoreCheckedItem = 0;
		BluetoothSetting.setSleepTime(1);
		checkedItem = 1;
		BluetoothSetting.setBlueScanDir(0);
		checkedScanDirItem = 0;
		BluetoothSetting.setBlueIsSendImage(false);
		BluetoothSetting.writeBack();

		TVfunction.setText(funcKeyStrs[funcKeyCheckedItem]);
		TVrecognition.setText(identCoreStrs[identCoreCheckedItem]);
		TVsleep.setText(sleepTimeStrs[checkedItem]);
		TVshutdown.setText(closeTimeStrs[checkedCloseItem]);
		TVscandir.setText(scanDirStrs[checkedScanDirItem]);
	}
	
	@Override
	public void onClick(View v) {
		if (BluetoothService.getServiceInstance().getBluetoothChatService()
				.getState() != BluetoothChatService.STATE_CONNECTED)
		{
			return;
		}
		if (HanvonApplication.isDormant){
			Toast.makeText(this, "蓝牙扫描笔进入休眠状态，请按power键进行唤醒！", Toast.LENGTH_SHORT).show();
			return;
		}
		switch(v.getId()){
		    case R.id.hw_break:
		    	
		    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage(R.string.msg_stop_connected);
				dialog.setCancelable(false);
				dialog.setNegativeButton(R.string.button_cancel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								})
						.setPositiveButton(R.string.ensure,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										stopConnect();
									}
								}).show();
			    break;
		    case R.id.hw_function:
		    	createDlg(FUNC_KEY, funcKeyStrs, funcKeyCheckedItem);
				alertDialog.show();
			    break;
		    case R.id.hw_recognition:
		    	createDlg(IDENT_CORE, identCoreStrs, identCoreCheckedItem);
				alertDialog.show();
			    break;
		    case R.id.hw_sleep:
		    	createDlg(SLEEP_TIME, sleepTimeStrs, checkedItem);
				alertDialog.show();
			    break;
		    case R.id.hw_shutdown:
		    	createDlg(CLOSE_TIME, closeTimeStrs, checkedCloseItem);
				alertDialog.show();
			    break;
		    case R.id.hw_scandir:
		    	createDlg(SCAN_DIR, scanDirStrs, checkedScanDirItem);
				alertDialog.show();
			    break;
		    case R.id.hw_restore:
		    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.comeback_tips)
						.setMessage(R.string.comeback_message)
						.setPositiveButton(R.string.ensure, new android.content.DialogInterface.OnClickListener(){

							public void onClick(DialogInterface dialog, int which){
								// 向设备发送数据，请求恢复默认设置
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("default_setting", "default");
								BluetoothService.getServiceInstance()
										.getBluetoothChatService()
										.sendBTData(2,BluetoothDataPackage.epenFactoryMode(map));
							}
						}).create().show();
			    break;
			default:
				break;
		}
	}
	
	private void createDlg(final int type, String[] Strs, int myCheckedItem) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String title = "";
		switch (type) {
		case FUNC_KEY:
			title = getResources().getString(R.string.hw_function);
			break;

		case IDENT_CORE:
			title = getResources().getString(R.string.hw_recognition);
			break;
		case SLEEP_TIME:
			title = getResources().getString(R.string.hw_sleep);
			break;
		case CLOSE_TIME:
			title = getResources().getString(R.string.hw_shutdown);
			break;
		case SCAN_DIR:
			title = getResources().getString(R.string.hw_scandir);
			break;
		}
		builder.setTitle(title).setSingleChoiceItems(Strs, myCheckedItem,
						new android.content.DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,int which) {
								switch (type) {
								case FUNC_KEY:
									// 功能键设定
									funcKeyChooseItem = which;
									dialog.dismiss();
									switch (funcKeyChooseItem) {
									case 0:
										BluetoothSetting.setFuncKeyCode("");
										break;
									case 1:
										BluetoothSetting.setFuncKeyCode("\n");
										break;
									case 2:
										BluetoothSetting.setFuncKeyCode(" ");
										break;
									case 3:
										BluetoothSetting.setFuncKeyCode("\b");
										break;

									default:
										break;
									}
									BluetoothSetting.writeBack();
									funcKeyCheckedItem = funcKeyChooseItem;
									TVfunction.setText(funcKeyStrs[funcKeyChooseItem]);
									break;

								case IDENT_CORE:
									// 识别核心设置
									identCoreChooseItem = which;
									dialog.dismiss();
									String identLang = "";
									switch (identCoreChooseItem) {
									case 0:
										identLang = "ch";
										break;
									case 1:
										identLang = "eng";
										break;
									case 2:
										identLang = "ja";
										break;
									case 3:
										identLang = "ko";
										break;
									default:
										break;
									}
									BluetoothSetting.setIdentCoreCode(identLang);
									BluetoothSetting.writeBack();
									//向速录笔发送设置信息
									sendLanguageToEpen(identLang);
									break;
								case SLEEP_TIME:
									chooseItem = which;
									BluetoothSetting.setSleepTime(chooseItem);
									BluetoothSetting.writeBack();
									dialog.dismiss();
									sendTimeToEpen();
									break;
								case CLOSE_TIME:
									closeChooseItem = which;
									BluetoothSetting.setShutdownTime(closeChooseItem);
									BluetoothSetting.writeBack();
									dialog.dismiss();
									sendCloseTimeToEpen();
									break;
								case SCAN_DIR:
									scanDirChoose = which;
									BluetoothSetting.setBlueScanDir(scanDirChoose);
									BluetoothSetting.writeBack();
									dialog.dismiss();
									sendScanDirToEpen();
									break;
								}

							}
						}).setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog = builder.create();
	}

	
	/**
	 * 断开连接
	 */
	private void stopConnect() {
		BluetoothService.getServiceInstance().getBluetoothChatService().stop();
		Intent Intent = new Intent(this, MainActivity.class);
    	startActivity(Intent);
    	this.finish();
	}
	
	private void sendLanguageToEpen(String lang) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("language", lang);
		BluetoothService.getServiceInstance().getBluetoothChatService()
				.sendBTData(2, BluetoothDataPackage.epenLanguageIdent(map));
	}
	
	private void sendTimeToEpen() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("sleep_time", "" + timesArray[chooseItem]);
		BluetoothService.getServiceInstance().getBluetoothChatService()
				.sendBTData(2, BluetoothDataPackage.epenSleepTime(map));
	}
	
	private void sendCloseTimeToEpen() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("powerdown_time", "" + closesArray[closeChooseItem]);
		BluetoothService.getServiceInstance().getBluetoothChatService()
				.sendBTData(2, BluetoothDataPackage.epenCloseTime(map));
	}

	private void sendScanDirToEpen(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("scandirection", "" + scanDirChoose);
		BluetoothService.getServiceInstance().getBluetoothChatService()
				.sendBTData(2, BluetoothDataPackage.epenScanDir(map));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BluetoothSetting.releaseInstance();
	}
}
