package com.bignerdranch.android.initialtwittersyncadapter.web;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by amohnacs on 4/6/16.
 */
public class AuthorizationInterceptor implements Interceptor {
    private final String TAG = getClass().getSimpleName();

    private static final String AUTH_HEADER = "Authorization";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        TwitterOauthHelper oauthHelper = TwitterOauthHelper.get();

        try {
            String authHeaderString = oauthHelper.getAuthorizationHeaderString(request);
            request = request.newBuilder().addHeader(AUTH_HEADER, authHeaderString).build();


        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            Log.e(TAG, "failed to get auth header string: ");
            e.printStackTrace();
        }

        return chain.proceed(request);
    }
}
