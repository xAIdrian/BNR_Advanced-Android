package com.bignerdranch.android.networkingarchitecture.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Location {
    @SerializedName("lat")private double mLatitude;
    @SerializedName("lng")private double mLongitude;
    @SerializedName("formattedAddress")private List<String> mFormattedAddress;

    public String getFormattedAddress() {
        String formattedAddress = "";
        for (String addressPart : mFormattedAddress) {
            formattedAddress += addressPart;
            if (mFormattedAddress.indexOf(addressPart) !=
                    (mFormattedAddress.size() - 1)) {
                formattedAddress += " ";
            }
        }
        return formattedAddress;
    }
}
