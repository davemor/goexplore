package uk.gov.eastlothian.gowalk.model;

import android.database.Cursor;

/**
 * Root class of all our model objects
 * Created by davidmorrison on 05/12/14.
 */
public class BaseRecord {

    protected static long getLong(Cursor cursor, String name, long defaultValue) {
        int index = cursor.getColumnIndex(name);
        if(index == -1) return defaultValue;
        return cursor.getLong(index);
    }

    protected static int getInt(Cursor cursor, String name, int defaultValue) {
        int index = cursor.getColumnIndex(name);
        if(index == -1) return defaultValue;
        return cursor.getInt(index);
    }

    protected static String getString(Cursor cursor, String name, String defaultValue) {
        int index = cursor.getColumnIndex(name);
        if(index == -1) return defaultValue;
        return cursor.getString(index);
    }

}
