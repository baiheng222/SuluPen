package com.hanvon.sulupen.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//import com.hanvon.sulupen.db.bean.NoteBookInfo;
//import com.hanvon.sulupen.db.bean.NoteInfo;
import com.hanvon.sulupen.db.bean.NoteBookRecord;

import com.hanvon.sulupen.db.helper.SCanRecordHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;


import android.content.Context;
import android.util.Log;
//import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.db.bean.*;

public class NoteRecordDao 
{
	private static String TAG = "NoteRecordDao";
	private SCanRecordHelper mHelper;
	private Dao<NoteRecord, Integer> mNoteRecordDao;
	private List<NoteRecord> mNoteRecordList;
	
	private int mUpdateCount;
	
	
	private static NoteRecordDao instance = null;
	
	public static NoteRecordDao getNoteRecordDaoInstance()
	{
	    return instance;
	}

	public NoteRecordDao(Context context) 
	{
		try 
		{
			mHelper = SCanRecordHelper.getHelper(context);
			mNoteRecordDao = mHelper.getDao(NoteRecord.class);
			instance = this;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		getAllNoteRecords();
	}

	// 保存一条数据
	public void add(NoteRecord article) 
	{
		try 
		{
			mNoteRecordDao.create(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//删除一条记录
	public void deleteRecord(NoteRecord article)
	{
		try 
		{
			mNoteRecordDao.delete(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}



	// 修改数据库里面的数据
	public int updataRecord(NoteRecord mScanRecord) 
	{
		try 
		{
			mUpdateCount = mNoteRecordDao.update(mScanRecord);
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
            mNoteRecordDao.delete(mNoteRecordList);
        } 
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        mNoteRecordList.clear();
        
    }
    

    public List<NoteRecord> searchRecordsByString(String seachStr)
    {
    	List<NoteRecord> searchRet = new ArrayList<NoteRecord>();
    	
    	try 
        {
        	mNoteRecordList = mNoteRecordDao.queryForAll();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    	
    	if (null != mNoteRecordList)
    	{
    		for (int i =0; i < mNoteRecordList.size(); i++)
    		{
    			if (mNoteRecordList.get(i).getNoteTitle().contains(seachStr) 
    				|| mNoteRecordList.get(i).getNoteContent().contains(seachStr))
    			{
    				searchRet.add(mNoteRecordList.get(i));
    			}
    		}
    	}
    	
    	return searchRet;
    }
    
	
	// 查询数据库里面所有的数据
    public List<NoteRecord> getAllNoteRecords() 
    {
        try 
        {
        	//mNoteRecordList = mNoteRecordDao.queryForAll();
			mNoteRecordList = mNoteRecordDao.queryBuilder().where().eq("isDelete", 0).query();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return mNoteRecordList;
    }
    
    public NoteRecord getNoteRecordById(int id)
    {
    	NoteRecord noteRecord = null;
    	try
    	{
    		noteRecord = mNoteRecordDao.queryForId(id);
    	}
    	catch (SQLException e)
    	{
    		e.printStackTrace();
    	}
    	
    	return noteRecord;
    }
    
    
    /** 
     * 能获取到NoteRecord图集合，但是里面的NoteBookRecord只有id有值, 
     * @param id
     * @return 返回某笔记本记所包含的所有笔记的集合
     */  
    public List<NoteRecord> getNoteRecordsByNoteBookId(int id)
    {
    	try
    	{
    		//return mNoteRecordDao.queryBuilder().where().eq("notebookID", id).query();
			return mNoteRecordDao.queryBuilder().where().eq("notebookID", id).and().eq("isDelete", 0).query();
    	}
    	catch (SQLException e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }

    /**
     * 通过Id得到一个笔记，这个笔记是附带着它所属的笔记本的信息的
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public NoteRecord getNoteRecordWithNoteBook(int id)
    {
        NoteRecord note = null;
        try
        {
            note = mNoteRecordDao.queryForId(id);
            mHelper.getDao(NoteBookRecord.class).refresh(note.getNoteBook());

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return note;
    }
    
}
