package com.bignerdranch.android.networkingarchitecture.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.networkingarchitecture.R;
import com.bumptech.glide.Glide;

public class VenueView extends LinearLayout {
    private TextView mTitleTextView;
    private TextView mAddressTextView;
    private ImageView iconImageView;

    public VenueView(Context context) {
        this(context, null);
    }

    public VenueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        setLayoutParams(params);



        LayoutInflater inflater = LayoutInflater.from(context);
        VenueView view = (VenueView) inflater.inflate(
                R.layout.view_venue, this, true);
        mTitleTextView = (TextView) view.findViewById(
                R.id.view_venue_list_VenueTitleTextView);
        mAddressTextView = (TextView) view.findViewById(
                R.id.view_venue_list_VenueLocationTextView);
        iconImageView = (ImageView) view.findViewById(R.id.view_venue_list_iconImageView);
    }

    public void setVenueTitle(String title) {
        mTitleTextView.setText(title);
    }

    public void setVenueAddress(String address) {
        mAddressTextView.setText(address);
    }

    public void setIconImageView(String url) {
        String downloadUrl = url + "88.png";

        Glide.with(getContext()).load(downloadUrl).into(iconImageView);
        iconImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
    }
}
