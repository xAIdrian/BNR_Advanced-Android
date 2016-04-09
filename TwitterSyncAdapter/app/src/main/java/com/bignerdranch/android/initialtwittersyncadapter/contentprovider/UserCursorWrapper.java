package com.bignerdranch.android.initialtwittersyncadapter.contentprovider;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.initialtwittersyncadapter.model.DatabaseContract;
import com.bignerdranch.android.initialtwittersyncadapter.model.User;

/**
 * Created by amohnacs on 4/6/16.
 */
public class UserCursorWrapper extends CursorWrapper {


    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getuser() {

        String serverId = getString(getColumnIndex(DatabaseContract.User.SERVER_ID));
        String screenName = getString(getColumnIndex(DatabaseContract.User.SCREEN_NAME));
        String photoUrl = getString(getColumnIndex(DatabaseContract.User.PHOTO_URL));

        return new User(serverId, screenName, photoUrl);
    }
}
