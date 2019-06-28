//package com.alekseyM73.view;
//
//import android.arch.lifecycle.ViewModelProviders;
//import android.support.annotation.NonNull;
//import android.support.constraint.ConstraintLayout;
//import android.support.design.widget.CoordinatorLayout;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.animation.LinearInterpolator;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.alekseyM73.adapter.PhotosAdapter;
//import com.alekseyM73.R;
//import com.alekseyM73.listener.ScrollingListener;
//import com.alekseyM73.viewmodel.SearchVM;
//
//import java.util.LinkedList;
//
//public class PhotosActivity extends AppCompatActivity {
//
//    private SearchVM searchVM;
//    private ProgressBar progressBar;
//    Button button;
//    private PhotosAdapter adapter;
//
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
//}
