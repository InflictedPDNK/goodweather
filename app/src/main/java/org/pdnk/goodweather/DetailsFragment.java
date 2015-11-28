package org.pdnk.goodweather;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment
{
    ContentManager<ILocation> favouriteContent;
    ILocation locationDetails;
    ColorStateList originalFavBkg;

    public DetailsFragment()
    {
        // Required empty public constructor
    }

    public DetailsFragment(ILocation locationDetails, ContentManager<ILocation> favouriteContent)
    {
        this.locationDetails = locationDetails;
        this.favouriteContent = favouriteContent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_details, container, false);


        ((TextView) v.findViewById(R.id.city)).setText(locationDetails.getName());
        ((TextView)v.findViewById(R.id.description)).setText(locationDetails.getDescription());
        ((TextView)v.findViewById(R.id.temp)).setText(locationDetails.getTemp());
        ((TextView)v.findViewById(R.id.wind)).setText(locationDetails.getWind());
        ((TextView)v.findViewById(R.id.coordinates)).setText(locationDetails.getLatitude() + ", " + locationDetails.getLongitude());

        FloatingActionButton favBtn = (FloatingActionButton) v.findViewById(R.id.favBtn);
        originalFavBkg = favBtn.getBackgroundTintList();

        updateFavouriteState(favBtn, false);

        favBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateFavouriteState((FloatingActionButton) v, true);
            }
        });


        return v;
    }

    void updateFavouriteState(FloatingActionButton favBtn, boolean modify)
    {
        if(favouriteContent.contains(locationDetails))
        {
            if(modify)
            {
                favouriteContent.removeItem(locationDetails);
                favBtn.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(android.R.color.darker_gray)));
            }else
                favBtn.setBackgroundTintList(originalFavBkg);        }
        else
        {
            if(modify)
            {
                favouriteContent.addItem(locationDetails);
                favBtn.setBackgroundTintList(originalFavBkg);
            }else
                favBtn.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(android.R.color.darker_gray)));
        }
    }

}
