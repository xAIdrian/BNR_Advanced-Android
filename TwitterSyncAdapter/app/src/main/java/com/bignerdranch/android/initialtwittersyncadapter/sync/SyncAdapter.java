package com.bignerdranch.android.initialtwittersyncadapter.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.bignerdranch.android.initialtwittersyncadapter.account.Authenticator;
import com.bignerdranch.android.initialtwittersyncadapter.controller.AuthenticationActivity;
import com.bignerdranch.android.initialtwittersyncadapter.model.DatabaseContract;
import com.bignerdranch.android.initialtwittersyncadapter.model.Tweet;
import com.bignerdranch.android.initialtwittersyncadapter.model.TweetSearchResponse;
import com.bignerdranch.android.initialtwittersyncadapter.model.User;
import com.bignerdranch.android.initialtwittersyncadapter.web.AuthorizationInterceptor;
import com.bignerdranch.android.initialtwittersyncadapter.web.TweetInterface;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by amohnacs on 4/6/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TWITTER_ENDPOINT = "https://api.twitter.com/1.1";
    private static final String QUERY = "android";

    private String mAccessTokenSecret;
    private String mAccessToken;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        AccountManager accountManager = AccountManager.get(context);
        Account account = new Account(
                Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);

        mAccessTokenSecret = accountManager
                .getUserData(account, AuthenticationActivity.OAUTH_TOKEN_SECRET_KEY);
        mAccessToken = accountManager.peekAuthToken(account, Authenticator.AUTH_TOKEN_TYPE);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        List<Tweet> tweets = fetchTweets();
        insertTweetData(tweets);
    }

    private void insertTweetData(List<Tweet> tweets) {

        User user;
        for(Tweet tweet : tweets) {

            user = tweet.getUser();
            getContext().getContentResolver()
                    .insert(DatabaseContract.User.CONTENT_URI, user.getContentValues());
            getContext().getContentResolver()
                    .insert(DatabaseContract.Tweet.CONTENT_URI, tweet.getContentValues());
        }
    }

    private List<Tweet> fetchTweets() {

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new AuthorizationInterceptor());

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TWITTER_ENDPOINT)
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        TweetInterface tweetInterface = restAdapter.create(TweetInterface.class);
        TweetSearchResponse response = tweetInterface.searchTweets(QUERY);
        return response.getTweetList();

    }
}
