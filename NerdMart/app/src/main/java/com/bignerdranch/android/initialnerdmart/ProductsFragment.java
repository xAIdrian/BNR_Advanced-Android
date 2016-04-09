package com.bignerdranch.android.initialnerdmart;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.android.initialnerdmart.databinding.FragmentProductsBinding;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;

import java.util.Collections;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ProductsFragment extends NerdMartAbstractFragment {

    private ProductRecyclerViewAdapter mAdapter;
    private FragmentProductsBinding mFragmentProductBinding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        mFragmentProductBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_products, container, false);

        ProductRecyclerViewAdapter.AddProductClickEvent
                addProductClickEvent = this::postProductToCart;
        mAdapter = new ProductRecyclerViewAdapter(Collections.EMPTY_LIST,
                getActivity(), addProductClickEvent);
        setupAdapter();
        updateUI();

        return mFragmentProductBinding.getRoot();
    }

    private void setupAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentProductBinding.fragmentProductsRecyclerView.setLayoutManager(linearLayoutManager);
        mFragmentProductBinding.fragmentProductsRecyclerView.setAdapter(mAdapter);
    }

    private void updateUI() {
        addSubscription(mNerdMartServiceManager.getProducts()
                        .toList()
                        .compose(loadingTransformer())
                        .subscribe(products -> {
                            Timber.i("received products :: " + products);

                            mAdapter.setProducts(products);
                            mAdapter.notifyDataSetChanged();
                        })
        );
    }

    private void postProductToCart(Product product) {

        Observable<Boolean> cartSuccessObservable = mNerdMartServiceManager
                .postProductToCart(product)
                .compose(loadingTransformer())
                .cache(); //cache catches all of your Observables while the loading dialog is present and launches multiple observable at one if they are waiting to launch

        Subscription cartUpdateNotificationObservable = cartSuccessObservable
                .subscribe(aBoolean -> {
                    int message = aBoolean ? R.string.product_add_success_message : R.string.product_add_failure_message;
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                });

        addSubscription(cartUpdateNotificationObservable);
        addSubscription(cartSuccessObservable
                .filter(aBoolean -> aBoolean) //if we don't successfully post to the cart we drop the Observable
                .subscribeOn(Schedulers.newThread()) //perform our observable actions on...
                .flatMap(aBoolean -> mNerdMartServiceManager.getCart()) //for every successful observable get the cart object that the observable wraps
                .subscribe(cart -> { //subscribe start our Observable and makes it "hot"
                    ((NerdMartAbstractActivity) getActivity()).updateCartStatus(cart);
                    updateUI();
                }));
    }
}
