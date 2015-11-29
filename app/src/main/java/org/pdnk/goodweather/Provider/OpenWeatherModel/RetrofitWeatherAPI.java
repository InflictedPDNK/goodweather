package org.pdnk.goodweather.Provider.OpenWeatherModel;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Inflicted on 28/11/2015.
 */
public interface RetrofitWeatherAPI
{
    @GET("/data/2.5/weather")
    Call<OpenWeatherObject> getName(@Query("q") String locationName, @Query("units") String units, @Query("appid") String appId);

    @GET("/data/2.5/weather")
    Call<OpenWeatherObject> getByCoordinates(@Query("lat") String latitude, @Query("lon") String longitude, @Query("units") String units, @Query("appid") String appId);

    @GET("/data/2.5/weather")
    Call<OpenWeatherObject> getById(@Query("id") int locationId, @Query("units") String units, @Query("appid") String appId);

}
