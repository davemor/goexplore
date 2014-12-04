package uk.gov.eastlothian.gowalk.ui;



import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;

/**
 * Created by davidmorrison on 03/12/14.
 */
public class RoutesMapFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = RoutesMapFragment.class.getSimpleName();

    // projection for the query to get the routes for an area
    private static final String[] ROUTES_PROJECTION = new String[] {
            WalksContract.RouteEntry._ID,
            WalksContract.RouteEntry.COLUMN_ROUTE_NUMBER,
            WalksContract.RouteEntry.COLUMN_COORDINATES,
            WalksContract.RouteEntry.COLUMN_DESCRIPTION,
            WalksContract.RouteEntry.COLUMN_LENGTH
    };
    public static final int ROUTE_ID = 0;
    public static final int ROUTE_NUMBER = 1;
    public static final int ROUTE_COORDINATES = 2;
    public static final int ROUTE_DESCRIPTION = 3;
    public static final int ROUTE_LENGTH = 4;

    GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_routes_map,
                container, false);

        // set up the map
        mMap = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.location_map)).getMap();
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // put a marker on Haddington
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(55.9552045, -2.7843538)).title("Haddington"));

            // zoom in on East Lothian
            CameraUpdate center = CameraUpdateFactory.newLatLng(
                    new LatLng(55.9552045, -2.7843538));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri routesUri = WalksContract.RouteEntry.CONTENT_URI;
        return new CursorLoader(getActivity(), routesUri, ROUTES_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // use the cursor to add the routes to the map
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // parse the coordinates
            List<LatLng> coords = makeCoordsFromJsonStr(cursor.getString(ROUTE_COORDINATES));

            if(coords.size() > 0) {
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.addAll(coords);
                mMap.addPolyline(lineOptions);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        getLoaderManager().restartLoader(0, null, this);
    }

    List<LatLng> makeCoordsFromJsonStr(String coordinates) {
        List<LatLng> rtnList = new ArrayList<LatLng>();
        try {
            JSONArray array = new JSONArray(coordinates).getJSONArray(0);
            for(int idx = 0; idx < array.length(); ++idx) {
                JSONArray pair = array.getJSONArray(idx);
                rtnList.add(new LatLng(pair.getDouble(1), pair.getDouble(0)));
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "Error parsing route coordinates for map: " + e);
        }
        return rtnList;
    }
}
