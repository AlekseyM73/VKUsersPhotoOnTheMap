package com.alekseyM73.view;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alekseyM73.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnGroundOverlayClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Arrays;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        OnGroundOverlayClickListener{

    private static final LatLng ULYANOVSK = new LatLng(54.1850, 48.2333);
    private static final String TAG = "MapActivity";
    private final int REQUEST_LOCATION = 100;

    private GoogleMap mMap = null;
    private GroundOverlay groundOverlay;
    private FusedLocationProviderClient locationClient;
    private Marker currentMarker;

    private View vGoToLocation;
    private BottomSheetBehavior bottomSheetBehavior;
    private AutoCompleteTextView vSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setSearchPlace();
        configureMap();

        setViews();

        locationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setViews(){
        vGoToLocation = findViewById(R.id.to_location);
        vGoToLocation.setOnClickListener(v -> {
            if (currentMarker != null) {
                moveToLocation(currentMarker.getPosition());
            }
        });

        View bottomS = findViewById(R.id.bottom_sheet);

        vSearch = bottomS.findViewById(R.id.input_search);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomS);
        vSearch.setOnClickListener( listener -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        vSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_SETTLING: {
                        vGoToLocation.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        vGoToLocation.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        Animation inAnim = new AlphaAnimation(0.0f, 1.0f);
                        inAnim.setDuration(100);
                        vGoToLocation.setVisibility(View.VISIBLE);
                        vGoToLocation.startAnimation(inAnim);
                        break;
                    }
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
    }

    private void setSearchPlace(){
    }

    private void configureMap(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync( this);
        }
    }

    private LocationRequest buildLocationRequest() {
        return new LocationRequest()
                .setNumUpdates(1)
                .setExpirationDuration(60000)
//                .setInterval(50000)
                .setPriority(LocationRequest.PRIORITY_LOW_POWER);
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

        findLocation();
    }

    private void findLocation(){
        if (checkPermission()) {
            locationClient.requestLocationUpdates(buildLocationRequest(), locationCallback, Looper.getMainLooper());
        }
    }

    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            System.out.println("----------- Updates");
            if (locationResult.getLastLocation() != null) {
                showMyLocation(locationResult.getLastLocation());
            } else {
                showRequestRationaleDialog();
            }
        }
    };

    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (permissions[0].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PERMISSION_DENIED) {
                Log.i("MapActivity", "Location permission was denied");
            } else {
                findLocation();
            }
        }
    }

    private void showRequestRationaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(R.string.permissions_request_message)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    dialog.cancel();
                })
                .setCancelable(true)
                .create()
                .show();
    }

    private void addObjectsToMap() {
    /*     groundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.harbour_bridge))
            .position(ULYANOVSK, 700000)
            .clickable(true));*/
    }

    private void showMyLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentMarker == null) {
            moveToLocation(latLng);
            // Add Marker to Map
            MarkerOptions option = new MarkerOptions();
            // option.title("My Location");
            option.snippet("....");
            option.position(latLng);
            currentMarker = mMap.addMarker(option);
        } else {
            currentMarker.setPosition(latLng);
        }
    }

    private void moveToLocation(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)             // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        Toast.makeText(this,"pic",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }
}
