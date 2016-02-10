package com.mountineer.mountaineer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.mountineer.mountaineer.service.ElevationServiceCallback;
import com.mountineer.mountaineer.service.GoogleElevationService;

public class MainActivity extends AppCompatActivity implements LocationListener, ElevationServiceCallback {

    //Private variables
    private TextView txtLongitude;
    private TextView txtLatitude;
    private TextView txtElevation;
    private LocationManager locationManager;
    private String provider;
    private GoogleElevationService googleElevationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout
        setContentView(R.layout.activity_main);

        //Get the views
        txtLatitude = (TextView) findViewById(R.id.txtLatitudeValue);
        txtLongitude = (TextView) findViewById(R.id.txtLongitudeValue);
        txtElevation = (TextView) findViewById(R.id.txtAltitudeValue);

        googleElevationService = new GoogleElevationService(this);

        //See if we have permission to access location, otherwise ask.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }

        //Get location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Set provider criteria (Default) and get location
        Criteria criteria = new Criteria();

        provider = locationManager.getBestProvider(criteria, false);

        Location location = locationManager.getLastKnownLocation(provider);

        //Update interface
        updateLocation(location);

    }

    //Updates the text fields in the main interface
    private void updateLocation(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        txtLatitude.setText(String.valueOf(lat));
        txtLongitude.setText(String.valueOf(lng));

        googleElevationService.refreshLocation(lat, lng);
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
    public void serviceSuccess(Double elevation) {

        //Round it off, for beauty
        int displayValue = (int)Math.round(elevation);

        //Set the text
        txtElevation.setText(String.valueOf(displayValue));
    }

    @Override
    public void serviceFailure(Exception e) {
        e.printStackTrace();
    }
}
