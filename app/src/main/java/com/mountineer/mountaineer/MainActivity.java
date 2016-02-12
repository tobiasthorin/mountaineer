package com.mountineer.mountaineer;

import android.Manifest;
import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mountineer.mountaineer.service.ElevationServiceCallback;
import com.mountineer.mountaineer.service.GoogleElevationService;

public class MainActivity extends AppCompatActivity implements LocationListener, ElevationServiceCallback {

    private static final int LOCATION_REQUEST_CODE = 1;

    //Private variables
    private TextView txtLongitude;
    private TextView txtLatitude;
    private TextView txtElevation;
    private ImageView imgRefresh;
    private ImageView imgInfo;

    private LocationManager locationManager;
    private String provider;
    private GoogleElevationService googleElevationService;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout
        setContentView(R.layout.activity_main);

        //Get the views
        txtLatitude = (TextView) findViewById(R.id.txtLatitudeValue);
        txtLongitude = (TextView) findViewById(R.id.txtLongitudeValue);
        txtElevation = (TextView) findViewById(R.id.txtAltitudeValue);
        imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
        imgInfo = (ImageView) findViewById(R.id.imgInfo);

        //Get location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Request location for both network and gps
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        //Create an elevation service
        googleElevationService = new GoogleElevationService(this);

        int permissionCheckFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        //For new android, we need to ask for permissions here
        if (Build.VERSION.SDK_INT >= 23) {
            //Do we already have permission?
            if (permissionCheckFine != PackageManager.PERMISSION_GRANTED && permissionCheckCoarse != PackageManager.PERMISSION_GRANTED) {
                //If not, request it!
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

            } else {
                updateLocationAndElevation();
            }
        } else {
            updateLocationAndElevation();
        }

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
    }


    //Updates the text fields in the main interface
    private void updateLocationAndElevation() {



        try {
            //Set a criteria (default) for choosing provider, then get the location!
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            currentLocation = locationManager.getLastKnownLocation(provider);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        //Set location fields
        double lat = currentLocation.getLatitude();
        double lng = currentLocation.getLongitude();

        txtLatitude.setText(String.valueOf(lat));
        txtLongitude.setText(String.valueOf(lng));

        //Do elevation call
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
    public void serviceSuccess(Double elevation) {

        //Round it off, for beauty
        int displayValue = (int) Math.round(elevation);

        //Set the text
        txtElevation.setText(String.valueOf(displayValue));
    }

    @Override
    public void serviceFailure(Exception e) {
        showMessage("Error", e.getMessage());
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocationAndElevation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
