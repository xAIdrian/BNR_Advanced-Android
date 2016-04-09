package com.bignerdranch.android.initialtwittersyncadapter.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by amohnacs on 4/6/16.
 */
public class SyncService extends Service {

    @Override
    public IBinder onBind(Intent intent) {

        return new SyncAdapter(this, true).getSyncAdapterBinder();
    }
}
