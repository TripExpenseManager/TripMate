package com.tripmate;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import android.app.models.PersonModel;
import android.app.models.TripModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class TripInfo_AddTrip extends AppCompatActivity {

    NonScrollListView lvPersonDetails,lvPlacesToVisit;
    TextView tvDesc;
    ArrayList<String> tripPlaces;
    ArrayList<PersonModel> tripPersonModels = new ArrayList<>();
    FloatingActionButton fabAddPerson;
    TripModel trip;

     BaseAdapter personsAdapter = null;


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
        trip.setTrip_amount(0.0);
        tripPersonModels = getIntent().getParcelableArrayListExtra("PersonsList");


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(trip.getTrip_name());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lvPersonDetails = (NonScrollListView) findViewById(R.id.lvPersonDetails);
        lvPlacesToVisit = (NonScrollListView) findViewById(R.id.lvPlacesToVisit);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        fabAddPerson = (FloatingActionButton) findViewById(R.id.fabAddPerson);

        // Persons
        personsAdapter = new PersonsAdapter(this,tripPersonModels);
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
                final AlertDialog alertDialog = new AlertDialog.Builder(TripInfo_AddTrip.this).setView(view).setTitle("Add Person")
                        .setPositiveButton("OK",null)
                        .setNegativeButton("CANCEL", null)
                        .create();
                alertDialog.show();


                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {    if (tilPersonName.getEditText().getText().toString().equals("")) {
                        Log.d("Person Added?", (tilPersonName.getEditText().getText().toString().length() > 0) + "");
                        tilPersonName.setError("Enter Name");

                    }
                    else {
                        int res=0;
                        for(int i=0;i<tripPersonModels.size();i++){
                            if(tripPersonModels.get(i).getName().equalsIgnoreCase(tilPersonName.getEditText().getText().toString().trim())){
                                tilPersonName.setError(tilPersonName.getEditText().getText().toString()+" already exist!");
                                res=1;
                            }
                        }
                        if(res==0){

                            PersonModel personModel = new PersonModel();
                            personModel.setName(tilPersonName.getEditText().getText().toString().trim());

                            if(tilPersonDeposit.getEditText().getText().toString().equalsIgnoreCase("")){
                                personModel.setDeposit(0.0);
                            }else{
                                personModel.setDeposit(Double.valueOf(tilPersonDeposit.getEditText().getText().toString()));
                            }
                            personModel.setMobile(tilPersonMobile.getEditText().getText().toString());
                            personModel.setEmail(tilPersonEmail.getEditText().getText().toString());
                            personModel.setAdmin(0);
                            tripPersonModels.add(personModel);
                            // Refresh Adapter
                            personsAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
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

                if(tripPersonModels.size()!=0){

                    int res = 0;
                    for(int i=0;i<tripPersonModels.size();i++){
                        if(tripPersonModels.get(i).getAdmin() == 1){
                            res=1;
                        }
                    }

                    if(res == 1){

                        String trip_id = "TRIP"+UUID.randomUUID().toString();

                        trip.setTrip_id(trip_id);
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
                        dataBaseHelper.addTrip(trip);
                        dataBaseHelper.addPersons(trip_id, tripPersonModels);
                        Intent intent = new Intent(TripInfo_AddTrip.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(this, "Trip created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), "Please select a person as admin", Snackbar.LENGTH_LONG).show();
                    }

                    return true;
                }else{
                    Snackbar.make(findViewById(android.R.id.content), "Please add atleast one person.", Snackbar.LENGTH_LONG).show();
                    return true;
                }

            case R.id.action_edit:
                onBackPressed();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("PersonsList",tripPersonModels);
        setResult(200,intent);
        finish();

        super.onBackPressed();

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
        public View getView(final int position, View convertView, ViewGroup parent) {
           View customView  = getLayoutInflater().inflate(R.layout.card_person,parent,false);
            final TextView tvPersonName = (TextView) customView.findViewById(R.id.tvPersonName);
            ImageView ivLeftImage = (ImageView) customView.findViewById(R.id.ivLeftImage);
            LinearLayout personLL = (LinearLayout) customView.findViewById(R.id.personLL);




            personLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tripPersonModels.get(position).setAdmin(1);
                    Toast.makeText(mContext,tripPersonModels.get(position).getName() + " is set as admin for the trip. Deposit amount will not account for this person.", Toast.LENGTH_SHORT).show();

                    for(int j=0;j<tripPersonModels.size();j++){
                        if(j!=position){
                            tripPersonModels.get(j).setAdmin(0);
                        }
                    }
                    if(personsAdapter!=null){
                        personsAdapter.notifyDataSetChanged();
                    }

                }
            });

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate color based on a key (same key returns the same color), useful for list/grid views
            String personName = personModels.get(position).getName();
            int color = generator.getColor(personName);
            TextDrawable drawable = TextDrawable.builder().buildRound((personName.charAt(0)+"").toUpperCase(),color);


            if(personModels.get(position).getAdmin() == 1){
                tvPersonName.setText(personName + " (Admin)");
            }else{
                tvPersonName.setText(personName);
            }

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
