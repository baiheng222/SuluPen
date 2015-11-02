package com.hanvon.sulupen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;

public class RenameNoteBookActivity extends Activity implements OnClickListener
{
	private final String TAG = "RenameNoteBookActivity";
	
	private TextView mTvCancleRename;
	private TextView mTvDoneRename;
	private ImageView mIvDeleteChar;
	private EditText mEtInputNewName;
	
	private NoteBookRecord mNoteBookNeedToReName = null;
	
	@Override
	protected void onCreate(Bundle saveBundleInstance)
	{
		super.onCreate(saveBundleInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_rename_notebook);
		
		initData();
		initView();
		
	}
	
	private void initView()
	{
		mTvCancleRename = (TextView) findViewById(R.id.tv_cancel_notebook_rename);
		mTvDoneRename = (TextView) findViewById(R.id.tv_done_notebook_rename);
		mIvDeleteChar = (ImageView) findViewById(R.id.iv_delchar_icon);
		mEtInputNewName = (EditText) findViewById(R.id.et_input_new_notebook_name);
		
		mTvCancleRename.setOnClickListener(this);
		mTvDoneRename.setOnClickListener(this);
		mIvDeleteChar.setOnClickListener(this);
		
		
		mEtInputNewName.setText(mNoteBookNeedToReName.getNoteBookName());
	}
	
	private void initData()
	{
		Intent intent = getIntent();
		if (null !=intent)
		{
			mNoteBookNeedToReName = (NoteBookRecord) intent.getSerializableExtra("NoteBook");
			Log.d(TAG, "get pass NoteBook");
			if (null == mNoteBookNeedToReName)
			{
				Log.d(TAG, "not get notebook");
			}
			Log.d(TAG, "NoteBook is " + mNoteBookNeedToReName.toString());
		}
	}
	
	private void saveNewNoteBookName()
	{
		String newName = mEtInputNewName.getText().toString();
		if (newName.equals(mNoteBookNeedToReName.getNoteBookName()))
		{
			Toast.makeText(this, "same name", Toast.LENGTH_SHORT);
			return;
		}
		
		mNoteBookNeedToReName.setNoteBookName(newName);
		
		NoteBookRecordDao mDao = new NoteBookRecordDao(this);
		mDao.updataRecord(mNoteBookNeedToReName);
		
	}
	
	private void clearNoteBookName()
	{
		mEtInputNewName.setText("");
	}
	
	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.tv_cancel_notebook_rename:
				finish();
			break;
			
			case R.id.tv_done_notebook_rename:
				saveNewNoteBookName();
				finish();
			break;
			
			
			case R.id.iv_delchar_icon:
				clearNoteBookName();
			break;
		}
	}
}
