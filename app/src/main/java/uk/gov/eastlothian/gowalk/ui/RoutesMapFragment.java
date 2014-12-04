package uk.gov.eastlothian.gowalk.ui;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        return rootView;
    }

}
