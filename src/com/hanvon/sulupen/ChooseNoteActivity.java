package com.hanvon.sulupen;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.sulupen.adapter.NoteChooseAdapter;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;

public class ChooseNoteActivity extends Activity implements OnClickListener
{
	private final String TAG = "ChooseNoteActivity";
	
	private ImageView mBack;
	private TextView mTitle;
	private TextView mCancelBtn;
	private ListView mNotesList;
	private ImageView mShareBtn;
	private ImageView mMoveNotesBtn;
	private ImageView mDeleteNoteBtn;
	
	private NoteRecordDao mNoteRecordDao;
	private List<NoteRecord> mNoteRecordList;
	private NoteBookRecord mPassedNoteBook;
	private NoteChooseAdapter mNotesAdapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notes_choice);
		
		initData();
		initView();
	}
	
	
	private void initView()
	{
		mBack = (ImageView) findViewById(R.id.tv_backbtn);
		mTitle = (TextView) findViewById(R.id.tv_title_choose);
		mCancelBtn = (TextView) findViewById(R.id.tv_cancel_btn);
		
		mShareBtn = (ImageView) findViewById(R.id.iv_share_note);
		mMoveNotesBtn = (ImageView) findViewById(R.id.iv_move_note);
		mDeleteNoteBtn = (ImageView) findViewById(R.id.iv_delete_note);
		
		
		mNotesList = (ListView) findViewById(R.id.lv_choose_notelist);
		
		mNotesAdapter = new NoteChooseAdapter(this, mNoteRecordList);
		mNotesList.setAdapter(mNotesAdapter);
		
		mBack.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		mShareBtn.setOnClickListener(this);
		mMoveNotesBtn.setOnClickListener(this);
		mDeleteNoteBtn.setOnClickListener(this);
	}
	
	private void initData()
	{
		Intent intent = getIntent();
        if (intent != null) 
        {
             
            mPassedNoteBook = (NoteBookRecord) intent.getSerializableExtra("NoteBook");
            //mNoteBookName = mPassedNoteBook.getNoteBookName();
        }
        else
        {
            //mNoteBookName = "NoteBook";
        }
        
        //mPassedNoteBook.toString();
        
        mNoteRecordDao = new NoteRecordDao(this);
        mNoteRecordList = mNoteRecordDao.getNoteRecordsByNoteBookId(mPassedNoteBook.getId());
        Log.d(TAG, "notes num is " +  mNoteRecordList.size());
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.tv_backbtn:
				finish();
			break;
			
			case R.id.tv_cancel_btn:
				finish();
			break;
			
			case R.id.iv_share_note:
				
			break;
			
			case R.id.iv_move_note:
				
			break;
			
			case R.id.iv_delete_note:
				deleteSelectedNote();
			break;
		}
	}
	
	private void deleteSelectedNote()
	{
		if (mNotesAdapter != null)
		{
			mNotesAdapter.delSelectedNotes();
		}
	}
	
}
