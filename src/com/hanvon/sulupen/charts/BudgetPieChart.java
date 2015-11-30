package com.hanvon.sulupen.charts;

/**
 * Created by fan on 2015/11/25.
 */
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Budget demo pie chart.
 */
public class BudgetPieChart extends AbstractDemoChart {
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

    public GraphicalView getView(Context context) {
        double[] values = new double[] { 12, 14, 11, 10, 19 };
        int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };
        DefaultRenderer renderer = buildCategoryRenderer(colors);
        renderer.setZoomButtonsVisible(false);
        renderer.setZoomEnabled(false);
        renderer.setChartTitleTextSize(20);
        renderer.setDisplayValues(true);
        renderer.setShowLabels(true);
        SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
        r.setGradientEnabled(true);
        r.setGradientStart(0, Color.BLUE);
        r.setGradientStop(0, Color.GREEN);
        r.setHighlighted(true);
        GraphicalView view = ChartFactory.getPieChartView(context,
                buildCategoryDataset("Project budget", values), renderer);
        return view;
    }
}

