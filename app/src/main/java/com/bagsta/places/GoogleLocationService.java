package com.bagsta.places;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by test on 1/14/2015.
 */
public class GoogleLocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationService, LocationListener {
    static final String TAG = "GoogleLocationService";
    Context mContext;
    GoogleLocationService googleLocationService;
    GoogleApiClient googleApiClient;
    LocationServiceCallbacks locationServiceCallbacks;
    LocationRequest locationRequest;

    GoogleLocationService(Context context, LocationServiceCallbacks locationServiceCallbacks) {
        mContext = context;
        googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        this.locationServiceCallbacks = locationServiceCallbacks;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    void startLocationUpdates(){
       LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }

    void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
    }

    public void connect() {
        if (!googleApiClient.isConnected() &&  !googleApiClient.isConnecting())
            googleApiClient.connect();
    }

    @Override
    public void disconnect() {
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"connect");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(mContext, " location : " + location, Toast.LENGTH_SHORT).show();
        locationServiceCallbacks.onLocationChanged(location);
    }
}
