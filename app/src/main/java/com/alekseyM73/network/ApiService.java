package com.alekseyM73.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private final static String BASE_URL = "https://api.vk.com/method/";
    public final static String URL_SEARCH = "https://maps.googleapis.com/maps/api/place/queryautocomplete/json?key=AIzaSyDBgLHJYirxcbCrWqn32GMNOcl9XYVyZQc&input=";
    public final static String URL_PLACE_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json?fields=geometry&key=AIzaSyDBgLHJYirxcbCrWqn32GMNOcl9XYVyZQc&placeid=";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {

        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()

                    .readTimeout(15, TimeUnit.SECONDS)

                    .connectTimeout(15, TimeUnit.SECONDS)
//                    .addInterceptor(logging)

                    .build();


            retrofit = new Retrofit.Builder()

                    .baseUrl(BASE_URL)

                    .client(okHttpClient)

                    .addConverterFactory(GsonConverterFactory.create())

                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                    .build();
        }
        return retrofit;
    }
}
