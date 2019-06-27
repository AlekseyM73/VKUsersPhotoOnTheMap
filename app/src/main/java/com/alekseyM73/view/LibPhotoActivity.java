package com.alekseyM73.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.alekseyM73.R;

public class LibPhotoActivity extends AppCompatActivity {

    private static final String URL = "https://vk.com/album";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_photo);
        Bundle arguments = getIntent().getExtras();
        Long idAlbum = arguments.getLong("AlbubID");
        webView = findViewById(R.id.LibWeb);

        webView.loadUrl(URL + idAlbum);
    }
}
