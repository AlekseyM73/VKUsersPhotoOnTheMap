package com.alekseyM73.repository;

import com.alekseyM73.model.photo.PhotosResponse;
import com.alekseyM73.network.ApiService;
import com.alekseyM73.network.VkApi;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ApiRepository {

    public Observable<PhotosResponse> searchPhotos(Map<String, String> options){
        VkApi service = ApiService.getRetrofit().create(VkApi.class);
        return service.getPhotos(options)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
