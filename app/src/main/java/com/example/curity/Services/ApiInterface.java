package com.example.curity.Services;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("maps/api/directions/json")
    Single<Result> getDirection(
            @Query("mode") String mode,
            @Query("transit_routing_preference") String preference,
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String key
    );

}
