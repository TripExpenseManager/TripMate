package com.tripmate;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class TripInfo extends AppCompatActivity {

    ListView lvPersonDetails,lvPlacesToVisit;
    TextView tvDesc;
    ArrayList<String> tripPlaces,tripPersonNames;
    ArrayList<Person> tripPersons;
    FloatingActionButton fabAddPerson;
    TripModel trip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);




        Intent intent = getIntent();
        trip = new TripModel();
        trip.setTrip_name(intent.getStringExtra("TripName"));
        trip.setTrip_places(intent.getStringExtra("TripPlaces"));
        trip.setTrip_desc(intent.getStringExtra("TripDesc"));
        trip.setTrip_date(intent.getStringExtra("TripDate"));
        trip.setTrip_amount("0");

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(trip.getTrip_name());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lvPersonDetails = (ListView) findViewById(R.id.lvPersonDetails);
        lvPlacesToVisit = (ListView) findViewById(R.id.lvPlacesToVisit);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        fabAddPerson = (FloatingActionButton) findViewById(R.id.fabAddPerson);

        // Persons
        tripPersons = new ArrayList<>();
        tripPersonNames = new ArrayList<>();
        ArrayAdapter<String> personsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tripPersonNames);
        lvPersonDetails.setAdapter(personsAdapter);


        // Desc
        tvDesc.setText(trip.getTrip_desc());

        // Places
        tripPlaces = new ArrayList<>(Arrays.asList(trip.getTrip_places().split(",")));
        ArrayAdapter<String> placesAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tripPlaces);
        lvPlacesToVisit.setAdapter(placesAdapter);


       // Add Person
        fabAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TripInfo.this);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_ok:
                MainActivity.AppBase.addTrip(trip);
                MainActivity.AppBase.addPersons(trip.getTrip_name(),tripPersons);
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_info,menu);
        return  true;
    }
}
