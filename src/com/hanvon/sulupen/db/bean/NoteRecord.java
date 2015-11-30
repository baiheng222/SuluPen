package com.hanvon.sulupen.db.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "noteRecord_table")

public class NoteRecord implements Serializable	//Parcelable
{
    //数据库中每条记录的id
	@DatabaseField(generatedId = true,dataType=DataType.INTEGER)
	private int id;

	//表示笔记本的唯一性的ID，用UUID随机生成保证唯一性
	@DatabaseField(columnName = "noteID", dataType = DataType.INTEGER)
	private int noteID;
	
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
	
	
	//创建笔记时的详细地址信息
	@DatabaseField(columnName = "addrDetail", dataType = DataType.STRING)
	private String addrDetail;
	
	//0表示未上传到汉王云,1表示已上传到汉王云
	@DatabaseField(columnName = "isUpLoad", dataType = DataType.INTEGER)
	private int isUpLoad;

	//0表示未删除,1表示删除
	@DatabaseField(columnName = "isDelete", dataType = DataType.INTEGER)
	private int isDelete;
	
	//0表示键盘输入,1表示速录笔输入
	@DatabaseField(columnName = "inputType", dataType = DataType.INTEGER)
	private int inputType;
	
	//用于记录笔记的版本
	@DatabaseField(columnName = "version", dataType = DataType.INTEGER)
	private int version;
	
	
	//用于关联笔记本表的外键，笔记本的id
	@DatabaseField(canBeNull = true, foreign = true, columnName = "noteBookId", foreignAutoRefresh = true)
	private NoteBookRecord notebook;
	
	
	@ForeignCollectionField
    private Collection<NotePhotoRecord> notePhotos;
	
	public Collection<NotePhotoRecord> getNoteRecords()
	{
		return notePhotos;
	}

	public void setArticles(Collection<NotePhotoRecord> photos)
	 {
	     this.notePhotos = photos;
	 }
	  
	/*获取笔记所包含的所有图片的集合*/
	public ArrayList<NotePhotoRecord> getNotePhotoList()
	{
		ArrayList<NotePhotoRecord> mNotesList = new ArrayList<NotePhotoRecord>(notePhotos);
		return mNotesList;
	}
	
	
	/*
	public static final Parcelable.Creator<ScanRecord> CREATOR = new Creator()
	{
	    @Override  
	    public NoteRecord createFromParcel(Parcel source) 
	    {  
	        // TODO Auto-generated method stub  
	        // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错  
	        NoteRecord mNoteRecord = new NoteRecord(); 
	        
	        mNoteRecord.setId(source.readInt());  
		    mNoteRecord.setNoteBookName(source.readString());
		    mNoteRecord.setNoteTitle(source.readString());
		    mNoteRecord.setNoteContent(source.readString());
		    mNoteRecord.setCreateTime(source.readString());
		    mNoteRecord.setCreateAddr(source.readString());
		    mNoteRecord.setWeather(source.readString());
			mNoteRecord.setAddrDetail(source.readString());
			mNoteRecord.setIsUpdateHanvon(source.readInt());
			mNoteRecord.setInputType(source.readInt());
			mNoteRecord.setVersion(source.readInt());
		
		    return mNoteRecord;  
	    } 
		        
	    @Override  
		public NoteRecord[] newArray(int size) 
	    {  
	        // TODO Auto-generated method stub  
	        return new NoteRecord[size];  
	    }  

	};
	
	*/

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public String getAddrDetail() {
		return addrDetail;
	}

	public void setAddrDetail(String addrDetail) {
		this.addrDetail = addrDetail;
	}

	public int getUpLoad() {
		return isUpLoad;
	}

	public void setUpLoad(int isUpdateHanvon) {
		this.isUpLoad = isUpdateHanvon;
	}
	
	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputtype) {
		this.inputType = inputtype;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public NoteBookRecord getNoteBook()
	{
		return notebook;
	}
	
	public void setNoteBook(NoteBookRecord nb)
	{
		this.notebook = nb;
	}

	public int getNoteID()
	{
		return noteID;
	}

	public void setNoteID(int noteID)
	{
		this.noteID = noteID;
	}

	public int getIsDelete()
	{
		return isDelete;
	}

	public void setIsDelete(int isDelete)
	{
		this.isDelete = isDelete;
	}

	public NoteRecord() {
		super();
	}

	/*
	public NoteRecord(int id, String type, String noteBookName, String title, String content, String createTime,
			String createAddr, String weather, String photos, String addrDetail,
			int isUpdateBaidu, int isUpdateHanvon, int inputtype, int version) 
	{
		super();
		this.id = id;
		this.noteBookName = noteBookName;
		this.noteTitle = title;
		this.noteContent = content;
		this.createTime = createTime;
		this.createAddr = createAddr;
		this.weather = weather;
		this.addrDetail = addrDetail;
		this.isUpdateHanvon = isUpdateHanvon;
		this.inputType = inputtype;
		this.version = version;
	}
	*/

	@Override
	public String toString() 
	{
		return "ScanRecord [id = " + id + ", noteID = " + noteID + ", noteBookName = " + noteBookName +
				", title = " + noteTitle + ", content = " + noteContent + ", createTime = " + createTime +
				", createAddr = " + createAddr + ", weather = " + weather + ", addrDetail = " + addrDetail +
				", isUpLoad = " + isUpLoad + ", isDelete = " + isDelete +
				", inputType =" + inputType + ", version = " + version + "]";
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
		 dest.writeString(noteBookName);
		 dest.writeString(noteTitle);
		 dest.writeString(noteContent); 
		 dest.writeString(createTime); 
		 dest.writeString(createAddr); 
		 dest.writeString(weather); 
		 dest.writeString(addrDetail);
		 dest.writeInt(isUpdateHanvon);
		 dest.writeInt(inputType);
		 dest.writeInt(version);
	}
	*/

}
