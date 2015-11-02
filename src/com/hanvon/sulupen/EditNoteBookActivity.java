package com.hanvon.sulupen;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.sulupen.adapter.NoteBookEditListAdapter;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;

public class EditNoteBookActivity extends Activity implements OnClickListener
{
	private final String TAG = "EditNoteBookActivity";
	
	private TextView mTvCancel;
	private TextView mTvDone;
	private ListView mLvList;
	
	public NoteBookRecordDao mNoteBookRecordDao;
	private List<NoteBookRecord> mNoteBookList;
	private NoteBookEditListAdapter mEditNoteBookAdapter;
	
	@Override
	protected void onCreate(Bundle saveBaundleInstance)
	{
		super.onCreate(saveBaundleInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_notebook);
		
		initData();
		initView();
	}
	
	
	private void initView()
	{
		mTvCancel = (TextView) findViewById(R.id.tv_cancel_notebook_edit);
		mTvDone = (TextView) findViewById(R.id.tv_done_notebook_edit);
		mLvList = (ListView) findViewById(R.id.lv_edit_notebook);
		
		mTvCancel.setOnClickListener(this);
		mTvDone.setOnClickListener(this);
		
		mEditNoteBookAdapter = new NoteBookEditListAdapter(this, mNoteBookList);
		mLvList.setAdapter(mEditNoteBookAdapter);
	}
	
	private void initData()
	{
		mNoteBookRecordDao = new NoteBookRecordDao(this);
    	mNoteBookList = mNoteBookRecordDao.getAllNoteBooks();
		
	}
	
	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.tv_done_notebook_edit:
			
			break;
			
			case R.id.tv_cancel_notebook_edit:
				finish();
			break;
		}
	}
	
	@Override
	protected void onResume() 
    {
		super.onResume();
		mNoteBookList = mNoteBookRecordDao.getAllNoteBooks();
		mEditNoteBookAdapter = new NoteBookEditListAdapter(this, mNoteBookList);
		mLvList.setAdapter(mEditNoteBookAdapter);
		
		mEditNoteBookAdapter.notifyDataSetChanged();
    }
	
	public void delNoteBook(int pos)
	{	
		/*
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage("Delete NoteBook?");
		dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{

			}
		});
				
		dialog.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				NoteBookRecord m = mNoteBookList.get(pos);
				mNoteBookRecordDao.deleteRecord(m);
			}
		});
		
		dialog.show();
		*/
	}
	
	
	
		
}
