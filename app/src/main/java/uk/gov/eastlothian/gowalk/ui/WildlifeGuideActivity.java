package uk.gov.eastlothian.gowalk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;
import uk.gov.eastlothian.gowalk.model.Wildlife;


public class WildlifeGuideActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wildlife_guide);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WildlifeGuideFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wildlife_guide, menu);
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
    public static class WildlifeGuideFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        static final int WILDLIFE_QUERY_ID = 0;

        List<Wildlife> wildlife;

        GridView gridView;
        WildlifeGridAdapter mAdapter;

        public WildlifeGuideFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wildlife_guide, container, false);

            gridView = (GridView) rootView.findViewById(R.id.wildlife_guide_gridview);
            mAdapter = new WildlifeGridAdapter(getActivity());
            gridView.setAdapter(mAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    // Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                    Wildlife w = (Wildlife) mAdapter.getItem(position);
                    Intent wildlifeDetailIntent = new Intent(getActivity(), WildlifeDetail.class);
                    wildlifeDetailIntent.putExtra("wildlife_id", w.getId());
                    startActivity(wildlifeDetailIntent);
                }
            });

            // set up the query for the wildlife
            getLoaderManager().initLoader(WILDLIFE_QUERY_ID, null, this);

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = WalksContract.WildlifeEntry.CONTENT_URI;
            Loader<Cursor> rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
            return rtnCursor;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // data should have all the wildlife
            wildlife = Wildlife.fromCursor(data);
            mAdapter.setWildlife(wildlife);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    public static class WildlifeGridAdapter extends BaseAdapter {

        static class ViewHolder {
            TextView textView;
            ImageView imageView;
        }

        private LayoutInflater mInflater;
        private Context mContext;
        List<Wildlife> wildlife = new ArrayList<Wildlife>();

        public WildlifeGridAdapter(Context c) {
            mInflater = LayoutInflater.from(c);
            mContext = c;
        }

        void setWildlife(List<Wildlife> wildlife) {
            this.wildlife = wildlife;
        }

        @Override
        public int getCount() {
            return wildlife.size();
        }

        @Override
        public Object getItem(int position) {
            return wildlife.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.wildlife_guide_gird_cell, parent, false);
                holder.textView = (TextView) convertView.findViewById(R.id.wildlife_guide_gird_text);
                holder.imageView = (ImageView) convertView.findViewById(R.id.wildlife_guide_gird_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Wildlife wl = wildlife.get(position);
            holder.textView.setText(wl.getCapitalisedName());
            int imageId = wl.getImageResourceId(mContext);
            holder.imageView.setImageResource(imageId);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(538, 400));
            return convertView;
        }
    }
}
