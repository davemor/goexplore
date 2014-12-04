package uk.gov.eastlothian.gowalk.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import uk.gov.eastlothian.gowalk.data.WalksContract.RouteEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.AreaEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.RouteInAreaEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.WildlifeEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.WildlifeOnRouteEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.LogEntry;

/**
 * Created by davidmorrison on 20/11/14.
 */
public class TestWalksDb extends AndroidTestCase {
    public static final String LOG_TAG = TestWalksDb.class.getSimpleName();

    /*

    public void testCreateWalksDb() throws Throwable {
        mContext.deleteDatabase(WalksDbHelper.DB_NAME);
        SQLiteDatabase db = new WalksDbHelper(this.mContext)
                                    .getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        // get a writable database
        WalksDbHelper dbHelper = new WalksDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // for each table in the database insert and then read a test row
        long routeId = insertRead(db, RouteEntry.TABLE_NAME, createSimpleRouteValues());
        long areaId = insertRead(db, AreaEntry.TABLE_NAME, createSimpleAreaValues());
        insertRead(db, RouteInAreaEntry.TABLE_NAME,
                createSimpleRoutesInAreasValues(routeId, areaId));
        long wildlifeId = insertRead(db, WildlifeEntry.TABLE_NAME, createSimpleWildlifeValues());
        insertRead(db, WildlifeOnRouteEntry.TABLE_NAME,
                createSimpleWildlifeOnRoute(routeId, wildlifeId));
        insertRead(db, LogEntry.TABLE_NAME, createSimpleLogEntryValues(wildlifeId));

        db.close();
    }

    //////////////////////////////
    // Some helper functions
    //////////////////////////////

    // insert a set of content values into a database and validate the results
    static long insertRead(SQLiteDatabase db, String tableName, ContentValues values) {
        long rowId = db.insert(tableName, null, values);
        assertTrue("Failed to insert row into " + tableName, rowId != -1);
        Log.d(LOG_TAG, "New row in " + tableName + ". id: " + rowId);
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        validateCursor(cursor, values);
        return rowId;
    }

    // validate a cursor against expected content values
    public static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Key: " + columnName + " not found in db.", idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    // generate test data as content values
    public static ContentValues createSimpleRouteValues() {
        ContentValues values = new ContentValues();
        values.put(RouteEntry.COLUMN_ROUTE_NUMBER, 42);
        values.put(RouteEntry.COLUMN_COORDINATES, "[[-2.958,55.950],[-2.958,55.949]]");
        values.put(RouteEntry.COLUMN_PATH_TYPE, "Existing Path");
        values.put(RouteEntry.COLUMN_LENGTH, 706);
        values.put(RouteEntry.COLUMN_SURFACE, "Tarmac/Gravel");
        values.put(RouteEntry.COLUMN_DESCRIPTION, "Part of Haddington’s local path network.");
        return values;
    }
    public static ContentValues createSimpleAreaValues() {
        ContentValues values = new ContentValues();
        values.put(AreaEntry.COLUMN_AREA_NAME, "Gifford and surrounding area");
        return values;
    }
    public static ContentValues createSimpleRoutesInAreasValues(long routeId, long areaId) {
        ContentValues values = new ContentValues();
        values.put(RouteInAreaEntry.COLUMN_ROUTE_KEY, routeId);
        values.put(RouteInAreaEntry.COLUMN_AREA_KEY, areaId);
        return values;
    }
    public static ContentValues createSimpleWildlifeValues() {
        ContentValues values = new ContentValues();
        values.put(WildlifeEntry.COLUMN_WILDLIFE_NAME, "kingfisher");
        values.put(WildlifeEntry.COLUMN_CATEGORY, "birds");
        values.put(WildlifeEntry.COLUMN_DESCRIPTION, "brilliantly coloured");
        values.put(WildlifeEntry.COLUMN_IMAGE_NAME, "kingfisher.jpg");
        values.put(WildlifeEntry.COLUMN_WHEN_SEEN, "all year possible");
        return values;
    }
    public static ContentValues createSimpleWildlifeOnRoute(long routeId, long wildlifeId) {
        ContentValues values = new ContentValues();
        values.put(WildlifeOnRouteEntry.COLUMN_ROUTE_KEY, routeId);
        values.put(WildlifeOnRouteEntry.COLUMN_WILDLIFE_KEY, wildlifeId);
        return values;
    }
    public static ContentValues createSimpleLogEntryValues(long wildlifeId) {
        ContentValues values = new ContentValues();
        values.put(LogEntry.COLUMN_WILDLIFE_KEY, wildlifeId);
        values.put(LogEntry.COLUMN_LAT, "56.0087819");
        values.put(LogEntry.COLUMN_LNG, "-2.7517742");
        values.put(LogEntry.COLUMN_DATATIME, "2014-11-21 13:25:28");
        values.put(LogEntry.COLUMN_WEATHER, "Sunny");
        values.put(LogEntry.COLUMN_IMAGE, "myphoto");
        return values;
    }

*/
}