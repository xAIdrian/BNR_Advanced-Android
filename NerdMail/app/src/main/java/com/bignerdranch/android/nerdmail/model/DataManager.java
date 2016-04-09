package com.bignerdranch.android.nerdmail.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bignerdranch.android.nerdmailservice.Email;
import com.bignerdranch.android.nerdmailservice.NerdMailService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DataManager {
    private static final String TAG = "DataManager";
    private static final String FETCHED_EMAILS_KEY = "DataManager.FetchedEmails";
    private static DataManager sDataManager;

    private Context mContext;
    private EmailDatabaseHelper mEmailDatabaseHelper;
    private NerdMailService mNerdMailService;

    public static DataManager get(Context context) {
        if (sDataManager == null) {
            sDataManager = new DataManager(context);
        }
        return sDataManager;
    }

    private DataManager(Context context) {
        mContext = context;
        mEmailDatabaseHelper = new EmailDatabaseHelper(mContext);
        mNerdMailService = new NerdMailService();
    }

    public Observable<Email> getEmails() {
        return Observable.create(new Observable.OnSubscribe<Email>() {
            @Override
            public void call(Subscriber<? super Email> subscriber) {
                if (!fetchedEmails()) {
                    mNerdMailService.fetchEmails()
                            .subscribe(DataManager.this::insertEmail,
                                    throwable -> {},
                                    DataManager.this::setFetchedEmails);
                }

                Cursor emailCursor = mEmailDatabaseHelper.getReadableDatabase()
                        .query(EmailDatabaseHelper.TABLE_NAME, null, null, null, null,
                                null, EmailDatabaseHelper.ID_COLUMN + " DESC");
                EmailCursorWrapper emailCursorWrapper =
                        new EmailCursorWrapper(emailCursor);

                try {
                    emailCursorWrapper.moveToFirst();
                    while (!emailCursorWrapper.isAfterLast()) {
                        subscriber.onNext(emailCursorWrapper.getEmail());
                        emailCursorWrapper.moveToNext();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Got exception", e);
                } finally {
                    emailCursor.close();
                    emailCursorWrapper.close();
                }
                subscriber.onCompleted();
            }
        })
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Email>> getNotificationEmails() {
        return Observable.create(new Observable.OnSubscribe<List<Email>>() {
            @Override
            public void call(Subscriber<? super List<Email>> subscriber) {
                String[] notifiedValue = new String[1];
                notifiedValue[0] = "0";
                Cursor emailCursor = mEmailDatabaseHelper.getReadableDatabase()
                        .query(EmailDatabaseHelper.TABLE_NAME, null,
                                "notified = ? AND spam = 0", notifiedValue,
                                null, null, null);
                EmailCursorWrapper emailCursorWrapper =
                        new EmailCursorWrapper(emailCursor);

                List<Email> emails = new ArrayList<Email>();
                try {
                    emailCursorWrapper.moveToFirst();
                    while (!emailCursorWrapper.isAfterLast()) {
                        emails.add(emailCursorWrapper.getEmail());
                        emailCursorWrapper.moveToNext();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Got exception", e);
                } finally {
                    emailCursor.close();
                    emailCursorWrapper.close();
                }
                subscriber.onNext(emails);
                subscriber.onCompleted();
            }
        })
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void markEmailsAsNotified() {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                Log.d(TAG, "Mark emails as notified");
                String[] notifiedValue = new String[1];
                notifiedValue[0] = "0";
                Cursor emailCursor = mEmailDatabaseHelper.getReadableDatabase()
                        .query(EmailDatabaseHelper.TABLE_NAME, null,
                                "notified = ? AND spam = 0", notifiedValue,
                                null, null, null);
                EmailCursorWrapper emailCursorWrapper =
                        new EmailCursorWrapper(emailCursor);

                List<Email> emails = new ArrayList<>();
                try {
                    emailCursorWrapper.moveToFirst();
                    while (!emailCursorWrapper.isAfterLast()) {
                        emails.add(emailCursorWrapper.getEmail());
                        emailCursorWrapper.moveToNext();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Got exception", e);
                } finally {
                    emailCursor.close();
                    emailCursorWrapper.close();
                }
                for (Email email : emails) {
                    email.setNotified(true);
                    updateEmail(email);
                }
            }
        })
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void insertEmail(Email email) {
        ContentValues contentValues = getEmailContentValues(email);
        mEmailDatabaseHelper.getWritableDatabase()
                .insert(EmailDatabaseHelper.TABLE_NAME,
                        null, contentValues);
    }

    public void updateEmail(Email email) {
        ContentValues contentValues = getEmailContentValues(email);
        String[] emailId = new String[1];
        emailId[0] = "" + email.getId();
        mEmailDatabaseHelper.getWritableDatabase()
                .update(EmailDatabaseHelper.TABLE_NAME, contentValues,
                        "_id = ?", emailId);
    }

    public void startEmailNotifications(Context context) {
        mNerdMailService.startNotifications(context);
    }

    public void stopEmailNotifications() {
        mNerdMailService.stopNotifications();
    }

    private boolean fetchedEmails() {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(FETCHED_EMAILS_KEY, false);
    }

    private void setFetchedEmails() {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putBoolean(FETCHED_EMAILS_KEY, true)
                .apply();
    }

    private static ContentValues getEmailContentValues(Email email) {
        ContentValues cv = new ContentValues();
        cv.put(EmailDatabaseHelper.SENDER_COLUMN, email.getSenderAddress());
        cv.put(EmailDatabaseHelper.SUBJECT_COLUMN, email.getSubject());
        cv.put(EmailDatabaseHelper.BODY_COLUMN, email.getBody());
        cv.put(EmailDatabaseHelper.IMPORTANT_COLUMN, email.isImportant() ? 1 : 0);
        cv.put(EmailDatabaseHelper.SPAM_COLUMN, email.isSpam() ? 1 : 0);
        cv.put(EmailDatabaseHelper.NOTIFIED_COLUMN, email.isNotified() ? 1 : 0);

        return cv;
    }
}
