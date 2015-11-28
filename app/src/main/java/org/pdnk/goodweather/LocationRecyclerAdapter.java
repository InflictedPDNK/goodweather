package org.pdnk.goodweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Inflicted on 27/11/2015.
 */
public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder>
{
    ContentManager<ILocation> content;
    boolean reoderOnSelect;

    public LocationRecyclerAdapter(ContentManager<ILocation> content, boolean reoderOnSelect)
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
        ImageView image;
        TextView city;
        TextView temp;
        View removeBtn;

        public ILocation mItem;

        public ViewHolder(View view)
        {
            super(view);

            image = (ImageView) view.findViewById(R.id.image);
            city = (TextView) view.findViewById(R.id.city);
            temp = (TextView) view.findViewById(R.id.temp);
            removeBtn = view.findViewById(R.id.removeBtn);

        }

        void updateUI(ILocation newLocation)
        {
            mItem = newLocation;

            city.setText(mItem.getName());
            temp.setText(mItem.getTemp());
        }

    }
}
