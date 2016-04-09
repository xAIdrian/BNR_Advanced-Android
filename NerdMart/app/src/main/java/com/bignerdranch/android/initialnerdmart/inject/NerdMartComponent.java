package com.bignerdranch.android.initialnerdmart.inject;

import com.bignerdranch.android.initialnerdmart.NerdMartAbstractActivity;
import com.bignerdranch.android.initialnerdmart.NerdMartAbstractFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by amohnacs on 4/5/16.
 */

@Singleton
@Component ( modules = { NerdMartApplicationModule.class })
public interface NerdMartComponent {
    void inject(NerdMartAbstractFragment fragment);
    void inject(NerdMartAbstractActivity activity);
}
