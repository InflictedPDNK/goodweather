package org.pdnk.goodweather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import org.pdnk.goodweather.Interfaces.ILocation;
import org.pdnk.goodweather.Provider.OpenWeatherModel.Clouds;
import org.pdnk.goodweather.Provider.OpenWeatherModel.Coord;
import org.pdnk.goodweather.Provider.OpenWeatherModel.Main;
import org.pdnk.goodweather.Provider.OpenWeatherModel.OpenWeatherObject;
import org.pdnk.goodweather.Provider.OpenWeatherModel.Sys;
import org.pdnk.goodweather.Provider.OpenWeatherModel.Weather;
import org.pdnk.goodweather.Provider.OpenWeatherModel.Wind;

import java.util.ArrayList;

/**
 * Created by Inflicted on 30/11/2015.
 */
public class DataPresentationTest extends ActivityInstrumentationTestCase2<MainActivity>
{
    public DataPresentationTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        getActivity();
    }


    public void testHistoryItem() throws Exception
    {
        OpenWeatherObject obj = new OpenWeatherObject();
        obj.name = "testlocation";
        obj.id = 123;

        obj.weather = new ArrayList<>();
        obj.weather.add(new Weather());
        obj.wind = new Wind();
        obj.clouds = new Clouds();
        obj.coord = new Coord();
        obj.sys = new Sys();
        obj.main = new Main();

        ILocation loc = WeatherLocation.createFromOpenWeatherObject(obj);
        ManagerFactory.getHistoryMgr().addItem(loc);

        Thread.sleep(2000);

        FragmentManager fm =  getActivity().getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag("home");

        RecyclerView rv = (RecyclerView) f.getView().findViewById(R.id.locationContainer);

        assertFalse(rv.getAdapter() == null);

        assertFalse(rv.getAdapter().getItemCount() == 0);
        assertFalse(rv.getChildCount() == 0);

        boolean found = false;


        for(int i = 0; i < rv.getChildCount(); ++i)
        {
            View v = rv.getChildAt(i);
            TextView tv = (TextView) v.findViewById(R.id.city);
            String s = new StringBuilder(tv.getText()).toString();
            if(s.contains("testlocation"))
            {
                found = true;
                break;
            }
        }


        assertTrue(found);

        ManagerFactory.getHistoryMgr().removeItem(loc);
    }

}
