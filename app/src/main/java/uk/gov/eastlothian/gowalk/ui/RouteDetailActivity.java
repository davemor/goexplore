package uk.gov.eastlothian.gowalk.ui;


import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import uk.gov.eastlothian.gowalk.R;

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
    public static class RouteDetailFragment extends Fragment { // implements LoaderManager.LoaderCallbacks<Cursor> {

        static final int AREA_LOADER_ID = 0;
        static final int ROUTE_LOADER_ID = 1;

        public RouteDetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_route_detail, container, false);

            // get the area id and the route id
            final Long routeId = getActivity().getIntent().getLongExtra("route_id", -1);
            final Long areaId = getActivity().getIntent().getLongExtra("area_id", -1);

            // set the background color of the header to the area color
            final ColorDrawable areaColor = new ColorDrawable(AreaColors.getAreaColor(getActivity(), areaId));
            View headerBG = rootView.findViewById(R.id.route_detail_background_rect);
            headerBG.setBackground(areaColor);
            getActivity().getActionBar().setBackgroundDrawable(areaColor.getConstantState().newDrawable());

            // get coordinates using the areaId

            return rootView;
        }
        /*
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            // set up the loaders
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
        */
    }
}
