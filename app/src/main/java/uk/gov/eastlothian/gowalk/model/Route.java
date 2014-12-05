package uk.gov.eastlothian.gowalk.model;

import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import uk.gov.eastlothian.gowalk.data.WalksContract.RouteEntry;

/**
 * Created by davidmorrison on 05/12/14.
 */
public class Route extends BaseRecord {

    private static final String LOG_TAG = Route.class.getSimpleName();

    private long id;
    private int routeNumber;
    private List<LatLng> coordinates;
    private int length;
    private String surface;
    private String description;

    public Route(long id, int routeNumber, String coordinates,
                 int length, String surface, String description) {
        this.id = id;
        this.routeNumber = routeNumber;
        this.coordinates = convertCoordinates(coordinates);
        this.length = length;
        this.surface = surface;
        this.description = description;
    }

    /*
     * This method will make a list of Routes from a cursor.
     * If the cursor doesn't have a column then it will set a default
     */
    public static List<Route> fromCursor(Cursor cursor) {
        List<Route> rtnList = new ArrayList<Route>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            long id = getLong(cursor, RouteEntry._ID, -1);
            int routeNumber = getInt(cursor, RouteEntry.COLUMN_ROUTE_NUMBER, -1);
            String coordinates = getString(cursor, RouteEntry.COLUMN_COORDINATES, "");
            int length = getInt(cursor, RouteEntry.COLUMN_LENGTH, 0);
            String surface = getString(cursor, RouteEntry.COLUMN_SURFACE, "Unknown");
            String description = getString(cursor, RouteEntry.COLUMN_DESCRIPTION, "");

            Route route = new Route(id, routeNumber, coordinates,
                                    length, surface, description);
            rtnList.add(route);
        }

        return rtnList;
    }

    public long getId() {
        return id;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public int getLength() {
        return length;
    }

    public String getSurface() {
        return surface;
    }

    public String getDescription() {
        return description;
    }

    // private

    private static List<LatLng> convertCoordinates(String coordinates) {
        List<LatLng> rtnList = new ArrayList<LatLng>();
        if(!coordinates.isEmpty()) {
            try {
                JSONArray array = new JSONArray(coordinates).getJSONArray(0);
                for (int idx = 0; idx < array.length(); ++idx) {
                    JSONArray pair = array.getJSONArray(idx);
                    rtnList.add(new LatLng(pair.getDouble(1), pair.getDouble(0)));
                }
            } catch (JSONException e) {
                Log.d(LOG_TAG, "Error parsing route coordinates for route: " + e);
            }
        }
        return rtnList;
    }



}