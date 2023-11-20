package com.example.imoto;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class NavigationMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;

    //Method to call navigation map activity fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_maps);
        //Check for android version code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_map);
        mapFragment.getMapAsync(this);
    }


    //Check location permissions
    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);

            }
            return false;

        }
        else
            return true;
    }


    //Method to handle permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission Granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        if (client != null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }else {
                    //Permission denied
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


    //Method to set and display current location
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.zoomBy(8));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(8));
        }
    }


    //On click listener for search address
    public void onClickSearch(View view){
        if (view.getId() == R.id.btn_search){

            EditText find_destination = findViewById(R.id.find_destination);
            //Convert to string
            String destination = find_destination.getText().toString();
            //Create adress list
            List<Address> addressList = null;
            //Set marker
            MarkerOptions markerOptions = new MarkerOptions();


            if( !destination.equals("")){
                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(destination, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Add markes to found locations
                for (int i =0; i<addressList.size(); i++){

                    Address myAddress = addressList.get(i);
                    LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                    markerOptions.position(latLng);
                    markerOptions.title("Search results");
                    //add marker to position
                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }

            }
        }

    }


    //Method to build google api client
    protected synchronized void buildGoogleApiClient(){
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }


    //Method setting the location intervals
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        //set priority
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest,this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //Method to display marker on current location and move the camera to the location
    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;

        if (currentLocationMarker !=null){

            currentLocationMarker.remove();
        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(8));

        //Stop location updates
        if (client !=null){

            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

    }
}
