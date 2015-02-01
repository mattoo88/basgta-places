package com.bagsta.places;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class LocationManagerService implements LocationListener, LocationService{
	
	private boolean isNetworkActive;
	private boolean isGPSActive;
	private Location location;
	
	private LocationManager locationManager;
	
	private Context mContext;
	private LocationServiceCallbacks serviceCallbacks;
	
	LocationManagerService(Context mContext, LocationServiceCallbacks serviceCallbacks) {
		this.mContext = mContext;
		this.serviceCallbacks = serviceCallbacks;
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	}
	
//	Location getLocation() throws Exception {
//
//		if (!(isNetworkActive | isGPSActive))
//			throw new Exception("Call locationCanBeFound before attempting to retrieve location");
//
//		if (isNetworkActive) {
//
//			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		}
//
//		if (isGPSActive) {
//
//			if (location == null) {
//				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//				location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//			}
//		}
//
//		Toast.makeText(mContext," location : "+location, Toast.LENGTH_SHORT).show();
//
//		return location;
//	}

	boolean canLocationBeFound() {
		
		isNetworkActive = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		isGPSActive		= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		return isNetworkActive | isGPSActive;
		
	}

	@Override
	public void onLocationChanged(Location location) {
		serviceCallbacks.onLocationChanged(location);
		Toast.makeText(mContext," onLocationChanged : "+location, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
//		serviceCallbacks.onStatusChanged(provider, status, extras);
//		Toast.makeText(mContext,provider+" status : "+status, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
//		serviceCallbacks.onProviderEnabled(provider);
//		Toast.makeText(mContext," provider "+provider, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {

//        serviceCallbacks.onProviderDisabled(provider);
	}

    @Override
    public void connect() {
//        if (!(isNetworkActive | isGPSActive))
//            throw new Exception("Call locationCanBeFound before attempting to retrieve location");
        canLocationBeFound();

        if (isNetworkActive) {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (isGPSActive) {

            if (location == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

        Toast.makeText(mContext," location : "+location, Toast.LENGTH_SHORT).show();

        if (location != null)
            onLocationChanged(location);
    }

    @Override
    public void disconnect() {
        locationManager.removeUpdates(this);
    }
}
