package com.hanvon.sulupen.utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.db.bean.StatisticsFunction;
import com.hanvon.sulupen.db.dao.StatisticsFunctionDao;
import com.hanvon.sulupen.net.HttpClientHelper;

public class StatisticsUtils {

	private final static String UploadTime = "UploadTime";
	private final static String LastSaveTime = "LastSaveTime";
	private final static String RecordTime = "RecordTime";
	/***********LoginPage*********************/
	private final static String RegisterButton = "RegisterBtn";
	private final static String HvnLoginButton = "HvnLoginBtn";
	private final static String QQLoginButton = "QQLoginBtn";
	private final static String WXLoginButton = "WXLoginBtn";
	
	/***********MainPage*********************/
	private final static String CrtNoteBookButton = "CreateNoteBookBtn";
	private final static String SearchNoteBookButton = "SearchNoteBookBtn";
	private final static String ConnectedButton = "BlueConnectedBtn";
	private final static String CrtNoteRecordButton = "CreateNoteRecodeBtn";
	private final static String StatisticsButton = "StatisticsBtn";
	private final static String CloudSyncButton = "CloudSyncBtn";
	
	/***********EditNoteBookPage*************/
	private final static String DelNoteBookButton = "DeleteNoteBookBtn";
	private final static String RemNoteBookButton = "RenameNoteBookBtn";
	
	/***********EditNoteRecodePage**********/
	private final static String ShareButton = "ShareBtn";
	private final static String DeleteButton = "DeleteBtn";
	private final static String AddImageButton = "AddImageSBtn";
	private final static String RectificationButton = "RectificationBtn";
	private final static String ScanMode = "ScanMode";
	private final static String KeyboardMode = "KeyboardMode";
	
	/***********SettingPage****************/
	private final static String IMESettingButton = "ImeSettingBtn";
	
	/***********SeleteNoteBookPage********/
	private final static String BatchShareButton = "BatchShareBtn";
	private final static String ChangeNoteBookButton = "ChangeNoteBookBtn";
	private final static String BatchDeleteButton = "BatchDeleteBtn";
	
	/************statisticsPages*********/
	private final static String LoginPage = "LoginPage";
	private final static String MainPage = "MainPage";
	private final static String EditNoteBookPage = "EditNoteBookPage";
	private final static String EditNoteRecodePage = "EditNoteRecodePage";
	private final static String SettingPage = "SettingPage";
	private final static String SeleteNoteBookPage = "SeleteNoteBookPage";
	
	/************************************/
	private static int mRegister;
	private static int mHvnLogin;
	private static int mQQLogin;
	private static int mWXLogin;
	private static int mCrtNoteBook;
	private static int mSearchNoteBook;
	private static int mBlueConnected;
	private static int mCrtNoteRecord;
	private static int mStatistics;
	private static int mCloudSync;
	private static int mDeleteNoteBook;
	private static int mRemNoteBook;
	private static int mShare;
	private static int mDelete;
	private static int mAddImage;
	private static int mRectification;
	private static int mScanMode;
	private static int mKeyboardMode;
	private static int mImeSettting;
	private static int mBatchShare;
	private static int mChangeNoteBook;
	private static int mBatchDelete;
    private static int mLoginPage;
    private static int mMainPage;
    private static int mEditNoteBookPage;
    private static int mEditNoteRecodePage;
    private static int mSettingPage;
    private static int mSeleteNoteBookPage; 
    
	
	private static StatisticsUtils mInstance = null;
	private static int mRefCount = 0;
	public static SharedPreferences mSharedPref = null;

	public static ProgressDialog pd;
	
	
	private static StatisticsFunctionDao mFunctionDao;
	private static List<StatisticsFunction> mFunctionList;
	
	
	protected StatisticsUtils(SharedPreferences pref) {
		mSharedPref = pref;
		initConfs();
	}

	public static StatisticsUtils getInstance(SharedPreferences pref) {
		if (mInstance == null) {
			mInstance = new StatisticsUtils(pref);
		}
		assert (pref == mSharedPref);
		mRefCount++;
		return mInstance;
	}
	
	public static void releaseInstance() {
		mRefCount--;
		if (mRefCount == 0) {
			mInstance = null;
		}
	}

	private void initConfs(){
		mRegister = mSharedPref.getInt(RegisterButton, 0);
		mHvnLogin = mSharedPref.getInt(HvnLoginButton, 0);
		mQQLogin = mSharedPref.getInt(QQLoginButton, 0);
		mWXLogin = mSharedPref.getInt(WXLoginButton, 0);
		mCrtNoteBook = mSharedPref.getInt(CrtNoteBookButton, 0);
		mSearchNoteBook = mSharedPref.getInt(SearchNoteBookButton, 0);
		mBlueConnected = mSharedPref.getInt(ConnectedButton, 0);
		mCrtNoteRecord = mSharedPref.getInt(CrtNoteRecordButton, 0);
		mStatistics = mSharedPref.getInt(StatisticsButton, 0);
		mCloudSync = mSharedPref.getInt(CloudSyncButton, 0);
		mDeleteNoteBook = mSharedPref.getInt(DelNoteBookButton, 0);
		mRemNoteBook = mSharedPref.getInt(RemNoteBookButton, 0);
		mShare = mSharedPref.getInt(ShareButton, 0);
		mDelete = mSharedPref.getInt(DeleteButton, 0);
		mAddImage = mSharedPref.getInt(AddImageButton, 0);
		mRectification = mSharedPref.getInt(RectificationButton, 0);
		mScanMode = mSharedPref.getInt(ScanMode, 0);
		mKeyboardMode = mSharedPref.getInt(KeyboardMode, 0);
		mImeSettting = mSharedPref.getInt(IMESettingButton, 0);
		mBatchShare = mSharedPref.getInt(BatchShareButton, 0);
		mChangeNoteBook = mSharedPref.getInt(ChangeNoteBookButton, 0);
		mBatchDelete = mSharedPref.getInt(BatchDeleteButton, 0);

		mLoginPage = mSharedPref.getInt(LoginPage, 0);
		mMainPage = mSharedPref.getInt(MainPage, 0);
		mEditNoteBookPage = mSharedPref.getInt(EditNoteBookPage, 0);
		mEditNoteRecodePage = mSharedPref.getInt(EditNoteRecodePage, 0);
		mSettingPage = mSharedPref.getInt(SettingPage, 0);
		mSeleteNoteBookPage = mSharedPref.getInt(SeleteNoteBookPage, 0);
		
	}

	private static void Init(){
	//	Editor editor = mSharedPref.edit();
	//	editor.clear();
	//	editor.commit();

		mRegister = 0;
		mHvnLogin = 0;
		mQQLogin = 0;
		mWXLogin = 0;
		mCrtNoteBook = 0;
		mSearchNoteBook = 0;
		mBlueConnected = 0;
		mCrtNoteRecord = 0;
		mStatistics = 0;
		mCloudSync = 0;
		mDeleteNoteBook = 0;
		mRemNoteBook = 0;
		mShare = 0;
		mDelete = 0;
		mAddImage = 0;
		mRectification = 0;
		mScanMode = 0;
		mKeyboardMode = 0;
		mImeSettting = 0;
		mBatchShare = 0;
		mChangeNoteBook = 0;
		mBatchDelete = 0; 
		mLoginPage = 0;
	    mMainPage = 0;
	    mEditNoteBookPage = 0;
	    mEditNoteRecodePage = 0;
	    mSettingPage = 0;
	    mSeleteNoteBookPage = 0; 
	    
	    Editor editor = mSharedPref.edit();
	    for(int i = 0;i < 24;i++){
	    	editor.putInt(i+"", 0);
		}
	    editor.putString(LastSaveTime, TimeUtil.getCurDate());
	    editor.commit();
	}
	
	public static void IncreaseRegister(){
		mRegister++;
	}
	public static void IncreaseHvnLogin(){
		mHvnLogin++;
	}
	public static void IncreaseQQLogin(){
		mQQLogin++;
	}
	public static void IncreaseWXLogin(){
		mWXLogin++;
	}
	public static void IncreaseCrtNoteBook(){
		mCrtNoteBook++;
	}
	public static void IncreaseSearchNoteBook(){
		mSearchNoteBook++;
	}
	public static void IncreaseBlueConnected(){
		mBlueConnected++;
	}
	public static void IncreaseCrtNoteRecord(){
		mCrtNoteRecord++;
	}
	public static void IncreaseStatistics(){
		mStatistics++;
	}
	public static void IncreaseCloudSync(){
		mCloudSync++;
	}
	public static void IncreaseDeleteNoteBook(){
		mDeleteNoteBook++;
	}
	public static void IncreaseRemNoteBook(){
		mRemNoteBook++;
	}
	public static void IncreaseShare(){
		mShare++;
	}
	public static void IncreaseDelete(){
		mDelete++;
	}
	public static void IncreaseAddImage(){
		mAddImage++;
	}
	public static void IncreaseRectification(){
		mRectification++;
	}
	public static void IncreaseScanMode(){
		mScanMode++;
	}
	public static void IncreaseKeyboardMode(){
		mKeyboardMode++;
	}
	public static void IncreaseImeSettting(){
		mImeSettting++;
	}
	public static void IncreaseBatchShare(){
		mBatchShare++;
	}
	public static void IncreaseChangeNoteBook(){
		mChangeNoteBook++;
	}
	public static void IncreaseBatchDelete(){
		mBatchDelete++;
	}
	public static void IncreaseLoginPage(){
		mLoginPage++;
	}
	public static void IncreaseMainPage(){
		mMainPage++;
	}
	public static void IncreaseEditNoteBookPage(){
		mEditNoteBookPage++;
	}
	public static void IncreaseEditNoteRecodePage(){
		mEditNoteRecodePage++;
	}
	public static void IncreaseSettingPage(){
		mSettingPage++;
	}
	public static void IncreaseSeleteNoteBookPage(){
		mSeleteNoteBookPage++;
	}
	
	
	public static int GetRegister(){
		return mRegister;
	}
	public static int GetHvnLogin(){
		return mHvnLogin;
	}
	public static int GetQQLogin(){
		return mQQLogin;
	}
	public static int GetWXLogin(){
		return mWXLogin;
	}
	public static int GetCrtNoteBook(){
		return mCrtNoteBook;
	}
	public static int GetSearchNoteBook(){
		return mSearchNoteBook;
	}
	public static int GetBlueConnected(){
		return mBlueConnected;
	}
	public static int GetCrtNoteRecord(){
		return mCrtNoteRecord;
	}
	public static int GetStatistics(){
		return mStatistics;
	}
	public static int GetCloudSync(){
		return mCloudSync;
	}
	public static int GetDeleteNoteBook(){
		return mDeleteNoteBook;
	}
	public static int GetRemNoteBook(){
		return mRemNoteBook;
	}
	public static int GetShare(){
		return mShare;
	}
	public static int GetDelete(){
		return mDelete;
	}
	public static int GetAddImage(){
		return mAddImage;
	}
	public static int GetRectification(){
		return mRectification;
	}
	public static int GetScanMode(){
		return mScanMode;
	}
	public static int GetKeyboardMode(){
		return mKeyboardMode;
	}
	public static int GetImeSettting(){
		return mImeSettting;
	}
	public static int GetBatchShare(){
		return mBatchShare;
	}
	public static int GetChangeNoteBook(){
		return mChangeNoteBook;
	}
	public static int GetBatchDelete(){
		return mBatchDelete;
	}
	
	public static int GetLoginPage(){
		return mLoginPage;
	}
	public static int GetMainPage(){
		return mMainPage;
	}
	public static int GetEditNoteBookPage(){
		return mEditNoteBookPage;
	}
	public static int GetEditNoteRecodePage(){
		return mEditNoteRecodePage;
	}
	public static int GetSettingPage(){
		return mSettingPage;
	}
	public static int GetSeleteNoteBookPage(){
		return mSeleteNoteBookPage;
	}
	
	public static void WriteBack(){
		Editor editor = mSharedPref.edit();
		editor.putInt(RegisterButton, mRegister);
		editor.putInt(HvnLoginButton, mHvnLogin);
		editor.putInt(QQLoginButton, mQQLogin);
		editor.putInt(WXLoginButton, mWXLogin);
		editor.putInt(CrtNoteBookButton, mCrtNoteBook);
		editor.putInt(SearchNoteBookButton, mSearchNoteBook);
		editor.putInt(ConnectedButton, mBlueConnected);
		editor.putInt(CrtNoteRecordButton, mCrtNoteRecord);
		editor.putInt(StatisticsButton, mStatistics);
		editor.putInt(CloudSyncButton, mCloudSync);
		editor.putInt(DelNoteBookButton, mDeleteNoteBook);
		editor.putInt(RemNoteBookButton, mRemNoteBook);
		editor.putInt(ShareButton, mShare);
		editor.putInt(DeleteButton, mDelete);
		editor.putInt(AddImageButton, mAddImage);
		editor.putInt(RectificationButton, mRectification);
		editor.putInt(ScanMode, mScanMode);
		editor.putInt(KeyboardMode, mKeyboardMode);
		editor.putInt(IMESettingButton, mImeSettting);
		editor.putInt(BatchShareButton, mBatchShare);
		editor.putInt(ChangeNoteBookButton, mChangeNoteBook);
		editor.putInt(BatchDeleteButton, mBatchDelete);
		
		editor.putInt(LoginPage, mLoginPage);
		editor.putInt(MainPage, mMainPage);
		editor.putInt(EditNoteBookPage, mEditNoteBookPage);
		editor.putInt(EditNoteRecodePage, mEditNoteRecodePage);
		editor.putInt(SettingPage, mSettingPage);
		editor.putInt(SeleteNoteBookPage, mSeleteNoteBookPage);
		editor.putString(RecordTime, TimeUtil.getCurDate());
		editor.commit();
	}

	public static void SetCurTimeHour(){
	    Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
		t.setToNow(); // 取得系统时间。
		int hour = t.hour; // 0-23
		
		Editor editor = mSharedPref.edit();
		editor.putInt(hour+"", 1);
		editor.commit();
	}

	public static JSONObject StatisticsJson(JSONObject paramJson){
		
		try {
			paramJson.put("ver", HanvonApplication.AppVer);
			paramJson.put("longitude", HanvonApplication.curLongitude);
			paramJson.put("latitude", HanvonApplication.curLatitude);
			paramJson.put("locationCountry", HanvonApplication.curCountry);
			paramJson.put("locationProvince", HanvonApplication.curProvince);
			paramJson.put("locationCity", HanvonApplication.curCity);
			paramJson.put("locationArea", HanvonApplication.curDistrict);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return paramJson;
	}
	
	public static void UpLoadFunctionStatus(Context mcontext){
		String uploadTime = mSharedPref.getString(UploadTime, "");
		String curTime = TimeUtil.getCurDate();

		if (uploadTime.equals("")){
			Editor editor = mSharedPref.edit();
			editor.putString(UploadTime, curTime);
			editor.putString(LastSaveTime, curTime);
			editor.commit();
			return;
		}
		if (uploadTime.equals(curTime)){
			return;
		}
		
		if (new ConnectionDetector(HanvonApplication.getcontext()).isConnectingTOInternet()) {
			pd = ProgressDialog.show(mcontext, "", "");
			new Thread(UpLoadFunctionThread).start();
		}else{
			return;
		}
	}
	
    static Runnable UpLoadFunctionThread = new Runnable() {
    	String curTime = TimeUtil.getcurTimeYMDHM();
		@Override
		public void run() {
			try {
				JSONArray listJson = GetAllActionJson();
				if (listJson == null){
					JSONArray json = GetActionJson();
					JSONObject json1 = new JSONObject();
					json1.put("pageList",json);
					json1.put("localCreateDate", mSharedPref.getString(UploadTime, ""));
					JSONObject actionTimeJson = new JSONObject();
					for(int i = 0;i < 24;i++){
						int value = mSharedPref.getInt(i+"",0);
						if (value == 1){
							actionTimeJson.put(i+"", value+"");
						}
					}
					json1.put("timeScopeList", actionTimeJson);
					listJson = new JSONArray();
					listJson.put(json1);
				}
				JSONObject paraJson = new JSONObject();
				paraJson.put("userid", HanvonApplication.AppUid);
				paraJson.put("devid", HanvonApplication.AppDeviceId);
				paraJson.put("devModel", "Android");
				paraJson.put("softName", "SuluPen");
				paraJson.put("softVer", HanvonApplication.AppVer);
				paraJson.put("list", listJson);
				paraJson.put("localUploadTime", curTime);
				LogUtil.i(paraJson.toString());
				String responce = HttpClientHelper.sendPostRequest(UrlBankUtil.getFunctionUrl(), paraJson.toString());

				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("responce", responce);
				message.setData(bundle);
				StatisticsUtils.loginHandler.sendMessage(message);
			} catch (Exception e) {
				pd.dismiss();
				e.printStackTrace();
			}
		}
	};
	
	static Handler loginHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				Bundle bundle = msg.getData();
				String responce = bundle.getString("responce");
				JSONObject jsonObj = new JSONObject(responce);
				if (jsonObj.get("code").equals("0")) {
					LogUtil.i("***************************");
					Init();
					mFunctionDao.deleteFunctionsDB();
					Editor editor = mSharedPref.edit();
				    editor.putString(UploadTime, TimeUtil.getCurDate());
				    editor.commit();
					pd.dismiss();
				}else {
					pd.dismiss();
				}
			} catch (Exception e) {
				pd.dismiss();
				e.printStackTrace();
			}
		}
	};
	private static JSONArray GetAllActionJson() throws JSONException{
		JSONArray json = new JSONArray();
		mFunctionList = mFunctionDao.getAllRecords();
		int i = 0;
		if (mFunctionList.size() != 0){
			for(StatisticsFunction function:mFunctionList){
				StatisticsFunction mm = mFunctionList.get(i);
				
				JSONObject paraJson = new JSONObject();
				JSONArray aa = new JSONArray();
				if (!function.getLoginpage().equals("")){
					aa.put(new JSONObject(new String(Base64Utils.decode(mm.getLoginpage()))));
				}
				if (!function.getMainpage().equals("")){
					aa.put(new JSONObject(new String(Base64Utils.decode(mm.getMainpage()))));
				}
				if (!function.getEditNoteBookPage().equals("")){
					aa.put(new JSONObject(new String(Base64Utils.decode(mm.getEditNoteBookPage()))));
				}
				if (!function.getEditNoteRecodePage().equals("")){
					aa.put(new JSONObject(new String(Base64Utils.decode(mm.getEditNoteRecodePage()))));
				}
				if (!function.getSeleteNoteBookPage().equals("")){
					aa.put(new JSONObject(new String(Base64Utils.decode(mm.getSeleteNoteBookPage()))));
				}
				if (!function.getSettingPage().equals("")){
					aa.put(new JSONObject(new String(Base64Utils.decode(mm.getSettingPage()))));
				}
				paraJson.put("pageList",aa);
				paraJson.put("timeScopeList",new JSONObject(new String(Base64Utils.decode(mm.geTtimeJson()))));
				paraJson.put("localCreateDate",mm.getCreateTime());
				json.put(paraJson);
				i++;
			}
		}else{
			return null;
		}
		return json;
		
	}
	private static JSONArray GetActionJson() throws Exception{
		
		JSONArray json = new JSONArray();
		/**************登录页面********************/
		JSONObject loginBtnJson = new JSONObject();
		if (mRegister != 0){
		    loginBtnJson.put("RegisterBtn", mRegister+"");
		}
		if (mHvnLogin != 0){
		    loginBtnJson.put("HvnLoginBtn", mHvnLogin+"");
		}
		if (mQQLogin != 0){
		    loginBtnJson.put("QQLoginBtn", mQQLogin+"");
		}
		if (mWXLogin != 0){
			loginBtnJson.put("WXLoginBtn", mWXLogin+"");
		}
		if (mLoginPage != 0){
		    JSONObject loginJson = new JSONObject();
		    loginJson.put("pageName", "LoginPage");
		    loginJson.put("accessTotal", mLoginPage);
		    loginJson.put("btnList", loginBtnJson);
		    json.put(loginJson);
		}
		
		/*********主页面*********************/
		JSONObject mainBtnJson = new JSONObject();
		if (mCrtNoteBook != 0){
		    mainBtnJson.put("CreateNoteBookBtn", mCrtNoteBook+"");
		}
		if (mSearchNoteBook != 0){
		    mainBtnJson.put("SearchNoteBookBtn", mSearchNoteBook+"");
		}
		if (mBlueConnected != 0){
		    mainBtnJson.put("BlueConnectedBtn", mBlueConnected+"");
		}
		if (mCrtNoteRecord != 0){
		    mainBtnJson.put("CreateNoteRecodeBtn", mCrtNoteRecord+"");
		}
		if (mStatistics != 0){
		    mainBtnJson.put("StatisticsBtn", mStatistics+"");
		}
		if (mCloudSync != 0){
		    mainBtnJson.put("CloudSyncBtn", mCloudSync+"");
		}
		if (mMainPage != 0){
		    JSONObject mainJson = new JSONObject();
		    mainJson.put("pageName", "MainPage");
		    mainJson.put("accessTotal", mMainPage);
		    mainJson.put("btnList", mainBtnJson);
		    json.put(mainJson);
		}
		
		/********编辑笔记本页面************/
		JSONObject editNoteBookBtnJson = new JSONObject();
		if (mDeleteNoteBook != 0){
		    editNoteBookBtnJson.put("DeleteNoteBookBtn", mDeleteNoteBook+"");
		}
		if (mRemNoteBook != 0){
		    editNoteBookBtnJson.put("RenameNoteBookBtn", mRemNoteBook+"");
		}
		if (mEditNoteBookPage != 0){
		    JSONObject editNoteBookJson = new JSONObject();
		    editNoteBookJson.put("pageName", "EditNoteBookPage");
		    editNoteBookJson.put("accessTotal", mEditNoteBookPage);
		    editNoteBookJson.put("btnList", editNoteBookBtnJson);
		    json.put(editNoteBookJson);
		}
		
		/*******笔记编辑页面*************/
		JSONObject editNoteRecordBtnJson = new JSONObject();
		if (mShare != 0){
		    editNoteRecordBtnJson.put("ShareBtn", mShare+"");
		}
		if (mDelete != 0){
		    editNoteRecordBtnJson.put("DeleteBtn", mDelete+"");
		}
		if (mAddImage != 0){
		    editNoteRecordBtnJson.put("AddImageSBtn", mAddImage+"");
		}
		if (mRectification != 0){
		    editNoteRecordBtnJson.put("RectificationBtn", mRectification+"");
		}
		if (mScanMode != 0){
		    editNoteRecordBtnJson.put("ScanMode", mScanMode+"");
		}
		if (mKeyboardMode != 0){
		    editNoteRecordBtnJson.put("KeyboardMode", mKeyboardMode+"");
		}
		if (mEditNoteRecodePage != 0){
		    JSONObject editNoteRecordBookJson = new JSONObject();
		    editNoteRecordBookJson.put("pageName", "EditNoteRecodePage");
		    editNoteRecordBookJson.put("accessTotal", mEditNoteRecodePage);
		    editNoteRecordBookJson.put("btnList", editNoteRecordBtnJson);
		    json.put(editNoteRecordBookJson);
		}
		
		/*********设置页面*************/
		JSONObject settingBtnJson = new JSONObject();
		if (mImeSettting != 0){
		    settingBtnJson.put("ImeSettingBtn", mImeSettting+"");
		}
		if (mSettingPage != 0){
		    JSONObject settingJson = new JSONObject();
		    settingJson.put("pageName", "SettingPage");
		    settingJson.put("accessTotal", mSettingPage);
		    settingJson.put("btnList", settingBtnJson);
		    json.put(settingJson);
		}
		
		/********笔记选择页面***********/
		JSONObject selectNoteBookBtnJson = new JSONObject();
		if (mBatchShare != 0){
		    selectNoteBookBtnJson.put("BatchShareBtn", mBatchShare+"");
		}
		if (mChangeNoteBook != 0){
		    selectNoteBookBtnJson.put("ChangeNoteBookBtn", mChangeNoteBook+"");
		}
		if (mBatchDelete != 0){
		    selectNoteBookBtnJson.put("BatchDeleteBtn", mBatchDelete+"");
		}
		if (mSeleteNoteBookPage != 0){
		    JSONObject selectNoteBookJson = new JSONObject();
		    selectNoteBookJson.put("pageName", "SeleteNoteBookPage");
		    selectNoteBookJson.put("accessTotal", mSeleteNoteBookPage);
		    selectNoteBookJson.put("btnList", selectNoteBookBtnJson);
		    json.put(selectNoteBookJson);
		}
		
		LogUtil.i("--PageList:--"+json.toString());
		
		return json;
	}
	/*****************************************/
	/**根据新的接口重新保存点击记录，每天一条记录进行保存，同时将保存*/
	/**json字符串的形式保存到数据库表里面****************/
	/****************************************/
	
	public static void UpLoadFunctionStatus1(Context mcontext) throws Exception{
		String uploadTime = mSharedPref.getString(UploadTime, "");
		String lastSaveTime = mSharedPref.getString(LastSaveTime, "");
		String curTime = TimeUtil.getCurDate();

		if (uploadTime.equals("")){
			Editor editor = mSharedPref.edit();
			editor.putString(UploadTime, curTime);
			editor.putString(LastSaveTime, curTime);
			editor.commit();
			return;
		}
		if (uploadTime.equals(curTime)){
			return;
		}

	//	mFunction = new StatisticsFunction();
		mFunctionDao = new StatisticsFunctionDao(mcontext);
		
		if (new ConnectionDetector(HanvonApplication.getcontext()).isConnectingTOInternet()) {
			if (HanvonApplication.hvnName.equals("")){
				if (lastSaveTime.equals(curTime)){
					return;
				}else{
					SaveLastToDb();
				}
				return;
			}
			pd = ProgressDialog.show(mcontext, "", "");
			new Thread(UpLoadFunctionThread).start();
		}else{
			if (lastSaveTime.equals(curTime)){
				return;
			}else{
				SaveLastToDb();
			}
		}
	}
	
	private static void SaveLastToDb() throws Exception{
		JSONArray functionJson = GetActionJson();
		StatisticsFunction mFunction = new StatisticsFunction();
		Object obj;
		int count = 0;
		if (mLoginPage == 0){
			mFunction.setLoginpage("");
		}else{
			obj = functionJson.get(count);
			mFunction.setLoginpage(Base64Utils.encode(obj.toString().getBytes()));
			count++;
		}
        if (mMainPage == 0){
        	mFunction.setMainpage("");
		}else{
			obj = functionJson.get(count);
			count++;
			mFunction.setMainpage(Base64Utils.encode(obj.toString().getBytes()));
		}
        if (mEditNoteBookPage == 0){
	        mFunction.setEditNoteBookPage("");
        }else{
        	obj = functionJson.get(count);
			count++;
			mFunction.setEditNoteBookPage(Base64Utils.encode(obj.toString().getBytes()));
        }
        if (mEditNoteRecodePage == 0){
			mFunction.setEditNoteRecodePage("");
		}else{
			obj = functionJson.get(count);
			count++;
			mFunction.setEditNoteRecodePage(Base64Utils.encode(obj.toString().getBytes()));
		}
        if (mSettingPage == 0){
			mFunction.setSettingPage("");
		}else{
			obj = functionJson.get(count);
			count++;
			mFunction.setSettingPage(Base64Utils.encode(obj.toString().getBytes()));
		}
        if (mSeleteNoteBookPage == 0){
	        mFunction.setSeleteNoteBookPage("");
        }else{
        	obj = functionJson.get(count);
			count++;
			mFunction.setSeleteNoteBookPage(Base64Utils.encode(obj.toString().getBytes()));
        }
        
        /*******记录活动时间**********/
		JSONObject actionTimeJson = new JSONObject();
		for(int i = 0;i < 24;i++){
			int value = mSharedPref.getInt(i+"",0);
			if (value == 1){
				actionTimeJson.put(i+"", value+"");
			}
		}
		mFunction.setTimeJson(Base64Utils.encode(actionTimeJson.toString().getBytes()));
        
		String createTime = mSharedPref.getString(RecordTime, "");
        mFunction.setCreateTime(createTime);
        
        mFunctionDao.add(mFunction);
 
        Init();
	}
}
