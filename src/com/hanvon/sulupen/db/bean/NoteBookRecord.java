package com.hanvon.sulupen.db.bean;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "notebook_table")
public class NoteBookRecord //implements Parcelable
{
    //数据库中每条记录的id
	@DatabaseField(generatedId = true,dataType=DataType.INTEGER)
	private int id;
	
	//笔记本ID,生成之后就不会再改变，是一个唯一的值，除非删除了这个笔记本，这个ID可以作为NoteRecord的外键
	@DatabaseField(columnName = "noteBookId",dataType=DataType.INTEGER)
    private int noteBookId;
	
	//笔记本名称，笔记本名称是可以被修改的，虽然笔记本名称不会有两个同样的名称存在，
    @DatabaseField(columnName = "noteBookName",dataType=DataType.STRING)
    private String noteBookName;
	
    /*
    public static final Parcelable.Creator<ScanRecord> CREATOR = new Creator()
	{
	    @Override  
	    public NoteBookRecord createFromParcel(Parcel source) 
	    {  
	        // TODO Auto-generated method stub  
	        // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错  
	        NoteBookRecord mNoteBookRecord = new NoteBookRecord(); 
	        
	        mNoteBookRecord.setId(source.readInt());
	        mNoteBookRecord.setNoteBookId(source.readInt());
		    mNoteBookRecord.setNoteBookName(source.readString());
		    
		    
		
		    return mNoteBookRecord;  
	    } 
		        
	    @Override  
		public NoteBookRecord[] newArray(int size) 
	    {  
	        // TODO Auto-generated method stub  
	        return new NoteBookRecord[size];  
	    }  

	};
	*/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getNoteBookId() {
		return noteBookId;
	}

	public void setNoteBookId(int id) {
		this.noteBookId = id;
	}
	
	
    public String getNoteBookName() 
    {
        return noteBookName;
    }

    public void setNoteBookName(String name) 
    {
        this.noteBookName = name;
    }
    
    public NoteBookRecord() {
		super();
	}

	public NoteBookRecord(int id, int notebookid, String noteBookName) 
	{
		super();
		this.id = id;
		this.noteBookId = notebookid;
		this.noteBookName = noteBookName;
	}

	@Override
	public String toString() 
	{
		return "NoteBookRecord [id = " + id + ", noteBookId = " + noteBookId + ", noteBookName = " + noteBookName; 
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
		 dest.writeInt(noteBookId);
		 dest.writeString(noteBookName);
	}
	*/
}
