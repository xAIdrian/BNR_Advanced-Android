package com.bignerdranch.android.initialtwittersyncadapter.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.bignerdranch.android.initialtwittersyncadapter.model.DatabaseContract;

/**
 * Created by amohnacs on 4/6/16.
 */
public class TweetContentProvider extends ContentProvider {
    private static final int TWEET_LIST = 1;
    private static final int TWEET_ID = 2;
    private static final int USER_LIST = 3;
    private static final int USER_ID = 4;
    private static final UriMatcher sUriMatcher;

    private TweetDatabaseHelper mTweetDatabaseHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH); //default impl
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.Tweet.TABLE_NAME, TWEET_LIST);
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.Tweet.TABLE_NAME + "/#", TWEET_ID);
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.User.TABLE_NAME, USER_LIST);
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.User.TABLE_NAME + "/#", USER_ID);

    }


    @Override
    public boolean onCreate() {
        mTweetDatabaseHelper = new TweetDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName;
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case TWEET_LIST:
                tableName = DatabaseContract.Tweet.TABLE_NAME;

                if(TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DatabaseContract.Tweet.TWEET_ID + " DESC";

                }

                cursor = mTweetDatabaseHelper.getReadableDatabase()
                        .query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case USER_LIST:

                tableName = DatabaseContract.User.TABLE_NAME;

                if(TextUtils.isEmpty(sortOrder)) {
                    sortOrder = "_ID DESC";
                }
                cursor = mTweetDatabaseHelper.getReadableDatabase()
                        .query(tableName, projection, selection, selectionArgs,
                                null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TWEET_LIST:
                return DatabaseContract.LIST_CONTENT_TYPE;

            case TWEET_ID:
                return DatabaseContract.SINGLE_CONTENT_TYPE;

            case USER_LIST:
                return DatabaseContract.LIST_CONTENT_TYPE;

            case USER_ID:
                return DatabaseContract.SINGLE_CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("unsupported Uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long insertedRowId = 0;

        switch (sUriMatcher.match(uri)) {
            case TWEET_LIST:
                insertedRowId = mTweetDatabaseHelper.getWritableDatabase().insert(
                        DatabaseContract.Tweet.TABLE_NAME, null, values);
                break;

            case USER_LIST:
                insertedRowId = mTweetDatabaseHelper.getWritableDatabase().insert(
                        DatabaseContract.User.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);
        }

        if(insertedRowId > 0) {
            notifyUriChanges(uri);
            return ContentUris.withAppendedId(uri, insertedRowId);
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int deleteCount;

        switch (sUriMatcher.match(uri)) {
            case TWEET_LIST:
                deleteCount = mTweetDatabaseHelper.getWritableDatabase().delete(
                        DatabaseContract.Tweet.TABLE_NAME, selection, selectionArgs);
                break;

            case USER_LIST:
                deleteCount = mTweetDatabaseHelper.getWritableDatabase().delete(
                        DatabaseContract.User.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);
        }

        if(deleteCount > 0) {
            notifyUriChanges(uri);
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private void notifyUriChanges(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
}
