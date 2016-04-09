package com.bignerdranch.android.nerdmail.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.android.nerdmail.R;
import com.bignerdranch.android.nerdmail.model.EmailNotifier;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DrawerActivity extends AppCompatActivity {
    private static final String TAG = "DrawerActivity";
    private static final String EXTRA_CURRENT_DRAWER_ITEM
            = "DrawerActivity.CurrentDrawerItem";

    @Bind(R.id.activity_drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.activity_drawer_navigation_view) NavigationView mNavigationView;
    @Bind(R.id.activity_drawer_toolbar) Toolbar mToolbar;

    private int mCurrentToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        View headerView = mNavigationView.getHeaderView(0);
        TextView userNameTextView = (TextView) headerView.findViewById(R.id.nav_drawer_header_username);
        userNameTextView.setText("nerdynerd@bignerdranch.com");

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        //Handle switching Fragments here
                        mDrawerLayout.closeDrawers();

                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.nav_drawer_inbox:
                                fragment = new InboxFragment();
                                mCurrentToolbarTitle = R.string.nav_drawer_inbox;
                                break;

                            case R.id.nav_drawer_important:
                                fragment = new ImportantFragment();
                                mCurrentToolbarTitle = R.string.nav_drawer_important;
                                break;

                            case R.id.nav_drawer_spam:
                                fragment = new SpamFragment();
                                mCurrentToolbarTitle = R.string.nav_drawer_spam;
                                break;

                            case R.id.nav_drawer_all:
                                fragment = new AllFragment();
                                mCurrentToolbarTitle = R.string.nav_drawer_all;
                                break;

                            default:
                                Log.e(TAG, "Incorrect nav drawer item selected");
                                return false;
                        }
                        updateToolbarTitle();
                        updateFragment(fragment);
                        return true;
                    }
                }
        );

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar,
                R.string.open_drawer_content_description,
                R.string.close_drawer_content_description);

        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            updateFragment(new InboxFragment());
            mCurrentToolbarTitle = R.string.nav_drawer_inbox;

        } else {
            mCurrentToolbarTitle = savedInstanceState.getInt(
                    EXTRA_CURRENT_DRAWER_ITEM, R.string.nav_drawer_inbox);
        }

    }

    private void updateToolbarTitle() {
        if(mCurrentToolbarTitle != 0) {
            mToolbar.setTitle(mCurrentToolbarTitle);
        }
    }

    private void updateFragment(Fragment fragment) {

        if(fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.activity_drawer_fragment_container, fragment)
                    .commit();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateToolbarTitle();
        clearNotifications();
        markEmailAsNotified();
    }

    private void clearNotifications() {
        EmailNotifier notifier = EmailNotifier.get(this);
        notifier.clearNotifications();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CURRENT_DRAWER_ITEM, mCurrentToolbarTitle);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else {
            super.onBackPressed();
        }
    }

    private void markEmailAsNotified() {
        Intent intent = EmailService.getClearIntent(this);
        startService(intent);
    }
}
