package com.hanvon.sulupen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.hanvon.sulupen.db.bean.*;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;
import com.hanvon.sulupen.utils.TimeUtil;

public class NewNoteBookActivity extends Activity implements OnClickListener
{
	private final String TAG = "NewNoteBookActivity";
	
	private TextView mCancleBtn;
	private TextView mDoneBtn;
	private EditText mInput;
	
	private int flagIntent = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_notebook);    
		
		initViews();
	    
		initDatas();
	}

	private void initDatas()
	{
		Intent intent = getIntent();
		if (intent != null) 
		{
			flagIntent = intent.getFlags();
			if (flagIntent == MainActivity.FLAG_CREATE) 
			{
				
			}
		}
	}
	
	 private void initViews()
	 {
		 mCancleBtn = (TextView) findViewById(R.id.tv_cancel_btn);
		 mDoneBtn = (TextView) findViewById(R.id.tv_done_btn);
		 mInput = (EditText) findViewById(R.id.ed_input_notebookname);
		 
		 mCancleBtn.setOnClickListener(this);
		 mDoneBtn.setOnClickListener(this);
	 }
	 
	 @Override
	 public void onClick(View view)
	 {
		 switch (view.getId())
		 {
		 	case R.id.tv_cancel_btn:
                finish();
            break;
            
            case R.id.tv_done_btn:
                saveNoteBookToDb();
            	Intent newIntent = new Intent(this, NoteBookListActivity.class);
    			//newIntent.setFlags(FLAG_CREATE);
    			startActivity(newIntent);  
    			finish();
            break;
                
            case R.id.tv_searchbtn:
                
                break;
            
            case R.id.iv_newnote:
                
                break;     
		 }
	 }
	 
	 public void saveNoteBookToDb()
	 {
	     NoteBookRecord record = new NoteBookRecord();
	     
	     //record.setRecType("notebook");
	     
	     String noteBookName = mInput.getText().toString();
	     Log.d(TAG, "noteBookName is " + noteBookName);
	     record.setNoteBookName(noteBookName);
	     record.setNoteBookId(1);
	     
	     
	     NoteBookRecordDao noteBookRecordDao = new NoteBookRecordDao(this);
	     
	     noteBookRecordDao.add(record);
	     
	 }
}
