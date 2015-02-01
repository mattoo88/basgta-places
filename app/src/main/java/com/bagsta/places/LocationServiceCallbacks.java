package com.bagsta.places;

import android.location.Location;
import android.os.Bundle;

/**
 * Created by test on 1/14/2015.
 */
interface LocationServiceCallbacks {
    public void onLocationChanged(Location location);
//    public void onStatusChanged(String provider, int status, Bundle extras);
//    public void onProviderEnabled(String provider);
//    public void onProviderDisabled(String provider);
}
