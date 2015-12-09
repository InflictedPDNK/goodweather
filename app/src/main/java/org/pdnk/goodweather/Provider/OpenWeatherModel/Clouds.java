package org.pdnk.goodweather.Provider.OpenWeatherModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Clouds
{

    @SerializedName("all")
    @Expose
    public int all;

}
