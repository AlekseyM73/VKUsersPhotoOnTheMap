package com.alekseyM73.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private final String KEY_PREFERENCES = "com.alekseyM73.settings";
    private final String KEY_PREFERENCES_TOKEN = "com.cathy.token";

    public void saveToken(String token, Activity activity){
        SharedPreferences.Editor editor = activity.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_PREFERENCES_TOKEN, token);
        editor.apply();
    }

    public  String getToken(Context context) {
        SharedPreferences mSettings= context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE);
        return mSettings.getString(KEY_PREFERENCES_TOKEN, null);
    }
}
