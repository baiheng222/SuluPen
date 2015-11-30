package com.hanvon.sulupen.charts;

import android.content.Context;
import android.content.Intent;

/**
 * Created by fan on 2015/11/25.
 */
public interface IDemoChart
{
    /** A constant for the name field in a list activity. */
    String NAME = "name";
    /** A constant for the description field in a list activity. */
    String DESC = "desc";

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    String getName();

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    String getDesc();

    /**
     * Executes the chart demo.
     *
     * @param context the context
     * @return the built intent
     */
    Intent execute(Context context);
}
