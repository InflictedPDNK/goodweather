package org.pdnk.goodweather.Fragments;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.pdnk.goodweather.ContentManager;
import org.pdnk.goodweather.Interfaces.ILocation;
import org.pdnk.goodweather.R;
import org.pdnk.goodweather.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment
{
    ContentManager favouriteContent;
    ILocation locationDetails;
    ColorStateList originalFavBkg;

    public DetailsFragment()
    {
        // Required empty public constructor
    }

    public DetailsFragment(ILocation locationDetails, ContentManager favouriteContent)
    {
        this.locationDetails = locationDetails;
        this.favouriteContent = favouriteContent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        updateDetails(v);

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

    void updateDetails(View v)
    {
        String tempSuffix;
        String speedSuffix;

        if(Utility.isMetric(getContext()))
        {
            tempSuffix = "°C";
            speedSuffix = " m/s";
        }else
        {
            tempSuffix = "°F";
            speedSuffix = " M/h";
        }

        ImageView image = (ImageView) v.findViewById(R.id.weatherImage);

        Picasso
                .with(getContext())
                .load(locationDetails.getImageId())
                .fit()
                .into(image);


        ((TextView) v.findViewById(R.id.city)).setText(locationDetails.getName() + " (" + locationDetails.getCountryCode() + ')');
        ((TextView)v.findViewById(R.id.description)).setText(locationDetails.getDescription());
        ((TextView)v.findViewById(R.id.temp)).setText(locationDetails.getTemp() + tempSuffix);
        ((TextView)v.findViewById(R.id.wind)).setText(locationDetails.getWind() + speedSuffix);
        ((TextView)v.findViewById(R.id.humidity)).setText(locationDetails.getHumidity() + '%');
        ((TextView)v.findViewById(R.id.pressure)).setText(locationDetails.getPressure() + " hPa");
        ((TextView)v.findViewById(R.id.sunrise)).setText(locationDetails.getSunrise());
        ((TextView)v.findViewById(R.id.sunset)).setText(locationDetails.getSunset());
        ((TextView)v.findViewById(R.id.coordinates)).setText(locationDetails.getLatitude() + ", " + locationDetails.getLongitude());
    }

}
