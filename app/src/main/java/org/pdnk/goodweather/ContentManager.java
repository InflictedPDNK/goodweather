package org.pdnk.goodweather;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.pdnk.goodweather.Interfaces.ILocation;
import org.pdnk.goodweather.Interfaces.IWeatherProvider;
import org.pdnk.goodweather.Provider.RetrofitWeatherProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Observable;

/**
 * Created by Inflicted on 27/11/2015.
 */
public class ContentManager extends Observable
{
    public enum UpdateAction {
        ADDEDNEW,
        UPDATE,
        CLEAR,
        SELECTEDITEM
    };

    LinkedList<ILocation> content = new LinkedList<>();
    private int maxCount = 0;
    private String myId;

    ILocation selectedLocation;

    IWeatherProvider provider;
    Context ctx;

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

    public ContentManager(Context ctx, String myId)
    {
        this.myId = myId;
        this.ctx = ctx;
        init();
    }

    public ContentManager(Context ctx, String myId, int maxCount)
    {
        this(ctx, myId);
        this.maxCount = maxCount;
    }

    void init()
    {
        provider = new RetrofitWeatherProvider(ctx);
        loadLocalData();
        updateAll();
    }

    public LinkedList<ILocation> getContent()
    {
        return content;
    }

    public void loadLocalData()
    {

        try {
            FileInputStream fis = ctx.openFileInput(myId);
            FileReader r = new FileReader(fis.getFD());
            Gson gson = new GsonBuilder().create();

            Type collectionType = new TypeToken<LinkedList<WeatherLocation>>(){}.getType();
            LinkedList<ILocation> loadedList = gson.fromJson(r, collectionType);
            if(loadedList != null)
                content = loadedList;

            fis.close();

        } catch (Exception e)
        {
            return;
        }
    }

    public void saveLocalData()
    {
        try{
            FileOutputStream fos = ctx.openFileOutput(myId, Context.MODE_PRIVATE);
            FileWriter w = new FileWriter(fos.getFD());
            Gson gson = new GsonBuilder().create();
            gson.toJson(content, w);
            w.close();
            fos.close();
        }catch (IOException e)
        {
            return;
        }
    }


    public  void createNewLocation(String incomingQuery)
    {
        ILocation newLocation = WeatherLocation.createFromSearchQuery(incomingQuery);

        provider.getLocation(newLocation, this);
    }

    public void setSelectedLocation(ILocation location, boolean reorder)
    {
        if(reorder)
        {
            for(ILocation loc : content)
            {
                if(loc.getId() == location.getId())
                {
                    content.remove(loc);
                    content.addFirst(loc);
                    break;
                }
            }
        }

        selectedLocation = location;

        setChanged();
        notifyObservers(new UpdateTrait(UpdateAction.SELECTEDITEM, selectedLocation));
    }

    public void addItem(ILocation item)
    {
        if(contains(item))
        {
            for (ILocation loc : content)
            {
                if (loc.getId() == item.getId())
                {
                    content.set(content.indexOf(loc), item);
                    setChanged();
                    notifyObservers(new UpdateTrait(UpdateAction.UPDATE, item));
                    return;
                }
            }
        }else
        {
            if (maxCount > 0 && content.size() > maxCount)
            {
                content.removeLast();
            }
            content.addFirst(item);

            setChanged();
            notifyObservers(new UpdateTrait(UpdateAction.ADDEDNEW, item));
        }
    }

    public void updateItem(ILocation item)
    {
        provider.updateLocation(item, this);
    }

    public void updateAll()
    {
        for(ILocation loc : content)
            provider.updateLocation(loc, this);
    }

    public boolean contains(ILocation item)
    {
        for(ILocation loc : content)
        {
            if(loc.getId() == item.getId())
                return true;
        }
        return false;
    }

    public void removeItem(ILocation item)
    {
        if(selectedLocation != null && item.getId() == selectedLocation.getId())
            selectedLocation = null;

        for(ILocation loc : content)
        {
            if(loc.getId() == item.getId())
            {
                content.remove(loc);
                setChanged();
                notifyObservers(new UpdateTrait(UpdateAction.UPDATE, null));
                return;
            }
        }

    }

    public void removeAll()
    {
        selectedLocation = null;

        content.clear();
        setChanged();
        notifyObservers(new UpdateTrait(UpdateAction.CLEAR, null));
    }

}
