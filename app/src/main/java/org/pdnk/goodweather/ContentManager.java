package org.pdnk.goodweather;

import android.content.Context;

import org.pdnk.goodweather.Interfaces.ILocation;
import org.pdnk.goodweather.Interfaces.IWeatherProvider;
import org.pdnk.goodweather.Provider.RetrofitWeatherProvider;

import java.util.LinkedList;
import java.util.Observable;

/**
 * Created by Inflicted on 27/11/2015.
 */
public class ContentManager extends Observable
{
    enum UpdateAction {
        UPDATE,
        CLEAR,
        SELECTEDITEM
    };

    LinkedList<ILocation> content = new LinkedList<>();
    private int maxCount = 0;

    ILocation selectedLocation;

    IWeatherProvider provider;

    public class UpdateTrait extends Object
    {
        public UpdateAction action;
        public ILocation location;

        public UpdateTrait(UpdateAction action, ILocation location)
        {
            this.action = action;
            this.location = location;
        }
    }

    public ContentManager(Context ctx)
    {

        provider = new RetrofitWeatherProvider(ctx);
    }

    public ContentManager(Context ctx, int maxCount)
    {
        provider = new RetrofitWeatherProvider(ctx);
        this.maxCount = maxCount;
    }

    public LinkedList<ILocation> getContent()
    {
        return content;
    }

    private void loadLocalData()
    {

    }

    public  void createNewLocation(String incomingQuery)
    {
        //TODO: try and find the existing first instead of creating each time

        ILocation newLocation = WeatherLocation.createFromSearchQuery(incomingQuery);

        provider.updateLocation(newLocation, this);
    }

    public ILocation getSelectedLocation()
    {
        return selectedLocation;
    }

    public void setSelectedLocation(ILocation location, boolean reorder)
    {
        if(reorder && content.contains(location))
        {
            content.remove(location);
            content.addFirst(location);
            //setChanged();
            //notifyObservers(new UpdateTrait(myId, UpdateAction.UPDATE, null));
        }

        selectedLocation = location;

        setChanged();
        notifyObservers(new UpdateTrait(UpdateAction.SELECTEDITEM, selectedLocation));
    }

    public void addItem(ILocation item)
    {
        if(maxCount > 0 && content.size() > maxCount)
        {
            content.removeLast();
        }
        content.addFirst(item);

        setChanged();

        notifyObservers(new UpdateTrait(UpdateAction.UPDATE, item));
    }

    public boolean contains(ILocation item)
    {
        return content.contains(item);
    }

    public void removeItem(ILocation item)
    {
        if(item == selectedLocation)
            selectedLocation = null;

        content.remove(item);
        setChanged();
        notifyObservers(new UpdateTrait(UpdateAction.UPDATE, null));
    }

    public void removeAll()
    {
        selectedLocation = null;

        content.clear();
        setChanged();
        notifyObservers(new UpdateTrait(UpdateAction.CLEAR, null));
    }

}
