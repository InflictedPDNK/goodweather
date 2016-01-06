package org.pdnk.goodweather;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
{
    private boolean wasSuspended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initGlobalToolbar();

        ManagerFactory.construct(this);

        getSupportFragmentManager().addOnBackStackChangedListener(ManagerFactory.getNavigationManager());
    }

    public void initGlobalToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
        {
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
            ab.setLogo(R.drawable.gwlogo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        ManagerFactory.getNavigationManager().initActionbarMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_location)
        {
            // item.setEnabled(false);
            ManagerFactory.getGPSManager().requestCoordinates();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        ManagerFactory.getNavigationManager().setNavigationState(NavigationManager.NavigationState.HOME, false);
    }

    @Override
    protected void onStop()
    {
        ManagerFactory.getHistoryMgr().saveLocalData();
        ManagerFactory.getFavouriteManager().saveLocalData();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        ManagerFactory.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        wasSuspended = true;
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        if (wasSuspended)
        {
            wasSuspended = false;
            ManagerFactory.getHistoryMgr().updateAll();
        }
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        getSupportFragmentManager().popBackStack();
        return super.onSupportNavigateUp();
    }
}
