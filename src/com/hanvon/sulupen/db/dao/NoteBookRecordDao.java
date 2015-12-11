package com.hanvon.sulupen.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//import com.hanvon.sulupen.db.bean.NoteBookInfo;
//import com.hanvon.sulupen.db.bean.NoteInfo;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;

import com.hanvon.sulupen.db.helper.SCanRecordHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;

import android.content.Context;
import android.util.Log;
import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.db.bean.*;

public class NoteBookRecordDao 
{
	private static String TAG = "NoteBookRecordDao";
	private SCanRecordHelper mHelper;
	private Dao<NoteBookRecord, Integer> mNoteBookRecordDao;
	private List<NoteBookRecord> mNoteBookRecordList;
	
	private int mUpdateCount;
	
	
	private static NoteBookRecordDao instance = null;
	
	public static NoteBookRecordDao getScanRecordDaoInstance()
	{
	    return instance;
	}

	public NoteBookRecordDao(Context context) 
	{
		try 
		{
			mHelper = SCanRecordHelper.getHelper(context);
			mNoteBookRecordDao = mHelper.getDao(NoteBookRecord.class);
			instance = this;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		//getDayInfo();
	}

	// 保存一条数据
	public void add(NoteBookRecord article) 
	{
		try 
		{
			mNoteBookRecordDao.create(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//删除一条记录
	public void deleteRecord(NoteBookRecord article)
	{
		try 
		{
			mNoteBookRecordDao.delete(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}



	// 修改数据库里面的数据
	public int updataRecord(NoteBookRecord mScanRecord) 
	{
		try 
		{
			mUpdateCount = mNoteBookRecordDao.update(mScanRecord);
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
            mNoteBookRecordDao.delete(mNoteBookRecordList);
        } 
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	//获取全部笔记本的列表,包含有删除标记的
	public List<NoteBookRecord> getAllNoteBooksIncludeDeleted()
	{
		try
		{
			mNoteBookRecordList = mNoteBookRecordDao.queryForAll();
			//mNoteBookRecordList = mNoteBookRecordDao.queryBuilder().where().eq("noteBookDelete", 0).query();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return mNoteBookRecordList;
	}
	
	//获取全部笔记本的列表,不包含有删除标记的
    public List<NoteBookRecord> getAllNoteBooks() 
    {
        try 
        {
        	//mNoteBookRecordList = mNoteBookRecordDao.queryForAll();
			mNoteBookRecordList = mNoteBookRecordDao.queryBuilder().where().eq("noteBookDelete", 0).query();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return mNoteBookRecordList;
    }
    
    //根据笔记本id获取笔记全部信息
    public NoteBookRecord getNoteBookRecordById(int id)
    {
    	NoteBookRecord noteBookRecord = null;
    	try
    	{
    		noteBookRecord = mNoteBookRecordDao.queryForId(id);
    	}
    	catch (SQLException e)
    	{
    		e.printStackTrace();
    	}
    	
    	return noteBookRecord;
    }

	public NoteBookRecord geNoteBookRecordByNameAndID(String id, String name)
	{
		NoteBookRecord notebook = null;

		mNoteBookRecordList = getAllNoteBooks();


		for (int i = 0; i <mNoteBookRecordList.size(); i++)
		{
			if (mNoteBookRecordList.get(i).getNoteBookId().equals(id) &&
					mNoteBookRecordList.get(i).getNoteBookName().equals(name))
			{
				notebook = mNoteBookRecordList.get(i);
				break;
			}
		}

		return notebook;
	}

	public NoteBookRecord geNoteBookRecordByStringID(String id)
	{
		NoteBookRecord notebook = null;

		mNoteBookRecordList = getAllNoteBooks();

		for (int i = 0; i <mNoteBookRecordList.size(); i++)
		{
			if (mNoteBookRecordList.get(i).getNoteBookId().equals(id))
			{
				notebook = mNoteBookRecordList.get(i);
				break;
			}
		}

		return notebook;
	}

    public NoteBookRecord getNoteBookRecordByName(String name)
    {
        NoteBookRecord notebook = null;

		mNoteBookRecordList = getAllNoteBooks();
        
        for (int i = 0; i <mNoteBookRecordList.size(); i++)
        {
            if (mNoteBookRecordList.get(i).getNoteBookName().equals(name))
            {
                notebook = mNoteBookRecordList.get(i);
                break;
            }
        }
        
        return notebook;
    }

	//获取所有需要删除的笔记本的列表
	public List<NoteBookRecord> getAllNoteBooksNeedToDelete()
	{
		try
		{
			//mNoteBookRecordList = mNoteBookRecordDao.queryForAll();
			mNoteBookRecordList = mNoteBookRecordDao.queryBuilder().where().eq("noteBookDelete", 1).query();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return mNoteBookRecordList;
	}

	//获取所有需要上传的笔记本的列表
	public List<NoteBookRecord> getAllNoteBooksNeedToUpload()
	{
		try
		{
			//mNoteBookRecordList = mNoteBookRecordDao.queryForAll();
			mNoteBookRecordList = mNoteBookRecordDao.queryBuilder().where().ne("noteBookUpLoad", 1).and().eq("noteBookDelete", 0).query();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return mNoteBookRecordList;
	}

	//删除已经同步的需要删除的笔记本
	public void deleteSyncedNoteBooks(String notebookID)
	{
		List<NoteBookRecord> booklist = null;
		try
		{
			booklist = mNoteBookRecordDao.queryBuilder().where().eq("noteBookId", notebookID).and().eq("noteBookDelete", 1).query();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (null != booklist)
		{
			for (int i = 0; i < booklist.size(); i++)
			{
				if (booklist.get(i).getNoteBookDelete() == 1)
				{
					Log.d(TAG, "!!!!!!!!!!!!!!! delete notebook");
					deleteRecord(booklist.get(i));
				}
			}
		}
		else
		{
			Log.d(TAG, "Notebook not found !!!!!!!!!!!!!!!!1");
		}
	}

	public void deleteAllSyncedNoteBooks()
	{
		List<NoteBookRecord> list =  getAllNoteBooksNeedToDelete();
		if (null == list)
		{
			Log.d(TAG, "No notebook need to delete");
			return;
		}
		for (int i = 0; i < list.size(); i++)
		{
			deleteRecord(list.get(i));
		}
	}

}
