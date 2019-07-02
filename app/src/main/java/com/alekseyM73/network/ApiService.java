package com.alekseyM73.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//Странный нейминг, сервисом по сути класс не является, лучше уже ApiProvider
public class ApiService {

    //Такое лучше выносить в app.gradle в билд варианты, чтоб если у вас есть dev и prod сервак
    //вы просто настроили два билд варианта и аппка при сборке сама (взависимости от билд варианта)
    //знала на какой сервак ей смотреть
    //Сейчас это конечно не ваш случай, но все же. А в вашем варианте хорошим тоном было бы сделать
    //отдельный класс и засунуть в него все строковые константы из проекта
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
                    .addInterceptor(logging)

                    .build();

//            Омг зачем эти пробелы между строками, мои глаза T_T
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
