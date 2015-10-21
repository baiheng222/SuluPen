package com.hanvon.sulupen.db.bean;

/*笔记本相关信息类*/

public class NoteBookInfo 
{
    private String mNoteBookName;
    private String mNoteBookCreateTime;
    private int mNoteBookIdInDb;
    
    public NoteBookInfo(String name, String time, int id)
    {
        mNoteBookName = name;
        mNoteBookCreateTime = time;
        mNoteBookIdInDb = id;
    }
    
    public NoteBookInfo()
    {
        
    }
    
    public String getNoteBookName()
    {
        return mNoteBookName;
    }
    
    public void setNoteBookName(String name)
    {
        mNoteBookName = name;
    }
    
    public String getNoteBookCreateTime()
    {
        return mNoteBookCreateTime;
    }
    
    public void setNoteBookCreateTime(String time)
    {
        mNoteBookCreateTime = time;
    }
    
    public int getNoteBookId()
    {
        return mNoteBookIdInDb;
    }

    public void setNoteBookId(int id)
    {
        mNoteBookIdInDb = id;
    }
}
