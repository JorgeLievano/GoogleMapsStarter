package com.example.googlemapsstarter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import  com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import  com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,OnMyLocationClickListener, OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    final private int PERMISSION_STATUS=111;
    private GoogleMap mMap;
    private GPS gps;
    private EditText editTextName;
    private ImageButton buttonAddMark;
    private TextView textViewInfo;
    private Marker provitionalMarker;
    private MarkersService markersService;
    private Marker userMarker;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        gps=new GPS(this);
        markersService=new MarkersService();
        editTextName=findViewById(R.id.editTextPlace);
        editTextName.setEnabled(false);
        buttonAddMark=findViewById(R.id.buttonAddMark);
        buttonAddMark.setEnabled(false);
        buttonAddMark.setOnClickListener(
                (v)->{
                    addMarker();
                    updateInfo(markersService.getMyLastLocation().latitude,markersService.getMyLastLocation().longitude);
                }
        );

        textViewInfo=findViewById(R.id.textViewInfo);
        userMarker=null;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void addMarker(){
        String name=editTextName.getText().toString();
        LatLng position=provitionalMarker.getPosition();
        mMap.addMarker(new MarkerOptions().title(name).position(position).snippet("calculando"));
        markersService.addMarker(name,position);

        editTextName.setText("Place Name");
        editTextName.setEnabled(false);
        buttonAddMark.setEnabled(false);
        provitionalMarker.remove();
        provitionalMarker=null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateLocation(double v, double v1){
        LatLng location= new LatLng(v,v1);
        markersService.setMyLastLocation(location);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        updateInfo(v,v1);
        if(userMarker!=null){
            userMarker.remove();
            userMarker=null;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateInfo( double v, double v1){
        String info="Location Info";
        if(markersService.hasMarkers()){
            HashMap<String,Double> places=markersService.getAllDistances();
            Map.Entry<String,Double> cercano= Collections.min(places.entrySet(), Map.Entry.comparingByValue());
            if(cercano.getValue().doubleValue()<3){
                info="Usted esta en "+cercano.getKey();
            }else{
                info="El lugar mas cercano es "+cercano.getKey();
            }

        }else{
            info="You are in Lat: "+ v +" Lon: "+ v1;
        }
        textViewInfo.setText(info);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        verifiedPermission();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        updateLocation(3.4,-76.5);
    }

    private void verifiedPermission(){
        int permissionCoarseLoca= ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionFineLoca= ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCoarseLoca != PackageManager.PERMISSION_GRANTED || permissionFineLoca != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_STATUS);
            }
        }

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        if(userMarker==null){
            LatLng myLocation=new LatLng(location.getLatitude(),location.getLongitude());
            String addres=markersService.locationAddress(location.getLatitude(),location.getLongitude());
             userMarker=mMap.addMarker(new MarkerOptions().position(myLocation).title("You").snippet(addres));
             userMarker.showInfoWindow();
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(provitionalMarker !=null){
            provitionalMarker.remove();
        }
        provitionalMarker= mMap.addMarker(new MarkerOptions().position(latLng));

        buttonAddMark.setEnabled(true);
        editTextName.setEnabled(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String title=marker.getTitle();
        if(markersService.exist(title)){
            double distance=markersService.distanceFromUserM(title);
            marker.setSnippet("Distancia: "+ distance+" m");
        }
        return false;
    }
}
