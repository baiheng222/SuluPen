
package com.hanvon.sulupen;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.bluetooth.BluetoothChatService;
import com.hanvon.bluetooth.BluetoothDetail;
import com.hanvon.bluetooth.BluetoothIntenAction;
import com.hanvon.bluetooth.BluetoothMsgReceive;
import com.hanvon.bluetooth.BluetoothSearch;
import com.hanvon.bluetooth.BluetoothService;
import com.hanvon.sulupen.adapter.NoteBookListAdapter;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.NoteBookListActivity;
import com.hanvon.sulupen.login.LoginActivity;

import java.util.List;
import java.util.Set;


public class MainActivity extends Activity implements OnClickListener, OnLongClickListener
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
    private ImageView mEpen;
    
    public final static int FLAG_EDIT = 1;
	public final static int FLAG_CREATE = 2;
	
	private NoteBookRecordDao mNoteBookRecordDao;
	private List<NoteBookRecord> mNoteBookList;
	private NoteBookListAdapter mNoteBookAdapter;
	//private List<NoteBookInfo> mNoteBookList;
    
	private BluetoothMsgReceive btMsgReceiver;
	private boolean isConnectedEpen;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BluetoothMsgReceive.BT_CONNECTED:
				mEpen.setBackgroundResource(R.drawable.epen_manager);
				break;
			case BluetoothMsgReceive.BT_DISCONNECT:
				mEpen.setBackgroundResource(R.drawable.epen_manager_nor);
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

        initDatas();
        
        initViews();
    }

    @Override
	protected void onStart() {
		super.onStart();
		 IntentFilter mFilter = new IntentFilter(
					BluetoothIntenAction.ACTION_EPEN_BATTERY_CHANGE);
			mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_BT_CONNECTED);
			mFilter.addAction(BluetoothIntenAction.ACTION_EPEN_BT_DISCONNECT);
			this.registerReceiver(btMsgReceiver, mFilter);
		//	if (BluetoothService.getServiceInstance().getBluetoothChatService()
		//			.getState() == BluetoothChatService.STATE_CONNECTED) {
		//		mEpen.setBackgroundResource(R.drawable.epen_manager);
		//	} else {
		//		mEpen.setBackgroundResource(R.drawable.epen_manager_nor);
		//	}
			
		//	if (!isConnected()){
		//	    BluetoothCheck();
		//	}
	}
    @TargetApi(Build.VERSION_CODES.ECLAIR) @SuppressLint("NewApi") public void BluetoothCheck(){
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
        mLeftBtn = (TextView) findViewById(R.id.tv_leftbtn);
        mRightBtn = (TextView) findViewById(R.id.tv_rightbtn);
        
        mNewNoteBook = (TextView) findViewById(R.id.tv_newnotebook);
        mEditNoteBook = (TextView) findViewById(R.id.tv_editnotebook);
        mSearchNoteBook = (TextView) findViewById(R.id.tv_searchbtn);
        
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
        	
        	mNoteBookAdapter = new NoteBookListAdapter(this, mNoteBookList);
        	mBooksList.setAdapter(mNoteBookAdapter);
        	mBooksList.setOnItemClickListener(new OnItemClickListener()
        	{
        	    @Override
        	    public void onItemClick(AdapterView <?> parent, View view, int position, long id)
        	    {
        	        Log.d(TAG, "item " + position + " clicked");
        	        NoteBookRecord noteBook = mNoteBookList.get(position);
        	        Intent newIntent = new Intent(MainActivity.this, NoteBookListActivity.class);
                    //newIntent.setFlags(FLAG_CREATE);
        	        newIntent.putExtra("NoteBook", noteBook);
                    //newIntent.putExtra("NoteBookName", noteBook.getNoteBookName());
                    startActivity(newIntent);
        	    }
        	});
        }

        btMsgReceiver = new BluetoothMsgReceive(mHandler);
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
                Intent intent = new Intent(this, EditNoteBookActivity.class);
                startActivity(intent);
                break;
                
            case R.id.tv_searchbtn:
                break;
            
            case R.id.iv_newnote:
                Intent newNoteIntent = new Intent(this, NoteDetailActivity.class);
                newNoteIntent.setFlags(FLAG_CREATE);
                startActivityForResult(newNoteIntent, 1);
                break;
            case R.id.iv_rightbtn:
            	if (isConnected()) {
            	    Intent blueDetailIntent = new Intent(this, BluetoothDetail.class);
        	    	startActivity(blueDetailIntent);
            	}else{
            		Intent blueSearchIntent = new Intent(this, BluetoothSearch.class);
            		startActivity(blueSearchIntent);
            	}
            	break;
            case R.id.tv_leftbtn:
                Intent newIntent1 = new Intent(this, LoginActivity.class);
    		    startActivity(newIntent1);
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
		mNoteBookAdapter = new NoteBookListAdapter(this, mNoteBookList);
		mBooksList.setAdapter(mNoteBookAdapter);
    	
    	mNoteBookAdapter.notifyDataSetChanged();
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
    
    private boolean isConnected() {
		return BluetoothService.getServiceInstance().getBluetoothChatService()
				.getState() == BluetoothChatService.STATE_CONNECTED ? true
				: false;
	}
    @Override
	protected void onDestroy() {
		unregisterReceiver(btMsgReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
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
