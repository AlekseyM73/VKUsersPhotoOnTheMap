package com.alekseyM73.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alekseyM73.R;

public class PageActivity extends AppCompatActivity {

    private static final String URL = "https://vk.com/id";
    private static final String URLulbum = "https://vk.com/album";
    public static final String KEY_USER_ID = "com.alekseyM73.view.user.id";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        webView = findViewById(R.id.PageWeb);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            //Скорее всего не нужная проверка, проверил 30 страниц, все с стандартным id

            //Раз уж вы решили предерживаться паттерна MVVM - выносите логику с UI слоя,
            // зочьем она вам тут, это вьюха и тут логики никакой не должно быть
            if(arguments.get("ID") != null){
                try {
                    Long idPage = arguments.getLong("ID");
                    webView.loadUrl(URL + idPage);
                }catch (NumberFormatException e){
                    String idPage = arguments.getString("ID");
                    webView.loadUrl(URL.substring(0, URL.length()-3) + idPage);
                }
            }else if(arguments.get("IDalbum") != null){
                String idAlbum = arguments.getString("IDalbum");
                Log.d("mylog", idAlbum + " Id Album");
                webView.loadUrl(URLulbum + idAlbum);
            }
        }
    }
}
