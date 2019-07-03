package com.alekseyM73.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toolbar;

import com.alekseyM73.R;

public class PageActivity extends AppCompatActivity {

    private static final String URL_PAGE = "https://vk.com/id";
    private static final String URL_PHOTO = "https://vk.com/photo";

    public static final String KEY_ID = "com.alekseyM73.user_id";
    public static final String KEY_PHOTO_ID = "com.alekseyM73.photo_id";
    public static final String KEY_ACTION = "com.alekseyM73.action";

    public static final String KEY_NAME = "com.alekseyM73.name";

    public static final String ACTION_PHOTO = "show_photo";
    public static final String ACTION_PAGE = "show_page";

    private WebView webView;
    private String url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        webView = findViewById(R.id.PageWeb);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        toolbar.inflateMenu(R.menu.menu_page);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            String name = arguments.getString(KEY_NAME);
            toolbar.setTitle(name);

            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);

            String action = arguments.getString(KEY_ACTION);
            if (action != null){
                long id = arguments.getLong(KEY_ID);
                if (action.equals(ACTION_PAGE)){
                    if (id > 0){
                        url = URL_PAGE + id;
                        webView.loadUrl(url);
                    }
                } else {
                    long photoId = arguments.getLong(KEY_PHOTO_ID);
                    url = URL_PHOTO + id + "_" + photoId;
                    webView.loadUrl(url);
                }
            }
        }
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_page_setting: {
                    Uri address = Uri.parse(url);
                    Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);

                    if (openLinkIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(openLinkIntent);
                    } else {
                        Log.d("Intent", "Не получается обработать намерение!");
                    }
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
