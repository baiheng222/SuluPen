package com.hanvon.sulupen.datas;

/**
 * 
 * @desc 笔端更改的广播Action定义类
 * @author  PengWenCai
 * @time 2015-6-25 上午11:22:33
 * @version
 */
public class IntentAction {

	/**
	 * 笔端电量改变广播动作
	 */
	public static final String ACTION_EPEN_BATTERY_CHANGE = "action.epen.battery.change";
	/**
	 * 与笔端蓝牙连接成功广播动作
	 */
	public static final String ACTION_EPEN_BT_CONNECTED = "action.epen.bt.connected";
	/**
	 * 与笔端断开连接广播动作
	 */
	public static final String ACTION_EPEN_BT_DISCONNECT = "action.epen.bt.disconnect";
	/**
	 * 笔端休眠时间改变广播动作
	 */
	public static final String ACTION_EPEN_SLEEPTIME_CHANGE = "action.epen.sleeptime.change";
	/**
	 * 笔端关机时间改变广播动作
	 */
	public static final String ACTION_EPEN_CLOSETIME_CHANGE = "action.epen.closetime.change";
	
	/**
	 * 笔端是否发送图片数据的设置改变广播动作
	 */
	public static final String ACTION_EPEN_RECEIVEIMG_CHANGE = "action.epen.receiveimg.change";
	/**
	 * 笔端识别语言改变广播动作
	 */
	public static final String ACTION_EPEN_LANGUAGE_CHANGE = "action.epen.language.change";
	/**
	 * 笔端扫描方向改变广播动作
	 */
	public static final String ACTION_EPEN_SCANDIRECTION_CHANGE = "action.epen.scandirection.change";
	/**
	 * 笔端恢复默认设置广播动作
	 */
	public static final String ACTION_EPEN_DEFAULTSET_CHANGE = "action.epen.defaultset.change";
}
