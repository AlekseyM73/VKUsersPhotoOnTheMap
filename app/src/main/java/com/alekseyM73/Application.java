package com.alekseyM73;

import android.content.Intent;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;
import com.google.android.libraries.places.api.Places;

public class Application extends android.app.Application{
    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                // VKAccessToken is invalid
                Intent intent = new Intent(Application.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Places.initialize(getApplicationContext(), "AIzaSyDBgLHJYirxcbCrWqn32GMNOcl9XYVyZQc");
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);

    }
}
