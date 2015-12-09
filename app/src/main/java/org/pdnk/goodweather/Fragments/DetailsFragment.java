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

import java.util.Observable;
import java.util.Observer;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements Observer
{
    private ContentManager favouriteContent;
    private ContentManager historyContent;
    private ILocation locationDetails;
    private ColorStateList originalFavBkg;

    private TextView city;
    private TextView description;
    private TextView temp;
    private TextView wind;
    private TextView humidity;
    private TextView pressure;
    private TextView sunrise;
    private TextView sunset;
    private TextView coordinates;
    private ImageView image;



    public DetailsFragment()
    {
        // Required empty public constructor
    }

    public DetailsFragment(ILocation locationDetails, ContentManager historyContent, ContentManager favouriteContent)
    {
        this.locationDetails = locationDetails;
        this.favouriteContent = favouriteContent;
        this.historyContent = historyContent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        city = ((TextView) v.findViewById(R.id.city));
        description = ((TextView)v.findViewById(R.id.description));
        temp = ((TextView)v.findViewById(R.id.temp));
        wind =  ((TextView)v.findViewById(R.id.wind));
        humidity = ((TextView)v.findViewById(R.id.humidity));
        pressure = ((TextView)v.findViewById(R.id.pressure));
        sunrise = ((TextView)v.findViewById(R.id.sunrise));
        sunset = ((TextView)v.findViewById(R.id.sunset));
        coordinates = ((TextView)v.findViewById(R.id.coordinates));
        image = (ImageView) v.findViewById(R.id.weatherImage);

        historyContent.addObserver(this);
        favouriteContent.addObserver(this);


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

    private void updateFavouriteState(FloatingActionButton favBtn, boolean modify)
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

    private void updateDetails(View v)
    {
        String tempSuffix;
        String speedSuffix;

        if(Utility.isMetric(v.getContext()))
        {
            tempSuffix = "°C";
            speedSuffix = " m/s";
        }else
        {
            tempSuffix = "°F";
            speedSuffix = " M/h";
        }

        Picasso
                .with(getContext())
                .load(locationDetails.getImageId())
                .fit()
                .into(image);


        city.setText(locationDetails.getName() + " (" + locationDetails.getCountryCode() + ')');
        description.setText(locationDetails.getDescription());
        temp.setText(locationDetails.getTemp() + tempSuffix);
        wind.setText(locationDetails.getWind() + speedSuffix);
        humidity.setText(locationDetails.getHumidity() + '%');
        pressure.setText(locationDetails.getPressure() + " hPa");
        sunrise.setText(locationDetails.getSunrise());
        sunset.setText(locationDetails.getSunset());
        coordinates.setText(locationDetails.getLatitude() + ", " + locationDetails.getLongitude());
    }

    @Override
    public void update(Observable observable, Object data)
    {
        ContentManager.UpdateTrait trait = (ContentManager.UpdateTrait) data;
        if(trait.action == ContentManager.UpdateAction.UPDATE && trait.location != null && locationDetails.getId() == trait.location.getId())
        {
            locationDetails = ((ContentManager.UpdateTrait) data).location;
            if(getView() != null)
            {
                getView().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateDetails(getView());
                    }
                });
            }
        }

    }

    @Override
    public void onDestroyView()
    {
        historyContent.deleteObserver(this);
        favouriteContent.deleteObserver(this);
        super.onDestroyView();
    }
}
