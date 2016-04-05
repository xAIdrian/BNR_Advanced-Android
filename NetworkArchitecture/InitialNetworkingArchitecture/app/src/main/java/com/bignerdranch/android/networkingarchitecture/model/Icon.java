package com.bignerdranch.android.networkingarchitecture.model;

import com.google.gson.annotations.SerializedName;

public class Icon {
    @SerializedName("prefix")private String mPrefix;
    @SerializedName("suffix")private String mSuffix;

    public String getmPrefix() {
        return mPrefix;
    }

    public void setmPrefix(String mPrefix) {
        this.mPrefix = mPrefix;
    }

    public String getmSuffix() {
        return mSuffix;
    }

    public void setmSuffix(String mSuffix) {
        this.mSuffix = mSuffix;
    }
}
