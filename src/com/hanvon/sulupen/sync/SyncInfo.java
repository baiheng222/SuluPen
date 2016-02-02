package com.hanvon.sulupen.sync;

import android.content.Context;

public class SyncInfo {
	public final static int HVN_DELETE_TAGS = 1;
	public final static int HVN_DELETE_FILES = 2;
	public final static int HVN_UPLOAD_FILE = 3;
	public final static int HVN_UPLOAD_TAGS = 4;
	public final static int HVN_FILES_LIST = 5;
	public final static int HVN_DOWN_SING_FILE = 6;
	public final static int HVN_TAGS_LIST = 7;
	public final static int HVN_DOWN_FILES = 8;
	public final static int HVN_GET_SYSTEM_TIME = 9;

	public final static String HvnIpUrl = "http://dpi.hanvon.com/rt/ap/v1/";
 
    public final static String HvnDeleteTagsUrl = HvnIpUrl + "app/note/delTag";
    public final static String HvnDeleteFilesUrl = HvnIpUrl + "app/note/delContent";
    public final static String HvnUploadTagsUrl = HvnIpUrl + "app/note/uploadtags";
    public final static String HvnUploadFileUrl = HvnIpUrl + "store/upload";
    public final static String HvnFilesListUrl = HvnIpUrl + "app/note/contentlist";
    public final static String HvnTagsListUrl = HvnIpUrl + "app/note/taglist";
    public final static String HvnDOWNSingFileUrl = "http://dpi.hanvon.com/pub/file/singledownload.do?input=";
    public final static String HvnDOWNFilesUrl = "http://dpi.hanvon.com/pub/file/download.do?input=";
    public final static String HvnGetSystemTimeUrl = HvnIpUrl + "/pub/std/getSystemTime";

  //下载解压文件根目录
  	public static final String DOWN_ZIP_DIR = "/data/data/com.hanvon.sulupen/down/";
  	//下载解析的图片文件
  	public static final String DOWN_PHOTO_DIR = "/data/data/com.hanvon.sulupen/users/";
  	
   	public static int uploadTotal = 0;   	
   	public static String HvnSystemCurTime = "";
   	public static String HvnOldSynchroTime = "";
   	public static int downZipCount = 0;

   	public static int NoNeedDownNoteBooks = 0;
   	public static int NeedDownNoteBooks = 0;
   	
   	public final static String SHOWDIOGSIGN = "com.hanvon.sulupen.showdiog";
   	public final static String DIMISSDIOGSIGN = "com.hanvon.sulupen.dimissdiog";
   	public final static String SYNCPROCESSEXCEPTION = "com.hanvon.sulupen.exception";
   	
   	public static Context mContext = null;
   	
   	public synchronized static void setNoNeedDownNoteBooks(){
   		NoNeedDownNoteBooks++;
   	}
   	public synchronized static void setNeedDownNoteBooks(){
   		NeedDownNoteBooks++;
   	}
   	
   	public synchronized  static int getNoNeedDownNoteBooks(){
   		return NoNeedDownNoteBooks;
   	}
   	public synchronized  static int getNeedDownNoteBooks(){
   		return NeedDownNoteBooks;
   	}
   	
   	public synchronized  static void ClearNoNeedDownNoteBooks(){
   		NoNeedDownNoteBooks = 0;
   	}
   	public synchronized  static void ClearNeedDownNoteBooks(){
   		NeedDownNoteBooks = 0;
   	}
   	
}
