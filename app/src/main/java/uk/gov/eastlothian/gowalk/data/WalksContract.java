package uk.gov.eastlothian.gowalk.data;

/**
 * Created by davidmorrison on 19/11/14.
 */

import android.content.ContentUris;
import android.content.res.TypedArray;
import android.net.Uri;
import android.provider.BaseColumns;

import uk.gov.eastlothian.gowalk.R;

/**
 * Defines table and column names for the walks database
 * Each class implements BaseColumns so it gets a _ID field for free.
 * Each class defines a name for a db table and the names of it's columns.
 */
public class WalksContract {

    /**
     * Content provider definitions
     */
    public static final String CONTENT_AUTHORITY = "uk.gov.eastlothian.gowalk";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // paths for content
    public static final String PATH_ROUTE = "route";
    public static final String PATH_AREA = "area";
    public static final String PATH_ROUTE_IN_AREA = "route_in_area";
    public static final String PATH_WILDLIFE = "wildlife";
    public static final String PATH_WILDLIFE_ON_ROUTE = "wildlife_on_route";
    public static final String PATH_LOG_ENTRY = "log_entry";

    /**
     * Geographical Data
     */
    public static final class RouteEntry implements BaseColumns {
        // content provider uris
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUTE).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ROUTE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ROUTE;

        // sql database table
        public static final String TABLE_NAME = "route";
        public static final String COLUMN_ROUTE_NUMBER = "route_number";
        public static final String COLUMN_COORDINATES = "coordinates";
        public static final String COLUMN_PATH_TYPE = "path_type";
        public static final String COLUMN_LENGTH = "length";
        public static final String COLUMN_SURFACE = "surface";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIMARY_AREA = "primary_area";

        // query uris builder helpers
        public static Uri buildRouteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildAreasForRouteUri(long id) {
            return buildRouteUri(id).buildUpon().appendPath("area").build();
        }
        public static Uri buildWildlifeOnRouteUri(long id) {
            return buildRouteUri(id).buildUpon().appendPath("wildlife").build();
        }
        public static String getRouteFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
    public static final class AreaEntry implements BaseColumns {
        // content provider uris
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AREA).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_AREA;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_AREA;

        // sql database table
        public static final String TABLE_NAME = "area";
        public static final String COLUMN_AREA_NAME = "name";

        // query uris builder helpers
        public static Uri buildAreaUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildRoutesInAreaUri(long id) {
            return buildAreaUri(id).buildUpon().appendPath("route").build();
        }
        public static Uri getRoutesForAreas() {
            return CONTENT_URI.buildUpon().appendPath("routes").build();
        }

        public static String getAreaFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
    public static final class RouteInAreaEntry implements BaseColumns {
        // content provider uris
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUTE_IN_AREA).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ROUTE_IN_AREA;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ROUTE_IN_AREA;

        // sql database table
        public static final String TABLE_NAME = "route_in_area";
        public static final String COLUMN_ROUTE_KEY = "route_id";
        public static final String COLUMN_AREA_KEY = "area_id";

        public static Uri buildRouteInAreaUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    /**
     * Wildlife Data
     */
    public static final class WildlifeEntry implements BaseColumns {
        // content provider uris
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WILDLIFE).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WILDLIFE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WILDLIFE;

        // sql database table
        public static final String TABLE_NAME = "wildlife";
        public static final String COLUMN_WILDLIFE_NAME = "name";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE_NAME = "image_name";
        public static final String COLUMN_WHEN_SEEN = "when_seen";

        // query uris builder helpers
        public static Uri buildWildLifeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildRoutesFromWildlifeUri(long id) {
            return buildWildLifeUri(id).buildUpon().appendPath("route").build();
        }
        public static Uri buildLogsForWildlifeUri(long wildlifeId) {
            return ContentUris.withAppendedId(CONTENT_URI, wildlifeId).buildUpon().appendPath("log").build();
        }
        public static String getWildlifeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
    public static final class WildlifeOnRouteEntry implements BaseColumns {
        // content provider uris
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WILDLIFE_ON_ROUTE).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WILDLIFE_ON_ROUTE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WILDLIFE_ON_ROUTE;

        // sql database table
        public static final String TABLE_NAME = "wildlife_on_route";
        public static final String COLUMN_ROUTE_KEY = "route_id";
        public static final String COLUMN_WILDLIFE_KEY = "wildlife_id";

        public static Uri buildWildlifeOnRouteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    /**
     * User Generated Content
     */
    public static final class LogEntry implements BaseColumns {
        // content provider uris
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOG_ENTRY).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOG_ENTRY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOG_ENTRY;

        // sql database table
        public static final String TABLE_NAME = "log_entry";
        public static final String COLUMN_WILDLIFE_KEY = "wildlife_id";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LNG = "lng";
        public static final String COLUMN_DATATIME = "datetime";
        public static final String COLUMN_WEATHER = "weather";
        public static final String COLUMN_IMAGE = "image";

        // query uris builder helpers
        public static Uri buildLogEntrysUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildWildlifeLogsUri() {
            return CONTENT_URI.buildUpon().appendPath("wildlife").build();
        }
    }
}
