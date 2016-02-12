package com.mountineer.mountaineer.service;

import android.content.Context;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by Tobias on 12/02/16.
 */
public class ElevationServiceCallback extends GenericServiceCallback implements ServiceCallback {

    public ElevationServiceCallback(Context context, TextView view) {
        super(context, view);
    }

    @Override
    public void serviceSuccess(JSONObject jsonObject) {

        //Find the right value
        Double elevation = jsonObject.optJSONArray("results").optJSONObject(0).optDouble("elevation");

        //Round it off, for beauty
        int displayValue = (int) Math.round(elevation);

        //Set the texts
        view.setText(String.valueOf(displayValue));
    }
}
