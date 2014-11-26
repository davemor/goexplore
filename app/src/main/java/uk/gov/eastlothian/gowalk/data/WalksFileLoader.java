package uk.gov.eastlothian.gowalk.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class can be used to load the various part of the data model
 * into the database.
 *
 * Created by davidmorrison on 26/11/14.
 */
public class WalksFileLoader {
    public static final String LOG_TAG = WalksFileLoader.class.getSimpleName();

    static void insertRoutesIntoWalksDatabase(String routesJsonStr, Context context)
    {
        List<ContentValues> valuesList = new ArrayList<ContentValues>();

        // parse the json in a list of content values for a row in the routes table
        try {
            JSONObject routesJson = new JSONObject(routesJsonStr);
            JSONArray paths = routesJson.getJSONArray("features");
            for (int idx=0; idx < paths.length(); ++idx) {
                JSONObject path = paths.getJSONObject(idx);
                JSONObject properties = path.getJSONObject("properties");
                JSONArray coordinatesArray = path.getJSONObject("geometry")
                                                 .getJSONArray("coordinates");

                int routeNumber = properties.getInt("route_no");
                String coordinates = coordinatesArray.toString();
                String pathType = properties.getString("path_type");
                int length = properties.getInt("length");
                String surface = properties.getString("surface");

                ContentValues values = new ContentValues();
                values.put(WalksContract.RouteEntry.COLUMN_ROUTE_NUMBER, routeNumber);
                values.put(WalksContract.RouteEntry.COLUMN_COORDINATES, coordinates);
                values.put(WalksContract.RouteEntry.COLUMN_PATH_TYPE, pathType);
                values.put(WalksContract.RouteEntry.COLUMN_LENGTH, length);
                values.put(WalksContract.RouteEntry.COLUMN_SURFACE, surface);
                valuesList.add(values);
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "There was an error parsing the core path json.", e);
        }

        // bulk insert the values into the content provider
        context.getContentResolver().bulkInsert(WalksContract.RouteEntry.CONTENT_URI,
                                     valuesList.toArray(new ContentValues[valuesList.size()]));
    }
}
