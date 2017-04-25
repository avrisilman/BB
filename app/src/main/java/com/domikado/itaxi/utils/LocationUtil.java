package com.domikado.itaxi.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtil {

    private LocationManager lm;

    public LocationUtil(Context context) {
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public final double[] getLatLong() {
        double[] gps = new double[2];
        gps[0] = 0;
        gps[1] = 0;

        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location lastKnownLocationNetwork = null;
        Location lastKnownLocationGps = null;
        Location betterLastKnownLocation = null;

        if (isNetworkEnabled) {
            lastKnownLocationNetwork = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (isGPSEnabled) {
            lastKnownLocationGps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (lastKnownLocationGps != null && lastKnownLocationNetwork != null) {
            betterLastKnownLocation = getBetterLocation(lastKnownLocationGps, lastKnownLocationNetwork);
        }

        if (betterLastKnownLocation == null) {
            betterLastKnownLocation = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        if (betterLastKnownLocation != null) {
            gps[0] = betterLastKnownLocation.getLatitude();
            gps[1] = betterLastKnownLocation.getLongitude();
        }
        return gps;
    }

    private Location getBetterLocation(final Location location1, final Location location2) {
        if (location1.getAccuracy() >= location2.getAccuracy()) {
            return location1;
        }
        else {
            return location2;
        }
    }
}