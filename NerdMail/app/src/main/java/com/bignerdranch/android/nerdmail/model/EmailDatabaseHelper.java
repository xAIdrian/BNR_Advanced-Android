package com.bignerdranch.android.nerdmail.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EmailDatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "email_database.sqlite";

    public static final String TABLE_NAME = "EMAIL";
    public static final String ID_COLUMN = "_id";
    public static final String SENDER_COLUMN = "sender";
    public static final String SUBJECT_COLUMN = "subject";
    public static final String BODY_COLUMN = "body";
    public static final String IMPORTANT_COLUMN = "important";
    public static final String SPAM_COLUMN = "spam";
    public static final String NOTIFIED_COLUMN = "notified";

    public EmailDatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                ID_COLUMN + " INTEGER  PRIMARY KEY," +
                SENDER_COLUMN + " TEXT," +
                SUBJECT_COLUMN + " TEXT," +
                BODY_COLUMN + " TEXT," +
                IMPORTANT_COLUMN + " INTEGER DEFAULT 0," +
                SPAM_COLUMN + " INTEGER DEFAULT 0, " +
                NOTIFIED_COLUMN + " INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
