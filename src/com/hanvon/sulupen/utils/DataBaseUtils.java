package com.hanvon.sulupen.utils;

import android.content.Context;
import android.util.Log;

import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NotePhotoRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.bean.RecordInfo;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;
import com.hanvon.sulupen.db.dao.NoteRecordDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by fan on 2015/12/4.
 */
public class DataBaseUtils
{
    private NoteBookRecordDao mNoteBookRecordDao;
    private NoteRecordDao mNoteRecordDao;

    public DataBaseUtils(Context context)
    {
        mNoteRecordDao = new NoteRecordDao(context);
        mNoteBookRecordDao = new NoteBookRecordDao(context);
    }


    //获取所有的笔记本（非删除状态的）
    public List<NoteBookRecord> GetAllNoteBooks()
    {
        return mNoteBookRecordDao.getAllNoteBooks();
    }

    //获取所有需要删除的笔记本的列表
    public List<NoteBookRecord> GetAllNoteBooksNeedToDelete()
    {
        return mNoteBookRecordDao.getAllNoteBooksNeedToDelete();
    }

    //获取所有需要上传的笔记本的列表
    public List<NoteBookRecord> GetAllNoteBooksNeedToUpload()
    {
        return mNoteBookRecordDao.getAllNoteBooksNeedToUpload();
    }

    //删除已经同步的需要删除的笔记本
    public void DeleteSyncedNoteBooks(String notebookID)
    {
        mNoteBookRecordDao.deleteSyncedNoteBooks(notebookID);
    }

    public void DeleteAllSyncedNoteBooks()
    {
        mNoteBookRecordDao.deleteAllSyncedNoteBooks();
    }

    //获取需要删除的笔记的列表
    public List<NoteRecord> GetAllNotesToDelete()
    {
        return mNoteRecordDao.getAllNotesToDelete();
    }

    //获取需要上传的笔记的列表
    public List<NoteRecord> GetAllNotesToUpload()
    {
        return mNoteRecordDao.getAllNotesToUpload();
    }

    //获取笔记所包含的所有图片的集合
    public ArrayList<NotePhotoRecord> GetNotePhotoListByNoteID(String tagid, UUID contentid)
    {
        NoteRecord note = null;
        note = mNoteRecordDao.getNoteRecordByUUID(contentid);
        if (null != note)
        {
            return note.getNotePhotoList();
        }
        else
        {
            return null;
        }
    }

    public void DeleteNoteBookFromCloud(String id)
    {
        List<NoteBookRecord> list = mNoteBookRecordDao.getAllNoteBooks();
        if (null == list)
        {
            return ;
        }

        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getNoteBookId().equals(id))
            {
                mNoteBookRecordDao.deleteRecord(list.get(i));
            }
        }
    }

    //删除所有笔记（已经标记为删除）
    public void DeleteAllNoteRecords()
    {
        mNoteRecordDao.deleteAllSyncedNotes();
    }

    //删除指定id的笔记
    public void DeleteNoteRecordByUUId(UUID uuid)
    {
        mNoteRecordDao.deleteSyncedNoteByID(uuid);
    }

    //获取指定id和指定笔记本名字的记录与本地记录对比的状态
    //返回值：int
    //       0 , notebookid     notebookname 都相同    表示没有过更改
    //       1 , notebookid相同     notebookname 不同， 表示笔记本名称有更改
    //       2 , notebookid不同  notebookname 相同，    表示不同设备上有同名笔记本，需要在本地重明明
    //       3 , notebookid不同  notebookname 不同      表示本地不存在，需要新建笔记本
    public int GetNoteBookCompState(String notebookid, String notebookname)
    {
        NoteBookRecord notebook3 = mNoteBookRecordDao.geNoteBookRecordByNameAndID(notebookid, notebookname);
        NoteBookRecord notebook = mNoteBookRecordDao.getNoteBookRecordByName(notebookname);
        NoteBookRecord notebook2 = mNoteBookRecordDao.geNoteBookRecordByStringID(notebookid);

        if (null != notebook3)
        {
            return 0;
        }

        if (notebook == null && notebook2 == null)
        {
            return 3;
        }
        else if (notebook == null && notebook2 != null)
        {
            return 1;
        }
        else if (notebook != null && notebook2 == null)
        {
            return 2;
        }


        int ret = -1;

        if (notebook.getNoteBookId().equals(notebook2.getNoteBookId()))
        {
            if (notebook.getNoteBookName().equals(notebook2.getNoteBookName()))
            {
                ret = 0;
            }
            else
            {
                ret = 1;
            }
        }
        else
        {
            if (notebook.getNoteBookName().equals(notebook2.getNoteBookName()))
            {
                ret = 2;
            }
            else
            {
                ret = 3;
            }
        }

        return ret;
    }

    public List<RecordInfo> GetAllRecordsInfo()
    {
        List<RecordInfo> mInfos = new ArrayList<RecordInfo>();
        List<NoteRecord> notes = mNoteRecordDao.getAllNoteRecords();
        for (int i = 0; i < notes.size(); i++)
        {
            String crTiem = notes.get(i).getCreateTime();
            if (crTiem.isEmpty())
            {
                Log.i("DataBaseUtils", "create time string is null !!!!!!");
                continue;
            }
            Log.i("DataBaseUtils", "create time is  " + notes.get(i).getCreateTime());
            Date date = TimeUtil.getDate(notes.get(i).getCreateTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.i("DataBaseUtils", "day is " + calendar.get(Calendar.DAY_OF_MONTH));
            Log.i("DataBaseUtils", "month is " + calendar.get(Calendar.MONTH));
            Log.i("DataBaseUtils", "year is " + calendar.get(Calendar.YEAR));
            RecordInfo tmp = new RecordInfo();
            tmp.setRecordId(notes.get(i).getNoteID());
            tmp.setDay(calendar.get(Calendar.DAY_OF_MONTH));
            tmp.setMonth(calendar.get(Calendar.MONTH));
            tmp.setYear(calendar.get(Calendar.YEAR));
            tmp.setNoteBookId(notes.get(i).getNoteBook().getNoteBookId());
            mInfos.add(tmp);

            Log.i("DataBaseUtils", "date info is -- " + tmp.toString());
        }
        return mInfos;
    }

    //删除指定id的笔记本
    //参数：String      -----要删除的笔记本的id
    public void DeleteNoteBookByID(String id)
    {
        mNoteBookRecordDao.deleteSyncedNoteBooks(id);
        return;
    }

    //根据id跟新笔记本名称
    public void UpdateNoteBookNameByID(String id, String notebookname)
    {
        NoteBookRecord notebook = null;
        notebook = mNoteBookRecordDao.geNoteBookRecordByStringID(id);
        if (null != notebook)
        {
            notebook.setNoteBookName(notebookname);
            mNoteBookRecordDao.updataRecord(notebook);
        }
    }


    //根据笔记本名称查询笔记本
    //参数：   String name  --------笔记本名称
    //返回值：  null ----笔记本不存在，
    //          或者NoteBookRecord对象，笔记本存在
    public NoteBookRecord GetNoteBookByName(String name)
    {
        return mNoteBookRecordDao.getNoteBookRecordByName(name);
    }

    //更新笔记本的上传状态
    public void UpdateNoteBookState(String id, int upload)
    {
        NoteBookRecord notebook = null;
        notebook = mNoteBookRecordDao.geNoteBookRecordByStringID(id);
        if (null != notebook)
        {
            notebook.setNoteBookUpLoad(upload);
            mNoteBookRecordDao.updataRecord(notebook);

        }
    }

    //更新笔记的上传状态
    public void UpdateNoteState(UUID id, int upload)
    {
        NoteRecord note = mNoteRecordDao.getNoteRecordByUUID(id);
        if (null != note)
        {
            note.setUpLoad(upload);
            mNoteRecordDao.updataRecord(note);
        }
    }
}
