package com.hanvon.sulupen.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.utils.LogUtil;


/**
 * 
 * @desc 该文件主要用于对云端请求的操作以及对应的逻辑处理
 * @author chenxzhuang
 * @date 2015-12-14 下午5:29:22
 */
public class CloudSynchroRequest {

	private static int BUF_SIZE = 32768;
	private Context mContext;
	private ObserverCallBack callBackData;
	private static DefaultHttpClient mHttpClient;
	private static final int REQUEST_TIMEOUT = 10*1000;//设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟
	private final Handler mHandler;

	public CloudSynchroRequest(Context context,ObserverCallBack callback,Handler msgHanlder){
		this.mContext = context;
		this.callBackData = callback;
		this.mHandler = msgHanlder;
	}

	public void HvnCloudGetSystemTime(int flag) throws JSONException{
		JSONObject JSuserInfoJson = SyncJointJson.GetSystemTimeJson();

	  	ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_GET_SYSTEM_TIME,JSuserInfoJson,
	  			SyncInfo.HvnGetSystemTimeUrl,flag,"",null));
	}

	public void HvnCloudDownSingleFile() throws JSONException{
		JSONObject JSuserInfoJson = SyncJointJson.GetDownSingleFileJson();
	  	String url = SyncInfo.HvnDOWNSingFileUrl+JSuserInfoJson.toString();

	  	ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_DOWN_SING_FILE,JSuserInfoJson,
	  			url,0,"7654321",null));
	}

	public void HvnCloudDownFiles(ArrayList<FileMsgInfo> filesInfo,int total) throws JSONException{
		String fuids = "";
		String tagId = "";
		int count = 0;

		for(FileMsgInfo fileinfo:filesInfo){
			if (fileinfo.getIsDown()){
			    if (count == 0){
				    fuids = fileinfo.getCloudId();
				    tagId = fileinfo.getNoteBookId();
			    }else{
			    	fuids = fuids + "," + fileinfo.getCloudId();
			    }
			    count++;
			}
		}

		if (fuids.equals("")){
			SyncInfo.setNoNeedDownNoteBooks();
			if (SyncInfo.getNoNeedDownNoteBooks() + SyncInfo.getNeedDownNoteBooks() == SyncInfo.downZipCount){
				SyncInfo.ClearNeedDownNoteBooks();
				SyncInfo.ClearNoNeedDownNoteBooks();
				HvnCloudGetSystemTime(1);
			}
			return;
		}
		JSONObject JSuserInfoJson = SyncJointJson.GetDownFilesJson(fuids);
	  	
	  	String url = SyncInfo.HvnDOWNFilesUrl+JSuserInfoJson.toString();
	  	
	  	ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_DOWN_FILES,JSuserInfoJson,
	  			url,total,tagId,null));
	  	
	}

	public void HvnCloudDeleteTags() throws JSONException{

		List<NoteBookRecord> NoteBooks = SyncDataUtils.GetAllNoteBooksNeedToDelete();
		String deleteNoteId = "";

		if (NoteBooks.size() == 0){
			HvnCloudTagsList();
			return;
		}
		int count = 0;
		for (NoteBookRecord noteBook:NoteBooks){
			if (count == 0){
				deleteNoteId = noteBook.getNoteBookId();
			}else{
				deleteNoteId = deleteNoteId + "," + noteBook.getNoteBookId();
			}
			count++;
		}
		JSONObject JSuserInfoJson = SyncJointJson.GetDeleteTagsJson(deleteNoteId);

		ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_DELETE_TAGS,JSuserInfoJson,
				SyncInfo.HvnDeleteTagsUrl,0,"",null));
	}
	
	public void HvnCloudDeleteFiles() throws JSONException{

		List<NoteRecord> FilessList = SyncDataUtils.GetAllNotesToDelete();
		if (FilessList.size() == 0){
			HvnCloudFilesList();
			return;
		}
		int count = 0;
		if (FilessList.size() != 0){
			String contents = "";
			for(NoteRecord Files:FilessList){
				if (count == 0){
					contents = Files.getNoteID()+"";
				}else{
					contents += ","+Files.getNoteID()+"";
				}
				count++;
			}
			JSONObject JSuserInfoJson = SyncJointJson.GetDeleteNotesJson(contents);

	  	    ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_DELETE_FILES,JSuserInfoJson,
				SyncInfo.HvnDeleteFilesUrl,0,"",null));
		}
	}
	
	public void HvnCloudUploadTags( ) throws JSONException{
		List<NoteBookRecord> NoteBooksList = SyncDataUtils.GetAllNoteBooksNeedToUpload();
		if (NoteBooksList == null){
			Toast.makeText(mContext, "获取上传笔记本列表失败!", Toast.LENGTH_SHORT).show();
			return;
		}

		if (NoteBooksList.size() != 0){
		    JSONObject JSuserInfoJson = SyncJointJson.GetUploadNoteBooksJson(NoteBooksList);
		
		    ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_UPLOAD_TAGS,JSuserInfoJson,
				    SyncInfo.HvnUploadTagsUrl,0,"",null));
	    }else{
			try {
				UploadFiles();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogUtil.i("--------error:"+e.toString());
			}
	    }
	}
	
	public void HvnCloudTagsList( ) throws JSONException{
		
		JSONObject JSuserInfoJson = SyncJointJson.GetNoteBooksListJson();

  	    ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_TAGS_LIST,JSuserInfoJson,
			SyncInfo.HvnTagsListUrl,0,"",null));
	}

	public void HvnCloudFilesList( ) throws JSONException{
		List<NoteBookRecord> NoteBooksList = SyncDataUtils.GetAllNoteBooks();
		String noteIds = "";
		int count = 0;
		if (NoteBooksList.size() != 0){
			for(NoteBookRecord noteBook:NoteBooksList){
			    if (count == 0){
			    	noteIds = noteBook.getNoteBookId();
			    }else{
			    	noteIds = noteIds + "," + noteBook.getNoteBookId();
			    }
			    count++;
			}
		}else{
		//	HvnSyncStatic.isDiagDimiss = true;
			HvnCloudGetSystemTime(1);
			return;
		}

		JSONObject JSuserInfoJson = SyncJointJson.GetNoteRecordsListJson(noteIds);

  	    ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_FILES_LIST,JSuserInfoJson,
			SyncInfo.HvnFilesListUrl,0,"",null));
	}

	public void UploadFiles( ) throws IOException{
		List<NoteRecord> NoteBooksList = SyncDataUtils.GetAllNotesToUpload();
		int totalLength  = NoteBooksList.size();
		SyncInfo.uploadTotal = totalLength;

		if (totalLength == 0){
			try {
				HvnCloudDeleteFiles();
			//	HvnCloudFilesList();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogUtil.i("--------error:"+e.toString());
			}
			return;
		}
		for(NoteRecord noteRecord:NoteBooksList){
			FileMsgInfo fileMsg = SyncJointJson.HvnCloudUploadFile(noteRecord);

		    ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_UPLOAD_FILE,null,
				    SyncInfo.HvnUploadFileUrl,totalLength,"",fileMsg));
		}
	}

	public void UploadSignFile(NoteRecord noteRecord ) throws IOException{
		FileMsgInfo fileMsg = SyncJointJson.HvnCloudUploadFile(noteRecord);

	    ThreadPoolUtils.execute(new MyRunnable(SyncInfo.HVN_UPLOAD_FILE,null,
			    SyncInfo.HvnUploadFileUrl,1,"",fileMsg));
	}

	public void SendException(int type){
		Message msg = new Message();
		msg.arg1 = type;
		mHandler.sendMessage(msg);
	}
    public class MyRunnable implements Runnable{
    	int type;
    	JSONObject params;
    	String url;
    	int total;
    	String noteId;
    	FileMsgInfo fileMsg;

    	public MyRunnable(int type,JSONObject parameters,String url,int total,String noteid,FileMsgInfo fileinfo){
    		this.type = type;
    		this.params = parameters;
    		this.url = url;
    		this.total = total;
    		this.noteId = noteid;
    		this.fileMsg = fileinfo;
    	}

    	@Override
    	public void run() {
    		String data = "";
    		try {
    		    // TODO Auto-generated method stub
    		    BasicHttpParams httpParams = new BasicHttpParams();
    	        HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
    	        HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
    		    if(mHttpClient == null){
    			    DefaultHttpClient client = new DefaultHttpClient(httpParams);
    			    ClientConnectionManager mgr = client.getConnectionManager();
    		        HttpParams params = client.getParams();
    		        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
    		        mHttpClient = client;
    		    }
    	        HttpResponse response = null;

    		    switch (type) {
    		        case SyncInfo.HVN_UPLOAD_FILE:
    		        	FilesManager.UploadFiletoHvn(fileMsg,total,callBackData);
    			        break;

    		        case SyncInfo.HVN_DOWN_SING_FILE:
    		        	FilesManager.DownSingFile(url, noteId);
    			        break;

    		        case SyncInfo.HVN_DOWN_FILES:
    		        	if (FilesManager.DownFileFromCloud(url,noteId)){
    		        		HvnCloudGetSystemTime(1);
    		        	}
    					break;

    		        default:
    		        	HttpPost post = new HttpPost(url);
        			    LogUtil.i("---------params:"+params.toString());
        			    post.setEntity(new StringEntity(params.toString(),"utf-8"));
        			    response = mHttpClient.execute(post);
    			        break;
    		    }
    		    
    		    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    			    data = EntityUtils.toString(response.getEntity());
    		    } else {
    			    data = null;
    		    }
    		} catch (Exception e) {
    			LogUtil.i("===error:"+e.toString());
    		//	SendException(1);
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
    		//	SendException(2);
    			LogUtil.i("--------error:"+e.toString());
    		}
    	}
    }  	
}

