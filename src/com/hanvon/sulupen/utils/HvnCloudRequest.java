package com.hanvon.sulupen.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.Toast;

import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.datas.ImageItem;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NotePhotoRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.bean.RecordInfo;
import com.hanvon.sulupen.utils.HvnSyncStatic.FilesInfo;
import com.hanvon.sulupen.utils.HvnSyncStatic.UploadFileMsg;
import com.hanvon.sulupen.utils.DataBaseUtils;

public class HvnCloudRequest {

	private static int BUF_SIZE = 32768;
	
	private static DataBaseUtils DataBase = new DataBaseUtils(HvnSyncStatic.mcontext);
	
	public static void HvnCloudGetSystemTime(ObserverCallBack callBackData,int flag) throws JSONException{
		JSONObject JSuserInfoJson = new JSONObject();
		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  	JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	JSuserInfoJson.put("ftype", "4");

	  	ThreadPoolUtils.execute(new MyRunnable(HvnSyncStatic.HVN_GET_SYSTEM_TIME,JSuserInfoJson,
	  			HvnSyncStatic.HvnGetSystemTimeUrl,callBackData,flag,"",null));
	}
	
	public static void HvnCloudDownSingleFile(ObserverCallBack callBackData) throws JSONException{
		JSONObject JSuserInfoJson = new JSONObject();
		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  	JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	JSuserInfoJson.put("userid", HanvonApplication.hvnName);
	  	JSuserInfoJson.put("devid", "wwww");
	  	JSuserInfoJson.put("ftype", "4");
	  	JSuserInfoJson.put("fuid", "1449209613791");
	  	
	  	String url = HvnSyncStatic.HvnDOWNSingFileUrl+JSuserInfoJson.toString();

	  	ThreadPoolUtils.execute(new MyRunnable(HvnSyncStatic.HVN_DOWN_SING_FILE,JSuserInfoJson,
	  			url,callBackData,0,"7654321",null));
	  	
	}

	public static void HvnCloudDownFiles(ObserverCallBack callBackData,ArrayList<FilesInfo> filesInfo,int total) throws JSONException{
		String fuids = "";
		String tagId = "";
		int count = 0;

		for(FilesInfo fileinfo:filesInfo){
			if (fileinfo.isDown){
			    if (count == 0){
				    fuids = fileinfo.fuid;
				    tagId = fileinfo.tagId;
			    }else{
			    	fuids = fuids + "," + fileinfo.fuid;
			    }
			    count++;
			}
		}

		if (fuids.equals("")){
			HvnSyncStatic.NoNeedDownNoteBooks++;
			if (HvnSyncStatic.NeedDownNoteBooks + HvnSyncStatic.NoNeedDownNoteBooks == HvnSyncStatic.downZipCount){
				HvnSyncStatic.isDiagDimiss = true;
				HvnSyncStatic.NoNeedDownNoteBooks = 0;
				HvnSyncStatic.NeedDownNoteBooks = 0;
			}

			if (total == HvnSyncStatic.downZipCount){
				if (HvnSyncStatic.isDiagDimiss){
				    HvnCloudGetSystemTime(callBackData,1);
				}
			}
			return;
		}
		JSONObject JSuserInfoJson = new JSONObject();
		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  	JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	JSuserInfoJson.put("userid", HanvonApplication.hvnName);
	  	JSuserInfoJson.put("devid", "wwww");
	  	JSuserInfoJson.put("ftype", "4");
	  	JSuserInfoJson.put("fuid", fuids);
	  	
	  	String url = HvnSyncStatic.HvnDOWNFilesUrl+JSuserInfoJson.toString();
	  	
	  	ThreadPoolUtils.execute(new MyRunnable(HvnSyncStatic.HVN_DOWN_FILES,JSuserInfoJson,
	  			url,callBackData,total,tagId,null));
	  	
	}

	public static void HvnCloudDeleteTags(ObserverCallBack callBackData) throws JSONException{

		List<NoteBookRecord> NoteBooks = DataBase.GetAllNoteBooksNeedToDelete();
		String deleteNoteId = "";

		if (NoteBooks.size() == 0){
			HvnCloudTagsList(callBackData);
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
		JSONObject JSuserInfoJson = new JSONObject();
		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
		JSuserInfoJson.put("sid", HanvonApplication.AppSid);
		JSuserInfoJson.put("ver", HanvonApplication.AppVer);
		JSuserInfoJson.put("userid", HanvonApplication.hvnName);
		JSuserInfoJson.put("devid", "wwww");
		JSuserInfoJson.put("ftype", "4");
		JSuserInfoJson.put("tagId", deleteNoteId);

		ThreadPoolUtils.execute(new MyRunnable(HvnSyncStatic.HVN_DELETE_TAGS,JSuserInfoJson,
					HvnSyncStatic.HvnDeleteTagsUrl,callBackData,0,"",null));
	}
	
	public static void HvnCloudDeleteFiles(ObserverCallBack callBackData) throws JSONException{

		List<NoteRecord> FilessList = DataBase.GetAllNotesToDelete();
		if (FilessList.size() == 0){
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
		    JSONObject JSuserInfoJson = new JSONObject();
		    JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  	    JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	    JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	    JSuserInfoJson.put("userid", HanvonApplication.hvnName);
	  	    JSuserInfoJson.put("devid", "wwww");
	  	    JSuserInfoJson.put("ftype", "4");
	  	    JSuserInfoJson.put("contentId", contents);

	  	    ThreadPoolUtils.execute(new MyRunnable(HvnSyncStatic.HVN_DELETE_FILES,JSuserInfoJson,
				HvnSyncStatic.HvnDeleteFilesUrl,callBackData,0,"",null));
		}
	}
	
	public static void HvnCloudUploadTags(ObserverCallBack callBackData) throws JSONException{
		List<NoteBookRecord> NoteBooksList = DataBase.GetAllNoteBooksNeedToUpload();
		if (NoteBooksList == null){
			Toast.makeText(HvnSyncStatic.mcontext, "获取上传笔记本列表失败!", Toast.LENGTH_SHORT).show();
			return;
		}

		JSONArray array = new JSONArray();
		if (NoteBooksList.size() != 0){
			for(NoteBookRecord noteBook:NoteBooksList){
			    JSONObject JSuserInfoJson1 = new JSONObject();
			    JSuserInfoJson1.put("tagId", noteBook.getNoteBookId());
			    JSuserInfoJson1.put("tagName", noteBook.getNoteBookName());
			    array.put(JSuserInfoJson1);
			    DataBase.UpdateNoteBookState(noteBook.getNoteBookId(),1);
			}
		    JSONObject JSuserInfoJson = new JSONObject();
		    JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  	    JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	    JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	    JSuserInfoJson.put("userid", HanvonApplication.hvnName);
	  	    JSuserInfoJson.put("devid", "wwww");
	  	    JSuserInfoJson.put("tags", array);
		    //String tags = "{\"tags\":[{\"tagId\":\"11111\", \"tagName\":\"笔记本上传测试1\"}, {\"tagId\":\"22222\", \"tagName\":\"笔记本上传测试2\"}]\",\"userid\":\"test2345\",\"uid\":\"10046\",\"sid\":\"SuluPen_Software\",\"ver\":\"1.0.1.021\",\"devid\":\"wwww\"}";
		
		    ThreadPoolUtils.execute(new MyRunnable(HvnSyncStatic.HVN_UPLOAD_TAGS,JSuserInfoJson,
				    HvnSyncStatic.HvnUploadTagsUrl,callBackData,0,"",null));
	    }else{
	    	//没有需要上传的笔记本，则直接上传文件
	    	try {
				UploadFiles(callBackData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	public static void HvnCloudTagsList(ObserverCallBack callBackData) throws JSONException{
		JSONObject conditionJson = new JSONObject();
		conditionJson.put("beginTime", HvnSyncStatic.HvnOldSynchroTime);
		conditionJson.put("endTime", HvnSyncStatic.HvnSystemCurTime);
		
		JSONObject JSuserInfoJson = new JSONObject();
	    JSuserInfoJson.put("uid", HanvonApplication.AppUid);
  	    JSuserInfoJson.put("sid", HanvonApplication.AppSid);
  	    JSuserInfoJson.put("ver", HanvonApplication.AppVer);
  	    JSuserInfoJson.put("userid", HanvonApplication.hvnName);
  	    JSuserInfoJson.put("devid", "wwww");
  	    JSuserInfoJson.put("ftype", "4");
  	  //  JSuserInfoJson.put("sort", "{\"last_modify_time\":\"desc\"}");
  	    JSuserInfoJson.put("start", "");
  	    JSuserInfoJson.put("count", "1000");
  	    JSuserInfoJson.put("condition", conditionJson);

  	    ThreadPoolUtils.execute(new MyRunnable(HvnSyncStatic.HVN_TAGS_LIST,JSuserInfoJson,
			HvnSyncStatic.HvnTagsListUrl,callBackData,0,"",null));
	}

	public static void HvnCloudFilesList(ObserverCallBack callBackData) throws JSONException{
		List<NoteBookRecord> NoteBooksList = DataBase.GetAllNoteBooks();
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
			HvnSyncStatic.isDiagDimiss = true;
			HvnCloudGetSystemTime(callBackData,1);
			return;
		}

		JSONObject conditionJson = new JSONObject();
		conditionJson.put("beginTime", HvnSyncStatic.HvnOldSynchroTime);
		conditionJson.put("endTime", HvnSyncStatic.HvnSystemCurTime);

		JSONObject JSuserInfoJson = new JSONObject();
	    JSuserInfoJson.put("uid", HanvonApplication.AppUid);
  	    JSuserInfoJson.put("sid", HanvonApplication.AppSid);
  	    JSuserInfoJson.put("ver", HanvonApplication.AppVer);
  	    JSuserInfoJson.put("userid", HanvonApplication.hvnName);
  	    JSuserInfoJson.put("devid", "wwww");
  	    JSuserInfoJson.put("ftype", "4");
  	    JSuserInfoJson.put("tagId", noteIds);
  	    JSuserInfoJson.put("condition", conditionJson);
 // 	    JSuserInfoJson.put("sort", "{\"modifyTime\":\"desc\"}");
  	    JSuserInfoJson.put("start", "");
	    JSuserInfoJson.put("count", "1000");

  	    ThreadPoolUtils.execute(new MyRunnable(HvnSyncStatic.HVN_FILES_LIST,JSuserInfoJson,
			HvnSyncStatic.HvnFilesListUrl,callBackData,0,"",null));
	}

	public static void UploadFiles(ObserverCallBack callBackData) throws IOException{
		List<NoteRecord> NoteBooksList = DataBase.GetAllNotesToUpload();
		int totalLength  = NoteBooksList.size();
		HvnSyncStatic.uploadTotal = totalLength;

		if (totalLength == 0){
			try {
				HvnCloudFilesList(callBackData);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		for(NoteRecord noteRecord:NoteBooksList){
			UploadFileMsg fileMsg = HvnCloudUploadFile(noteRecord);

		    ThreadPoolUtils.execute(new MyRunnable(HvnSyncStatic.HVN_UPLOAD_FILE,null,
				    HvnSyncStatic.HvnUploadFileUrl,callBackData,totalLength,"",fileMsg));
		}
	}
	
	//更新上传笔记的状态
	public static void UpdateNoteStatus(UUID noteRecordId){
		DataBase.UpdateNoteState(noteRecordId, 1);
	}
	
	private static UploadFileMsg HvnCloudUploadFile(NoteRecord noteRecord) throws IOException{
		String filename = noteRecord.getNoteID() +".txt";
		String path = "/sdcard/" + filename;

		File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }

		ArrayList<NotePhotoRecord> PhootoRecordList = noteRecord.getNotePhotoList();
		
		UploadFileMsg fileMsg = new UploadFileMsg();;
		
		FileWriter writer = new FileWriter(path, true);
		writer.write("{\"createTime\":\"" + noteRecord.getCreateTime() +
				     "\",\"address\":\"" + noteRecord.getCreateAddr() +
				     "\",\"title\":\"" + noteRecord.getNoteTitle() +
				     "\",\"content\":\"" + noteRecord.getNoteContent() + "\",\"images\":[");

		int count = 0;
		for(NotePhotoRecord item:PhootoRecordList){
			if (count == 0){
				writer.write("{\"image\":\"");
				count++;
			}else{
				writer.write("\"},{\"image\":\"");
			}
			
			String base64 = getImageBase64(item.getLocalUrl());
    	    writer.write(base64);
			writer.flush();
		}
		if (count != 0){
			writer.write("\"}]}");
		}else{
			writer.write("]}");
		}
		writer.flush();

		writer.close();
		
		fileMsg.filePath = path;
		fileMsg.contentId = noteRecord.getNoteID();
		fileMsg.tagId = noteRecord.getNoteBook().getNoteBookId();
		fileMsg.title = noteRecord.getNoteTitle();

	    return fileMsg;
	}
	
	private static String getImageBase64(String srcPath) throws IOException{
		String imageBase64 = "";
		Bitmap rawBitmap = BitmapFactory.decodeFile(srcPath,null);
		int w = rawBitmap.getWidth();
        int h = rawBitmap.getHeight();
        LogUtil.i("w:"+w+"         h:"+h);
        if (w > 400 || h > 400){
        	float hh = 400f;//这里设置高度为800f
            float ww = 400f;//这里设置宽度为480f
            float be = 1;
            if ((w > h) && (w > ww)) {//如果宽度大的话根据宽度固定大小缩放
            	be = (float)(ww/w);
            } else if ((w < h) && (h > hh)) {//如果高度高的话根据宽度固定大小缩放
            	be = (float)(hh/h);
            }
            if (be <= 0)
                be = 1;
            Matrix matrix = new Matrix();
            matrix.postScale(be, be);
            Bitmap bitmap = Bitmap.createBitmap(rawBitmap, 0, 0, w, h,matrix,true);

            try {
            	ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
                out.flush();
                out.close();
                byte[] imgBytes = out.toByteArray();
                imageBase64 = Base64Utils.encode(imgBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
        	rawBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            byte[] imgBytes = out.toByteArray();
            imageBase64 = Base64Utils.encode(imgBytes);
        }
        return imageBase64;
	}
	
	public static void DeleteLocalNoteBooks(){
		DataBase.DeleteAllSyncedNoteBooks();
	}
	
    public static void DeleteLocalNotes(){
		DataBase.DeleteAllNoteRecords();
	}
}
