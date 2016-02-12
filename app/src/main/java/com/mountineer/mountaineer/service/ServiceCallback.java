package com.mountineer.mountaineer.service;

import org.json.JSONObject;

/**
 * Created by Tobias on 10/02/16.
 */
public interface ServiceCallback {
    void serviceSuccess(JSONObject jsonObject);
    void serviceFailure(Exception e);
}
