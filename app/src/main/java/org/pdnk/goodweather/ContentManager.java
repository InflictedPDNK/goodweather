package org.pdnk.goodweather;

import android.content.Context;

import java.util.LinkedList;
import java.util.Observable;

/**
 * Created by Inflicted on 27/11/2015.
 */
public class ContentManager<T> extends Observable
{
    enum UpdateAction {
        UPDATE,
        CLEAR,
        SELECTEDITEM
    };

    LinkedList<T> content = new LinkedList<T>();
    private int myId;
    private int maxCount = 0;

    T selectedLocation;

    class UpdateTrait extends Object
    {
        public UpdateAction action;
        public int managerId;
        public T location;

        public UpdateTrait(int managerId, UpdateAction action, T location)
        {
            this.managerId = managerId;
            this.action = action;
            this.location = location;
        }
    }

    public ContentManager(Context ctx, int managerId)
    {
        myId = managerId;
    }

    public ContentManager(Context ctx, int managerId, int maxCount)
    {
        myId = managerId;
        this.maxCount = maxCount;
    }

    public LinkedList<T> getContent()
    {
        return content;
    }

    private void loadLocalData()
    {

    }

    //test
    public  void createTestLocation(String incomingQuery)
    {

        ILocation newLocation = WeatherLocation.createFromSearchQuery(incomingQuery);

        addItem((T) newLocation);

        setSelectedLocation((T) newLocation, false);
    }

    public T getSelectedLocation()
    {
        return selectedLocation;
    }

    public void setSelectedLocation(T location, boolean reorder)
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
        notifyObservers(new UpdateTrait(myId, UpdateAction.SELECTEDITEM, selectedLocation));
    }

    public void addItem(T item)
    {
        if(maxCount > 0 && content.size() > maxCount)
        {
            content.removeLast();
        }
        content.addFirst(item);

        setChanged();

        notifyObservers(new UpdateTrait(myId, UpdateAction.UPDATE, item));
    }

    public boolean contains(T item)
    {
        return content.contains(item);
    }

    public void removeItem(T item)
    {
        if(item == selectedLocation)
            selectedLocation = null;

        content.remove(item);
        setChanged();
        notifyObservers(new UpdateTrait(myId, UpdateAction.UPDATE, null));
    }

    public void removeAll()
    {
        selectedLocation = null;

        content.clear();
        setChanged();
        notifyObservers(new UpdateTrait(myId, UpdateAction.CLEAR, null));
    }

}
