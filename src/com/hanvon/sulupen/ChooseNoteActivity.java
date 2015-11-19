package com.hanvon.sulupen;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.sulupen.adapter.NoteChooseAdapter;
import com.hanvon.sulupen.datas.ImageItem;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.utils.CustomConstants;

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
	private String mSearchString;
	
	private int flagIntent = 1;
	
	private final static int FLAG_CREATE = 2;
    private final static int FLAG_EDIT = 1;
    public final static int FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK = 3;
    private final static int UPLLOAD_FILE_CLOUD_SUCCESS = 5;
    private final static int UPLLOAD_FILE_CLOUD_FAIL = 6;
    private final static int FLAG_SEARCH = 7;
    private final static int RESULT_CHAGNE_NOTEBOOK_BATCH = 8;
	
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
		
		if (flagIntent == FLAG_SEARCH)
		{
			mMoveNotesBtn.setEnabled(false);
			mMoveNotesBtn.setVisibility(View.INVISIBLE);
		}
	}
	
	private void initData()
	{
		mNoteRecordDao = new NoteRecordDao(this);
		
		Intent intent = getIntent();
        if (intent != null) 
        {
        	flagIntent = intent.getIntExtra("SearchType", -1);
        	
			if (flagIntent == FLAG_SEARCH)
			{
				mSearchString = (String) intent.getStringExtra("SearchString");
				Log.d(TAG, "get search string "  + mSearchString);
				mNoteRecordList = mNoteRecordDao.searchRecordsByString(mSearchString);
			}
			else
			{
				mPassedNoteBook = (NoteBookRecord) intent.getSerializableExtra("NoteBook");
				mNoteRecordList = mNoteRecordDao.getNoteRecordsByNoteBookId(mPassedNoteBook.getId());
		        Log.d(TAG, "notes num is " +  mNoteRecordList.size());
			}
        }
		/*
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
        
        
        mNoteRecordList = mNoteRecordDao.getNoteRecordsByNoteBookId(mPassedNoteBook.getId());
        Log.d(TAG, "notes num is " +  mNoteRecordList.size());
        */
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
			    Intent changeIntent = new Intent(this, ChangNoteBookActivity.class);
			    changeIntent.putExtra("NoteBook", mPassedNoteBook);
			    startActivityForResult(changeIntent, RESULT_CHAGNE_NOTEBOOK_BATCH);
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
	
	private void batchChangeNoteBook(NoteBookRecord notebook)
	{
		if (mNotesAdapter != null)
		{
			mNotesAdapter.batchReNameNoteBook(notebook);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		    case  RESULT_CHAGNE_NOTEBOOK_BATCH:
		        if (RESULT_OK == resultCode)
		        {
		            //Log.d(TAG, "get change note book result");
		            NoteBookRecord notebook = (NoteBookRecord) data.getSerializableExtra("NewNoteBook");
		            Log.d(TAG, "get change note book result, notebook is " + notebook.toString());
		            batchChangeNoteBook(notebook);
		        }
		        else
		        {
		            Log.d(TAG, "receive reslut code is " + resultCode);
		        }
		    break;
			
		}
		
	}
	
}
