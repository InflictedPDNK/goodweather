package org.pdnk.goodweather.Provider.OpenWeatherModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coord
{

    @SerializedName("lon")
    @Expose
    public double lon;
    @SerializedName("lat")
    @Expose
    public double lat;

}
