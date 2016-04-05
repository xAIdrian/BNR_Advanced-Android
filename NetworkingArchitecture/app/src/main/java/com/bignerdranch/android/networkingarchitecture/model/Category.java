package com.bignerdranch.android.networkingarchitecture.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")private String mId;
    @SerializedName("name")private String mName;
    @SerializedName("icon")private Icon mIcon;
    @SerializedName("primary")private boolean mPrimary;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Icon getmIcon() {
        return mIcon;
    }

    public void setmIcon(Icon mIcon) {
        this.mIcon = mIcon;
    }

    public boolean ismPrimary() {
        return mPrimary;
    }

    public void setmPrimary(boolean mPrimary) {
        this.mPrimary = mPrimary;
    }
}
