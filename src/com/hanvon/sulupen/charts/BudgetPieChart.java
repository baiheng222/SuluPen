package com.hanvon.sulupen.charts;

/**
 * Created by fan on 2015/11/25.
 */
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.dao.NoteBookRecordDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Budget demo pie chart.
 */
public class BudgetPieChart extends AbstractDemoChart
{
    private final String TAG = "BudgetPieChart";
    private List<NoteBookRecord> mNoteBookList = null;
    private double[] values = new double[] { 12, 14, 11, 10, 19 };
    //int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    public String getName() {
        return "Budget chart";
    }

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    public String getDesc() {
        return "The budget per project for this year (pie chart)";
    }

    /**
     * Executes the chart demo.
     *
     * @param context the context
     * @return the built intent
     */
    public Intent execute(Context context) {
        double[] values = new double[] { 12, 14, 11, 10, 19 };
        int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };

        DefaultRenderer renderer = buildCategoryRenderer(colors);
        renderer.setZoomButtonsVisible(false);
        renderer.setZoomEnabled(false);
        renderer.setChartTitleTextSize(20);
        renderer.setDisplayValues(true);
        renderer.setShowLabels(false);
        SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
        r.setGradientEnabled(true);
        r.setGradientStart(0, Color.BLUE);
        r.setGradientStop(0, Color.GREEN);
        r.setHighlighted(false);
        Intent intent = ChartFactory.getPieChartIntent(context,
                buildCategoryDataset("Project budget", values), renderer, "Budget");
        return intent;
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

    protected CategorySeries buildCategoryDataset(String title, double[] values)
    {
        CategorySeries series = new CategorySeries(title);
        int k = 0;
        for (double value : values)
        {
            series.add("", value);
        }

        return series;
    }

    public GraphicalView getView(Context context, int[] colors, CategorySeries series)
    {
        /*
        CategorySeries series = new CategorySeries("Notebooks");
        int valuesize = 0;

        NoteBookRecordDao noteBookRecordDao = new NoteBookRecordDao(context);
        mNoteBookList = noteBookRecordDao.getAllNoteBooks();
        if (null != mNoteBookList)
        {
            values = new double[mNoteBookList.size()];
            for (int i = 0; i < mNoteBookList.size(); i++)
            {
                values[i] = mNoteBookList.get(i).getNoteRecordList().size();
                if (values[i] > 0)
                {
                    series.add(mNoteBookList.get(i).getNoteBookName(), values[i]);
                    valuesize++;
                }

            }
        }

        if (valuesize == 0)
        {
            return  null;
        }

        int[] colors = new int[valuesize];
        for (int i = 0; i < valuesize; i++)
        {
            colors[i] = Color.parseColor(getRandColorCode());    //Integer.parseInt(getRandColorCode());
        }
        */

        DefaultRenderer renderer = buildCategoryRenderer(colors);
        renderer.setApplyBackgroundColor(false);
        renderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        renderer.setZoomButtonsVisible(false);
        renderer.setZoomEnabled(false);
        //renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(20);
        renderer.setLabelsColor(Color.BLACK);
        renderer.setDisplayValues(true);
        renderer.setShowLabels(false);
        renderer.setPanEnabled(false);
        renderer.setShowLegend(true);
        renderer.setLegendTextSize(30);
        renderer.setMargins(new int[]{0, 0, 0, 0});
        renderer.setFitLegend(true);
        //renderer.setLegendHeight(60);
        /*
        SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
        //r.setGradientEnabled(true);
        //r.setGradientStart(0, Color.BLUE);
        //r.setGradientStop(0, Color.GREEN);
        r.setHighlighted(true);
        */
        GraphicalView view = ChartFactory.getPieChartView(context, series, renderer);
        return view;
    }
}

