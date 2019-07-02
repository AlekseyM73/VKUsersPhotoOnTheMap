package com.alekseyM73.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alekseyM73.model.photo.Item;
import com.alekseyM73.model.photo.PhotosResponse;
import com.alekseyM73.model.place.PlaceLocation;
import com.alekseyM73.model.search.Prediction;
import com.alekseyM73.model.user.UserResponse;
import com.alekseyM73.repository.ApiRepository;
import com.alekseyM73.util.Area;
import com.alekseyM73.util.Preferences;
import com.alekseyM73.util.SearchFilter;
import com.google.android.gms.maps.model.Circle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapVM extends AndroidViewModel {

    private ApiRepository apiRepository = new ApiRepository();
    private String accessToken = null;
    private MutableLiveData<Set<Item>> photosForShow = new MutableLiveData<>();
    private Set<Item> allPhotos = new HashSet<>();
    private Map<Long, UserResponse> usersMap = new HashMap<>();

    private MutableLiveData<Integer> progress = new MutableLiveData<>();
    private MutableLiveData<String> message = new MutableLiveData<>();

    private MutableLiveData<List<Prediction>> predictions = new MutableLiveData<>();
    private MutableLiveData<PlaceLocation> location = new MutableLiveData<>();

    private SearchFilter searchFilter;

    private double lat, prevLat = 0, lon, prevLon = 0;
    private List<Circle> circles = new ArrayList<>();

    public MapVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<Set<Item>> getPhotos(){
        return photosForShow;
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

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }

    public void setCircles(List<Circle> circles) {
        for (Circle circle : this.circles){
            circle.remove();
        }
        this.circles = circles;
    }

    @SuppressLint("CheckResult")
    public void searchPhotos(Context context, SearchFilter searchFilter, List<Area> areas){
        progress.setValue(View.VISIBLE);
        if (accessToken == null){
            accessToken = new Preferences().getToken(context);
        }
        if (prevLat == lat && prevLon == lon && this.searchFilter.getRadius().equals(searchFilter.getRadius())){
            this.searchFilter = searchFilter;
            prepare();
            return;
        }
        Date date = new Date();
        Map<String, String> options = new HashMap<>();
//        options.put("end_time", String.valueOf(date.getTime() / 1000));
        options.put("start_time", "1561334400");
        options.put("radius", searchFilter.getRadius());
        options.put("count", "200");
        options.put("sort", "0");
        options.put("v", "5.95");
        options.put("access_token", accessToken);

        apiRepository.search(areas, options)
                .subscribe(photosResponseList -> {
                    if (photosResponseList != null){
                        prevLon = lon;
                        prevLat = lat;
                        this.searchFilter = searchFilter;

                        allPhotos = new HashSet<>();
                        for (PhotosResponse response: photosResponseList){
                            allPhotos.addAll(response.getResponse().getItems());
                        }
                        System.out.println("------ SIZE = " + allPhotos.size());
                        if (allPhotos.size() == 0){
                            showMessage("Упс! Здесь ничего нет");
                            usersMap = new HashMap<>();
                            prepare();
                        } else {
                            getUsersInfo();
                        }
                    } else {
                        showMessage("Ничего не найдено");
                    }
                }, error -> {
                    error.printStackTrace();
                    showMessage("Не удалось загрузить данные");
                    progress.setValue(View.INVISIBLE);
                });
    }

    @SuppressLint("CheckResult")
    private void getUsersInfo(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Item item: allPhotos){
            long id = item.getOwnerId();
            if (id > 0) {
                stringBuilder.append(item.getOwnerId()).append(",");
            }
        }
        apiRepository.getUsers(stringBuilder.toString(), accessToken)
                .subscribe(map ->{
                    System.out.println("----------- Users size = " + map.size());
                    usersMap = map;
                    prepare();
                }, throwable -> {
                    throwable.printStackTrace();
                    progress.setValue(View.INVISIBLE);
                    showMessage("Не удалось загрузить данные");
                });
    }

    private void prepare(){
        System.out.println("------- Filter = " + searchFilter.toString());
        if (allPhotos.size() == 0){
            photosForShow.setValue(allPhotos);
            return;
        }
        Set<Item> items = new HashSet<>();
        for (Item item: allPhotos){
            UserResponse user = usersMap.get(item.getOwnerId());
            if (user != null){
                if (check(user)){
//                    System.out.println("-------- Add: " + user.toString());
                    item.setUser(user);
                    items.add(item);
                }
            }
        }
        System.out.println("------ After filter size = " + items.size());
        if (items.size() == 0){
            showMessage("Ничего не найдено по заданному фильтру");
        }
        progress.setValue(View.INVISIBLE);
        photosForShow.setValue(items);
    }

    private boolean check(UserResponse user){
        if (searchFilter.getSex() != 0 && user.getSex() != searchFilter.getSex()) {
            return false;
        }
        int start = Integer.parseInt(searchFilter.getAgeStart());
        int end = Integer.parseInt(searchFilter.getAgeFinish());
        if (start == 14 && end == 80) {
            return true;
        }

        if (user.getBdate() != null) {
            String[] bdate = user.getBdate().split("\\.");
            if (bdate.length == 2) {
                return false;
            }

            int age = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR) - Integer.valueOf(bdate[2]);
            if (age >= start && age <= end) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("CheckResult")
    public void searchPlace(String text){
        progress.setValue(View.VISIBLE);
        apiRepository.searchPlace(text).
                subscribe(placeSearchResponse -> {
                    progress.setValue(View.INVISIBLE);
                    if (placeSearchResponse.getPredictions().isEmpty()){
                        showMessage("Ничего не найдено");
                        Log.d("MapView", "searchPlace() status = " + placeSearchResponse.getStatus());
                    } else {
                        predictions.setValue(placeSearchResponse.getPredictions());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    progress.setValue(View.INVISIBLE);
                    showMessage("Не удалось загрузить данные");
                });
    }

    @SuppressLint("CheckResult")
    public void getPlaceDetails(Prediction prediction){
        apiRepository.searchPlaceDetails(prediction.getPlaceId()).
                subscribe(placeDetailsResponse -> {
                    if (placeDetailsResponse != null && placeDetailsResponse.getResult() != null){
                        PlaceLocation loc = placeDetailsResponse.getResult().getGeometry().getLocation();
                        location.setValue(loc);
                        lat = loc.getLat();
                        lon = loc.getLng();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                   showMessage("Не удалось загрузить данные");
                });
    }

    public void reset(SearchFilter searchFilter){
        this.searchFilter = searchFilter;
        prepare();
    }

    private void showMessage(String text){
        message.setValue(text);
        message.setValue(null);
    }
}
