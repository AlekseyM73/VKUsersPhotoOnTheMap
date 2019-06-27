package com.alekseyM73.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.alekseyM73.R;

public class PageActivity extends AppCompatActivity {

    private static final String URL = "https://vk.com/";
    public static final String KEY_USER_ID = "com.alekseyM73.view.user.id";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            Long idPage = arguments.getLong(KEY_USER_ID);
            webView.loadUrl(URL + idPage);
        }
    }
}
