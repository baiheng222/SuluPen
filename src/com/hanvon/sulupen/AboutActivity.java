package com.hanvon.sulupen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AboutActivity extends Activity implements OnClickListener
{
    private final String TAG = "SettingActivity";
    
    private ImageView mBack;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        
        mBack = (ImageView) findViewById(R.id.iv_about_backbtn);
        mBack.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_about_backbtn:
                finish();
            break;
            
        }
        
    }
    
}
