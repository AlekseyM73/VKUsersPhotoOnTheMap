package com.alekseyM73;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.model.VKScopes;


public class MainActivity extends Activity {

    private String[] scope = new String[]{
            VKScope.FRIENDS
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VKSdk.login(this, scope);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
            // Пользователь успешно авторизовался
                Toast.makeText(getApplicationContext(), "GOOD", Toast.LENGTH_LONG);



            }
            @Override
            public void onError(VKError error) {
            // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG);
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
