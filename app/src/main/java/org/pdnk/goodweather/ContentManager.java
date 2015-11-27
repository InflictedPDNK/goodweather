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
        CLEAR
    };

    enum UpdateTarget {
        HISTORY,
        FAVOURITES,
        ALL
    }

    LinkedList<T> history = new LinkedList<T>();
    LinkedList<T> favourites = new LinkedList<T>();

    final int MAX_HISTORY = 10;

    class UpdateTrait extends Object
    {
        public UpdateAction action;
        public UpdateTarget target;

        public UpdateTrait(UpdateTarget target, UpdateAction action)
        {
            this.target = target;
            this.action = action;
        }
    }

    public ContentManager(Context ctx)
    {

    }

    public LinkedList<T> getHistoryList()
    {
        return history;
    }

    public LinkedList<T> getFavouritesList()
    {
        return favourites;
    }

    private void loadLocalData()
    {

    }

    public  void createTestLocation(String incomingQuery)
    {
       // if(T instanceof ILocation)
        {
            ILocation newLocation = WeatherLocation.createFromSearchQuery(incomingQuery);

            if(history.size() > MAX_HISTORY)
            {
                history.removeLast();
            }
            history.addFirst((T)newLocation);

            setChanged();
            notifyObservers(new UpdateTrait(UpdateTarget.HISTORY, UpdateAction.UPDATE));

        }
    }
}
