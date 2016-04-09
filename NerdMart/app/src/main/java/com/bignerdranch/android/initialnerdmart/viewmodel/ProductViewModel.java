package com.bignerdranch.android.initialnerdmart.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.v4.content.ContextCompat;

import com.bignerdranch.android.initialnerdmart.R;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;

import java.text.NumberFormat;

/**
 * Created by amohnacs on 4/5/16.
 */
public class ProductViewModel extends BaseObservable {

    private Context mContext;
    private Product mProduct;
    private int mRowNumber;

    public ProductViewModel(Context mContext, Product mProduct, int mRowNumber) {
        this.mContext = mContext;
        this.mProduct = mProduct;
        this.mRowNumber = mRowNumber;
    }

    public Integer getId() {
        return mProduct.getId();
    }

    public String getSKU() {
        return mProduct.getSKU();
    }

    public String getTitle() {
        return mProduct.getTitle();
    }

    public String getDescription() {
        return mProduct.getDescription();
    }

    public String getDisplayPrice() {
        return NumberFormat.getCurrencyInstance()
                .format(mProduct.getPriceInCents() / 100.0);
    }

    public String getProductUrl() {
        return mProduct.getProductUrl();
    }

    public String getProductQuantityDisplay() {
        return mContext.getString(R.string.quantity_display_text, mProduct.getBackendQuantity());
    }

    public int getRowColor() {
        int resourceId = mRowNumber % 2 == 0 ? R.color.white : R.color.light_blue;
        return ContextCompat.getColor(mContext, resourceId);
    }

}
