package com.bignerdranch.android.initialtwittersyncadapter.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by amohnacs on 4/6/16.
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    public AuthenticatorService() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
