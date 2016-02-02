package com.hanvon.sulupen.sync;

import java.util.List;
import java.util.UUID;

import com.hanvon.sulupen.utils.LogUtil;

import android.R.bool;

public class FileMsgInfo {
	private String fuid = "";
	private	String isDelete = "0";
	private	String modifyTime = "";
	private	boolean isDown = true;
	private	String noteBookId = "";
	private	String filePath = "";
	private	UUID noteRecordId;
	private	String title = "";

	public void setCloudId(String Id){
		this.fuid = Id;
	}
	public void setIsDelete(String flag){
		this.isDelete = flag;
	}
	public void setModifyTime(String time){
		this.modifyTime = time;
	}
	public void setIsDown(boolean flag){
		this.isDown = flag;
	}
	public void setNoteBookId(String Id){
		this.noteBookId = Id;
	}
	public void setFilePath(String path){
		this.filePath = path;
	}
	public void setNoteRecordId(UUID Id){
		this.noteRecordId = Id;
	}
	public void setTitle(String title){
		this.title = title;
	}
	
	
	public String getCloudId(){
		return fuid;
	}
	public String getIsDelete(){
		return isDelete;
	}
	public String getModifyTime(){
		return modifyTime;
	}
	public boolean getIsDown(){
		return isDown;
	}
	public String getNoteBookId(){
		return noteBookId;
	}
	public String getFilePath(){
		return filePath;
	}
	public UUID getNoteRecordId(){
		return noteRecordId;
	}
	public String getTitle(){
		return title;
	}
	
	public String toString(){
		 LogUtil.i("---noteBookId:"+noteBookId+"  noteRecordId:"+noteRecordId+"  fuid:"+fuid);
		 return null;
	}
}
