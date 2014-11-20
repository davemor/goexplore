package uk.gov.eastlothian.gowalk.data;

/**
 * Created by davidmorrison on 19/11/14.
 */

import android.provider.BaseColumns;

/**
 * Defines table and column names for the walks database
 * Each class implements BaseColumns so it gets a _ID field for free.
 * Each class defines a name for a db table and the names of it's columns.
 */
public class WalksContract {
    /**
     * Geographical Data
     */
    public static final class RouteEntry implements BaseColumns {
        public static final String TABLE_NAME = "route";
        public static final String COLUMN_ROUTE_NUMBER = "route_number";
        public static final String COLUMN_COORDINATES = "coordinates";
        public static final String COLUMN_PATH_TYPE = "path_type";
        public static final String COLUMN_LENGTH = "length";
        public static final String COLUMN_SURFACE = "surface";
    }
    public static final class AreaEntry implements BaseColumns {
        public static final String TABLE_NAME = "area";
        public static final String COLUMN_AREA_NAME = "name";
    }
    public static final class RouteInAreaEntry implements BaseColumns {
        public static final String TABLE_NAME = "route_in_area";
        public static final String COLUMN_ROUTE_KEY = "route_id";
        public static final String COLUMN_AREA_KEY = "area_id";
    }
    /**
     * Wildlife Data
     */
    public static final class WildlifeEntry implements BaseColumns {
        public static final String TABLE_NAME = "wildlife";
        public static final String COLUMN_WILDLIFE_NAME = "name";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE_NAME = "image_name";
        public static final String COLUMN_WHEN_SEEN = "when_seen";
    }
    public static final class WildlifeOnRouteEntry implements BaseColumns {
        public static final String TABLE_NAME = "wildlife_on_route";
        public static final String COLUMN_ROUTE_KEY = "route_id";
        public static final String COLUMN_WILDLIFE_KEY = "wildlife_id";
    }
    /**
     * User Generated Content
     */
    public static final class LogEntry implements BaseColumns {
        public static final String TABLE_NAME = "log_entry";
        public static final String COLUMN_WILDLIFE_KEY = "wildlife_id";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LNG = "lng";
        public static final String COLUMN_DATATIME = "datetime";
        public static final String COLUMN_WEATHER = "weather";
        public static final String COLUMN_IMAGE = "image";
    }
}
