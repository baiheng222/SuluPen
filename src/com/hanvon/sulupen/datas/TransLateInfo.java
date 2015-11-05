package com.hanvon.sulupen.datas;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.hanvon.sulupen.application.Configs;
import com.hanvon.sulupen.R;

/**
 * @desc 百度翻译类
 * @author  PengWenCai
 * @time 2015-6-25 上午11:29:14
 * @version
 */
public class TransLateInfo {

	private Map<String, String> languageTypes;
	private ArrayList<String> languageList;
	public static final String TARANS_MODE_SWITCH = "TRANS_MODE_SWITCH";
	private Context context;

	private void init() {
		initReasoureList();
		initHashMap();

	}

	public TransLateInfo(Context context) {
		this.context = context;
		init();

	}

	private void initHashMap() {
		String[] translateStrs = context.getResources().getStringArray(
				R.array.language_translate_type_items);
		languageTypes = new HashMap<String, String>();
		if (languageList.size() == translateStrs.length) {
			for (int i = 0; i < languageList.size(); i++) {
				languageTypes.put(languageList.get(i), translateStrs[i]);
			}
		}
	}

	private void initReasoureList() {
		if (languageList == null)
			languageList = new ArrayList<String>();
		String[] typesStrings = context.getResources().getStringArray(
				R.array.language_type_items);
		int i = 0;
		for (i = 0; i < typesStrings.length; i++) {
			languageList.add(typesStrings[i]);
		}
	}

	public String getLanguageTransType(String str) {
		if (null == languageTypes) {
			initHashMap();
		}
		return languageTypes.get(str);
	}

	public ArrayList<String> getLanguageTypeList() {
		if (languageList == null)
			initReasoureList();
		return languageList;
	}

	/**
	 * pwc
	 * 
	 * @param content
	 *            待翻译的字符串
	 * @param from
	 *            源语言
	 * @param to
	 *            目标语言
	 * @return
	 */
	public HashMap<String, String> putParams(String content, String from,
			String to) {
		System.out.println("tong--------------is:"+content);
		HashMap<String, String> transMap = new HashMap<String, String>();
		transMap.put(Body.client_id, Configs.APIKEY);

		try {
			content = URLEncoder.encode(content, Body.charsetName);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		transMap.put(Body.q, content);
		transMap.put(Body.from, getLanguageTransType(from));
		transMap.put(Body.to, getLanguageTransType(to));
		System.out.println("tong--------------"+content);
		System.out.println("tong--------------"+getLanguageTransType(from));
		System.out.println("tong--------------"+getLanguageTransType(to));
		
		return transMap;
	}

	/**
	 * 请求参数体
	 * 
	 * @author pwc
	 * 
	 */
	public static class Body {
		private static String charsetName = "UTF-8";
		private static String client_id = "client_id";
		public static String from = "from";
		public static String to = "to";
		private static String q = "q";
	}

	public static class Response {
		public static final String CODE_TIMEOUT = "52001";
		public static final String CODE_SYSTEM_ERROR = "52002";
		public static final String CODE_UNAUTHORIZED_USER = "52003";

		public static String trans_result = "trans_result";
		/**
		 * 错误码：52001,52002,52003三种
		 */
		public static String error_code = "error_code";
		public static String error_msg = "error_msg";

	}

}
