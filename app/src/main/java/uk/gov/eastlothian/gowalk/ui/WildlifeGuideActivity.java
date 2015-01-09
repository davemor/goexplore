package uk.gov.eastlothian.gowalk.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;
import uk.gov.eastlothian.gowalk.model.Wildlife;


public class WildlifeGuideActivity extends MainMenuActivity {

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class WildlifeGuideFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        static final int WILDLIFE_QUERY_ID = 0;

        List<Wildlife> wildlife;

        GridView gridView;
        WildlifeGridAdapter mAdapter;
        int screenWidth = 0;

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
                    Intent wildlifeDetailIntent = new Intent(getActivity(), WildlifeDetailActivity.class);
                    wildlifeDetailIntent.putExtra("wildlife_id", w.getId());
                    startActivity(wildlifeDetailIntent);
                }
            });

            // set the screen width
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;

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
            mAdapter.setWildlife(wildlife, screenWidth);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    public static class WildlifeGridAdapter extends BaseAdapter {

        int viewWidth;

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

        void setWildlife(List<Wildlife> wildlife, int viewWidth) {
            this.wildlife = wildlife;
            // consider async task to generate all the wildlife bit maps
            /*
            for (int idx=0; idx < wildlife.size(); ++idx) {
                Bundle b = new Bundle();
                b.putInt("idx", idx);
                b.putInt("screenWidth", viewWidth);
                new LoadWildlifeThumbnail().execute(b);
            }
            */
            this.viewWidth = viewWidth;
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
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                int width = (int) (parent.getWidth() / 2.0);
                int height = (int) (0.74 * width); // TODO: It's magic!
                holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Wildlife wl = wildlife.get(position);
            holder.textView.setText(wl.getCapitalisedName());

            // set the image
            Bitmap thumbnail = wl.getThumbnail();
            if (thumbnail == null) {
                /*
                int resourceId = wl.getImageResourceId(mContext);
                holder.imageView.setImageResource(resourceId);
                */
                wl.makeThumbnail(mContext, viewWidth / 2);
                thumbnail = wl.getThumbnail();
            }
            holder.imageView.setImageBitmap(thumbnail);

            return convertView;
        }

        /*
        // needs wildlife id and resource id
        private class LoadWildlifeThumbnail extends AsyncTask<Bundle, Void, Bundle> {


            @Override
            protected Bundle doInBackground(Bundle... bundles) {

                int idx = bundles[0].getInt("idx");
                int width = bundles[0].getInt("screenWidth");
                int height = (int) (0.74 * width);

                Wildlife wl = wildlife.get(idx);
                int resourceId = wl.getImageResourceId(mContext);

                System.gc();

                Bitmap thumbnail = decodeSampledBitmapFromResource(mContext.getResources(), resourceId, width, height);
                wl.setThumbnail(thumbnail);

                System.gc();

                return null;
            }

            public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                                 int reqWidth, int reqHeight) {

                // First decode with inJustDecodeBounds=true to check dimensions
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(res, resId, options);

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeResource(res, resId, options);
            }

            public int calculateInSampleSize(
                    BitmapFactory.Options options, int reqWidth, int reqHeight) {
                // Raw height and width of image
                final int height = options.outHeight;
                final int width = options.outWidth;
                int inSampleSize = 1;

                if (height > reqHeight || width > reqWidth) {

                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                    // height and width larger than the requested height and width.
                    while ((halfHeight / inSampleSize) > reqHeight
                            && (halfWidth / inSampleSize) > reqWidth) {
                        inSampleSize *= 2;
                    }
                }

                return inSampleSize;
            }
        }
        */
    }
}
