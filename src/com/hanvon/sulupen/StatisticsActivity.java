package com.hanvon.sulupen;

import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanvon.sulupen.charts.BarChartView;
import com.hanvon.sulupen.charts.BudgetDoughnutChart;
import com.hanvon.sulupen.charts.BudgetPieChart;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.bean.RecordInfo;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;
import com.hanvon.sulupen.db.dao.NoteRecordDao;
import com.hanvon.sulupen.utils.DataBaseUtils;
import com.hanvon.sulupen.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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
    private TextView mTitle;

    private List<RecordInfo> mRecordInfoList = new ArrayList<RecordInfo>();

    private List<NoteBookRecord> mNoteBookList = null;
    private double[] values;
    int[] colors;
    CategorySeries series;
    int[] years;
    int[] months;
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    private void assignViews()
    {
        mLlSummary = (RelativeLayout) findViewById(R.id.ll_summary);
        mIvLabel = (ImageView) findViewById(R.id.iv_label);
        mTvLabel = (TextView) findViewById(R.id.tv_label);
        mIvMax = (ImageView) findViewById(R.id.iv_max);
        mTvMax = (TextView) findViewById(R.id.tv_max);
        mLlChart = (LinearLayout) findViewById(R.id.ll_chart);
        mLlBarChart = (LinearLayout) findViewById(R.id.ll_bar_chart);
        mTitle = (TextView) findViewById(R.id.tv_year);
        String title = String.valueOf(currentYear) + getString(R.string.string_year);
        Log.i(TAG, "title is " + getString(R.string.string_year));
        mTitle.setText(title);
    }


    public static String getRandColorCode()
    {
        String r, g, b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase();
        g = Integer.toHexString(random.nextInt(256)).toUpperCase();
        b = Integer.toHexString(random.nextInt(256)).toUpperCase();

        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;

        return "#" + r + g + b;
    }

    private int getDatas()
    {
        series = new CategorySeries("Notebooks");
        int valuesize = 0;

        NoteBookRecordDao noteBookRecordDao = new NoteBookRecordDao(this);
        mNoteBookList = noteBookRecordDao.getAllNoteBooks();
        if (null != mNoteBookList)
        {
            values = new double[mNoteBookList.size()];
            for (int i = 0; i < mNoteBookList.size(); i++)
            {
                values[i] = mNoteBookList.get(i).getNoteRecordList().size();
                if (values[i] > 0)
                {
                    series.add(" " + mNoteBookList.get(i).getNoteBookName() + "      ", values[i]);
                    valuesize++;
                }

            }
        }

        if (valuesize == 0)
        {
            return -1;
        }

        colors = new int[valuesize];
        for (int i = 0; i < valuesize; i++)
        {
            colors[i] = Color.parseColor(getRandColorCode());    //Integer.parseInt(getRandColorCode());
        }

        DataBaseUtils dbutils = new DataBaseUtils(this);
        mRecordInfoList = dbutils.GetAllRecordsInfo();


        Log.i("TAG", "current year is " + currentYear);

        if (null == mRecordInfoList)
        {
            Log.i(TAG, "!!!!!!!!!!! no records found !!!!!!!!");
        }

        Log.i(TAG, "mRecordInfoList.size is " + mRecordInfoList.size());

        months = new int[12];
        for (int i = 0; i < 12; i++)
        {
            months[i] = 0;
        }

        for (int i = 0; i < mRecordInfoList.size(); i++)
        {
            if (mRecordInfoList.get(i).getYear() == currentYear)
            {
                Log.d(TAG, "current year is " + currentYear);
                Log.d(TAG, "month " + mRecordInfoList.get(i).getMonth());
                months[mRecordInfoList.get(i).getMonth()]++;
            }
        }

        for (int i = 0; i < 12; i++)
        {
            Log.d(TAG, "months[" + i + "] is" + months[i]);
        }

        return 0;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_statistics);

        assignViews();

        if (getDatas() < 0)
        {
            return;
        }

        mPieChart = new BudgetPieChart();
        mBarChart = new BarChartView();
        mBudgetDoughnutChart = new BudgetDoughnutChart();
        //LinearLayout layout = (LinearLayout) findViewById(R.id.ll_chart);
        mPieChartView = mPieChart.getView(this, colors, series);
        mBarChartView = mBarChart.getView(this, months);
        //mRenderer.setClickEnabled(true);

        if (null == mPieChartView)
        {
            return;
        }

        if (null == mBarChartView)
        {
            return;
        }

        mLlChart.addView(mPieChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mLlBarChart.addView(mBarChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Log.d(TAG, "!!!! onResume");

    }

    /*
    void getAllNotes()
    {
        NoteRecordDao dao = new NoteRecordDao(this);
        List<NoteRecord> notelist = dao.getAllNoteRecords();
        if (null != notelist)
        {
            for (int i = 0; i < notelist.size(); i++)
            {
               RecordInfo recinfo = new RecordInfo();
                recinfo.setNoteBookId(notelist.get(i).getNoteBook());
            }
        }
    }
    */
}
