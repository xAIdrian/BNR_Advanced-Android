package com.bignerdranch.android.initialtwittersyncadapter.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bignerdranch.android.initialtwittersyncadapter.controller.AuthenticationActivity;

/**
 * Created by amohnacs on 4/6/16.
 */
public class Authenticator extends AbstractAccountAuthenticator {

    public static final String ACCOUNT_NAME = "TwitterSyncAdapter";
    public static final String ACCOUNT_TYPE = "com.bignerdranch.android.initialtwittersyncadapter.USER_ACCOUNT";
    public static final String AUTH_TOKEN_TYPE = "com.bignerdranch.android.initialtwittersyncadapter.FULL_ACCESS";

    private Context mContext;

    public Authenticator(Context context) {
        super(context);

        mContext = context;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
                               Bundle options) throws NetworkErrorException {

        AccountManager accountManager = AccountManager.get(mContext);

        String authToken = accountManager.peekAuthToken(account, authTokenType);

        Bundle results = new Bundle();
        if(TextUtils.isEmpty(authToken)) {
            Intent intent = AuthenticationActivity.newIntent(
                    mContext, account.type, authTokenType);
            results.putParcelable(AccountManager.KEY_INTENT, intent);
        } else {
            results.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            results.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            results.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        }

        return results;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
                             String[] requiredFeatures, Bundle options) throws NetworkErrorException {

        Intent intent = AuthenticationActivity.newIntent(
                mContext, accountType, authTokenType);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
