package org.pdnk.goodweather.Provider;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.squareup.okhttp.OkHttpClient;

import org.pdnk.goodweather.ContentManager;
import org.pdnk.goodweather.Interfaces.ILocation;
import org.pdnk.goodweather.Interfaces.IWeatherProvider;
import org.pdnk.goodweather.Provider.OpenWeatherModel.OpenWeatherObject;
import org.pdnk.goodweather.Provider.OpenWeatherModel.RetrofitWeatherAPI;
import org.pdnk.goodweather.R;
import org.pdnk.goodweather.Utility;
import org.pdnk.goodweather.WeatherLocation;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Inflicted on 28/11/2015.
 */
public class RetrofitWeatherProvider implements IWeatherProvider
{
    private final Context ctx;
    private final RetrofitWeatherAPI apiService;
    private final String BASE_URL = "http://api.openweathermap.org";
    public static final String IMAGE_URL = "http://openweathermap.org/img/w/";
    private final String APP_ID = "95d190a434083879a6398aafd54d9e73";

    private final Retrofit retro;
    public RetrofitWeatherProvider(Context ctx)
    {
        OkHttpClient c = new OkHttpClient();
        c.setFollowRedirects(true);

        c.setFollowSslRedirects(true);

        retro = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(c)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
        apiService = retro.create(RetrofitWeatherAPI.class);



        this.ctx = ctx;
    }
    @Override
    public boolean updateLocation(ILocation oldLocation, final ContentManager contentManager)
    {
        Call<OpenWeatherObject> call;

        String units = Utility.isMetric(ctx) ? ctx.getString(R.string.METRIC) : ctx.getString(R.string.IMPERIAL);

        if(oldLocation.getId() == 0)
            return false;


        call = apiService.getById(oldLocation.getId(), units, APP_ID);

        call.enqueue(new Callback<OpenWeatherObject>()
        {
            @Override
            public void onResponse(Response<OpenWeatherObject> response, Retrofit retrofit)
            {
                handleSuccessfulResponse(response, contentManager, false);
            }

            @Override
            public void onFailure(Throwable t)
            {

            }
        });
        return true;
    }

    @Override
    public boolean getLocation(ILocation oldLocation, final ContentManager contentManager)
    {
        Call<OpenWeatherObject> call;

        String units = Utility.isMetric(ctx) ? ctx.getString(R.string.METRIC) : ctx.getString(R.string.IMPERIAL);

        if(oldLocation.getId() != 0)
            call = apiService.getById(oldLocation.getId(), units, APP_ID);
        else if(!oldLocation.getLongitude().isEmpty() && !oldLocation.getLatitude().isEmpty())
            call = apiService.getByCoordinates(oldLocation.getLatitude(), oldLocation.getLongitude(), units, APP_ID);
        else
            call = apiService.getName(oldLocation.getName(), units, APP_ID);

        call.enqueue(new Callback<OpenWeatherObject>()
        {
            @Override
            public void onResponse(Response<OpenWeatherObject> response, Retrofit retrofit)
            {
                handleSuccessfulResponse(response, contentManager, true);
            }

            @Override
            public void onFailure(Throwable t)
            {
                showFailureMessage("Network request failure");
            }
        });
        return true;
    }

    private void handleSuccessfulResponse(Response<OpenWeatherObject> response, final ContentManager contentManager, boolean select)
    {
        OpenWeatherObject newWeatherObj = response.body();

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
