package com.hanvon.sulupen.charts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by fan on 2015/11/25.
 */
public class BarChartView
{
    private static final int SERIES_NR = 1;

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    public String getName() {
        return "Budget chart for several years";
    }

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    public String getDesc() {
        return "The budget per project for several years (doughnut chart)";
    }

    private XYMultipleSeriesDataset getBarDemoDataset()
    {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        final int nr = 12;
        Random r = new Random();
        for (int i = 0; i < SERIES_NR; i++) {
            CategorySeries series = new CategorySeries("year");
            for (int k = 0; k < nr; k++) {
                series.add(100 + r.nextInt() % 100);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    private XYMultipleSeriesDataset getBarDataset(int[] datas)
    {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int nr = datas.length;
        //Random r = new Random();
        for (int i = 0; i < SERIES_NR; i++)
        {
            CategorySeries series = new CategorySeries("year");
            for (int k = 0; k < nr; k++)
            {
                series.add(datas[k]);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    public XYMultipleSeriesRenderer getBarDemoRenderer()
    {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setPanEnabled(false);
        renderer.setAxisTitleTextSize(20);
        //renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(20);
        renderer.setDisplayValues(true);
        //renderer.setLegendTextSize(15);
        renderer.setShowLegend(false);
        renderer.setMargins(new int[] {20, 30, 15, 0});
        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        //r.setColor(Color.BLUE);
        //renderer.addSeriesRenderer(r);
        //r = new SimpleSeriesRenderer();
        r.setColor(Color.GREEN);
        renderer.addSeriesRenderer(r);
        return renderer;
    }

    private void setChartSettings(XYMultipleSeriesRenderer renderer)
    {
        //renderer.setChartTitle("");
        //renderer.setXTitle("x values");
        //renderer.setYTitle("y values");

        renderer.setXLabels(12);
        renderer.setXAxisMin(0.5);
        renderer.setXAxisMax(12.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(50);
        renderer.setBarSpacing(0.5f);
        renderer.setZoomButtonsVisible(false);
        renderer.setZoomEnabled(false);
        renderer.setPanEnabled(false);
        renderer.setXLabelsAlign(Align.LEFT);//刻度线与X轴坐标文字左侧对齐
        renderer.setYLabelsAlign(Align.LEFT);
        renderer.setXLabelsColor(Color.BLUE);
        renderer.setYLabelsColor(0, Color.BLUE);
    }

    public GraphicalView getView(Context context, int[] datas)
    {
        XYMultipleSeriesRenderer renderer =getBarDemoRenderer();
        setChartSettings(renderer);
        //GraphicalView view = ChartFactory.getBarChartView(context, getBarDemoDataset(), renderer, BarChart.Type.DEFAULT);
        GraphicalView view = ChartFactory.getBarChartView(context, getBarDataset(datas), renderer, BarChart.Type.DEFAULT);
        return view;
    }
}
