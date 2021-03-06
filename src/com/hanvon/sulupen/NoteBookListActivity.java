package com.hanvon.sulupen;

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

public class NoteBookListActivity extends Activity implements OnClickListener
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
	
	 
	//public final static int FLAG_EDIT = 1;
	//public final static int FLAG_CREATE_WITH_BOOKNAME = 2;
	
	private final static int FLAG_CREATE = 2;
    private final static int FLAG_EDIT = 1;
    private final static int FLAG_SEARCH_WITH_STRING = 8;
	

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
		 mBack = (ImageView) findViewById(R.id.tv_backbtn);
		 mTitle = (TextView) findViewById(R.id.tv_title);
		 mRightBtn = (TextView) findViewById(R.id.tv_rightbtn);
		 mInput = (ClearEditText) findViewById(R.id.ed_search_input);
		 
		 mInput.setOnEditorActionListener(new OnEditorActionListener() 
		 { 
			 public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  
			 {                          

				 if (actionId == EditorInfo.IME_ACTION_SEND 
					|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
							event.getAction() == KeyEvent.ACTION_DOWN)) 
				 {                
					 //do something;
					 Log.d(TAG, "search key pressed !!!!!!!!!!!!!!!!!");
					 startSearchActivity(mInput.getText().toString());
					 return true;             
				 }               

				 return false;           
			 }       

		 });
		 
		 mNewNote = (ImageView) findViewById(R.id.iv_newnote);
		 mLvNoteList = (ListView) findViewById(R.id.lv_notelist);
		 mEmptyNoteTip = (TextView) findViewById(R.id.tv_showemptynote);
		 
		 mTitle.setText(mNoteBookName);
		 
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
	 
	 public void startSearchActivity(String searchStr)
	 {
		 Intent intent = new Intent(this, SearchActivity.class);
		 intent.putExtra("SearchType", FLAG_SEARCH_WITH_STRING);
		 intent.putExtra("SearchString", searchStr);
		 startActivity(intent);
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
		 		intent.putExtra("NoteBook", mPassedNoteBook);
		 		startActivity(intent);
		 	break;
                     
            case R.id.iv_newnote:
            	Intent newNoteIntent = new Intent(this, ScanNoteActivity.class);
                String flagStr = Integer.toString(FLAG_CREATE);
                newNoteIntent.putExtra("CreatFlag", flagStr);
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
	 
	 
	 public void setNoteListAdapter()
	 {
		 mNoteRecordList = mNoteRecordDao.getNoteRecordsByNoteBookId(mPassedNoteBook.getId());
	     mNoteListAdapter = new NoteListAdapter(this, mNoteRecordList);
         mLvNoteList.setAdapter(mNoteListAdapter);
	 }
}
