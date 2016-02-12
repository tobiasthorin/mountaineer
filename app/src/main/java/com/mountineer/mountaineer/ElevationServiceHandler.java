package com.mountineer.mountaineer;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.TextView;

import com.mountineer.mountaineer.service.ElevationServiceCallback;

/**
 * Created by Tobias on 12/02/16.
 */
public class ElevationServiceHandler implements ElevationServiceCallback {

    private TextView view;
    private Context context;

    public ElevationServiceHandler(Context context, TextView view) {
        this.view = view;
    }

    @Override
    public void serviceSuccess(Double elevation) {
        //Round it off, for beauty
        int displayValue = (int) Math.round(elevation);

        //Set the texts
        view.setText(String.valueOf(displayValue));
    }

    @Override
    public void serviceFailure(Exception e) {
        showMessage("Error", e.getMessage());
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }
}
