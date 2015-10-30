package com.hanvon.sulupen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class ChooseNoteActivity extends Activity implements OnClickListener
{
	private final String TAG = "ChooseNoteActivity";
	
	private TextView mBack;
	private TextView mTitle;
	private TextView mCancelBtn;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notes_choice);
	}
	
	
	private void initView()
	{
		mBack = (TextView) findViewById(R.id.tv_backbtn);
		mTitle = (TextView) findViewById(R.id.tv_title_choose);
		mCancelBtn = (TextView) findViewById(R.id.tv_cancel_btn);
		
		mBack.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view)
	{
		
	}
}
