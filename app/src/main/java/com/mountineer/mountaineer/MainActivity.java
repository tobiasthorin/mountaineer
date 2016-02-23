package com.mountineer.mountaineer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mountineer.mountaineer.database.DatabaseHelper;
import com.mountineer.mountaineer.service.ApiService;
import com.mountineer.mountaineer.service.ElevationServiceCallback;
import com.mountineer.mountaineer.service.LocationServiceCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int LOCATION_REQUEST_CODE = 1;

    //Private variables
    private TextView txtLongitude;
    private TextView txtLatitude;
    private TextView txtElevation;
    private TextView txtLocation;
    private ImageView imgRefresh;
    private ImageView imgInfo;
    private Button btnViewHistory;
    private Button btnSaveLocation;

    private LocationManager locationManager;
    private String provider;
    private ApiService elevationApiService;
    private ApiService locationApiService;
    private ElevationServiceCallback elevationServiceCallback;
    private LocationServiceCallback locationServiceCallback;
    private Location currentLocation;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout
        setContentView(R.layout.activity_main);

        //Get the views
        txtLatitude = (TextView) findViewById(R.id.txtLatitudeValue);
        txtLongitude = (TextView) findViewById(R.id.txtLongitudeValue);
        txtElevation = (TextView) findViewById(R.id.txtAltitudeValue);
        txtLocation = (TextView) findViewById(R.id.txtLocationValue);
        imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
        imgInfo = (ImageView) findViewById(R.id.imgInfo);
        btnViewHistory = (Button) findViewById(R.id.btnHistory);
        btnSaveLocation = (Button) findViewById(R.id.btnSaveLocation);

        int permissionCheckFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        //For new android, we need to ask for permissions here
        if (Build.VERSION.SDK_INT >= 23) {
            //Do we already have permission?
            if (permissionCheckFine != PackageManager.PERMISSION_GRANTED && permissionCheckCoarse != PackageManager.PERMISSION_GRANTED) {
                //If not, request it!
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                setUpServices();
            }
        } else {
            setUpServices();
        }

        //Do a database
        databaseHelper = new DatabaseHelper(getApplicationContext());

        //Button functionality
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocationAndElevation();
                Toast.makeText(getApplicationContext(), "updated", Toast.LENGTH_SHORT).show();
            }
        });

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infoIntent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(infoIntent);
            }
        });

        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(getApplicationContext(), ViewHistoryActivity.class);
                startActivity(historyIntent);
            }
        });

        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = findCurrentDate();
                String location = txtLocation.getText().toString();
                String altitude = txtElevation.getText().toString();

                int isInsert = databaseHelper.insertData(date, location, altitude);

                switch (isInsert) {
                    case DatabaseHelper.INSERTION_SUCCESS:
                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        break;
                    case DatabaseHelper.INSERTION_FAILURE:
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        break;
                    case DatabaseHelper.INSERTION_DUPLICATE:
                        Toast.makeText(getApplicationContext(), "Location already saved", Toast.LENGTH_SHORT).show();
                        break;
                    case DatabaseHelper.INSERTION_INCOMPLETE:
                        Toast.makeText(getApplicationContext(), "Unable to save, no data", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void setUpServices() {

        //Get location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Create urls for api calls
        String elevationUrl = "https://maps.googleapis.com/maps/api/elevation/json?locations=%s,%s&key=%s";
        String locationUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s";

        //Create elevation service
        elevationServiceCallback = new ElevationServiceCallback(this, txtElevation);
        elevationApiService = new ApiService(elevationServiceCallback, elevationUrl);

        //Create location service
        locationServiceCallback = new LocationServiceCallback(this, txtLocation);
        locationApiService = new ApiService(locationServiceCallback, locationUrl);

        updateLocationAndElevation();
    }

    private Location getLastLocation() {

        Location returnLocation = null;

        //Find all the possible providers...
        List<String> providers = locationManager.getProviders(true);

        //...and try to get the location from every single one
        for (String provider : providers) {

            Location testLocation = null;

            try {
                testLocation = locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            if (returnLocation == null || testLocation.getAccuracy() > returnLocation.getAccuracy()) {
                returnLocation = testLocation;
                Log.d("Provider", returnLocation.getProvider());
            }
        }

        return returnLocation;
    }

    private String findCurrentDate() {
        //Get current time
        Calendar now = Calendar.getInstance();

        //Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //Return formatted string
        return dateFormat.format(now.getTime());
    }


    //Updates the text fields in the main interface
    private void updateLocationAndElevation() {

        currentLocation = getLastLocation();

        //Set location fields
        double lat = currentLocation.getLatitude();
        double lng = currentLocation.getLongitude();

        txtLatitude.setText(String.valueOf(lat));
        txtLongitude.setText(String.valueOf(lng));

        //Do api calls
        elevationApiService.refreshLocation(lat, lng);
        locationApiService.refreshLocation(lat, lng);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled provider: " + provider, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider: " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpServices();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }


}
