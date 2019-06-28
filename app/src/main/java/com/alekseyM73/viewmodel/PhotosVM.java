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

    private String accessToken = null;
    private LinkedList<Item> photos;
    private MutableLiveData<String> message = new MutableLiveData<>();
    private MutableLiveData<LinkedList<Item>> photosForShow = new MutableLiveData<>();
    private MutableLiveData<Integer> showBtnMore = new MutableLiveData<>();
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

    public LiveData<Integer> getShowBtnMore() {
        return showBtnMore;
    }

    public void setPhotos(LinkedList<Item> photos) {
        this.photos = photos;
        if (photos.size() <= photoCount) {
            photosForShow.setValue(photos);
            showBtnMore.setValue(View.GONE);
        } else {
            photosForShow.setValue(new LinkedList<>(photos.subList(0, photoCount)));
        }
    }

    public void loadMore(){
        if (photos == null || photosForShow.getValue() == null) return;
        int next = photoCount + 10;
        int photosSize = photos.size();
        if (photosSize < next){
            showBtnMore.setValue(View.GONE);
            LinkedList<Item> newList = photosForShow.getValue();
            newList.addAll(photos.subList(photoCount, photosSize));
            photosForShow.setValue(newList);
        } else {
            LinkedList<Item> newList = photosForShow.getValue();
            newList.addAll(photos.subList(photoCount, next));
            photosForShow.setValue(newList);
        }
        photoCount = next;
        if (photoCount == photos.size()){
            showBtnMore.setValue(View.GONE);
        }
    }
}
