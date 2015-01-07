package uk.gov.eastlothian.gowalk.ui;

import android.app.ActionBar;
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
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;
import uk.gov.eastlothian.gowalk.model.Wildlife;

public class LogEntryActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_entry);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LogEntryFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class LogEntryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        static final int WILDLIFE_ID = 0;
        static final int WILDLIFE_LOG_QUERY_ID = 1;

        long wildlifeId;

        ImageView imageView;
        ListView listView;
        LogListAdapter adapter;

        public LogEntryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_log_entry, container, false);

            // set the wildlife id
            wildlifeId = getActivity().getIntent().getLongExtra("wildlife_id", -1);

            // bind the views
            imageView = (ImageView) rootView.findViewById(R.id.log_entry_imageview);
            listView = (ListView) rootView.findViewById(R.id.log_entry_listview);
            adapter = new LogListAdapter(getActivity(), null, false);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Long entryId = (Long) view.getTag();
                    Intent intent = new Intent(getActivity(), LogBookSightingDetailActivity.class);
                    intent.putExtra("log_entry_id", entryId);
                    startActivity(intent);
                }
            });

            // get the view
            int width = this.getResources().getDisplayMetrics().widthPixels;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int height = (int) (0.74 * width); // TODO: It's magic!
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));

            // start the query
            getLoaderManager().initLoader(WILDLIFE_ID, null, this);
            getLoaderManager().initLoader(WILDLIFE_LOG_QUERY_ID, null, this);

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Loader<Cursor> rtnCursor;
            switch(id) {
                case WILDLIFE_ID: {
                    Uri uri = WalksContract.WildlifeEntry.buildWildLifeUri(wildlifeId);
                    rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
                    break;
                }
                case WILDLIFE_LOG_QUERY_ID: {
                    Uri uri = WalksContract.LogEntry.buildLogsForWildlifeUri(wildlifeId);
                    rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown loader id.");
            }
            return rtnCursor;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            switch(loader.getId()) {
                case WILDLIFE_ID:
                    Wildlife wildlife = Wildlife.fromCursor(data).get(0);
                    imageView.setImageResource(wildlife.getImageResourceId(getActivity()));
                    getActivity().getActionBar().setTitle(wildlife.getCapitalisedName());
                    break;
                case WILDLIFE_LOG_QUERY_ID:
                    adapter.swapCursor(data);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown loader id.");
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        public static class LogListAdapter extends CursorAdapter {

            LayoutInflater inflater;

            public LogListAdapter(Context context, Cursor cursor, boolean autoRequery) {
                super(context, cursor, autoRequery);
                inflater = LayoutInflater.from(context);
            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View element = inflater.inflate(R.layout.logbook_entry_list_element, parent, false);
                return element;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                // TODO: some of this stuff could be cached.
                int idIdx = cursor.getColumnIndex(WalksContract.LogEntry._ID);
                int latIdx = cursor.getColumnIndex(WalksContract.LogEntry.COLUMN_LAT);
                int lngIdx = cursor.getColumnIndex(WalksContract.LogEntry.COLUMN_LNG);
                int dateTimeIdx = cursor.getColumnIndex(WalksContract.LogEntry.COLUMN_DATATIME);
                int weatherIdx = cursor.getColumnIndex(WalksContract.LogEntry.COLUMN_WEATHER);

                long id = cursor.getLong(idIdx);
                String lat = cursor.getString(latIdx);
                String lng = cursor.getString(lngIdx);
                String dateTime = cursor.getString(dateTimeIdx);
                String weather = cursor.getString(weatherIdx);

                TextView locationText = (TextView) view.findViewById(R.id.log_entry_place);
                TextView datetimeText = (TextView) view.findViewById(R.id.log_entry_datetime);

                // TODO: format this better
                SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    iso8601Format.parse(dateTime);
                    final Calendar calendar = iso8601Format.getCalendar();

                    SimpleDateFormat prettyFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                    dateTime = prettyFormat.format(calendar.getTime());

                    datetimeText.setText("Sighting at " + dateTime.split(" ")[0]
                                               + " on " + dateTime.split(" ")[1]);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                locationText.setText("" + weather + " at " + lat + ", " + lng + "");

                view.setTag(id);
            }



        }
    }
}
