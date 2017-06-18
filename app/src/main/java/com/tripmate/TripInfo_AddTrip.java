package com.tripmate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.Arrays;

public class TripInfo_AddTrip extends AppCompatActivity {

    ListView lvPersonDetails,lvPlacesToVisit;
    TextView tvDesc;
    ArrayList<String> tripPlaces;
    ArrayList<PersonModel> tripPersonModels;
    FloatingActionButton fabAddPerson;
    TripModel trip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        trip = new TripModel();
        trip.setTrip_name(intent.getStringExtra("TripName"));
        trip.setTrip_places(intent.getStringExtra("TripPlaces"));
        trip.setTrip_desc(intent.getStringExtra("TripDesc"));
        trip.setTrip_date(intent.getStringExtra("TripDate"));
        trip.setTrip_amount("0");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(trip.getTrip_name());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lvPersonDetails = (ListView) findViewById(R.id.lvPersonDetails);
        lvPlacesToVisit = (ListView) findViewById(R.id.lvPlacesToVisit);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        fabAddPerson = (FloatingActionButton) findViewById(R.id.fabAddPerson);

        if(getIntent().getBooleanExtra("is_edit",false)){
            tripPersonModels = getIntent().getParcelableArrayListExtra("PersonsList");
        }else{
            tripPersonModels = new ArrayList<>();
        }
        // Persons
        final BaseAdapter personsAdapter = new PersonsAdapter(this,tripPersonModels);
        lvPersonDetails.setAdapter(personsAdapter);


        // Desc
        tvDesc.setText(trip.getTrip_desc());

        // Places
        tripPlaces = new ArrayList<>(Arrays.asList(trip.getTrip_places().split(",")));
        ArrayAdapter<String> placesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tripPlaces);
        lvPlacesToVisit.setAdapter(placesAdapter);


        // Add PersonModel
        fabAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getLayoutInflater().inflate(R.layout.layout_add_person, null);
                final TextInputLayout tilPersonName, tilPersonDeposit, tilPersonMobile, tilPersonEmail;
                tilPersonName = (TextInputLayout) view.findViewById(R.id.tilPersonName);
                tilPersonDeposit = (TextInputLayout) view.findViewById(R.id.tilPersonDeposit);
                tilPersonMobile = (TextInputLayout) view.findViewById(R.id.tilPersonMobile);
                tilPersonEmail = (TextInputLayout) view.findViewById(R.id.tilPersonEmail);
                final AlertDialog alertDialog = new AlertDialog.Builder(TripInfo_AddTrip.this)
                        .setView(view)
                        .setTitle("Add Person")
                        .setPositiveButton("OK", null)
                        .setNegativeButton("CANCEL", null)
                        .create();
                alertDialog.show();
                Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tilPersonName.getEditText().getText().toString().equals("")) {
                            Log.d("Person Added?", (tilPersonName.getEditText().getText().toString().length() > 0) + "");
                            tilPersonName.setError("Enter Name");

                        } else {
                            PersonModel personModel = new PersonModel();
                            personModel.setName(tilPersonName.getEditText().getText().toString());
                            personModel.setDeposit(tilPersonDeposit.getEditText().getText().toString());
                            personModel.setMobile(tilPersonMobile.getEditText().getText().toString());
                            personModel.setEmail(tilPersonEmail.getEditText().getText().toString());
                            tripPersonModels.add(personModel);
                           // Refresh Adapter
                             personsAdapter.notifyDataSetChanged();

                            alertDialog.dismiss();
                        }
                    }
                });
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
                DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
                dataBaseHelper.addTrip(trip);
                dataBaseHelper.addPersons(trip.getTrip_name(), tripPersonModels);
                Intent intent = new Intent(TripInfo_AddTrip.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_edit:
                Intent intent1 = new Intent(this, AddTrip.class);
                intent1.putExtra("TripName",trip.getTrip_name());
                intent1.putExtra("TripPlaces",trip.getTrip_places());
                intent1.putExtra("TripDesc",trip.getTrip_desc());
                intent1.putExtra("TripDate",trip.getTrip_date());
                intent1.putExtra("is_edit", true);
                intent1.putExtra("PersonsList",tripPersonModels);
                startActivity(intent1);
                finish();
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

    public class PersonsAdapter extends BaseAdapter{
        Context mContext;
        ArrayList<PersonModel> personModels;

        public PersonsAdapter(Context mContext, ArrayList<PersonModel> personModels) {
            this.mContext = mContext;
            this.personModels = personModels;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           View customView  = getLayoutInflater().inflate(R.layout.card_person,parent,false);
            TextView tvPersonName = (TextView) customView.findViewById(R.id.tvPersonName);
            ImageView ivLeftImage = (ImageView) customView.findViewById(R.id.ivLeftImage);

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate color based on a key (same key returns the same color), useful for list/grid views
            String personName = personModels.get(position).getName();
            int color = generator.getColor(personName);
            TextDrawable drawable = TextDrawable.builder().buildRound((personName.charAt(0)+"").toUpperCase(),color);

            tvPersonName.setText(personName);
            ivLeftImage.setImageDrawable(drawable);
            return customView;
        }
        @Override
        public int getCount() {
            return personModels.size();
        }

        @Override
        public Object getItem(int position) {
            return personModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


    }
}
