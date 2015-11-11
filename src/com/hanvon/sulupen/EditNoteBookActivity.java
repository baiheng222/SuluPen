package com.hanvon.sulupen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.sulupen.adapter.NoteBookEditListAdapter;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;

import java.util.List;

public class EditNoteBookActivity extends Activity implements OnClickListener
{
	private final String TAG = "EditNoteBookActivity";
	
	//private TextView mTvCancel;
	//private TextView mTvDone;
	private ListView mLvList;
	private ImageView mIvBackBtn;
	
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
		//mTvCancel = (TextView) findViewById(R.id.tv_cancel_notebook_edit);
		//mTvDone = (TextView) findViewById(R.id.tv_done_notebook_edit);
		mIvBackBtn = (ImageView) findViewById(R.id.iv_edit_noetbook_backbtn);
		mLvList = (ListView) findViewById(R.id.lv_edit_notebook);
		
		mIvBackBtn.setOnClickListener(this);
		
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
			case R.id.iv_edit_noetbook_backbtn:
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
