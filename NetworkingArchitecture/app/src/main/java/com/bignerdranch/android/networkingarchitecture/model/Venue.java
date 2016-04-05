package com.bignerdranch.android.networkingarchitecture.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Venue {
    @SerializedName("id")private String mId;
    @SerializedName("name")private String mName;
    @SerializedName("verified")private boolean mVerified;
    @SerializedName("location")private Location mLocation;
    @SerializedName("categories")private List<Category> mCategoryList;

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getFormattedAddress() {
        return mLocation.getFormattedAddress();
    }

    public List<Category> getCategoryList() {
        return mCategoryList;
    }
}
