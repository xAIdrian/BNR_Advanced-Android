package com.bignerdranch.android.initialnerdmart.model;

import com.bignerdranch.android.nerdmartservice.service.payload.Cart;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;
import com.bignerdranch.android.nerdmartservice.service.payload.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by amohnacs on 4/5/16.
 */
public class DataStore {

    private User mCachedUser;
    private List<Product> mCachedProducts;
    private Cart mCachedCart;

    public UUID getCachedAuthToken() {
        return mCachedUser.getAuthToken();
    }

    public void setCachedUser(User user) {
        mCachedUser = user;
    }

    public User getCachedUser() {
        return mCachedUser;
    }

    public void setCachedProducts(List<Product> products) {
        mCachedProducts = products;
    }

    public void setCachedCart(Cart cart) {
        mCachedCart = cart;
    }

    public Cart getCachedCart() {
        return mCachedCart;
    }

    public void clearCache() {
        mCachedProducts = new ArrayList<>();
        mCachedCart = null;
        mCachedUser = null;
    }

}
