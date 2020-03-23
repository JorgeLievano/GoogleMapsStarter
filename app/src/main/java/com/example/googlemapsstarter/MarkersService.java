package com.example.googlemapsstarter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MarkersService extends FragmentActivity {

    public static final double RADIO_TIERRA=6371; //radio promedio de la tierra en km
    private LatLng myLastLocation;
    private Map<String, LatLng> markers;
    private Geocoder geocoder;


    public MarkersService(){
        this.markers= new HashMap<String, LatLng>();
        this.geocoder= new Geocoder(this, Locale.getDefault());
    }

    public void setMyLastLocation(LatLng myLastLocation){
        this.myLastLocation=myLastLocation;
    }

    public LatLng getMyLastLocation(){
        return myLastLocation;
    }

    public void addMarker(String name,LatLng location){
        markers.put(name,location);
    }

    public String locationAddress(double lat,double lon){
        Address address= null;
        try {
            address = geocoder.getFromLocation(lat,lon,1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address.getAddressLine(0)+", "+address.getLocality();
    }

    //formula de harvesine para la distancia
    public double distanceM(LatLng a,LatLng b){
        double distance=0;
        double latA=Math.toRadians(a.latitude);
        double latB=Math.toRadians(b.latitude);
        double dLat=(latA - latB);
        double dLon= Math.toRadians(a.longitude-b.longitude);
        double sinLat= Math.sin(dLat/2);
        double sinLon= Math.sin(dLon/2);
        double pre1= (sinLat*sinLat)+Math.cos(latA)*Math.cos(latB)*sinLon*sinLon;
        double pre2=2*Math.asin(Math.min(1.0,Math.sqrt(pre1)));
        distance=RADIO_TIERRA*pre2*1000;
        return distance;
    }

    public double distanceFromUserM(String makerid){
        double distance= distanceM(myLastLocation,markers.get(makerid));
        return distance;
    }

    public HashMap<String,Double> getAllDistances(){
        HashMap<String, Double> places = new HashMap<String, Double>();
        for(String place : markers.keySet()){
            double distance= distanceFromUserM(place);
            places.put(place,distance);
        }
        return places;
    }

    public boolean hasMarkers(){
        return !markers.isEmpty();
    }

    public boolean exist(String name){
        return markers.containsKey(name);
    }

}
