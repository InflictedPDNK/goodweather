package org.pdnk.goodweather;

import android.support.v7.widget.SearchView;



/**
 * Created by Inflicted on 27/11/2015.
 */
public class LocationSearchListener implements SearchView.OnQueryTextListener
{
    ContentManager<ILocation> content;
    public LocationSearchListener(ContentManager<ILocation> content)
    {
       this.content = content;
    }
    @Override
    public boolean onQueryTextSubmit(String query)
    {
        content.createTestLocation(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        return false;
    }
}
