package com.alekseyM73.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alekseyM73.R;
import com.alekseyM73.adapter.PhotosAdapter;
import com.alekseyM73.model.photo.Item;
import com.alekseyM73.viewmodel.PhotosVM;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class PhotosActivity extends AppCompatActivity {

    private PhotosVM photosVM;
    private Button button;
    private PhotosAdapter adapter;
    private RecyclerView recyclerView;

    public static final String KEY_DATA = "com.alekseyM73.view.photos_activity.items";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        recyclerView = findViewById(R.id.rv_photos);
        adapter = new PhotosAdapter(new LinkedList<>());
        recyclerView.setAdapter(adapter);

        button = findViewById(R.id.btn_more);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        photosVM = ViewModelProviders.of(this).get(PhotosVM.class);

        photosVM = ViewModelProviders.of(this).get(PhotosVM.class);
        photosVM.getPhotos().observe(this, items -> {
            adapter.addItems(items);
        });

        photosVM.getShowBtnMore().observe(this, visibility -> {
            button.setVisibility(visibility == null ? View.GONE : visibility);
        });

        photosVM.getMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        setListeners();

        if (getIntent().getExtras() != null){
            String json = getIntent().getStringExtra(KEY_DATA);
            Gson gson = new Gson();
            Type listType = new TypeToken<LinkedList<Item>>(){}.getType();
            LinkedList<Item> mapItems = gson.fromJson(json, listType);
            photosVM.setPhotos(mapItems);
        }
    }

    private void setListeners() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (button.getVisibility() == View.VISIBLE) {
                    if (dy > 0) {
                        button.animate().translationY(0).start();
                    } else {
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) button.getLayoutParams();
                        int fab_bottomMargin = layoutParams.bottomMargin;
                        button.animate().translationY(button.getHeight() + fab_bottomMargin).start();

                    }
                }
            }
        });

        button.setOnClickListener(listener -> {
            photosVM.loadMore();
        });
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_photos);
//
//        RecyclerView recyclerView = findViewById(R.id.rv_photos);
//        progressBar = findViewById(R.id.progress);
//        adapter = new PhotosAdapter(new LinkedList<>());
//        recyclerView.setAdapter(adapter);
//
//        button = findViewById(R.id.btn_more);
//
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//
//        searchVM = ViewModelProviders.of(this).get(SearchVM.class);
//        searchVM.getPhotos().observe(this, items -> {
//            adapter.addItems(items);
//        });
//
//        searchVM.getProgress().observe(this, progress -> {
//            progressBar.setVisibility(progress == null ? View.INVISIBLE : progress);
//        });
//
//        searchVM.getShowBtnMore().observe(this, visibility -> {
//            button.setVisibility(visibility == null ? View.GONE : visibility);
//        });
//
//        searchVM.getMessage().observe(this, message -> {
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//        });
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                if (button.getVisibility() == View.VISIBLE) {
//                    if (dy > 0) {
//                        button.animate().translationY(0).start();
//                    } else {
//                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) button.getLayoutParams();
//                        int fab_bottomMargin = layoutParams.bottomMargin;
//                        button.animate().translationY(button.getHeight() + fab_bottomMargin).start();
//
//                    }
//                }
//            }
//        });
//
//        button.setOnClickListener(listener -> {
//            searchVM.loadMore();
//        });
//    }
}
