package com.hanvon.sulupen;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hanvon.sulupen.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener
{
    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;

    private ImageView[] dots;

    private int currentIndex;


    private void initViews()
    {
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();

        views.add(inflater.inflate(R.layout.one_main, null));
        views.add(inflater.inflate(R.layout.two_main, null));
        views.add(inflater.inflate(R.layout.three_main, null));
        views.add(inflater.inflate(R.layout.four_main, null));

        vpAdapter = new ViewPagerAdapter(views, this);

        vp = (ViewPager) findViewById(R.id.viewpager_guide);

        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);
    }

    /*
    private void initDots()
    {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[views.size()];

        // 循环取得小点图片
        for (int i = 0; i < views.size(); i++)
        {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);// 下面小点，初始化所有项都使能状态 }

            currentIndex = 0;
            dots[currentIndex].setEnabled(false);// 初始化第一项为非使能状态
        }
    }
    */

    /*
    private void setCurrentDot(int position)
    {
        if (position < 0 || position > views.size() - 1
                || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.guide);

        initViews();
        //initDots();
    }

    //滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0)
    {

    }

    //当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2)
    {

    }

    //当页面被选中时调用
    @Override
    public void onPageSelected(int arg0)
    {
        //setCurrentDot(arg0);
    }
}
