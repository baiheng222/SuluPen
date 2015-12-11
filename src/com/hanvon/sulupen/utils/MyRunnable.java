package com.hanvon.sulupen.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.Toast;

import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.utils.HvnSyncStatic.UploadFileMsg;

class MyRunnable implements Runnable{
	int type;
	JSONObject params;
	String url;
	ObserverCallBack callBackData;
	int total;
	String contentid;
	UploadFileMsg fileMsg;
	private int BUF_SIZE = 32768;

	public MyRunnable(int type,JSONObject parameters,String url,ObserverCallBack callBack,int total,String contentid,UploadFileMsg fileMsg){
		this.type = type;
		this.params = parameters;
		this.url = url;
		this.callBackData = callBack;
		this.total = total;
		this.contentid = contentid;
		this.fileMsg = fileMsg;
	}

	@Override
	public void run() {
		String data = "";
		try {
		    // TODO Auto-generated method stub
		    BasicHttpParams httpParams = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParams, HvnSyncStatic.REQUEST_TIMEOUT);
	        HttpConnectionParams.setSoTimeout(httpParams, HvnSyncStatic.SO_TIMEOUT);
		    if(HvnSyncStatic.mHttpClient == null){
			    DefaultHttpClient client = new DefaultHttpClient(httpParams);
			    ClientConnectionManager mgr = client.getConnectionManager();
		        HttpParams params = client.getParams();
		        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
		        HvnSyncStatic.mHttpClient = client;
		    }
	        HttpResponse response = null;

	        if (HvnSyncStatic.isError){
	        	return;
	        }
	        if (!HvnSyncStatic.isConnNetwork){
				Toast.makeText(HvnSyncStatic.mcontext, "网络连接不可用，同步失败!", Toast.LENGTH_SHORT).show();
			//	HvnSyncStatic.pd.dismiss();
				return;
			}
		    switch (type) {
		        case HvnSyncStatic.HVN_DELETE_TAGS:
			        HttpPost post = new HttpPost(url);
			        LogUtil.i("---------params:"+params.toString());
			        post.setEntity(new StringEntity(params.toString(),"utf-8"));
			        response = HvnSyncStatic.mHttpClient.execute(post);
			     //   response = HvnSyncStatic.mHttpClient.execute(post);
			        break;

		        case HvnSyncStatic.HVN_DELETE_FILES:
			        HttpPost post1 = new HttpPost(url);
			        LogUtil.i("---------params:"+params.toString());
			        post1.setEntity(new StringEntity(params.toString(),"utf-8"));
			        response = HvnSyncStatic.mHttpClient.execute(post1);
			        break;

		        case HvnSyncStatic.HVN_UPLOAD_FILE:
		        	UploadFiletoHvn(fileMsg,total,callBackData);
		        	
			        break;

		        case HvnSyncStatic.HVN_UPLOAD_TAGS:
			        HttpPost post2 = new HttpPost(url);
			        LogUtil.i("---------params:"+params.toString());
			        post2.setEntity(new StringEntity(params.toString(),"utf-8"));
			        response = HvnSyncStatic.mHttpClient.execute(post2);
			        break;

		        case HvnSyncStatic.HVN_FILES_LIST:
			        HttpPost post3 = new HttpPost(url);
			        LogUtil.i("---------params:"+params.toString());
			        post3.setEntity(new StringEntity(params.toString(),"utf-8"));
			        response = HvnSyncStatic.mHttpClient.execute(post3);
			        break;

		        case HvnSyncStatic.HVN_DOWN_SING_FILE:
		        	URL url1 = new URL(url);
	    	        HttpURLConnection conn =  (HttpURLConnection) url1.openConnection();
	    	        conn.setConnectTimeout(5000);
	    	        int length = conn.getContentLength();
	    	        InputStream is = conn.getInputStream();
	    	        File file = new File("/sdcard/sulupen/"+HanvonApplication.hvnName+"/"+contentid+".txt");
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
	    	  //      HvnCloudResponse.ParseFileContent("/sdcard/sulupen/"+HanvonApplication.hvnName+contentid+".txt",filepath);
	    	        LogUtil.i("------------------------------");
			        break;

		        case HvnSyncStatic.HVN_DOWN_FILES:
				    URL url2 = new URL(url);
			    	HttpURLConnection conn1 =  (HttpURLConnection) url2.openConnection();
			    	conn1.setConnectTimeout(5000);
			    	int length1 = conn1.getContentLength();
			    	InputStream is1 = conn1.getInputStream();
			    	File file1 = new File(HvnSyncStatic.DOWN_ZIP_DIR+contentid+".zip");
			    	        
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
			    	HvnCloudResponse.UnZipFile(HvnSyncStatic.DOWN_ZIP_DIR+contentid+".zip",contentid);
			    	HvnSyncStatic.NeedDownNoteBooks++;
			    	if (HvnSyncStatic.NeedDownNoteBooks + HvnSyncStatic.NoNeedDownNoteBooks == HvnSyncStatic.downZipCount){
			    		HvnSyncStatic.isDiagDimiss = true;
			    	    HvnCloudRequest.HvnCloudGetSystemTime(callBackData,1);
			    	    HvnSyncStatic.NeedDownNoteBooks = 0;
			    	    HvnSyncStatic.NoNeedDownNoteBooks = 0;
			    	}
			    	file1.delete();
					break;
		        case HvnSyncStatic.HVN_TAGS_LIST:
		        	HttpPost post4 = new HttpPost(url);
			        LogUtil.i("---------params:"+params.toString());
			        post4.setEntity(new StringEntity(params.toString(),"utf-8"));
			        response = HvnSyncStatic.mHttpClient.execute(post4);
			        break;
		        case HvnSyncStatic.HVN_GET_SYSTEM_TIME:
		        	HttpPost post6 = new HttpPost(url);
			        LogUtil.i("---------params:"+params.toString());
			        post6.setEntity(new StringEntity(params.toString(),"utf-8"));
			        response = HvnSyncStatic.mHttpClient.execute(post6);
		        	break;

		        default:
			        break;
		    }
		    
		    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			    data = EntityUtils.toString(response.getEntity());
		    } else {
			    data = null;
		    }
		} catch (Exception e) {
			HvnSyncStatic.pd.dismiss();
	//		Toast.makeText(HvnSyncStatic.mcontext, "数据异常或者非法!", Toast.LENGTH_SHORT).show();
			data = null;
		}
	//	LogUtil.i("-----type:"+type+"     --response:"+data);
		try { // 回调数据
			if(callBackData != null){
				if (data.equals("")){
					LogUtil.i("-----type:"+type+"     --response:"+data);
				}
				callBackData.back(data, type,total);
			}
		} catch (Exception e) {
			//Log.e(AnsynHttpRequest.tag, e.getMessage());
		}
	}

	private void UploadFiletoHvn(UploadFileMsg fileMsg,int total,ObserverCallBack callBack){
		try {
			final int blocknum;
			final byte[] buffer;
			int readBytes = BUF_SIZE;
		    boolean isSuccess;
			String requestData;
		    final File file = new File(fileMsg.filePath);

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

            String fuid = fileMsg.tagId+"_"+fileMsg.contentId;
	        for(int i = 0;i < blocknum;i++){
	            if (i == blocknum -1){
	                readBytes = length - i*BUF_SIZE;
	            	byte[] buffer1 = new byte[readBytes];
	                readBytes = fis.read(buffer1);
	            	requestData = SendBody(Base64Utils.encode(buffer1),fileMsg.contentId+".txt",i*BUF_SIZE,length,fileMsg.title,fuid);
	            }else{
	            	readBytes = BUF_SIZE;
	            	readBytes = fis.read(buffer);
	            	requestData = SendBody(Base64Utils.encode(buffer),fileMsg.contentId+".txt",i*BUF_SIZE,length,fileMsg.title,fuid);
	            }

				String str = uploadFile(requestData,length);
				callBack.back(str, HvnSyncStatic.HVN_UPLOAD_FILE, length);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
		    e.printStackTrace();
	    } catch (JSONException e) {
		    // TODO Auto-generated catch block
			e.printStackTrace();
		}

		DelteTmpFile(fileMsg.filePath);
		
		//更新上传过的笔记的upload状态为1
		HvnCloudRequest.UpdateNoteStatus(fileMsg.contentId);
	}
	
	private void DelteTmpFile(String path){
		File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
	}
	
	private String SendBody(String data,String filename,int offset,int totalLength,String title,String fuid){
		JSONObject JSuserInfoJson = new JSONObject();
		
		LogUtil.i("---totalLength:"+totalLength+" ----filename:"+filename);
	  	try {
	  		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  		JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	  	JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	  	JSuserInfoJson.put("userid", HanvonApplication.hvnName);
	  	    JSuserInfoJson.put("devid", "wwww");
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
	  	}
	//  	LogUtil.i(JSuserInfoJson.toString());

	  	return JSuserInfoJson.toString();
	}
	
	private HttpURLConnection CreateConnection() throws IOException{
		URL url = null;
		HttpURLConnection httpurlconnection = null;

		url = new URL("http://cloud.hwyun.com/dws-cloud/rt/ap/v1/store/upload");
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
	
	
	private HttpURLConnection CreateConnectionForShare() throws IOException{
		URL url = null;
		HttpURLConnection httpurlconnection = null;

		url = new URL("http://cloud.hwyun.com/dws-cloud/rt/ap/v1/store/sharedata");
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

	public String uploadFile(final String requestData,int length) throws IOException, JSONException{
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
}
