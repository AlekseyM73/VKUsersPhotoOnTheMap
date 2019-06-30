package com.alekseyM73.network;

import com.alekseyM73.model.photo.PhotosResponse;
import com.alekseyM73.model.place.PlaceDetailsResponse;
import com.alekseyM73.model.search.PlaceSearchResponse;
import com.alekseyM73.model.user.UserInfoResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface VkApi {

    // sort: 0 - по дате добавления, 1 - по кол-ву Нравится
    // radius в метрах, по умолчанию 5000
//    @POST("photos.searchPhotos?")
//    Observable<PhotosResponse> getPhotos(
//            @Query("name") String name,
//            @Query("lat") double lat,
//            @Query("long") double lon,
//            @Query("start_time") long startTime,
//            @Query("end_time") long endTime,
//            @Query("sort") int sort,
//            @Query("offset") int offset,
//            @Query("count") int count,
//            @Query("radius") int radius);

    @GET("photos.search?")
    Observable<PhotosResponse> getPhotos(@Query("lat") double lat, @Query("long") double lon, @QueryMap Map<String, String> options);

    @GET("users.get?v=5.52&fields=sex,bdate,city")
    Observable<UserInfoResponse> getUserInfo(@Query("user_id") long id, @Query("access_token") String token);

    @GET()
    Observable<PlaceSearchResponse> searchPlace(@Url String url);

    @GET()
    Observable<PlaceDetailsResponse> getPlaceDetails(@Url String url);
}
