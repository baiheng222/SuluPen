package com.hanvon.sulupen.db.dao;

import java.sql.SQLException;
import java.util.List;


import com.hanvon.sulupen.db.helper.SCanRecordHelper;
import com.j256.ormlite.dao.Dao;
import android.content.Context;
import com.hanvon.sulupen.db.bean.*;

public class StatisticsFunctionDao 
{
	private SCanRecordHelper mHelper;
	private Dao<StatisticsFunction, Integer> mFunctionDao;
	private List<StatisticsFunction> mFunctionList;
	
	private int mUpdateCount;
	
	
	private static StatisticsFunctionDao instance = null;
	
	public static StatisticsFunctionDao getScanRecordDaoInstance()
	{
	    return instance;
	}

	public StatisticsFunctionDao(Context context) 
	{
		try 
		{
			mHelper = SCanRecordHelper.getHelper(context);
			mFunctionDao = mHelper.getDao(StatisticsFunction.class);
			instance = this;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	// 保存一条数据
	public void add(StatisticsFunction article) 
	{
		try 
		{
			mFunctionDao.create(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//删除一条记录
	public void deleteRecord(StatisticsFunction article)
	{
		try 
		{
			mFunctionDao.delete(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}



	// 修改数据库里面的数据
	public int updataRecord(StatisticsFunction mScanRecord) 
	{
		try 
		{
			mUpdateCount = mFunctionDao.update(mScanRecord);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return mUpdateCount;
	}
	
    //清空数据库
    public void deleteFunctionsDB() 
    {
    	getAllRecords();
        try 
        {
        	mFunctionDao.delete(mFunctionList);
        } 
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	
	public List<StatisticsFunction> getAllRecords()
	{
		try
		{
			mFunctionList = mFunctionDao.queryForAll();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return mFunctionList;
	}
}

