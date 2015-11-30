package com.hanvon.sulupen.db.bean;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "notebook_table")
public class NoteBookRecord implements Serializable
{
    //数据库中每条记录的id
	@DatabaseField(generatedId = true,dataType=DataType.INTEGER)
	private int id;
	
	//笔记本ID,生成之后就不会再改变，是一个唯一的值，除非删除了这个笔记本，这个ID可以作为NoteRecord的外键
	//使用UUID随机生成的ID,保证在任何时候都是唯一的
	@DatabaseField(columnName = "noteBookId",dataType=DataType.STRING)
    private String noteBookId;
	
	//笔记本名称，笔记本名称是可以被修改的，虽然笔记本名称不会有两个同样的名称存在，
    @DatabaseField(columnName = "noteBookName",dataType=DataType.STRING)
    private String noteBookName;

	//笔记本是否已经上传， 0 已经上传， 1 未上传
	@DatabaseField(columnName = "noteBookUpLoad",dataType=DataType.INTEGER)
	private int noteBookUpLoad;

	//笔记本是否要删除， 用于与服务端同步后进行删除, 0 未删除， 1 删除
	@DatabaseField(columnName = "noteBookDelete",dataType=DataType.INTEGER)
	private int noteBookDelete;
    
    @ForeignCollectionField
    private Collection<NoteRecord> noteRecords;

    public Collection<NoteRecord> getNoteRecords()
    {
        return noteRecords;
    }

    public void setArticles(Collection<NoteRecord> notes)
    {
        this.noteRecords = notes;
    }
	
    public ArrayList<NoteRecord> getNoteRecordList()
    {
    	ArrayList<NoteRecord> mNotesList = new ArrayList<NoteRecord>(noteRecords);
    	return mNotesList;
    }
    
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

	public String getNoteBookId()
	{
		return noteBookId;
	}

	public void setNoteBookId(String noteBookId)
	{
		this.noteBookId = noteBookId;
	}

	public String getNoteBookName()
    {
        return noteBookName;
    }

    public void setNoteBookName(String name) 
    {
        this.noteBookName = name;
    }

	public int getNoteBookUpLoad()
	{
		return noteBookUpLoad;
	}

	public void setNoteBookUpLoad(int noteBookUpLoad)
	{
		this.noteBookUpLoad = noteBookUpLoad;
	}

	public int getNoteBookDelete()
	{
		return noteBookDelete;
	}

	public void setNoteBookDelete(int noteBookDelete)
	{
		this.noteBookDelete = noteBookDelete;
	}

	public NoteBookRecord()
	{
		super();
	}

	public NoteBookRecord(int id, String notebookid, String noteBookName)
	{
		super();
		this.id = id;
		this.noteBookId = notebookid;
		this.noteBookName = noteBookName;
	}

	@Override
	public String toString() 
	{
		return "NoteBookRecord [id = " + id + ", noteBookId = " + noteBookId +
				", noteBookName = " + noteBookName + ", noteBookUpLoad = " + noteBookUpLoad +
				", noteBookDelete = " + noteBookDelete;
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
