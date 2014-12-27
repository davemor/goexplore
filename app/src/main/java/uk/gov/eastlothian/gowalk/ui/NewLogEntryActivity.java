package uk.gov.eastlothian.gowalk.ui;

import android.app.Activity;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.util.Calendar;

import uk.gov.eastlothian.gowalk.R;

public class NewLogEntryActivity extends FragmentActivity {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_log_entry, menu);
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
    public static class NewLogEntryFragment extends Fragment {

        // view
        Button dateButton;
        Button timeButton;
        Spinner weatherSpinner;

        // data
        int year, month, day; // date
        int hour, minute; // time

        public NewLogEntryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_log_entry, container, false);

            dateButton = (Button) rootView.findViewById(R.id.new_log_select_date_button);
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialog(view);
                }
            });
            setDateToToday();

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

            return rootView;
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

            // update the view - note: month is zero indexed
            String label = "" + day + " / " + (month + 1) + " / " + year;
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
            String label = "" + hour + ":" + minute;
            timeButton.setText(label);
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
