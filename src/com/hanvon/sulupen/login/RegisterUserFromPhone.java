package com.hanvon.sulupen.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hanvon.sulupen.R;
import com.hanvon.sulupen.utils.ClearEditText;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterUserFromPhone extends Activity implements OnClickListener{

	private ClearEditText CEphoneNumber;
	private ClearEditText CEpassword;
	private Button BTregist;
	private TextView TVemailRegist;
	private ImageView IVback;
	
	private String strPhoneNumber;
	private String strPassword;
	
	private ProgressDialog pd;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_user_phone);

		CEphoneNumber = (ClearEditText)findViewById(R.id.rgst_user);
		CEpassword = (ClearEditText)findViewById(R.id.rgst_pswd);
		BTregist = (Button)findViewById(R.id.rgst_rgstbutton);
		TVemailRegist = (TextView)findViewById(R.id.regist_emailuser);
        IVback = (ImageView)findViewById(R.id.rgst_back);
		
        CEphoneNumber.setOnClickListener(this);
        CEpassword.setOnClickListener(this);
        BTregist.setOnClickListener(this);
        TVemailRegist.setOnClickListener(this);
        IVback.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		    case R.id.rgst_rgstbutton:
		    	strPhoneNumber = CEphoneNumber.getText().toString();
		    	strPassword = CEpassword.getText().toString();
		    	if (strPhoneNumber.equals("") || strPassword.equals("")){
					Toast.makeText(RegisterUserFromPhone.this, "手机号和密码不允许为空", Toast.LENGTH_SHORT).show();
					return;
				}
		    	
		    	if ((strPassword.length() < 6) || (strPassword.length() > 16)){
					Toast.makeText(RegisterUserFromPhone.this, "密码应为6-16位字母和数字组合!", Toast.LENGTH_SHORT).show();
					return;
				} else {
					Pattern pN = Pattern.compile("[0-9]{6,16}");
				    Matcher mN = pN.matcher(strPassword);
				    Pattern pS = Pattern.compile("[a-zA-Z]{6,16}");
				    Matcher mS = pS.matcher(strPassword);
				    if((mN.matches()) || (mS.matches())){
				        Toast.makeText(RegisterUserFromPhone.this,"请输入符合规则的密码!", Toast.LENGTH_SHORT).show();
				        return;
				    }
				}
		    	
		    	if (strPhoneNumber != null){
		            Pattern p = Pattern.compile("[1][358]+\\d{9}");
	                Matcher m = p.matcher(strPhoneNumber);
	                if(!m.matches() ){
	                    Toast.makeText(RegisterUserFromPhone.this,"手机号码不合法", Toast.LENGTH_SHORT).show();
	                    return;
	                }
	            //    sendCodeToPhone();
		        }
		    	
			    break;
		    case R.id.regist_emailuser:
		    	Intent intent = new Intent(RegisterUserFromPhone.this, RegisterUserFromEmail.class);
		    	RegisterUserFromPhone.this.startActivity(intent);
		    	RegisterUserFromPhone.this.finish();
		    	break;
		    case R.id.resetpwd_back:
		    	break;
		    default:
		    	break;
		}
	}
}
