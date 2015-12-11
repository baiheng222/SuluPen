package com.hanvon.sulupen.utils;

import java.util.UUID;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;

class HvnSyncStatic {
	public final static int HVN_DELETE_TAGS = 1;
	public final static int HVN_DELETE_FILES = 2;
	public final static int HVN_UPLOAD_FILE = 3;
	public final static int HVN_UPLOAD_TAGS = 4;
	public final static int HVN_FILES_LIST = 5;
	public final static int HVN_DOWN_SING_FILE = 6;
	public final static int HVN_TAGS_LIST = 7;
	public final static int HVN_DOWN_FILES = 8;
	public final static int HVN_GET_SYSTEM_TIME = 9;
	
    public static String HvnIpUrl = "http://cloud.hwyun.com/dws-cloud/rt/ap/v1/";
 	
	public static String HvnDeleteTagsUrl = HvnIpUrl + "app/note/delTag";
	public static String HvnDeleteFilesUrl = HvnIpUrl + "app/note/delContent";
	public static String HvnUploadTagsUrl = HvnIpUrl + "app/note/uploadtags";
	public static String HvnUploadFileUrl = HvnIpUrl + "store/upload";
	public static String HvnFilesListUrl = HvnIpUrl + "app/note/contentlist";
	public static String HvnTagsListUrl = HvnIpUrl + "app/note/taglist";
	public static String HvnDOWNSingFileUrl = "http://cloud.hwyun.com/dws-cloud/pub/file/singledownload.do?input=";
	public static String HvnDOWNFilesUrl = "http://cloud.hwyun.com/dws-cloud/pub/file/download.do?input=";
	public static String HvnGetSystemTimeUrl = HvnIpUrl + "/pub/std/getSystemTime";
	
	
	public static DefaultHttpClient mHttpClient;
	public static final int REQUEST_TIMEOUT = 10*1000;//设置请求超时10秒钟
	public static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟
	public static int uploadTotal = 0;
	
	public static String HvnSystemCurTime = "";
	public static String HvnOldSynchroTime = "";
	public static Context mcontext;
	public static ProgressDialog pd;
	public static boolean isConnNetwork;
	public static int downZipCount = 0;
	public static boolean isError = false;
	
	public static int NoNeedDownNoteBooks = 0;
	public static int NeedDownNoteBooks = 0;
	public static boolean isDiagDimiss = false;
	
	public static class FilesInfo{
		String tagId;
		UUID contentId;
		String title;
		String fuid;
		String isDelete;
		String modifyTime;
		boolean isDown;
	}
	
	public static class UploadFileMsg{
		String tagId;
		String filePath;
		UUID contentId;
		String title;
	}

	//下载解压文件根目录
	public static final String DOWN_ZIP_DIR = "/data/data/com.hanvon.sulupen/down/";
	//下载解析的图片文件
	public static final String DOWN_PHOTO_DIR = "/data/data/com.hanvon.sulupen/users/";
}
