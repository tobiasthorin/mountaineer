package com.mountineer.mountaineer.service;

/**
 * Created by Tobias on 10/02/16.
 */
public interface ElevationServiceCallback {
    void serviceSuccess(Double elevation);
    void serviceFailure(Exception e);
}
