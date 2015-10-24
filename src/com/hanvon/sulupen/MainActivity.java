
package com.hanvon.sulupen;


import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.sulupen.db.bean.NoteBookInfo;
import com.hanvon.sulupen.db.bean.ScanRecord;
import com.hanvon.sulupen.db.dao.SCanRecordDao;

public class MainActivity extends Activity implements OnClickListener
{
    private final String TAG = "MainActivity";
    private TextView mTitle;
    private TextView mLeftBtn;
    private TextView mRightBtn;
    private TextView mNewNoteBook;
    private TextView mEditNoteBook;
    private TextView mSearchNoteBook;
    private ImageView mNewNote;
    private ListView mBooksList;
    private TextView mEmptyNoteBook;
    
    public final static int FLAG_EDIT = 1;
	public final static int FLAG_CREATE = 2;
	
	private SCanRecordDao mScanRecordDao;
	private List<ScanRecord> mDataList;
	private List<NoteBookInfo> mNoteBookList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        initDatas();
        
        initViews();
        
    }

    public void initDatas()
    {
        mScanRecordDao = new SCanRecordDao(this);
        mNoteBookList = mScanRecordDao.getAllNoteBooks();
        //mDataList = mScanRecordDao.getALLRecordOrderByTime();
        Log.d(TAG, "mNoteBookList.size is " + mNoteBookList.size());
    }
    
    public void initViews()
    {
        mTitle = (TextView) findViewById(R.id.tv_title);
        mLeftBtn = (TextView) findViewById(R.id.tv_leftbtn);
        mRightBtn = (TextView) findViewById(R.id.tv_rightbtn);
        
        mNewNoteBook = (TextView) findViewById(R.id.tv_newnotebook);
        mEditNoteBook = (TextView) findViewById(R.id.tv_editnotebook);
        mSearchNoteBook = (TextView) findViewById(R.id.tv_searchbtn);
        
        mNewNote = (ImageView) findViewById(R.id.iv_newnote);
        
        mBooksList = (ListView) findViewById(R.id.lv_notebooklist);
        mEmptyNoteBook = (TextView) findViewById(R.id.tv_showempty);
        
        mNewNoteBook.setOnClickListener(this);
        mEditNoteBook.setOnClickListener(this);
        mSearchNoteBook.setOnClickListener(this);
        mNewNote.setOnClickListener(this);
        
        if (mNoteBookList.size() <= 0)
        {
        	mBooksList.setVisibility(View.GONE);
        	mEmptyNoteBook.setVisibility(View.VISIBLE);
        	mEditNoteBook.setVisibility(View.GONE);
        	mSearchNoteBook.setVisibility(View.GONE);
        	
        }
        else
        {
        	mEmptyNoteBook.setVisibility(View.GONE);
        	mBooksList.setVisibility(View.VISIBLE);
        	mEditNoteBook.setVisibility(View.VISIBLE);
        	mSearchNoteBook.setVisibility(View.VISIBLE);
        	
        }
        
        
    }
    
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_newnotebook:
            	Intent newIntent = new Intent(this, NewNoteBookActivity.class);
    			newIntent.setFlags(FLAG_CREATE);
    			startActivity(newIntent);
            break;
            
            case R.id.tv_editnotebook:
                
                break;
                
            case R.id.tv_searchbtn:
                
                break;
            
            case R.id.iv_newnote:
                Intent newNoteIntent = new Intent(this, NoteDetailActivity.class);
                newNoteIntent.setFlags(FLAG_CREATE);
                startActivity(newNoteIntent);
                break;     
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
