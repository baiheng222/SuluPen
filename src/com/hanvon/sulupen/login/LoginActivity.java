package com.hanvon.sulupen.login;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.MainActivity;
import com.hanvon.sulupen.R;
import com.hanvon.sulupen.net.HttpClientHelper;
import com.hanvon.sulupen.net.JsonData;
import com.hanvon.sulupen.net.RequestResult;
import com.hanvon.sulupen.net.RequestServerData;
import com.hanvon.sulupen.utils.LogUtil;
import com.hanvon.sulupen.utils.ClearEditText;
import com.hanvon.sulupen.utils.ConnectionDetector;
import com.hanvon.sulupen.utils.LoginUtil;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.Tencent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private TextView TVSkip;
	private ClearEditText ETUserName;
	private ClearEditText ETPassWord;
	private Button BTLogin;
	private TextView TVRegist;
	private TextView TVForgetPassword;
	private String strUserName;
	private String strPassWord;
	
	private ProgressDialog pd;

	private ImageView LLQQUser;
	private ImageView LLWXUser;
	
	private int userflag = 0;
	public static LoginActivity instance = null;
	public String flag;   // 0 从其他界面跳转 1 从云信息登陆跳转  2 从上传界面跳转
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

	    TVSkip = (TextView) findViewById(R.id.login_quit);
		ETUserName = (ClearEditText) findViewById(R.id.username_editText);
		ETPassWord = (ClearEditText) findViewById(R.id.passwd_editText);
		BTLogin = (Button) findViewById(R.id.login_button);
		TVRegist = (TextView) findViewById(R.id.registuser);
		TVForgetPassword = (TextView) findViewById(R.id.remember_pwd);

		LLQQUser = (ImageView)findViewById(R.id.login_qq);
		LLWXUser = (ImageView)findViewById(R.id.login_weixin);

		TVSkip.setOnClickListener(this);
		ETUserName.setOnClickListener(this);
		ETPassWord.setOnClickListener(this);
		BTLogin.setOnClickListener(this);
		TVRegist.setOnClickListener(this);
		TVForgetPassword.setOnClickListener(this);
		LLQQUser.setOnClickListener(this);
		LLWXUser.setOnClickListener(this);

		if (HanvonApplication.mTencent == null) {
			HanvonApplication.mTencent = Tencent.createInstance("1104705079", this);
	    }
		
		Intent intent = getIntent();
		if (intent != null){
			flag = intent.getStringExtra("flag");
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		    case R.id.login_quit:
		    	goHome();
			    if (flag != null){
					if (Integer.valueOf(flag) == 1){
					//	startActivity(new Intent(LoginActivity.this, MyCloudActivity.class));
					//    LoginActivity.this.finish();
					}else if (Integer.valueOf(flag) == 2){
					///	startActivity(new Intent(LoginActivity.this, ScanNoteDetailActivity.class));
					 //   LoginActivity.this.finish();
					}
				}else{
				//	goHome();
				}
			    break;
            case R.id.login_button:
            	strPassWord = ETPassWord.getText().toString();
            	strUserName = ETUserName.getText().toString();
				if (strPassWord.equals("") || strUserName.equals("")){
					Toast.makeText(LoginActivity.this, "用户名或者密码不允许为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if ((strPassWord.length() < 6) || (strPassWord.length() > 16)){
					Toast.makeText(LoginActivity.this, "密码应为6-16位字母和数字组合!", Toast.LENGTH_SHORT).show();
					return;
				} else {
					Pattern pN = Pattern.compile("[0-9]{6,16}");
				    Matcher mN = pN.matcher(strPassWord);
				    Pattern pS = Pattern.compile("[a-zA-Z]{6,16}");
				    Matcher mS = pS.matcher(strPassWord);
				    if((mN.matches()) || (mS.matches())){
				        Toast.makeText(LoginActivity.this,"请输入符合规则的密码!", Toast.LENGTH_SHORT).show();
				        return;
				    }
				}
				LogUtil.i("username:"+strUserName+", passwd:"+strPassWord);
				InputMethodManager m=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            	judgeUserIsOk();
				break;
            case R.id.registuser:
            	LogUtil.i("INTO Create user Before");
            	Intent intent = new Intent(LoginActivity.this, RegisterUserGetCodePhone.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
                break;
            case R.id.remember_pwd:
            	Intent intent1 = new Intent(LoginActivity.this, RememberPassword.class);
                LoginActivity.this.startActivity(intent1);
                LoginActivity.this.finish();
                break;
                
            case R.id.login_qq:
            	QQUserLogin();
            	break;
            
            case R.id.login_weixin:
            	weiXinUserLogin();
            	break;

			default:
			    break;
		}
	}

	private void goHome() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		LoginActivity.this.startActivity(intent);
		LoginActivity.this.finish();
	}

	public void judgeUserIsOk(){
		if (new ConnectionDetector(LoginActivity.this).isConnectingTOInternet()) {
			pd = ProgressDialog.show(LoginActivity.this, "", "正在登录......");
			new Thread(loginThread).start();
		} else {
			Toast.makeText(LoginActivity.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
		}
		
	}
	
    Runnable loginThread = new Runnable() {
		
		@Override
		public void run() {
			try {
				JSONObject paramJson = new JSONObject();
				paramJson.put("uid",HanvonApplication.AppUid);
				paramJson.put("sid", HanvonApplication.AppSid);
				paramJson.put("user", strUserName);
				paramJson.put("pwd", strPassWord);
				LogUtil.i(paramJson.toString());
				String responce = HttpClientHelper.sendPostRequest("http://dpi.hanvon.com/rt/ap/v1/user/login", paramJson.toString());

				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("responce", responce);
				message.setData(bundle);
				LoginActivity.this.loginHandler.sendMessage(message);
			} catch (Exception e) {
				pd.dismiss();
				e.printStackTrace();
			}
		}
	};
	
	Handler loginHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				Bundle bundle = msg.getData();
				String responce = bundle.getString("responce");
				JSONObject jsonObj = new JSONObject(responce);
				if (jsonObj.get("code").equals("0")) {
					LogUtil.i("***************************");
					new RequestTask().execute();
				} else if (jsonObj.get("code").equals("520")){
					pd.dismiss();
					Toast.makeText(LoginActivity.this, "服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
				} else {
					pd.dismiss();
					Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				pd.dismiss();
				Toast.makeText(getApplication(), "网络连接超时", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	};
	
	class RequestTask  extends AsyncTask<Void, Void, RequestResult>{
  	  @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    }
		@Override
		protected RequestResult doInBackground(Void... arg0) {
			RequestResult result=null;
		    result = getUserInfoFromServer();
			return result;
		}
		 //响应结果
	    protected void onPostExecute(RequestResult result) {
	        JsonData data = result.getData();
	        String jsonCode= data.getJson();
	        try {
	        	pd.dismiss();
			    JSONObject json=new JSONObject(jsonCode);
			    LogUtil.i(json.toString());
			    if (json.getString("code").equals("0") ){
			    	boolean isHasNick = true;
			    	String email = json.getString("email");
			    	String phone = json.getString("phone");
                    String nickname = json.getString("nickname");
                    if(nickname.equals("")){
                    	nickname = strUserName;
                    	isHasNick = false;
                    }
                    String username = json.getString("user");

			    	SharedPreferences mSharedPreferences=getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
					Editor mEditor=	mSharedPreferences.edit();
					mEditor.putString("nickname", nickname);
					mEditor.putString("username", username);
					mEditor.putBoolean("isHasNick", isHasNick);
					
					if (!email.equals("")){
						mEditor.putString("email", email);
						HanvonApplication.strEmail = email;
					}
					if (!phone.equals("")){
						mEditor.putString("phone", phone);
						HanvonApplication.strPhone = phone;
					}
					mEditor.putString("passwd", strPassWord);
					mEditor.putInt("flag", 0);
					mEditor.putInt("status", 1);
					mEditor.commit();
					
					SharedPreferences mSharedCloudPreferences=getSharedPreferences("Cloud_Info", Activity.MODE_MULTI_PROCESS);
					int cloudType = mSharedCloudPreferences.getInt("cloudtype", 0);
					if (cloudType != 2){
					    Editor mCloudEditor = mSharedCloudPreferences.edit();
					    mCloudEditor.putString("cloudname", username);
					    mCloudEditor.putInt("cloudtype", 1);
				        HanvonApplication.cloudType = 1;
				        mCloudEditor.commit();
					}

					LogUtil.i("--------emai:"+email+"  phone:"+phone);
					HanvonApplication.userFlag = 0;
					if (flag != null){
						if (Integer.valueOf(flag) == 1){
							//startActivity(new Intent(LoginActivity.this, MyCloudMsg.class));
						    LoginActivity.this.finish();
						}else if (Integer.valueOf(flag) == 2){
						//	startActivity(new Intent(LoginActivity.this, ScanNoteDetailActivity.class));
						    LoginActivity.this.finish();
						}else{
							Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
						//	startActivity(new Intent(LoginActivity.this, MainActivity.class));
						    LoginActivity.this.finish();
						}
					}else{
						Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
					  //  startActivity(new Intent(LoginActivity.this, MainActivity.class));
					    LoginActivity.this.finish();
					}
			    }
		    } catch (JSONException e) {
			    e.printStackTrace();
		    }
	    }
    }

    public RequestResult getUserInfoFromServer(){
    	JSONObject paramJson=new JSONObject();
  	    try {
  		    paramJson.put("user", strUserName);
  	    } catch (JSONException e) {
  		    e.printStackTrace();
  	    }

  	    LogUtil.i(paramJson.toString());
  	    RequestResult result=new RequestResult();
 	    result=RequestServerData.getUserInfo(paramJson);
 	    LogUtil.i(result.toString());
  	    return result;
    }

	public void QQUserLogin(){
		LogUtil.i("INTO QQUserLogin!!!!!!!!");
		if (new ConnectionDetector(LoginActivity.this).isConnectingTOInternet()) {
			HanvonApplication.userFlag = 1;
			pd = ProgressDialog.show(LoginActivity.this, "", "");
			LoginUtil logutil = new LoginUtil(LoginActivity.this,LoginActivity.this);
			if (flag != null){
				logutil.QQLogin(pd,Integer.valueOf(flag));
			}else{
				logutil.QQLogin(pd,0);	
			}
		} else {
			Toast.makeText(LoginActivity.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
		}
		LogUtil.i("Leave QQUserLogin!!!!!!!!");
	}
	
	public void hvnUserLogin(){
	//	Drawable drawable = getResources().getDrawable(R.drawable.logo); 
	//	IVloginImage.setImageDrawable(drawable);
	}

	public void weiXinUserLogin(){
		LogUtil.i("INTO WeixinUserLogin!!!!!!!!");
		if (new ConnectionDetector(LoginActivity.this).isConnectingTOInternet()) {
			HanvonApplication.userFlag = 2;
			final SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			if (flag != null){
				req.state = "carjob_wx_login"+flag;
			}else{
				req.state = "carjob_wx_login"+"0";
			}
			HanvonApplication.api.sendReq(req);
		//	LoginActivity.this.finish();
		} else {
			Toast.makeText(LoginActivity.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
		}
	}
	
	public synchronized Drawable byteToDrawable(String icon) { 
	    byte[] img=Base64.decode(icon.getBytes(), Base64.DEFAULT);
	    Bitmap bitmap;
	    if (img != null) {
	        bitmap = BitmapFactory.decodeByteArray(img,0, img.length);  
	        @SuppressWarnings("deprecation")
	        Drawable drawable = new BitmapDrawable(bitmap);

	        return drawable;
	    }
	    return null;
	}

	@Override
	protected void onDestroy() {
		LogUtil.i("INTO onDestroy!!!!!!!!");
		super.onDestroy();
	}
	
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK )
	    {
	    	startActivity(new Intent(LoginActivity.this, MainActivity.class));
	    	this.finish();
	    }
	    return false;                                                                  
	}
}

