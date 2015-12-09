package org.pdnk.goodweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.pdnk.goodweather.Interfaces.ILocation;


/**
 * Created by Inflicted on 27/11/2015.
 */
public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder>
{
    private final ContentManager content;
    private final boolean reoderOnSelect;

    public LocationRecyclerAdapter(ContentManager content, boolean reoderOnSelect)
    {
        this.reoderOnSelect = reoderOnSelect;
        this.content = content;
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
        holder.updateUI(content.getContent().get(position));
        holder.removeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                content.removeItem(holder.mItem);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //instruct item to update
                content.updateItem(holder.mItem);

                //select it for details display. once update completed, the fragment will catch the
                //update
                content.setSelectedLocation(holder.mItem, reoderOnSelect);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return content.getContent().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        final ImageView image;
        final TextView city;
        final TextView temp;
        final TextView description;
        final View removeBtn;

        public ILocation mItem;

        public ViewHolder(View view)
        {
            super(view);

            image = (ImageView) view.findViewById(R.id.image);
            city = (TextView) view.findViewById(R.id.city);
            temp = (TextView) view.findViewById(R.id.temp);
            description = (TextView) view.findViewById(R.id.description);
            removeBtn = view.findViewById(R.id.removeBtn);


        }

        void updateUI(ILocation newLocation)
        {
            mItem = newLocation;

            String tempSuffix;

            if (Utility.isMetric(itemView.getContext()))
            {
                tempSuffix = "°C";
            } else
            {
                tempSuffix = "°F";
            }

            city.setText(String.format("%s (%s)", mItem.getName(), mItem.getCountryCode()));
            temp.setText(mItem.getTemp() + tempSuffix);
            description.setText(mItem.getDescription());

            Picasso
                    .with(image.getContext())
                    .load(mItem.getImageId())
                    .fit()
                    .placeholder(R.drawable.ic_label_outline_black_48dp)
                    .into(image);
        }

    }
}
