package com.alekseyM73.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.alekseyM73.util.Preferences;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.alekseyM73.R;


public class MainActivity extends Activity {

    private TextView tvAuth;
    private View layout;
   // String token = new Preferences().getToken(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle arg = getIntent().getExtras();
        if (arg != null){
            Boolean logout = arg.getBoolean("logout");
           if (logout){
               new Preferences().saveToken(null,this);
               VKSdk.logout();
            }
        }
        initViews();
        login();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void initViews(){
        tvAuth = findViewById(R.id.tv_auth);
        layout = findViewById(R.id.layout);
        tvAuth.setOnClickListener(listener -> {
            login();
        });
    }

    private void login(){
        if (new Preferences().getToken(this) != null){
            onSuccess();
        } else {
            layout.setVisibility(View.INVISIBLE);
            String[] scopes = new String[]{VKScope.PHOTOS};
            VKSdk.login(this, scopes);
        }
    }

    private void onSuccess(){
        startActivity(new Intent(MainActivity.this, MapActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
            // Пользователь успешно авторизовался
                new Preferences().saveToken(res.accessToken, MainActivity.this);
                System.out.println("Token = " + res.accessToken);
                onSuccess();
            }

            @Override
            public void onError(VKError error) {
            // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                layout.setVisibility(View.VISIBLE);
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
