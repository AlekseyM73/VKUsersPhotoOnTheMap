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
import com.alekseyM73.repository.ApiRepository;
import com.alekseyM73.util.Preferences;

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

    public MapVM(@NonNull Application application) {
        super(application);
//        search(application.getBaseContext());
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


    @SuppressLint("CheckResult")
    public void search(Context context, double lat, double lon){
        progress.setValue(View.VISIBLE);
        if (accessToken == null){
            accessToken = new Preferences().getToken(context);
        }
        Date date = new Date();
        Map<String, String> options = new HashMap<>();
        options.put("lat", String.valueOf(lat));
        options.put("long", String.valueOf(lon));
//        options.put("start_time", "1561334400");
//        options.put("end_time", String.valueOf(date.getTime() / 1000));
        options.put("radius", "500");
        options.put("count", "400");
        options.put("sort", "0");
        options.put("v", "5.95");
        options.put("access_token", accessToken);

        apiRepository.searchPhotos(options)
                .subscribe(photosResponse -> {
                    if (photosResponse.getResponse() != null){
                        photos.setValue(new LinkedList<>(photosResponse.getResponse().getItems()));
                        message.setValue(
                                photosResponse.getResponse().getCount() + "/" + photosResponse.getResponse().getItems().size());
                    } else {
                        message.setValue("Не удалось загрузить данные");
                    }
                    progress.setValue(View.INVISIBLE);
                }, error -> {
                    error.printStackTrace();
                    progress.setValue(View.INVISIBLE);
                    message.setValue("Не удалось загрузить данные");
                });

    }

}
