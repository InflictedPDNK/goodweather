package org.pdnk.goodweather;

import android.content.Context;

/**
 * Created by Inflicted on 29/11/2015.
 */
public class Utility
{
    static public boolean isMetric(Context ctx)
    {
        if(MainActivity.OPT_FORCE_UNIT_SYSTEM != null)
            return MainActivity.OPT_FORCE_UNIT_SYSTEM == ctx.getString(R.string.METRIC);

        return ctx.getString(R.string.UNITS) == ctx.getString(R.string.METRIC);
    }


}
