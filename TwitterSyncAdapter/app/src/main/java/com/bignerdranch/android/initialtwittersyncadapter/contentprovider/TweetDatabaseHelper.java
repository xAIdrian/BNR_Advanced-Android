package com.bignerdranch.android.initialtwittersyncadapter.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.initialtwittersyncadapter.model.DatabaseContract;

/**
 * Created by amohnacs on 4/6/16.
 */
public class TweetDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tweet.sqlite";
    private static final int VERSION = 1;

    public TweetDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " +
                        DatabaseContract.User.TABLE_NAME + " (" +
                        DatabaseContract.User.USER_ID +
                        " integer primary key autoincrement, " +
                        DatabaseContract.User.SERVER_ID + " string, " +
                        DatabaseContract.User.SCREEN_NAME + " string, " +
                        DatabaseContract.User.PHOTO_URL + " string, " +
                        "UNIQUE (" + DatabaseContract.User.SCREEN_NAME +
                        ") ON CONFLICT IGNORE)");

        db.execSQL(
                "create table " +
                        DatabaseContract.Tweet.TABLE_NAME + " (" +
                        DatabaseContract.Tweet.TWEET_ID +
                        " integer primary key autoincrement, " +
                        DatabaseContract.Tweet.SERVER_ID + " string, " +
                        DatabaseContract.Tweet.TEXT + " string, " +
                        DatabaseContract.Tweet.RETWEET_COUNT + " integer, " +
                        DatabaseContract.Tweet.FAVORITE_COUNT + " integer, " +
                        DatabaseContract.Tweet.USER_ID + " integer, " +
                        "UNIQUE (" + DatabaseContract.Tweet.SERVER_ID +
                        ") ON CONFLICT IGNORE, " +
                        "FOREIGN KEY (" + DatabaseContract.Tweet.USER_ID +
                        ") REFERENCES " +
                        DatabaseContract.User.TABLE_NAME + " (" +
                        DatabaseContract.User.USER_ID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
