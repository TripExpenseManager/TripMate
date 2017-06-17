package com.tripmate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class AddTrip extends AppCompatActivity {

    TextInputLayout tilTripName,tilTripPlaces,tilTripDesc;
    ImageView ivDate;
    TextView tvDate;
    int mYear,mMonth,mDay;
    String trip_date;
    DatePickerDialog.OnDateSetListener dateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Create New Trip");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tilTripName = (TextInputLayout) findViewById(R.id.tilTripName);
        tilTripPlaces = (TextInputLayout) findViewById(R.id.tilTripPlaces);
        tilTripDesc = (TextInputLayout) findViewById(R.id.tilTripDesc);
        tvDate = (TextView)findViewById(R.id.tvDate);
        ivDate = (ImageView) findViewById(R.id.ivDate);

        // Setting of Date

        // Getting Current Date and Time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // Setting Default Time and Date
        trip_date = mDay + "-" + (mMonth + 1) + "-" + mYear;
        tvDate.setText(trip_date);

        // Date Picker
        dateSetListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                trip_date = mDay + "-" + (mMonth + 1) + "-" + mYear;
                tvDate.setText(trip_date);


            }
        };

        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTrip.this,dateSetListener,mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });






    }

    public void addTrip(){
        String trip_name,trip_places,trip_desc;
        try {
             trip_name = tilTripName.getEditText().getText().toString();
             trip_places = tilTripPlaces.getEditText().getText().toString();
             trip_desc = tilTripDesc.getEditText().getText().toString();
        }catch (Exception e){
            return;
        }
        String trip_date = tvDate.getText().toString();
        if(!validate(trip_name)){
            tilTripName.setError("Please Enter Trip Name");
        }else if(!validate(trip_places)){
            tilTripName.setErrorEnabled(false);
            tilTripPlaces.setError("Please Enter Places to Visit");
        }
        else if(!validate(trip_desc)){
            tilTripPlaces.setErrorEnabled(false);
            tilTripDesc.setError("Please Enter something about this Trip");
        }else{
            tilTripDesc.setErrorEnabled(false);
            TripModel trip = new TripModel(trip_name,trip_places,trip_desc,trip_date);
            MainActivity.AppBase.addTrip(trip);
            Intent intent = new Intent(AddTrip.this,TripInfo_AddTrip.class);
            intent.putExtra("TripName",trip.getTrip_name());
            intent.putExtra("TripPlaces",trip.getTrip_places());
            intent.putExtra("TripDesc",trip.getTrip_desc());
            intent.putExtra("TripDate",trip.getTrip_date());
            startActivity(intent);
        }

    }

    public boolean validate(String textField){
        return  textField.length()>0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_forward :
                addTrip();
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_trip,menu);
        return  true;
    }
}

