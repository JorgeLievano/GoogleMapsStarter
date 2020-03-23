package com.example.googlemapsstarter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPS implements LocationListener {

    private MapsActivity mapsActivity;
    private LocationManager locationManager;

    @SuppressLint("MissingPermission")
    public GPS(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
        this.locationManager = (LocationManager) this.mapsActivity.getSystemService(this.mapsActivity.LOCATION_SERVICE);

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        //this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);

    }


    @Override
    public void onLocationChanged(Location location) {
        mapsActivity.updateLocation(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
