package com.bignerdranch.android.initialtwittersyncadapter.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by amohnacs on 4/6/16.
 */
public class TweetSearchResponse {

    @SerializedName("statuses")
    private List<Tweet> mTweetLists;

    public List<Tweet> getTweetList() {
        return mTweetLists;
    }
}
