package uk.gov.eastlothian.gowalk.ui;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.gov.eastlothian.gowalk.R;


public class LocationPickerActivity extends FragmentActivity {

    public static final int LOCATION_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LocationPickerFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_picker, menu);
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
    public static class LocationPickerFragment extends Fragment {

        GoogleMap mMap;

        public LocationPickerFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_location_picker, container, false);

            // set up the map
            mMap = ((SupportMapFragment)((LocationPickerActivity)getActivity())
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.location_picker_map))
                    .getMap();
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
                    public void onMapClick(LatLng latLng) {
                        Intent result = new Intent();
                        result.putExtra("lat", latLng.latitude);
                        result.putExtra("lng", latLng.longitude);
                        getActivity().setResult(Activity.RESULT_OK, result);
                        getActivity().finish();
                    }
                });
            }

            return rootView;
        }
    }
}
