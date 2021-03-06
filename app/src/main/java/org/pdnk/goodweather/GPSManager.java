package org.pdnk.goodweather;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import org.pdnk.goodweather.Interfaces.IManager;

import java.util.Observable;

/**
 * Created by Inflicted on 28/11/2015.
 */
class GPSManager extends Observable implements LocationListener, IManager
{
    private final Context ctx;

    public GPSManager(Context ctx)
    {
        this.ctx = ctx;
    }

    public void initialise()
    {
        //none
    }

    public void cleanUp()
    {
        //none
    }

    public void requestCoordinates()
    {
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        if (Options.ENABLE_FINE_LOCATION && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, ctx.getMainLooper());
        } else
        {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            formLocationString(loc);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        formLocationString(location);


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        if (status != LocationProvider.AVAILABLE)
        {
            notifyObservers(null);
        }
    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {
        notifyObservers(null);
    }

    private void formLocationString(Location loc)
    {
        if (loc == null)
        {
            notifyObservers(null);
        } else
        {
            String formedLocationQuery = loc.getLatitude() + "," + loc.getLongitude();
            notifyObservers(formedLocationQuery);
        }
    }

    private void notifyObservers(String coordinates)
    {
        //immediately remove updates
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);

        setChanged();
        super.notifyObservers(coordinates);

    }


}
