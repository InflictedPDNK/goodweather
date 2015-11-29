package org.pdnk.goodweather;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.pdnk.goodweather.Fragments.ContentFragment;
import org.pdnk.goodweather.Fragments.DetailsFragment;
import org.pdnk.goodweather.Interfaces.ILocation;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, Observer
{

    /**
     * CUSTOMISABLE OPTIONS WHICH COULD GO TO APP's SETTINGS:
     */

    final static int OPT_MAX_HISTORY = 10;
    final static boolean OPT_STORE_HISTORY_FROM_FAVOURITES = false;
    final static boolean OPT_ENABLE_FINE_LOCATION = false;
    final static boolean OPT_SUBMIT_GPS_SEARCH = false;
    final static boolean OPT_AUTO_ADD_FAVOURITES = true;
    final static String OPT_FORCE_UNIT_SYSTEM = null;


    /**
     * END OF OPTIONS
     */
    ContentManager contentHistory;
    ContentManager contentFavourites;

    GPSManager locationGPSmgr;

    enum NavigationState {
        UNKNOWN,
        HOME,
        DETAILS,
        FAVOURITES
    }

    final String FRAG_HOME = "home";
    final String FRAG_DETAILS = "details";
    final String FRAG_FAVOURITES = "favourites";

    private NavigationState currentNavigationState = NavigationState.UNKNOWN;

    private int prevBackStackCnt = 0;

    private View refreshBtn;
    private View favBtn;
    private android.support.v7.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onRefreshBtnClick(v);
            }
        });

        favBtn = findViewById(R.id.favBtn);
        favBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onFavouritesBtnClick(v);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initToolbar();

        contentHistory = new ContentManager(this, OPT_MAX_HISTORY);
        contentHistory.addObserver(this);

        contentFavourites = new ContentManager(this);
        contentFavourites.addObserver(this);

        locationGPSmgr = new GPSManager(this);
        locationGPSmgr.addObserver(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);


    }

    private void onFavouritesBtnClick(View v)
    {
        setNavigationState(NavigationState.FAVOURITES, false);
    }

    private void onRefreshBtnClick(View v)
    {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setIconifiedByDefault(true);

        View plate = searchView.findViewById(R.id.search_plate);
        plate.setBackgroundResource(R.drawable.element_semi_transparent);

        EditText searchEdit = (EditText) searchView.findViewById(R.id.search_src_text);
        searchEdit.setHintTextColor(Color.DKGRAY);
        searchEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.search_text_size));
        searchEdit.setFocusableInTouchMode(true);
        searchEdit.setFocusable(true);

        searchView.setOnQueryTextListener(new LocationSearchListener(contentHistory, searchView));

        SpannableStringBuilder ssb = new SpannableStringBuilder("  " + getString(R.string.search_hint));
        int textSize = (int) (searchEdit.getTextSize() * 1.25);
        Drawable magIcon = getResources().getDrawable(R.drawable.ic_search_black_18dp);
        magIcon.setBounds(0, 0, textSize, textSize);
        ssb.setSpan(new ImageSpan(magIcon),
                           1,
                           2,
                           Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchEdit.setHint(ssb);

        searchView.setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.7));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.action_location)
        {
           // item.setEnabled(false);
            locationGPSmgr.requestCoordinates();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void initToolbar()
    {
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        ab.setLogo(R.drawable.gwlogo);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        setNavigationState(NavigationState.HOME, false);
    }

    void setNavigationState(NavigationState newState, boolean preserveBackstack)
    {
        switch (newState)
        {
            case HOME:
                if(!preserveBackstack)
                {
                    FragmentManager fm = getSupportFragmentManager();
                    fm.popBackStackImmediate(null, 0);
                }
                navigateHome();
                break;
            case DETAILS:
                navigateDetails();
                break;
            case FAVOURITES:
                if(currentNavigationState == NavigationState.HOME)
                    navigateFavourites();
                break;
        }

        currentNavigationState = newState;

        updateButtonVisibility();
    }

    private void navigateFavourites()
    {
        FragmentManager fm = getSupportFragmentManager();

        Fragment favourites = new ContentFragment(contentFavourites);
        Bundle b = new Bundle();
        b.putString(ContentFragment.ARG_TITLE, getString(R.string.title_favourites));
        b.putString(ContentFragment.ARG_EMPTY1, getString(R.string.empty_line1));
        b.putString(ContentFragment.ARG_EMPTY2, getString(R.string.empty_line2));

        favourites.setArguments(b);

        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, R.anim.slide_left, R.anim.slide_right);
        ft.add(R.id.fragmentContainer, favourites, FRAG_FAVOURITES);
        ft.addToBackStack(FRAG_FAVOURITES);

        ft.commitAllowingStateLoss();

    }

    void navigateHome()
    {
        FragmentManager fm = getSupportFragmentManager();

        Fragment home = new ContentFragment(contentHistory);
        Bundle b = new Bundle();
        b.putString(ContentFragment.ARG_TITLE, getString(R.string.recent_locations_text));
        b.putBoolean(ContentFragment.ARG_REORDER_ON_SELECT, true);

        if(contentFavourites.getContent().isEmpty())
        {
            b.putString(ContentFragment.ARG_EMPTY1, getString(R.string.empty_line1));
            b.putString(ContentFragment.ARG_EMPTY2, getString(R.string.empty_line2));
        }
        else
        {
            b.putString(ContentFragment.ARG_EMPTY1, getString(R.string.empty_search_1));
            b.putString(ContentFragment.ARG_EMPTY2, getString(R.string.empty_search_2));
        }
        home.setArguments(b);

        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(0, 0, 0, 0);
        ft.replace(R.id.fragmentContainer, home, FRAG_HOME);
        ft.commitAllowingStateLoss();
    }

    void navigateDetails()
    {
        FragmentManager fm = getSupportFragmentManager();

        Fragment details = new DetailsFragment(contentHistory.getSelectedLocation(), contentFavourites);

        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, R.anim.slide_left, R.anim.slide_right);
        ft.add(R.id.fragmentContainer, details, FRAG_DETAILS);
        ft.addToBackStack(FRAG_DETAILS);

        ft.commitAllowingStateLoss();

    }


    @Override
    public boolean onSupportNavigateUp()
    {
        getSupportFragmentManager().popBackStack();

        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackStackChanged()
    {
        ActionBar ab = getSupportActionBar();

        FragmentManager fm = getSupportFragmentManager();

        int currentCount = fm.getBackStackEntryCount();

            if (fm.getBackStackEntryCount() > 0)
            {
                //update state only if popping from BS
                if(currentCount < prevBackStackCnt)
                {
                    FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);

                    //only explicitly set to favourites as favourites can be only on level 2,
                    if (entry.getName() == FRAG_FAVOURITES)
                        setNavigationState(NavigationState.FAVOURITES, true);
                }

                ab.setDisplayHomeAsUpEnabled(true);
            }
            else
            {
                ab.setDisplayHomeAsUpEnabled(false);
                setNavigationState(NavigationState.HOME, true);
            }


        prevBackStackCnt = currentCount;

    }

    @Override
    public void update(Observable observable, Object data)
    {

        if(observable == contentHistory)
        {
            ContentManager.UpdateTrait trait = (ContentManager.UpdateTrait) data;
            switch (trait.action)
            {
                case UPDATE:
                    if(trait.location != null && OPT_AUTO_ADD_FAVOURITES)
                        contentFavourites.addItem((ILocation) trait.location);
                    break;
                case SELECTEDITEM:
                    setNavigationState(NavigationState.DETAILS, false);
                    break;
            }
            updateButtonVisibility();

        }
        else if(observable == contentFavourites)
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
                    if(OPT_STORE_HISTORY_FROM_FAVOURITES)
                        contentHistory.addItem((ILocation) trait.location);

                    contentHistory.setSelectedLocation((ILocation) trait.location, true);
                    break;
            }

            updateButtonVisibility();
        } else if(observable == locationGPSmgr)
        {

            //findViewById(R.id.action_location).setEnabled(true);

            if(data == null || ((String) data).isEmpty())
            {
                showNotification("Failed to obtain GPS coordinates");
            }
            else
            {
                searchView.setIconified(false);
                searchView.setQuery((String) data, OPT_SUBMIT_GPS_SEARCH);
            }

        }

    }

    void updateButtonVisibility()
    {
        refreshBtn.setVisibility(View.INVISIBLE);
        favBtn.setVisibility(View.INVISIBLE);

        switch (currentNavigationState)
        {
            case HOME:
                refreshBtn.setVisibility(contentHistory.getContent().isEmpty()? View.INVISIBLE : View.VISIBLE);
                favBtn.setVisibility(contentFavourites.getContent().isEmpty() ? View.INVISIBLE : View.VISIBLE);
                break;
            case DETAILS:
                refreshBtn.setVisibility(View.VISIBLE);
                favBtn.setVisibility(View.INVISIBLE);
                break;
            case FAVOURITES:
                refreshBtn.setVisibility(View.VISIBLE);
                favBtn.setVisibility(View.INVISIBLE);
                break;
        }

    }

    void showNotification(String message)
    {
        Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


}
