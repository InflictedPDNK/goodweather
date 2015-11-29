package org.pdnk.goodweather.Interfaces;

import org.pdnk.goodweather.ContentManager;

/**
 * Created by Inflicted on 28/11/2015.
 */
public interface IWeatherProvider
{
    ILocation updateLocation(ILocation oldLocation, ContentManager contentManager);

}
