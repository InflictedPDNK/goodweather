package org.pdnk.goodweather.Provider;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.pdnk.goodweather.ContentManager;
import org.pdnk.goodweather.Interfaces.ILocation;
import org.pdnk.goodweather.Interfaces.IWeatherProvider;
import org.pdnk.goodweather.Provider.OpenWeatherModel.OpenWeatherObject;
import org.pdnk.goodweather.R;
import org.pdnk.goodweather.Utility;
import org.pdnk.goodweather.WeatherLocation;

import java.io.IOException;


/**
 * Created by Inflicted on 1/12/2015.
 */
public class OkHTTPWeatherProvider implements IWeatherProvider
{
    private final Context ctx;
    private final String BASE_URL = "http://api.openweathermap.org";
    public static final String IMAGE_URL = "http://openweathermap.org/img/w/";
    private final String APP_ID = "95d190a434083879a6398aafd54d9e73";

    private final OkHttpClient okClient;
    public OkHTTPWeatherProvider(Context ctx)
    {
        okClient = new OkHttpClient();
        okClient.setFollowRedirects(true);
        okClient.setFollowSslRedirects(true);

        this.ctx = ctx;
    }
    @Override
    public boolean getLocation(ILocation oldLocation, final ContentManager contentManager)
    {
        String units = Utility.isMetric(ctx) ? ctx.getString(R.string.METRIC) : ctx.getString(R.string.IMPERIAL);

        final String url;
        if(oldLocation.getId() != 0)
            url = String.format("%s/data/2.5/weather?id=%s&units=%s&appid=%s", BASE_URL, oldLocation.getId(), units, APP_ID);
        else if(!oldLocation.getLongitude().isEmpty() && !oldLocation.getLatitude().isEmpty())
            url = String.format("%s/data/2.5/weather?lat=%s&lon=%s&units=%s&appid=%s", BASE_URL, oldLocation.getLatitude(), oldLocation.getLongitude(), units, APP_ID);
        else
            url = String.format("%s/data/2.5/weather?q=%s&units=%s&appid=%s", BASE_URL, oldLocation.getName(), units, APP_ID);


        Request request = new Request.Builder()
                                  .url(url)
                                  .build();

        okClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onResponse(Response response) throws IOException
            {
                handleSuccessfulResponse(response, contentManager, true);
            }

            @Override
            public void onFailure(Request request, IOException e)
            {

            }
        });

        return true;

    }

    @Override
    public boolean updateLocation(ILocation oldLocation, final ContentManager contentManager)
    {
        if(oldLocation.getId() == 0)
            return false;

        String units = Utility.isMetric(ctx) ? ctx.getString(R.string.METRIC) : ctx.getString(R.string.IMPERIAL);

        String url = String.format("%s/data/2.5/weather?id=%s&units=%s&appid=%s", BASE_URL, oldLocation.getId(), units, APP_ID);

        Request request = new Request.Builder()
                                  .url(url)
                                  .build();

        okClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onResponse(Response response) throws IOException
            {
                handleSuccessfulResponse(response, contentManager, false);
            }

            @Override
            public void onFailure(Request request, IOException e)
            {
                showFailureMessage("Network request failure");
            }
        });
        return true;
    }

    private void handleSuccessfulResponse(Response response, final ContentManager contentManager, boolean select) throws IOException
    {
        Gson gson = new Gson();

        OpenWeatherObject newWeatherObj = gson.fromJson(response.body().charStream(), OpenWeatherObject.class);


        ILocation newLocation = WeatherLocation.createFromOpenWeatherObject(newWeatherObj);
        if(newLocation == null)
        {
            showFailureMessage("Location not found or connection failure");
        }else
        {
            contentManager.addItem(newLocation);
            if(select)
                contentManager.setSelectedLocation(newLocation, true);
        }
    }

    private void showFailureMessage(final String msg)
    {
        ((Activity) ctx).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog.Builder b = new AlertDialog.Builder(ctx);
                b.setTitle("Update failed");
                b.setMessage(msg);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setPositiveButton("Got it", null);

                b.create().show();
            }
        });

    }
}
