package com.bignerdranch.android.initialnerdmart;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.initialnerdmart.databinding.ActivityNerdmartAbstractBinding;
import com.bignerdranch.android.initialnerdmart.model.service.NerdMartServiceManager;
import com.bignerdranch.android.initialnerdmart.viewmodel.NerdMartViewModel;
import com.bignerdranch.android.nerdmartservice.service.payload.Cart;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class NerdMartAbstractActivity extends AppCompatActivity {

    private CompositeSubscription mCompositeSubscription;
    @Inject NerdMartServiceManager mNerdMartServiceManager;
    @Inject NerdMartViewModel mNerdMartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_nerdmart_abstract);

        mCompositeSubscription = new CompositeSubscription();
        NerdMartApplication.component(this).inject(this);
        ActivityNerdmartAbstractBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_nerdmart_abstract);
        binding.setLogoutButtonClickListener(v -> signout());
        binding.setNerdMartViewModel(mNerdMartViewModel);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    //.add(R.id.activity_abstract_nerdmart_fragment_frame, getFragment())
                    .add(binding.activityAbstractNerdmartFragmentFrame.getId(), getFragment())
                    .commit();
        }
    }

    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    private void signout() {
        addSubscription(mNerdMartServiceManager
                .signout()
                .subscribe(aBoolean -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }));
    }

    public void updateCartStatus(Cart cart) {
        mNerdMartViewModel.updateCartStatus(cart);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.clear();
    }

    protected abstract Fragment getFragment();
}
