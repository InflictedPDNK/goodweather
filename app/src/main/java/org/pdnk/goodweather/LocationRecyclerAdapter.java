package org.pdnk.goodweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by Inflicted on 27/11/2015.
 */
public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder>
{
    LinkedList<ILocation> content;
    public LocationRecyclerAdapter(LinkedList<ILocation> locationData)
    {
        this.content = locationData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.location_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.updateUI(content.get(position));

        /*
        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
             //   if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
           //         mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        */
    }

    @Override
    public int getItemCount()
    {
        return content.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView city;
        TextView temp;

        public ILocation mItem;

        public ViewHolder(View view)
        {
            super(view);

            image = (ImageView) view.findViewById(R.id.image);
            city = (TextView) view.findViewById(R.id.city);
            temp = (TextView) view.findViewById(R.id.temp);

        }

        void updateUI(ILocation newLocation)
        {
            mItem = newLocation;

            city.setText(mItem.getName());
            temp.setText(String.format("%.0f °C",mItem.getTemp()));
        }

    }
}
