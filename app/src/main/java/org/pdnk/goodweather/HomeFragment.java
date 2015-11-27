package org.pdnk.goodweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment
{
    ContentManager<ILocation> content;

    public HomeFragment()
    {

        //default parameter-less
    };

    public HomeFragment(ContentManager<ILocation> content)
    {
        setRetainInstance(true);

        this.content = content;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View myView;
        if(content.getHistoryList() == null || content.getHistoryList().isEmpty())
            myView = inflater.inflate(R.layout.fragment_main_empty, container, false);
        else
        {
            myView = inflater.inflate(R.layout.fragment_main_history, container, false);

            RecyclerView rv = (RecyclerView) myView.findViewById(R.id.locationContainer);

            rv.setLayoutManager(new LinearLayoutManager(myView.getContext()));
            rv.setAdapter(new LocationRecyclerAdapter(content.getHistoryList()));

        }

        return myView;
    }

}
