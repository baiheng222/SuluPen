package com.hanvon.sulupen.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "scanRecord_table")
public class ScanRecord implements Parcelable
{
    //数据库中每条记录的id
	@DatabaseField(generatedId = true,dataType=DataType.INTEGER)
	private int id;
	
	//数据库中记录所表示的类型，是笔记本名称还是一条笔记， 值（notebook/note)
	@DatabaseField(columnName = "recType",dataType=DataType.STRING)
	private String recType;
	
	//笔记本名称字段，当recType为笔记本时，表示笔记本名字；当为笔记的时候，表示这条笔记所属的笔记本的名字。
    @DatabaseField(columnName = "noteBookName",dataType=DataType.STRING)
    private String noteBookName;
	
    @DatabaseField(columnName = "noteTitle",dataType=DataType.STRING)
    private String noteTitle;
    
	@DatabaseField(columnName = "noteContent",dataType=DataType.STRING)
	private String noteContent;
	
	@DatabaseField(columnName = "createTime",dataType=DataType.STRING)
	private String createTime;
	
	@DatabaseField(columnName = "createAddr",dataType=DataType.STRING)
	private String createAddr;

	@DatabaseField(columnName = "weather",dataType=DataType.STRING)
	private String weather;
	
	@DatabaseField(columnName = "photos", dataType = DataType.STRING)
	private String photos;
	
	//创建笔记时的详细地址信息
	@DatabaseField(columnName = "addrDetail", dataType = DataType.STRING)
	private String addrDetail;
	
	//0表示未上传到百度云,1表示已上传到百度云
	@DatabaseField(columnName = "isUpdateBaidu", dataType = DataType.INTEGER)
	private int isUpdateBaidu;
	
	//0表示未上传到汉王云,1表示已上传到汉王云
	@DatabaseField(columnName = "isUpdateHanvon", dataType = DataType.INTEGER)
	private int isUpdateHanvon;
	
	//用于记录笔记的版本
	@DatabaseField(columnName = "version", dataType = DataType.INTEGER)
	private int version;
	
	public static final Parcelable.Creator<ScanRecord> CREATOR = new Creator()
	{
	    @Override  
	    public ScanRecord createFromParcel(Parcel source) 
	    {  
	        // TODO Auto-generated method stub  
	        // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错  
	        ScanRecord mScanRecord = new ScanRecord(); 
	        
	        mScanRecord.setId(source.readInt());  
		    mScanRecord.setRecType(source.readString());
		    mScanRecord.setNoteBookName(source.readString());
		    mScanRecord.setNoteTitle(source.readString());
		    mScanRecord.setNoteContent(source.readString());
		    mScanRecord.setCreateTime(source.readString());
		    mScanRecord.setCreateAddr(source.readString());
		    mScanRecord.setWeather(source.readString());
			mScanRecord.setPhotos(source.readString());
			mScanRecord.setAddrDetail(source.readString());
			mScanRecord.setIsUpdateBaidu(source.readInt());
			mScanRecord.setIsUpdateHanvon(source.readInt());
			mScanRecord.setVersion(source.readInt());
		
		    return mScanRecord;  
	    } 
		        
	    @Override  
		public ScanRecord[] newArray(int size) 
	    {  
	        // TODO Auto-generated method stub  
	        return new ScanRecord[size];  
	    }  

	};

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getRecType() 
	{
        return recType;
    }

    public void setRecType(String type) 
    {
        this.recType = type;
    }

    public String getNoteBookName() 
    {
        return noteBookName;
    }

    public void setNoteBookName(String name) 
    {
        this.noteBookName = name;
    }
    
	public String getNoteTitle() {
		return noteTitle;
	}

	public void setNoteTitle(String title) {
		this.noteTitle = title;
	}

	public String getNoteContent() {
		return noteContent;
	}

	public void setNoteContent(String content) {
		this.noteContent = content;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateAddr() {
		return createAddr;
	}

	public void setCreateAddr(String createAddr) {
		this.createAddr = createAddr;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getPhotos() {
		return photos;
	}
	public void setPhotos(String photos) {
		this.photos = photos;
	}	
	
	public String getAddrDetail() {
		return addrDetail;
	}

	public void setAddrDetail(String addrDetail) {
		this.addrDetail = addrDetail;
	}

	public int getIsUpdateBaidu() {
		return isUpdateBaidu;
	}

	public void setIsUpdateBaidu(int isUpdateBaidu) {
		this.isUpdateBaidu = isUpdateBaidu;
	}

	public int getIsUpdateHanvon() {
		return isUpdateHanvon;
	}

	public void setIsUpdateHanvon(int isUpdateHanvon) {
		this.isUpdateHanvon = isUpdateHanvon;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public ScanRecord() {
		super();
	}

	public ScanRecord(int id, String type, String noteBookName, String title, String content, String createTime,
			String createAddr, String weather, String photos, String addrDetail,
			int isUpdateBaidu, int isUpdateHanvon, int version) 
	{
		super();
		this.id = id;
		this.recType = type;
		this.noteBookName = noteBookName;
		this.noteTitle = title;
		this.noteContent = content;
		this.createTime = createTime;
		this.createAddr = createAddr;
		//this.type = type;
		//this.topicId = topicId;
		//this.topicName = topicName;
		this.weather = weather;
		this.photos = photos;
		this.addrDetail = addrDetail;
		this.isUpdateBaidu = isUpdateBaidu;
		this.isUpdateHanvon = isUpdateHanvon;
		this.version = version;
	}

	@Override
	public String toString() 
	{
		return "ScanRecord [id = " + id + ", type=" + recType + ", noteBookName = " + noteBookName + ", title = " + noteTitle +  
		        ", content = " + noteContent + ", createTime = " + createTime + ", createAddr = "
				+ createAddr + ", weather = " + weather + ", photos = " + photos + ", addrDetail = " + addrDetail
				+ ", isUpdateBaidu = " + isUpdateBaidu + ", isUpdateHanvon = " + isUpdateHanvon + ", version = " + version + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		 dest.writeInt(id);
		 dest.writeString(recType);
		 dest.writeString(noteBookName);
		 dest.writeString(noteTitle);
		 dest.writeString(noteContent); 
		 dest.writeString(createTime); 
		 dest.writeString(createAddr); 
		 dest.writeString(weather); 
		 dest.writeString(photos);
		 dest.writeString(addrDetail);
		 dest.writeInt(isUpdateBaidu);
		 dest.writeInt(isUpdateHanvon);
		 dest.writeInt(version);
	}
}

