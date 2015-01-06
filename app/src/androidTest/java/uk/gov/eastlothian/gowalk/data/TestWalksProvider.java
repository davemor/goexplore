package uk.gov.eastlothian.gowalk.data;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.IOException;

import uk.gov.eastlothian.gowalk.data.WalksContract.AreaEntry;
import uk.gov.eastlothian.gowalk.data.WalksContract.RouteEntry;

/**
 * Created by davidmorrison on 20/11/14.
 */
public class TestWalksProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestWalksProvider.class.getSimpleName();

    public void testDeleteWalksDb() throws Throwable {
        mContext.deleteDatabase(WalksDbHelper.DB_NAME);
    }

    public void testLoadWalksContent() {
        // upload the data
        try {
            WalksDataLoader.loadWalksDatabaseFromFiles(mContext);
        } catch (IOException e) {
            fail("Exception while opening Wildlife.csv. " + e.toString());
        }

        // do some test queries on the data

        // get all the routes
        Cursor routeCursor = mContext.getContentResolver().query(RouteEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue(routeCursor.moveToFirst());
        int count = routeCursor.getCount();
        // printCursor(routeCursor);
        // assertTrue(count == 326);

        // get all the areas
        Cursor areasCursor = mContext.getContentResolver().query(AreaEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue(areasCursor.moveToFirst());
        int areaCount = areasCursor.getCount();
        printCursor(areasCursor);


        // get the routes for an area
        areasCursor.moveToFirst();
        do {
            int idIndex = areasCursor.getColumnIndex(AreaEntry._ID);
            long id = areasCursor.getLong(idIndex);
            int nameIndex = areasCursor.getColumnIndex(AreaEntry.COLUMN_AREA_NAME);
            String areaName = areasCursor.getString(nameIndex);
            // query the routes for the area
            Cursor routesInArea = mContext.getContentResolver().query(
                    AreaEntry.buildRoutesInAreaUri(id),
                    null, null, null, null);
            // Log.d(LOG_TAG, "Routes in area id: " + id + ", name: " + areaName);
            printCursor(routesInArea);
            routesInArea.close();
        } while (areasCursor.moveToNext());
    }

    /*
    public void testDeleteWalksDb() throws Throwable {
        mContext.deleteDatabase(WalksDbHelper.DB_NAME);
    }

    public void testGetType() {
        expectMimeType(RouteEntry.CONTENT_URI, RouteEntry.CONTENT_TYPE);
        expectMimeType(RouteEntry.buildRouteUri(1234), RouteEntry.CONTENT_ITEM_TYPE);
        expectMimeType(RouteEntry.buildAreasForRouteUri(4321), AreaEntry.CONTENT_TYPE);
        expectMimeType(RouteEntry.buildWildlifeOnRouteUri(5678), WildlifeEntry.CONTENT_TYPE);

        expectMimeType(AreaEntry.CONTENT_URI, AreaEntry.CONTENT_TYPE);
        expectMimeType(AreaEntry.buildAreaUri(9012), AreaEntry.CONTENT_ITEM_TYPE);
        expectMimeType(AreaEntry.buildRoutesInAreaUri(3456), RouteEntry.CONTENT_TYPE);

        expectMimeType(WildlifeEntry.CONTENT_URI, WildlifeEntry.CONTENT_TYPE);
        expectMimeType(WildlifeEntry.buildWildLifeUri(7890), WildlifeEntry.CONTENT_ITEM_TYPE);
        expectMimeType(WildlifeEntry.buildRoutesFromWildlifeUri(4567), RouteEntry.CONTENT_TYPE);

        expectMimeType(LogEntry.CONTENT_URI, LogEntry.CONTENT_TYPE);
        expectMimeType(LogEntry.buildLogEntrysUri(1234), LogEntry.CONTENT_ITEM_TYPE);
    }

    private void expectMimeType(Uri uri, String expected) {
        String type = mContext.getContentResolver().getType(uri);
        assertEquals(expected, type);
    }

    public void testInsertReadProvider() {
        // test route
        ContentValues routeValues = TestWalksDb.createSimpleRouteValues();
        Uri routeUri = mContext.getContentResolver().insert(RouteEntry.CONTENT_URI, routeValues);
        long routeId = ContentUris.parseId(routeUri);
        Cursor routeCursor = mContext.getContentResolver().query(
                RouteEntry.CONTENT_URI,
                null, null, null, null);
        TestWalksDb.validateCursor(routeCursor, routeValues);

        // test route with id
        Cursor singleRouteCursor = mContext.getContentResolver().query(
                RouteEntry.buildRouteUri(routeId),
                null, null, null, null);
        TestWalksDb.validateCursor(singleRouteCursor, routeValues);

        // test area
        ContentValues areaValues = TestWalksDb.createSimpleAreaValues();
        Uri areaUri = mContext.getContentResolver().insert(AreaEntry.CONTENT_URI, areaValues);
        long areaId = ContentUris.parseId(areaUri);
        Cursor areaCursor = mContext.getContentResolver().query(
                AreaEntry.CONTENT_URI,
                null, null, null, null);
        TestWalksDb.validateCursor(areaCursor, areaValues);

        // test area with id
        Cursor areaFromIdCursor = mContext.getContentResolver().query(
                AreaEntry.buildAreaUri(areaId),
                null, null, null, null);
        TestWalksDb.validateCursor(areaFromIdCursor, areaValues);

        // test areas the the route crosses
        ContentValues routeInAreaValues = TestWalksDb.createSimpleRoutesInAreasValues(routeId, areaId);
        Uri routeInAreaUri = mContext.getContentResolver().insert(RouteInAreaEntry.CONTENT_URI, routeInAreaValues);
        Cursor areasForRouteCursor = mContext.getContentResolver().query(
            RouteEntry.buildAreasForRouteUri(routeId),
            null, null, null, null);
        TestWalksDb.validateCursor(areasForRouteCursor, areaValues);

        // test routes that cross an area
        Cursor routesForAreaCursor = mContext.getContentResolver().query(
            AreaEntry.buildRoutesInAreaUri(areaId),
            null, null, null, null);
        TestWalksDb.validateCursor(routesForAreaCursor, routeValues);

        // test wildlife
        ContentValues wildlifeValues = TestWalksDb.createSimpleWildlifeValues();
        Uri wildlifeUri = mContext.getContentResolver().insert(WildlifeEntry.CONTENT_URI, wildlifeValues);
        long wildlifeId = ContentUris.parseId(wildlifeUri);
        Cursor wildlifeCursor = mContext.getContentResolver().query(
            WildlifeEntry.CONTENT_URI,
            null, null, null, null);
        TestWalksDb.validateCursor(wildlifeCursor, wildlifeValues);

        // test wildlife with id
        Cursor wildlifeFromIdCursor = mContext.getContentResolver().query(
            WildlifeEntry.buildWildLifeUri(wildlifeId),
            null, null, null, null);
        TestWalksDb.validateCursor(wildlifeFromIdCursor, wildlifeValues);

        // test wildlife on the route
        ContentValues wildlifeOnRouteValues = TestWalksDb.createSimpleWildlifeOnRoute(routeId, wildlifeId);
        Uri wildlifeOnRouteUri = mContext.getContentResolver().insert(WildlifeOnRouteEntry.CONTENT_URI, wildlifeOnRouteValues);
        Cursor wildlifeOnRouteCursor = mContext.getContentResolver().query(
            RouteEntry.buildWildlifeOnRouteUri(routeId),
            null, null, null, null);
        TestWalksDb.validateCursor(wildlifeOnRouteCursor, wildlifeValues);

        // test routes for wildlife
        Cursor routesForWildLifeCursor = mContext.getContentResolver().query(
            WildlifeEntry.buildRoutesFromWildlifeUri(wildlifeId),
            null, null, null, null);
        TestWalksDb.validateCursor(routesForWildLifeCursor, routeValues);

        // test log entry
        ContentValues logEntryValues = TestWalksDb.createSimpleLogEntryValues(wildlifeId);
        Uri logEntryUri = getContext().getContentResolver().insert(LogEntry.CONTENT_URI, logEntryValues);
        long logEntryId = ContentUris.parseId(logEntryUri);
        Cursor logEntryCursor = mContext.getContentResolver().query(
            LogEntry.CONTENT_URI,
            null, null, null, null
        );
        TestWalksDb.validateCursor(logEntryCursor, logEntryValues);

        // test log entry with id
        Cursor logEntryFromIdCursor = mContext.getContentResolver().query(
            LogEntry.buildLogEntrysUri(logEntryId),
            null, null, null, null);
        TestWalksDb.validateCursor(logEntryFromIdCursor, logEntryValues);
    }
*/
    private void printCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                StringBuilder sb = new StringBuilder();
                int columnsQty = cursor.getColumnCount();
                for (int idx = 0; idx < columnsQty; ++idx) {
                    sb.append(cursor.getString(idx));
                    if (idx < columnsQty - 1)
                        sb.append("; ");
                }
                // Log.d(LOG_TAG, String.format("Row: %d, Values: %s", cursor.getPosition(), sb.toString()));
            } while (cursor.moveToNext()) ;
        }
    }

}