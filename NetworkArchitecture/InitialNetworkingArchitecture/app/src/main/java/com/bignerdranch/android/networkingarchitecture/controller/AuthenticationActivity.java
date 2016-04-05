package com.bignerdranch.android.networkingarchitecture.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bignerdranch.android.networkingarchitecture.helper.FoursquareOauthUriHelper;
import com.bignerdranch.android.networkingarchitecture.model.TokenStore;
import com.bignerdranch.android.networkingarchitecture.web.DataManager;

public class AuthenticationActivity extends AppCompatActivity {
    private WebView mWebView;
    private DataManager mDataManager;

    public static Intent newIntent(Context context) {
        return new Intent(context, AuthenticationActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView = new WebView(this);
        setContentView(mWebView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(mWebViewClient);

        mDataManager = DataManager.get(this);
        mWebView.loadUrl(mDataManager.getAuthenticationUrl());
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {

            if(url.contains(DataManager.OAUTH_REDIRECT_URI)) {
                FoursquareOauthUriHelper uriHelper =
                        new FoursquareOauthUriHelper(url);

                if(uriHelper.isAuthorized()) {
                    //fetch token and store it
                    String accessToken = uriHelper.getAccessToken();
                    TokenStore tokenStore = TokenStore.get(AuthenticationActivity.this);
                    tokenStore.setAccessToken(accessToken);
                }
                finish();
            }
            return false;
        }
    };
}
