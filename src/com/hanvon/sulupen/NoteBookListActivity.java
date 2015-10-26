package com.hanvon.sulupen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteBookListActivity extends Activity implements OnClickListener
{
	private final String TAG = "NoteBookListActivity";
	
	private String mNoteBookName = "NoteBook";
	
	private TextView mBack;
	private TextView mTitle;
	private EditText mInput;
	private ImageView mNewNote;
	 
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
            mNoteBookName = intent.getStringExtra("NoteBookName");
        }
        
    }
	
	 private void initViews()
	 {
		 mBack = (TextView) findViewById(R.id.tv_backbtn);
		 mTitle = (TextView) findViewById(R.id.tv_title);
		 mInput = (EditText) findViewById(R.id.ed_search_input);
		 mNewNote = (ImageView) findViewById(R.id.iv_newnote);
		 
		 mTitle.setText(mNoteBookName);
		 
		 mBack.setOnClickListener(this);
		 mNewNote.setOnClickListener(this);
	 }
	 
	 @Override
	 public void onClick(View view)
	 {
		 switch (view.getId())
		 {
		 	case R.id.tv_backbtn:
                
            break;
                     
            case R.id.iv_newnote:
            	Intent newNoteIntent = new Intent(this, NoteDetailActivity.class);
                newNoteIntent.setFlags(FLAG_CREATE_WITH_BOOKNAME);
                startActivity(newNoteIntent);
            break;     
		 }
	 }
}
