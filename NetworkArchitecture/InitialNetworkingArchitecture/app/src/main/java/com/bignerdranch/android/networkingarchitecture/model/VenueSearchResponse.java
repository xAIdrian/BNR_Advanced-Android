package com.bignerdranch.android.networkingarchitecture.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by amohnacs on 4/4/16.
 */
public class VenueSearchResponse {
    @SerializedName("venues")List<Venue> mVenueList;

    public List<Venue> getVenueList() {
        return mVenueList;
    }
}
