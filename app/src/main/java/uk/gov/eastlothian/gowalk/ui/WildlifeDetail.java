package uk.gov.eastlothian.gowalk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;
import uk.gov.eastlothian.gowalk.model.Route;
import uk.gov.eastlothian.gowalk.model.Wildlife;

public class WildlifeDetail extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wildlife_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WildlifeDetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wildlife_detail, menu);
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
    public static class WildlifeDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String LOG_TAG = WildlifeDetailFragment.class.getSimpleName();

        static final int WILDLIFE_QUERY_ID = 0;
        static final int ROUTES_FOR_WILDLIFE_QUERY_ID = 1;

        long wildlifeId;
        List<Route> routes = new ArrayList<Route>();
        LayoutInflater mInflator;

        // view parts
        ImageView imageView;
        TextView descriptionView;
        ViewGroup routeNumbersInsertPoint;

        public WildlifeDetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mInflator = inflater;

            View rootView = inflater.inflate(R.layout.fragment_wildlife_detail, container, false);

            wildlifeId = getActivity().getIntent().getLongExtra("wildlife_id", -1);

            // get the view
            imageView = (ImageView) rootView.findViewById(R.id.wildlife_detail_image);
            descriptionView = (TextView) rootView.findViewById(R.id.wildlife_detail_description);
            routeNumbersInsertPoint = (ViewGroup) rootView.findViewById(R.id.wildlife_detail_route_number_insert_point);

            // set up the query for the wildlife
            getLoaderManager().initLoader(WILDLIFE_QUERY_ID, null, this);
            getLoaderManager().initLoader(ROUTES_FOR_WILDLIFE_QUERY_ID, null, this);

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Loader<Cursor> rtnCursor = null;
            switch (id) {
                case WILDLIFE_QUERY_ID: {
                    Uri uri = WalksContract.WildlifeEntry.buildWildLifeUri(wildlifeId);
                    rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
                    break;
                }
                case ROUTES_FOR_WILDLIFE_QUERY_ID: {
                    Uri uri = WalksContract.WildlifeEntry.buildRoutesFromWildlifeUri(wildlifeId);
                    rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
                    break;
                }
            }
            return rtnCursor;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case WILDLIFE_QUERY_ID: {
                    // update the view of the wildlife
                    List<Wildlife> wildlife = Wildlife.fromCursor(data);
                    if (wildlife.size() == 1) {
                        Wildlife w = wildlife.get(0);
                        int imageId = w.getImageResourceId(getActivity());
                        imageView.setImageResource(imageId);
                        descriptionView.setText(w.getDescription());
                        getActivity().getActionBar().setTitle(w.getCapitalisedName());
                    } else {
                        Log.d(LOG_TAG, "Error loading wildlife.");
                    }
                    break;
                }
                case ROUTES_FOR_WILDLIFE_QUERY_ID: {
                    // update the list of route ids
                    routes = Route.fromCursor(data);
                    for (Route route : routes) {
                        addRouteView(route);
                    }
                    break;
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        private void addRouteView(Route route) {
            final long routeId = route.getId();
            final long primaryAreaId = route.getPrimaryAreaId();
            View view =  mInflator.inflate(R.layout.wildlife_detail_route_circle, null);
            view.setPadding(8,8,8,8);

            // set circle background colour
            View circleView = view.findViewById(R.id.wildlife_detail_circle);
            int color = AreaColors.getAreaColor(getActivity(), route.getPrimaryAreaId());
            GradientDrawable drawable = (GradientDrawable) circleView.getBackground();
            drawable.setColor(color);
            drawable.setStroke(3, color); // TODO: define a global stroke value

            TextView routeNumberText = (TextView) view.findViewById(R.id.wildlife_detail_route_number);
            routeNumberText.setText("" + route.getRouteNumber());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: for out the correct area id
                    Intent intent = new Intent(getActivity(), RouteDetailActivity.class);
                    intent.putExtra("route_id", routeId);
                    intent.putExtra("area_id", primaryAreaId);
                    startActivity(intent);

                }
            });
            routeNumbersInsertPoint.addView(view);
        }
    }
}
