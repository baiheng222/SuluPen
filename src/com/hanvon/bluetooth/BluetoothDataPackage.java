package com.hanvon.bluetooth;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanvon.utils.AnalyzeJSONString;
import com.hanvon.utils.MD5Util;


/**
 * 
 * @desc 蓝牙数据传输实体
 * @author chenxzhuang
 * @date 2015-10-22 上午9:23:53
 */
public class BluetoothDataPackage {

	
	/**
	 * 数据包头
	 */
	public static final int DATA_START_SIGN = 2015;
	public static final String DATA_END_SIGN = "TEOF";
	public static String charsetName = "UTF-8";
	public static String fileSavePath = "";

	public static void sendPackage(JSONObject json) {

	}

	/**
	 * 添加数据包类型
	 * 
	 * @param type
	 * @param params
	 * @return
	 */
	private static JSONObject addType(int type, Map<String, String> params) {
		JSONObject dataJson = new JSONObject();
		try {
			dataJson.put("type", type);
			dataJson.put("data", AnalyzeJSONString.mapToJson(params));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataJson;
	}

	/**
	 * 得到完整的数据包
	 * 
	 * @param type
	 * @param params
	 * @return
	 */
	private static String getDataPackage(int type, Map<String, String> params) {
		JSONObject jsonData = addType(type, params);

		return jsonData.toString();
	}

	/**
	 * 得到完整的数据包
	 * 
	 * @param type
	 * @param params
	 * @return
	 */
	private static String getDataPackage(JSONObject jsonData) {
		// Log.i("pwc", "发送包:"+jsonData.toString());
		// byte[] dataSign=DataConvert.intToByte(DATA_START_SIGN);
		return jsonData.toString();
	}

	/**
	 * 拼接数据类型+数据大小+数据内容
	 * 
	 * @param byte_1
	 * @param byte_2
	 * @param byte_3
	 * @return
	 */
	private static byte[] dataMerger(byte[] byte_1, byte[] byte_2, byte[] byte_3) {
		byte[] byte_4 = new byte[byte_1.length + byte_2.length + byte_3.length];
		System.arraycopy(byte_1, 0, byte_4, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_4, byte_1.length, byte_2.length);
		System.arraycopy(byte_3, 0, byte_4, byte_1.length + byte_2.length,
				byte_3.length);
		return byte_4;
	}

	// ////////////////////////////已下为接口详情/////////////////////////

	/**
	 * 1.设备基本信息
	 */
	public static String epenDetailInfo(Map<String, String> params) {
		int type = 101;
		return getDataPackage(type, params);
	}

	/**
	 * 2.设备电量信息
	 */
	public static String epenBatteryInfo(Map<String, String> params) {
		int type = 102;
		return getDataPackage(type, params);
	}

	/**
	 * 3.扫描录入数据信息
	 */
	public static String epenScanData(Map<String, String> params) {
		int type = 103;
		return getDataPackage(type, params);
	}

	/**
	 * 4.设备基本信息
	 */
	public static String epenSleepTime(Map<String, String> params) {
		int type = 104;
		return getDataPackage(type, params);
	}
	
	/**
	 * 4.设备基本信息关机时间
	 */
	public static String epenCloseTime(Map<String, String> params) {
		int type = 111;
		return getDataPackage(type, params);
	}
	/**
	 * 5.升级接口
	 */
	public static String epenUpgradePackage(String filePath,
			Map<String, String> params) {
		int type = 105;
		return getDataPackage(type, params);
	}

	/**
	 * 6.扫描原图
	 */
	public static String epenReceiveScanImage(Map<String, String> params) {
		int type = 106;
		return getDataPackage(type, params);
	}
   
	/**
	 * 恢复出厂设置模式
	 * */
	public static String epenFactoryMode(Map<String, String> params){
		int type=108;
		return getDataPackage(type, params);
	}
	/**
	 * 9.设备识别语言
	 */
	public static String epenLanguageIdent(Map<String, String> params) {
		int type = 109;
		return getDataPackage(type, params);
	}
	/**
	 * 传输的文件头
	 * 
	 * @param file
	 * @return
	 */
	public static String epenFileHead(File file) {
		String fName = file.getName();
		long fileLen = file.length();
		String checkCodeStr = "";
		try {
			checkCodeStr = MD5Util.getFileMD5String(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject json = new JSONObject();
		try {
			json.put("fileName", fName);
			json.put("fileSize", fileLen);
			json.put("fileType", "zip");
			json.put("fileCheckCode", checkCodeStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getDataPackage(json);
	}

	/**
	 * 响应结果包
	 * 
	 * @param type
	 *            接口类型
	 * @param resultCode
	 *            1 or 0
	 * @return
	 */
	public static String resultPackage(int type, int resultCode) {
		JSONObject json = new JSONObject();
		try {
			json.put("type", type);
			json.put("result", resultCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getDataPackage(json);

	}
}
