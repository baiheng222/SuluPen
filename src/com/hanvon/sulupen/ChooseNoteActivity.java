package com.hanvon.sulupen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.hanvon.sulupen.adapter.NoteChooseAdapter;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NotePhotoRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.utils.HvnCloudManager;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.SHA1Util;

public class ChooseNoteActivity extends Activity implements OnClickListener
{
	private final String TAG = "ChooseNoteActivity";
	
	private ImageView mBack;
	private TextView mTitle;
	private TextView mCancelBtn;
	private ListView mNotesList;
	private ImageView mShareBtn;
	private ImageView mMoveNotesBtn;
	private ImageView mDeleteNoteBtn;
	
	private NoteRecordDao mNoteRecordDao;
	private List<NoteRecord> mNoteRecordList;
	private NoteBookRecord mPassedNoteBook;
	private NoteChooseAdapter mNotesAdapter;
	private String mSearchString;
	
	private int flagIntent = 1;
	
	private final static int FLAG_CREATE = 2;
    private final static int FLAG_EDIT = 1;
    public final static int FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK = 3;
    private final static int UPLLOAD_FILE_CLOUD_SUCCESS = 5;
    private final static int UPLLOAD_FILE_CLOUD_FAIL = 6;
    private final static int FLAG_SEARCH = 7;
    private final static int RESULT_CHAGNE_NOTEBOOK_BATCH = 8;
	
    
	private ProgressDialog pd;
    private String  strLinkPath = null;
    private Boolean bShareClick = false;
    private Bitmap bitmapLaunch;
	private List<NoteRecord> mCSelectDatas;
	
	
	private Handler handler = new Handler() {
		@SuppressLint("ShowToast")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case UPLLOAD_FILE_CLOUD_SUCCESS:
					pd.dismiss();
					showShare();
				
				break;
			case UPLLOAD_FILE_CLOUD_FAIL:
	
				
					pd.dismiss();
					Toast.makeText(ChooseNoteActivity.this, "获取链接失败，不能分享!", Toast.LENGTH_SHORT).show();
					bShareClick = false;
				
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notes_choice);
		
		initData();
		initView();
	}
	
	
	private void initView()
	{
		mBack = (ImageView) findViewById(R.id.tv_backbtn);
		mTitle = (TextView) findViewById(R.id.tv_title_choose);
		mCancelBtn = (TextView) findViewById(R.id.tv_cancel_btn);
		
		mShareBtn = (ImageView) findViewById(R.id.iv_share_note);
		mMoveNotesBtn = (ImageView) findViewById(R.id.iv_move_note);
		mDeleteNoteBtn = (ImageView) findViewById(R.id.iv_delete_note);
		
		
		mNotesList = (ListView) findViewById(R.id.lv_choose_notelist);
		
		mNotesAdapter = new NoteChooseAdapter(this, mNoteRecordList);
		mNotesList.setAdapter(mNotesAdapter);
		
		mBack.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		mShareBtn.setOnClickListener(this);
		mMoveNotesBtn.setOnClickListener(this);
		mDeleteNoteBtn.setOnClickListener(this);
		
		if (flagIntent == FLAG_SEARCH)
		{
			mMoveNotesBtn.setEnabled(false);
			mMoveNotesBtn.setVisibility(View.INVISIBLE);
		}
	}
	
	private void initData()
	{
		mNoteRecordDao = new NoteRecordDao(this);
		
		Intent intent = getIntent();
        if (intent != null) 
        {
        	flagIntent = intent.getIntExtra("SearchType", -1);
        	
			if (flagIntent == FLAG_SEARCH)
			{
				mSearchString = (String) intent.getStringExtra("SearchString");
				Log.d(TAG, "get search string "  + mSearchString);
				mNoteRecordList = mNoteRecordDao.searchRecordsByString(mSearchString);
			}
			else
			{
				mPassedNoteBook = (NoteBookRecord) intent.getSerializableExtra("NoteBook");
				mNoteRecordList = mNoteRecordDao.getNoteRecordsByNoteBookId(mPassedNoteBook.getId());
		        Log.d(TAG, "notes num is " +  mNoteRecordList.size());
			}
        }
		/*
		Intent intent = getIntent();
        if (intent != null) 
        {
             
            mPassedNoteBook = (NoteBookRecord) intent.getSerializableExtra("NoteBook");
            //mNoteBookName = mPassedNoteBook.getNoteBookName();
        }
        else
        {
            //mNoteBookName = "NoteBook";
        }
        
        //mPassedNoteBook.toString();
        
        
        mNoteRecordList = mNoteRecordDao.getNoteRecordsByNoteBookId(mPassedNoteBook.getId());
        Log.d(TAG, "notes num is " +  mNoteRecordList.size());
        */
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.tv_backbtn:
				finish();
			break;
			
			case R.id.tv_cancel_btn:
				finish();
			break;
			
			case R.id.iv_share_note:
				if(!bShareClick)
				{
					//CharSequence cs =  R.string.link_mess;
					pd = ProgressDialog.show(ChooseNoteActivity.this, "", getString(R.string.link_mess));
			try {
				shareSelect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		     }
			break;
			
			case R.id.iv_move_note:
			    Intent changeIntent = new Intent(this, ChangNoteBookActivity.class);
			    changeIntent.putExtra("NoteBook", mPassedNoteBook);
			    startActivityForResult(changeIntent, RESULT_CHAGNE_NOTEBOOK_BATCH);
			break;
			
			case R.id.iv_delete_note:
				deleteSelectedNote();
			break;
		}
	}
	
	

	
	private void shareSelect() throws IOException
	{
		LogUtil.i("tong-----------shareSelect");
		String filename = SHA1Util.encodeBySHA("selectshare")+".txt";
		String path = "/sdcard/" + filename;
		Boolean upFlag = false;
		HvnCloudManager hvnCloud = new HvnCloudManager();
		
		//取得内容，titile等
		if (mNotesAdapter != null)
		{
			
			int allSize = mNotesAdapter.getSelecetNote().size();
			LogUtil.i("tong-------*****---allsize"+allSize);
			for(int i = 0;i<allSize;i++)
			{
				NoteRecord note = mNotesAdapter.getSelecetNote().get(i);
				String title =note.getNoteTitle();
				String content = note.getNoteContent();

				ArrayList<NotePhotoRecord> notephoteRecordDate = note.getNotePhotoList();
				hvnCloud.WriteFileForShareSelect(title,content,notephoteRecordDate);	
				
				if((content != null) || (notephoteRecordDate.size() != 0) )
				{
					upFlag = true;
				}
				
			}
			
			mNotesAdapter.clearSelectData();
		}
		
		if(upFlag)
		{
			LogUtil.i("tong--------upFlag");
			bShareClick = true;
			UploadFilesToHvnCloudForShare();
		}
		else
		{
			pd.dismiss();
			File file = new File(path);
	        if (file.isFile() && file.exists()) {
	            file.delete();
	        }
			
		}
		
			
			
	}
	
	
	
	
	public void UploadFilesToHvnCloudForShare(){
		
		//final String result = null;
		new Thread() {
			@Override
			public void run() {
				String result = null;
				HvnCloudManager hvnCloud = new HvnCloudManager();
				result = hvnCloud.ShareForSelect();
				LogUtil.i(result);
				
				if (result == null){
					Message msg = new Message();
                    msg.what = UPLLOAD_FILE_CLOUD_FAIL;
                    handler.sendMessage(msg); 
				}else{
					strLinkPath = result;
					Message msg = new Message();
                    msg.what = UPLLOAD_FILE_CLOUD_SUCCESS; 
                    handler.sendMessage(msg);	
				}
			}
		}.start();
	}	
	
	
	private void showShare() {
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 
        LogUtil.i("tong------strLinkPath:"+strLinkPath);
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(getString(R.string.share_from_hanvon));
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl(strLinkPath);
		 // text是分享文本，所有平台都需要这个字段
//		 String title = etNoteTitle.getText().toString();
//		 if(title == "")
//		 {
//			 String strContent = etScanContent.getText().toString();
//			 title = strContent;
//		 }
//		 oks.setText(title);
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 String curPath = getApplicationContext().getFilesDir().getPath();

		 copyPhoto();
		 String srcPath = curPath + "/"+"image.png";
		 //String newPath= "/sdcard/app_launcher.png";
		 LogUtil.i("tong-----------srcPath:"+srcPath);
		 
		 //copyFile(srcPath,newPath);
		 oks.setImagePath(srcPath);//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl(strLinkPath);
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment("我是测试评论文本");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl(strLinkPath);

		// 启动分享GUI
		 oks.show(this);
		 bShareClick = false;
		 }
	
	
	
	public void copyPhoto()
	{
		
		bitmapLaunch = BitmapFactory.decodeResource(getResources(), R.drawable.app_launcher);  
        FileOutputStream fos = null;  
        try {  
            fos = openFileOutput("image.png", Context.MODE_PRIVATE);  
            bitmapLaunch.compress(Bitmap.CompressFormat.PNG, 100, fos);  
        } catch (FileNotFoundException e) {  
        } finally {  
            if (fos != null) {  
                try {  
                    fos.flush();  
                    fos.close();  
                } catch (IOException e) {  
                }  
            }  
        } 
	}
	
	private void deleteSelectedNote()
	{
		if (mNotesAdapter != null)
		{
			mNotesAdapter.delSelectedNotes();
		}
	}
	
	private void batchChangeNoteBook(NoteBookRecord notebook)
	{
		if (mNotesAdapter != null)
		{
			mNotesAdapter.batchReNameNoteBook(notebook);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		    case  RESULT_CHAGNE_NOTEBOOK_BATCH:
		        if (RESULT_OK == resultCode)
		        {
		            //Log.d(TAG, "get change note book result");
		            NoteBookRecord notebook = (NoteBookRecord) data.getSerializableExtra("NewNoteBook");
		            Log.d(TAG, "get change note book result, notebook is " + notebook.toString());
		            batchChangeNoteBook(notebook);
		        }
		        else
		        {
		            Log.d(TAG, "receive reslut code is " + resultCode);
		        }
		    break;
			
		}
		
	}
	
}
