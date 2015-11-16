package com.hanvon.sulupen;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.hanvon.sulupen.adapter.NoteListAdapter;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.utils.ClearEditText;

public class SearchActivity extends Activity implements OnClickListener
{

	private final String TAG = "NoteBookListActivity";
	
	private String mNoteBookName = "NoteBook";
	
	private ImageView mBack;
	private TextView mTitle;
	private TextView mRightBtn;
	private TextView mEmptyNoteTip;
	private ClearEditText mInput;
	private ImageView mNewNote;
	private ListView mLvNoteList;
	
	NoteRecordDao mNoteRecordDao;
	NoteListAdapter mNoteListAdapter;
	
	List<NoteRecord> mNoteRecordList;
	
	NoteBookRecord mPassedNoteBook;
	
	private int flagIntent = 0;
	
	 
	//public final static int FLAG_EDIT = 1;
	//public final static int FLAG_CREATE_WITH_BOOKNAME = 2;
	
	private final static int FLAG_CREATE = 2;
    private final static int FLAG_EDIT = 1;
    public final static int FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK = 3;
    private final static int FLAG_SEARCH = 7;
    private final static int FLAG_SEARCH_WITH_STRING = 8;
	
    String mSearchString;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);    
		
		initDatas();
		
		initViews();
	        
	}

	
	private void initDatas()
    {
		mNoteRecordDao = new NoteRecordDao(this);
		Intent intent = getIntent();
        if (intent != null) 
        {
        	//flagIntent = intent.getFlags();
        	flagIntent = intent.getIntExtra("SearchType", -1);
			if (flagIntent == FLAG_SEARCH_WITH_STRING)
			{
				mSearchString = (String) intent.getStringExtra("SearchString");
				Log.d(TAG, "get search string "  + mSearchString);
			}
			else if (flagIntent == FLAG_SEARCH)
			{
				
			}
				
        }
		
		/*
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
        */
    }
	
	 private void initViews()
	 {
		 mBack = (ImageView) findViewById(R.id.tv_backbtn);
		 mTitle = (TextView) findViewById(R.id.tv_title);
		 mRightBtn = (TextView) findViewById(R.id.tv_rightbtn);
		 mInput = (ClearEditText) findViewById(R.id.ced_search);
		 
		 mInput.setOnEditorActionListener(new OnEditorActionListener() 
		 { 
			 public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  
			 {                          

				 if (actionId==EditorInfo.IME_ACTION_SEND 
						 ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) 
				 {                
					 //do something;
					 Log.d(TAG, "search key pressed !!!!!!!!!!!!!!!!!");
					 setSearchResult(mInput.getText().toString());
					 return true;             
				 }               

				 return false;           
			 }       

		 });
		 
		 if (flagIntent == FLAG_SEARCH_WITH_STRING)
		 {
			 mInput.setText(mSearchString);
		 }
		 
		 mNewNote = (ImageView) findViewById(R.id.iv_newnote);
		 mLvNoteList = (ListView) findViewById(R.id.lv_notelist);
		 mEmptyNoteTip = (TextView) findViewById(R.id.tv_showemptynote);
		 
		 //mTitle.setText(mNoteBookName);
		 
		 mBack.setOnClickListener(this);
		 mRightBtn.setOnClickListener(this);
		 mNewNote.setOnClickListener(this);
		 
		 setNoteListAdapter();
         
         mLvNoteList.setOnItemClickListener(new OnItemClickListener()
         {
             @Override
             public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
             {
                 Log.d(TAG, "item " +  position + " clicked");
                 startNoteDetailActivity(position);
             }
         });
		 
		 if (mNoteRecordList.size() > 0)
		 {
		     mRightBtn.setVisibility(View.VISIBLE);
		     mLvNoteList.setVisibility(View.VISIBLE);
		     mEmptyNoteTip.setVisibility(View.GONE);
		     
		     
		 }
		 else
		 {
		     mRightBtn.setVisibility(View.GONE);
		     mLvNoteList.setVisibility(View.GONE);
             mEmptyNoteTip.setVisibility(View.VISIBLE);
		 }
	 }
	 
	 
	 public void startNoteDetailActivity(int pos)
	 {
		 Intent intent = new Intent(this, ScanNoteActivity.class);
		 String flagStr = Integer.toString(FLAG_EDIT);
		 intent.putExtra("CreatFlag", flagStr);
		 Log.d(TAG, "note is : " +  mNoteRecordList.get(pos).toString());
		 intent.putExtra("NoteRecordId", mNoteRecordList.get(pos).getId());
		 startActivity(intent);
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
		 		Intent intent = new Intent(this, ChooseNoteActivity.class);
		 		intent.putExtra("SearchType", FLAG_SEARCH);
		 		intent.putExtra("SearchString", mInput.getText().toString());
		 		startActivity(intent);
		 	break;
                     
            case R.id.iv_newnote:
            	Intent newNoteIntent = new Intent(this, ScanNoteActivity.class);
                String flagStr = Integer.toString(FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK);
                newNoteIntent.putExtra("CreatFlag", flagStr);
                startActivity(newNoteIntent);
            break;     
		 }
	 }
	 
	 @Override
	 protected void onResume()
	 {
	     super.onResume();
	     setAdapter();
	     if (mNoteRecordList.size() > 0)
         {
             mRightBtn.setVisibility(View.VISIBLE);
             mLvNoteList.setVisibility(View.VISIBLE);
             mEmptyNoteTip.setVisibility(View.GONE);
             
             
         }
         else
         {
             mRightBtn.setVisibility(View.GONE);
             mLvNoteList.setVisibility(View.GONE);
             mEmptyNoteTip.setVisibility(View.VISIBLE);
         }
	     mNoteListAdapter.notifyDataSetChanged();
	 }
	 
	 private void setAdapter()
	 {
		 String searchText = mInput.getText().toString();
		 if (searchText.length() < 1)
		 {
			 setNoteListAdapter();
		 }
		 else
		 {
			 setSearchResult(searchText); 
		 }
			 
	 }
	 
	 private void setSearchResult(String searchStr)
	 {
		mNoteRecordList = mNoteRecordDao.searchRecordsByString(searchStr);
		if (mNoteRecordList == null)
		{
			Log.d(TAG, "null pointer in search !!!!!!!!!!!!");
			return;
		}
		mNoteListAdapter = new NoteListAdapter(this, mNoteRecordList);
        mLvNoteList.setAdapter(mNoteListAdapter);
	 }
	 
	 public void setNoteListAdapter()
	 {
		 mNoteRecordList = mNoteRecordDao.getAllNoteRecords();
	     mNoteListAdapter = new NoteListAdapter(this, mNoteRecordList);
         mLvNoteList.setAdapter(mNoteListAdapter);
	 }
	
}
