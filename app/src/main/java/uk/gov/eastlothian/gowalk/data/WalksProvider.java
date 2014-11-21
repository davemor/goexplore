package uk.gov.eastlothian.gowalk.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by davidmorrison on 21/11/14.
 */
public class WalksProvider extends ContentProvider {

    // constants to distinguish different kinds of query
    private static final int ROUTE = 100;               // list of routes
    private static final int ROUTE_ID = 101;            // single route from id
    private static final int AREA = 200;                // list of areas
    private static final int AREA_ID = 201;             // single area from id
    private static final int ROUTES_FOR_AREA = 300;     // list of routes in an area from id
    private static final int AREAS_FOR_ROUTE = 301;     // list of areas route passes though from id
    private static final int WILDLIFE = 400;            // list of wildlife
    private static final int WILDLIFE_ID = 401;         // single wildlife from id
    private static final int WILDLIFE_ON_ROUTE = 500;   // list of wildlife on a specific route
    private static final int ROUTES_FOR_WILDLIFE = 501; // list of the routes that specific wildlife is found on
    private static final int LOG_ENTRY = 600;           // list of log entries
    private static final int LOG_ENTRY_ID = 601;        // single log entry base on id

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WalksContract.CONTENT_AUTHORITY;

        // routes
        matcher.addURI(authority, WalksContract.PATH_ROUTE, ROUTE);
        matcher.addURI(authority, WalksContract.PATH_ROUTE + "/#", ROUTE_ID);
        matcher.addURI(authority, WalksContract.PATH_ROUTE + "/areas", AREAS_FOR_ROUTE);
        matcher.addURI(authority, WalksContract.PATH_ROUTE + "/wildlife", WILDLIFE_ON_ROUTE);

        // areas
        matcher.addURI(authority, WalksContract.PATH_AREA, AREA);
        matcher.addURI(authority, WalksContract.PATH_AREA + "/#", AREA_ID);
        matcher.addURI(authority, WalksContract.PATH_AREA + "/routes", ROUTES_FOR_AREA);

        // wildlife
        matcher.addURI(authority, WalksContract.WILDLIFE, WILDLIFE);
        matcher.addURI(authority, WalksContract.WILDLIFE + "/#", WILDLIFE_ID);
        matcher.addURI(authority, WalksContract.WILDLIFE + "/routes", ROUTES_FOR_WILDLIFE);

        // log
        matcher.addURI(authority, WalksContract.LOG_ENTRY, LOG_ENTRY);
        matcher.addURI(authority, WalksContract.LOG_ENTRY + "/#", LOG_ENTRY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
