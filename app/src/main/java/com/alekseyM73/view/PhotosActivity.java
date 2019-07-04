package com.alekseyM73.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alekseyM73.Application;
import com.alekseyM73.listeners.PhotoListener;
import com.alekseyM73.R;
import com.alekseyM73.adapter.PhotosAdapter;
import com.alekseyM73.model.photo.Item;
import com.alekseyM73.viewmodel.PhotosVM;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

public class PhotosActivity extends AppCompatActivity implements PhotoListener {

    private PhotosVM photosVM;
    private PhotosAdapter adapter;
    private RecyclerView recyclerView;

    public static final String KEY_PHOTOS = "com.alekseyM73.view.photos_activity.photos";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        recyclerView = findViewById(R.id.rv_photos);
        adapter = new PhotosAdapter(new LinkedList<>(), this);
        recyclerView.setAdapter(adapter);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

        photosVM = ViewModelProviders.of(this).get(PhotosVM.class);

        photosVM = ViewModelProviders.of(this).get(PhotosVM.class);
        photosVM.getPhotos().observe(this, items -> {
            adapter.addItems(items);
        });

        photosVM.getMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        if (getIntent().getExtras() != null){
            String json = getIntent().getStringExtra(KEY_PHOTOS);
            Gson gson = new Gson();
            Type listType = new TypeToken<LinkedList<Item>>(){}.getType();
            LinkedList<Item> mapItems = gson.fromJson(json, listType);
            photosVM.setPhotos(mapItems);
        } else {
            photosVM.setPhotos(new LinkedList<>(Application.photosToGallery));
        }
    }

    @Override
    public void onClick(int position) {
        Gson gson = new Gson();
        Application.itemsForViewPager = new ArrayList(photosVM.getPhotos().getValue()){};
        Intent intent = new Intent(PhotosActivity.this, InfoActivity.class);
        intent.putExtra(InfoActivity.CURRENT_ITEM, position);
        intent.putExtra(InfoActivity.TYPE, InfoActivity.TYPE_LIST);
        startActivity(intent);
    }
}
