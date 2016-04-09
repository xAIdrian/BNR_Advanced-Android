package com.bignerdranch.android.nerdmail.model;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.nerdmailservice.Email;

public class EmailCursorWrapper extends CursorWrapper {

    public EmailCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Email getEmail() {
        int id = getInt(getColumnIndex(EmailDatabaseHelper.ID_COLUMN));
        String sender = getString(getColumnIndex(EmailDatabaseHelper.SENDER_COLUMN));
        String subject =
                getString(getColumnIndex(EmailDatabaseHelper.SUBJECT_COLUMN));
        String body = getString(getColumnIndex(EmailDatabaseHelper.BODY_COLUMN));

        int importantInt =
                getInt(getColumnIndex(EmailDatabaseHelper.IMPORTANT_COLUMN));
        boolean important = importantInt == 1;

        int spamInt = getInt(getColumnIndex(EmailDatabaseHelper.SPAM_COLUMN));
        boolean spam = spamInt == 1;

        int notifiedInt = getInt(getColumnIndex(EmailDatabaseHelper.NOTIFIED_COLUMN));
        boolean notified = notifiedInt == 1;

        return new Email(id, sender, subject, body, important, spam, notified);
    }
}
