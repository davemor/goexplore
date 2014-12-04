package uk.gov.eastlothian.gowalk.ui;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.gov.eastlothian.gowalk.R;

/**
 * Created by davidmorrison on 03/12/14.
 */
public class RoutesMapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_routes_map,
                container, false);

        // set up the map
        GoogleMap map = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.location_map)).getMap();
        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // put a marker on Haddington
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(55.9552045,-2.7843538)).title("Haddington"));

            // zoom in on East Lothian
            CameraUpdate center = CameraUpdateFactory.newLatLng(
                    new LatLng(55.9552045, -2.7843538));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
            map.moveCamera(center);
            map.animateCamera(zoom);

            // draw the routes on to the map
        }

        return rootView;
    }

}
