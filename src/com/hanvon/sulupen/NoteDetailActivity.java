package com.hanvon.sulupen;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.utils.TimeUtil;



public class NoteDetailActivity extends Activity implements OnClickListener
{
    private final String TAG = "NoteDetailActivity";
    
    private Button mConfirmButton;
    private TextView mTextView ;
    private EditText mEtContent;
    private EditText mEtTitle;
    private int flagIntent = -1;
    private static int FLAG_CREATE = 2;
    private static int FLAG_EDIT = 1;
    
    private NoteBookRecord mPassedNoteBookRecord;
    private NoteRecord mPassedNoteRecord;
    private String mCreateDate;
    private String mNoteBookName;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_note_detail);
		
		initDatas();
		
        initView();
        
        mCreateDate = TimeUtil.getCurDate();
        Log.d(TAG, "note create tiem " +  mCreateDate);
        

	}
	
	private void initView() 
	{
		mConfirmButton=(Button) findViewById(R.id.save_button);
		mTextView = (TextView) findViewById(R.id.etNoteContent);
		mEtContent = (EditText) findViewById(R.id.etNoteContent);
		mEtTitle = (EditText) findViewById(R.id.etNoteTitle);
		
		mConfirmButton.setOnClickListener(this);
	}
	
	private void initDatas()
	{
	    mNoteBookName = "NoteBook";
	    
		Intent intent = getIntent();
		if (intent != null) 
		{
			flagIntent = intent.getFlags();
			if (flagIntent == FLAG_CREATE) 
			{
				mPassedNoteBookRecord = (NoteBookRecord) intent.getSerializableExtra("NoteBook");
				mNoteBookName = mPassedNoteBookRecord.getNoteBookName();
				Log.d(TAG, "NoteBookName is " + mNoteBookName);	
			}
			else if (flagIntent == FLAG_EDIT)
			{
				mPassedNoteRecord = (NoteRecord) intent.getSerializableExtra("NoteRecord");
			}
		}
	}
	
	
	
	@Override
	public void onClick(View v) 
	{
	     switch (v.getId())
	        {
	            case R.id.save_button:
	                saveNoteToDb();
	                finish();
	            break;
	        }
	}
	     
	
	private void saveNoteToDb()
	{
		NoteRecord note = new NoteRecord();
		note.setNoteBookName(mNoteBookName);
		note.setNoteContent(mEtContent.getText().toString());
		note.setNoteTitle(mEtTitle.getText().toString());
		note.setCreateTime(mCreateDate);
		note.setCreateAddr("test");
		note.setWeather("test");
		note.setAddrDetail("test");
		note.setIsUpdateHanvon(0);
		note.setInputType(0);
		note.setVersion(1);
		note.setNoteBook(mPassedNoteBookRecord);
		
		NoteRecordDao noteDao = new NoteRecordDao(this);
		noteDao.add(note);
		
	}
}
