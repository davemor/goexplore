package uk.gov.eastlothian.gowalk.ui;

import android.app.Fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.gov.eastlothian.gowalk.R;

/**
 * Created by davidmorrison on 02/12/14.
 */
public class RoutesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_routes_list,
                                                          container, false);


        // ExpandableListAdapter adapter = new ExpandableListAdapter(getActivity(), groups, children);
        // ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.routesListView);
        // listView.setAdapter(adapter);

        return rootView;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
