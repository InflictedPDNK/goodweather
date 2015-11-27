package org.pdnk.goodweather;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, Observer
{
    ContentManager<ILocation> content;


    enum NavigationState {
        UNKNOWN,
        HOME,
        DETAILS,
        FAVOURITES
    }

    private NavigationState currentNavigationState = NavigationState.UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initToolbar();

        content = new ContentManager<>(this);
        content.addObserver(this);


        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        android.support.v7.widget.SearchView sv = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();

        View plate = sv.findViewById(R.id.search_plate);
        plate.setBackgroundColor(0x9fffffff);

    //   ImageView searchIcon = (ImageView) sv.findViewById(R.id.search_mag_icon);
     //   searchIcon.setImageAlpha(0);

        EditText searchEdit = (EditText) sv.findViewById(R.id.search_src_text);
        searchEdit.setHintTextColor(Color.DKGRAY);
        searchEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.search_text_size));
        searchEdit.setFocusableInTouchMode(true);
        searchEdit.setFocusable(true);

        sv.setOnQueryTextListener(new LocationSearchListener(content));

        SpannableStringBuilder ssb = new SpannableStringBuilder("  " + getString(R.string.search_hint));
        int textSize = (int) (searchEdit.getTextSize() * 1.25);
        Drawable magIcon = getResources().getDrawable(R.drawable.ic_search_black_18dp);
        magIcon.setBounds(0, 0, textSize, textSize);
        ssb.setSpan(new ImageSpan(magIcon),
                           1,
                           2,
                           Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchEdit.setHint(ssb);

        sv.setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.7));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.action_location)
        {
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

        setNavigationState(NavigationState.HOME);
    }

    void setNavigationState(NavigationState newState)
    {


        switch (newState)
        {
            case HOME:
                navigateHome();
                break;
            case DETAILS:
                navigateDetails();
                break;
            case FAVOURITES:
                break;
        }

        currentNavigationState = newState;
    }

    void navigateHome()
    {
        FragmentManager fm = getSupportFragmentManager();

        fm.popBackStackImmediate("home", 0);

        resetHome();
    }

    void navigateDetails()
    {

    }

    void resetHome()
    {
        FragmentManager fm = getSupportFragmentManager();

        Fragment home = new HomeFragment(content);
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.fragmentContainer, home, "home");
        ft.setCustomAnimations(0,0,0,0);

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

        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            ab.setDisplayHomeAsUpEnabled(true);
        else
            ab.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void update(Observable observable, Object data)
    {
        ContentManager.UpdateTrait trait = (ContentManager.UpdateTrait) data;

        if(trait.target != ContentManager.UpdateTarget.FAVOURITES )
            resetHome();
    }

}
