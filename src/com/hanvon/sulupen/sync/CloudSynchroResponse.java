package com.hanvon.sulupen.sync;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.datas.ImageItem;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NotePhotoRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.utils.Base64Utils;

/**
 * 
 * @desc 该文件主要用于在云端正确返回后，对数据字段的处理
 * @author chenxzhuang
 * @date 2015-12-14 下午5:28:12
 */
public class CloudSynchroResponse {

	private static int BUF_SIZE = 32768;
//	private Context mContext;
	//108  140

	public static void ParseTagsList(String data) throws JSONException{
		JSONArray jsonArray = new JSONArray(data);
		for(int i = 0;i < jsonArray.length();i++){
			JSONObject json = jsonArray.getJSONObject(i);
			String tagid = json.getString("tagId");
			String tagname = json.getString("tagName");
			String isDelete = json.getString("delFlag");
			if (isDelete.equals("1")){
				SyncDataUtils.DeleteNoteBookFromCloud(tagid);
				continue;
			}
			String lastTime = json.getString("modifyTime");
//			LogUtil.i("第 "+ i + " 个笔记本,tagid："+tagid+", tagname:"+tagname+", isdelete:"+isDelete+", time:"+lastTime);
			/***
			 * 1.服务器端的tagid与本地的tagid以及tagname一致，不做处理
			 * 2.服务器端的tagid与本地的tagid一致，但是tagname不一致，直接对本地的tagname进行更新
			 * 3.服务器端的tagid与本地的tagid不一致，但是tangname一致，对服务端的tagname进行更新，并将新的笔记本isupload标记置为0
			 * 4.服务器端的tagid和tagname都不一致，则直接插入
			 */
			NoteBookRecord notebook = new NoteBookRecord();

			int result = SyncDataUtils.GetNoteBookCompState(tagid,tagname);
			if (result == 0){
				//不做处理
			}else if (result == 1){
				//更新本地的笔记本名称
				SyncDataUtils.UpdateNoteBookNameByID(tagid,tagname);
			}else if (result == 2){
				for(int j = 0;j < 10;j++){
					tagname = tagname + "_" + j;
				    NoteBookRecord result1 = null;
				    if ((result1 = SyncDataUtils.GetNoteBookByName(tagname)) == null){
					    //插入笔记本
					    notebook.setNoteBookName(tagname);
					    notebook.setNoteBookId(tagid);
					 //   String time = TimeUtil.getCurTimeForMd5();
					//    notebook.setNoteBookId(MD5Util.md5(time));
					    notebook.setNoteBookUpLoad(0);
					    notebook.setNoteBookDelete(0);

					    SyncDataUtils.NoteBookAdd(notebook);
					    break;
				    }
				}
			}else if (result == 3){
				//插入笔记本
				notebook.setNoteBookName(tagname);
				notebook.setNoteBookId(tagid);
			//	String time = TimeUtil.getCurTimeForMd5();
			//	notebook.setNoteBookId(MD5Util.md5(time));
				notebook.setNoteBookUpLoad(1);
				notebook.setNoteBookDelete(0);

				SyncDataUtils.NoteBookAdd(notebook);
			}
		}
	}

	public static ArrayList<DownFilesMsg> ParseFilesList(String data) throws JSONException{
		JSONArray jsonArray = new JSONArray(data);

		 int total = jsonArray.length();
		 SyncInfo.downZipCount = total;
		if (total == 0){
		//	HvnSyncStatic.pd.dismiss();
		//	HvnSyncStatic.isDiagDimiss = true;
	//		HvnCloudRequest.HvnCloudGetSystemTime(1);
			return null;
		}
		ArrayList<DownFilesMsg> fileMsgList = new ArrayList<DownFilesMsg>();
		for(int i = 0;i < total;i++){
			DownFilesMsg fileMsg = new DownFilesMsg();
			
			ArrayList<FileMsgInfo> filesInfo = new ArrayList<FileMsgInfo>();
			JSONObject json = jsonArray.getJSONObject(i);
			String tagId = json.getString("tagId");
			fileMsg.setNoteBookId(tagId);
			String contents = json.getString("contents");
			JSONArray conArray = new JSONArray(contents);
			for(int j = 0;j < conArray.length();j++){
				FileMsgInfo fileInfo = new FileMsgInfo();
				JSONObject conJson = conArray.getJSONObject(j);
			//	fileInfo.contentId = UUID.fromString(conJson.getString("contentId"));
				String contentid = conJson.getString("contentId");
			//	contentid = "38400000-8cf0-11bd-b23e-10b96e4ef00"+j;
				fileInfo.setNoteBookId(tagId);
				fileInfo.setNoteRecordId(UUID.fromString(contentid));
				fileInfo.setTitle(conJson.getString("title"));
				fileInfo.setCloudId(conJson.getString("fuid"));
				fileInfo.setIsDelete(conJson.getString("delFlag"));
				if (fileInfo.getIsDelete().equals("1")){
					SyncDataUtils.DeleteNoteRecordByUUId(fileInfo.getNoteRecordId());
					continue;
				}else{
					fileInfo.setIsDown(true);
				}
				fileInfo.setModifyTime(conJson.getString("modifyTime"));
				
				filesInfo.add(fileInfo);
			}
			fileMsg.setFileMsg(filesInfo);
			fileMsgList.add(fileMsg);
		    //分笔记本进行下载
		//     HvnCloudDownFiles(filesInfo,i+1);
		}
		return fileMsgList;
	}

	public static void UnZipFile(String filePath,String tagid) throws IOException, JSONException{
		// 创建解压目标目录
		String outputDirectory = SyncInfo.DOWN_ZIP_DIR + tagid + "/";
		File file = new File(outputDirectory);
		// 如果目标目录不存在，则创建
		if (!file.exists()) {
		    file.mkdirs();
		}
		// 打开压缩文件
		File src = new File(filePath);
		InputStream inputStream = new FileInputStream(src);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		// 读取一个进入点
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		// 使用1Mbuffer
		byte[] buffer = new byte[1024 * 1024];
		// 解压时字节计数
		int count = 0;
		// 如果进入点为空说明已经遍历完所有压缩包中文件和目录
		while (zipEntry != null) {
		    // 如果是一个目录
		    if (zipEntry.isDirectory()) {
		        file = new File(outputDirectory + File.separator + zipEntry.getName());
		        // 文件需要覆盖或者是文件不存在
		        if (!file.exists()) {
		            file.mkdir();
		        }
		    } else {
		        // 如果是文件
		        file = new File(outputDirectory + File.separator + zipEntry.getName());
		        // 文件需要覆盖或者文件不存在，则解压文件
		        if (!file.exists()) {
		            file.createNewFile();
		            FileOutputStream fileOutputStream = new FileOutputStream(file);
		            while ((count = zipInputStream.read(buffer)) > 0) {
		                fileOutputStream.write(buffer, 0, count);
		            }
		            fileOutputStream.close();
		        }
		    }
		    // 定位到下一个文件入口
		    zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();
		
		ParseFile(outputDirectory,tagid);
		
		//删除对应解压目录
		deleteUnZipDir(outputDirectory);
	}
	
	public static void deleteUnZipDir(String SDPATH) {
	    File dir = new File(SDPATH);
	    if (dir == null || !dir.exists() || !dir.isDirectory())
	        return;
	         
	    for (File file : dir.listFiles()) {
	        if (file.isFile())
	            file.delete(); // 删除所有文件
	    }
	    dir.delete();// 删除目录本身
	}

	public static void ParseFile(String path,String tagId) throws IOException, JSONException {
        File file = new File(path);
        File[] subFile = file.listFiles();
 
        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
				ParseFileContent(path+filename,tagId,filename.substring(0, filename.lastIndexOf(".")));
            }
        }
    }
	
	public static void ParseFileContent(String base64File,String dir,String contentId) throws IOException, JSONException{
		byte[] buffer;
		String fileContent = null;
	    File file = new File(base64File);

	    String fileDir =  SyncInfo.DOWN_PHOTO_DIR + HanvonApplication.hvnName + "/" + dir + "/";
	    File file1 = new File(fileDir);
		// 如果目标目录不存在，则创建
		if (!file1.exists()) {
		    file1.mkdirs();
		}

		FileInputStream fis = new FileInputStream(file); 
		int length = fis.available();

        buffer =  new byte[length];
        fis.read(buffer);
        fileContent = new String(buffer);
        fis.close();

        JSONObject json = new JSONObject(fileContent);
        byte[] titlebyte = Base64Utils.decode(json.getString("title"));
        byte[] contentbyte = Base64Utils.decode(json.getString("content"));
        String content = new String(contentbyte);
        String title = new String(titlebyte);
        String createTime = "";
        String address = "";
        if (!json.isNull("createTime")){
        	createTime = json.getString("createTime");
        }
        if (!json.isNull("address")){
        	byte[] addressbyte = Base64Utils.decode(json.getString("address"));
        	if (address.equals("null") || addressbyte == null){
        		address = "";
        	}else{
        	    address = new String(addressbyte);
        	}
        }
        if (!json.isNull("images")){
            JSONArray imgArray = new JSONArray(json.getString("images"));
            List<ImageItem> mDataList = new ArrayList<ImageItem>();
            for (int i = 0;i < imgArray.length();i++){
        	    ImageItem Imagitem = new ImageItem();
        	    JSONObject imgJson = imgArray.getJSONObject(i);
        	    String imagsrc = imgJson.getString("image");
        	    byte[]bitmapArray;
                bitmapArray=Base64.decode(imagsrc, Base64.DEFAULT);
                Bitmap  bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                String localPhotoUrl = fileDir + contentId + "_" + i + ".jpg";
                Imagitem.sourcePath = localPhotoUrl;
                mDataList.add(Imagitem);
        	    saveBitmapFile(bitmap,localPhotoUrl);
            }
            saveNoteToDb(title,content,contentId,mDataList,dir,createTime,address);
        }else{
        	saveNoteToDb(title,content,contentId,null,dir,createTime,address);
        }
	}

	public static void saveBitmapFile(Bitmap bitmap,String localPhotoUrl){

        File file=new File(localPhotoUrl);//将要保存图片的路径  头像文件
        try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

	public static void saveNoteToDb(String title,String content,String contentId,List<ImageItem> mDataList,String tagname,String createTime,String address){
		NoteRecord note = new NoteRecord();
		NoteBookRecord notebook = SyncDataUtils.getNoteBookNameById(tagname);

		SyncDataUtils.DeleteNoteRecordByUUId(UUID.fromString(contentId));

		note.setNoteTitle(title);
		note.setNoteContent(content);
		note.setNoteBookName(notebook.getNoteBookName());
		note.setCreateAddr(address);
		note.setCreateTime(createTime);//---------------
		note.setWeather("");
        
		note.setInputType(0);
		note.setAddrDetail("");//----------
		note.setNoteBook(notebook);
		note.setNoteID(UUID.fromString(contentId));
		note.setUpLoad(1);
		note.setIsDelete(0);
		SyncDataUtils.NoteRecordAdd(note);

		//添加图片
		if (mDataList != null){
		    for(int i=0;i<mDataList.size();i++)
		    {
		        NotePhotoRecord cphote = new NotePhotoRecord();
			    cphote.setLocalUrl(mDataList.get(i).sourcePath);
			    cphote.setNote(note);
			    SyncDataUtils.NoteRecordAddPhoto(cphote);
		    }
		}
	}
	
	public static void DeleteLocalNoteBooks(){
		SyncDataUtils.DeleteAllSyncedNoteBooks();
	}
	
    public static void DeleteLocalNotes(){
    	SyncDataUtils.DeleteAllNoteRecords();
	}
}

