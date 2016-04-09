package com.bignerdranch.android.initialnerdmart.inject;

import android.content.Context;

import com.bignerdranch.android.initialnerdmart.model.DataStore;
import com.bignerdranch.android.initialnerdmart.model.service.NerdMartServiceManager;
import com.bignerdranch.android.initialnerdmart.viewmodel.NerdMartViewModel;
import com.bignerdranch.android.nerdmartservice.service.NerdMartService;
import com.bignerdranch.android.nerdmartservice.service.NerdMartServiceInterface;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by amohnacs on 4/5/16.
 */

@Module
public class NerdMartApplicationModule {

    private Context mApplicationContext;

    public NerdMartApplicationModule(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Provides
    @Singleton
    DataStore provideDataStore() {
        return new DataStore();
    }

    @Provides
    NerdMartServiceInterface providesNerdMartServiceInterface() {
        return new NerdMartService();
    }

    @Provides
    @Singleton
    NerdMartServiceManager providesNerdMartServiceManager(
            NerdMartServiceInterface serviceInterface, DataStore dataStore) {
        return new NerdMartServiceManager(serviceInterface, dataStore);
    }

    @Provides
    NerdMartViewModel provideNerdMartViewModel(DataStore dataStore) {
        return new NerdMartViewModel(mApplicationContext, dataStore.getCachedCart(),
                dataStore.getCachedUser());
    }
}
