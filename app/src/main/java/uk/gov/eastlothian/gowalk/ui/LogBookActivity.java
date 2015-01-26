package uk.gov.eastlothian.gowalk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;
import uk.gov.eastlothian.gowalk.model.Wildlife;

public class LogBookActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_book);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LogBookFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class LogBookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        static final int WILDLIFE_LOG_ENTRIES_ID = 0;

        List<Wildlife> wildlife;
        
        LogBookGridAdapter mAdapter;
        TextView emptyLabel;

        public LogBookFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_log_book, container, false);

            GridView gridView = (GridView) rootView.findViewById(R.id.log_book_gridview);
            mAdapter = new LogBookGridAdapter(getActivity());
            gridView.setAdapter(mAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Wildlife w = (Wildlife) mAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), LogEntryActivity.class);
                    intent.putExtra("wildlife_id", w.getId());
                    startActivity(intent);
                }
            });

            emptyLabel = (TextView) rootView.findViewById(R.id.logbook_empty_label);

            // set up the query for the wildlife
            getLoaderManager().initLoader(WILDLIFE_LOG_ENTRIES_ID, null, this);

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = WalksContract.LogEntry.buildWildlifeLogsUri();
            Loader<Cursor> rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
            return rtnCursor;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            wildlife = Wildlife.fromCursor(data);
            if (wildlife.isEmpty()) {
                emptyLabel.setVisibility(View.VISIBLE);
            } else {
                emptyLabel.setVisibility(View.INVISIBLE);
            }
            mAdapter.setWildlifeList(wildlife);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    public static class LogBookGridAdapter extends BaseAdapter {

        static class ViewHolder {
            TextView textView;
            ImageView imageView;
            View circleView;
            TextView badgeText;
        }

        Context mContext;
        LayoutInflater mInflater;
        List<Wildlife> mWildlife = new ArrayList<Wildlife>();

        public void setWildlifeList(List<Wildlife> wildlifeList) {
            this.mWildlife = wildlifeList;
        }

        LogBookGridAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            int size = mWildlife.size();
            return size;
        }

        @Override
        public Object getItem(int position) {
            return mWildlife.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.log_book_grid_cell, parent, false);
                holder.textView = (TextView) convertView.findViewById(R.id.log_book_gird_text);
                holder.imageView = (ImageView) convertView.findViewById(R.id.log_book_gird_image);
                holder.circleView = convertView.findViewById(R.id.log_book_circle);
                holder.badgeText = (TextView) convertView.findViewById(R.id.log_book_route_num);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Wildlife wl = mWildlife.get(position);
            holder.textView.setText(wl.getCapitalisedName());
            int imageId = wl.getImageResourceId(mContext);
            holder.imageView.setImageResource(imageId);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int width = (int) (parent.getWidth() / 2.0);
            int height = (int) (0.74 * width); // TODO: It's magic!
            holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            GradientDrawable shape = (GradientDrawable) holder.circleView.getBackground();
            int color = Color.parseColor("#FF4400");
            shape.setColor(color);
            shape.setStroke(0, color);
            holder.badgeText.setText("" + wl.getNumLogEntries());
            return convertView;
        }
    }
}
