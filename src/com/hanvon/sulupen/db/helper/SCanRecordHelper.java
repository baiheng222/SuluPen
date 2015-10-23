package com.hanvon.sulupen.db.helper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.hanvon.sulupen.db.bean.*;

public class SCanRecordHelper extends OrmLiteSqliteOpenHelper 
{
	private final static String DATABASENAME = "hanvon2.db";
	private final static int DATABASEVERSION = 1;
	private Dao<ScanRecord, Integer> mDao = null;
	private static SCanRecordHelper instance;
	private Map<String, Dao> daos = new HashMap<String, Dao>();
	public SCanRecordHelper(Context context) 
	{
		super(context, DATABASENAME, null, DATABASEVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase mSqliteDatabase, ConnectionSource mConnectionSource) 
	{
		try 
		{
			TableUtils.createTable(mConnectionSource, NoteBookRecord.class);
			TableUtils.createTable(mConnectionSource, NoteRecord.class);
			TableUtils.createTable(mConnectionSource, NotePhotoRecord.class);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	

	/**
	 * 这个方法在你的应用升级以及它有一个更高的版本号时调用。所以需要你调整各种数据来适应新的版本
	 * */
	@Override
	public void onUpgrade(SQLiteDatabase mSqliteDatabase,
			ConnectionSource mConnectionSource, int oldVersion, int newVersion) 
	{
		// 先删除就得版本，再创建新的版本
		try 
		{
			TableUtils.dropTable(mConnectionSource, NoteBookRecord.class, true);
			TableUtils.dropTable(mConnectionSource, NoteRecord.class, true);
			TableUtils.dropTable(mConnectionSource, NotePhotoRecord.class, true);
			onCreate(mSqliteDatabase, mConnectionSource);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 单例获取该Helper
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized SCanRecordHelper getHelper(Context context)
	{
		context = context.getApplicationContext();
		if (instance == null)
		{
			synchronized (SCanRecordHelper.class)
			{
				if (instance == null)
					instance = new SCanRecordHelper(context);
			}
		}
		return instance;
	}

	public synchronized Dao getDao(Class clazz) throws SQLException
	{
		Dao dao = null;
		String className = clazz.getSimpleName();

		if (daos.containsKey(className))
		{
			dao = daos.get(className);
		}
		if (dao == null)
		{
			dao = super.getDao(clazz);
			daos.put(className, dao);
		}
		return dao;
	}

	/**
	 * 释放资源
	 */
	@Override
	public void close()
	{
		super.close();

		for (String key : daos.keySet())
		{
			Dao dao = daos.get(key);
			dao = null;
		}
	}
}
