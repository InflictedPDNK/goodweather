package org.pdnk.goodweather;

import android.support.v7.widget.SearchView;


/**
 * Created by Inflicted on 27/11/2015.
 */
class LocationSearchListener implements SearchView.OnQueryTextListener
{
    private final ContentManager content;
    private final SearchView parent;

    public LocationSearchListener(ContentManager content, SearchView sender)
    {
        this.content = content;
        parent = sender;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        content.createNewLocation(query);

        parent.setQuery("", false);
        parent.setIconified(true);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        return false;
    }
}
