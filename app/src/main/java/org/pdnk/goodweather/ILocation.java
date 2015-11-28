package org.pdnk.goodweather;

/**
 * Created by Inflicted on 27/11/2015.
 */
public interface ILocation
{
    int getId();
    String getName();
    String getZIP();
    String getCountryCode();
    String getLongitude();
    String getLatitude();

    String getTemp();
    String getWind();
    String getDescription();

}
