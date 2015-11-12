package com.hanvon.sulupen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.alibaba.fastjson.JSON;
import com.hanvon.bluetooth.BluetoothChatService;
import com.hanvon.bluetooth.BluetoothDataPackage;
import com.hanvon.bluetooth.BluetoothIntenAction;
import com.hanvon.bluetooth.BluetoothMsgReceive;
import com.hanvon.bluetooth.BluetoothService;
import com.hanvon.bluetooth.BluetoothSetting;
import com.hanvon.sulupen.adapter.ImagePublishAdapter;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.datas.ImageItem;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NotePhotoRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;
import com.hanvon.sulupen.db.dao.NotePhotoRecordDao;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.ui.ImageBucketChooseActivity;
import com.hanvon.sulupen.ui.ImageZoomActivity;
import com.hanvon.sulupen.utils.CustomConstants;
import com.hanvon.sulupen.utils.CustomDialog;
import com.hanvon.sulupen.utils.HvnCloudManager;
import com.hanvon.sulupen.utils.IntentConstants;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.TimeUtil;
import com.hanvon.sulupen.utils.UiUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanNoteActivity extends Activity implements OnClickListener{

    private final String TAG = "ScanNoteActivity";
    
	public static ScanNoteActivity scanNoteAct;
	
    private ImageView mBcakImage;
    private ImageView mShareImage;
    private ImageView mChangeTag;
    private ImageView mInsertImag;
    private ImageView mDeleteImage;

    //扫描edit
    private EditText etScanContent;
    //笔记名字edit
    private EditText etNoteTitle;
    private TextView tvTopic;
    private TextView tvScanContent;
    private TextView tvNoteTitle;
    
    private TextView tvNewNote;
    
    private HorizontalScrollView hsvLayoutScanImage;
    private ImageView ivScanBmp;
    
    private SharedPreferences sp;
    
    private ImageView IVsendImag;
    
    private Boolean bShareFlag = false;
    private String  strLinkPath = null;
    private Boolean bShareClick = false;
    
	//private TopicDialog topicDlg;

	private String curTopicId = "";
	
	private final static int RESULT_CHAGNE_NOTEBOOK = 4;
	
	//数据库相关
	private final static int FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK = 3;
	private final static int FLAG_CREATE = 2;
    private final static int FLAG_EDIT = 1;
    private final static int UPLLOAD_FILE_CLOUD_SUCCESS = 5;
    private final static int UPLLOAD_FILE_CLOUD_FAIL = 6;
	private int flagIntent = 1;
	private NoteRecordDao mScanRecordDao;
	private NoteRecord mScanRecord = null;
	private NotePhotoRecordDao mPhotoRecordDao;
	private NotePhotoRecord mPhotoRecord = null;
	
	private NoteBookRecord mNoteBookRecord = null;
	
	InputMethodManager imm;
	
	private String curAddress;
    
	private Handler mbUiThreadHandler;
   // private TextView tv_nd_scanAddress;
    
	private GridView mGridView;
	private ImagePublishAdapter mAdapter;
	public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
	public static List<ImageItem> mPrevDataList = new ArrayList<ImageItem>();
	
	private WakeLock wakeLock;

	private BluetoothMsgReceive btMsgReceiver;
	private int isSendImageMode = 0;

	private ProgressDialog pd;
	private boolean flag;
	private Handler handler = new Handler() {
		@SuppressLint("ShowToast")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothMsgReceive.RECEIVEIMG_CHANGE:
				
				int resultStr = msg.arg1;
				LogUtil.i("tong-----***********---BTMsgReceiver.RECEIVEIMG_CHANGE");
				if (resultStr == 1) {
					if (isSendImageMode == 1) {
						isSendImageMode = 0;
					} else {
						isSendImageMode = 1;
					}
					updateCheckModeState();
				}
				break;

			case BluetoothMsgReceive.BT_CONNECTED:
			//	getRightButton().setEnabled(true);
				break;
			case BluetoothMsgReceive.BT_DISCONNECT:
			//	getRightButton().setEnabled(false);
				break;
			case UPLLOAD_FILE_CLOUD_SUCCESS:
				if(bShareFlag )
				{
					pd.dismiss();
					bShareFlag = false;
					showShare();
				}
				else
				{
					pd.dismiss();
					Toast.makeText(ScanNoteActivity.this, "文件上传成功!", Toast.LENGTH_SHORT).show();
			
				}
				

				break;
			case UPLLOAD_FILE_CLOUD_FAIL:
				
				if(bShareFlag )
				{
					bShareFlag = false;
					pd.dismiss();
					Toast.makeText(ScanNoteActivity.this, "获取链接失败，不能分享!", Toast.LENGTH_SHORT).show();
				
					bShareClick = false;
				}
				else
				{
				pd.dismiss();
				Toast.makeText(ScanNoteActivity.this, "文件上传失败!", Toast.LENGTH_SHORT).show();
				LogUtil.i("-----xxxxxxx----------上传失败");
				}
				
				break;

			default:
				break;
			}
		}
	};
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			LogUtil.i("===========================");
			saveNoteToDb();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scanNoteAct=this;
		setContentView(R.layout.activity_note_detail);
		getTempFromPref();
		//@mTopicDao = new TopicDao(this);
		//setContentView(R.layout.activity_share);
        initView();
        initData();

        mbUiThreadHandler = new Handler();
	}
	
	private void initView() {

		mBcakImage= (ImageView) findViewById(R.id.come_back);
		mShareImage = (ImageView) findViewById(R.id.ivShare);
		mChangeTag = (ImageView) findViewById(R.id.ivChangelag);
		mInsertImag = (ImageView) findViewById(R.id.ivInsertImage);
		mDeleteImage = (ImageView) findViewById(R.id.ivDelete);
		
		
		hsvLayoutScanImage = (HorizontalScrollView) this
				.findViewById(R.id.horScroll);
		ivScanBmp = (ImageView) this.findViewById(R.id.ivScanImage);
		
		tvNewNote = (TextView) this.findViewById(R.id.tv_title);;
		etScanContent = (EditText) this.findViewById(R.id.etNoteContent);
		etNoteTitle = (EditText) this.findViewById(R.id.etNoteTitle);
		tvScanContent = (TextView) this.findViewById(R.id.tvNoteContent);
		tvNoteTitle = (TextView) this.findViewById(R.id.tvNoteTitle);
		tvTopic = (TextView) this.findViewById(R.id.tvMyTopic);
		IVsendImag = (ImageView)this.findViewById(R.id.ivScan);
		
		tvNoteTitle.setOnClickListener(this);
		tvScanContent.setOnClickListener(this);
		tvTopic.setOnClickListener(this);
		IVsendImag.setOnClickListener(this);
		mDeleteImage.setOnClickListener(this);
		
		mChangeTag.setOnClickListener(this);
		mBcakImage.setOnClickListener(this);
		mShareImage.setOnClickListener(this);
		mInsertImag.setOnClickListener(this);
		
		
		mScanRecordDao = new NoteRecordDao(this);
		
		mPhotoRecordDao = new NotePhotoRecordDao(this);
		
		//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		btMsgReceiver = new BluetoothMsgReceive(handler);
		// 添加图片相关代码 
		mGridView = (GridView) findViewById(R.id.gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		//只在这里setAdapter会导致进入历史笔记后添加的图片显示不一致的问题.因为这里Activity的mDataList和adapter内操作的mDataList的本体不是一个,在onResume中设置(或重新)setAdapter可解决该问题
		// mAdapter = new ImagePublishAdapter(this, mDataList);
		// mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == getDataSize()) {
					//new PopupWindows(ScanNoteActivity.this, mGridView);    
			       // imm.hideSoftInputFromWindow(etScanContent.getWindowToken(),0);
				} else {
					Intent intent = new Intent(ScanNoteActivity.this,
							ImageZoomActivity.class);
					intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
							(Serializable) mDataList);
					intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION,
							position);
					startActivity(intent);
				}
			}
		});
		
	}
	
	
	private int getDataSize() {
		return mDataList == null ? 0 : mDataList.size();
	}
	
	private void initData()
	{
		LogUtil.i("tong---------initData");
		
		Intent intent = getIntent();
		if (intent != null) 
		{
			flagIntent = intent.getFlags();
			if (flagIntent == FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK)
			{
				changeRecordMode(2);
			    if (sp == null)
			    {
			        sp = getSharedPreferences(CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
			    }
		        String defNoteBook = sp.getString("lastTagSeclected", "EmptyNoteBook");
		        Log.d(TAG, "lastSaved Notebook name is " + defNoteBook);
		        if (defNoteBook.equals("EmptyNoteBook"))
		        {
		            Log.d(TAG, "no notebook, need new one !!!!");
		            mNoteBookRecord = new NoteBookRecord();
		            mNoteBookRecord.setNoteBookName("笔记本01");
		        }
		        else
		        {
		            mNoteBookRecord = getNoteBookByName(defNoteBook);
		            if (null == mNoteBookRecord)
		            {
		                mNoteBookRecord = new NoteBookRecord();
		                mNoteBookRecord.setNoteBookName("笔记本01");
		            }
		        }

                tvTopic.setText(mNoteBookRecord.getNoteBookName());     
                HanvonApplication.noteCreateTime = TimeUtil.getcurTime(TimeUtil.FORMAT_FULL);
                curAddress = ((HanvonApplication) getApplication()).getAddrDetail();
                
                mScanRecord = new NoteRecord();
			}
			else if (flagIntent == FLAG_CREATE ) 
			{
				changeRecordMode(2);
			    //新建
				mNoteBookRecord = (NoteBookRecord) intent.getSerializableExtra("NoteBook");
			    
				tvTopic.setText(mNoteBookRecord.getNoteBookName());
				if(sp == null)
				{
					sp = getSharedPreferences(CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
				}		

				HanvonApplication.noteCreateTime = TimeUtil.getcurTime(TimeUtil.FORMAT_FULL);
				curAddress = ((HanvonApplication) getApplication()).getAddrDetail();
				
				mScanRecord = new NoteRecord();
			} 
			else 
			{
				changeRecordMode(1);
				tvNewNote.setText("");

			    int noteid = intent.getIntExtra("NoteRecordId", -1);
			    Log.d(TAG, "get noteid: " + noteid);
			    if (noteid != -1)
			    {
			        mScanRecord = mScanRecordDao.getNoteRecordById(noteid);
			    }
			    mNoteBookRecord = mScanRecord.getNoteBook();
			    Log.d(TAG, "get note : " + mScanRecord.toString());
				etScanContent.setText(mScanRecord.getNoteContent());
				tvScanContent.setText(mScanRecord.getNoteContent());
				HanvonApplication.noteCreateTime = (mScanRecord.getCreateTime()!=null&&mScanRecord.getCreateTime().length()>0) ? mScanRecord.getCreateTime() : TimeUtil.getcurTime(TimeUtil.FORMAT_FULL);

				tvTopic.setText(mScanRecord.getNoteBookName());
				if (mScanRecord.getNoteTitle() != null) 
				{
					if (!mScanRecord.getNoteTitle().equals(""))
						etNoteTitle.setText(mScanRecord.getNoteTitle());
						tvNoteTitle.setText(mScanRecord.getNoteTitle());
				}
						
				ArrayList<NotePhotoRecord> mPhotesList = mScanRecord.getNotePhotoList();
				for(int i=0;i<mPhotesList.size();i++)
				{
				    Log.d(TAG, "photo url is: " + mPhotesList.get(i).getLocalUrl());
				    
					ImageItem curImage = new ImageItem();
					curImage.sourcePath =  mPhotesList.get(i).getLocalUrl();
					mDataList.add(curImage);
				
				}
				
				toBrowseMode();
				
			}
		}

		initIsSendScanImage();
		updateCheckModeState();
	}
	
	
	
	
	private void toBrowseMode() {
		etScanContent.setVisibility(View.GONE);
		etNoteTitle.setVisibility(View.GONE);
		tvScanContent.setVisibility(View.VISIBLE);
		tvNoteTitle.setVisibility(View.VISIBLE);
	}
	
	
	private void toEditableMode(EditText view) {
		changeRecordMode(2);
		etScanContent.setVisibility(View.VISIBLE);
		etNoteTitle.setVisibility(View.VISIBLE);
		tvScanContent.setVisibility(View.GONE);
		tvNoteTitle.setVisibility(View.GONE);
		//显示键盘
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	//	imm.showSoftInput(view, 0);
	}
	
	/**
	 * 更改模式按钮状态,扫描框
	 */
	private void updateCheckModeState() {
		LogUtil.i("tong----------updateCheckModeState:"+isSendImageMode);
		if (isConnected()) {
			LogUtil.i("tong---------is SendImag----------");
		    if (isSendImageMode==1) {
			    IVsendImag.setBackgroundResource(R.drawable.scan_note);
			    hsvLayoutScanImage.setVisibility(View.VISIBLE);
			   
		    } else {
			    IVsendImag.setBackgroundResource(R.drawable.close_scan);
			    hsvLayoutScanImage.setVisibility(View.GONE);
			  
		    }
		}else{
			IVsendImag.setBackgroundResource(R.drawable.notuse_scan);
			hsvLayoutScanImage.setVisibility(View.GONE);
			//设置校对图片
		}
      //  if (isConnected()) {
		//	getRightButton().setEnabled(true);
	//	} else {
		//	getRightButton().setEnabled(false);
	//	}

	}
	
	
	/**
	 * 得到校对模式初始状态
	 * 
	 * @return
	 */
	private void initIsSendScanImage() {
	    //判断网络是否连接
		if (isConnected())
		{
	        Boolean curState = BluetoothSetting.getBlueIsSendImage();
	        if(curState)
	        {
	        	isSendImageMode = 1;
	        }
	        else
	        {
	        	isSendImageMode = 0;
	        }
		}
		else
		{
			isSendImageMode = 0;
		}
		

        
		//isSendImageMode = BluetoothSetting.getBlueIsSendImage();
	}
	
	

	private void sendRecevieImagetoEpen() {
		LogUtil.i("tong-------sendRecevieImagetoEpen");
		HashMap<String, String> map = new HashMap<String, String>();
		if (isSendImageMode == 1) {
			map.put("receive_scan_image", "0");
		} else {
			map.put("receive_scan_image", "1");
		}
		BluetoothService.getServiceInstance().getBluetoothChatService()
				.sendBTData(2, BluetoothDataPackage.epenReceiveScanImage(map));
	}
	
	
	/**
	 * 更新模式
	 * 
	 * @return
	 */
	private void updateCheckMode() {
		Boolean curState = false ;
		if(isSendImageMode == 1)
		{
			curState = true;
		}
		else
		{
			curState = false;
		}
		
		BluetoothSetting.setBlueIsSendImage(curState);
		BluetoothSetting.writeBack();
		
	}
	
	/**
	 * 获取连接状态
	 * 
	 * @return
	 */
	private boolean isConnected() {
		return BluetoothService.getServiceInstance().getBluetoothChatService()
				.getState() == BluetoothChatService.STATE_CONNECTED;
	}
	
	
	
	public boolean getIsSendScanImage() {
		return isSendImageMode == 1 ? true : false;
	}
	
	
	public void setScanImage(Bitmap bmp) {

		ivScanBmp.setImageBitmap(bmp);

	}
	
	/**
	 * 
	 * @param mode
	 */
	private void changeRecordMode(int mode) {
	//	HanVonService.scanRecordMode = mode;
		BluetoothService.scanRecordMode = mode;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
	}
	
	private NoteBookRecord getNoteBookByName(String name)
	{
	    NoteBookRecordDao bookDao = new NoteBookRecordDao(this);
        NoteBookRecord notebook = bookDao.getNoteBookRecordByName(name);
        return notebook;
	}
	
	private boolean isNoteBookExist(String name)
	{
	    boolean ret = false;
	    NoteBookRecord notebook = null;    
	    notebook = getNoteBookByName(name);
	    if (null != notebook)
	    {
	        ret = true;
	    }
	    else
	    {
	        ret = false;
	    }
	    
	    return ret;
	}
	
	private void saveNoteToDb() 
	{
		if (flagIntent == FLAG_CREATE || flagIntent == FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK) 
		{
			// add Note to Db
            LogUtil.i("tong-------create");
			String title = etNoteTitle.getText().toString();
			String content = etScanContent.getText().toString();
			String notebookName = tvTopic.getText().toString();
			
			if (!isNoteBookExist(mNoteBookRecord.getNoteBookName()))
			{
			    NoteBookRecordDao bookDao = new NoteBookRecordDao(this);
			    bookDao.add(mNoteBookRecord);
			}
			
			//new a NoteRecord object to be saved in database
			NoteRecord note = mScanRecord;//new NoteRecord();
			//get NoteBookRecord object include in NoteRecord
            NoteBookRecord noteBook = mNoteBookRecord;//= new NoteBookRecord();
			noteBook.setNoteBookName(notebookName);
			
			Log.d(TAG, "line 502notebookName is " + notebookName);
			if (!title.equals("")) 
			{
				note.setNoteTitle(title);
			} 
			else 
			{
				note.setNoteTitle("");
			}
			if (!content.equals("")) 
			{
				
				note.setNoteBookName(notebookName);
				note.setNoteContent(content);

				note.setCreateAddr(((HanvonApplication) getApplication()).getAddress());
				note.setCreateTime((HanvonApplication.noteCreateTime!=null&&HanvonApplication.noteCreateTime.length()>0) ? HanvonApplication.noteCreateTime : TimeUtil.getcurTime(TimeUtil.FORMAT_FULL));
				note.setWeather(((HanvonApplication) getApplication()).getWeather());
				
				note.setAddrDetail(((HanvonApplication) getApplication()).getAddrDetail());
				note.setNoteBook(noteBook);
				mScanRecordDao.add(note);
				Log.d(TAG, "saved note is :" + note.toString());
				
				//添加图片
				
				for(int i=0;i<mDataList.size();i++)
				{
				    NotePhotoRecord cphote = new NotePhotoRecord();
					cphote.setLocalUrl(mDataList.get(i).sourcePath);
					cphote.setNote(note);
					mPhotoRecordDao.add(cphote);
				}
				
				//保存该次选择的标签类型到sharedpreference
				if(sp==null)
				{
					sp = getSharedPreferences(CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
				}
				sp.edit().putString("lastTagSeclected", notebookName).commit();
			} 
			else 
			{
				UiUtil.showToast(this, "您的笔记内容为空，将不进行保存");
			}
		} 
		else 
		{
			LogUtil.i("tong-------edit");
			// update Note
			String title = etNoteTitle.getText().toString();
			String content = etScanContent.getText().toString();
			if (!title.equals("")) {
				mScanRecord.setNoteTitle(title);
			}
			if (!content.equals("")) {
				mScanRecord.setNoteContent(content);
				//for test
				String curNoteBookName =tvTopic.getText().toString();
				mScanRecord.setNoteBookName(curNoteBookName);
				//mScanRecord.setTopicId(curTopicId);
				//mScanRecord.setPhotos(JSON.toJSONString(mDataList));
				if(mScanRecord.getCreateTime()==null){
					mScanRecord.setCreateTime(TimeUtil.getcurTime(TimeUtil.FORMAT_FULL));
				}
				//(noteInfo, noteInfo.getS_id())
				mScanRecordDao.updataRecord(mScanRecord);
				
				//添加图片
				
				for(int i=0;i<mDataList.size();i++)
				{
				    NotePhotoRecord cphote = new NotePhotoRecord();
					cphote.setLocalUrl(mDataList.get(i).sourcePath);
					cphote.setNote(mScanRecord);
					mPhotoRecordDao.updataScanRecord(cphote);
				}
				
				//保存该次选择的标签类型到sharedpreference
				if(sp==null){
					sp = getSharedPreferences(
							CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
				}
				sp.edit().putString("lastTagSeclected",curNoteBookName ).commit();
			} else {
				UiUtil.showToast(this, "您的笔记内容为空，将不进行保存");
			}
		}
		// 清空选择图片的pref文件记录
		removeTempFromPref();
		mDataList = new ArrayList<ImageItem>();	
	}
	
	public void UploadFilesToHvnCloud(final String title,final String content,final List<ImageItem> mDataList){
		//final String result = null;
		new Thread() {
			@Override
			public void run() {
				String result = null;
				HvnCloudManager hvnCloud = new HvnCloudManager();
				try {
					result = hvnCloud.UploadNotesToHvnCloud(title, content, mDataList);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	
	/**
	 * 光标后追加文字
	 * 
	 * @param str
	 */
	public void appendText(String str) {
		LogUtil.i("AppendText:611--"+str);
		EditText curEditText = (EditText) getCurrentFocus();
		
		int index = curEditText.getSelectionStart();
		Editable edit = curEditText.getEditableText();
		
		
		//2000给予提示
		if((edit.length()+str.length())>2000)
		{
			UiUtil.showToast(this, "您的笔记内容已达最大！");
			return;

		}
//		int index = etScanContent.getSelectionStart();
//		Editable edit = etScanContent.getEditableText();
		 
		if(str.length()>=1)
		{
			int iLast = str.length()-1;

		    //如果是英文字母，需要在末尾追加空格
		    if((str.charAt(iLast)>='a' && str.charAt(iLast)<='z') || (str.charAt(iLast)>='A' && str.charAt(iLast)<='Z'))
		    {
		        str += " ";
		    }
		}

		
		if ("\b".equals(str)) {
			if (index == 0) {
				return;
			}
			edit.delete(index - 1, index);
			return;
		}
		if (index < 0 || index >= edit.length()) {
			edit.append(str);
		} else {
			edit.insert(index, str);
		}
	}
	
	
	private void showBottomDlg() {
		new CustomDialog.Builder(ScanNoteActivity.this)
		.setTitle(R.string.tip)
		.setMessage(R.string.msg_hint_delete_note)
		.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {

					}
				})
		.setPositiveButton(R.string.bn_sure,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						mScanRecordDao.deleteRecord(mScanRecord);
						mDataList = new ArrayList<ImageItem>();
						finish();
						overridePendingTransition(R.anim.act_delete_enter_anim,
								R.anim.act_delete_exit_anim);
					}
				}).show();
	}
	
	
	public void onClick(View v) {
	     switch (v.getId())
	        {
	    	case R.id.tvNoteTitle:
				//点击标题栏进入编辑模式
	    		
				toEditableMode(etNoteTitle);
				break;
	    	case R.id.tvNoteContent:
				//点击笔记内容进入编辑模式
				toEditableMode(etScanContent);
				break;
	            case R.id.ivShare:
                    
	            	//跳转到分享和上传功能界面
//	            	Intent intent = new Intent(ScanNoteActivity.this,ShareNoteActivity.class);
//	            	intent.putExtra("title", etNoteTitle.getText().toString());
//	            	startActivity(intent);
	            	if(!bShareClick)
	            	{
	            		
		            	//直接分享
		            	bShareFlag = true;
		            	String title1 = etNoteTitle.getText().toString();
			    	    String content1 = etScanContent.getText().toString();
			    			if (!content1.equals("")){
			    				bShareClick = true;
			    				pd = ProgressDialog.show(ScanNoteActivity.this, "", "生成连接中，请等待...");
			            	    UploadFilesToHvnCloud(title1,content1,mDataList);
			    			}
		                  
	            	}

	            	 break;
	            case R.id.ivInsertImage:
					new PopupWindows(ScanNoteActivity.this, mGridView);    
			       // imm.hideSoftInputFromWindow(etScanContent.getWindowToken(),0);
					break;
	            case R.id.ivScan:
	    			LogUtil.i("tong------------home_go_home");
	    			if (isConnected()) {
	    			    sendRecevieImagetoEpen();
	    			}
	    	
	            	break;
	            case R.id.ivDelete:
	            	showBottomDlg();
	            	break;
/*
	            	//Linkify.addLinks(mTextView, Linkify.WEB_URLS|Linkify.EMAIL_ADDRESSES); 
	            	
	            	String curContent = mTextView.getText().toString();
	            	System.out.println("tong-----------curContent:"+curContent);
	                Intent intent=new Intent(Intent.ACTION_SEND);    
	                intent.setType("image/*");    
	                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
	                intent.putExtra(Intent.EXTRA_TEXT,curContent);
	                //intent.putExtra(Intent.EXTRA_TEXT, "I have successfully share my message through my app (分享自汉王速录笔)");    
	              
	                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	                startActivity(Intent.createChooser(intent, getTitle()));  
	            	
	            	//Intent newIntent = new Intent(this, NewNoteBookActivity.class);
	    			//newIntent.setFlags(FLAG_CREATE);
	    			//startActivity(newIntent);
	            break;
	            
   */
	            case R.id.ivChangelag:
	            	//点击第二个图标，上传云
		            	String title = etNoteTitle.getText().toString();
		    			String content = etScanContent.getText().toString();
		    			if (!content.equals("")){
		    				pd = ProgressDialog.show(ScanNoteActivity.this, "", "文件上传中...");
		            	    UploadFilesToHvnCloud(title,content,mDataList);
		    			}
	            	
	            	//startChangeNoteBook();
	            	break;
	            	
	    		case R.id.come_back:
	    			saveNoteToDb();
	    			finish();
	    			break;
	    			
	    		case R.id.tvMyTopic:
	    			startChangeNoteBook();
	    		    break;
	        }
	}
	
	private void startChangeNoteBook()
	{
	    Log.d(TAG, "current notebook is " + mNoteBookRecord.toString());
	    Intent changeIntent = new Intent(this, ChangNoteBookActivity.class);
	    changeIntent.putExtra("NoteBook", mNoteBookRecord);
	    startActivityForResult(changeIntent, RESULT_CHAGNE_NOTEBOOK);
	}
	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveTempToPref();
	}

	
	private void getTempFromPref() {
		if(sp == null){
			sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		}		
		String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
		if (!TextUtils.isEmpty(prefStr)) {
			List<ImageItem> tempImages = JSON.parseArray(prefStr,
					ImageItem.class);
			mDataList = tempImages;
		}
	}
	
	private void saveTempToPref() {
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		String prefStr = JSON.toJSONString(mDataList);
		sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();

	}


	private void removeTempFromPref() {
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
	}
	
	private void showShare() {
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 

		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(getString(R.string.share_from_hanvon));
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl(strLinkPath);
		 // text是分享文本，所有平台都需要这个字段
		 String title = etNoteTitle.getText().toString();
		 if(title == "")
		 {
			 String strContent = etScanContent.getText().toString();
			 title = strContent;
		 }
		 oks.setText(title);
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
	
		 //oks.setImagePath("/data/data/com.hanvon.sulupen/res/drawable-mdpi/ic_launcher.png");
		 //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
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
	
	
	
	/** 
     * 分享功能 
     *  
     * @param context 
     *            上下文 
     * @param activityTitle 
     *            Activity的名字 
     * @param msgTitle 
     *            消息标题 
     * @param msgText 
     *            消息内容 
     * @param imgPath 
     *            图片路径，不分享图片则传null 
     */  
    public void shareMsg(String activityTitle, String msgTitle, String msgText,  
            String imgPath) {  
        Intent intent = new Intent(Intent.ACTION_SEND);  
        if (imgPath == null || imgPath.equals("")) {  
            intent.setType("text/plain"); // 纯文本  
        } else {  
            File f = new File(imgPath);  
            if (f != null && f.exists() && f.isFile()) {  
                intent.setType("image/jpg");  
                Uri u = Uri.fromFile(f);  
                intent.putExtra(Intent.EXTRA_STREAM, u);  
            }  
        }  
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);  
        intent.putExtra(Intent.EXTRA_TEXT, msgText);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        startActivity(Intent.createChooser(intent, activityTitle));  
    } 
	

	@Override
	protected void onResume() {
		super.onResume();
		acquireWakeLock();
		//@是否传入图片
		this.registerReceiver(btMsgReceiver, new IntentFilter(BluetoothIntenAction.ACTION_EPEN_RECEIVEIMG_CHANGE));
		//修复进入历史笔记后图片添加的问题.
		mAdapter = new ImagePublishAdapter(this, mDataList,true);
		mGridView.setAdapter(mAdapter);
		notifyDataChanged(); // 当在ImageZoomActivity中删除图片时，返回这里需要刷新
		//imm.hideSoftInputFromWindow(etNoteTitle.getWindowToken(),0);
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		this.unregisterReceiver(btMsgReceiver);
		updateCheckMode();
		saveTempToPref();
	}

	
	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {
			super(parent);
			View view = View.inflate(mContext, R.layout.item_popupwindow, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					takePhoto();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
		
				public void onClick(View v) {
					LogUtil.i("tong-------from bucket choose");
					Intent intent = new Intent(ScanNoteActivity.this,
							ImageBucketChooseActivity.class);
					intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
							getAvailableSize());
					startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}
	
	
	private int getAvailableSize() {
		int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
		if (availSize >= 0) {
			return availSize;
		}
		return 0;
	}

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void takePhoto() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File vFile = new File(Environment.getExternalStorageDirectory()
				+ "/sulupenimage/", String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		if (!vFile.exists()) {
			File vDirPath = vFile.getParentFile();
			vDirPath.mkdirs();
		} else {
			if (vFile.exists()) {
				vFile.delete();
			}
		}
		path = vFile.getPath();
		LogUtil.i("tong------path:"+path);
		Uri cameraUri = Uri.fromFile(vFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		    case TAKE_PICTURE:
			if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
					&& resultCode == -1 && !TextUtils.isEmpty(path))
			{
				ImageItem item = new ImageItem();
				item.sourcePath = path;
				mDataList.add(item);
			}
			break;
			
		    case  RESULT_CHAGNE_NOTEBOOK:
		        if (RESULT_OK == resultCode)
		        {
		            //Log.d(TAG, "get change note book result");
		            NoteBookRecord notebook = (NoteBookRecord) data.getSerializableExtra("NewNoteBook");
		            Log.d(TAG, "get change note book result, notebook is " + notebook.toString());
		            mNoteBookRecord = notebook;
		            tvTopic.setText(mNoteBookRecord.getNoteBookName());
		            mScanRecord.setNoteBook(mNoteBookRecord);
		        }
		        else
		        {
		            Log.d(TAG, "receive reslut code is " + resultCode);
		        }
		    break;
			
		}
		
	}


	
	@SuppressWarnings("deprecation")
	private void acquireWakeLock() {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
					"myLock");
			wakeLock.acquire();
		}
	}

	private void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}
	
	private void notifyDataChanged() {
		LogUtil.i("tong------notifyDataChanged");
		// TODO Auto-generated method stub
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		scanNoteAct=null;
		changeRecordMode(1);
	
		// 保存笔记操作
		releaseWakeLock();
	
	}
}
