package com.hanvon.bluetooth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BluetoothSetting {
	private static final String ANDPY_CONFS_SERIAL_KEY = "SerialKey";
	private static final String ANDPY_CONFS_SLEEP_KEY = "SleepKey";
	private static final String ANDPY_CONFS_SHUTDOWN_KEY = "ShutdownKey";
	private static final String ANDPY_CONFS_FUNCTIONKEY_KEY = "FunctionKey";
	private static final String ANDPY_CONFS_IDENTCORE_KEY = "IdentCore";
	private static final String ANDPY_CONFS_BLUEADDRESS_KEY = "BlueAddress";
	private static final String ANDPY_CONFS_BLUEVERSION_KEY = "BlueVersion";
	
	private static final String ANDPY_CONFS_SCANDIR_KEY = "ScanDir";
	
	private static final String ANDPY_CONFS_SENDIMAGE_KEY = "BlueSendImage";

	private static int    msleepCode;
	private static int    mshutdownCode;
    private static String    mFuncKeyCode;
    private static String    mIdentCoreCode;
    private static String    mSerialNum;
    private static String    mAddress;
    private static String    mVersion;
    private static boolean isSendImage;
    private static int mScanDir;
	private static BluetoothSetting mInstance = null;

	/**
	 * 引用计数
	 */
	private static int mRefCount = 0;

	public static SharedPreferences mSharedPref = null;

	protected BluetoothSetting(SharedPreferences pref) {
		mSharedPref = pref;
		initConfs();
	}

	/**
	 * 获得该实例
	 * 
	 * @param pref
	 * @return
	 */
	public static BluetoothSetting getInstance(SharedPreferences pref) {
		if (mInstance == null) {
			mInstance = new BluetoothSetting(pref);
		}
		assert (pref == mSharedPref);
		mRefCount++;
		return mInstance;
	}

	/**
	 * 设置震动、声音、预报开关标记进入配置文件
	 */
	public static void writeBack() {
		Editor editor = mSharedPref.edit();
		editor.putInt(ANDPY_CONFS_SLEEP_KEY, msleepCode);
		editor.putInt(ANDPY_CONFS_SHUTDOWN_KEY, mshutdownCode);
		editor.putString(ANDPY_CONFS_FUNCTIONKEY_KEY, mFuncKeyCode);
		editor.putString(ANDPY_CONFS_IDENTCORE_KEY, mIdentCoreCode);
		editor.putString(ANDPY_CONFS_SERIAL_KEY, mSerialNum);
		editor.putString(ANDPY_CONFS_BLUEADDRESS_KEY, mAddress);
		editor.putBoolean(ANDPY_CONFS_SENDIMAGE_KEY, isSendImage);
		editor.putInt(ANDPY_CONFS_SCANDIR_KEY, mScanDir);
		editor.putString(ANDPY_CONFS_BLUEVERSION_KEY, mVersion);
		editor.commit();
	}

	/**
	 * 释放对该实例的使用。
	 */
	public static void releaseInstance() {
		mRefCount--;
		if (mRefCount == 0) {
			mInstance = null;
		}
	}

	/**
	 *获取蓝牙基本配置信息
	 */
	private void initConfs() {
		msleepCode = mSharedPref.getInt(ANDPY_CONFS_SLEEP_KEY, 1);
		mshutdownCode = mSharedPref.getInt(ANDPY_CONFS_SHUTDOWN_KEY, 1);
		mFuncKeyCode = mSharedPref.getString(ANDPY_CONFS_FUNCTIONKEY_KEY, "\n");
		mIdentCoreCode = mSharedPref.getString(ANDPY_CONFS_IDENTCORE_KEY, "ch-eng");
		mSerialNum = mSharedPref.getString(ANDPY_CONFS_IDENTCORE_KEY, "0000-0000-0000-0000");
		mAddress = mSharedPref.getString(ANDPY_CONFS_BLUEADDRESS_KEY, "");
	    isSendImage = mSharedPref.getBoolean(ANDPY_CONFS_SENDIMAGE_KEY, false);
	    mScanDir = mSharedPref.getInt(ANDPY_CONFS_SCANDIR_KEY, 0);
	    mVersion = mSharedPref.getString(ANDPY_CONFS_BLUEVERSION_KEY, "v1.001");
	}

	
	/*
	 * 获得功能键信息
	 */
	public static String getFuncKeyCode(){
		return mFuncKeyCode;	
	}
	
	/**
	 * 设置功能键信息
	 * 
	 * @param v
	 */
	public static void setFuncKeyCode(String func) {
		if (func.equals(mFuncKeyCode))
			return;
		mFuncKeyCode = func;
	}
	
	/*
	 * 获得识别核心信息
	 */
	public static String getIdentCoreCode(){
		return mIdentCoreCode;	
	}
	
	
	public static void setIdentCoreCode(String identCore) {
		if (identCore.equals(mIdentCoreCode))
			return;
		mIdentCoreCode = identCore;
	}
	
	public static int getSleepTime(){
		return msleepCode;	
	}
	
	
	public static void setSleepTime(int index) {
		if (msleepCode == index)
			return;
		msleepCode = index;
	}
	
	public static int getShutdownTime(){
		return mshutdownCode;	
	}
	
	
	public static void setShutdownTime(int index) {
		if (mshutdownCode == index)
			return;
		mshutdownCode = index;
	}
	
	public static String getSeralNumber(){
		return mSerialNum;	
	}
	
	
	public static void setSeralNumber(String serial) {
		mSerialNum = serial;
	}
	
	public static String getBlueAddress(){
		return mAddress;	
	}
	
	
	public static void setBlueAddress(String address) {
		mAddress = address;
	}
	
	
	public static boolean getBlueIsSendImage(){
		return isSendImage;
	}
	
	
	public static void setBlueIsSendImage(boolean sendImage) {
		isSendImage = sendImage;
	}
	
	public static int getBlueScanDir(){
		return mScanDir;
	}
	
	
	public static void setBlueScanDir(int scandir) {
		mScanDir = scandir;
	}
	
	public static String getBlueVersion(){
		return mVersion;
	}
	
	
	public static void setBlueVersion(String version) {
		mVersion = version;
	}
}
