package com.alekseyM73;

import android.content.Intent;

import com.alekseyM73.model.photo.Item;
import com.alekseyM73.view.MainActivity;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;
import com.google.android.libraries.places.api.Places;

import java.util.HashSet;
import java.util.Set;

public class Application extends android.app.Application{

    public static Set<Item> photosToGallery;

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
