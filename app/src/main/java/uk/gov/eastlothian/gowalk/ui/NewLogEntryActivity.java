package uk.gov.eastlothian.gowalk.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.gov.eastlothian.gowalk.R;
import uk.gov.eastlothian.gowalk.data.WalksContract;

// TODO: This class needs refactored
// - There are to many places where the time gets set etc.
// - The image saving could be moved out to it's own class.
public class NewLogEntryActivity extends MainMenuActivity {

    NewLogEntryFragment newLogEntryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_log_entry);
        if (savedInstanceState == null) {
            newLogEntryFragment = new NewLogEntryFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, newLogEntryFragment)
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class NewLogEntryFragment extends Fragment {

        private static String LOG_TAG = NewLogEntryActivity.class.getSimpleName();

        // view
        Button locationButton;
        Button dateButton;
        Button timeButton;
        Spinner weatherSpinner;
        ImageView imageView;

        // data // TODO: This might not be a good default location (current location)
        LatLng location = new LatLng(55.9561054, -2.7770153);
        int year, month, day; // date
        int hour, minute; // time

        public NewLogEntryFragment() {
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // Check which request we're responding to
            if (requestCode == LocationPickerActivity.LOCATION_RESULT) {
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    double lat = data.getDoubleExtra("lat", 0.0);
                    double lng = data.getDoubleExtra("lng", 0.0);
                    location = new LatLng(lat, lng);
                    locationButton.setText("(" + lat + ", " + lng + ")");
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                // Bundle extras = data.getExtras();
                // Bitmap imageBitmap = (Bitmap) extras.get("data");
                // imageView.setImageBitmap(imageBitmap);
                galleryAddPic(); // add the current photo to the media gallery
                setPic(); // set the image view to show a scaled version of the image
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_log_entry, container, false);

            // set the title and image to the correct value for this animal
            String wildlifeName = getActivity().getIntent().getStringExtra("wildlife_name");
            TextView nameView = (TextView) rootView.findViewById(R.id.new_log_name);
            nameView.setText(wildlifeName);

            int wildlifeImageId = getActivity().getIntent().getIntExtra("wildlife_image_res_id", -1);
            if (wildlifeImageId != -1) {
                imageView = (ImageView) rootView.findViewById(R.id.new_log_wildlife_image_view);
                imageView.setImageResource(wildlifeImageId);
            }

            // set up pick location button
            locationButton = (Button) rootView.findViewById(R.id.new_log_location_of_sighting_button);
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), LocationPickerActivity.class);
                    startActivityForResult(intent, LocationPickerActivity.LOCATION_RESULT);
                }
            });

            // set up date button
            dateButton = (Button) rootView.findViewById(R.id.new_log_select_date_button);
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialog(view);
                }
            });
            setDateToToday();

            // set up time button
            timeButton = (Button) rootView.findViewById(R.id.new_log_time_button);
            timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePickerDialog(view);
                }
            });
            setTimeToToday();

            // set up weather spinner
            weatherSpinner = (Spinner) rootView.findViewById(R.id.new_log_weather_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.weather_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            weatherSpinner.setAdapter(adapter);

            // set up the log this button
            Button logButton = (Button) rootView.findViewById(R.id.new_log_button);
            logButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // add the log to the database
                    insertLogEntry();

                    Toast toast = Toast.makeText(getActivity(), "Sighting logged", Toast.LENGTH_SHORT);
                    toast.show();

                    // go to the log book activity
                    Intent intent = new Intent(getActivity(), LogBookActivity.class);
                    startActivity(intent);
                }
            });

            Button cameraButton = (Button) rootView.findViewById(R.id.new_log_camera_button);
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakePictureIntent();
                }
            });

            return rootView;
        }

        // Camera and image saving
        static final int REQUEST_TAKE_PHOTO = 2;

        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.d(LOG_TAG, "Error occurred while creating the File: " + ex);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }
        String mCurrentPhotoPath = "";

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String imageFileName = "WILDLIFE_JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }

        private void galleryAddPic() {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);
        }

        private void setPic() {
            // Get the dimensions of the View
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        }

        // update the database
        private void insertLogEntry() {
            ContentValues values = new ContentValues();

            // wildlife id
            long wildlifeId = getActivity().getIntent().getLongExtra("wildlife_id", -1);
            values.put(WalksContract.LogEntry.COLUMN_WILDLIFE_KEY, wildlifeId);

            // date time
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);
            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = iso8601Format.format(calendar.getTime());
            values.put(WalksContract.LogEntry.COLUMN_DATATIME, dateTime);

            // location
            values.put(WalksContract.LogEntry.COLUMN_LAT, location.latitude);
            values.put(WalksContract.LogEntry.COLUMN_LNG, location.longitude);

            // weather
            values.put(WalksContract.LogEntry.COLUMN_WEATHER,
                       weatherSpinner.getSelectedItem().toString());

            // image TODO: The image will need to be set to whatever later.
            String imageName = getActivity().getIntent().getStringExtra("wildlife_image_name");
            if (mCurrentPhotoPath.isEmpty()) mCurrentPhotoPath = imageName;
            values.put(WalksContract.LogEntry.COLUMN_IMAGE, mCurrentPhotoPath);

            getActivity().getContentResolver().insert(WalksContract.LogEntry.CONTENT_URI, values);
        }

        // date
        public void showDatePickerDialog(View v) {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.setNewLogEntryFragment(this);
            newFragment.show(((FragmentActivity)getActivity()).getSupportFragmentManager(), "datePicker");
        }
        public void setDate(int year, int month, int day) {
            // set the date
            this.year = year;
            this.month = month;
            this.day = day;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute); // dd/MM/yyyy
            SimpleDateFormat iso8601Format = new SimpleDateFormat("dd/MM/yyyy");
            String dateTime = iso8601Format.format(calendar.getTime());

            // update the view - note: month is zero indexed
            String label = dateTime;
            dateButton.setText(label);
        }
        void setDateToToday() {
            final Calendar c = Calendar.getInstance();
            setDate(c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
        }
        public int getYear() {
            return year;
        }
        public int getMonth() {
            return month;
        }
        public int getDay() {
            return day;
        }

        // time
        public void showTimePickerDialog(View v) {
            TimePickerFragment newFragment = new TimePickerFragment();
            newFragment.setNewLogEntryFragment(this);
            newFragment.show(((FragmentActivity)getActivity()).getSupportFragmentManager(), "timePicker");
        }
        public void setTime(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute); // dd/MM/yyyy
            SimpleDateFormat iso8601Format = new SimpleDateFormat("HH:mm");
            String dateTime = iso8601Format.format(calendar.getTime());

            timeButton.setText(dateTime);
        }
        void setTimeToToday() {
            final Calendar c = Calendar.getInstance();
            setTime(c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE));
        }
        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        NewLogEntryFragment newLogEntryFragment;
        int year, month, day;

        // config methods
        public void setNewLogEntryFragment(NewLogEntryFragment newLogEntryFragment) {
            this.newLogEntryFragment = newLogEntryFragment;
            year = newLogEntryFragment.getYear();
            month = newLogEntryFragment.getMonth();
            day = newLogEntryFragment.getDay();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            newLogEntryFragment.setDate(year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        NewLogEntryFragment newLogEntryFragment;
        int hour, minute;

        public void setNewLogEntryFragment(NewLogEntryFragment newLogEntryFragment) {
            this.newLogEntryFragment = newLogEntryFragment;
            hour = newLogEntryFragment.getHour();
            minute = newLogEntryFragment.getMinute();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            newLogEntryFragment.setTime(hourOfDay, minute);
        }
    }
}
