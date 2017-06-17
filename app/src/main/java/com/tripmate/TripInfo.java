package com.tripmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class TripInfo extends AppCompatActivity {

    ListView lvPersonDetails,lvPlacesToVisit;
    TextView tvDesc;
    ArrayList<String> tripPlaces,tripPersons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);




        Intent intent = getIntent();
        TripModel trip = new TripModel();
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

        String persons = "Vineeth,Sai Krishna";
        // Persons
        tripPersons = new ArrayList<>(Arrays.asList(persons.split(",")));
        ArrayAdapter<String> personsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tripPersons);
        lvPersonDetails.setAdapter(personsAdapter);


        // Desc
        tvDesc.setText(trip.getTrip_desc());

        // Places
        tripPlaces = new ArrayList<>(Arrays.asList(trip.getTrip_places().split(",")));
        ArrayAdapter<String> placesAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tripPlaces);
        lvPlacesToVisit.setAdapter(placesAdapter);



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
