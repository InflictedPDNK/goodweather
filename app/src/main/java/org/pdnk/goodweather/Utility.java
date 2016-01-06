package org.pdnk.goodweather;

import android.content.Context;

import java.util.Objects;

/**
 * Created by Inflicted on 29/11/2015.
 */
public class Utility
{
    static public boolean isMetric(Context ctx)
    {
        if (Options.FORCE_UNIT_SYSTEM != null)
            return Objects.equals(Options.FORCE_UNIT_SYSTEM, ctx.getString(R.string.METRIC));

        return Objects.equals(ctx.getString(R.string.UNITS), ctx.getString(R.string.METRIC));
    }


}
