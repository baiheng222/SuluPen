package com.hanvon.sulupen.sync;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NotePhotoRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.utils.Base64Utils;
import com.hanvon.sulupen.utils.LogUtil;


/**
 * 
 * @desc 云端请求时的json封装 
 * @author chenxzhuang
 * @date 2015-12-14 下午5:32:33
 */
public class SyncJointJson {

	public static JSONObject GetSystemTimeJson() throws JSONException{
		JSONObject JSuserInfoJson = new JSONObject();
		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  	JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	JSuserInfoJson.put("ftype", "4");

	  	return JSuserInfoJson;
	}
	
	public static JSONObject GetDownSingleFileJson() throws JSONException{
		JSONObject JSuserInfoJson = new JSONObject();
		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  	JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	JSuserInfoJson.put("userid", HanvonApplication.hvnName);
	  	JSuserInfoJson.put("devid", "wwww");
	  	JSuserInfoJson.put("ftype", "4");
	  	JSuserInfoJson.put("fuid", "1449209613791");
	  	
	  	return JSuserInfoJson;
	}
	
	public static JSONObject GetDownFilesJson(String noteBooksId) throws JSONException{
		JSONObject JSuserInfoJson = new JSONObject();
		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
	  	JSuserInfoJson.put("sid", HanvonApplication.AppSid);
	  	JSuserInfoJson.put("ver", HanvonApplication.AppVer);
	  	JSuserInfoJson.put("userid", HanvonApplication.hvnName);
	  	JSuserInfoJson.put("devid", "wwww");
	  	JSuserInfoJson.put("ftype", "4");
	  	JSuserInfoJson.put("fuid", noteBooksId);
	  	
	  	return JSuserInfoJson;
	}
	
	public static JSONObject GetDeleteTagsJson(String noteBooksId) throws JSONException{
		JSONObject JSuserInfoJson = new JSONObject();
		JSuserInfoJson.put("uid", HanvonApplication.AppUid);
		JSuserInfoJson.put("sid", HanvonApplication.AppSid);
		JSuserInfoJson.put("ver", HanvonApplication.AppVer);
		JSuserInfoJson.put("userid", HanvonApplication.hvnName);
		JSuserInfoJson.put("devid", "wwww");
		JSuserInfoJson.put("ftype", "4");
		JSuserInfoJson.put("tagId", noteBooksId);
		
		return JSuserInfoJson;
	}
	
	public static JSONObject GetDeleteNotesJson(String noteRecordsId) throws JSONException{
		JSONObject JSuserInfoJson = new JSONObject();
	    JSuserInfoJson.put("uid", HanvonApplication.AppUid);
  	    JSuserInfoJson.put("sid", HanvonApplication.AppSid);
  	    JSuserInfoJson.put("ver", HanvonApplication.AppVer);
  	    JSuserInfoJson.put("userid", HanvonApplication.hvnName);
  	    JSuserInfoJson.put("devid", "wwww");
  	    JSuserInfoJson.put("ftype", "4");
  	    JSuserInfoJson.put("contentId", noteRecordsId);
		
		return JSuserInfoJson;
	}
	
	public static JSONObject GetUploadNoteBooksJson(List<NoteBookRecord> NoteBooksList) throws JSONException{
		JSONArray array = new JSONArray();
		if (NoteBooksList.size() != 0){
			for(NoteBookRecord noteBook:NoteBooksList){
			    JSONObject JSuserInfoJson1 = new JSONObject();
			    JSuserInfoJson1.put("tagId", noteBook.getNoteBookId());
			    JSuserInfoJson1.put("tagName", noteBook.getNoteBookName());
			    array.put(JSuserInfoJson1);
			    SyncDataUtils.UpdateNoteBookState(noteBook.getNoteBookId(),1);
			}
		}
		JSONObject JSuserInfoJson = new JSONObject();
	    JSuserInfoJson.put("uid", HanvonApplication.AppUid);
  	    JSuserInfoJson.put("sid", HanvonApplication.AppSid);
  	    JSuserInfoJson.put("ver", HanvonApplication.AppVer);
  	    JSuserInfoJson.put("userid", HanvonApplication.hvnName);
  	    JSuserInfoJson.put("devid", "wwww");
  	    JSuserInfoJson.put("tags", array);
  	    
  	    return JSuserInfoJson;
	}
	
	public static JSONObject GetNoteBooksListJson() throws JSONException{
		JSONObject conditionJson = new JSONObject();
		conditionJson.put("beginTime", SyncInfo.HvnOldSynchroTime);
		conditionJson.put("endTime", SyncInfo.HvnSystemCurTime);
		
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
  	    
  	    return JSuserInfoJson;
	}
	
	public static JSONObject GetNoteRecordsListJson(String noteIds) throws JSONException{
		JSONObject conditionJson = new JSONObject();
		conditionJson.put("beginTime", SyncInfo.HvnOldSynchroTime);
		conditionJson.put("endTime", SyncInfo.HvnSystemCurTime);

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
  	    
  	    return JSuserInfoJson;
	}
	
	
	public static FileMsgInfo HvnCloudUploadFile(NoteRecord noteRecord) throws IOException{
		String filename = noteRecord.getNoteID() +".txt";
		String path = "/sdcard/" + filename;

		File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }

		ArrayList<NotePhotoRecord> PhootoRecordList = noteRecord.getNotePhotoList();
		
		FileMsgInfo fileMsg = new FileMsgInfo();
		
		FileWriter writer = new FileWriter(path, true);
		writer.write("{\"createTime\":\"" + noteRecord.getCreateTime() +
				     "\",\"address\":\"" + Base64Utils.encode(noteRecord.getCreateAddr().getBytes()) +
				     "\",\"title\":\"" + Base64Utils.encode(noteRecord.getNoteTitle().getBytes()) +
				  //   "\",\"content\":\"" + noteRecord.getNoteContent() + "\",\"images\":[");
				     "\",\"content\":\"" + Base64Utils.encode(noteRecord.getNoteContent().getBytes()) + "\",\"images\":[");

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
		
		fileMsg.setFilePath(path);
		fileMsg.setNoteRecordId(noteRecord.getNoteID());
		fileMsg.setNoteBookId(noteRecord.getNoteBook().getNoteBookId());
		fileMsg.setTitle(noteRecord.getNoteTitle());

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
	
}
