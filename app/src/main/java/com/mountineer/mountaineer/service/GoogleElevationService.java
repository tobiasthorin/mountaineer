package com.mountineer.mountaineer.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Tobias on 10/02/16.
 */
public class GoogleElevationService extends Service {

    private static final String API_KEY = "AIzaSyCYW9c5zsJXv6Ep6_cHehji-GSEP0xq1Vk";
    private Exception error;
    private ElevationServiceCallback callback;

    public GoogleElevationService(ElevationServiceCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void refreshLocation(final double latitude, final double longitude) {

        new AsyncTask<Double, Void, String>() {

            //Fetching API data is a background task
            @Override
            protected String doInBackground(Double... params) {

                //Specify the query for Google
                String endpoint = String.format("https://maps.googleapis.com/maps/api/elevation/json?locations=%s,%s&key=%s", latitude, longitude, API_KEY);

                try {
                    //Establish the connection
                    URL url = new URL(endpoint);
                    URLConnection urlConnection = url.openConnection();

                    //Get some data from the connection
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    //Make the data into a big string
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }

                    //Pass the big string to the next step, where it will be converted to a JSON object
                    return result.toString();


                } catch (MalformedURLException e) {
                    error = e;
                    return null;
                } catch (IOException e) {
                    error = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {

                //Let's see if there was an error in the previous step
                if (s == null && error != null) {
                    callback.serviceFailure(error);
                    return;
                }

                //No error? Time to make the big string into a JSON object
                try {
                    JSONObject data = new JSONObject(s);

                    //Find the right value
                    Double returnValue = data.optJSONArray("results").optJSONObject(0).optDouble("elevation");

                    //And back we go
                    callback.serviceSuccess(returnValue);

                } catch (JSONException e) {
                    callback.serviceFailure(e);
                }
            }
        }.execute();
    }
}
