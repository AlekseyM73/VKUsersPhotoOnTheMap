package com.alekseyM73.repository;

import com.alekseyM73.model.photo.PhotosResponse;
import com.alekseyM73.model.place.PlaceDetailsResponse;
import com.alekseyM73.model.search.PlaceSearchResponse;
import com.alekseyM73.model.user.UserResponse;
import com.alekseyM73.network.ApiService;
import com.alekseyM73.network.VkApi;
import com.alekseyM73.util.Area;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


//Опять странный нейминг, по сути репозиторием не является (не хранит не чего), ApiMiddleware ?
public class ApiRepository {

    private VkApi service;

    public ApiRepository() {
        service = ApiService.getRetrofit().create(VkApi.class);
    }

    public Observable<PhotosResponse> searchPhotos(double lat, double lon, Map<String, String> options){
        return service.getPhotos(lat, lon, options).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<PhotosResponse>> search(List<Area> list, Map<String, String> options){
        return Observable
                .fromIterable(list)
                .concatMap(item ->
                        Observable
                                .interval(300, TimeUnit.MICROSECONDS)
                                .subscribeOn(Schedulers.io())
                                .take(1)
                                .flatMap(second -> service.getPhotos(item.getLat(), item.getLon(), options)))
                .toList().toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Map<Long, UserResponse>> getUsers(String ids, String token){
        return service.getUserInfo(ids, token)
                .flatMap(userInfoResponse ->
                        Observable
                                .fromIterable(userInfoResponse.getResponse())
                                .toMap(UserResponse::getId).toObservable()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
