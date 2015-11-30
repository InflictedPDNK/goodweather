package org.pdnk.goodweather;

import android.test.ActivityUnitTestCase;

import org.pdnk.goodweather.Interfaces.ILocation;

import java.util.Observable;
import java.util.Observer;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

public class ContentManagerTest extends ActivityUnitTestCase<MainActivity>
{
    public ContentManagerTest() {
        super(MainActivity.class);
    }

    ContentManager cm;
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        cm = new ContentManager(getInstrumentation()
                                        .getTargetContext(), "test_manager");

    }


    public void testDataSaveLoad() throws Exception
    {
        ILocation loc = WeatherLocation.createFromSearchQuery("testlocation");

        cm.addItem(loc);

        cm.saveLocalData();

        cm.removeAll();

        cm.loadLocalData();

        assertNotNull(cm.getContent());
        assertFalse(cm.getContent().isEmpty());
        assertEquals(cm.getContent().getFirst().getName(), "testlocation");

    }

    public void testDataAdding() throws Exception
    {
        cm.removeAll();

        ILocation loc = WeatherLocation.createFromSearchQuery("testlocation");

        cm.addItem(loc);

        assertNotNull(cm.getContent());
        assertFalse(cm.getContent().isEmpty());
        assertEquals(cm.getContent().getFirst().getName(), "testlocation");
    }

    public void testDataRemoval() throws Exception
    {
        cm.removeAll();

        ILocation loc = WeatherLocation.createFromSearchQuery("testlocation");

        cm.addItem(loc);

        assertNotNull(cm.getContent());
        assertFalse(cm.getContent().isEmpty());

        cm.removeItem(loc);
        assertTrue(cm.getContent().isEmpty());

        cm.addItem(loc);
        assertNotNull(cm.getContent());
        assertFalse(cm.getContent().isEmpty());

        cm.removeAll();
        assertTrue(cm.getContent().isEmpty());
    }

    public void testDataUpdate() throws Exception
    {
        cm.removeAll();

        ILocation loc = WeatherLocation.createFromSearchQuery("testlocation1");


        cm.addItem(loc);

        assertNotNull(cm.getContent());
        assertFalse(cm.getContent().isEmpty());

        //the default id will be 0, so it must match the containing item effectively updating it
        //instead of adding
        ILocation loc2 = WeatherLocation.createFromSearchQuery("testlocation2");

        cm.addItem(loc2);

        assertFalse(cm.getContent().size() > 1);
        assertEquals(cm.getContent().getFirst().getName(), "testlocation2");
    }

    public void testLoadFromProvider() throws Exception
    {
        cm.removeAll();

        ILocation loc = WeatherLocation.createFromSearchQuery("Sydney");

        cm.createNewLocation("sydney");

        //wait for data to be downloaded
        Thread.sleep(3000);

        assertNotNull(cm.getContent());
        assertFalse(cm.getContent().isEmpty());
        assertEquals(cm.getContent().getFirst().getName(), "Sydney");
        assertTrue(cm.getContent().getFirst().getId() != 0);
    }



    public void testCallbacks() throws Exception
    {
        cm.removeAll();

        final boolean[] clear_sent = new boolean[1];
        final boolean[] update_sent = new boolean[1];
        final boolean[] new_sent = new boolean[1];
        final boolean[] selected_sent = new boolean[1];
        final boolean[] selected_has_item = new boolean[1];
        final boolean[] update_has_item = new boolean[1];
        final boolean[] new_has_item = new boolean[1];

        cm.addObserver(new Observer()
        {
            @Override
            public void update(Observable observable, Object data)
            {
                ContentManager.UpdateTrait trait = (ContentManager.UpdateTrait) data;
                switch (trait.action)
                {

                    case ADDEDNEW:
                        new_sent[0] = true;
                        new_has_item[0] = trait.location != null;
                        break;
                    case UPDATE:
                        update_sent[0] = true;
                        update_has_item[0] = trait.location != null;
                        break;
                    case CLEAR:
                        clear_sent[0] = true;
                        break;
                    case SELECTEDITEM:
                        selected_sent[0] = true;
                        selected_has_item[0] = trait.location != null;
                        break;
                }
            }


        });

        testDataUpdate();

        assertTrue(new_sent[0]);
        assertTrue(new_has_item[0]);
        assertTrue(update_sent[0]);
        assertTrue(update_has_item[0]);

        ILocation loc = WeatherLocation.createFromSearchQuery("testlocation");
        cm.setSelectedLocation(loc, false);

        assertTrue(selected_sent[0]);
        assertTrue(selected_has_item[0]);

        testDataRemoval();
        assertTrue(clear_sent[0]);

        cm.deleteObservers();

    }

}