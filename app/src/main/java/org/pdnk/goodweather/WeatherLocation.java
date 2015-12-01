package org.pdnk.goodweather;

import org.pdnk.goodweather.Interfaces.ILocation;
import org.pdnk.goodweather.Provider.OpenWeatherModel.OpenWeatherObject;
import org.pdnk.goodweather.Provider.RetrofitWeatherProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Inflicted on 27/11/2015.
 */
public class WeatherLocation extends Object implements ILocation
{
    private int id;
    private String name;
    private String countryCode;
    private double longitude;
    private double latitude;
    private double wind;
    private double temp;
    private int humidity;
    private int pressure;
    private String description;
    private String sunrise;
    private String sunset;



    private String imageId;

    static ILocation createFromSearchQuery(String searchQuery)
    {
        WeatherLocation o = new WeatherLocation();
        String modifiedQuery;

        //trim leading/trailing spaces
        modifiedQuery = searchQuery.trim();

        //if first char is letter, perhaps it's a city name
        if(Character.isLetter(modifiedQuery.charAt(0)))
        {
            o.name = modifiedQuery;
            return o;
        }

        //tokenize string by spaces and commas
        StringTokenizer tokens = new StringTokenizer(modifiedQuery, ", ");

        //if more than one token, it's probably coords. consider only first two
        if(tokens.countTokens() > 1)
        {
            try
            {
                o.latitude = Double.parseDouble(tokens.nextToken());
                o.longitude = Double.parseDouble(tokens.nextToken());
                return  o;
            }catch (NumberFormatException e)
            {
                //can't parse, probably some wrong chars there. try the last check
            }
        }


        //simply try what's inside as a last resort
        o.name = modifiedQuery;

        return o;
    }

    static public ILocation createFromOpenWeatherObject(OpenWeatherObject origin)
    {
        WeatherLocation o = new WeatherLocation();

        if(origin == null || origin.weather.isEmpty() || origin.id == 0)
            return null;

        o.name = origin.name;
        o.description = origin.weather.get(0).description;

        o.imageId = RetrofitWeatherProvider.IMAGE_URL + origin.weather.get(0).icon + ".png";

        o.wind = origin.wind.speed;

        o.countryCode = origin.sys.country;
        o.longitude = origin.coord.lon;
        o.latitude = origin.coord.lat;

        o.temp = origin.main.temp;
        o.humidity = origin.main.humidity;
        o.pressure = (int) origin.main.pressure;

        o.id = origin.id;

        Date d = new Date(origin.sys.sunrise *1000);
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");

        //f.setTimeZone(TimeZone.getDefault());
        o.sunrise = f.format(d) + " UTC";

        d = new Date(origin.sys.sunset *1000);
        o.sunset = f.format(d) + " UTC";;
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
    public String getCountryCode()
    {
        return countryCode;
    }

    @Override
    public String getLongitude()
    {
        if(longitude != 0.0)
            return String.format("%.4f",longitude);

        return "";
    }

    @Override
    public String getLatitude()
    {
        if(latitude != 0.0)
            return String.format("%.4f",latitude);

        return "";
    }

    @Override
    public String getTemp()
    {
        return String.format("%.1f",temp);
    }

    @Override
    public String getWind()
    {
        return String.format("%.1f", wind);
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getImageId()
    {
        return imageId;
    }

    @Override
    public String getHumidity()
    {
        return String.format("%d", humidity);
    }

    @Override
    public String getPressure()
    {
        return String.format("%d", pressure);

    }

    @Override
    public String getSunrise()
    {
        return sunrise;

    }

    @Override
    public String getSunset()
    {
        return sunset;
    }
}
