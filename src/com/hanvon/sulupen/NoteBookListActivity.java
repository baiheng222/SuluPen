package com.hanvon.sulupen;

import android.app.Activity;
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
	
	private TextView mBack;
	//private TextView mDoneBtn;
	private EditText mInput;
	private ImageView mNewNote;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.notebook_list);    
		
		initViews();
	        
	}

	 private void initViews()
	 {
		 mBack = (TextView) findViewById(R.id.tv_backbtn);
		 //mDoneBtn = (TextView) findViewById(R.id.tv_done_btn);
		 mInput = (EditText) findViewById(R.id.ed_search_input);
		 mNewNote = (ImageView) findViewById(R.id.iv_newnote);
		 
		 mBack.setOnClickListener(this);
		 //mDoneBtn.setOnClickListener(this);
	 }
	 
	 @Override
	 public void onClick(View view)
	 {
		 switch (view.getId())
		 {
		 	case R.id.tv_backbtn:
                
            break;
                     
            case R.id.iv_newnote:
                
            break;     
		 }
	 }
}
