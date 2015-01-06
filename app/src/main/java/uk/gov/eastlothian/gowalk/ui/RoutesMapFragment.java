package uk.gov.eastlothian.gowalk.ui;



import android.content.Intent;
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
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;
import uk.gov.eastlothian.gowalk.model.Route;

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
            WalksContract.RouteEntry.COLUMN_PRIMARY_AREA,
            WalksContract.RouteEntry.COLUMN_LENGTH
    };

    GoogleMap mMap;
    List<Route> routes = new ArrayList<Route>();

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

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng clickCoords) {
                    for (Route route : routes) {
                        if (PolyUtil.isLocationOnPath(clickCoords, route.getPolylineOptions().getPoints(), true, 100)) {
                            long areaId = route.getPrimaryAreaId();
                            if(areaId == -1) areaId = 0;

                            Intent intent = new Intent(getActivity(), RouteDetailActivity.class);
                            intent.putExtra("route_id", route.getId());
                            intent.putExtra("area_id", areaId);
                            //Log.d(LOG_TAG, "route_id: " + route.getId() + ", area_id: " + areaId);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        routes = Route.fromCursor(cursor);
        for (Route route : routes) {
            PolylineOptions lineOptions = new PolylineOptions();
            long primaryAreaId = route.getPrimaryAreaId();
            int color = AreaColors.getAreaColor(getActivity(), primaryAreaId);
            lineOptions.color(color);
            lineOptions.addAll(route.getCoordinates());
            route.setPolylineOptions(lineOptions);
            if(mMap != null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        getLoaderManager().restartLoader(0, null, this);
    }

}
