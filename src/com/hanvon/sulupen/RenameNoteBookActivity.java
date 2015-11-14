package com.hanvon.sulupen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.sulupen.NewNoteBookActivity.MaxLengthWatcher;
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
		mEtInputNewName.addTextChangedListener(new MaxLengthWatcher(10, mEtInputNewName));
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
		Log.d(TAG, "clear notebook name");
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
	
	
	/*
	 * 监听输入内容是否超出最大长度，并设置光标位置
	 */
	public class MaxLengthWatcher implements TextWatcher
	{

		private int maxLen = 0;
		private EditText editText = null;

		public MaxLengthWatcher(int maxLen, EditText editText) 
		{
			this.maxLen = maxLen;
			this.editText = editText;
		}

		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) 
		{
			// TODO Auto-generated method stub
			Editable editable = editText.getText();
			int len = editable.length();

			if (len > maxLen) 
			{		
				int selEndIndex = Selection.getSelectionEnd(editable);
				String str = editable.toString();
				// 截取新字符串
				String newStr = str.substring(0, maxLen);
				editText.setText(newStr);
				editable = editText.getText();

				// 新字符串的长度
				int newLen = editable.length();
				// 旧光标位置超过字符串长度
				if (selEndIndex > newLen) {
					selEndIndex = editable.length();
				}
				// 设置新光标所在的位置
				Selection.setSelection(editable, selEndIndex);
				Toast.makeText(RenameNoteBookActivity.this, "reach max", Toast.LENGTH_SHORT).show();
			}
		}

	}
}
