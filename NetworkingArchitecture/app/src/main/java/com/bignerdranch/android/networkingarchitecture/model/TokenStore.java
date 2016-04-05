package com.bignerdranch.android.networkingarchitecture.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TokenStore {
    private static final String TOKEN_KEY = "TokenStore.TokenKey";

    private static TokenStore sTokenStore;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public static TokenStore get(Context context) {
        if (sTokenStore == null) {
            sTokenStore = new TokenStore(context);
        }
        return sTokenStore;
    }

    private TokenStore(Context context) {
        mContext = context.getApplicationContext();
        mSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public String getAccessToken() {
        return mSharedPreferences.getString(TOKEN_KEY, null);
    }

    public void setAccessToken(String accessToken) {
        mSharedPreferences.edit()
                .putString(TOKEN_KEY, accessToken)
                .apply();
    }
}