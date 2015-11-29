package org.pdnk.goodweather.Interfaces;

/**
 * Created by Inflicted on 27/11/2015.
 */
public interface ILocation
{
    int getId();
    String getName();
    String getCountryCode();
    String getLongitude();
    String getLatitude();

    String getTemp();
    String getWind();
    String getDescription();

    String getImageId();

    String getPressure();
    String getHumidity();

    String getSunrise();
    String getSunset();
}
