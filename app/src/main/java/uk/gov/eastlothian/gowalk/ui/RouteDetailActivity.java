package uk.gov.eastlothian.gowalk.ui;


import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.List;
import java.util.zip.Inflater;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;
import uk.gov.eastlothian.gowalk.model.Area;
import uk.gov.eastlothian.gowalk.model.Route;
import uk.gov.eastlothian.gowalk.model.Wildlife;

public class RouteDetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RouteDetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_route_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class RouteDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        static final int AREA_QUERY_ID = 0;
        static final int ROUTE_QUERY_ID = 1;
        static final int WILDLIFE_QUERY_ID = 2;

        TextView routeDescriptionView;
        TextView areaDescriptionView;
        TextView routeLengthView;
        TextView routeNumberView;
        TextView routeSurface;
        TextView surfaceLabel;
        TextView wildlifeLabel;
        GoogleMap mMap;
        ViewGroup wildlifeInsertPoint;

        LayoutInflater mInflater;

        long routeId;
        long areaId;
        Route route;
        Area area;
        List<Wildlife> wildlife;

        public RouteDetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mInflater = inflater;
            View rootView = inflater.inflate(R.layout.fragment_route_detail, container, false);

            // get the area id and the route id
            routeId = getActivity().getIntent().getLongExtra("route_id", -1);
            areaId = getActivity().getIntent().getLongExtra("area_id", -1);

            // set the background color of the header to the area color
            final ColorDrawable areaColor = new ColorDrawable(AreaColors.getAreaColor(getActivity(), areaId));
            //View headerBG = rootView.findViewById(R.id.route_detail_background_rect);
            //headerBG.setBackground(areaColor);
            View header = rootView.findViewById(R.id.route_detail_header);
            header.setBackground(areaColor);
            getActivity().getActionBar().setBackgroundDrawable(areaColor.getConstantState().newDrawable());

            // bind the parts of the header we want to update
            routeDescriptionView = (TextView) rootView.findViewById(R.id.route_detail_description);
            areaDescriptionView = (TextView) rootView.findViewById(R.id.route_detail_area_description);
            routeLengthView = (TextView) rootView.findViewById(R.id.route_detail_length);
            routeNumberView = (TextView) rootView.findViewById(R.id.route_detail_route_num);
            routeSurface = (TextView) rootView.findViewById(R.id.route_detail_surface_text);
            mMap = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.route_detail_mapview)).getMap();
            wildlifeInsertPoint = (ViewGroup) rootView.findViewById(R.id.route_detail_wildlife_insert_point);

            surfaceLabel = (TextView) rootView.findViewById(R.id.route_detail_accessibility_label);
            wildlifeLabel = (TextView) rootView.findViewById(R.id.route_detail_wildlife_label);

            // set up a query to get the route information
            getLoaderManager().initLoader(AREA_QUERY_ID, null, this);
            getLoaderManager().initLoader(ROUTE_QUERY_ID, null, this);
            getLoaderManager().initLoader(WILDLIFE_QUERY_ID, null, this);

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Loader<Cursor> rtnCursor;
            switch (id) {
                case AREA_QUERY_ID: {
                    Uri uri = WalksContract.AreaEntry.buildAreaUri(areaId);
                    rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
                    break;
                }
                case ROUTE_QUERY_ID: {
                    Uri uri = WalksContract.RouteEntry.buildRouteUri(routeId);
                    rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
                    break;
                }
                case WILDLIFE_QUERY_ID: {
                    Uri uri = WalksContract.RouteEntry.buildWildlifeOnRouteUri(routeId);
                    rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Don't recognise this loader id");
            }
            return rtnCursor;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case AREA_QUERY_ID:
                    area = Area.fromCursor(data).get(0);
                    bindArea(area);
                    break;
                case ROUTE_QUERY_ID:
                    route = Route.fromCursor(data).get(0);
                    bindRoute(route);
                    break;
                case WILDLIFE_QUERY_ID:
                    wildlife = Wildlife.fromCursor(data);
                    bindWildlife();
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // As we are not saving the cursor we don't need to reset anything.
        }

        void bindArea(Area area) {
            areaDescriptionView.setText(area.getName());
        }

        void bindRoute(Route route) {
            // header
            routeNumberView.setText("" + route.getRouteNumber());
            routeDescriptionView.setText(route.getDescription());
            routeLengthView.setText("" + route.getLength() + "m");

            getActivity().getActionBar().setTitle("Route Number " + route.getRouteNumber());

            // map
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // put a marker on Haddington
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(55.9552045, -2.7843538)).title("Haddington"));

                // add marker for start and end
                mMap.addMarker(new MarkerOptions().position(route.startPoint()).title("Start"));
                mMap.addMarker(new MarkerOptions().position(route.endPoint()).title("End"));

                // zoom in on East Lothian
                CameraUpdate center = CameraUpdateFactory.newLatLng(route.centrePoint());
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);

                // draw the route
                int color = AreaColors.getAreaColor(getActivity(), areaId);
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(route.getCoordinates());
                polylineOptions.color(color);
                mMap.addPolyline(polylineOptions);
            }

            // bind accessibility
            routeSurface.setText(route.getSurface());
        }

        void bindWildlife() {
            if (wildlife.isEmpty()) {
                wildlifeLabel.setText("");
            } else {
                LinearLayout rowLayout = null;
                int idx = 0;
                for (Wildlife wl : wildlife) {
                    if (idx % 3 == 0) {
                        if (rowLayout != null) {
                            wildlifeInsertPoint.addView(rowLayout);
                        }
                        rowLayout = new LinearLayout(getActivity());
                        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                        rowLayout.setGravity(Gravity.CENTER);
                        // rowLayout.setBackgroundColor(getResources().getColor(R.color.purple));
                    }
                    View wildlifeView = makeWildlifeView(wl);
                    rowLayout.addView(wildlifeView);
                    ++idx;
                }
            }
        }

        View makeWildlifeView(Wildlife wl) {
            final long wildlifeId = wl.getId();
            View view = mInflater.inflate(R.layout.route_detail_wildlife_image, null);
            view.setPadding(8, 8, 8, 8);
            ImageView imageView = (ImageView) view.findViewById(R.id.route_detail_wildlife_image_view);
            imageView.setImageResource(wl.getImageResourceId(getActivity()));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), WildlifeDetail.class);
                    intent.putExtra("wildlife_id", wildlifeId);
                    startActivity(intent);

                }
            });
            return view;
        }
    }
}
