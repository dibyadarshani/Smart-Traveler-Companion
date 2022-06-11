package com.example.smarttravelercompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;

public class createCustTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    TextView sdate,stime,ctans;
    Button ddate,dtime;
    int dt, mon, yr, hr, min;
    String cdate,ctime,splace,dplace;
    SharedPreferences sharedpreferences;
    EditText tripnotes, tripname, stpoint, dtpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cust_trip);
        /*
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.spf);

        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                splace=place.getName();
                Log.i("placesapi", "Start Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("placesapi", "An error occurred: " + status);
            }
        });

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment2 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.dpf);

        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                dplace=place.getName();
                Log.i("placesapi", "Dest Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("placesapi", "An error occurred: " + status);
            }
        });
         */

        tripnotes = findViewById(R.id.tripnotes);
        tripname = findViewById(R.id.tripname);
        sdate = findViewById(R.id.showdate);
        ddate = findViewById(R.id.ddate);
        stime = findViewById(R.id.showtime);
        dtime = findViewById(R.id.dtime);
        ctans = findViewById(R.id.ctans);
        stpoint = findViewById(R.id.spoint);
        dtpoint = findViewById(R.id.dpoint);
        ddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                yr = calendar.get(Calendar.YEAR);
                mon = calendar.get(Calendar.MONTH);
                dt = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(createCustTrip.this, createCustTrip.this,yr, mon,dt);
                datePickerDialog.show();
            }
        });
        dtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                hr = c.get(Calendar.HOUR);
                min = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(createCustTrip.this, createCustTrip.this, hr, min, DateFormat.is24HourFormat(createCustTrip.this));
                timePickerDialog.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        cdate=dayOfMonth+"/"+(month+1)+"/"+year;
        sdate.setText(cdate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        ctime=hourOfDay+":"+minute;
        stime.setText(ctime);
    }

    public void addCustTrip(View view) {
        sharedpreferences = getSharedPreferences("loginID", Context.MODE_PRIVATE);
        String email = sharedpreferences.getString("email", null);
        String spoint = stpoint.getText().toString();
        String dpoint = dtpoint.getText().toString();
        String tnotes = tripnotes.getText().toString();
        String tname = tripname.getText().toString();
        String tdate = cdate;
        String ttime = ctime;
        CTrip cTrip = new CTrip();
        cTrip.setEmail(email);
        cTrip.setSpoint(spoint);
        cTrip.setDpoint(dpoint);
        cTrip.setTnotes(tnotes);
        cTrip.setTname(tname);
        cTrip.setTdate(tdate);
        cTrip.setTtime(ttime);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("smarttravelercompanion-default-rtdb");
        DatabaseReference getCustTrips = databaseReference.child("CustTrips");
        getCustTrips.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getCustTrips.push().setValue(cTrip);
                ctans.setText("Trip created");
            }
            // this will called when any problem
            // occurs in getting data
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // we are showing that error message
                Log.d("MSG", "error occured");
            }
        });

    }
}