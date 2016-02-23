package com.mountineer.mountaineer.database;

import com.mountineer.mountaineer.MountaineerLocation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Tobias on 23/02/16.
 */
public class LocationAdapterTest {

    LocationAdapter locationAdapter;

    @Before
    public void setUp() throws Exception {
        locationAdapter = new LocationAdapter(null, 0);

        locationAdapter.add(new MountaineerLocation("0", "2016-01-01", "34", "Home"));
        locationAdapter.add(new MountaineerLocation("1", "2016-01-01", "4", "Home"));
        locationAdapter.add(new MountaineerLocation("2", "2016-01-01", "5434", "Home"));
        locationAdapter.add(new MountaineerLocation("3", "2016-01-01", "36", "Home"));
        locationAdapter.add(new MountaineerLocation("4", "2016-01-01", "10", "Home"));
    }

    @Test
    public void testGetCount() throws Exception {

        double expected = 5;
        double actual = locationAdapter.getCount();

        assertEquals(expected, actual, 0);

    }
}