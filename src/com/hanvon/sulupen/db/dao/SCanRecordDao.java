package com.hanvon.sulupen.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import com.hanvon.inputmethod.db.bean.DataListBean;
import com.hanvon.sulupen.db.bean.ScanRecord;
//import com.hanvon.inputmethod.db.bean.YMDInfo;
import com.hanvon.sulupen.db.helper.SCanRecordHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;

import android.content.Context;
import android.util.Log;
import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.db.bean.*;

public class SCanRecordDao 
{
	private static String TAG = "SCanRecordDao";
	private SCanRecordHelper mHelper;
	private Dao<ScanRecord, Integer> mSCanRecordDao;
	private List<ScanRecord> mScanRecordList;
	private List<NoteBookInfo> mNoteBookList = new ArrayList<NoteBookInfo>();
	private List<NoteInfo> mNoteList = new ArrayList<NoteInfo>();
	private int mUpdateCount;
	//private List<DataListBean> mDayInfoList;
	//private List<YMDInfo> yearList=new ArrayList<YMDInfo>();
	// 每次加载 月份数
	public static final int KLOAD_LASTEST_MON = 3;
	
	private static SCanRecordDao instance = null;
	
	public static SCanRecordDao getScanRecordDaoInstance()
	{
	    return instance;
	}

	public SCanRecordDao(Context context) 
	{
		try 
		{
			mHelper = SCanRecordHelper.getHelper(context);
			mSCanRecordDao = mHelper.getDao(ScanRecord.class);
			instance = this;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		//getDayInfo();
	}

	// 保存一条数据
	public void add(ScanRecord article) 
	{
		try 
		{
			mSCanRecordDao.create(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//删除一条记录
	public void deleteRecord(ScanRecord article)
	{
		try 
		{
			mSCanRecordDao.delete(article);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}



	// 修改数据库里面的数据
	public int updataScanRecord(ScanRecord mScanRecord) 
	{
		try 
		{
			mUpdateCount = mSCanRecordDao.update(mScanRecord);
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
            mSCanRecordDao.delete(mScanRecordList);
        } 
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

	
	// 查询数据库里面所有的数据
    public List<ScanRecord> query() 
    {
        try 
        {
            mScanRecordList = mSCanRecordDao.queryForAll();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return mScanRecordList;
    }

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
                mNoteList.add(note);
            }    
        }
    	return mNoteList;
    }
    
	/**
	 * 获取年月记录统计数列表
	 */
	/*
	public List<YMDInfo> getAllYMDCount() 
	{
		List<YMDInfo> mYearList = new ArrayList<YMDInfo>();
		try 
		{
			GenericRawResults<YMDInfo> rawResults = mSCanRecordDao.queryRaw(
							"SELECT strftime('%Y',createTime),count(strftime('%Y',createTime)) FROM scanRecord_table GROUP BY strftime('%Y',createTime) ORDER BY strftime('%Y',createTime) DESC ",
							new RawRowMapper<YMDInfo>() 
							{
								@Override
								public YMDInfo mapRow(String[] columnNames,
										String[] resultColumns)
										throws SQLException {
									YMDInfo mYmdInfo = new YMDInfo();
									mYmdInfo.setDate(resultColumns[0]);
									mYmdInfo.setCount(resultColumns[1]);
									return mYmdInfo;
								}
							});
			
			Iterator<YMDInfo> iterator = rawResults.iterator();
			while (iterator.hasNext()) {
				YMDInfo mYmdInfo = iterator.next();
				mYearList.add(mYmdInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return mYearList;
	}
	*/

	/**
	 * 获取某年的所有月的记录数
	 * 
	 * */
	/*
	public  List<YMDInfo> getOneYearAllMCount(String year) {
		List<YMDInfo> mMonthList = new ArrayList<YMDInfo>();
		try {
			GenericRawResults<YMDInfo> rawResults = mSCanRecordDao
					.queryRaw(
							"SELECT strftime('%m',createTime),count(strftime('%Y-%m',createTime))FROM scanRecord_table WHERE strftime('%Y-%m',createTime) LIKE '"+year+"%' GROUP BY strftime('%Y-%m',createTime) ORDER BY strftime('%Y-%m',createTime) DESC",
							new RawRowMapper<YMDInfo>() {
								@Override
								public YMDInfo mapRow(String[] columnNames,
										String[] resultColumns)
										throws SQLException {
									YMDInfo mYmdInfo = new YMDInfo();
									mYmdInfo.setDate(resultColumns[0]);
									mYmdInfo.setCount(resultColumns[1]);
									return mYmdInfo;
								}
							});
			Iterator<YMDInfo> iterator = rawResults.iterator();
			
			while (iterator.hasNext()) {
				YMDInfo mYmdInfo = iterator.next();
				mMonthList.add(mYmdInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mMonthList;
	}
	*/

	/**
	 * 获取某标签的某年某月所有日记录统计数列表
	 * 
	 * */
	/*
	public ArrayList<YMDInfo> getOneY_MAllDayCount(String month,String topicId) {
		ArrayList<YMDInfo> mOneY_MAllDay=new ArrayList<YMDInfo>();
		try {
			//	"SELECT strftime('%Y-%m-%d',createTime),count(strftime('%Y-%m-%d',createTime)) FROM scanRecord_table WHERE strftime('%Y-%m-%d',createTime) LIKE '"+month+"%'GROUP BY strftime('%Y-%m-%d',createTime) ORDER BY strftime('%Y-%m-%d',createTime) DESC ",
						

			GenericRawResults<YMDInfo> rawResults = mSCanRecordDao
					.queryRaw(
							("".equals(topicId)?("SELECT strftime('%Y-%m-%d',createTime),count(strftime('%Y-%m-%d',createTime)) FROM scanRecord_table WHERE strftime('%Y-%m-%d',createTime) LIKE '"+month+"%' GROUP BY strftime('%Y-%m-%d',createTime) ORDER BY strftime('%Y-%m-%d',createTime) DESC "):("SELECT strftime('%Y-%m-%d',createTime),count(strftime('%Y-%m-%d',createTime)) FROM scanRecord_table WHERE strftime('%Y-%m-%d',createTime) LIKE '"+month+"%' AND topicId LIKE'"+topicId+"' GROUP BY strftime('%Y-%m-%d',createTime) ORDER BY strftime('%Y-%m-%d',createTime) DESC ")),
							new RawRowMapper<YMDInfo>() {
								@Override
								public YMDInfo mapRow(String[] columnNames,
										String[] resultColumns)
										throws SQLException {
									Log.i("SCanRecordDao", "getOneY_MAllDayCount resultColumns[0]=="+resultColumns[0]+"resultColumns[1]"+resultColumns[1]);
									YMDInfo ymdInfo = new YMDInfo();
									ymdInfo.setDate(resultColumns[0]);
									ymdInfo.setCount(resultColumns[1]);
									return ymdInfo;
								}
							});
			Iterator<YMDInfo> iterator = rawResults.iterator();
			while (iterator.hasNext()) {
				YMDInfo mYmdInfo = iterator.next();
				mOneY_MAllDay.add(mYmdInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.i("SCanRecordDao", "mOneY_MAllDay.size()"+mOneY_MAllDay.size());
		return mOneY_MAllDay;
	}
	*/
	
	// 查询所有数据的时间
	/*
	private List<YMDInfo> getAllYmdInfo() {
		try {
			GenericRawResults<YMDInfo> rawResults = mSCanRecordDao
					.queryRaw("SELECT distinct strftime('%Y-%m-%d',createTime) FROM scanRecord_table  ORDER BY strftime('%Y-%m-%d',createTime) DESC",
							new RawRowMapper<YMDInfo>() {
								@Override
								public YMDInfo mapRow(String[] columnNames,
										String[] resultColumns)
										throws SQLException {
									YMDInfo ymdInfo = new YMDInfo();
									ymdInfo.setDate(resultColumns[0]);
									return ymdInfo;
								}
							});
			List<YMDInfo> mList = new ArrayList<YMDInfo>();
			Iterator<YMDInfo> iterator = rawResults.iterator();
			while (iterator.hasNext()) {
				mList.add(iterator.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mList;
	}
	*/
	
	/*
	// 按照时间倒叙将数据库中的数据取出
		private List<ScanRecord> getALLRecordOrderByTime(String curTopicId) {
			List<ScanRecord> mList = query();
			List<ScanRecord> mNewList = null;
			mNewList = new ArrayList<ScanRecord>();
			if(mList != null){
			    for(int i = mList.size()-1; i >=0; i--){
			    	if(curTopicId.equals(mList.get(i).getTopicId())){
			    		mNewList.add(mList.get(i));
				    }
			    }
			}
			return mNewList;
		}
		
	*/
	
	/*
	//日期显示信息
	public List<DataListBean> getDayInfo(){
		mDayInfoList = new ArrayList<DataListBean>();
		int preSize=0;
		int recordSize=getALLRecordOrderByTime().size();
		if (recordSize > 0 && preSize <= recordSize) {
			for (int i = preSize; i < recordSize; i++) {
				if (preSize == 0 && i == 0) {
					DataListBean tag = new DataListBean();
					tag.setType(DataListBean.TYPE_CATEGORY);
					List<ScanRecord> tempList = getALLRecordOrderByTime();
					//Log.i(TAG, "tempList:"+tempList);
					ScanRecord tempScanRecord = tempList.get(i);
					//Log.i(TAG, "tempScanRecord:"+tempScanRecord);
					String timeStr = tempScanRecord.getCreateTime();
					//Log.i(TAG, "timeStr:"+timeStr);
					String category0 = "2099-09-29";
					if(timeStr!=null&&timeStr.length()>0){
						category0 = timeStr.substring(0, 10);
					}
					String category = category0;
					tag.setCategory(category);
					mDayInfoList.add(tag);
				} else {
					String creTimeI = getALLRecordOrderByTime().get(i).getCreateTime();
					String curCategory = (creTimeI!=null && creTimeI.length()>0)? creTimeI.substring(0, 10):"2099-09-29";
					String creTimeIm = getALLRecordOrderByTime().get(i - 1).getCreateTime();
					String preCategory = (creTimeIm!=null && creTimeIm.length()>0)? creTimeIm.substring(0, 10):"2099-09-29";
					if (!curCategory.equals(preCategory)) {
						DataListBean tag = new DataListBean();
						tag.setType(DataListBean.TYPE_CATEGORY);
						tag.setCategory(curCategory);
						mDayInfoList.add(tag);
					}
				}
				DataListBean bean = new DataListBean();
				bean.setType(DataListBean.TYPE_DATA);
				bean.setScanInfo(getALLRecordOrderByTime().get(i));
				mDayInfoList.add(bean);
			}
		}
		return mDayInfoList;
	}
	
	//
		public List<DataListBean> getDayInfo(String curTopicId){
			mDayInfoList = new ArrayList<DataListBean>();
			int preSize=0;
			List<ScanRecord> resultList = new ArrayList<ScanRecord>();
			resultList = getALLRecordOrderByTime(curTopicId);
			int recordSize=resultList.size();
			if (recordSize > 0 && preSize <= recordSize) {
				for (int i = preSize; i < recordSize; i++) {
					if (preSize == 0 && i == 0) {
						DataListBean tag = new DataListBean();
						tag.setType(DataListBean.TYPE_CATEGORY);
						String creTime = resultList.get(i).getCreateTime();
						String category = (creTime!=null&&creTime.length()>0) ? creTime.substring(0, 10):"2099-09-29";
						tag.setCategory(category);
						mDayInfoList.add(tag);
					} else {
						String creTimeI = resultList.get(i).getCreateTime();
						String curCategory = (creTimeI!=null && creTimeI.length()>0)? creTimeI.substring(0, 10):"2099-09-29";
						String creTimeIm = resultList.get(i - 1).getCreateTime();
						String preCategory = (creTimeIm!=null && creTimeIm.length()>0)? creTimeIm.substring(0, 10):"2099-09-29";
						if (!curCategory.equals(preCategory)) {
							DataListBean tag = new DataListBean();
							tag.setType(DataListBean.TYPE_CATEGORY);
							tag.setCategory(curCategory);
							mDayInfoList.add(tag);
						}
					}
					DataListBean bean = new DataListBean();
					bean.setType(DataListBean.TYPE_DATA);
					bean.setScanInfo(resultList.get(i));
					mDayInfoList.add(bean);
				}
			}
			return mDayInfoList;
		}
		//获取某一标题的date日期前的笔记信息
		public List<DataListBean> getDayInfo(String date,String curTopicId){
			mDayInfoList = new ArrayList<DataListBean>();
			int preSize=0;
			List<ScanRecord> resultList = new ArrayList<ScanRecord>();
			resultList = getRecordByTopicTillDay(date,curTopicId);
			int recordSize=resultList.size();
			if (recordSize > 0 && preSize <= recordSize) {
				for (int i = preSize; i < recordSize; i++) {
					if (preSize == 0 && i == 0) {
						DataListBean tag = new DataListBean();
						tag.setType(DataListBean.TYPE_CATEGORY);
						String category = resultList.get(i).getCreateTime().substring(0, 10);
						tag.setCategory(category);
						mDayInfoList.add(tag);
					} else {
						String curCategory = resultList.get(i).getCreateTime().substring(0, 10);
						String preCategory = resultList.get(i - 1).getCreateTime().substring(0, 10);
						if (!curCategory.equals(preCategory)) {
							DataListBean tag = new DataListBean();
							tag.setType(DataListBean.TYPE_CATEGORY);
							tag.setCategory(curCategory);
							mDayInfoList.add(tag);
						}
					}
					DataListBean bean = new DataListBean();
					bean.setType(DataListBean.TYPE_DATA);
					bean.setScanInfo(resultList.get(i));
					mDayInfoList.add(bean);
				}
			}
			return mDayInfoList;
		}
	//根据主题名称查找某年某月的主题数
	public List<YMDInfo> getCreateTimeByTopic(String TopicId,String year){
		List<YMDInfo> mTopicListById = new ArrayList<YMDInfo>();
		try {
			//
			GenericRawResults<YMDInfo> rawResults = mSCanRecordDao
					.queryRaw(
							"SELECT strftime('%Y',createTime),count('"+TopicId+"') FROM scanRecord_table  WHERE topicId ='"+TopicId+"' AND strftime('%Y',createTime)='"+year+"'",
							new RawRowMapper<YMDInfo>() {
								@Override
								public YMDInfo mapRow(String[] columnNames,
										String[] resultColumns)
										throws SQLException {
									YMDInfo mYMDInfo = new YMDInfo();									
									mYMDInfo.setDate(resultColumns[0]);
									mYMDInfo.setCount(resultColumns[1]);
								return mYMDInfo;
								}
							});
			Iterator<YMDInfo> iterator = rawResults.iterator();
			while (iterator.hasNext()) {
				mTopicListById.add(iterator.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mTopicListById;
	}
	
	//获取某年主题的分类
	public List<YMDInfo> getTopicNumber(String year){
		List<YMDInfo> mYearTopicList = new ArrayList<YMDInfo>();
		try {
			GenericRawResults<YMDInfo> rawResults = mSCanRecordDao
					.queryRaw(
							"SELECT DISTINCT topicId ,strftime('%Y',createTime)  FROM scanRecord_table WHERE strftime('%Y',createTime) LIKE '"+year+"%'",
							new RawRowMapper<YMDInfo>() {
								@Override
								public YMDInfo mapRow(String[] columnNames,
										String[] resultColumns)
										throws SQLException {
									YMDInfo mYMDInfo = new YMDInfo();
									mYMDInfo.setDate(resultColumns[1]);
									mYMDInfo.setCount(resultColumns[0]);
								return mYMDInfo;
								}
							});
			Iterator<YMDInfo> iterator = rawResults.iterator();
			while (iterator.hasNext()) {
				mYearTopicList.add(iterator.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mYearTopicList;
	}
	//根据日期获取主题id 
	public ArrayList<YMDInfo> getAllMouthInfo(){
		ArrayList<YMDInfo> mAllMouthList = new ArrayList<YMDInfo>();
		try {
			GenericRawResults<YMDInfo> rawResults = mSCanRecordDao
					.queryRaw(
							"SELECT strftime('%Y-%m-%d',createTime),count(strftime('%Y-%m',createTime)) FROM scanRecord_table GROUP BY strftime('%Y-%m',createTime) ORDER BY strftime('%Y-%m',createTime) DESC",
							new RawRowMapper<YMDInfo>() {
								@Override
								public YMDInfo mapRow(String[] columnNames,
										String[] resultColumns)
										throws SQLException {
									YMDInfo mYMDInfo = new YMDInfo();
									mYMDInfo.setDate(resultColumns[0]);
									mYMDInfo.setCount(resultColumns[1]);
								return mYMDInfo;
								}
							});
			Iterator<YMDInfo> iterator = rawResults.iterator();
			while (iterator.hasNext()) {
				mAllMouthList.add(iterator.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mAllMouthList;
	}
	
	*/
	/**
	 * 年数据列表
	 * 
	 * @return
	 */
	/*
	public  List<DataListBean> getYearListBeans() 
	{
	    Log.d("SCanRecordDap", "!!! getYearListBeans");
		 List<DataListBean> mYearlistBeans = new ArrayList<DataListBean>();
		if (yearList != null && yearList.size() == 0) 
		{
			yearList = getAllYMDCount();
		}
			int yearCount = yearList.size();
			Log.d("SCanRecordDap", "yearCount is " + yearCount);
			for (int i = 1; i <=yearCount; i++) 
			{
				DataListBean tag = new DataListBean();
				tag.setType(DataListBean.TYPE_CATEGORY);
				String category = yearList.get(i-1).getDate();
				Log.d("SCanRecordDap", "category is " + category);
				tag.setCategory(category);
				mYearlistBeans.add(tag);
				DataListBean bean = new DataListBean();
				bean.setType(DataListBean.TYPE_DATA);
				List<YMDInfo> monthList = getTopicNumber("2014");
				if (monthList != null) 
				{
					bean.setYmdInfos(monthList);
				}				
				mYearlistBeans.add(bean);
			}
		return mYearlistBeans;
	}
	*/
	/**获取月列表
	 * */
	/*
	public  List<DataListBean> getMonthListBeans(String TopicId){
		 List<DataListBean> mMonthlistBeans = new ArrayList<DataListBean>();
		 ArrayList<YMDInfo> monthList = new ArrayList<YMDInfo>();
		 int start = 0;
			if ((monthList != null && monthList.size() == 0)) {
				monthList = getAllMouthInfo();
			}
			ScanRecord srAccess = new ScanRecord();
			int monthCount = monthList.size();
			if (start >= monthCount) {
				return mMonthlistBeans;
			}
			if (start < monthCount
					&& (monthCount - start) > KLOAD_LASTEST_MON) {
				start += KLOAD_LASTEST_MON;
			} else if (start < monthCount
					&& (monthCount - start) <= KLOAD_LASTEST_MON) {
				start = monthCount;

			}
			for (int i = 0; i < start; i++) {
				DataListBean tag = new DataListBean();
				tag.setType(DataListBean.TYPE_CATEGORY);
				String category = monthList.get(i).getDate();
				tag.setCategory(category);
				mMonthlistBeans.add(tag);

				DataListBean bean = new DataListBean();
				bean.setType(DataListBean.TYPE_DATA);
				Log.i("SCanRecordDao", "getOneY_MAllDayCount(monthList.get(i).getDate()=="+monthList.get(i).getDate());
				List<YMDInfo> dayList = getOneY_MAllDayCount(monthList
						.get(i).getDate().split("-")[0]+"-"+monthList
						.get(i).getDate().split("-")[1],TopicId);
				Log.i("SCanRecordDao", "(dayList != null)=="+(dayList != null));
				if (dayList != null) {
					bean.setYmdInfos(dayList);
				}
				mMonthlistBeans.add(bean);
			}
		 return mMonthlistBeans;
	}
	//根据日期和topicid查找ScanRecord(包含当天的笔记)
	public List<ScanRecord> getRecordByTopicTillDay(String date,String topicId){
		ArrayList<ScanRecord> mRecordByTopicList=new ArrayList<ScanRecord>();
		try {
			//date = "2015-08-31";
//			topicId = "2ecb1d46-00e9-465a-a877-34c996732dd2";
			date = date.substring(0, 8)+(Integer.parseInt(date.substring(8))+1);
			Log.i("SCanRecordDao", "date:"+date);
			GenericRawResults<ScanRecord> rawResults = mSCanRecordDao
					.queryRaw(
							"select * from scanRecord_table where createTime < '" + date + "' and topicId like '%" + topicId + "%' ORDER BY strftime('%Y-%m-%d',createTime) DESC ",
							new RawRowMapper<ScanRecord>() { 
								@Override
								public ScanRecord mapRow(String[] columnNames,
										String[] resultColumns)
										throws SQLException {
									ScanRecord mScanRecord = new ScanRecord();
									mScanRecord.setAddrDetail(resultColumns[0]);
									mScanRecord.setContent(resultColumns[1]);
									mScanRecord.setCreateAddr(resultColumns[2]);
									mScanRecord.setCreateTime(resultColumns[3]);
									mScanRecord.setWeather(resultColumns[4]);
									mScanRecord.setType(resultColumns[5]);
									mScanRecord.setTopicName(resultColumns[6]);
									mScanRecord.setPhotos(resultColumns[7]);
									mScanRecord.setTitle(resultColumns[8]);
									mScanRecord.setTopicId(resultColumns[9]);
									mScanRecord.setIsUpdateHanvon(Integer.parseInt(resultColumns[10]));
									mScanRecord.setIsUpdateBaidu(Integer.parseInt(resultColumns[11]));
									mScanRecord.setVersion(Integer.parseInt(resultColumns[12]));
									mScanRecord.setId(Integer.parseInt(resultColumns[13]));
									for(int i=0;i<resultColumns.length;i++){
										Log.i("ScanRecordDao", "resultColumns[i]=="+resultColumns[i]+"====i==="+i);
									}
								return mScanRecord;
								}
							});
			Iterator<ScanRecord> iterator = rawResults.iterator();
			while (iterator.hasNext()) {
				mRecordByTopicList.add(iterator.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mRecordByTopicList;
		
	}
	*/

    }