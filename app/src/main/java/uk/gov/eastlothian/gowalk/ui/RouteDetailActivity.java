package uk.gov.eastlothian.gowalk.ui;


import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
    public static class RouteDetailFragment extends Fragment {

        public RouteDetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_route_detail, container, false);

            // get the area id
            Long routeId = getActivity().getIntent().getLongExtra("route_id", -1);
            Long areaId = getActivity().getIntent().getLongExtra("area_id", -1);

            // set the background color of the header to the area color
            ColorDrawable areaColor = new ColorDrawable(AreaColors.getAreaColor(areaId));
            View header = rootView.findViewById(R.id.route_detail_header);
            header.setBackground(areaColor);
            getActivity().getActionBar().setBackgroundDrawable(areaColor);

            // draw the route on the map

            return rootView;
        }
    }
}
