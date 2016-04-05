package com.bignerdranch.android.networkingarchitecture.web;

import android.content.Context;
import android.net.Uri;

import com.bignerdranch.android.networkingarchitecture.exception.UnauthorizedException;
import com.bignerdranch.android.networkingarchitecture.listener.VenueCheckInListener;
import com.bignerdranch.android.networkingarchitecture.listener.VenueSearchListener;
import com.bignerdranch.android.networkingarchitecture.model.TokenStore;
import com.bignerdranch.android.networkingarchitecture.model.Venue;
import com.bignerdranch.android.networkingarchitecture.model.VenueSearchResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by amohnacs on 4/4/16.
 */
public class DataManager {
    private final String TAG = "DataManager";

    private static final String FOURSQUARE_ENDPOINT = "https://api.foursquare.com/v2";
    private static final String OAUTH_ENDPOINT = "https://foursquare.com/oauth2/authenticate";
    public static final String OAUTH_REDIRECT_URI = "http://www.bignerdranch.com";
    private static final String CLIENT_ID = "O0RT35PJ1M2C5E4032VZVL1F4WZOXA1JVOV23UGUQVHAEKYU";
    private static final String CLIENT_SECRET = "LEPJELT4DEDIMAUPMH2K2AZKFDPCAFR5GXADLFQE1POTJEQL";
    private static final String FOURSQUARE_VERSION = "20150406";
    private static final String FOURSQUARE_MODE = "foursquare";
    private static final String SWARM_MODE = "swarm";

    private static final String TEST_LAT_LNG = "33.759, -84.332";

    private static DataManager sDataManager;
    private Context mContext;
    private static TokenStore sTokenStore;
    private RestAdapter mBasicRestAdapter;
    private RestAdapter mAuthenticatedRestAdapter;

    private List<Venue> mVenueList;
    private List<VenueSearchListener> mSearchListenerList;
    private List<VenueCheckInListener> mCheckInListenerList;

    public static DataManager get(Context context) {

        if(sDataManager == null) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(VenueSearchResponse.class,
                            new VenueListDeserializer())
                    .create();

            RestAdapter basicRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(FOURSQUARE_ENDPOINT)
                    .setConverter(new GsonConverter(gson))
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(sRequstInterceptor)
                    .build();

            RestAdapter authenticatedRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(FOURSQUARE_ENDPOINT)
                    .setConverter(new GsonConverter(gson))
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(sAuthenticatedRequestInterceptor)
                    .setErrorHandler(new RetrofitErrorHandler())
                    .build();

            sDataManager = new DataManager(context, basicRestAdapter, authenticatedRestAdapter);
        }

        return sDataManager;
    }

    public static DataManager get(Context context, RestAdapter basicRestAdapter, RestAdapter authenticatedRestAdapter) {

        if(sDataManager == null) {
            sDataManager = new DataManager(context, basicRestAdapter, authenticatedRestAdapter);
        }
        return sDataManager;
    }

    public DataManager(Context context, RestAdapter basicRestAdapter, RestAdapter authenticatedRestAdapter) {
        mContext = context.getApplicationContext();
        sTokenStore = TokenStore.get(mContext);
        mBasicRestAdapter = basicRestAdapter;
        mAuthenticatedRestAdapter = authenticatedRestAdapter;
        mSearchListenerList = new ArrayList<>();
        mCheckInListenerList = new ArrayList<>();
    }

    public void addVenueSearchListner(VenueSearchListener listener) {
        mSearchListenerList.add(listener);
    }

    public void removeVenueSearchListener(VenueSearchListener listener) {
        mSearchListenerList.remove(listener);
    }

    public void notifySearchListeners() {
        for(VenueSearchListener listener : mSearchListenerList) {
            listener.onVenueSearchFinished();
        }
    }

    public void addVenueCheckInListner(VenueCheckInListener listener) {
        mCheckInListenerList.add(listener);
    }

    public void removeVenueCheckInListener(VenueCheckInListener listener) {
        mCheckInListenerList.remove(listener);
    }

    public void notifyCheckInListeners() {
        for(VenueCheckInListener listener : mCheckInListenerList) {
            listener.onVenueCheckInFinished();
        }
    }

    private static RequestInterceptor sRequstInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam("client_id", CLIENT_ID);
            request.addQueryParam("client_secret", CLIENT_SECRET);
            request.addQueryParam("v", FOURSQUARE_VERSION);
            request.addQueryParam("m", FOURSQUARE_MODE);
        }
    };

    private static RequestInterceptor sAuthenticatedRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam("oauth_token", sTokenStore.getAccessToken());
            request.addQueryParam("v", FOURSQUARE_VERSION);
            request.addQueryParam("m", SWARM_MODE);
        }
    };

    public void fetchVenueSearch() {
        VenueInterface venueInterface = mBasicRestAdapter.create(VenueInterface.class);

        venueInterface.venueSearchByLocation(TEST_LAT_LNG, new Callback<VenueSearchResponse>() {
            @Override
            public void success(VenueSearchResponse venueSearchResponse, Response response) {
                mVenueList = venueSearchResponse.getVenueList();
                notifySearchListeners();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    //todo:
    public void fetchVenuePlaceSearch(String searcher) {
        VenueInterface venueInterface = mBasicRestAdapter.create(VenueInterface.class);

        venueInterface.venueSearchByPlace(searcher, new Callback<VenueSearchResponse>() {
            @Override
            public void success(VenueSearchResponse venueSearchResponse, Response response) {
                mVenueList = venueSearchResponse.getVenueList();
                notifySearchListeners();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void checkInToVenue(String venueId) {
        VenueInterface venueInterface = mAuthenticatedRestAdapter.create(VenueInterface.class);
        venueInterface.venueCheckIn(venueId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> notifyCheckInListeners(),
                        error -> handleCheckInException(error)
                );

    }

    private void handleCheckInException(Throwable error) {
        if (error instanceof UnauthorizedException) {    //instanceOf actually EQUALS typeOf
            sTokenStore.setAccessToken(null);
            notifyCheckInListenersTokenExpired();
        }
    }


                /* using Lambdas w/out Java 8
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) { //on success
                        notifyCheckInListeners();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                        error.printStackTrace();

                        if (error instanceof UnauthorizedException) {
                            sTokenStore.setAccessToken(null);
                            notifyCheckInListenersTokenExpired();
                        }
                    }
                });*/

        /* using a CallBack (baked into RetroFit)
        venueInterface.venueCheckIn(venueId, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                notifyCheckInListeners();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();

                if (error.getCause() instanceof UnauthorizedException) {
                    sTokenStore.setAccessToken(null);
                    notifyCheckInListenersTokenExpired();
                } else {
                    //todo: handle every other exception that you receive when making a post request
                }
            }
        });*/


    public List<Venue> getVenueList() {
        return  mVenueList;
    }

    public String getAuthenticationUrl() {
        return Uri.parse(OAUTH_ENDPOINT).buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("redirect_uri", OAUTH_REDIRECT_URI)
                .build()
                .toString();
    }

    public Venue getVenue(String venueId) {
        for(Venue venue : mVenueList) {
            if(venue.getId().equals(venueId)) {
                return venue;
            }
        }
        return null;
    }

    private void notifyCheckInListenersTokenExpired() {
        for(VenueCheckInListener listener : mCheckInListenerList) {
            listener.onTokenExpired();
        }
    }

}
