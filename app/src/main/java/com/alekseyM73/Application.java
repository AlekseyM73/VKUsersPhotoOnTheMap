package com.alekseyM73;

import com.google.android.libraries.places.api.Places;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(getApplicationContext(), "AIzaSyDBgLHJYirxcbCrWqn32GMNOcl9XYVyZQc");
    }
}
