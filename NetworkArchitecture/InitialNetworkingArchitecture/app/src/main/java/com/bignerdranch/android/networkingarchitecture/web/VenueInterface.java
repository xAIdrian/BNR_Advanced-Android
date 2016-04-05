package com.bignerdranch.android.networkingarchitecture.web;

import com.bignerdranch.android.networkingarchitecture.model.VenueSearchResponse;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by amohnacs on 4/4/16.
 */
public interface VenueInterface {

    @GET("/venues/search")
    void venueSearchByLocation(
            @Query("ll") String latLngString,
            Callback<VenueSearchResponse> callback);

    @GET("/venues/search")
    void venueSearchByPlace(
            @Query("near") String nearString,
            Callback<VenueSearchResponse> callback
    );

    @FormUrlEncoded
    @POST("/checkins/add")
    Observable<Object> venueCheckIn(@Field("venueId") String venueId);
            //, Callback<Object> callback);

}
