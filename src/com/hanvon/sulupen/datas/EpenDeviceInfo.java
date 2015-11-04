package com.hanvon.sulupen.datas;

import android.content.Context;

import com.hanvon.sulupen.helper.PreferHelper;

/**
 * 
 * @desc 扫描笔设备基本信息实体类
 * @author  PengWenCai
 * @time 2015-6-25 上午11:21:17
 * @version
 */
public class EpenDeviceInfo {
	private Context context;
	public static final String DEFAULT_BT_NAME = "Android Bluedroid";
	// public static final String DEFAULT_BT_NAME="HelloKitty";
	public static final String ADDRESS_KEY = "ePenAddress";
	private String eBtName; // 设备蓝牙名字
	public String eBtAddress; // 设备蓝牙mac地址
	private String eSoftVersion; // 设备软件版本信息
	private String eHardwareVersion; // 设备硬件版本信息
	private String eFactoryInfo; // 出厂信息
	private String eSerialNum; // 设备序列号
	private int eScanDirection;//设备扫描方向
	private int eSleepTime; // 设备休眠时间
	private int eCloseTime; //设备关机时间
	private int eIsSendScanImage; // 是否传原图

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String geteBtName() {
		return eBtName;
	}

	public void seteBtName(String eBtName) {
		this.eBtName = eBtName;
	}

	public String geteBtAddress() {
		return eBtAddress;
	}

	public void seteBtAddress(String eBtAddress) {
		this.eBtAddress = eBtAddress;
	}

	public String geteFactoryInfo() {
		return eFactoryInfo;
	}

	public void seteFactoryInfo(String eFactoryInfo) {
		this.eFactoryInfo = eFactoryInfo;
	}

	public String geteSerialNum() {
		return eSerialNum;
	}

	public void seteSerialNum(String eSerialNum) {
		this.eSerialNum = eSerialNum;
	}
	
	public int geteScanDirection() {
		return eScanDirection;
	}

	public void seteScanDirection(int eScanDirection) {
		this.eScanDirection = eScanDirection;
	}
	

	public int geteSleepTime() {
		return eSleepTime;
	}

	public void seteSleepTime(int eSleepTime) {
		this.eSleepTime = eSleepTime;
	}
	
	public int geteCloseTime() {
		return eCloseTime;
	}

	public void seteCloseTime(int eCloseTime) {
		this.eCloseTime = eCloseTime;
	}
	
	public int geteIsSendScanImage() {
		return eIsSendScanImage;
	}

	public void seteIsSendScanImage(int eIsSendScanImage) {
		this.eIsSendScanImage = eIsSendScanImage;
	}

	public static String getDefaultBtName() {
		return DEFAULT_BT_NAME;
	}

	public static String getAddressKey() {
		return ADDRESS_KEY;
	}

	public EpenDeviceInfo(Context context) {
		this.context = context;
	}

	public String geteSoftVersion() {
		return eSoftVersion;
	}

	public void seteSoftVersion(String eSoftVersion) {
		this.eSoftVersion = eSoftVersion;
	}

	public String geteHardwareVersion() {
		return eHardwareVersion;
	}

	public void seteHardwareVersion(String eHardwareVersion) {
		this.eHardwareVersion = eHardwareVersion;
	}

	/**
	 * @function:保存蓝牙地址
	 * @param newAddress
	 */
	public static void saveAddress(String newAddress) {

		// String oldAddress=
		// PreferHelper.getString(EpenDeviceInfo.ADDRESS_KEY,"");
		// if(oldAddress.equals("")||!oldAddress.equals(newAddress))
		// {
		PreferHelper.saveString(EpenDeviceInfo.ADDRESS_KEY, newAddress);
		// }
	}

	@Override
	public String toString() {
		return "EpenDeviceInfo [context=" + context + ", eBtName=" + eBtName
				+ ", eBtAddress=" + eBtAddress + ", eSoftVersion="
				+ eSoftVersion + ", eHardwareVersion=" + eHardwareVersion
				+ ", eFactoryInfo=" + eFactoryInfo + ", eSerialNum="
				+ eSerialNum + ", eScanDirection=" + eScanDirection + ", eSleepTime=" + eSleepTime
				+ ", eCloseTime=" + eCloseTime
				+ ", eIsSendScanImage=" + eIsSendScanImage + "]";
	}
	
	

}
