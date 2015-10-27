package com.hanvon.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @desc JSON串解析 
 * @author chenxzhuang
 * @date 2015-10-22 上午11:07:10
 */
public class AnalyzeJSONString {

	// 转换 JSONArray 成 List<Map<String, String>>
		public static List<Map<String, String>> convertArrayToMap(JSONArray array) {
			List<Map<String, String>> result = new ArrayList<Map<String, String>>();
			for (int i = 0; i < array.length(); i++) {
				try {
					Map<String, String> map = convertJSONObjectToMap(array
							.getJSONObject(i));
					result.add(map);
	 
				} catch (Exception e) {
				}
			}
			return result;
		}

		// 解析String成map
		public static Map<String, String> analyzeString(String s)
				throws JSONException {
			Map<String, String> result = new HashMap<String, String>();
			if(s==null || "".equalsIgnoreCase(s)){ // 判断要解析的字符串是否为空
				return result;
			}
			JSONObject jsobj = new JSONObject(s);
			@SuppressWarnings("rawtypes")
			Iterator it = jsobj.keys();
			while (it.hasNext()) {
				try {
					String key = it.next().toString();
					result.put(key, jsobj.getString(key));
				} catch (Exception e) {
					// LpEnvironmentHelper.writeLogError(e.getMessage());
				}
				// it.next();
			}
			return result;
		}

		// 解析含有list的JsonString成map
		@SuppressWarnings("rawtypes")
		public static List<Map<String, String>> analyzeJsonListString(String s) throws JSONException {

			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			Map<String, String> result = null;
			JSONArray jsArray = new JSONArray(s);
			JSONObject jsobj = null;
			for (int i = 0; i < jsArray.length(); i++) {
				result = new HashMap<String, String>();
				jsobj = jsArray.getJSONObject(i);
				Iterator it = jsobj.keys();
				while (it.hasNext()) {
					try {
						String key = it.next().toString();
						result.put(key, jsobj.getString(key));

					} catch (Exception e) {
					}
				}
				mapList.add(result);
			}
			return mapList;
		}
	    /**
	     * 将map型数据转换为json串
	     * @param map
	     * @return
	     */
		public static JSONObject mapToJson(Map<String, String> map) {
			JSONObject job = new JSONObject();
			Set<String> set = map.keySet();
			for (String ss : set) {
				try {
					job.put(ss, map.get(ss));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return job;
		}

		public static JSONObject mapToJsons(Map<String, Map<String, String>> map) {
			JSONObject job = new JSONObject();
			for (String ss : map.keySet()) {
				Map<String, String> map2 = map.get(ss);
				Set<String> set2 = map2.keySet();
				JSONObject job2 = new JSONObject();
				for (String s : set2) {
					try {
						job2.put(s, map2.get(s));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					job.put(ss, job2);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return job;
		}

		public static Map<String, String> convertJSONObjectToMap(JSONObject jsobj) {
			Map<String, String> result = new HashMap<String, String>();

			@SuppressWarnings("rawtypes")
			Iterator it = jsobj.keys();
			while (it.hasNext()) {
				try {
					String key = it.next().toString();
					result.put(key, jsobj.getString(key));
				} catch (Exception e) {
					// LpEnvironmentHelper.writeLogError(e.getMessage());
				}
				// it.next();
			}
			return result;
		}

		/**
		 * 附属list<map>转成jsonobject方法
		 * 
		 * @param params
		 * @return
		 */
		public static String convertListMapToJSONObject(
				List<Map<String, String>> params) {
			JSONArray array = new JSONArray();
			for (Map<String, String> map : params) {
				JSONObject jsobject = new JSONObject();
				for (String key : map.keySet()) {
					try {
						jsobject.put(key, map.get(key));
					} catch (Exception e) {
					}
				}
				array.put(jsobject);
			}
			return array.toString();
		}

	
}
