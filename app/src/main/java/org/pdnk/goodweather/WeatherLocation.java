package org.pdnk.goodweather;

/**
 * Created by Inflicted on 27/11/2015.
 */
public class WeatherLocation implements ILocation
{
    private int id;
    private String name;
    private int ZIP;
    private String countryCode;
    private double longitude;
    private double latitude;
    private float wind;
    private double temp;
    private String description;

    static ILocation createFromSearchQuery(String searchQuery)
    {
        WeatherLocation o = new WeatherLocation();
        o.name = "city" + (int)(Math.random() * 1000);
        o.ZIP = 0000;
        o.latitude = -23.24;
        o.longitude = 42.24;
        o.temp = Math.random() * 50.f;
        return o;
    }

    private static ILocation create(String cityName)
    {
        WeatherLocation o = new WeatherLocation();
        o.name = cityName;
        return o;
    }

    private static ILocation create(int ZIP)
    {
        WeatherLocation o = new WeatherLocation();
        o.ZIP = ZIP;
        return o;
    }

    private static ILocation create(double longitude, double latitude)
    {
        WeatherLocation o = new WeatherLocation();
        o.longitude = longitude;
        o.latitude = latitude;
        return o;
    }

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getZIP()
    {
        return ZIP;
    }

    @Override
    public String getCountryCode()
    {
        return countryCode;
    }

    @Override
    public double getLongitude()
    {
        return longitude;
    }

    @Override
    public double getLatitude()
    {
        return latitude;
    }

    @Override
    public double getTemp()
    {
        return temp;
    }

    @Override
    public float getWind()
    {
        return wind;
    }

    @Override
    public String getDescription()
    {
        return description;
    }
}
