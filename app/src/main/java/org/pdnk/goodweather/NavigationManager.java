package org.pdnk.goodweather;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import org.pdnk.goodweather.Fragments.ContentFragment;
import org.pdnk.goodweather.Fragments.DetailsFragment;
import org.pdnk.goodweather.Interfaces.ILocation;
import org.pdnk.goodweather.Interfaces.IManager;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Inflicted on 9/12/2015.
 */
public class NavigationManager implements FragmentManager.OnBackStackChangedListener, Observer, IManager
{
    private final AppCompatActivity parentActivity;

    private final String FRAG_HOME = "home";
    private final String FRAG_DETAILS = "details";
    private final String FRAG_FAVOURITES = "favourites";

    private NavigationState currentNavigationState = NavigationState.UNKNOWN;
    private int prevBackStackCnt = 0;
    private ILocation lastSelectedLocation;
    private View refreshBtn;
    private View favBtn;
    private android.support.v7.widget.SearchView searchView;

    public NavigationManager(AppCompatActivity parent)
    {
        parentActivity = parent;
    }

    public void initialise()
    {
        refreshBtn = parentActivity.findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onRefreshBtnClick(v);
            }
        });

        favBtn = parentActivity.findViewById(R.id.favBtn);
        favBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onFavouritesBtnClick(v);
            }
        });

        ManagerFactory.getHistoryMgr().addObserver(this);
        ManagerFactory.getFavouriteManager().addObserver(this);
        ManagerFactory.getGPSManager().addObserver(this);
    }

    public void cleanUp()
    {
        ManagerFactory.getFavouriteManager().deleteObserver(this);
        ManagerFactory.getHistoryMgr().deleteObserver(this);
        ManagerFactory.getGPSManager().deleteObserver(this);
    }


    public void initActionbarMenu(Menu menu)
    {
        parentActivity.getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setIconifiedByDefault(true);

        View plate = searchView.findViewById(R.id.search_plate);
        plate.setBackgroundResource(R.drawable.element_semi_transparent);

        EditText searchEdit = (EditText) searchView.findViewById(R.id.search_src_text);
        searchEdit.setHintTextColor(Color.DKGRAY);
        searchEdit.setTextColor(Color.DKGRAY);
        searchEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.search_text_size));
        searchEdit.setFocusableInTouchMode(true);
        searchEdit.setFocusable(true);

        searchView.setOnQueryTextListener(new LocationSearchListener(ManagerFactory.getHistoryMgr(), searchView));

        SpannableStringBuilder ssb = new SpannableStringBuilder("  " + parentActivity.getString(R.string.search_hint));
        int textSize = (int) (searchEdit.getTextSize() * 1.25);
        Drawable magIcon = parentActivity.getResources().getDrawable(R.drawable.ic_search_black_18dp);
        if (magIcon != null)
        {
            magIcon.setBounds(0, 0, textSize, textSize);
            ssb.setSpan(new ImageSpan(magIcon),
                               1,
                               2,
                               Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        searchEdit.setHint(ssb);

        searchView.setMaxWidth((int) (parentActivity.getResources().getDisplayMetrics().widthPixels * 0.7));

    }

    private void navigateHome()
    {
        FragmentManager fm = parentActivity.getSupportFragmentManager();

        Fragment home = new ContentFragment(ManagerFactory.getHistoryMgr());
        Bundle b = new Bundle();
        b.putString(ContentFragment.ARG_TITLE, parentActivity.getString(R.string.recent_locations_text));
        b.putBoolean(ContentFragment.ARG_REORDER_ON_SELECT, true);

        if (ManagerFactory.getFavouriteManager().getContent().isEmpty())
        {
            b.putString(ContentFragment.ARG_EMPTY1, parentActivity.getString(R.string.empty_line1));
            b.putString(ContentFragment.ARG_EMPTY2, parentActivity.getString(R.string.empty_line2));
        } else
        {
            b.putString(ContentFragment.ARG_EMPTY1, parentActivity.getString(R.string.empty_search_1));
            b.putString(ContentFragment.ARG_EMPTY2, parentActivity.getString(R.string.empty_search_2));
        }
        home.setArguments(b);

        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(0, 0, 0, 0);
        ft.replace(R.id.fragmentContainer, home, FRAG_HOME);
        ft.commitAllowingStateLoss();
    }

    private void navigateDetails()
    {
        FragmentManager fm = parentActivity.getSupportFragmentManager();

        Fragment details = new DetailsFragment(lastSelectedLocation);

        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, R.anim.slide_left, R.anim.slide_right);
        ft.add(R.id.fragmentContainer, details, FRAG_DETAILS);
        ft.addToBackStack(FRAG_DETAILS);

        ft.commitAllowingStateLoss();

    }

    private void navigateFavourites()
    {
        ManagerFactory.getFavouriteManager().updateAll();

        FragmentManager fm = parentActivity.getSupportFragmentManager();

        Fragment favourites = new ContentFragment(ManagerFactory.getFavouriteManager());
        Bundle b = new Bundle();
        b.putString(ContentFragment.ARG_TITLE, parentActivity.getString(R.string.title_favourites));
        b.putString(ContentFragment.ARG_EMPTY1, parentActivity.getString(R.string.empty_line1));
        b.putString(ContentFragment.ARG_EMPTY2, parentActivity.getString(R.string.empty_line2));

        favourites.setArguments(b);

        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, R.anim.slide_left, R.anim.slide_right);
        ft.add(R.id.fragmentContainer, favourites, FRAG_FAVOURITES);
        ft.addToBackStack(FRAG_FAVOURITES);

        ft.commitAllowingStateLoss();

    }

    @Override
    public void onBackStackChanged()
    {
        ActionBar ab = parentActivity.getSupportActionBar();

        FragmentManager fm = parentActivity.getSupportFragmentManager();

        int currentCount = fm.getBackStackEntryCount();

        if (fm.getBackStackEntryCount() > 0)
        {
            //update state only if popping from BS
            if (currentCount < prevBackStackCnt)
            {
                FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);

                //only explicitly set to favourites as favourites can be only on level 2,
                if (Objects.equals(entry.getName(), FRAG_FAVOURITES))
                    setNavigationState(NavigationState.FAVOURITES, true);
            }

            if (ab != null)
            {
                ab.setDisplayHomeAsUpEnabled(true);
            }
        } else
        {
            if (ab != null)
            {
                ab.setDisplayHomeAsUpEnabled(false);
            }
            setNavigationState(NavigationState.HOME, true);
        }


        prevBackStackCnt = currentCount;

    }

    private void onFavouritesBtnClick(View v)
    {
        setNavigationState(NavigationState.FAVOURITES, false);
    }

    private void onRefreshBtnClick(View v)
    {
        if (currentNavigationState == NavigationState.HOME)
            ManagerFactory.getHistoryMgr().updateAll();
        else if (currentNavigationState == NavigationState.FAVOURITES)
            ManagerFactory.getFavouriteManager().updateAll();
    }

    public void setNavigationState(NavigationState newState, boolean preserveBackstack)
    {
        switch (newState)
        {
            case HOME:
                if (!preserveBackstack)
                {
                    FragmentManager fm = parentActivity.getSupportFragmentManager();
                    fm.popBackStackImmediate(null, 0);
                }
                navigateHome();
                break;
            case DETAILS:
                navigateDetails();
                break;
            case FAVOURITES:
                if (currentNavigationState == NavigationState.HOME)
                    navigateFavourites();
                break;
        }

        currentNavigationState = newState;

        updateButtonVisibility();
    }

    private void updateButtonVisibility()
    {
        parentActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                refreshBtn.setVisibility(View.INVISIBLE);
                favBtn.setVisibility(View.INVISIBLE);

                switch (currentNavigationState)
                {
                    case HOME:
                        refreshBtn.setVisibility(ManagerFactory.getHistoryMgr().getContent().isEmpty() ? View.INVISIBLE : View.VISIBLE);
                        favBtn.setVisibility(ManagerFactory.getFavouriteManager().getContent().isEmpty() ? View.INVISIBLE : View.VISIBLE);
                        break;
                    case DETAILS:
                        refreshBtn.setVisibility(View.INVISIBLE);
                        favBtn.setVisibility(View.INVISIBLE);
                        break;
                    case FAVOURITES:
                        refreshBtn.setVisibility(View.VISIBLE);
                        favBtn.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });


    }

    @Override
    public void update(Observable observable, Object data)
    {

        if (observable == ManagerFactory.getHistoryMgr())
        {
            ContentManager.UpdateTrait trait = (ContentManager.UpdateTrait) data;
            switch (trait.action)
            {
                case ADDEDNEW:
                    if (trait.location != null && Options.AUTO_ADD_FAVOURITES)
                        ManagerFactory.getFavouriteManager().addItem(trait.location);
                    break;
                case SELECTEDITEM:
                    lastSelectedLocation = trait.location;
                    setNavigationState(NavigationState.DETAILS, false);
                    break;
            }
            updateButtonVisibility();

        } else if (observable == ManagerFactory.getFavouriteManager())
        {
            ContentManager.UpdateTrait trait = (ContentManager.UpdateTrait) data;
            switch (trait.action)
            {
                case CLEAR:
                    setNavigationState(NavigationState.HOME, false);
                    break;
                case SELECTEDITEM:
                    //if item was selected by favourites, update history as the main container.
                    //this will also trigger the details display as per normal handling

                    //if option enabled, store item to history
                    if (Options.STORE_HISTORY_FROM_FAVOURITES)
                        ManagerFactory.getHistoryMgr().addItem(trait.location);

                    ManagerFactory.getHistoryMgr().setSelectedLocation(trait.location, true);
                    break;
            }

            updateButtonVisibility();
        } else if (observable == ManagerFactory.getGPSManager())
        {
            //findViewById(R.id.action_location).setEnabled(true);

            if (data == null || ((String) data).isEmpty())
            {
                showFailureMessage("GPS provider is not available");
            } else
            {
                searchView.setIconified(false);
                searchView.setQuery((String) data, Options.SUBMIT_GPS_SEARCH);
            }

        }

    }

    private void showFailureMessage(final String msg)
    {
        final AlertDialog.Builder b = new AlertDialog.Builder(parentActivity);

        parentActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                b.setTitle("Update failed");
                b.setMessage(msg);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setPositiveButton("Got it", null);

                b.create().show();
            }
        });

    }

    public enum NavigationState
    {
        UNKNOWN,
        HOME,
        DETAILS,
        FAVOURITES
    }
}
