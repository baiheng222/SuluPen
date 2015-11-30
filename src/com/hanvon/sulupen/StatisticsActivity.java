package com.hanvon.sulupen;

import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanvon.sulupen.charts.BarChartView;
import com.hanvon.sulupen.charts.BudgetDoughnutChart;
import com.hanvon.sulupen.charts.BudgetPieChart;

public class StatisticsActivity extends Activity
{
    private static final  String TAG = "StatisticsActivity";
    private GraphicalView mPieChartView;
    private GraphicalView mBarChartView;
    private  static int[] COLORS = new int[]{Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN};
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private BudgetPieChart mPieChart;
    private BarChartView mBarChart;
    private BudgetDoughnutChart mBudgetDoughnutChart;


    private RelativeLayout mLlSummary;
    private ImageView mIvLabel;
    private TextView mTvLabel;
    private ImageView mIvMax;
    private TextView mTvMax;
    private LinearLayout mLlChart;
    private LinearLayout mLlBarChart;

    private void assignViews()
    {
        mLlSummary = (RelativeLayout) findViewById(R.id.ll_summary);
        mIvLabel = (ImageView) findViewById(R.id.iv_label);
        mTvLabel = (TextView) findViewById(R.id.tv_label);
        mIvMax = (ImageView) findViewById(R.id.iv_max);
        mTvMax = (TextView) findViewById(R.id.tv_max);
        mLlChart = (LinearLayout) findViewById(R.id.ll_chart);
        mLlBarChart = (LinearLayout) findViewById(R.id.ll_bar_chart);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        assignViews();

        mPieChart = new BudgetPieChart();
        mBarChart = new BarChartView();
        mBudgetDoughnutChart = new BudgetDoughnutChart();
        //LinearLayout layout = (LinearLayout) findViewById(R.id.ll_chart);
        mPieChartView = mPieChart.getView(this);
        mBarChartView = mBarChart.getView(this);
        //mRenderer.setClickEnabled(true);

        mLlChart.addView(mPieChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mLlBarChart.addView(mBarChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Log.d(TAG, "!!!! onResume");

    }
}
