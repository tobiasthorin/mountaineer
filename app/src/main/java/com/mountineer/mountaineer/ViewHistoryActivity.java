package com.mountineer.mountaineer;

import android.app.AlertDialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.mountineer.mountaineer.database.DatabaseHelper;
import com.mountineer.mountaineer.database.LocationAdapter;

public class ViewHistoryActivity extends AppCompatActivity {

    //Private variables
    private ListView lvHistory;
    private DatabaseHelper databaseHelper;
    private LocationAdapter locationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        //get list view and an adapter for it
        lvHistory = (ListView) findViewById(R.id.lvHistoryList);
        locationAdapter = new LocationAdapter(getApplicationContext(), R.layout.location_list_item);

        //Do the database
        databaseHelper = new DatabaseHelper(getApplicationContext());

        //populate the list in the helper with data from the database
        populateList();

        //set the list view to use the now populated adapter
        lvHistory.setAdapter(locationAdapter);
    }

    private void populateList() {
        Cursor allData = databaseHelper.getAllData();

        if (allData.getCount() < 1) {
            displayErrorMessage("Error", "No items in database");
            return;
        }

        while (allData.moveToNext()) {
            String id = allData.getString(0);
            String date = allData.getString(1);
            String location = allData.getString(2);
            String altitude = allData.getString(3);

            locationAdapter.add(new MountaineerLocation(id, date, altitude, location));
        }
    }

    private void displayErrorMessage(String title, String message) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();

    }
}
