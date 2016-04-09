package com.bignerdranch.android.initialnerdmart.model.service;

import com.bignerdranch.android.initialnerdmart.model.DataStore;
import com.bignerdranch.android.nerdmartservice.service.NerdMartServiceInterface;
import com.bignerdranch.android.nerdmartservice.service.payload.Cart;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;

import java.util.UUID;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by amohnacs on 4/5/16.
 */
public class NerdMartServiceManager {

    private Scheduler mScheduler;

    private NerdMartServiceInterface mNerdMartServiceInterface;
    private DataStore mDataStore;

    private final Observable.Transformer<Observable, Observable>
        mScheduleTransformer =
            observable -> observable.subscribeOn(Schedulers.newThread()) //handle our observers on
                                    .observeOn(mScheduler);             //AndroidSchedulers.mainThread()); //handle our subscribers on

    public NerdMartServiceManager(NerdMartServiceInterface serviceInterface, DataStore dataStore) {
        mNerdMartServiceInterface = serviceInterface;
        mDataStore = dataStore;
        mScheduler = scheduler;
    }

    @SuppressWarnings("unchecked")
    private <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) mScheduleTransformer;
    }

    /**
     * Returns true or false based on whether the API returned a User object or null based on the
     * credentials you sent to the "server"
     * @param username
     * @param password
     * @return
     */
    public Observable<Boolean> authenticate(String username, String password) {

        return mNerdMartServiceInterface.authenticate(username, password)
                .doOnNext(mDataStore::setCachedUser) // .doOnNext(user -> mDataStore.setCachedUser(user))
                .map(user -> user != null)
                .compose(applySchedulers());

                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());*/

        /* w/out RetroLambda
        return mNerdMartServiceInterface.authenticate(username, password)
                .map(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return user != null;
                    }
                });*/
    }

    private Observable<UUID> getToken() {
        return Observable.just(mDataStore.getCachedAuthToken());
    }

    public Observable<Product> getProducts() {
        return getToken().flatMap(mNerdMartServiceInterface::requestProducts) //request products returns an Observable containing an array.  flatMap unpacks it into a list of products
                .doOnNext(mDataStore::setCachedProducts)
                .flatMap(Observable::from) //creating a new observable from that arraylist. Now it is an observable containing a number of products (not in a list) {"product", "product2", "product2", ...}
                .compose(applySchedulers());
                //.subscribeOn(Schedulers.io()) //observable sends message to subscribers
                //.observeOn(AndroidSchedulers.mainThread()); //subscribers observe observables
    }

    public Observable<Cart> getCart() {
        return getToken().flatMap(mNerdMartServiceInterface::fetchUserCart)
                .doOnNext(mDataStore::setCachedCart)
                .compose(applySchedulers());
    }

    public Observable<Boolean> postProductToCart(final Product product) {
        return getToken()
                .flatMap(uuid -> mNerdMartServiceInterface.addProductToCart(uuid, product))
                .compose(applySchedulers());
    }

    public Observable<Boolean> signout() {
        mDataStore.clearCache();
        return mNerdMartServiceInterface.signout();
    }
}
