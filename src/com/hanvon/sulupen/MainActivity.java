
package com.hanvon.sulupen;


import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.bluetooth.BluetoothChatService;
import com.hanvon.bluetooth.BluetoothDetail;
import com.hanvon.bluetooth.BluetoothIntenAction;
import com.hanvon.bluetooth.BluetoothMsgReceive;
import com.hanvon.bluetooth.BluetoothSearch;
import com.hanvon.bluetooth.BluetoothService;
//import com.hanvon.bluetooth.HardWareDownFille;
import com.hanvon.sulupen.adapter.NoteBookListAdapter;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;
import com.hanvon.sulupen.login.LoginActivity;
import com.hanvon.sulupen.login.ShowUserMessage;
import com.hanvon.sulupen.utils.CircleImageView;
import com.hanvon.sulupen.utils.ConnectionDetector;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.MD5Util;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;


@SuppressLint("NewApi") public class MainActivity extends Activity implements OnClickListener, OnLongClickListener
{
    private final String TAG = "MainActivity";
    private TextView mTitle;
    private ImageView mLeftBtn;
    private TextView mRightBtn;
    private LinearLayout mNewNoteBook;
    private LinearLayout mEditNoteBook;
    private LinearLayout mSearchNoteBook;
    private ImageView mNewNote;
    private ListView mBooksList;
    private TextView mEmptyNoteBook;
    private ImageView mEpen;

    SlidingMenu leftMenu;
    private CircleImageView mIvLogin;
    private TextView TVusername;
    private TextView TVnickname;
    private RelativeLayout mRlSetting;
    private RelativeLayout mRlCloudSync;
    private RelativeLayout mRlCount;
    
    
    public final static int FLAG_EDIT = 1;
	public final static int FLAG_CREATE = 2;
	public final static int FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK = 3;
	
	
	
	private NoteBookRecordDao mNoteBookRecordDao;
	private List<NoteBookRecord> mNoteBookList;
	private NoteBookListAdapter mNoteBookAdapter;
	//private List<NoteBookInfo> mNoteBookList;
    
	private BluetoothMsgReceive btMsgReceiver;
	private boolean isConnectedEpen;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			LogUtil.i("INTO Main handlemessage-------------------");
			switch (msg.what) {
			case BluetoothMsgReceive.BT_CONNECTED:
				mEpen.setBackgroundResource(R.drawable.epen_manager);
				break;
			case BluetoothMsgReceive.BT_DISCONNECT:
				mEpen.setBackgroundResource(R.drawable.epen_manager_nor);
				break;
			case BluetoothMsgReceive.HARD_WARE_UPDATE:
				new UpdateAppService(MainActivity.this,2).CreateInform(HanvonApplication.HardUpdateUrl);
				break;
				
			}
		};
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initLeftMenu();
        
        initDatas();
        
        initViews();

        if (new ConnectionDetector(this).isConnectingTOInternet()) {
            boolean autoUpdateflag = Settings.getKeyVersionUpdate(MainActivity.this);
		    if (autoUpdateflag){
                SoftUpdate updateInfo = new SoftUpdate(this,0);
	            updateInfo.checkVersion();
		    }
        }
		
        if (BluetoothService.getServiceInstance() != null){
		    if (!isConnected()){
                LogUtil.i("--------Before Call BluetoothCheck（）-------1-------");
		        BluetoothCheck(2);
		    }
        }else{
        	LogUtil.i("--------Before Call BluetoothCheck（）-------3-------");
        	BluetoothCheck(1);
        }
    }

    @Override
	protected void onStart() {
		super.onStart();
		LogUtil.i("---------MAIN---1---------------------"+HanvonApplication.hvnName);
		 IntentFilter mFilter = new IntentFilter(BluetoothIntenAction.ACTION_EPEN_BATTERY_CHANGE);
			mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_BT_CONNECTED);
			mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_BT_DISCONNECT);
			mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_HARD_WARE_UPDATE);
			this.registerReceiver(btMsgReceiver, mFilter);
			if (BluetoothService.getServiceInstance() != null){
		    	if (BluetoothService.getServiceInstance().getBluetoothChatService()
			    		  .getState() == BluetoothChatService.STATE_CONNECTED) {
				    mEpen.setBackgroundResource(R.drawable.epen_manager);
			    } else {
				    mEpen.setBackgroundResource(R.drawable.epen_manager_nor);
			    }
		
			}else{
				mEpen.setBackgroundResource(R.drawable.epen_manager_nor);
			}

			if (!HanvonApplication.hvnName.equals("")){
				LogUtil.i("---------MAIN---true---------------------");
        	    ShowUserInfo();
        	}else{
        		LogUtil.i("---------MAIN---false---------------------");
        		TVusername.setText("");
		    	TVnickname.setText("未登录");
        	//	mIvLogin.setBackgroundResource(R.drawable.logicon);
        	//	mIvLogin.setImageResource(R.drawable.logicon);
		    	if (HanvonApplication.userFlag == 0){
		    		mIvLogin.setBackgroundResource(R.drawable.logicon);
		    	}else{
		    		mIvLogin.setImageDrawable((getResources().getDrawable(R.drawable.logicon)));
		    	}
		    	
		    	//(getResources().getDrawable(R.drawable.logicon));
        	}
			
			SharedPreferences mSharedPreferences=getSharedPreferences("Blue", Activity.MODE_MULTI_PROCESS);
			if (mSharedPreferences != null){
			    HanvonApplication.isDormant = mSharedPreferences.getBoolean("isDormant", false);
			}
	}
    @TargetApi(Build.VERSION_CODES.ECLAIR) @SuppressLint("NewApi") public void BluetoothCheck(int isflag){
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		boolean flag = false;
		BluetoothDevice hanBluetooth = null;
		for(int i=0; i<devices.size(); i++)    
		{
		    BluetoothDevice device = (BluetoothDevice) devices.iterator().next();    
		    LogUtil.i("===============devicename:==="+device.getName());
		    if (device.getName().indexOf("hanvon-scanpen") != -1){
		    	flag = true;
		    	hanBluetooth = device;
		    }
		}
		if (flag){
			String name = hanBluetooth.getName();
			String deviceInfo = name + "\n" + hanBluetooth.getAddress();

			Intent intent = new Intent();
			intent.putExtra("device", deviceInfo);
			intent.setClass(MainActivity.this, BluetoothSearch.class);  
			MainActivity.this.startActivity(intent);
		}else{
			if (isflag == 0){
			    Intent blueSearchIntent = new Intent(this, BluetoothSearch.class);
    		    startActivity(blueSearchIntent);
			}
		}
	}
    
    
    private String getCurVersion() throws Exception 
	{
		PackageManager packageManager = this.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
		return packInfo.versionName;
	}
    
    public void initLeftMenu()
    {
    	// configure the SlidingMenu
    	leftMenu = new SlidingMenu(this);
    	leftMenu.setMode(SlidingMenu.LEFT);
    	//设置触摸屏幕的模式
    			
    	leftMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    	leftMenu.setShadowWidthRes(R.dimen.shadow_width);
    	leftMenu.setShadowDrawable(R.drawable.shadow);

    	//设置滑动菜单视图的宽度
    	leftMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
    	//设置渐入渐出效果的值
    	leftMenu.setFadeDegree(0.35f);
    	
    	/**
    	 * SLIDING_WINDOW will include the Title/ActionBar in the content
		 *section of the SlidingMenu, while SLIDING_CONTENT does not.
    	*/
    	//把滑动菜单添加进所有的Activity中，可选值SLIDING_CONTENT ， SLIDING_WINDOW
    	leftMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    	//为侧滑菜单设置布局
    	leftMenu.setMenu(R.layout.leftmenu);
    	
    	mIvLogin = (CircleImageView) findViewById(R.id.iv_login_icon);
    	mIvLogin.setOnClickListener(this);

    	TVusername = (TextView)findViewById(R.id.ivUserName);
    	TVnickname = (TextView)findViewById(R.id.ivhvnUserName);
    	
    	mRlSetting = (RelativeLayout) findViewById(R.id.rl_setting);
    	mRlSetting.setOnClickListener(this);
    	
    	mRlCount = (RelativeLayout) findViewById(R.id.rl_count);
    	mRlCount.setOnClickListener(this);
    	 
    	mRlCloudSync = (RelativeLayout) findViewById(R.id.rl_cloud);
    	mRlCloudSync.setOnClickListener(this);
    	
    	TextView version = (TextView) findViewById(R.id.tv_version);
    	
    	try 
		{
    		version.setText("Version" + getCurVersion());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
    	

    	ShowUserInfo();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") 
    public void ShowUserInfo(){
    	String email = "",phone = "",hvnname = "",figureurl = "",username = "";
		SharedPreferences mSharedPreferences=this.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
		int flag = mSharedPreferences.getInt("flag", 0);
		HanvonApplication.userFlag = flag;
		String nickname=mSharedPreferences.getString("nickname", "");
		boolean isHasNick = mSharedPreferences.getBoolean("isHasNick", true);
		if (flag == 0){
		//    email = mSharedPreferences.getString("email", "");
		//    phone = mSharedPreferences.getString("phone", "");
		    username = mSharedPreferences.getString("username", "");
		}else{
		    figureurl=mSharedPreferences.getString("figureurl", "");	
		    hvnname = mSharedPreferences.getString("username", "");
		}
		int status = mSharedPreferences.getInt("status", 0);
		LogUtil.i("flag:"+flag+"  nickname:"+nickname+"   status:"+status+"  username:"+hvnname+"  figureurl:"+figureurl);

		if (status == 1){
		    if (flag == 0){
			    if(!nickname.isEmpty()){
			    	TVusername.setText(nickname);
			    	TVnickname.setText(username);
				     HanvonApplication.hvnName = username;
				     HanvonApplication.strName = nickname;
				     mIvLogin.setBackgroundResource(R.drawable.login_head_default);
				     LogUtil.i("hvnName:"+username+"  strName:"+nickname);
			    }else{
			    	TVusername.setText("");
			    	TVnickname.setText(username);
				     HanvonApplication.hvnName = username;
				     HanvonApplication.strName = nickname;
				     mIvLogin.setBackgroundResource(R.drawable.login_head_default);
				     LogUtil.i("hvnName:"+username+"  strName:"+nickname);
			    }
		    }
		    if (flag == 1 || flag == 2){
			    if(!nickname.isEmpty()){
			    	 TVusername.setText(nickname);
			    	 TVnickname.setText(hvnname);
				     HanvonApplication.strName = nickname;
				     HanvonApplication.hvnName = hvnname;
			    }else{
			    	 TVusername.setText("");
			    	 TVnickname.setText(hvnname);
				     HanvonApplication.strName = nickname;
				     HanvonApplication.hvnName = hvnname;
			    }
			    if(!figureurl.isEmpty()){
				    BitmapUtils  bitmapUtils = new  BitmapUtils(this);
		            bitmapUtils.configDefaultLoadingImage(R.drawable.logicon);
		            bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(
		        		    this).scaleDown(3));
		            bitmapUtils.configDefaultShowOriginal(true);
				    bitmapUtils.display(((ImageView)findViewById(R.id.iv_login_icon)),figureurl);
			    }
		    }
		}else{
			TVusername.setText("");
	    	TVnickname.setText("未登录");
	    	if (HanvonApplication.userFlag == 0){
	    		mIvLogin.setBackgroundResource(R.drawable.logicon);
	    	}else{
	    		mIvLogin.setImageDrawable((getResources().getDrawable(R.drawable.logicon)));
	    	}
	    	//mIvLogin.setImageResource(R.drawable.logicon);
		}
    }
    
    public void initDatas()
    {
    	mNoteBookRecordDao = new NoteBookRecordDao(this);
    	mNoteBookList = mNoteBookRecordDao.getAllNoteBooks();
        //mDataList = mScanRecordDao.getALLRecordOrderByTime();
        Log.d(TAG, "mNoteBookList.size is " + mNoteBookList.size());
    }
    
    public void initViews()
    {
        mTitle = (TextView) findViewById(R.id.tv_title);
        mLeftBtn = (ImageView) findViewById(R.id.tv_leftbtn);
        mRightBtn = (TextView) findViewById(R.id.tv_rightbtn);
        
        mNewNoteBook = (LinearLayout) findViewById(R.id.ll_new);
        mEditNoteBook = (LinearLayout) findViewById(R.id.ll_edit);
        mSearchNoteBook = (LinearLayout) findViewById(R.id.ll_search);
        
        mNewNote = (ImageView) findViewById(R.id.iv_newnote);
        
        mBooksList = (ListView) findViewById(R.id.lv_notebooklist);
        mEmptyNoteBook = (TextView) findViewById(R.id.tv_showempty);
        mEpen = (ImageView)findViewById(R.id.iv_rightbtn);
        
        mLeftBtn.setOnClickListener(this);
        mNewNoteBook.setOnClickListener(this);
        mEditNoteBook.setOnClickListener(this);
        mSearchNoteBook.setOnClickListener(this);
        mNewNote.setOnClickListener(this);
        mEpen.setOnClickListener(this);
        mEpen.setOnLongClickListener(this);
        
        mNoteBookAdapter = new NoteBookListAdapter(this, mNoteBookList);
        mBooksList.setAdapter(mNoteBookAdapter);
        mBooksList.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id)
            {
                Log.d(TAG, "item " + position + " clicked");
                NoteBookRecord noteBook = mNoteBookList.get(position);
                Intent newIntent = new Intent(MainActivity.this, NoteBookListActivity.class);;
                newIntent.putExtra("NoteBook", noteBook);
                startActivity(newIntent);
            }
        });
        
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

        btMsgReceiver = new BluetoothMsgReceive(mHandler);
    }
    
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_new:
            	Intent newIntent = new Intent(this, NewNoteBookActivity.class);
    			newIntent.putExtra("CreateNoteBook", FLAG_CREATE);
    			startActivity(newIntent);
            break;
            
            case R.id.ll_edit:
                Intent intent = new Intent(this, EditNoteBookActivity.class);
                startActivity(intent);
                break;
                
            case R.id.ll_search:
            	Intent search = new Intent(this, SearchActivity.class);
            	startActivity(search);
                break;
            
            case R.id.iv_newnote:
            	NoteBookRecord defaultNoteBook = new NoteBookRecord();
            	defaultNoteBook.setNoteBookName("笔记本");
				defaultNoteBook.setNoteBookId(MD5Util.md5("笔记本"));
                Intent newNoteIntent = new Intent(this, ScanNoteActivity.class);
                String flagStr = Integer.toString(FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK);
                newNoteIntent.putExtra("CreatFlag", flagStr);
               // newNoteIntent.setFlags(FLAG_CREATE_NOTE_WITH_DEFAULT_NOTEBOOK);
                newNoteIntent.putExtra("NoteBook", defaultNoteBook);
                startActivityForResult(newNoteIntent, 1);
                break;
            case R.id.iv_rightbtn:
            	if (isConnected()) {
            	    Intent blueDetailIntent = new Intent(this, BluetoothDetail.class);
        	    	startActivity(blueDetailIntent);
            	}else{
            		LogUtil.i("--------Before Call BluetoothCheck（）---------2-----");
            		BluetoothCheck(0);
            		//Intent blueSearchIntent = new Intent(this, BluetoothSearch.class);
            		//startActivity(blueSearchIntent);
            	}
            	break;
            case R.id.tv_leftbtn:
            	leftMenu.toggle();
                //Intent newIntent1 = new Intent(this, LoginActivity.class);
    		    //startActivity(newIntent1);
    		break;
    		
            case R.id.iv_login_icon:
            	if (HanvonApplication.hvnName.equals("")){
            	    Intent newIntent1 = new Intent(this, LoginActivity.class);
    		        startActivity(newIntent1);
            	}else{
            		Intent newIntent1 = new Intent(this, ShowUserMessage.class);
    		        startActivity(newIntent1);
            	}
            break;
            
            case R.id.rl_setting:
                Intent settingItent = new Intent(this, SettingActivity.class);
                startActivity(settingItent);
            break;
            
            case R.id.rl_count:
            	//Toast.makeText(this, "此版本暂不支持该功能！", Toast.LENGTH_SHORT).show();
            	Intent statistics = new Intent(this, StatisticsActivity.class);
            	startActivity(statistics);
            break;
            
            case R.id.rl_cloud:
            	Toast.makeText(this, "此版本暂不支持该功能！", Toast.LENGTH_SHORT).show();  
            break;    
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	mNoteBookList = mNoteBookRecordDao.getAllNoteBooks();
    	Log.d(TAG, "in onActivityResult func, mNoteBookList.size is " + mNoteBookList.size());
    	//if (null == mNoteBookAdapter)
    	//{
    		mNoteBookAdapter = new NoteBookListAdapter(this, mNoteBookList);
    	//}
    	
    	mBooksList.setAdapter(mNoteBookAdapter);
    	
    	mNoteBookAdapter.notifyDataSetChanged();
        //String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
        //Log.i(TAG, result);
    }
    
    @Override
	protected void onResume() 
    {
		super.onResume();
		Log.d(TAG, "on resume here");
		mNoteBookList = mNoteBookRecordDao.getAllNoteBooks();
		Log.d(TAG, "note book size is " + mNoteBookList.size());
		mNoteBookAdapter = new NoteBookListAdapter(this, mNoteBookList);
		mBooksList.setAdapter(mNoteBookAdapter);
    	
		
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
		
    	mNoteBookAdapter.notifyDataSetChanged();
    }	
    
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */

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
    
    private boolean isConnected() {
		return BluetoothService.getServiceInstance().getBluetoothChatService()
				.getState() == BluetoothChatService.STATE_CONNECTED ? true
				: false;
	}
    @Override
	protected void onDestroy() {
		unregisterReceiver(btMsgReceiver);
		LogUtil.i("-------onDestroy in Main Function--------------");
		super.onDestroy();
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		SharedPreferences mSharedPreferences=getSharedPreferences("Blue", Activity.MODE_MULTI_PROCESS);
		if (mSharedPreferences != null){
		    HanvonApplication.isDormant = mSharedPreferences.getBoolean("isDormant", false);
		}
		
		if (HanvonApplication.isDormant){
			Toast.makeText(this, "蓝牙扫描笔进入休眠状态，请按power键进行唤醒！", Toast.LENGTH_SHORT).show();
			return false;
		}
		switch (v.getId()) {
		case R.id.iv_rightbtn:
			if (isConnected()) {// 如果连接
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setMessage(R.string.msg_stop_connected);
				dialog.setCancelable(false);
				dialog.setNegativeButton(R.string.button_cancel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								})
						.setPositiveButton(R.string.ensure,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										stopConnect();
									}
								}).show();
			} else {
				// 如果没有连接就去搜索连接
				startActivity(new Intent(this,
						BluetoothSearch.class));
			}

			break;

		default:
			break;
		}
		return false;
	}
	
	/**
	 * 断开连接
	 */
	private void stopConnect() {
		BluetoothService.getServiceInstance().getBluetoothChatService().stop();
	}

}
