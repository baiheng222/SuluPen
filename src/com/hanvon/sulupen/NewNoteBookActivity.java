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

import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;

import java.util.List;

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
		 		Intent retIntent = new Intent(this, MainActivity.class);
		 		setResult(RESULT_CANCELED, retIntent);
                finish();
            break;
         
            case R.id.tv_done_btn:
                saveNoteBookToDb();
            	Intent newIntent = new Intent(this, NoteBookListActivity.class);
    			//newIntent.setFlags(FLAG_CREATE);
            	newIntent.putExtra("NoteBookName", mInput.getText().toString());
    			startActivity(newIntent);  
    			finish();
            break;
                
            case R.id.tv_searchbtn:
                
                break;
            
            case R.id.iv_newnote:
                
                break;     
		 }
	 }
	 
	 public boolean isNoteBookCreated()
	 {
	     boolean ret = false;
	     NoteBookRecordDao noteBookRecordDao = new NoteBookRecordDao(this);
	     List<NoteBookRecord> noteBooks = noteBookRecordDao.getAllNoteBooks();
	     String noteBookName = mInput.getText().toString();
	     for (int i = 0; i < noteBooks.size(); i++)
	     {
	         if (noteBooks.get(i).getNoteBookName().equals(noteBookName))
	         {
	             ret = true;
	             break;
	         }
	     }
	     
	     return ret;
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
	     
	     Log.d(TAG, "send result to MainActivity");
	     Intent retIntent = new Intent(this, MainActivity.class);
	     setResult(RESULT_OK, retIntent);
	 }
}
