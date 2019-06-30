package com.alekseyM73.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alekseyM73.model.photo.Item;
import com.alekseyM73.model.place.PlaceLocation;
import com.alekseyM73.model.search.Prediction;
import com.alekseyM73.repository.ApiRepository;
import com.alekseyM73.util.Preferences;
import com.alekseyM73.util.SearchFilter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.internal.impl.net.pablo.PlaceResult;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapVM extends AndroidViewModel {

    private ApiRepository apiRepository = new ApiRepository();
    private String accessToken = null;
    private MutableLiveData<LinkedList<Item>> photos = new MutableLiveData<>();
    private MutableLiveData<Integer> progress = new MutableLiveData<>();
    private MutableLiveData<String> message = new MutableLiveData<>();
    private MutableLiveData<List<Prediction>> predictions = new MutableLiveData<>();
    private MutableLiveData<PlaceLocation> location = new MutableLiveData<>();
    private double lat, lon;

    public MapVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<LinkedList<Item>> getPhotos(){
        return photos;
    }

    public LiveData<Integer> getProgress() {
        return progress;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<List<Prediction>> getPredictions() {
        return predictions;
    }

    public LiveData<PlaceLocation> getLocation() {
        return location;
    }

    public void setLocation(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }


    @SuppressLint("CheckResult")
    public void searchPhotos(Context context, SearchFilter searchFilter){
        if (accessToken == null){
            accessToken = new Preferences().getToken(context);
        }
        Date date = new Date();
        Map<String, String> options = new HashMap<>();
//        options.put("end_time", String.valueOf(date.getTime() / 1000));
        options.put("lat", String.valueOf(lat));
        options.put("long", String.valueOf(lon));
        options.put("start_time", "1561334400");
        options.put("radius", searchFilter.getRadius());
        options.put("count", "200");
        options.put("sort", "0");
        options.put("v", "5.95");
        options.put("access_token", accessToken);

        apiRepository.searchPhotos(options)
                .subscribe(photosResponse -> {
                    if (photosResponse.getResponse() != null){
                        if (photosResponse.getResponse().getCount() == 0){
                            message.setValue("Упс! Здесь ничего нет");
                        }
                        photos.setValue(new LinkedList<>(photosResponse.getResponse().getItems()));
                    } else {
                        message.setValue("Ничего не найдено");
                    }
                }, error -> {
                    error.printStackTrace();
                    message.setValue("Не удалось загрузить данные");
                });
    }

    @SuppressLint("CheckResult")
    public void searchPlace(String text){
        progress.setValue(View.VISIBLE);
        apiRepository.searchPlace(text).
                subscribe(placeSearchResponse -> {
                    progress.setValue(View.INVISIBLE);
                    if (placeSearchResponse.getPredictions().isEmpty()){
                        message.setValue("Ничего не найдено");
                        Log.d("MapView", "searchPlace() status = " + placeSearchResponse.getStatus());
                    } else {
                        predictions.setValue(placeSearchResponse.getPredictions());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    progress.setValue(View.INVISIBLE);
                    message.setValue("Не удалось загрузить данные");
                });
    }

    @SuppressLint("CheckResult")
    public void getPlaceDetails(Prediction prediction){
        apiRepository.searchPlaceDetails(prediction.getPlaceId()).
                subscribe(placeDetailsResponse -> {
                    if (placeDetailsResponse != null && placeDetailsResponse.getResult() != null){
                        location.setValue(placeDetailsResponse.getResult().getGeometry().getLocation());
                        if (location.getValue() != null) {
                            lat = location.getValue().getLat();
                            lat = location.getValue().getLng();
                        }
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    message.setValue("Не удалось загрузить данные");
                });
    }

}
