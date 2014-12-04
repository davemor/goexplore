package uk.gov.eastlothian.gowalk.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class can be used to load the various part of the data model
 * into the database.
 *
 * Created by davidmorrison on 26/11/14.
 */
public class WalksDataLoader {
    public static final String LOG_TAG = WalksDataLoader.class.getSimpleName();

    public static void initDatabase(Context context) {
        File dbFile = context.getDatabasePath(WalksDbHelper.DB_NAME);
        if(!dbFile.exists()) {
            try {
                loadWalksDatabaseFromFiles(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadWalksDatabaseFromFiles(Context context) throws IOException {
        AssetManager asserts = context.getAssets();
        Map<Integer, String> descriptions = loadRouteDescriptionsFromCSV(asserts.open("Routes.csv"));
        insertRoutesIntoWalksDatabase(asserts.open("core_paths.json"), descriptions, context);
        WalksDataLoader.loadWildlifeDbFromCSV(asserts.open("Wildlife.csv"), context);
        loadRoutesInAreas(asserts.open("RoutesInAreas.csv"), context);
    }

    private static void insertRoutesIntoWalksDatabase(InputStream jsonIS, Map<Integer, String> descriptions, Context context) throws IOException
    {
        // read in the json string from the file
        BufferedReader reader = new BufferedReader(new InputStreamReader(jsonIS));
        StringBuilder builder = new StringBuilder();
        String aux = "";
        while ((aux = reader.readLine()) != null) {
            builder.append(aux);
        }
        String routesJsonStr = builder.toString();

        // parse the json in a list of content values for a row in the routes table
        List<ContentValues> valuesList = new ArrayList<ContentValues>();
        try {
            JSONObject routesJson = new JSONObject(routesJsonStr);
            JSONArray paths = routesJson.getJSONArray("features");
            Log.d(LOG_TAG, "" + paths.length());
            for (int idx=0; idx < paths.length(); ++idx) {

                // get the json objects out the document tree
                JSONObject path = paths.getJSONObject(idx);

                String coordinates = "[]";
                if(!path.isNull("geometry")) {
                    JSONObject geometryObj = path.getJSONObject("geometry");
                    JSONArray coordinatesArray = geometryObj.getJSONArray("coordinates");
                    coordinates = coordinatesArray.toString();
                }

                // get the values out of the json object
                int routeNumber = -1;
                String pathType = "unknown";
                int length = 0;
                String surface = "unknown";
                String description = "no description available";

                if(!path.isNull("properties")) {
                    JSONObject properties = path.getJSONObject("properties");
                    routeNumber = properties.getInt("route_no");
                    pathType = properties.getString("path_type");
                    length = properties.getInt("length");
                    surface = properties.getString("surface");
                    if (surface.equalsIgnoreCase("null")) {
                        surface = "unknown";
                    }
                    description = descriptions.get(routeNumber);
                    if (description == null || description.equalsIgnoreCase("null")) {
                        description = "no description available";
                    }
                }

                // associate the values with table names
                ContentValues values = new ContentValues();
                values.put(WalksContract.RouteEntry.COLUMN_ROUTE_NUMBER, routeNumber);
                values.put(WalksContract.RouteEntry.COLUMN_COORDINATES, coordinates);
                values.put(WalksContract.RouteEntry.COLUMN_PATH_TYPE, pathType);
                values.put(WalksContract.RouteEntry.COLUMN_LENGTH, length);
                values.put(WalksContract.RouteEntry.COLUMN_SURFACE, surface);
                values.put(WalksContract.RouteEntry.COLUMN_DESCRIPTION, description);

                Log.d(LOG_TAG, "idx: " + idx);

                valuesList.add(values);
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "There was an error parsing the core path json.", e);
        }

        // bulk insert the values into the content provider
        ContentValues [] contentValues = valuesList.toArray(new ContentValues[valuesList.size()]);
        for(int idx=0; idx < contentValues.length-1; ++idx) {
            Log.d(LOG_TAG, contentValues[idx].toString());
        }
        context.getContentResolver().bulkInsert(WalksContract.RouteEntry.CONTENT_URI, contentValues);
    }

    private static Map<Integer, String> loadRouteDescriptionsFromCSV(InputStream inStream) {
        Map<Integer, String> rtnMap = new HashMap<Integer, String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        try {
            reader.readLine(); // read and throw away the first line
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");
                int routeNumber = Integer.parseInt(rowData[0]);
                String description = rowData[1];
                rtnMap.put(routeNumber, description);
            }
        } catch (IOException ex) {
            Log.d(LOG_TAG, "Error while loading the route descriptions.", ex);
        } finally {
            try {
                inStream.close();
            }
            catch (IOException e) {
                Log.d(LOG_TAG, "Error while closing the route descriptions csv input stream.", e);
            }
        }
        return rtnMap;
    }

    private static void loadWildlifeDbFromCSV(InputStream inStream, Context context) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        try {
            reader.readLine();
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null) {
                String[] rowData = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                if(rowData.length >= 5) {
                    // get the values out of the row
                    String name = rowData[1];
                    String category = rowData[2];
                    String description = rowData[3];
                    String foundOnRoutes = rowData[4];
                    String whenSeen = rowData[5];
                    String imageFile = "no_image";

                    // images are optional
                    if (rowData.length > 6) {
                        imageFile = rowData[6];
                    }

                    // assign them to database table names
                    ContentValues values = new ContentValues();
                    values.put(WalksContract.WildlifeEntry.COLUMN_WILDLIFE_NAME, name);
                    values.put(WalksContract.WildlifeEntry.COLUMN_CATEGORY, category);
                    values.put(WalksContract.WildlifeEntry.COLUMN_DESCRIPTION, description);
                    values.put(WalksContract.WildlifeEntry.COLUMN_WHEN_SEEN, whenSeen);
                    values.put(WalksContract.WildlifeEntry.COLUMN_IMAGE_NAME, imageFile);

                    // insert the route into the content resolver
                    Uri wildlifeUri = context.getContentResolver().insert(WalksContract.WildlifeEntry.CONTENT_URI, values);
                    long wildlifeId = Long.parseLong(WalksContract.WildlifeEntry.getWildlifeFromUri(wildlifeUri));

                    // get the route numbers of the routes the wildlife is found on
                    // remove the first and last character (quotes) then split on comma
                    String[] foundOnRoutesArr = foundOnRoutes.substring(1, foundOnRoutes.length() - 1)
                            .split(",");

                    // get the ids of those routes from the database
                    // note - we are assuming that the routes have been inserted already
                    List<ContentValues> wildlifeOnRouteValues = new ArrayList<ContentValues>();
                    for (String routeNumStr : foundOnRoutesArr) {
                        // get the route id from the route number
                        try {
                            routeNumStr = routeNumStr.replace('.', ' ').trim();
                            long routeNum = Long.parseLong(routeNumStr);
                            Cursor cursor = context.getContentResolver().query(
                                    WalksContract.RouteEntry.CONTENT_URI,
                                    new String[]{WalksContract.RouteEntry._ID},
                                    WalksContract.RouteEntry.COLUMN_ROUTE_NUMBER + " LIKE ?",
                                    new String[]{routeNum + "%"},
                                    null);
                            if (cursor.moveToFirst()) {
                                ContentValues rowValues = new ContentValues();
                                int idx = cursor.getColumnIndex(WalksContract.RouteEntry._ID);
                                long routeId = cursor.getLong(idx);
                                rowValues.put(WalksContract.WildlifeOnRouteEntry.COLUMN_ROUTE_KEY, routeId);
                                rowValues.put(WalksContract.WildlifeOnRouteEntry.COLUMN_WILDLIFE_KEY, wildlifeId);
                                wildlifeOnRouteValues.add(rowValues);

                            } else {
                                Log.d(LOG_TAG, "Error while loading wildlife.  Cannot find route number " + routeNumStr + ".");
                            }
                            cursor.close();
                        } catch (NumberFormatException e) {
                            Log.d(LOG_TAG, "Invalid route number for wildlife " + routeNumStr + ".");
                        }
                    }
                    // insert the values into the database
                    context.getContentResolver().bulkInsert(WalksContract.WildlifeOnRouteEntry.CONTENT_URI,
                            wildlifeOnRouteValues.toArray(new ContentValues[wildlifeOnRouteValues.size()]));
                } else {
                    Log.d(LOG_TAG, "Skipping line that does not have enough data.");
                }
            }
        } catch (IOException ex) {
            Log.d(LOG_TAG, "Error while loading the wildlife.", ex);
        } finally {
            try {
                inStream.close();
            }
            catch (IOException e) {
                Log.d(LOG_TAG, "Error while closing the wildlife csv input stream.", e);
            }
        }
    }

    private static void loadRoutesInAreas(InputStream inputStream, Context context) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            reader.readLine();

            // build up a hash table of the area names to the routes that cross them
            Map<String, List<Long>> areas = new HashMap<String, List<Long>>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                // resolve the route id from it's number
                int routeNumber = Integer.parseInt(rowData[0]);
                long routeId = getRouteIdFromRouteNumber(routeNumber, context);

                // find the area and add the route id to it's routes
                String areaName = rowData[1].replace("\"", ""); // remove all the quotes
                if (areas.containsKey(areaName)) {
                    areas.get(areaName).add(routeId);
                } else {
                    List<Long> routes = new ArrayList<Long>();
                    routes.add(routeId);
                    areas.put(areaName, routes);
                }
            }

            // insert the areas and routes on areas into the content provider
            for (Map.Entry<String, List<Long>> area : areas.entrySet()) {
                // create a new area
                ContentValues areaValues = new ContentValues();
                areaValues.put(WalksContract.AreaEntry.COLUMN_AREA_NAME, area.getKey());
                Uri areaUri = context.getContentResolver().insert(WalksContract.AreaEntry.CONTENT_URI, areaValues);
                long areaId = Long.parseLong(WalksContract.AreaEntry.getAreaFromUri(areaUri));

                // for each route, create an entry in the ROUTE_IN_AREA table
                ContentValues[] areaInRouteValues = new ContentValues[area.getValue().size()];
                int idx = 0;
                for (long routeId : area.getValue()) {
                    areaInRouteValues[idx] = new ContentValues();
                    areaInRouteValues[idx].put(WalksContract.RouteInAreaEntry.COLUMN_ROUTE_KEY, routeId);
                    areaInRouteValues[idx].put(WalksContract.RouteInAreaEntry.COLUMN_AREA_KEY, areaId);
                    ++idx;
                }
/*
                Log.d(LOG_TAG, "Content Values for " + areaId);
                for(int i=0; i < areaInRouteValues.length; ++i) {
                    printContentValues(areaInRouteValues[i]);
                }
                */
                context.getContentResolver().bulkInsert(WalksContract.RouteInAreaEntry.CONTENT_URI, areaInRouteValues);
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error while reading routes in areas table." + e.toString());
        } finally {
            try {
                reader.close();
            }
            catch (IOException ex) {
                Log.d(LOG_TAG, "Error while closing routes in area reader.");
            }
        }
    }

    private static long getRouteIdFromRouteNumber(int routeNumber, Context context) {
        Cursor cursor = context.getContentResolver().query(
                WalksContract.RouteEntry.CONTENT_URI,
                new String[]{WalksContract.RouteEntry._ID},
                WalksContract.RouteEntry.COLUMN_ROUTE_NUMBER + " LIKE ?",
                new String[]{routeNumber + "%"},
                null);
        long routeId = -1;
        if (cursor.moveToFirst()) {
            int routeIndex = cursor.getColumnIndex(WalksContract.RouteEntry._ID);
            routeId = cursor.getLong(routeIndex);
        }
        return routeId;
    }

    // debug
    private static void printContentValues(ContentValues values) {
        for(Map.Entry<String, Object> pair : values.valueSet()) {
            Log.d(LOG_TAG, pair.getKey() + " : " + pair.getValue());
        }
    }
}
