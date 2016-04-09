package com.bignerdranch.android.initialtwittersyncadapter.controller;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bignerdranch.android.initialtwittersyncadapter.account.Authenticator;
import com.bignerdranch.android.initialtwittersyncadapter.web.AuthenticationInterface;
import com.bignerdranch.android.initialtwittersyncadapter.web.AuthorizationInterceptor;
import com.bignerdranch.android.initialtwittersyncadapter.web.TwitterOauthHelper;
import com.squareup.okhttp.OkHttpClient;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class AuthenticationActivity extends AccountAuthenticatorActivity {
    private static final String TAG = "AuthenticationActivity";

    private static final String EXTRA_ACCOUNT_TYPE = "com.bignerdranch.android.initialtwittersyncadapter.ACCOUNT_TYPE";
    private static final String EXTRA_AUTH_TYPE = " com.bignerdranch.android.initialtwittersyncadapter.AUTH_TYPE";

    private static final String TWITTER_ENDPOINT = "https://api.twitter.com";
    private static final String TWITTER_OAUTH_ENDPOINT = "https://api.twitter.com/oauth/authorize";

    private static final String CALLBACK_URL = "http://www.bignerdranch.com";
    public static final String OAUTH_TOKEN_SECRET_KEY =
            "com.bignerdranch.android.initialtwittersyncadapter.OAUTH_TOKEN_SECRET";

    private WebView mWebView;
    private RestAdapter mRestAdapter;

    private TwitterOauthHelper mTwitterOauthHelper;
    private AuthenticationInterface mAuthenticationInterface;

    public static Intent newIntent(Context context, String accountType, String authTypeToken) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.putExtra(EXTRA_ACCOUNT_TYPE, accountType);
        intent.putExtra(EXTRA_AUTH_TYPE, authTypeToken);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView = new WebView(this);
        setContentView(mWebView);
        mWebView.setWebViewClient(mWebViewClient);

        mTwitterOauthHelper = TwitterOauthHelper.get();
        mTwitterOauthHelper.resetOauthToken(); //wiping token

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new AuthorizationInterceptor());//this is constructing our Token to meet Twitter's format

        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(TWITTER_ENDPOINT)
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        mAuthenticationInterface = mRestAdapter.create(AuthenticationInterface.class);
        mAuthenticationInterface.fetchRequestToken("", new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Uri uri = getResponseUri(response);
                String oauthToken = uri.getQueryParameter("oauth_token");
                Uri twitterOauthUri = Uri.parse(TWITTER_OAUTH_ENDPOINT).buildUpon()
                        .appendQueryParameter("oauth_token", oauthToken)
                        .build();

                mWebView.loadUrl(twitterOauthUri.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failed to fetch request token: ");
                error.printStackTrace();
            }
        });

    }

    private Uri getResponseUri(Response response) {
        String responseBody = new String(((TypedByteArray) response.getBody()).getBytes());
        String parseUrl = "http://localhost?" + responseBody;
        return Uri.parse(parseUrl);
    }

    /**
     * this is the webclient we should use when the user approves permission to the account
     * we are storing the token that is given back to us.
     */
    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(!url.contains(CALLBACK_URL)) {
                return true;
            }

            Uri callbackUri = Uri.parse(url);
            String oauthToken = callbackUri.getQueryParameter("oauth_token");
            String oauthVerifier = callbackUri.getQueryParameter("oauth_verifier");

            mTwitterOauthHelper.setOauthToken(oauthToken, null);

            mAuthenticationInterface.fetchAccessToken(oauthVerifier, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    Uri uri = getResponseUri(response);
                    String oauthToken = uri.getQueryParameter("oauth_token");
                    String oauthTokenSecret = uri.getQueryParameter("oauth_token_secret");
                    mTwitterOauthHelper.setOauthToken(oauthToken, oauthTokenSecret);

                    setupAccount(oauthToken, oauthTokenSecret);

                    final Intent intent = createAccountManagerIntent(oauthToken);

                    setAccountAuthenticatorResult(intent.getExtras());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "failure : ");
                    error.printStackTrace();
                }
            });

            return true;
        }
    };

    private void setupAccount(String oauthToken, String oauthTokenSecret) {
        String accountType = getIntent().getStringExtra(EXTRA_ACCOUNT_TYPE);
        final Account account = new Account(Authenticator.ACCOUNT_NAME, accountType);
        String authTokenType = getIntent().getStringExtra(EXTRA_AUTH_TYPE);

        AccountManager accountManager = AccountManager.get(AuthenticationActivity.this);
        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, authTokenType, oauthToken);
        accountManager.setUserData(account, OAUTH_TOKEN_SECRET_KEY, oauthTokenSecret);
    }


    private Intent createAccountManagerIntent(String oauthToken) {

        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Authenticator.ACCOUNT_NAME);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getIntent().getStringExtra(EXTRA_ACCOUNT_TYPE));
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, oauthToken);
        return intent;
    }
}
