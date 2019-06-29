package com.alekseyM73.repository;

import com.alekseyM73.model.photo.PhotosResponse;
import com.alekseyM73.model.place.PlaceDetailsResponse;
import com.alekseyM73.model.search.PlaceSearchResponse;
import com.alekseyM73.network.ApiService;
import com.alekseyM73.network.VkApi;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ApiRepository {

    private VkApi service;

    public ApiRepository() {
        service = ApiService.getRetrofit().create(VkApi.class);
    }

    public Observable<PhotosResponse> searchPhotos(Map<String, String> options){
        return service.getPhotos(options).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PlaceSearchResponse> searchPlace(String text){
        return service.searchPlace(ApiService.URL_SEARCH + text).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PlaceDetailsResponse> searchPlaceDetails(String placeId){
        return service.getPlaceDetails(ApiService.URL_PLACE_DETAILS + placeId).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }
}
