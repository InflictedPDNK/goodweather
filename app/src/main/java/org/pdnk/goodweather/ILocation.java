package org.pdnk.goodweather;

/**
 * Created by Inflicted on 27/11/2015.
 */
public interface ILocation
{
    int getId();
    String getName();
    int getZIP();
    String getCountryCode();
    double getLongitude();
    double getLatitude();

    double getTemp();
    float getWind();
    String getDescription();

}
