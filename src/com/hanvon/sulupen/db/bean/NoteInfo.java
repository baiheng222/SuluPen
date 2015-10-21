package com.hanvon.sulupen.db.bean;

public class NoteInfo 
{
	private int mNoteIdInDb;
	private String mNoteBookName;
	private String mNoteCreateTime;
	private String mNoteCreateAddr;
	private String mNoteContent;
	private String mNoteTitle;
	private String mNotePhotoUrl;
	private int mNoteIsShared;
	private int mNoteIsUpload;

	public NoteInfo()
	{
		
	}
	
	public void setNoteId(int id)
	{
		mNoteIdInDb = id;
	}
	
	public int getNoteId()
	{
		return mNoteIdInDb;
	}
	
	public void setNoteBookName(String name)
	{
		mNoteBookName = name;
	}
	
	public String getNoteBookName()
	{
		return mNoteBookName;
	}
	
	public void setNoteCreateTime(String time)
	{
		mNoteCreateTime = time;
	}
	
	public String getNoteCreateTime()
	{
		return mNoteCreateTime;
	}
	
	public void setNoteCreateAddr(String addr)
	{
		mNoteCreateAddr = addr;
	}
	
	public String getNoteCreateAddr()
	{
		return mNoteCreateAddr;
	}
	
	public void setNoteCotent(String content)
	{
		mNoteContent = content;
	}
	
	public String getNoteContent()
	{
		return mNoteContent;
	}
	
	public void setNoteTitle(String title)
	{
		mNoteTitle = title;
	}
	
	public String getNoteTitle()
	{
		return mNoteTitle;
	}
	
	public void setNotePhotoUrl(String url)
	{
		mNotePhotoUrl = url;
	}
	
	public String getNotePhotourl()
	{
		return mNotePhotoUrl;
	}
	
	public void setNoteIsShared(int flag)
	{
		mNoteIsShared = flag;
	}
	
	public int getNoteIsShared()
	{
		return mNoteIsShared;
	}
	
	public void setNoteIsUpload(int flag)
	{
		mNoteIsUpload = flag;
	}
	
	public int getNoteIsUpload()
	{
		return mNoteIsUpload;
	}
}
