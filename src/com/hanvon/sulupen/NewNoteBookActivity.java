package com.hanvon.sulupen;

import java.util.List;
import java.util.UUID;

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
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;
import com.hanvon.sulupen.utils.MD5Util;
import com.hanvon.sulupen.utils.TimeUtil;

public class NewNoteBookActivity extends Activity implements OnClickListener {
	private final String TAG = "NewNoteBookActivity";

	private TextView mCancleBtn;
	private TextView mDoneBtn;
	private EditText mInput;

	private NoteBookRecord mCreatedNoteBook;

	private int flagIntent = -1;

	private final static int FLAG_CREATE = 2;
	private final static int FLAG_CREATE_FOR_CHANGE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			flagIntent = intent.getIntExtra("CreateNoteBook", -1);
			if (flagIntent == FLAG_CREATE_FOR_CHANGE) 
			{
				Log.d(TAG, "start for change notebook");
			} 
			else if (flagIntent == FLAG_CREATE)
			{
				Log.d(TAG, "start for new notebook");
			}
		}
	}

	private void initViews() 
	{
		mCancleBtn = (TextView) findViewById(R.id.tv_cancel_btn);
		mDoneBtn = (TextView) findViewById(R.id.tv_done_btn);
		mInput = (EditText) findViewById(R.id.ed_input_notebookname);
		
		mInput.addTextChangedListener(new MaxLengthWatcher(10, mInput));

		mCancleBtn.setOnClickListener(this);
		mDoneBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.tv_cancel_btn:
			if (flagIntent == FLAG_CREATE_FOR_CHANGE) {
				Intent retIntent = new Intent(this, ChangNoteBookActivity.class);
				setResult(RESULT_CANCELED, retIntent);
			} else {
				Intent retIntent = new Intent(this, MainActivity.class);
				setResult(RESULT_CANCELED, retIntent);
			}
			finish();
			break;

		case R.id.tv_done_btn:

			String newName = mInput.getText().toString();
			if (newName.length() < 1)
			{
				Toast.makeText(this, R.string.notebook_name_null, Toast.LENGTH_SHORT).show();
				return;
			}

			if (saveNoteBookToDb())
			{
				if (flagIntent == FLAG_CREATE_FOR_CHANGE) 
				{

				} 
				else 
				{
					Intent newIntent = new Intent(this, NoteBookListActivity.class);
					newIntent.putExtra("NoteBook", mCreatedNoteBook);
					startActivity(newIntent);
				}
				finish();
			}
			else
			{
				Toast.makeText(this, R.string.same_notebook_name, Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.tv_searchbtn:

			break;

		case R.id.iv_newnote:

			break;
		}
	}

	public boolean isNoteBookCreated()
	{
		NoteBookRecord notebook = null;


		String newName = mInput.getText().toString();

		if (newName.length() < 1)
		{
			Toast.makeText(this, R.string.notebook_name_null, Toast.LENGTH_SHORT).show();
			return true;
		}

		
		boolean ret = false;
		NoteBookRecordDao noteBookRecordDao = new NoteBookRecordDao(this);
		List<NoteBookRecord> noteBooks = noteBookRecordDao.getAllNoteBooks();
		String noteBookName = mInput.getText().toString();
		for (int i = 0; i < noteBooks.size(); i++) 
		{
			Log.d(TAG, "notebook name is " + noteBooks.get(i).getNoteBookName());
			if (noteBooks.get(i).getNoteBookName().equals(noteBookName)) 
			{

				ret = true;
				break;
			}
		}

		return ret;
	}

	public boolean saveNoteBookToDb()
	{
		//去得当前笔记本列表，并用输入的笔记本名称与列表中的笔记本名称比对
		NoteBookRecord notebook = null;

		NoteBookRecordDao noteBookRecordDao = new NoteBookRecordDao(this);
		List<NoteBookRecord> noteBooks = noteBookRecordDao.getAllNoteBooksIncludeDeleted();
		String noteBookName = mInput.getText().toString();

		for (int i = 0; i < noteBooks.size(); i++)
		{
			Log.d(TAG, "notebook name is " + noteBooks.get(i).getNoteBookName());
			if (noteBooks.get(i).getNoteBookName().equals(noteBookName))
			{

				notebook = noteBooks.get(i);
				break;
			}
		}

		if (null != notebook)
		{
			//当输入的笔记本名称与列表中的某一个笔记本名称相同，并且，笔记本不是被标记删除的，表示想要新建的笔记本已经存在
			if (notebook.getNoteBookDelete() == 0)
			{
				return false;
			}
			else
			{
				notebook.setNoteBookDelete(0);
				noteBookRecordDao.updataRecord(notebook);
				mCreatedNoteBook = notebook;
				return true;
			}
		}

		notebook = new NoteBookRecord();

		Log.d(TAG, "noteBookName is " + noteBookName);
		notebook.setNoteBookName(noteBookName);
		String time = TimeUtil.getCurTimeForMd5();
		Log.d(TAG, "!!!!!!!!! !!!! time is " + time);
		notebook.setNoteBookId(MD5Util.md5(time));
		notebook.setNoteBookUpLoad(0);
		notebook.setNoteBookDelete(0);

		noteBookRecordDao.add(notebook);

		mCreatedNoteBook = notebook;

		Log.d(TAG, "send result to MainActivity");

		if (flagIntent == FLAG_CREATE_FOR_CHANGE) {
			Intent retIntent = new Intent(this, ChangNoteBookActivity.class);
			retIntent.putExtra("NoteBook", notebook);
			setResult(RESULT_OK, retIntent);
		} else {
			Intent retIntent = new Intent(this, MainActivity.class);
			setResult(RESULT_OK, retIntent);
		}

		return true;
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
				Toast.makeText(NewNoteBookActivity.this, R.string.input_reach_max, Toast.LENGTH_SHORT).show();
			}
		}

	}

}
