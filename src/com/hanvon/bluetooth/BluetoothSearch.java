package com.hanvon.bluetooth;

import java.util.ArrayList;

import com.hanvon.sulupen.R;
import com.hanvon.sulupen.utils.LogUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;



public class BluetoothSearch extends Activity implements OnClickListener{

	public static String curBtAddress = "";
	private String connectAddress;
	private BluetoothAdapter mBtAdapter;
	private String deviceInfo = null;
	private BluetoothMsgReceive btMsgReceiver;
	private boolean isDoDiscovery = false;
	private boolean isDoConnect = false;
	private ArrayList<String> noPairedDeviceList = null;
	private int deviceCount = 0;
	
	
	private TextView tvMessage;
	Button bnLeft, bnRight;
	LinearLayout layoutRight;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BluetoothMsgReceive.BT_CONNECTED:
			//	HanvonApplication.lastConnectedTime = TimeUtil.getcurTimeYMDHM();
				BluetoothSearch.this.finish();
				break;
			case BluetoothMsgReceive.BT_DISCONNECT:
				setProgressBarIndeterminateVisibility(false);
				setTitle("连接失败....");
				setMessage(R.string.msg_fail);
				bnLeft.setVisibility(View.VISIBLE);
				layoutRight.setVisibility(View.VISIBLE);
				bnRight.setText(R.string.bnSearchNewDevice);
				bnLeft.setText(R.string.button_cancel);
				break;
			}
		};
	};

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.device_list);
		setFinishOnTouchOutside(false);
		
		Intent intent = getIntent();
		if (intent != null){
			deviceInfo = intent.getStringExtra("device");
		}
		Init();
	} 
	
	
	public void Init(){
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		btMsgReceiver = new BluetoothMsgReceive(mHandler);
		
	//	onStart();
		
		if (noPairedDeviceList == null)
			noPairedDeviceList = new ArrayList<String>();
		
		tvMessage = (TextView) findViewById(R.id.tvHintMessage);
		bnRight = (Button) findViewById(R.id.bnRight);
		bnLeft = (Button) findViewById(R.id.bnLeft);
		layoutRight = (LinearLayout) findViewById(R.id.layoutRight);
		
		bnRight.setOnClickListener(this);
		bnLeft.setOnClickListener(this);
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_BT_CONNECTED);
		mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_BT_DISCONNECT);
		this.registerReceiver(btMsgReceiver, mFilter);
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(mReceiver, filter);
		if (curBtAddress.equals("")) {
			curBtAddress = BluetoothSetting.getBlueAddress();
		}

		if (curBtAddress.equals("")) {
			doDiscovery();
		} else {
			connectAddress = curBtAddress;
			tryConnect();
		}
	}
	
	/**
	 * 蓝牙广播
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					String name = device.getName();
					String deviceInfo = name + "\n" + device.getAddress();
					LogUtil.i("----DeviceInfo---:"+deviceInfo);
					//汉王速录笔以"hanvon-scanpen"开始，如果不是汉王速录笔，不在列表中进行显示
					if (deviceInfo.indexOf("hanvon-scanpen") != -1){
						connectAddress = deviceInfo.substring(deviceInfo.length() - 17);
						if (noPairedDeviceList.indexOf(deviceInfo) == -1) {
						    deviceCount++;
						//    setMessage("发现" + deviceCount + "个可连设备");
						    if(deviceCount == 1){
						        noPairedDeviceList.add(deviceInfo);
						    }
					    }
					}
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
		//		setTitle("搜索完成");
				if (noPairedDeviceList.size() == 0){
				    if (deviceInfo != null){
					    // 自动连接
					    tryConnect();
				    }else{
				    	setMessage(R.string.none_found);
						bnLeft.setVisibility(View.VISIBLE);
						layoutRight.setVisibility(View.VISIBLE);
						bnLeft.setText(R.string.button_cancel);
						bnRight.setText(R.string.bnTryAgain);
				    }
				}
				
				if (noPairedDeviceList.size() == 1) {
					String info = noPairedDeviceList.get(0);
					connectAddress = info.substring(info.length() - 17);
					// 自动连接
					tryConnect();
				}
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:
					Log.d("BlueToothTestActivity", "正在配对......");
					break;
				case BluetoothDevice.BOND_BONDED:
					Log.d("BlueToothTestActivity", "完成配对");
					break;
				case BluetoothDevice.BOND_NONE:
					Log.d("BlueToothTestActivity", "取消配对");
				default:
					break;
				}
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				LogUtil.w("ACTION_CONNECTION_STATE_CHANGED");
				switch (mBtAdapter.getState()) {
				case BluetoothAdapter.STATE_ON:
					LogUtil.i("STATE_ON");
					if (isDoDiscovery)
						doDiscovery();
					if (isDoConnect)
						tryConnect();
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					setTitle(R.string.title_openingBT);
					setMessage(R.string.msg_openingBT);
					LogUtil.i("STATE_TURNING_ON");
					break;
				case BluetoothAdapter.STATE_OFF:
					LogUtil.i("STATE_OFF");
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					LogUtil.i("STATE_TURNING_OFF");
					break;
				case BluetoothAdapter.STATE_CONNECTED:
					LogUtil.i("STATE_CONNECTED");
					break;
				case BluetoothAdapter.STATE_CONNECTING:
					LogUtil.i("STATE_CONNECTING");
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					LogUtil.i("STATE_DISCONNECTED");
					break;
				case BluetoothAdapter.STATE_DISCONNECTING:
					LogUtil.i("STATE_DISCONNECTING");
					break;

				default:
					break;
				}

			} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getAddress().equals(curBtAddress)) {
					LogUtil.i("信号太弱，无法连接");
				}

				LogUtil.i("ACTION_ACL_DISCONNECTED");
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED
					.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getAddress().equals(curBtAddress)) {
					LogUtil.i("设备信号太弱，请求断开连接");
				}

				LogUtil.i("ACTION_ACL_DISCONNECT_REQUESTED");
			} else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getAddress().equals(curBtAddress)) {
					LogUtil.i("设备信号正常，可以连接");
				}
				LogUtil.i("ACTION_ACL_CONNECTED");
			}

		}
	};

	
	/**
	 * 强制打开蓝牙
	 */
	private void openBlueTooth() {
		try {
			mBtAdapter.enable();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * 尝试连接
	 */
	private void tryConnect() {
		if (!mBtAdapter.isEnabled()) {
			isDoConnect = true;
			openBlueTooth();
		} else {
			setProgressBarIndeterminateVisibility(true);
			setTitle("连接中....");
			setMessage(R.string.msg_wait_connect);

			bnLeft.setVisibility(View.GONE);
			layoutRight.setVisibility(View.GONE);
			BluetoothService.getServiceInstance().getBluetoothChatService()
					.connect(mBtAdapter.getRemoteDevice(connectAddress));
		}

	}
	
	/**
	 * 开始搜索扫描
	 */
	private void doDiscovery() {

		if (!mBtAdapter.isEnabled()) {
			isDoDiscovery = true;
			openBlueTooth();
		} else {
			setProgressBarIndeterminateVisibility(true);
			setTitle(R.string.scanning);
			if (mBtAdapter.isDiscovering()) {
				mBtAdapter.cancelDiscovery();
			}
			mBtAdapter.startDiscovery();
			setMessage(R.string.msg_searching);
			layoutRight.setVisibility(View.GONE);
			bnLeft.setVisibility(View.VISIBLE);

			deviceCount = 0;
			noPairedDeviceList.clear();
	//		mNewDevicesArrayAdapter.notifyDataSetChanged();
		}
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bnRight:
			if (deviceCount == 0) {
				doDiscovery();
				return;
			}
			tryConnect();
			break;
		case R.id.bnLeft:
			if (mBtAdapter.isDiscovering()) {
				mBtAdapter.cancelDiscovery();
				this.finish();
				return;
			}
			this.finish();
			break;

		default:
			break;
		}

		
	}
	
	private void setMessage(int resStr) {
		if (tvMessage.getVisibility() != View.VISIBLE) {
			tvMessage.setVisibility(View.VISIBLE);
		}
		tvMessage.setText(resStr);
	}
	
	private void setMessage(String msg) {
		if (tvMessage.getVisibility() != View.VISIBLE) {
			tvMessage.setVisibility(View.VISIBLE);
		}
		tvMessage.setText(msg);
	}

	private void disMissMsg() {
		if (tvMessage.getVisibility() == View.VISIBLE) {
			tvMessage.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i("");
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}

		this.unregisterReceiver(mReceiver);
		this.unregisterReceiver(btMsgReceiver);
	}
}
