package com.mountineer.mountaineer.service;

import android.content.Context;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by Tobias on 12/02/16.
 */
public class LocationServiceCallback extends GenericServiceCallback implements ServiceCallback {

    public LocationServiceCallback(Context context, TextView view) {
        super(context, view);
    }

    @Override
    public void serviceSuccess(JSONObject jsonObject) {

        //find the address
        String location = jsonObject.optJSONArray("results").optJSONObject(1).optString("formatted_address");

        //and set it
        view.setText(location);
    }
}
