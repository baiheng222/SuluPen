package com.hanvon.sulupen;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.sulupen.adapter.NoteListAdapter;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;

public class NoteBookListActivity extends Activity implements OnClickListener
{
	private final String TAG = "NoteBookListActivity";
	
	private String mNoteBookName = "NoteBook";
	
	private TextView mBack;
	private TextView mTitle;
	private TextView mRightBtn;
	private TextView mEmptyNoteTip;
	private EditText mInput;
	private ImageView mNewNote;
	private ListView mLvNoteList;
	
	NoteRecordDao mNoteRecordDao;
	NoteListAdapter mNoteListAdapter;
	
	List<NoteRecord> mNoteRecordList;
	
	NoteBookRecord mPassedNoteBook;
	
	 
	public final static int FLAG_EDIT = 1;
	public final static int FLAG_CREATE_WITH_BOOKNAME = 2;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.notebook_list);    
		
		initDatas();
		
		initViews();
	        
	}

	
	private void initDatas()
    {
        Intent intent = getIntent();
        if (intent != null) 
        {
             
            mPassedNoteBook = (NoteBookRecord) intent.getSerializableExtra("NoteBook");
            mNoteBookName = mPassedNoteBook.getNoteBookName();
        }
        else
        {
            mNoteBookName = "NoteBook";
        }
        
        mPassedNoteBook.toString();
        
        mNoteRecordDao = new NoteRecordDao(this);
        //mNoteRecordList = mNoteRecordDao.getAllNoteRecords();
        mNoteRecordList = mNoteRecordDao.getNoteRecordsByNoteBookId(mPassedNoteBook.getId());
        Log.d(TAG, "notes num is " +  mNoteRecordList.size());
        
    }
	
	 private void initViews()
	 {
		 mBack = (TextView) findViewById(R.id.tv_backbtn);
		 mTitle = (TextView) findViewById(R.id.tv_title);
		 mRightBtn = (TextView) findViewById(R.id.tv_rightbtn);
		 mInput = (EditText) findViewById(R.id.ed_search_input);
		 mNewNote = (ImageView) findViewById(R.id.iv_newnote);
		 mLvNoteList = (ListView) findViewById(R.id.lv_notelist);
		 mEmptyNoteTip = (TextView) findViewById(R.id.tv_showemptynote);
		 
		 mTitle.setText(mNoteBookName);
		 
		 mBack.setOnClickListener(this);
		 mRightBtn.setOnClickListener(this);
		 mNewNote.setOnClickListener(this);
		 
		 if (mNoteRecordList.size() > 0)
		 {
		     mRightBtn.setVisibility(View.VISIBLE);
		     mLvNoteList.setVisibility(View.VISIBLE);
		     mEmptyNoteTip.setVisibility(View.GONE);
		     setNoteListAdapter();
		     
		 }
		 else
		 {
		     mRightBtn.setVisibility(View.GONE);
		     mLvNoteList.setVisibility(View.GONE);
             mEmptyNoteTip.setVisibility(View.VISIBLE);
		 }
	 }
	 
	 @Override
	 public void onClick(View view)
	 {
		 switch (view.getId())
		 {
		 	case R.id.tv_backbtn:
		 	    
		 	    finish();
                
            break;
            
		 	case R.id.tv_rightbtn:
		 	    
		 	break;
                     
            case R.id.iv_newnote:
            	Intent newNoteIntent = new Intent(this, NoteDetailActivity.class);
                newNoteIntent.setFlags(FLAG_CREATE_WITH_BOOKNAME);
                newNoteIntent.putExtra("NoteBook", mPassedNoteBook);
                startActivity(newNoteIntent);
            break;     
		 }
	 }
	 
	 @Override
	 protected void onResume()
	 {
	     super.onResume();
	     setNoteListAdapter();
	     mNoteListAdapter.notifyDataSetChanged();
	 }
	 
	 public void setNoteListAdapter()
	 {
		 mNoteRecordList = mNoteRecordDao.getNoteRecordsByNoteBookId(mPassedNoteBook.getId());
	     mNoteListAdapter = new NoteListAdapter(this, mNoteRecordList);
         mLvNoteList.setAdapter(mNoteListAdapter);
	 }
}
