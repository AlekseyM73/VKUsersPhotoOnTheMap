package com.alekseyM73;
import android.Manifest;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnGroundOverlayClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;


public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        OnGroundOverlayClickListener {

    private static final LatLng ULYANOVSK = new LatLng(54.1850, 48.2333);
    private final int REQUEST_LOCATION = 100;
        private GoogleMap mMap = null;
        private GroundOverlay groundOverlay;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED) {
                showRequestRationaleDialog();
            } else {
                configureMap();
            }
    }

    private void showRequestRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permissions_request_title)
                .setMessage(R.string.permissions_request_message)
                .setPositiveButton(R.string.ok, (dialogInterface, i) ->
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION))
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (permissions[0].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PERMISSION_DENIED) {
                Toast.makeText(this, R.string.back_off, Toast.LENGTH_LONG).show();
                finish();
            } else {
                configureMap();
            }
        }
    }

        private void configureMap(){
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync( this);
        }

        @Override
        public void onMapReady(GoogleMap map) {
        mMap = map;
        UiSettings mUiSettings = mMap.getUiSettings();

        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);

        addObjectsToMap();

        mMap.setOnGroundOverlayClickListener(this);

        showMyLocation();
    }

        private void addObjectsToMap() {
       /*     groundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.harbour_bridge))
                .position(ULYANOVSK, 700000)
                .clickable(true));*/
    }

        private void showMyLocation() {
            LocationManager locationManager = (LocationManager)
                    getSystemService(LOCATION_SERVICE);
            String locationProvider = this.getEnabledLocationProvider();
            if (locationProvider == null) {
                return;
            }

            final long MIN_TIME_BW_UPDATES = 1000;
            final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

            Location myLocation = null;
            try {
                locationManager.requestLocationUpdates(
                        locationProvider,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                myLocation = locationManager
                        .getLastKnownLocation(locationProvider);
            }
            catch (SecurityException e) {
                Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            if (myLocation != null) {
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)             // Sets the center of the map to location user
                        .zoom(15)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                // Add Marker to Map
                MarkerOptions option = new MarkerOptions();
               // option.title("My Location");
                option.snippet("....");
                option.position(latLng);
                Marker currentMarker = mMap.addMarker(option);

            } else {
                Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
            }

        }
        private String getEnabledLocationProvider() {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, true);
            boolean enabled = locationManager.isProviderEnabled(bestProvider);

            if (!enabled) {
                Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
                return null;
            }
            return bestProvider;
        }

        @Override
        public void onGroundOverlayClick(GroundOverlay groundOverlay) {
            Toast.makeText(this,"pic",Toast.LENGTH_SHORT).show();
    }

        @Override
        public void onLocationChanged(Location location) {

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
