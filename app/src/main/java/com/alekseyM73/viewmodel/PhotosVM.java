package com.alekseyM73.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alekseyM73.model.photo.Item;

import java.util.LinkedList;

public class PhotosVM extends AndroidViewModel {

    private MutableLiveData<String> message = new MutableLiveData<>();
    private MutableLiveData<LinkedList<Item>> photosForShow = new MutableLiveData<>();
    private int photoCount = 10;

    public PhotosVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<LinkedList<Item>> getPhotos(){
        return photosForShow;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void setPhotos(LinkedList<Item> photos) {
        photosForShow.setValue(photos);
    }
}
