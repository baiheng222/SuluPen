package com.hanvon.sulupen.sync;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.utils.Base64Utils;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.MD5Util;
import com.hanvon.sulupen.utils.StatisticsUtils;
import com.hanvon.sulupen.utils.UrlBankUtil;

/**
 * 
 * @desc 该文件主要用于文件的上传与下载操作
 * @author chenxzhuang
 * @date 2015-12-14 下午5:26:41
 */
public class FilesManager {

	private static int BUF_SIZE = 32768;
	
	public static void UploadFiletoHvn(FileMsgInfo fileMsg,int total,ObserverCallBack callBack){
		try {
			final int blocknum;
			final byte[] buffer;
			int readBytes = BUF_SIZE;
		    String requestData;
		    final File file = new File(fileMsg.getFilePath());

			FileInputStream fis = new FileInputStream(file); 
			int length = fis.available();

			if (length%BUF_SIZE != 0){
	            blocknum = length/BUF_SIZE + 1;
	        }else{
	            blocknum = length/BUF_SIZE;
	        }
	        if (blocknum <= 1){
	        	buffer =  new byte[length];
	        }else{
	        	buffer =  new byte[BUF_SIZE];
	        }

            String fuid = fileMsg.getNoteBookId()+"_"+fileMsg.getNoteRecordId();
            LogUtil.i("---------upload files,fuid:"+fuid);
	        for(int i = 0;i < blocknum;i++){
	            if (i == blocknum -1){
	                readBytes = length - i*BUF_SIZE;
	            	byte[] buffer1 = new byte[readBytes];
	                readBytes = fis.read(buffer1);
	            	requestData = SendBody(Base64Utils.encode(buffer1),fileMsg.getNoteRecordId()+".txt",i*BUF_SIZE,length,fileMsg.getTitle(),fuid);
	            }else{
	            	readBytes = BUF_SIZE;
	            	readBytes = fis.read(buffer);
	            	requestData = SendBody(Base64Utils.encode(buffer),fileMsg.getNoteRecordId()+".txt",i*BUF_SIZE,length,fileMsg.getTitle(),fuid);
	            }

				String str = uploadFile(requestData,length);
				callBack.back(str, SyncInfo.HVN_UPLOAD_FILE, length);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
		    e.printStackTrace();
		    LogUtil.i("--------error:"+e.toString());
	    } catch (JSONException e) {
		    // TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.i("--------error:"+e.toString());
		}

		DelteTmpFile(fileMsg.getFilePath());
		
		//更新上传过的笔记的upload状态为1
		UpdateNoteStatus(fileMsg.getNoteRecordId());
	}
	
	private static void DelteTmpFile(String path){
		File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
	}
	
	//更新上传笔记的状态
	public static void UpdateNoteStatus(UUID noteRecordId){
		SyncDataUtils.UpdateNoteState(noteRecordId, 1);
	}
	
	private static String SendBody(String data,String filename,int offset,int totalLength,String title,String fuid){
		JSONObject JSuserInfoJson = new JSONObject();
		
		LogUtil.i("---totalLength:"+totalLength+" ----filename:"+filename);
	  	try {
	  		JSuserInfoJson = StatisticsUtils.StatisticsJson(JSuserInfoJson);
	  		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  		JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	  	JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	  	JSuserInfoJson.put("userid", HanvonApplication.hvnName);
	  	    JSuserInfoJson.put("devid", HanvonApplication.AppDeviceId);
	  	    JSuserInfoJson.put("fuid", fuid);
	  	    JSuserInfoJson.put("ftype", "4");
	  	    JSuserInfoJson.put("fname", filename);
	  	    JSuserInfoJson.put("title", title);
	  	    JSuserInfoJson.put("flength", totalLength);
	  	    JSuserInfoJson.put("offset", offset);
			JSuserInfoJson.put("checksum", MD5Util.md5(data));
	  	    JSuserInfoJson.put("iszip", String.valueOf(false));
	  	    JSuserInfoJson.put("data", data);
	  	}catch (JSONException e) {
	  		e.printStackTrace();
	  		LogUtil.i("--------error:"+e.toString());
	  	}
	//  	LogUtil.i(JSuserInfoJson.toString());

	  	return JSuserInfoJson.toString();
	}
	
	private static HttpURLConnection CreateConnection() throws IOException{
		URL url = null;
		HttpURLConnection httpurlconnection = null;

		url = new URL(UrlBankUtil.getHvnUploadUrl());
		httpurlconnection = (HttpURLConnection) url.openConnection();
		httpurlconnection.setConnectTimeout(60*1000);
		httpurlconnection.setUseCaches(false);
		httpurlconnection.setDoInput(true);
		httpurlconnection.setDoOutput(true);
		
		//1.设备请求类型
		httpurlconnection.setRequestMethod("POST");
		
		//2.设备请求头
		httpurlconnection.setRequestProperty("token","wdf34568koisjfsjkj");
		httpurlconnection.setRequestProperty("Content-Type","application/octet-stream");

		return httpurlconnection;
	}

	public static String uploadFile(final String requestData,int length) throws IOException, JSONException{
	    HttpURLConnection httpurlconnection = null;
		httpurlconnection = CreateConnection();
		//3加密请求数据
		httpurlconnection.getOutputStream().write(requestData.getBytes());
		//5发送数据
		httpurlconnection.getOutputStream().flush();
		//5接收结果数据
		InputStream in = null;
		in = httpurlconnection.getInputStream();
		
		BufferedReader r = null;
		r = new BufferedReader(new InputStreamReader(in,"utf-8"));

		String line;
		StringBuilder sb = new StringBuilder();

		while ((line = r.readLine()) != null) {
			sb.append(line);
		}

		LogUtil.i("--------"+sb.toString());
		return sb.toString();
    }
	
	public static boolean DownFileFromCloud(String url,String noteBookId) throws IOException, JSONException{
		boolean isComplete = false;
		
		URL url2 = new URL(url);
    	HttpURLConnection conn1 =  (HttpURLConnection) url2.openConnection();
    	conn1.setConnectTimeout(5000);
    	int length1 = conn1.getContentLength();
    	InputStream is1 = conn1.getInputStream();
    	File file1 = new File(SyncInfo.DOWN_ZIP_DIR+noteBookId+".zip");
    	        
    	FileOutputStream fos1 = new FileOutputStream(file1);
    	BufferedInputStream bis1 = new BufferedInputStream(is1);
    	byte[] buffer1 = new byte[BUF_SIZE];
    	int len1 = 0;

    	while((len1 =bis1.read(buffer1))!=-1){
    	    fos1.write(buffer1, 0, len1);
    	}

    	fos1.close();
    	bis1.close();
    	is1.close();
    	LogUtil.i("------------------------------");
    	//下载完压缩文件，则进行文件的解析与本地数据的插入工作
    	CloudSynchroResponse.UnZipFile(SyncInfo.DOWN_ZIP_DIR+noteBookId+".zip",noteBookId);
    	SyncInfo.setNeedDownNoteBooks();
    	if (SyncInfo.getNeedDownNoteBooks() + SyncInfo.getNoNeedDownNoteBooks() == SyncInfo.downZipCount){
    	   // HvnCloudGetSystemTime(1);
    	    SyncInfo.ClearNeedDownNoteBooks();
    	    SyncInfo.ClearNoNeedDownNoteBooks();
    	    isComplete = true;
    	}
    	file1.delete();
    	
    	return isComplete;
	}
	
	public static void DownSingFile(String url,String noteRecordId) throws IOException{
		URL url1 = new URL(url);
        HttpURLConnection conn =  (HttpURLConnection) url1.openConnection();
        conn.setConnectTimeout(5000);
        int length = conn.getContentLength();
        InputStream is = conn.getInputStream();
        File file = new File("/sdcard/sulupen/"+HanvonApplication.hvnName+"/"+noteRecordId+".txt");
        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len = 0;
        int total = 0;

        while((len =bis.read(buffer))!=-1){
            fos.write(buffer, 0, len);
        }

        fos.close();
        bis.close();
        is.close();
   //     HvnCloudResponse.ParseFileContent("/sdcard/sulupen/"+HanvonApplication.hvnName+noteRecordId+".txt",filepath);
        LogUtil.i("------------------------------");
	}
}
