package com.hanvon.sulupen.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.hanvon.sulupen.db.bean.NoteBookInfo;
import com.hanvon.sulupen.db.bean.NoteInfo;
import com.hanvon.sulupen.db.bean.ScanRecord;

import com.hanvon.sulupen.db.helper.SCanRecordHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;

import android.content.Context;
import android.util.Log;
import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.db.bean.*;


public class NotePhotoRecordDao 
{
	private static String TAG = "NotePhotoRecordDao";
	private SCanRecordHelper mHelper;
	private Dao<NotePhotoRecord, Integer> mNotePhotoRecordDao;
	private List<NotePhotoRecord> mScanRecordList;
	//private List<NoteBookInfo> mNoteBookList = new ArrayList<NoteBookInfo>();
	//private List<NoteInfo> mNoteList = new ArrayList<NoteInfo>();
	private int mUpdateCount;
	//private List<DataListBean> mDayInfoList;
	//private List<YMDInfo> yearList=new ArrayList<YMDInfo>();
	// 每次加载 月份数
	//public static final int KLOAD_LASTEST_MON = 3;
	
	private static NotePhotoRecordDao instance = null;
	
	public static NotePhotoRecordDao getNotePhotoRecordDaoInstance()
	{
	    return instance;
	}

	public NotePhotoRecordDao(Context context) 
	{
		try 
		{
			mHelper = SCanRecordHelper.getHelper(context);
			mNotePhotoRecordDao = mHelper.getDao(NotePhotoRecord.class);
			instance = this;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		//getDayInfo();
	}

	// 保存一条数据
	public void add(NotePhotoRecord article) 
	{
		try 
		{
			mNotePhotoRecordDao.create(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//删除一条记录
	public void deleteRecord(NotePhotoRecord article)
	{
		try 
		{
			mNotePhotoRecordDao.delete(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}



	// 修改数据库里面的数据
	public int updataScanRecord(NotePhotoRecord mScanRecord) 
	{
		try 
		{
			mUpdateCount = mNotePhotoRecordDao.update(mScanRecord);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return mUpdateCount;
	}
	
	//清空数据
    public void clearRecords() 
    {
        //      Log.i("ymq", "mDayInfoList:"+mDayInfoList);
        //mDayInfoList.clear();    
    }
    
    //清空数据库
    public void deleteRecordsDB() 
    {
        try 
        {
        	mNotePhotoRecordDao.delete(mScanRecordList);
        } 
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

	
	// 查询数据库里面所有的数据
    public List<NotePhotoRecord> query() 
    {
        try 
        {
            mScanRecordList = mNotePhotoRecordDao.queryForAll();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return mScanRecordList;
    }

    /*
    // 按照时间倒叙将数据库中的数据取出
    public List<ScanRecord> getALLRecordOrderByTime() 
    {
        List<ScanRecord> mList = query();
        List<ScanRecord> mNewList = null;
        mNewList = new ArrayList<ScanRecord>();
        if(mList != null)
        {
            for(int i = mList.size()-1; i >=0; i--)
            {
                mNewList.add(mList.get(i));
            }
        }
        return mNewList;
    }
    
    public List<NoteBookInfo> getAllNoteBooks()
    {
        List<ScanRecord> rawList = getALLRecordOrderByTime();
        for (int i = 0; i < rawList.size(); i++)
        {
            if (rawList.get(i).getRecType().equals("notebook"))
            {
                NoteBookInfo notebook = new NoteBookInfo();
                notebook.setNoteBookName(rawList.get(i).getNoteBookName());
                notebook.setNoteBookCreateTime(rawList.get(i).getCreateTime());
                notebook.setNoteBookId(rawList.get(i).getId());
                mNoteBookList.add(notebook);
            }    
        }
        
        return mNoteBookList;
        
    }

    public List<NoteInfo> getNotesByNoteBookName(String noteBookName)
    {
    	List<ScanRecord> rawList = getALLRecordOrderByTime();
    	for (int i = 0; i < rawList.size(); i++)
        {
            if (rawList.get(i).getRecType().equals("note")
            	&& 	rawList.get(i).getNoteBookName().equals(noteBookName))
            {
                NoteInfo note = new NoteInfo();
                note.setNoteId(rawList.get(i).getId());
                note.setNoteBookName(rawList.get(i).getNoteBookName());
                note.setNoteCreateTime(rawList.get(i).getCreateTime());
                note.setNoteCreateAddr(rawList.get(i).getCreateAddr());
                note.setNoteCotent(rawList.get(i).getNoteContent());
                note.setNoteTitle(rawList.get(i).getNoteTitle());
                note.setNotePhotoUrl(rawList.get(i).getPhotos());
                note.setNoteIsShared(rawList.get(i).getIsUpdateBaidu());
                note.setNoteIsUpload(rawList.get(i).getIsUpdateHanvon());
                note.setNoteInputType(rawList.get(i).getInputType());
                mNoteList.add(note);
            }    
        }
    	return mNoteList;
    }
    */

}
