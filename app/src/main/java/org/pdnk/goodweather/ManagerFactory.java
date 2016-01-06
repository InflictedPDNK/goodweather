package org.pdnk.goodweather;

import android.support.v7.app.AppCompatActivity;

import org.pdnk.goodweather.Interfaces.IManager;

/**
 * Created by Inflicted on 9/12/2015.
 */
public class ManagerFactory
{
    private static ManagerFactory self;
    private IManager contentHistory;
    private IManager contentFavourites;
    private IManager navigationMgr;
    private IManager locationGPSmgr;

    private ManagerFactory(AppCompatActivity parent)
    {

    }

    public static void construct(AppCompatActivity parent)
    {
        self = new ManagerFactory(parent);

        self.contentHistory = new ContentManager(parent, "history", Options.MAX_HISTORY);
        self.contentFavourites = new ContentManager(parent, "favourites");
        self.navigationMgr = new NavigationManager(parent);
        self.locationGPSmgr = new GPSManager(parent);

        self.contentHistory.initialise();
        self.contentFavourites.initialise();
        self.locationGPSmgr.initialise();
        self.navigationMgr.initialise();
    }


    public static void shutdown()
    {
        self.contentHistory.cleanUp();
        self.contentFavourites.cleanUp();
        self.locationGPSmgr.cleanUp();
        self.navigationMgr.cleanUp();
    }

    public static ContentManager getHistoryMgr()
    {
        return (ContentManager) self.contentHistory;
    }

    public static ContentManager getFavouriteManager()
    {
        return (ContentManager) self.contentFavourites;
    }

    public static NavigationManager getNavigationManager()
    {
        return (NavigationManager) self.navigationMgr;
    }

    public static GPSManager getGPSManager()
    {
        return (GPSManager) self.locationGPSmgr;
    }


}
