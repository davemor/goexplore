package uk.gov.eastlothian.gowalk.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by davidmorrison on 21/11/14.
 */
public class WalksProvider extends ContentProvider {

    // constants to distinguish different kinds of query
    private static final int ROUTE = 100;                   // list of routes
    private static final int ROUTE_ID = 101;                // single route from id
    private static final int AREA = 200;                    // list of areas
    private static final int AREA_ID = 201;                 // single area from id
    private static final int ROUTE_IN_AREA = 300;           // list of rows in junction table route_in_area
    private static final int ROUTE_IN_AREA_ID = 301;        // single row of route_in_area table
    private static final int ROUTES_FOR_AREA = 302;         // list of routes in an area from id
    private static final int AREAS_FOR_ROUTE = 303;         // list of areas route passes though from id
    private static final int WILDLIFE = 400;                // list of wildlife
    private static final int WILDLIFE_ID = 401;             // single wildlife from id
    private static final int WILDLIFE_ON_ROUTE = 500;       // list of rows in junction table wildlife_on_route
    private static final int WILDLIFE_ON_ROUTE_ID = 501;    // single row of wildlife_on_route from id
    private static final int WILDLIFE_FOR_ROUTE = 502;      // list of wildlife on a specific route
    private static final int ROUTES_FOR_WILDLIFE = 503;     // list of the routes that specific wildlife is found on
    private static final int LOG_ENTRY = 600;               // list of log entries
    private static final int LOG_ENTRY_ID = 601;            // single log entry base on id

    private WalksDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /*
    private static final SQLiteQueryBuilder sAreasForRouteQueryBuilder;

    static {
        sAreasForRouteQueryBuilder = new SQLiteQueryBuilder();
        sAreasForRouteQueryBuilder.setTables(
                WalksContract.RouteEntry.TABLE_NAME + " INNER JOIN"

        );
    }
    */

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WalksContract.CONTENT_AUTHORITY;

        // routes
        matcher.addURI(authority, WalksContract.PATH_ROUTE, ROUTE);
        matcher.addURI(authority, WalksContract.PATH_ROUTE + "/#", ROUTE_ID);
        matcher.addURI(authority, WalksContract.PATH_ROUTE + "/#/area", AREAS_FOR_ROUTE);
        matcher.addURI(authority, WalksContract.PATH_ROUTE + "/#/wildlife", WILDLIFE_FOR_ROUTE);


        // areas
        matcher.addURI(authority, WalksContract.PATH_AREA, AREA);
        matcher.addURI(authority, WalksContract.PATH_AREA + "/#", AREA_ID);
        matcher.addURI(authority, WalksContract.PATH_AREA + "/#/route", ROUTES_FOR_AREA);

        // route_in_area junction table
        matcher.addURI(authority, WalksContract.PATH_ROUTE_IN_AREA, ROUTE_IN_AREA);
        matcher.addURI(authority, WalksContract.PATH_ROUTE_IN_AREA + "/#", ROUTE_IN_AREA_ID);

        // wildlife
        matcher.addURI(authority, WalksContract.PATH_WILDLIFE, WILDLIFE);
        matcher.addURI(authority, WalksContract.PATH_WILDLIFE + "/#", WILDLIFE_ID);
        matcher.addURI(authority, WalksContract.PATH_WILDLIFE + "/#/route", ROUTES_FOR_WILDLIFE);

        // wildlife_on_route junction table
        matcher.addURI(authority, WalksContract.PATH_WILDLIFE_ON_ROUTE, WILDLIFE_ON_ROUTE);
        matcher.addURI(authority, WalksContract.PATH_WILDLIFE_ON_ROUTE + "/#", WILDLIFE_ON_ROUTE_ID);

        // log
        matcher.addURI(authority, WalksContract.PATH_LOG_ENTRY, LOG_ENTRY);
        matcher.addURI(authority, WalksContract.PATH_LOG_ENTRY + "/#", LOG_ENTRY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WalksDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor rtnCursor;
        switch (sUriMatcher.match(uri))
        {
            case ROUTE:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.RouteEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            case ROUTE_ID:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.RouteEntry.TABLE_NAME,
                    projection,
                    WalksContract.RouteEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                    null,
                    null,
                    null,
                    sortOrder);
                break;
            case AREAS_FOR_ROUTE: {
                // TODO: refactor so this uses the
                String [] subs = new String [] { WalksContract.RouteEntry.getRouteFromUri(uri) };
                String query = "SELECT "      + WalksContract.AreaEntry.TABLE_NAME +
                                          "." + WalksContract.AreaEntry._ID + " ,"
                                              + WalksContract.AreaEntry.COLUMN_AREA_NAME +
                               " FROM "       + WalksContract.AreaEntry.TABLE_NAME +
                               " INNER JOIN " + WalksContract.RouteInAreaEntry.TABLE_NAME +
                               " ON "         + WalksContract.RouteInAreaEntry.TABLE_NAME +
                                          "." + WalksContract.RouteInAreaEntry.COLUMN_AREA_KEY +
                                          "=" + WalksContract.AreaEntry.TABLE_NAME +
                                          "." + WalksContract.AreaEntry._ID +
                               " WHERE "      + WalksContract.RouteInAreaEntry.TABLE_NAME +
                                          "." + WalksContract.RouteInAreaEntry.COLUMN_ROUTE_KEY +
                                          "=?;";
                rtnCursor = mOpenHelper.getReadableDatabase().rawQuery(query, subs);
            }
            break;
            case WILDLIFE_FOR_ROUTE: {
                String [] subs = new String [] { WalksContract.RouteEntry.getRouteFromUri(uri) };
                // TODO: refactor the query to use the WalksContract
                String query = "SELECT wildlife._ID, wildlife.name, " +
                    "wildlife.category, " +
                    "wildlife.description, " +
                    "wildlife.image_name, " +
                    "wildlife.when_seen " +
                    "FROM wildlife " +
                    "INNER JOIN wildlife_on_route " +
                    "ON wildlife._ID " +
                    "= wildlife_on_route._ID " +
                    "WHERE wildlife_on_route.route_id = ?;";
                rtnCursor = mOpenHelper.getReadableDatabase().rawQuery(query, subs);
            }
                break;
            case AREA:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.AreaEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            case AREA_ID:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.AreaEntry.TABLE_NAME,
                    projection,
                    WalksContract.AreaEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                    null,
                    null,
                    null,
                    sortOrder);
                break;
            case ROUTES_FOR_AREA: {
                String [] subs = new String [] { WalksContract.AreaEntry.getAreaFromUri(uri) };
                String query = "SELECT route._ID, " +
                    "route.route_number, " +
                    "route.coordinates, " +
                    "route.path_type, " +
                    "route.length, " +
                    "route.surface, " +
                    "route.description " +
                    "FROM route " +
                    "INNER JOIN route_in_area " +
                    "ON route_in_area.route_id = route._ID " +
                    "WHERE route_in_area.area_id = ?;";
                rtnCursor = mOpenHelper.getReadableDatabase().rawQuery(query, subs);
                }
                break;
            case ROUTE_IN_AREA:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.RouteInAreaEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            case ROUTE_IN_AREA_ID:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.RouteInAreaEntry.TABLE_NAME,
                    projection,
                    WalksContract.RouteInAreaEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                    null,
                    null,
                    null,
                    sortOrder);
                break;
            case WILDLIFE:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.WildlifeEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            case WILDLIFE_ID:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.WildlifeEntry.TABLE_NAME,
                    projection,
                    WalksContract.WildlifeEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                    null,
                    null,
                    null,
                    sortOrder);
                break;
            case ROUTES_FOR_WILDLIFE: {
                String [] subs = new String [] {
                        WalksContract.WildlifeEntry.getWildlifeFromUri(uri)
                };
                String query = "SELECT route._ID, " +
                    "route.route_number, " +
                    "route.coordinates, " +
                    "route.path_type, " +
                    "route.length, " +
                    "route.surface, " +
                    "route.description " +
                    "FROM route " +
                    "INNER JOIN wildlife_on_route " +
                    "ON wildlife_on_route.route_id = route._ID " +
                    "WHERE wildlife_on_route.wildlife_id = ?";
                rtnCursor = mOpenHelper.getReadableDatabase().rawQuery(query, subs);
            }
            break;
            case WILDLIFE_ON_ROUTE:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.WildlifeOnRouteEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            case WILDLIFE_ON_ROUTE_ID:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.WildlifeOnRouteEntry.TABLE_NAME,
                    projection,
                    WalksContract.WildlifeOnRouteEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                    null,
                    null,
                    null,
                    sortOrder);
                break;
            case LOG_ENTRY:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.LogEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            case LOG_ENTRY_ID:
                rtnCursor = mOpenHelper.getReadableDatabase().query(
                    WalksContract.LogEntry.TABLE_NAME,
                    projection,
                    WalksContract.LogEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                    null,
                    null,
                    null,
                    sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        rtnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return rtnCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        String mimeType = "";
        switch(match) {
            case ROUTE:
                mimeType = WalksContract.RouteEntry.CONTENT_TYPE;
                break;
            case ROUTE_ID:
                mimeType = WalksContract.RouteEntry.CONTENT_ITEM_TYPE;
                break;
            case AREAS_FOR_ROUTE:
                mimeType = WalksContract.AreaEntry.CONTENT_TYPE;
                break;
            case WILDLIFE_FOR_ROUTE:
                mimeType = WalksContract.WildlifeEntry.CONTENT_TYPE;
                break;
            case AREA:
                mimeType = WalksContract.AreaEntry.CONTENT_TYPE;
                break;
            case AREA_ID:
                mimeType = WalksContract.AreaEntry.CONTENT_ITEM_TYPE;
                break;
            case ROUTES_FOR_AREA:
                mimeType = WalksContract.RouteEntry.CONTENT_TYPE;
                break;
            case WILDLIFE:
                mimeType = WalksContract.WildlifeEntry.CONTENT_TYPE;
                break;
            case WILDLIFE_ID:
                mimeType = WalksContract.WildlifeEntry.CONTENT_ITEM_TYPE;
                break;
            case ROUTES_FOR_WILDLIFE:
                mimeType = WalksContract.RouteEntry.CONTENT_TYPE;
                break;
            case LOG_ENTRY:
                mimeType = WalksContract.LogEntry.CONTENT_TYPE;
                break;
            case LOG_ENTRY_ID:
                mimeType = WalksContract.LogEntry.CONTENT_ITEM_TYPE;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri rtnUri;
        switch (match) {
            case ROUTE: {
                long id = db.insert(WalksContract.RouteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    rtnUri = WalksContract.RouteEntry.buildRouteUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            } break;
            case ROUTE_IN_AREA: {
                long id = db.insert(WalksContract.RouteInAreaEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    rtnUri = WalksContract.RouteInAreaEntry.buildRouteInAreaUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            } break;
            case AREA: {
                long id = db.insert(WalksContract.AreaEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    rtnUri = WalksContract.AreaEntry.buildAreaUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            } break;
            case WILDLIFE: {
                long id = db.insert(WalksContract.WildlifeEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    rtnUri = WalksContract.WildlifeEntry.buildWildLifeUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            } break;
            case WILDLIFE_ON_ROUTE: {
                long id = db.insert(WalksContract.WildlifeOnRouteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    rtnUri = WalksContract.WildlifeOnRouteEntry.buildWildlifeOnRouteUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            } break;
            case LOG_ENTRY: {
                long id = db.insert(WalksContract.LogEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    rtnUri = WalksContract.LogEntry.buildLogEntrysUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            } break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rtnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ROUTE:
                rowsDeleted = db.delete(WalksContract.RouteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case AREA:
                rowsDeleted = db.delete(WalksContract.AreaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ROUTE_IN_AREA:
                rowsDeleted = db.delete(WalksContract.RouteInAreaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WILDLIFE:
                rowsDeleted = db.delete(WalksContract.WildlifeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WILDLIFE_ON_ROUTE:
                rowsDeleted = db.delete(WalksContract.WildlifeOnRouteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOG_ENTRY:
                rowsDeleted = db.delete(WalksContract.LogEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case ROUTE:
                rowsUpdated = db.update(WalksContract.RouteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AREA:
                rowsUpdated = db.update(WalksContract.AreaEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_IN_AREA:
                rowsUpdated = db.update(WalksContract.RouteInAreaEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case WILDLIFE:
                rowsUpdated = db.update(WalksContract.WildlifeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case WILDLIFE_ON_ROUTE:
                rowsUpdated = db.update(WalksContract.WildlifeOnRouteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LOG_ENTRY:
                rowsUpdated = db.update(WalksContract.LogEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        // TODO: override bulktransactions for when we are parsing json
        return super.bulkInsert(uri, values);
    }
}
