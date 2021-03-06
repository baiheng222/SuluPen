package com.hanvon.bluetooth;

import com.hanvon.sulupen.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @desc 笔端广播接收器类
 * @author chenxzhuang
 * @date 2015-10-20 下午4:37:59
 */
public class BluetoothMsgReceive extends BroadcastReceiver{

	
	private Handler handler;
	public final static int BATTERY_CHANGED = 0x1001;
	public final static int BT_CONNECTED = 0x1002;
	public final static int BT_DISCONNECT = 0x1003;
	public final static int SLEEPTIME_CHANGE = 0x1004;
	public final static int RECEIVEIMG_CHANGE = 0x1005;
	public final static int LANGUAGE_CHANGE = 0x1006;
	public final static int DEFAULTSET_CHANGE = 0x1007;
	public final static int CLOSETIME_CHANGE = 0x1008;
	public final static int SCANDIR_CHANGE = 0x1009;

	public final static int SLEEP_STATE_CHANGE = 0x1010;
	public final static int HARD_WARE_UPDATE = 0x1011;
	
	public BluetoothMsgReceive(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(BluetoothIntenAction.ACTION_EPEN_BATTERY_CHANGE)) {
			LogUtil.i("ACTION_EPEN_BATTERY_CHANGE");
			int power = intent.getIntExtra("epen_power", 0);
			int status = intent.getIntExtra("epen_status", 0);
			updateBatterDetail(power, status);
		} else if (action.equals(BluetoothIntenAction.ACTION_EPEN_BT_CONNECTED)) {
			Message msg = handler.obtainMessage(BT_CONNECTED, 0, 0);
			handler.sendMessage(msg);
		} else if (action.equals(BluetoothIntenAction.ACTION_EPEN_BT_DISCONNECT)) {
			Message msg = handler.obtainMessage(BT_DISCONNECT, 0, 0);
			handler.sendMessage(msg);
		} else if (action.equals(BluetoothIntenAction.ACTION_EPEN_SLEEPTIME_CHANGE)) {
			int result = intent.getIntExtra("result", 0);
			Message msg = handler.obtainMessage(SLEEPTIME_CHANGE, result, 0);
			handler.sendMessage(msg);
		} else if (action.equals(BluetoothIntenAction.ACTION_EPEN_CLOSETIME_CHANGE)) {
			int result = intent.getIntExtra("result", 0);
			Message msg = handler.obtainMessage(CLOSETIME_CHANGE, result, 0);
			handler.sendMessage(msg);
		} else if (action.equals(BluetoothIntenAction.ACTION_EPEN_SCANDIR_CHANGE)) {
			int result = intent.getIntExtra("result", 0);
			Message msg = handler.obtainMessage(SCANDIR_CHANGE, result, 0);
			handler.sendMessage(msg);
		}else if (action.equals(BluetoothIntenAction.ACTION_EPEN_SLEEP_STATE_CHANGE)) {
			int result = intent.getIntExtra("result", 0);
			Message msg = handler.obtainMessage(SLEEP_STATE_CHANGE, result, 0);
			handler.sendMessage(msg);
		}else if (action.equals(BluetoothIntenAction.ACTION_EPEN_HARD_WARE_UPDATE)) {
			Message msg = handler.obtainMessage(HARD_WARE_UPDATE, 0, 0);
			handler.sendMessage(msg);
		} else if (action.equals(BluetoothIntenAction.ACTION_EPEN_RECEIVEIMG_CHANGE)) {
			LogUtil.i("------------------------------------------------");
			int result = intent.getIntExtra("result", 0);
			Message msg = handler.obtainMessage(RECEIVEIMG_CHANGE, result, 0);
			handler.sendMessage(msg);
		} else if (action.equals(BluetoothIntenAction.ACTION_EPEN_LANGUAGE_CHANGE)) {
			int result = intent.getIntExtra("result", 0);
			Message msg = handler.obtainMessage(LANGUAGE_CHANGE, result, 0);
			handler.sendMessage(msg);
		} else if (action.equals(BluetoothIntenAction.ACTION_EPEN_DEFAULTSET_CHANGE)) {
			int result = intent.getIntExtra("result", 0);
			Message msg = handler.obtainMessage(DEFAULTSET_CHANGE, result, 0);
			handler.sendMessage(msg);
		}
	}

	private void updateBatterDetail(int powerValue, int status) {
		Message msg = handler.obtainMessage(BATTERY_CHANGED, powerValue, status);
		handler.sendMessage(msg);
	}
}
