package com.mountineer.mountaineer.service;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.TextView;

/**
 * Created by Tobias on 12/02/16.
 */
public abstract class GenericServiceCallback implements ServiceCallback {

    protected TextView view;
    protected Context context;

    public GenericServiceCallback(Context context, TextView view) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void serviceFailure(Exception e) {
        showMessage("Error", e.getMessage());
    }

    //makes an error message with title and message
    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }
}
