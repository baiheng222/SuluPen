package com.hanvon.sulupen;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.hanvon.sulupen.helper.PreferHelper;

public class SplashActivity extends Activity
{
    boolean isFirstIn = false;

    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    // 延迟3秒
    private static final long SPLASH_DELAY_MILLIS = 1000;
    private static String isFirstInStr = "isFirstIn";

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private  void goHome()
    {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("Flag", "firstrun");
        startActivity(intent);
        finish();
    }

    private  void goGuide()
    {
    	Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
    }


    private void init()
    {
        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = PreferHelper.getBoolean(isFirstInStr, true);
        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
        if (!isFirstIn)
        {
            // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
        }
        else
        {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        init();
    }

}
