package com.hanvon.sulupen.sync;

import java.util.List;
import java.util.UUID;

import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NotePhotoRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;
import com.hanvon.sulupen.db.dao.NotePhotoRecordDao;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.utils.DataBaseUtils;
import android.content.Context;


/**
 * 
 * @desc 再次封装的与数据库的交互函数 
 * @author chenxzhuang
 * @date 2015-12-14 下午5:31:47
 */
public class SyncDataUtils {

	private Context mContext;
	private static DataBaseUtils DataBase;
	private static NoteRecordDao mScanRecordDao;
	private static NotePhotoRecordDao mPhotoRecordDao;
	private static NoteBookRecordDao noteBookRecordDao;
	
	private static SyncDataUtils dataUtil = null;

	public SyncDataUtils(Context context){
		this.mContext = context;
		init();
	}

	public static SyncDataUtils getInstance(Context context) {
		if (dataUtil == null) {
			dataUtil = new SyncDataUtils(context);
		}
		return dataUtil;
	}

	private void init(){
		DataBase = new DataBaseUtils(mContext);
		mScanRecordDao = new NoteRecordDao(mContext);

		mPhotoRecordDao = new NotePhotoRecordDao(mContext);
	    noteBookRecordDao = new NoteBookRecordDao(mContext);
	}
	
	public static void DeleteNoteBookFromCloud(String noteBookId){
		DataBase.DeleteNoteBookFromCloud(noteBookId);
	}
	
	public static int GetNoteBookCompState(String noteBookId,String noteBookName){
		return DataBase.GetNoteBookCompState(noteBookId,noteBookName);
	}
	
	public static void UpdateNoteBookNameByID(String noteBookId,String noteBookName){
		DataBase.UpdateNoteBookNameByID(noteBookId,noteBookName);
	}
	
	public static NoteBookRecord GetNoteBookByName(String noteBookName){
	    return DataBase.GetNoteBookByName(noteBookName);
	}
	
	public static void NoteBookAdd(NoteBookRecord NoteBook){
		noteBookRecordDao.add(NoteBook);
	}
	
	public static void DeleteNoteRecordByUUId(UUID contentId){
		DataBase.DeleteNoteRecordByUUId(contentId);
	}

	public static void NoteRecordAdd(NoteRecord note){
		mScanRecordDao.add(note);
	}
	
	public static void NoteRecordAddPhoto(NotePhotoRecord cphote){
		mPhotoRecordDao.add(cphote);
	}

	public static NoteBookRecord getNoteBookNameById(String NoteBookId){
        NoteBookRecord notebook = noteBookRecordDao.geNoteBookRecordByStringID(NoteBookId);
        return notebook;
	}
	
	public static List<NoteBookRecord> GetAllNoteBooksNeedToDelete(){
		return DataBase.GetAllNoteBooksNeedToDelete();
	}
	
	public static List<NoteRecord> GetAllNotesToDelete(){
		return DataBase.GetAllNotesToDelete();
	}
	
	public static List<NoteBookRecord> GetAllNoteBooksNeedToUpload(){
		return DataBase.GetAllNoteBooksNeedToUpload();
	}
	
	public static void UpdateNoteBookState(String noteBookId,int flag){
		DataBase.UpdateNoteBookState(noteBookId,flag);
	}
	
	public static List<NoteBookRecord> GetAllNoteBooks(){
		return DataBase.GetAllNoteBooks();
	}
	
	public static List<NoteRecord> GetAllNotesToUpload(){
		return DataBase.GetAllNotesToUpload();
	}
	
	public static void UpdateNoteState(UUID noteRecordId,int flag){
		DataBase.UpdateNoteState(noteRecordId, flag);
	}
	
	public static void DeleteAllSyncedNoteBooks(){
		DataBase.DeleteAllSyncedNoteBooks();
	}
	public static void DeleteAllNoteRecords(){
		DataBase.DeleteAllNoteRecords();
	}
}
