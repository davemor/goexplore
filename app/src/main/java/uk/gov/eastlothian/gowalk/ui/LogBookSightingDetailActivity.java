package uk.gov.eastlothian.gowalk.ui;

import android.app.ActionBar;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;

public class LogBookSightingDetailActivity extends MainMenuActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_book_sighting_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LogBookSightingDetailFragment())
                    .commit();
        }
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class LogBookSightingDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

        static final int SIGHTING_ID = 0;

        ViewGroup rootView;
        ImageView imageView;
        TextView dataTimeTextView;
        TextView locationTextView;
        TextView weatherTextView;

        public LogBookSightingDetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_log_book_sighting_detail, container, false);

            // bind the items in the view
            imageView = (ImageView) rootView.findViewById(R.id.log_book_sighting_image);
            dataTimeTextView = (TextView) rootView.findViewById(R.id.log_book_sighting_datetime);
            locationTextView = (TextView) rootView.findViewById(R.id.log_book_sighting_place);
            weatherTextView = (TextView) rootView.findViewById(R.id.log_book_sighting_weather);

            // start loading the log entry
            getLoaderManager().initLoader(SIGHTING_ID, null, this);



            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            long logEntryId = getActivity().getIntent().getLongExtra("log_entry_id", -1);
            if( logEntryId != -1) {
                Uri uri = WalksContract.LogEntry.buildLogEntrysUri(logEntryId);
                Loader<Cursor> rtnCursor = new CursorLoader(getActivity(), uri, null, null, null, null);
                return rtnCursor;
            } else {
                return null; // TODO: How should we handle this? Exception?
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int latIdx = cursor.getColumnIndex(WalksContract.LogEntry.COLUMN_LAT);
                int lngIdx = cursor.getColumnIndex(WalksContract.LogEntry.COLUMN_LNG);
                int dateTimeIdx = cursor.getColumnIndex(WalksContract.LogEntry.COLUMN_DATATIME);
                int weatherIdx = cursor.getColumnIndex(WalksContract.LogEntry.COLUMN_WEATHER);
                int imagePathIdx = cursor.getColumnIndex(WalksContract.LogEntry.COLUMN_IMAGE);

                String lat = cursor.getString(latIdx);
                String lng = cursor.getString(lngIdx);
                String dateTime = cursor.getString(dateTimeIdx);
                String weather = cursor.getString(weatherIdx);
                String imagePath = cursor.getString(imagePathIdx);

                SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    iso8601Format.parse(dateTime);
                    final Calendar calendar = iso8601Format.getCalendar();

                    SimpleDateFormat prettyFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                    dateTime = prettyFormat.format(calendar.getTime());

                    dataTimeTextView.setText("Sighting at " + dateTime.split(" ")[0]
                                                   + " on " + dateTime.split(" ")[1]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                weatherTextView.setText(weather);
                locationTextView.setText(lat + ", " + lng + "");

                // check if the file exists for the path
                File file = new File(imagePath);
                if(file.exists()) {
                    setImageView(imagePath);
                }
                // set up the camera button
                /*
                Button cameraButton = (Button) rootView.findViewById(R.id.log_book_sighting_camera_button);
                cameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                */
            }
        }

        private void setImageView(String path) {
            // TODO: Can this be done in asyc
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
            imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
