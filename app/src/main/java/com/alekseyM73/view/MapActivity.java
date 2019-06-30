package com.alekseyM73.view;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
import androidx.lifecycle.Observer;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        radiusRangeBar.setSeekPinByIndex(0);

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
            mapVM.searchPhotos(this, getFilterValue());
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
        });
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

    }

    private void addItems(List<Item> items) {
        mClusterManager.clearItems();

        for (Item item : items) {
            if (item.getLat() == null || item.getLong() == null){
                return;
            }
            GlideApp.with(this)
                    .asBitmap()
                    .load(item.getPhotos().get(0).getUrl())
                    .into(new CustomTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            item.setBitmap(resource);
                            mClusterManager.addItem(item);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
//                            mClusterManager.addItem(mapItem);
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

    private ArrayList<Area> setAreas(double lat, double lon){
        final double ONEGRAD = 0.000009009;
        SearchFilter searchFilter = getFilterValue();
        int radius = Integer.parseInt(searchFilter.getRadius());
        Area areaLeft = new Area();
        Area areaRigth = new Area();
        Area areaLeftTop = new Area();
        Area areaRigthTop = new Area();
        Area areaLeftBot = new Area();
        Area areaRigthBot = new Area();
        Area areaCentre = new Area();

        areaLeft.setLat(lat);
        areaLeft.setLon(lon - 2*radius/3 * ONEGRAD);

        areaRigth.setLat(lat);
        areaRigth.setLon(lon + 2*radius/3 * ONEGRAD);

        areaLeftTop.setLat(lat + 2*radius/3 * ONEGRAD);
        areaLeftTop.setLon(lon - radius/3 * ONEGRAD);

        areaRigthTop.setLat(lat + 2*radius/3 * ONEGRAD);
        areaRigthTop.setLon(lon + radius/3 * ONEGRAD);

        areaLeftBot.setLat(lat - 2*radius/3 * ONEGRAD);
        areaLeftBot.setLon(lon - radius/3 * ONEGRAD);

        areaRigthBot.setLat(lat - 2*radius/3 * ONEGRAD);
        areaRigthBot.setLon(lon + radius/3 * ONEGRAD);

        areaCentre.setLat(lat);
        areaCentre.setLon(lon);

        ArrayList<Area> areaArrayList = new ArrayList<>();
        areaArrayList.add(areaCentre);
        areaArrayList.add(areaLeft);
        areaArrayList.add(areaLeftBot);
        areaArrayList.add(areaLeftTop);
        areaArrayList.add(areaRigth);
        areaArrayList.add(areaRigthBot);
        areaArrayList.add(areaRigthTop);

        return areaArrayList;
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
        ArrayList<Area> areaArrayList = setAreas(lat, lon);
        //TODO: Добавить аргумент объект класса Area к функции searchPhotos и брать из них новые lat lon
        for (Area a: areaArrayList) {
            mapVM.searchPhotos(this, getFilterValue());
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
        Toast.makeText(this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();;
    }
}
