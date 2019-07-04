package com.alekseyM73.view;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alekseyM73.Application;
import com.alekseyM73.R;
import com.alekseyM73.adapter.PlaceAutoCompleteAdapter;
import com.alekseyM73.model.search.Prediction;
import com.alekseyM73.util.GlideApp;
import com.alekseyM73.util.IconRenderer;
import com.alekseyM73.util.Preferences;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Set;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener{

    private final int REQUEST_LOCATION = 100;
    private GoogleMap mMap;
    private ClusterManager<Item> mClusterManager;
    private FusedLocationProviderClient locationClient;
    private BottomSheetBehavior bottomSheetBehavior;
    private AutoCompleteTextView vSearch;
    private RangeBar radiusRangeBar, ageRangeBar;
    private MapVM mapVM;
    public TextView reset;
    private RadioGroup sexRadioGroup;
    private ProgressBar progressBar;
    private boolean isClusteringEnabled = true;
    private Circle radiusCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        configureMap();

        locationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("clustering",isClusteringEnabled);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            isClusteringEnabled = savedInstanceState.getBoolean("clustering");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setViews(){
        mapVM = ViewModelProviders.of(this).get(MapVM.class);

        if (mapVM.isFindLocation()) {
            findLocation();
        }

        mapVM.setMap(mMap);

        View vLogout = findViewById(R.id.logout_click);
        vLogout.setOnClickListener(v ->{
            Intent intent = new Intent(MapActivity.this,MainActivity.class);
            intent.putExtra("logout", true);
            startActivity(intent);
        } );

        View vSwitchClustering = findViewById(R.id.switch_clustering);
        vSwitchClustering.setOnClickListener(v->{
            if (isClusteringEnabled){
                isClusteringEnabled = false;
               mClusterManager.setRenderer(new IconRenderer(this, mMap, mClusterManager, isClusteringEnabled));
                Toast.makeText(this, "Кластеризация фотографий отключена", Toast.LENGTH_SHORT).show();
            } else {
                isClusteringEnabled = true;
                mClusterManager.setRenderer(new IconRenderer(this, mMap, mClusterManager, isClusteringEnabled));
                Toast.makeText(this, "Кластеризация фотографий включена", Toast.LENGTH_SHORT).show();
            }

        });
        View vGoToLocation = findViewById(R.id.to_location);

        vGoToLocation.setOnClickListener(v -> {
            findLocation();
        });

        View vGoSearch = findViewById(R.id.search_click);
        vGoSearch.setOnClickListener(v->{
            search();
        });

        View vGoToGallery = findViewById(R.id.to_gallery_click);

        vGoToGallery.setOnClickListener(view -> {
            if (Application.photosToGallery != null && !Application.photosToGallery.isEmpty()){
                startActivity(
                        new Intent(MapActivity.this, PhotosActivity.class));
            }
        });


        View bottomS = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomS);
        sexRadioGroup = findViewById(R.id.sex_group);
        radiusRangeBar = findViewById(R.id.rangeBar_radius);
        ageRangeBar = findViewById(R.id.rangeBar_age);
        radiusRangeBar.setSeekPinByIndex(1);

        vSearch = bottomS.findViewById(R.id.input_search);
        progressBar = findViewById(R.id.progress_bar);

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
            vSearch.setText("");
            radiusRangeBar.setSeekPinByIndex(1);
            ageRangeBar.setRangePinsByValue(ageRangeBar.getTickStart(), ageRangeBar.getTickEnd());
            sexRadioGroup.check(R.id.sex_any);
            mapVM.reset(getFilterValue());
        });

        mapVM.getPhotos().observe(this, items -> {
            Application.photosToGallery = items;
            addItems(items);
        });

        mapVM.getMessage().observe(this, message ->{
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
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

        if (radiusCircle == null && mapVM.getCircleOptions() != null){
            radiusCircle = mMap.addCircle(mapVM.getCircleOptions());
        }
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
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mUiSettings.setZoomControlsEnabled(true);
        }
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);

        mClusterManager = new ClusterManager<Item>(this, mMap);


        mClusterManager.setRenderer(new IconRenderer(this, mMap, mClusterManager, isClusteringEnabled));
        mClusterManager.setOnClusterClickListener(cluster -> {
            if (cluster.getItems().size() == Application.photosToGallery.size()){
                startActivity(
                        new Intent(MapActivity.this, PhotosActivity.class));
                return true;
            }
            Gson gson = new Gson();
            String json = gson.toJson(cluster.getItems());
            startActivity(
                    new Intent(MapActivity.this, PhotosActivity.class)
                            .putExtra(PhotosActivity.KEY_PHOTOS, json)
            );
            return false;
        });

        mClusterManager.setOnClusterItemClickListener(item -> {
            Intent intent = new Intent(MapActivity.this, InfoActivity.class);
            intent.putExtra(InfoActivity.ITEM, new Gson().toJson(item));
            intent.putExtra(InfoActivity.TYPE, InfoActivity.TYPE_ONE);
            startActivity(intent);
            return false;
        });

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnMarkerDragListener(this);

        mClusterManager.getMarkerCollection().getMarkers();

        setViews();
    }

    private void addItems(Set<Item> items) {
        if (mClusterManager == null) return;
        mClusterManager.clearItems();
        mClusterManager.cluster();

        for (Item item : items) {
            if (item.getLat() == null || item.getLon() == null){
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
            Log.v("MapActivity", "Updates");
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

        searchFilter.setRadius(String.valueOf(radiusArray[radiusRangeBar.getRightIndex()]));

        RadioButton myRadioButton = findViewById(sexRadioGroup.getCheckedRadioButtonId());
        int index = sexRadioGroup.indexOfChild(myRadioButton);
        switch (index){
            case 0: {
                searchFilter.setSex(0);
                break;
            }
            case 1: {
                searchFilter.setSex(2);
                break;
            }
            case 2: {
                searchFilter.setSex(1);
                break;
            }
        }
        return searchFilter;
    }

    private void search(){
        LatLng latLng = mMap.getCameraPosition().target;
        mapVM.setLocation(latLng.latitude, latLng.longitude);
        SearchFilter searchFilter = getFilterValue();
        mapVM.searchPhotos(this, searchFilter);
        createCircle(latLng, Integer.parseInt(searchFilter.getRadius()));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void createCircle(LatLng latLng, int radius){
        if (mMap == null){
            return;
        }
        CircleOptions circleOptions = mapVM.getCircleOptions();
        if (circleOptions == null){
            circleOptions = new CircleOptions()
                    .fillColor(getResources().getColor(R.color.radiusColor))
                    .strokeWidth(0);
        }
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        mapVM.setCircleOptions(circleOptions);
        if (radiusCircle == null){
            radiusCircle = mMap.addCircle(circleOptions);
        } else {
            radiusCircle.setCenter(circleOptions.getCenter());
            radiusCircle.setRadius(circleOptions.getRadius());
        }
    }

    private void showMyLocation(double lat, double lon) {
        LatLng latLng = new LatLng(lat, lon);
        mapVM.setLocation(lat, lon);
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
