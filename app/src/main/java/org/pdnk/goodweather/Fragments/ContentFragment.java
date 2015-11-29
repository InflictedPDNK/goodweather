package org.pdnk.goodweather.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pdnk.goodweather.ContentManager;
import org.pdnk.goodweather.LocationRecyclerAdapter;
import org.pdnk.goodweather.R;

import java.util.Observable;
import java.util.Observer;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContentFragment extends Fragment implements Observer
{
    ContentManager content;
    View clearAllBtn;

    String titleText;
    ViewGroup emptyBlock;
    ViewGroup contentBlock;

    public static final String ARG_TITLE = "title";
    public static final String ARG_REORDER_ON_SELECT = "reorder";
    public static final String ARG_EMPTY1 = "empty1";
    public static final String ARG_EMPTY2 = "empty2";

    String emptyLine1;
    String emptyLine2;

    LocationRecyclerAdapter adapter;
    public ContentFragment()
    {
        //default parameter-less
    };

    public ContentFragment(ContentManager content)
    {
        this.content = content;
        content.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        Bundle b = getArguments();
        boolean reorder = false;
        if(b != null)
        {
            titleText = b.getString(ARG_TITLE);
            reorder = b.getBoolean(ARG_REORDER_ON_SELECT);

            emptyLine1 = b.getString(ARG_EMPTY1);
            emptyLine2 = b.getString(ARG_EMPTY2);

        }

        View myView = inflater.inflate(R.layout.fragment_main_list, container, false);

        emptyBlock = (ViewGroup) myView.findViewById(R.id.emptyBlock);
        contentBlock = (ViewGroup) myView.findViewById(R.id.contentBlock);

        ((TextView) myView.findViewById(R.id.line1)).setText(emptyLine1);
        ((TextView) myView.findViewById(R.id.line2)).setText(emptyLine2);


        RecyclerView rv = (RecyclerView) myView.findViewById(R.id.locationContainer);
        rv.setLayoutManager(new LinearLayoutManager(myView.getContext()));
        adapter = new LocationRecyclerAdapter(content, reorder);
        rv.setAdapter(adapter);

        clearAllBtn = myView.findViewById(R.id.clearAllBtn);
        clearAllBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                content.removeAll();
            }
        });

        TextView titleTxt = (TextView) myView.findViewById(R.id.titleTxt);
        titleTxt.setText(titleText);

        updateBlocksVisibility();

        return myView;
    }

    void updateBlocksVisibility()
    {
        emptyBlock.setVisibility(content.getContent().isEmpty()? View.VISIBLE : View.INVISIBLE);
        contentBlock.setVisibility(content.getContent().isEmpty()? View.INVISIBLE : View.VISIBLE);
    }


    @Override
    public void update(Observable observable, Object data)
    {
        ContentManager.UpdateTrait trait = (ContentManager.UpdateTrait) data;


        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.notifyDataSetChanged();

        updateBlocksVisibility();
    }
}
