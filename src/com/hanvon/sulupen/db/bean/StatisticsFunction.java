package com.hanvon.sulupen.db.bean;


import java.io.Serializable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "function_table")
public class StatisticsFunction implements Serializable
{
    //数据库中每条记录的id
	@DatabaseField(generatedId = true,dataType=DataType.INTEGER)
	private int id;

	@DatabaseField(columnName = "loginPage",dataType=DataType.STRING)
    private String loginpage;

    @DatabaseField(columnName = "mainPage",dataType=DataType.STRING)
    private String mainpage;

	@DatabaseField(columnName = "editNoteBookPage",dataType=DataType.STRING)
	private String editNoteBookPage;

	@DatabaseField(columnName = "settingPage",dataType=DataType.STRING)
	private String settingPage;
	
	@DatabaseField(columnName = "seleteNoteBookPage",dataType=DataType.STRING)
	private String seleteNoteBookPage;
	
	@DatabaseField(columnName = "editNoteRecodePage",dataType=DataType.STRING)
	private String editNoteRecodePage;
	
	@DatabaseField(columnName = "timeJson",dataType=DataType.STRING)
	private String timeJson;
	
	@DatabaseField(columnName = "createTime",dataType=DataType.STRING)
	private String createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLoginpage()
	{
		return loginpage;
	}

	public void setLoginpage(String loginpage)
	{
		this.loginpage = loginpage;
	}

	public String getMainpage()
    {
        return mainpage;
    }

    public void setMainpage(String mainpage) 
    {
        this.mainpage = mainpage;
    }

	public String getEditNoteBookPage()
	{
		return editNoteBookPage;
	}

	public void setEditNoteBookPage(String editNoteBookPage)
	{
		this.editNoteBookPage = editNoteBookPage;
	}

	public String getSettingPage()
	{
		return settingPage;
	}

	public void setSettingPage(String settingPage)
	{
		this.settingPage = settingPage;
	}
	
	public String getSeleteNoteBookPage()
	{
		return seleteNoteBookPage;
	}

	public void setSeleteNoteBookPage(String seleteNoteBookPage)
	{
		this.seleteNoteBookPage = seleteNoteBookPage;
	}
	
	public String getEditNoteRecodePage()
	{
		return editNoteRecodePage;
	}

	public void setEditNoteRecodePage(String editNoteRecodePage)
	{
		this.editNoteRecodePage = editNoteRecodePage;
	}
	
	public String geTtimeJson()
	{
		return timeJson;
	}

	public void setTimeJson(String timeJson)
	{
		this.timeJson = timeJson;
	}
	
	public String getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}

	public StatisticsFunction()
	{
		super();
	}

	public StatisticsFunction(int id,String loginpage, String mainpage,String editbook,String setting,String select,String editrecord,String time,String createtime )
	{
		super();
		this.id = id;
		this.loginpage = loginpage;
		this.mainpage = mainpage;
		this.editNoteBookPage = editbook;
		this.settingPage = setting;
		this.seleteNoteBookPage = select;
		this.editNoteRecodePage = editrecord;
		this.timeJson = time;
		this.createTime = createtime;
	}

/*
	public NoteBookRecord(int id, String notebookid, String noteBookName)
	{
		super();
		this.id = id;
		this.noteBookId = notebookid;
		this.noteBookName = noteBookName;
	}
*/
	@Override
	public String toString() 
	{
		return "";
	}

}

