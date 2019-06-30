package com.alekseyM73.view;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.alekseyM73.R;
import com.alekseyM73.adapter.PlaceAutoCompleteAdapter;
import com.alekseyM73.model.search.Prediction;
import com.alekseyM73.util.Area;
import com.alekseyM73.util.GlideApp;
import com.alekseyM73.util.IconRenderer;
import com.alekseyM73.util.SearchFilter;
import com.alekseyM73.viewmodel.MapVM;
import com.appyvet.materialrangebar.RangeBar;
import com.alekseyM73.model.photo.Item;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener{

    private final int REQUEST_LOCATION = 100;

    private GoogleMap mMap;
    private Marker currentMarker;
    private View vGoToLocation;
    private ClusterManager<Item> mClusterManager;

    private FusedLocationProviderClient locationClient;

    private BottomSheetBehavior bottomSheetBehavior;
    private AutoCompleteTextView vSearch;
    private RangeBar radiusRangeBar, ageRangeBar;

    private MapVM mapVM;
    private TextView reset;
    private RadioGroup sexRadioGroup;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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
        bottomSheetBehavior = BottomSheetBehavior.from(bottomS);

        sexRadioGroup = findViewById(R.id.sex_group);
        radiusRangeBar = findViewById(R.id.rangeBar_radius);
        ageRangeBar = findViewById(R.id.rangeBar_age);
        radiusRangeBar.setSeekPinByIndex(1);

        vSearch = bottomS.findViewById(R.id.input_search);
        progressBar = bottomS.findViewById(R.id.progress_bar);

        vSearch.setOnClickListener( listener -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        vSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        Button btnApply = bottomS.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(v -> {
            search();
        });

        reset = findViewById(R.id.reset);
        reset.setOnClickListener(listener ->{
            radiusRangeBar.setSeekPinByIndex(0);
            ageRangeBar.setRangePinsByValue(ageRangeBar.getTickStart(), ageRangeBar.getTickEnd());
            sexRadioGroup.check(R.id.sex_any);
        });

        mapVM = ViewModelProviders.of(this).get(MapVM.class);

        mapVM.getPhotos().observe(this, items -> {
            addItems(items);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        mapVM.getMessage().observe(this, message ->{
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
        mapVM.getProgress().observe(this, visibility -> {
            progressBar.setVisibility(visibility);
        });
        mapVM.getPredictions().observe(this, predictions -> {
            vSearch.setAdapter(new PlaceAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, predictions));
            vSearch.showDropDown();
        });
        mapVM.getLocation().observe(this, placeLocation -> {
            showMyLocation(placeLocation.getLat(), placeLocation.getLng());
        });

        vSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (v.getText().toString().trim().isEmpty()) {
                    return false;
                }
                mapVM.searchPlace(v.getText().toString());
                return true;
            } else {
                return false;
            }
        });
        vSearch.setOnItemClickListener((parent, view, position, id) -> {
            Prediction prediction = (Prediction) parent.getItemAtPosition(position);
            mapVM.getPlaceDetails(prediction);
            vSearch.setText(prediction.getDescription());
            hideKeyboard();
        });
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

        findLocation();

        mClusterManager = new ClusterManager<Item>(this, mMap);
        mClusterManager.setRenderer(new IconRenderer(this, mMap, mClusterManager));
        mClusterManager.setOnClusterClickListener(cluster -> {
            Gson gson = new Gson();
            String json = gson.toJson(cluster.getItems());
            startActivity(new Intent(MapActivity.this, PhotosActivity.class)
                    .putExtra(PhotosActivity.KEY_DATA, json));
            return false;
        });

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnMarkerDragListener(this);

    }

    private void addItems(Set<Item> items) {
        mClusterManager.clearItems();
        mClusterManager.cluster();

        for (Item item : items) {
            if (item.getLat() == null || item.getLong() == null){
                continue;
            }
            GlideApp.with(this)
                    .asBitmap()
                    .load(item.getPhotos().get(0).getUrl())
                    .into(new CustomTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            item.setBitmap(resource);
                            mClusterManager.addItem(item);
                            mClusterManager.cluster();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
            });
        }
    }

    private void findLocation(){
        if (checkPermission()) {
            locationClient.requestLocationUpdates(buildLocationRequest(), locationCallback, Looper.getMainLooper());
            locationClient.getLastLocation().addOnSuccessListener(this, location ->{
                if (location == null){
                    showRequestRationaleDialog();
                }
            });
        }
    }

    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            System.out.println("----------- Updates");
            if (locationResult.getLastLocation() != null) {
                showMyLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
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

    private SearchFilter getFilterValue(){
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setAgeStart(ageRangeBar.getLeftPinValue());
        searchFilter.setAgeFinish(ageRangeBar.getRightPinValue());
        String[] radiusArray = getResources().getStringArray(R.array.radius_list);
        searchFilter.setRadius(radiusArray[radiusRangeBar.getRightIndex()]);
        return searchFilter;
    }

    private void search(){
        SearchFilter searchFilter = getFilterValue();
        mapVM.searchPhotos(this, searchFilter,
                setAreas(mapVM.getLatitude(), mapVM.getLongitude(), Integer.parseInt(searchFilter.getRadius())));
    }

    private List<Area> setAreas(double lat, double lon, int radius){
        final double ONEGRAD = 0.00001038; //подгон, исходное значение 0.000009009

//        List<Circle> circleList = new ArrayList<>();
        List<Area> areas = new ArrayList<>();

        double newRadius = (double) radius / 3;

//        circleList.add(createCircle(
//                lat,
//                lon - (double) radius * ONEGRAD,
//                newRadius, Color.RED));
        areas.add(new Area(lat, lon - (double) radius * ONEGRAD));

//        circleList.add(createCircle(
//                lat,
//                lon + (double) radius * ONEGRAD,
//                newRadius, Color.MAGENTA));
        areas.add(new Area(lat, lon + (double) radius * ONEGRAD));

//        circleList.add(createCircle(
//                lat + (double) radius/2 * ONEGRAD, //0.00003 подгон
//                lon - (double) radius/2 * ONEGRAD,
//                newRadius, Color.GREEN));
        areas.add(new Area(lat + (double) radius/2 * ONEGRAD, lon - (double) radius/2 * ONEGRAD));

//        circleList.add(createCircle(
//                lat + (double) radius/2 * ONEGRAD,
//                lon + (double) radius/2 * ONEGRAD,
//                newRadius, Color.BLUE));
        areas.add(new Area(lat + (double) radius/2 * ONEGRAD, lon + (double) radius/2 * ONEGRAD));

//        circleList.add(createCircle(
//                lat - (double) radius/2 * ONEGRAD,
//                lon - (double) radius/2 * ONEGRAD,
//                newRadius, Color.YELLOW));
        areas.add(new Area(lat - (double) radius/2 * ONEGRAD, lon - (double) radius/2 * ONEGRAD));

//        circleList.add(createCircle(
//                lat - (double) radius/2 * ONEGRAD,
//                lon + (double) radius/2 * ONEGRAD,
//                newRadius, Color.CYAN));
        areas.add(new Area(lat - (double) radius/2 * ONEGRAD, lon + (double) radius/2 * ONEGRAD));

//        circleList.add(createCircle(
//                lat,
//                lon,
//                newRadius, Color.BLACK));
        areas.add(new Area(lat, lon));

//        mapVM.setCircles(circleList);
        return areas;
    }

    private Circle createCircle(double lat, double lon, double radius, int color){
        return mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lon))
                .radius(radius)
                .strokeColor(color)
                .strokeWidth(3)
        );
    }

    private void showMyLocation(double lat, double lon) {
        LatLng latLng = new LatLng(lat, lon);
        mapVM.setLocation(lat, lon);
        if (currentMarker == null) {
            // Add Marker to Map
            MarkerOptions option = new MarkerOptions();
            // option.title("My Location");
            option.snippet("....");
            option.position(latLng);
            currentMarker = mMap.addMarker(option);
            currentMarker.setDraggable(true);
        } else {
            currentMarker.setPosition(latLng);
        }
        moveToLocation(latLng);
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
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mapVM.setLocation(marker.getPosition().latitude, marker.getPosition().longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }
}
