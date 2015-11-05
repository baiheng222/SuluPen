package com.hanvon.sulupen;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends Activity implements OnClickListener
{
    private final String TAG = "SettingActivity";
    
    RelativeLayout mRlInputMethod;
    RelativeLayout mRlAutoUpgrade;
    RelativeLayout mRlClearBuffer;
    RelativeLayout mRlHelpManual;
    RelativeLayout mRlFeedBack;
    RelativeLayout mRlAboutUs;
    
    TextView mTvLogout;
    ImageView mTvBackBtn;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        
        initView();
    }
    
    
    private void initView()
    {
        mRlInputMethod = (RelativeLayout) findViewById(R.id.rl_setting_inputmethod);
        mRlAutoUpgrade = (RelativeLayout) findViewById(R.id.rl_setting_upgrade);
        mRlClearBuffer = (RelativeLayout) findViewById(R.id.rl_setting_clear);
        mRlHelpManual = (RelativeLayout) findViewById(R.id.rl_setting_help);
        mRlFeedBack = (RelativeLayout) findViewById(R.id.rl_setting_feedback);
        mRlAboutUs = (RelativeLayout) findViewById(R.id.rl_setting_about);
        
        mTvLogout = (TextView) findViewById(R.id.tv_setting_logout);
        mTvBackBtn = (ImageView) findViewById(R.id.tv_backbtn);
        
        mRlInputMethod.setOnClickListener(this);
        mRlAutoUpgrade.setOnClickListener(this);
        mRlClearBuffer.setOnClickListener(this);
        mRlHelpManual.setOnClickListener(this);
        mRlFeedBack.setOnClickListener(this);
        mRlAboutUs.setOnClickListener(this);
        
        mTvLogout.setOnClickListener(this);
        mTvBackBtn.setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.rl_setting_inputmethod:
                
            break;
            
            case R.id.rl_setting_about:
                
            break;
            
            case R.id.rl_setting_feedback:
                
            break;
            
            case R.id.rl_setting_help:
                
            break;
                
            case R.id.rl_setting_clear:
                    
            break;
                
            case R.id.tv_setting_logout:
                    
            break;
                
            case R.id.rl_setting_upgrade:
                    
            break;
            
            case R.id.tv_backbtn:
               Log.d(TAG, "back btn licked");
                finish();
            break;
        }
    }
}
