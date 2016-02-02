package com.hanvon.sulupen.sync;

import java.util.ArrayList;

public class DownFilesMsg {
    private String NoteBookId;
    private ArrayList<FileMsgInfo> filesInfoList;
    
    public void setNoteBookId(String noteId){
    	this.NoteBookId = noteId;
    }
    public String getNoteBookId(){
    	return NoteBookId;
    }
    
    public void setFileMsg(ArrayList<FileMsgInfo> filelist){
    	this.filesInfoList = filelist;
    }
    public ArrayList<FileMsgInfo> getFilesList(){
    	return filesInfoList;
    }
}
