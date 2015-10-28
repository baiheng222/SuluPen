package com.hanvon.sulupen;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.utils.TimeUtil;



public class NoteDetailActivity extends Activity implements OnClickListener
{
    private final String TAG = "NoteDetailActivity";
    
    private Button mConfirmButton;
    private TextView mTextView ;  
    private int flagIntent = -1;
    
    private NoteRecord mNoteRecord;
    private String mCreateDate;
    private String mNoteBookName;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_note_detail);
        initView();
        
        mCreateDate = TimeUtil.getCurDate();
        Log.d(TAG, "note create tiem " +  mCreateDate);
        

	}
	
	private void initView() 
	{
		mConfirmButton=(Button) findViewById(R.id.save_button);
		mTextView = (TextView) findViewById(R.id.etNoteContent);
		
		mConfirmButton.setOnClickListener(this);
	}
	
	private void initDatas()
	{
		Intent intent = getIntent();
		if (intent != null) 
		{
			flagIntent = intent.getFlags();
			if (flagIntent == MainActivity.FLAG_CREATE) 
			{
				mNoteBookName = "NoteBook";
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
		note.setNoteTitle("test");
		note.setNoteContent(mTextView.getText().toString());
		note.setCreateTime(mCreateDate);
		note.setCreateAddr("test");
		note.setWeather("test");
		note.setAddrDetail("test");
		note.setIsUpdateHanvon(0);
		note.setInputType(0);
		note.setVersion(1);
		
		NoteRecordDao noteDao = new NoteRecordDao(this);
		noteDao.add(note);
		
	}
}
