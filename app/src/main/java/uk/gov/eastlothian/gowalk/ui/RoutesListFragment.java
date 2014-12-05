package uk.gov.eastlothian.gowalk.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;

public class RoutesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // log tag
    private final String LOG_TAG = getClass().getSimpleName().toString();

    // projection for the query to get the areas
    private static final String[] AREA_PROJECTION = new String[] {
            WalksContract.AreaEntry._ID,
            WalksContract.AreaEntry.COLUMN_AREA_NAME
    };

    // projection for the query to get the routes for an area
    private static final String[] ROUTES_PROJECTION = new String[] {
            WalksContract.RouteEntry._ID,
            WalksContract.RouteEntry.COLUMN_ROUTE_NUMBER,
            WalksContract.RouteEntry.COLUMN_DESCRIPTION,
            WalksContract.RouteEntry.COLUMN_LENGTH
    };

    // adapter for the list view
    RoutesListAdapter mRoutesAdapter;
    ExpandableListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_routes_list, container, false);
        mListView = (ExpandableListView) rootView.findViewById(R.id.route_list_expandable_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // bind the view and the adapter
        mRoutesAdapter = new RoutesListAdapter(getActivity(),
            this,
            R.layout.routes_list_group,
            R.layout.routes_list_child,
            new String[] {
                WalksContract.AreaEntry.COLUMN_AREA_NAME
            },
            new int[] {
                R.id.routes_list_group_title_text
            },
            new String[] {
                WalksContract.RouteEntry.COLUMN_ROUTE_NUMBER,
                WalksContract.RouteEntry.COLUMN_DESCRIPTION,
                WalksContract.RouteEntry.COLUMN_LENGTH
            },
            new int[] {
                R.id.routes_list_child_route_num_text,
                R.id.routes_list_child_description,
                R.id.routes_list_child_length
            });
        mListView.setAdapter(mRoutesAdapter);

        // set up the listener for when user clicks on child
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView listView, View view,
                                        int groupPos, int childPos, long id) {
                int areaId = mRoutesAdapter.getAreaIdFromGroupPos(groupPos); // TODO: change the indices over to longs from ints to avoid this
                Long routeId = mRoutesAdapter.getRouteId(areaId, childPos);

                Intent intent = new Intent(getActivity(), RouteDetailActivity.class);
                intent.putExtra("route_id", routeId);
                intent.putExtra("area_id", new Long(areaId)); // TODO: change to a long all the way though
                startActivity(intent);
                return false;
            }
        });

        // set up the loaded based on the loader state
        LoaderManager loaderManager = ((FragmentActivity)getActivity()).getSupportLoaderManager();
        Loader<Cursor> loader = loaderManager.getLoader(-1);
        if (loader != null && !loader.isReset()) {
            loaderManager.restartLoader(-1, null, this);
        } else {
            loaderManager.initLoader(-1, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // called when a new Loader needs to be created
        CursorLoader cursorLoader = null;
        Activity activity = getActivity();
        if (activity != null) {
            if (id != -1) {
                // create a cursor that gets all the routes in the specified area
                Uri routesUri = WalksContract.AreaEntry.buildRoutesInAreaUri(id);
                cursorLoader = new CursorLoader(getActivity(), routesUri, ROUTES_PROJECTION,
                        null, null, null);
            } else {
                // create a cursor that gets all the areas
                Uri areasUri = WalksContract.AreaEntry.CONTENT_URI;
                cursorLoader = new CursorLoader(getActivity(), areasUri, null, // AREA_PROJECTION,
                        null, null, null);
            }
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int count = data.getCount();
        Log.d(LOG_TAG, "" + count);

        int id = loader.getId();
        if(id != -1) {
            if (!data.isClosed()) {
                SparseArray<Integer> areaMap = mRoutesAdapter.getAreaMap();
                try {
                    int areaPos = areaMap.get(id);
                    mRoutesAdapter.setChildrenCursor(areaPos, data);
                } catch (NullPointerException e) {
                    Log.d(LOG_TAG, "Adapter expired, try again on the next query: "
                                    + e.getMessage());
                }
            }
        } else {
            mRoutesAdapter.setGroupCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        if (id != -1) {
            // it's a child cursor
            try {
                mRoutesAdapter.setChildrenCursor(id, null);
            } catch (NullPointerException e) {
                Log.w(LOG_TAG, "Adapter expired, try again on the next query: " + e.getMessage());
            }
        } else {
            // it's a parent cursor
            mRoutesAdapter.setGroupCursor(null);
        }
    }

    public static class RoutesListAdapter extends SimpleCursorTreeAdapter {
        private RoutesActivity mActivity;
        private RoutesListFragment mFragment;
        private SparseArray<Integer> mGroupMap;
        private SparseArray<SparseArray<Long>> mChildMaps;

        public RoutesListAdapter(Context context, RoutesListFragment fragment,
                                 int groupLayout, int childLayout,
                                 String[] groupFrom, int[] groupTo,
                                 String[] childrenFrom, int[] childrenTo) {
            super(context, null, groupLayout, groupFrom,
                  groupTo, childLayout, childrenFrom, childrenTo);
            mActivity = (RoutesActivity) context;
            mFragment = fragment;
            mGroupMap = new SparseArray<Integer>();
            mChildMaps = new SparseArray<SparseArray<Long>>();
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
            super.bindChildView(view, context, cursor, isLastChild);
            View circle = view.findViewById(R.id.list_item_circle);
            GradientDrawable shape = (GradientDrawable) circle.getBackground();

            int areaIdColIndex = cursor.getColumnIndex(WalksContract.RouteInAreaEntry.COLUMN_AREA_KEY);
            long areaId = cursor.getLong(areaIdColIndex);
            shape.setStroke(3, AreaColors.getAreaColor(areaId));

            // insert the id of the child into a map at this position
            int childPos = cursor.getPosition();
            int childIdColIndex = cursor.getColumnIndex(WalksContract.RouteEntry._ID);
            long childId = cursor.getLong(childIdColIndex);
            SparseArray<Long> childMap = mChildMaps.get((int)areaId, new SparseArray<Long>());
            childMap.put(childPos, childId);
            mChildMaps.put((int)areaId, childMap);
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
            super.bindGroupView(view, context, cursor, isExpanded);

            View rect = view.findViewById(R.id.list_group_rectangle);
            int keyIndex = cursor.getColumnIndex(WalksContract.AreaEntry._ID);
            long id = cursor.getLong(keyIndex);
            rect.setBackgroundColor(AreaColors.getAreaColor(id));
        }

        @Override
        protected Cursor getChildrenCursor(Cursor cursor) {
            int areaPos = cursor.getPosition();
            int areaId = cursor.getInt(cursor.getColumnIndex(WalksContract.AreaEntry._ID));
            mGroupMap.put(areaId, areaPos);
            Loader loader = mActivity.getSupportLoaderManager().getLoader(areaId);
            if (loader != null && !loader.isReset()) {
                mActivity.getSupportLoaderManager().restartLoader(areaId, null, mFragment);
            } else {
                mActivity.getSupportLoaderManager().initLoader(areaId, null, mFragment);
            }
            return null;
        }

        public SparseArray<Integer> getAreaMap() {
            return mGroupMap;
        }

        public Long getRouteId(int areaId, int childPos) {
            return mChildMaps.get(areaId).get(childPos);
        }

        private int getAreaIdFromGroupPos(int groupPos) {
            int index = mGroupMap.indexOfValue(groupPos);
            return mGroupMap.keyAt(index);
        }
    }
}