package com.hanvon.sulupen.db.bean;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "notephoto_table")
public class NotePhotoRecord //implements Parcelable
{
    //数据库中每条记录的id
	@DatabaseField(generatedId = true,dataType=DataType.INTEGER)
	private int id;
	
	//图片的本地url
	@DatabaseField(columnName = "photoLocalUrl",dataType=DataType.STRING)
    private String photoLocalUrl;
	
	//图片的Web url
    @DatabaseField(columnName = "photoWebUrl",dataType=DataType.STRING)
    private String photoWebUrl;
    
    //关联笔记表的外键
    @DatabaseField(canBeNull = true, foreign = true, columnName = "noteID")
	private NoteRecord noteRecord;
	
    /*
    public static final Parcelable.Creator<ScanRecord> CREATOR = new Creator()
	{
	    @Override  
	    public NotePhotoRecord createFromParcel(Parcel source) 
	    {  
	        // TODO Auto-generated method stub  
	        // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错  
	    	NotePhotoRecord mNotePhotoRecord = new NotePhotoRecord(); 
	        
	        mNotePhotoRecord.setId(source.readInt());
	        mNotePhotoRecord.setLocalUrl(source.readString());
	        mNotePhotoRecord.setWebUrl(source.readString());
		    
		    
		
		    return mNotePhotoRecord;  
	    } 
		        
	    @Override  
		public NotePhotoRecord[] newArray(int size) 
	    {  
	        // TODO Auto-generated method stub  
	        return new NotePhotoRecord[size];  
	    }  

	};
	
	*/
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getLocalUrl() {
		return photoLocalUrl;
	}

	public void setLocalUrl(String local) {
		this.photoLocalUrl = local;
	}
	
	public String getWebUrl() {
		return photoWebUrl;
	}

	public void setWebUrl(String web) {
		this.photoWebUrl = web;
	}
	
	public NoteRecord getNoteId()
	{
		return this.noteRecord;
	}
	
	public void setNoteId(NoteRecord rec)
	{
		this.noteRecord = rec;
	}
	
    
    public NotePhotoRecord() {
		super();
	}

    /*
	public NotePhotoRecord(int id, String localurl, String weburl) 
	{
		super();
		this.id = id;
		this.photoLocalUrl = localurl;
		this.photoWebUrl = weburl;
	}
	*/

	@Override
	public String toString() 
	{
		return "NotePhotoRecord [id = " + id + ", localUrl = " + photoLocalUrl + ", photoWebUrl = " + photoWebUrl; 
	}
	
	/*
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		 dest.writeInt(id);
		 dest.writeString(photoLocalUrl);
		 dest.writeString(photoWebUrl);
	}
	*/
}
