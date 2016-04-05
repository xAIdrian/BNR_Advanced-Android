package com.bignerdranch.android.networkingarchitecture.controller;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.networkingarchitecture.R;

public class VenueListActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    public static final String SEARCH_STRING = "venuelistactivity.searchstring";

    VenueListFragment listFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_list);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
            Bundle fragmentSearchBundle = new Bundle();
            fragmentSearchBundle.putString(SEARCH_STRING, query);
            listFrag = new VenueListFragment();
            listFrag.setArguments(fragmentSearchBundle);

        } else {
            listFrag = new VenueListFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, listFrag)
                .commit();
    }

}
