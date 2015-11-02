package com.hanvon.sulupen.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	
	private int mUpdateCount;
	
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

    /** 
     * 能获取到NotePhotoRecord图片集合，但是里面的NoteRecord只有id有值, 
     * @param id
     * @return 返回某一条笔记所包含的所有图片的集合
     */  
    public List<NotePhotoRecord> getNotePhotoRecordListByNoteId(int id)
    {
    	try
    	{
    		return mNotePhotoRecordDao.queryBuilder().where().eq("noteID", id).query();
    	}
    	catch (SQLException e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    	
    }

}
