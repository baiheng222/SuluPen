package com.hanvon.sulupen;

import java.util.ArrayList;
import java.util.List;

import com.hanvon.bluetooth.BluetoothChatService;
import com.hanvon.bluetooth.ConstPool;
import com.hanvon.sulupen.datas.IntentAction;
import com.hanvon.sulupen.R;
import com.hanvon.sulupen.pinyin.Settings;
import com.hanvon.bluetooth.BluetoothMsgReceive;
import com.hanvon.bluetooth.BluetoothService;
import com.hanvon.sulupen.utils.CustomDialog;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.UiUtil;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SuLuInputActivity extends Activity implements OnClickListener, OnLongClickListener {
	private TextView mChoose;
	private TextView mSwitch;
	
	private ImageView mSettingSoundSwitch;
	private ImageView mSettingVibrateSwitch;
	private ImageView mSettingPredicationSwitch;
	
	//private Button mStartInput;
	private List<InputMethodInfo> inputMethodInfoList;
	private String packageName="com.hanvon.sulupen";
	private boolean isOK = false;
//	private BluetoothMsgReceive btMsgReceiver;
//	private Handler mHandler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case BluetoothMsgReceive.BT_CONNECTED:
//				((ImageView) findViewById(R.id.home_go_home))
//						.setBackgroundResource(R.drawable.epen_manager);
//				break;
//			case BluetoothMsgReceive.BT_DISCONNECT:
//				((ImageView) findViewById(R.id.home_go_home))
//						.setBackgroundResource(R.drawable.epen_manager_nor);
//				break;
//			}
//		};
//	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_inputmethod_setting);
		Settings.getInstance(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
		//btMsgReceiver=new BluetoothMsgReceive(mHandler);
		initView();
		initData();
	}
	private void initView() {
	//((TextView)findViewById(R.id.home_title)).setText(R.string.sulu_input);
		((ImageView)findViewById(R.id.iv_comeback)).setOnClickListener(this);
		mChoose = (TextView) findViewById(R.id.tv_choose);
		mSwitch = (TextView) findViewById(R.id.tv_switch);
	
		
		mSettingSoundSwitch = (ImageView) findViewById(R.id.setting_sound_switch);
		mSettingVibrateSwitch = (ImageView) findViewById(R.id.setting_vibrate_switch);
		mSettingPredicationSwitch = (ImageView) findViewById(R.id.setting_predication_switch);
		
		boolean mKeySound = Settings.getKeySound();
		if (mKeySound) {
			mSettingSoundSwitch.setBackgroundResource(R.drawable.switch_on);
		} else {
			mSettingSoundSwitch.setBackgroundResource(R.drawable.switch_off);
		}
		boolean mkeyvibrate = Settings.getVibrate();
		if (mkeyvibrate) {
			mSettingVibrateSwitch.setBackgroundResource(R.drawable.switch_on);
		} else {
			mSettingVibrateSwitch.setBackgroundResource(R.drawable.switch_off);
		}
		boolean mPrediction = Settings.getPrediction();
		if (mPrediction) {
			mSettingPredicationSwitch
					.setBackgroundResource(R.drawable.switch_on);
		} else {
			mSettingPredicationSwitch
					.setBackgroundResource(R.drawable.switch_off);
		}
		
		mChoose.setOnClickListener(this);
		mSwitch.setOnClickListener(this);
		
		//((ImageView) findViewById(R.id.home_go_home)).setOnClickListener(this);
		//((ImageView) findViewById(R.id.home_go_home)).setOnLongClickListener(this);
		
	
		mSettingSoundSwitch.setOnClickListener(this);
		mSettingVibrateSwitch.setOnClickListener(this);
		mSettingPredicationSwitch.setOnClickListener(this);
		
		}
	private void initData() {
		if (ConstPool.SwitchInputMethod) {
			mSwitch.setText(R.string.open_bn_switched);
			//mSwitch.setBackgroundResource(R.color.open_bn_unenabled_bg);
			mSwitch.setEnabled(false);
		} else {
			mSwitch.setText(R.string.open_bn_switch);
			//mSwitch.setBackgroundResource(R.drawable.open_btn_bg);
			mSwitch.setEnabled(true);
		}
		inputMethodInfoList = new ArrayList<InputMethodInfo>();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.INPUT_METHOD_CHANGED");
		filter.addAction(ConstPool.SWITCH_OFF_INPUTMETHOD_ACTION);
		filter.addAction(ConstPool.SWITCH_ON_INPUTMETHOD_ACTION);
		registerReceiver(inputMethodReciver, filter);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		inputMethodInfoList = ((InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE))
				.getEnabledInputMethodList();
		int size = inputMethodInfoList.size();
		try {
			packageName = getPackageManager().getPackageInfo("",
					0).packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		LogUtil.i("tong----packagename:"+packageName);
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				LogUtil.i("ID:" + inputMethodInfoList.get(i).getId() +

				"包名：" + inputMethodInfoList.get(i).getPackageName() + "-\n");
				if (packageName.equals(inputMethodInfoList.get(i)
						.getPackageName())) {
					isOK = true;
					ConstPool.ChooseInputMethod = true;
				}
			}
		}
		if (isOK) {
			LogUtil.i("tong------isOK TRUE");
			ConstPool.ChooseInputMethod = true;
			mChoose.setText(R.string.open_bn_choosed);
			//mChoose.setBackgroundResource(R.color.open_bn_unenabled_bg);
			mChoose.setEnabled(false);
		} else {
			ConstPool.ChooseInputMethod = false;
			mChoose.setText(R.string.open_bn_choose);
			//mChoose.setBackgroundResource(R.drawable.open_btn_bg);
			mChoose.setEnabled(true);
		}

		initBroast();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 把用户的设置存入配置文件中
		Settings.writeBack();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_choose:
			/**
			 * com.android.settings.LanguageSettings 语言和键盘设置
			 * 　　com.android.settings.LocalePicker 选择手机语言
			 * 　　com.android.settings.LocalePickerInSetupWizard 选择手机语言
			 */
			try {
				Intent intent = new Intent();
				ComponentName comp = new ComponentName("com.android.settings",
						"com.settings.INPUT_METHOD_SETTINGS");
				intent.setComponent(comp);
				intent.setAction("android.intent.action.VIEW");
				startActivityForResult(intent, RESULT_OK);
			} catch (Exception e) {
				try {
					startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS"));
				} catch (Exception ex) {
					UiUtil.showToast(this, "无法打开，请进入系统界面手动设置！");
					ex.printStackTrace();
				}
				e.printStackTrace();
			}
			break;
		case R.id.tv_switch:
			try {
				((InputMethodManager) getApplicationContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.showInputMethodPicker();
			} catch (Exception e) {
				UiUtil.showToast(this, "无法打开，请进入系统界面手动设置！");
				e.printStackTrace();

			}	
			break;
//		case R.id.bn_start_input:
//			startActivity(new Intent(SuLuInputActivity.this, MainActivity.class));
//			break;
		case R.id.setting_sound_switch:
			boolean mKeySound = Settings.getKeySound();
			if (mKeySound) {
				Settings.setKeySound(false);
				mSettingSoundSwitch
						.setBackgroundResource(R.drawable.switch_off);
			} else {
				Settings.setKeySound(true);
				mSettingSoundSwitch.setBackgroundResource(R.drawable.switch_on);
			}

			break;
		case R.id.setting_vibrate_switch:
			boolean mkeyvibrate = Settings.getVibrate();
			if (mkeyvibrate) {
				Settings.setVibrate(false);
				mSettingVibrateSwitch
						.setBackgroundResource(R.drawable.switch_off);
			} else {
				Settings.setVibrate(true);
				mSettingVibrateSwitch
						.setBackgroundResource(R.drawable.switch_on);
			}
			break;
		case R.id.setting_predication_switch:
			boolean mPrediction = Settings.getPrediction();
			if (mPrediction) {
				Settings.setPrediction(false);
				mSettingPredicationSwitch
						.setBackgroundResource(R.drawable.switch_off);
			} else {
				Settings.setPrediction(true);
				mSettingPredicationSwitch
						.setBackgroundResource(R.drawable.switch_on);
			}
			break;
		case R.id.iv_comeback:
			finish();
			break;
//		case R.id.home_go_home:
//			if (isConnected()) {
//				startActivity(new Intent(this, DeviceDetailActivity.class));
//			} else {
//				startActivity(new Intent(this, DeviceListActivity.class));
//			}
//			break;
		default:
			break;
		}
	}
	private void initBroast(){
//		IntentFilter mFilter = new IntentFilter(
//				IntentAction.ACTION_EPEN_BATTERY_CHANGE);
//		mFilter.addAction(IntentAction.ACTION_EPEN_BT_CONNECTED);
//		mFilter.addAction(IntentAction.ACTION_EPEN_BT_DISCONNECT);
//		this.registerReceiver(btMsgReceiver, mFilter);
//		if (BluetoothService.getServiceInstance().getBluetoothChatService()
//				.getState() == BluetoothChatService.STATE_CONNECTED) {
//			((ImageView) findViewById(R.id.home_go_home))
//					.setBackgroundResource(R.drawable.epen_manager);
//		} else {
//			((ImageView) findViewById(R.id.home_go_home))
//					.setBackgroundResource(R.drawable.epen_manager_nor);
//		}
	}
	/**
	 * 是否已连接笔
	 * 
	 * @return
	 */
	private boolean isConnected() {
		return BluetoothService.getServiceInstance().getBluetoothChatService()
				.getState() == BluetoothChatService.STATE_CONNECTED ? true
				: false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(inputMethodReciver);
	//	unregisterReceiver(btMsgReceiver);
	}
	private BroadcastReceiver inputMethodReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			LogUtil.i("change");
			// arg1.getExtras().get;
			if (intent.getAction().equals(
					ConstPool.SWITCH_ON_INPUTMETHOD_ACTION)){
				mSwitch.setText(R.string.open_bn_switched);
			//	mSwitch.setBackgroundResource(R.color.open_bn_unenabled_bg);
				mSwitch.setEnabled(false);
				//mStartInput.setEnabled(true);
			} else if (intent.getAction().equals(
					ConstPool.SWITCH_OFF_INPUTMETHOD_ACTION)) {
				mSwitch.setText(R.string.open_bn_switch);
			//	mSwitch.setBackgroundResource(R.drawable.open_btn_bg);
				mSwitch.setEnabled(true);
			} else if (intent.getAction().equals(
					Intent.ACTION_INPUT_METHOD_CHANGED)) {
			}
		}
	};
	/**
	 * 断开连接
	 */
	private void stopConnect() {
		BluetoothService.getServiceInstance().getBluetoothChatService().stop();
	}

	@Override
	public boolean onLongClick(View v) {
//		switch (v.getId()) {
//		case R.id.home_go_home:
//			if (isConnected()) {// 如果连接
//				new CustomDialog.Builder(this)
//						.setMessage(R.string.msg_stop_connected)
//						.setNegativeButton(R.string.cancel,
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//
//									}
//								})
//						.setPositiveButton(R.string.bn_sure,
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										stopConnect();
//									}
//								}).show();
//			} else {
//				// 如果没有连接就去搜索连接
//				startActivity(new Intent(this, DeviceListActivity.class));
//			}
//			break;
//		}
		return false;
	}	
}
