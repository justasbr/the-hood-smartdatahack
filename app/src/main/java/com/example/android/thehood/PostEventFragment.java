package com.example.android.thehood;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostEventFragment extends android.support.v4.app.Fragment {

    private static final String LOG_TAG = "PostFragment says: ";

    private SupportMapFragment mapFragment;
    private EditText pickStartTimeButton;
    private EditText pickEndTimeButton;
    private EditText pickStartDateButton;
    private EditText pickEndDateButton;
    private TextView radiusUnitTextView;
    private GoogleMap mMap;
    //variables for showing selected date and time nicely
    private static final SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
    // Variables to store the event details
    private LatLng eventLatLng;
    //private static Calendar currentDate = Calendar.getInstance();
    private static Calendar startDate; // = Calendar.getInstance();  //= Calendar.getInstance();
    private static Calendar endDate; // = Calendar.getInstance(); // ;= Calendar.getInstance();

    public PostEventFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_event, container, false);
        registerViews(rootView);
        radiusUnitTextView = (TextView) rootView.findViewById(R.id.radius_units_textview);
        setDistanceUnits();
        return rootView;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.post_map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.post_map, mapFragment).commit();
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                ParseGeoPoint eventGeoPoint = ParseUser.getCurrentUser().getParseGeoPoint("Address");
                eventLatLng = new LatLng(eventGeoPoint.getLatitude(), eventGeoPoint.getLongitude());
                if (savedInstanceState != null) {
                    if (savedInstanceState.containsKey("eventLatLng")){
                        eventLatLng = savedInstanceState.getParcelable("eventLatLng");
                    }
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, (float) 14.5));
                mMap.addMarker(new MarkerOptions().position(eventLatLng));

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.clear();
                        eventLatLng = latLng;
                        mMap.addMarker(new MarkerOptions().position(latLng));
                    }
                });
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("eventLatLng", eventLatLng);
        super.onSaveInstanceState(outState);
    }

    private void registerViews(View v) {
        pickStartTimeButton = (EditText) v.findViewById(R.id.pick_start_time_button);
        pickEndTimeButton = (EditText) v.findViewById(R.id.pick_end_time_button);
        pickStartDateButton = (EditText) v.findViewById(R.id.pick_start_date_button);
        pickEndDateButton = (EditText) v.findViewById(R.id.pick_end_date_button);
        Button createEventButton = (Button) v.findViewById(R.id.create_event_button);

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(createEvent())
                    getActivity().finish();
            }
        });

        pickStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
        pickEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        pickStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        pickEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        // Get the calling view, so we can set its text when we update the time
        private View viewCalledFrom;

        public TimePickerFragment(View v) {
            super();
            viewCalledFrom = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current set time as the default values for the picker
            int hour;
            int minute;

            switch (viewCalledFrom.getId()) {
                case R.id.pick_start_time_button:
                    Log.v(LOG_TAG, "start time1");
                    if (startDate == null) {
                        setStartDate();
                    }
                    hour = startDate.get(Calendar.HOUR_OF_DAY);
                    minute = startDate.get(Calendar.MINUTE);
                    break;
                default: //case R.id.pick_end_time_button:
                    Log.v(LOG_TAG, "end time!");
                    if (endDate == null){
                        setEndDate();
                    }
                    hour = endDate.get(Calendar.HOUR_OF_DAY);
                    minute = endDate.get(Calendar.MINUTE);
                    break;
                //default:
                //    Log.v(LOG_TAG, "wtf?!");
                //    c = Calendar.getInstance();
            }

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }



        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String timeText = new String();
            switch (viewCalledFrom.getId()) {
                case R.id.pick_start_time_button:
                    startDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startDate.set(Calendar.MINUTE, minute);
                    timeText = sdf_time.format(startDate.getTime());
                    Log.v(LOG_TAG, startDate.toString());
                    break;
                case R.id.pick_end_time_button:
                    endDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endDate.set(Calendar.MINUTE, minute);
                    timeText = sdf_time.format(endDate.getTime());
                    break;
                default:
                    Log.v(LOG_TAG, "What the actual fuck?!@!");
            }

            //String newText = "" + hourOfDay + ":" + minute;
            updateLabel((EditText) viewCalledFrom, timeText);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        // Get the calling view, so we can set its text when we update the date
        private View viewCalledFrom;

        public DatePickerFragment() {
            super();
        }

        public DatePickerFragment(View v) {
            super();
            viewCalledFrom = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current set date as the default date in the picker
            final Calendar c;
            switch (viewCalledFrom.getId()) {
                case R.id.pick_start_date_button:
                    if (startDate == null){
                        setStartDate();
                    }
                    c = startDate;
                    break;
                case R.id.pick_end_date_button:
                    if (endDate == null){
                        setEndDate();
                    }
                    c = endDate;
                    break;
                default:
                    c = Calendar.getInstance();
            }

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        public void onDateSet(DatePicker view, int year, int month, int day) {
            String DateText = new String();
            switch (viewCalledFrom.getId()) {
                case R.id.pick_start_date_button:
                    startDate.set(Calendar.YEAR, year);
                    startDate.set(Calendar.MONTH, month);
                    startDate.set(Calendar.DAY_OF_MONTH, day);
                    DateText = sdf_date.format(startDate.getTime());
                    break;
                case R.id.pick_end_date_button:
                    endDate.set(Calendar.YEAR, year);
                    endDate.set(Calendar.MONTH, month);
                    endDate.set(Calendar.DAY_OF_MONTH, day);
                    DateText = sdf_date.format(endDate.getTime());
                    break;
            }
            updateLabel((EditText) viewCalledFrom, DateText);
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(v);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(v);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    // A method used in the picker dialogs to set the labels ot the textViews
    private static void updateLabel(EditText et, String newText) {
        et.setText(newText);
    }

    private boolean createEvent() {
        String title = ((TextView) getActivity().findViewById(R.id.title_input_field))
                .getText().toString();
        String description = ((TextView) getActivity()
                .findViewById(R.id.description_input_field))
                .getText().toString();
        String radiusString = ((TextView) getActivity().findViewById(R.id.radius_input_field))
                        .getText().toString();

        boolean valid = validateEventData(title,startDate, endDate, radiusString, description);

        if(valid) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            int radius = Integer.parseInt(radiusString);

            // Make a new event, add it to the current user's posts_and_events
            ParseObject event = new ParseObject("Event");

            event.put("location", new ParseGeoPoint(eventLatLng.latitude, eventLatLng.longitude));
            event.put("title", title);
            event.put("description", description);
            event.put("startDate", startDate.getTime());
            event.put("endDate", endDate.getTime());
            event.put("visibility_radius", radius);
            event.put("author", currentUser);
            currentUser.add("events", event);
            event.saveInBackground();
            return true;
        }
        return false;
    }

    private boolean validateEventData(String title, Calendar startDate, Calendar endDate,
                                      String radius, String description) {
        if(title.isEmpty()) {
            Toast.makeText(getActivity(),"An event without a title? Come on...",Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if(startDate.compareTo(endDate) >= 0 || startDate.compareTo(Calendar.getInstance()) < 0) {
            Toast.makeText(getActivity(),"Time travellers not allowed",Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        if(radius.isEmpty()) {
            Toast.makeText(getActivity(),"Enter a radius, por favor",Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        // TODO: Decide on a maximum radius and perform a validation on that
        return true;
    }

    @Override
    public void onResume() {
        startDate = null;
        setDistanceUnits();
        super.onResume();
    }

    private void setDistanceUnits(){
        radiusUnitTextView.setText(Utility.getPreferredDistanceUnits(getActivity()));
    }

    private static void setStartDate() {
        startDate = Calendar.getInstance();
        startDate.add(Calendar.MINUTE, 10);
    }
    private static void setEndDate() {
        if (startDate == null){
            endDate = Calendar.getInstance();
        }
        else {
            endDate = (Calendar) startDate.clone();
        }
        endDate.add(Calendar.HOUR_OF_DAY, 1);
    }
}
