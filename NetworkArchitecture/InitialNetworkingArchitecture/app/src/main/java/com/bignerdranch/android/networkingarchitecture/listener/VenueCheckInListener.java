package com.bignerdranch.android.networkingarchitecture.listener;

/**
 * Created by amohnacs on 4/4/16.
 */
//with this extension every time we decide to checkIn (or a token is required)
    //we have to check to make sure the token if valid
public interface VenueCheckInListener extends AuthentificationListener {
    void onVenueCheckInFinished();
}
