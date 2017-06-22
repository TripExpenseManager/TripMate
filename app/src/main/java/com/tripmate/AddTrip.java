package com.tripmate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.app.models.PersonModel;
import android.app.models.TripModel;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTrip extends AppCompatActivity {

    TextInputLayout tilTripName,tilTripPlaces,tilTripDesc;
    ImageView ivDate;
    TextView tvDate;
    int mYear,mMonth,mDay;
    String trip_date;
    DatePickerDialog.OnDateSetListener dateSetListener;
    RelativeLayout dateRL;
    ArrayList<PersonModel> tripPersonModels = new ArrayList<>();

    LinearLayout commaInfoLL;

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
        commaInfoLL = (LinearLayout) findViewById(R.id.commaInfoLL);
        dateRL = (RelativeLayout) findViewById(R.id.dateRL);

        tilTripPlaces.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                commaInfoLL.setVisibility(View.VISIBLE);
            }
        });


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

        dateRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTrip.this,dateSetListener,mYear,mMonth,mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });


    }

    public void addTrip(){
        String trip_name,trip_places,trip_desc;
        try {
             trip_name = tilTripName.getEditText().getText().toString().trim().substring(0, 1).toUpperCase() + tilTripName.getEditText().getText().toString().trim().substring(1);
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
            Intent intent = new Intent(AddTrip.this,TripInfo_AddTrip.class);
            intent.putExtra("TripName",trip.getTrip_name());
            intent.putExtra("TripPlaces",trip.getTrip_places());
            intent.putExtra("TripDesc",trip.getTrip_desc());
            intent.putExtra("TripDate",trip.getTrip_date());
            intent.putExtra("PersonsList",tripPersonModels);
            startActivityForResult(intent,200);

        }

    }

    public boolean validate(String textField){
        return  textField.length()>0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==200)
        {
            tripPersonModels = data.getParcelableArrayListExtra("PersonsList");
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddTrip.this);

        dialog.setMessage("Do you want to exit without creating the trip?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog.show();

    }
}

