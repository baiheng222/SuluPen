package com.hanvon.sulupen;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanvon.sulupen.adapter.NoteBookChangeAdapter;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;

public class ChangNoteBookActivity extends Activity implements OnClickListener
{
    private final String TAG = "ChangNoteBookActivity";
    
    private TextView mTvCancel;
    private RelativeLayout mIvAddNewNoteBook;
    private ListView mLvNoetBookList;
    
    private NoteBookRecordDao mNoteBookRecordDao;
    private List<NoteBookRecord> mNoteBookList;
    
    private NoteBookChangeAdapter mAdapter; 
    
    private int flagIntent = -1;
    private final static int FLAG_CREATE_FOR_CHANGE = 3;
    private final static int RESULT_CHAGNE_NOTEBOOK = 4;
    private final static int REQUEST_CREATE_ROR_CHANGE = 5;
    private NoteBookRecord mPassedNoteBookRecord;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_notebook);
        
        initData();
        
        initView();
        
    }
    
    private void initView()
    {
        mTvCancel = (TextView) findViewById(R.id.tv_cancel_change_notebook_btn);
        mIvAddNewNoteBook = (RelativeLayout) findViewById(R.id.rl_new_notebook);
        mLvNoetBookList = (ListView) findViewById(R.id.lv_list_of_change_notebook);
        
        mIvAddNewNoteBook.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        
        mAdapter = new NoteBookChangeAdapter(ChangNoteBookActivity.this, mNoteBookList, mPassedNoteBookRecord);
        
        mLvNoetBookList.setAdapter(mAdapter);
        mLvNoetBookList.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
            {
                Log.d(TAG, "item " + position + " clicked");
                
                Intent intent = new Intent();
                Log.d(TAG, "selected note book is " + mNoteBookList.get(position).toString());
                intent.putExtra("NewNoteBook", mNoteBookList.get(position));
                setResult(RESULT_OK, intent);
                finish();
                
            }
        });
    }
    
    private void initData()
    {
        Intent intent = getIntent();
        if (intent != null) 
        {
            mPassedNoteBookRecord = (NoteBookRecord) intent.getSerializableExtra("NoteBook");
        }
        
        mNoteBookRecordDao = new NoteBookRecordDao(this);
        mNoteBookList = mNoteBookRecordDao.getAllNoteBooks();
    }
    
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_cancel_change_notebook_btn:
                finish();
            break;
            
            case R.id.rl_new_notebook:
                Intent newNoteIntent = new Intent(this, NewNoteBookActivity.class);
                newNoteIntent.setFlags(FLAG_CREATE_FOR_CHANGE);
                startActivityForResult(newNoteIntent, REQUEST_CREATE_ROR_CHANGE);
            break;
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        //mNoteBookList = mNoteBookRecordDao.getAllNoteBooks();
        //Log.d(TAG, "in onActivityResult func, mNoteBookList.size is " + mNoteBookList.size());
        if (requestCode == REQUEST_CREATE_ROR_CHANGE)
        {
            if (resultCode == RESULT_OK)
            {
                NoteBookRecord noteBook = (NoteBookRecord) data.getSerializableExtra("NoteBook");
                Log.d(TAG, "get new note book instance, name is " + noteBook.getNoteBookName());
                
                Intent intent = new Intent();
                Log.d(TAG, "selected note book is " + noteBook.toString());
                intent.putExtra("NewNoteBook", noteBook);
                setResult(RESULT_OK, intent);
                finish();
            }
            else
            {
                Log.d(TAG, "result is canceled");
            }
        }
    }
    
}
