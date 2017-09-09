package com.tripmate;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.models.PersonWiseExpensesSummaryModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import android.app.models.PersonModel;
import android.app.models.TripModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;

public class TripInfo_AddTrip extends AppCompatActivity {

    RecyclerView rvPersonDetails;
    ArrayList<String> tripPlaces;
    ArrayList<PersonModel> tripPersonModels = new ArrayList<>();
    FloatingActionButton fabAddPerson;
    TripModel trip;

    PersonsAdapter personsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

        setContentView(R.layout.activity_trip_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);


        Intent intent = getIntent();
        trip = new TripModel();
        trip.setTrip_name(intent.getStringExtra("TripName"));
        trip.setTrip_places(intent.getStringExtra("TripPlaces"));
        trip.setTrip_date(intent.getStringExtra("TripDate"));
        trip.setTrip_amount(0.0);
        tripPersonModels = getIntent().getParcelableArrayListExtra("PersonsList");


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(trip.getTrip_name());
            getSupportActionBar().setSubtitle(trip.getTrip_date());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvPersonDetails = (RecyclerView) findViewById(R.id.rvPersonDetails);
        fabAddPerson = (FloatingActionButton) findViewById(R.id.fabAddPerson);

        // Persons
        rvPersonDetails.setLayoutManager(new LinearLayoutManager(this));
        personsAdapter = new PersonsAdapter(this);
        rvPersonDetails.setAdapter(personsAdapter);



        String placesArray[] = trip.getTrip_places().split(",");
        for(int i=0;i<placesArray.length;i++){
            placesArray[i] = placesArray[i].trim();
            placesArray[i] = placesArray[i].substring(0,1).toUpperCase() + placesArray[i].substring(1);
        }

        // Places
        tripPlaces = new ArrayList<>(Arrays.asList(placesArray));


        // Add PersonModel
        fabAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View view = getLayoutInflater().inflate(R.layout.layout_add_person, null);
                final TextInputLayout tilPersonName, tilPersonDeposit, tilPersonMobile, tilPersonEmail;
                final AutoCompleteTextView actvPersonName,actvPersonDeposit,actvPersonMobile,actvPersonEmail;

                tilPersonName = (TextInputLayout) view.findViewById(R.id.tilPersonName);
                tilPersonDeposit = (TextInputLayout) view.findViewById(R.id.tilPersonDeposit);
                tilPersonMobile = (TextInputLayout) view.findViewById(R.id.tilPersonMobile);
                tilPersonEmail = (TextInputLayout) view.findViewById(R.id.tilPersonEmail);

                actvPersonName = (AutoCompleteTextView) view.findViewById(R.id.actvPersonName);
                actvPersonDeposit = (AutoCompleteTextView) view.findViewById(R.id.actvPersonDeposit);
                actvPersonMobile = (AutoCompleteTextView) view.findViewById(R.id.actvPersonMobile);
                actvPersonEmail = (AutoCompleteTextView) view.findViewById(R.id.actvPersonEmail);

                DataBaseHelper db = new DataBaseHelper(TripInfo_AddTrip.this);
                ArrayList<PersonModel> allPersons = db.getAllPersons();

                final ArrayList<String> allPersonsNames = new ArrayList<>();
                final ArrayList<String> allPersonsMobiles= new ArrayList<>();
                final ArrayList<String> allPersonsEmails = new ArrayList<>();

                for(PersonModel p : allPersons){
                    allPersonsNames.add(p.getName());
                    allPersonsMobiles.add(p.getMobile());
                    allPersonsEmails.add(p.getEmail());
                }


                ArrayAdapter personNamesAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_list_item_1,allPersonsNames);
                ArrayAdapter personMobilesAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_list_item_1,allPersonsMobiles);
                ArrayAdapter personEmailsAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_list_item_1,allPersonsEmails);

                actvPersonName.setAdapter(personNamesAdapter);
                actvPersonMobile.setAdapter(personMobilesAdapter);
                actvPersonEmail.setAdapter(personEmailsAdapter);


                actvPersonName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String personName = ((TextView)view).getText().toString().trim();
                        int index = allPersonsNames.indexOf(personName);
                        if(index!=-1) {
                            actvPersonMobile.setText(allPersonsMobiles.get(index));
                            actvPersonEmail.setText(allPersonsEmails.get(index));
                        }
                    }
                });

                final AlertDialog alertDialog = new AlertDialog.Builder(TripInfo_AddTrip.this).setView(view).setTitle("Add Person")
                        .setPositiveButton("OK",null)
                        .setNegativeButton("CANCEL", null)
                        .create();

                alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
                alertDialog.show();


                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {    if (tilPersonName.getEditText().getText().toString().equals("")) {
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

                            String tempName = tilPersonName.getEditText().getText().toString().trim().substring(0, 1).toUpperCase() + tilPersonName.getEditText().getText().toString().trim().substring(1);
                            personModel.setName(tempName);

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

        if(tripPersonModels.size()==0){
            TapTargetView.showFor(this,
                    TapTarget.forView(findViewById(R.id.fabAddPerson), "Add persons", "Click on the plus(+) button to add persons.")
                            .cancelable(false)
                            .descriptionTextColor(R.color.white)
                            .titleTextColor(R.color.white)
                            .transparentTarget(true)
                            .textTypeface(Typeface.SANS_SERIF),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                    }
                });
        }

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
                        trip.setTotal_persons(tripPersonModels.size());
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
                        dataBaseHelper.addTrip(trip);
                        dataBaseHelper.addPersons(trip_id, tripPersonModels);
                        Intent intent = new Intent(TripInfo_AddTrip.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(this, "Trip created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Snackbar.make(fabAddPerson, "Please select a person as admin (by clicking on the name of the person)", Snackbar.LENGTH_LONG).show();
                    }

                    return true;
                }else{
                    Snackbar.make(fabAddPerson, "Please add atleast one person.", Snackbar.LENGTH_LONG).show();
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

        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);

        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_info_activity,menu);
        return  true;
    }


    class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.MyViewHolder> {


        Context mContext;

         PersonsAdapter(Context mContext) {
            this.mContext = mContext;
         }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tvPersonName,tvPhone,tvDeposit,tvEmail,textViewOptions;
            ImageView imageView;
            LinearLayout clickLL,llPhone,llDeposit,llEmail;

            MyViewHolder(View view) {
                super(view);
                tvPersonName = (TextView) view.findViewById(R.id.tvPersonName);
                tvPhone = (TextView) view.findViewById(R.id.tvPhone);
                tvDeposit = (TextView) view.findViewById(R.id.tvDeposit);
                tvEmail = (TextView) view.findViewById(R.id.tvEmail);
                textViewOptions = (TextView) view.findViewById(R.id.textViewOptions);
                imageView = (ImageView) view.findViewById(R.id.imageView);
                clickLL = (LinearLayout) view.findViewById(R.id.clickLL);
                llPhone = (LinearLayout) view.findViewById(R.id.llPhone);
                llDeposit = (LinearLayout) view.findViewById(R.id.llDeposit);
                llEmail = (LinearLayout) view.findViewById(R.id.llEmail);


            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_person_add_trip, parent, false);

            return new MyViewHolder(itemView);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            // Loading data to view holders
            final PersonModel personModel = tripPersonModels.get(holder.getAdapterPosition());
            if(personModel.getMobile().equalsIgnoreCase("")){
                holder.llPhone.setVisibility(View.GONE);
            }else{
                holder.llPhone.setVisibility(View.VISIBLE);
                holder.tvPhone.setText(personModel.getMobile());
            }
            if(personModel.getEmail().equalsIgnoreCase("")){
                holder.llEmail.setVisibility(View.GONE);
            }else{
                holder.llEmail.setVisibility(View.VISIBLE);
                holder.tvEmail.setText(personModel.getEmail());
            }
            if(personModel.getDeposit()==0.0){
                holder.llDeposit.setVisibility(View.GONE);
            }else{
                holder.llDeposit.setVisibility(View.VISIBLE);
                holder.tvDeposit.setText(personModel.getDeposit()+"");
            }

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate color based on a key (same key returns the same color), useful for list/grid views
            String personName = personModel.getName();
            int color = generator.getColor(personName);
            TextDrawable drawable = TextDrawable.builder().buildRound((personName.charAt(0)+"").toUpperCase(),color);


            if(personModel.getAdmin() == 1){
                holder.tvPersonName.setText(personName + " (Admin)");
            }else{
                holder.tvPersonName.setText(personName);
            }

            holder.imageView.setImageDrawable(drawable);


            View.OnClickListener adminClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tripPersonModels.get(holder.getAdapterPosition()).setAdmin(1);
                    String adminName=tripPersonModels.get(holder.getAdapterPosition()).getName();

                    if(adminName.length()>20){
                        adminName = adminName.substring(0,17)+"...";
                    }
                    Snackbar.make(fabAddPerson,adminName+ " is set as admin for the trip. He should look after all the money related matters.", Snackbar.LENGTH_LONG).show();

                    for(int j=0;j<tripPersonModels.size();j++){
                        if(j!=holder.getAdapterPosition()){
                            tripPersonModels.get(j).setAdmin(0);
                        }
                    }
                    if(personsAdapter!=null){
                        personsAdapter.notifyDataSetChanged();
                    }

                }
            };


            holder.clickLL.setOnClickListener(adminClickListener);
            holder.imageView.setOnClickListener(adminClickListener);


            //popup menu
            holder.textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(mContext, holder.textViewOptions);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_persons_add_trip);

                    MenuItem edit = popup.getMenu().getItem(0);
                    MenuItem delete = popup.getMenu().getItem(1);


                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    editPerson(personModel,holder.getAdapterPosition());
                                    break;
                                case R.id.delete:

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);

                                    TextView customTitleTextview = new TextView(mContext);
                                    customTitleTextview.setTextSize(20);
                                    customTitleTextview.setText("Are you sure?");
                                    customTitleTextview.setTextColor(getResources().getColor(R.color.red));
                                    customTitleTextview.setPadding(10,40,10,10);
                                    customTitleTextview.setGravity(Gravity.CENTER);

                                    builder1.setCustomTitle(customTitleTextview);

                                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            tripPersonModels.remove(holder.getAdapterPosition());
                                            personsAdapter.notifyItemRemoved(holder.getAdapterPosition());

                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder1.setMessage("You want to delete "+personModel.getName()+" from Trip?");
                                    builder1.setCancelable(false);
                                    AlertDialog dialog1 = builder1.create();
                                    dialog1.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                                    dialog1.show();

                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return tripPersonModels.size();
        }
    }

    void editPerson(final PersonModel personModel , final int position){

        final View view = getLayoutInflater().inflate(R.layout.layout_add_person, null);
        final TextInputLayout tilPersonName, tilPersonDeposit, tilPersonMobile, tilPersonEmail;
        tilPersonName = (TextInputLayout) view.findViewById(R.id.tilPersonName);
        tilPersonDeposit = (TextInputLayout) view.findViewById(R.id.tilPersonDeposit);
        tilPersonMobile = (TextInputLayout) view.findViewById(R.id.tilPersonMobile);
        tilPersonEmail = (TextInputLayout) view.findViewById(R.id.tilPersonEmail);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).setTitle("Edit Person")
                .setPositiveButton("OK",null)
                .setNegativeButton("CANCEL", null)
                .create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        alertDialog.show();


        tilPersonName.getEditText().setText(personModel.getName());
        tilPersonDeposit.getEditText().setText(personModel.getDeposit()+"");
        tilPersonMobile.getEditText().setText(personModel.getMobile());
        tilPersonEmail.getEditText().setText(personModel.getEmail());



        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PersonModel personModel = new PersonModel();

                String tempName = tilPersonName.getEditText().getText().toString().trim().substring(0, 1).toUpperCase() + tilPersonName.getEditText().getText().toString().trim().substring(1);
                String tempMobile = tilPersonMobile.getEditText().getText().toString();
                String tempEmail = tilPersonEmail.getEditText().getText().toString();

                if(tempName.equalsIgnoreCase("")){
                    tilPersonName.setError("Please Enter Name");
                }else if(!tempMobile.equalsIgnoreCase("")  && !isValidMobile(tempMobile)){
                    tilPersonMobile.setError("Please Enter a valid Mobile no.");
                }else if(!tempEmail.equalsIgnoreCase("")  && !isValidEmailAddress(tempEmail)){
                    tilPersonEmail.setError("Please Enter a valid email address");
                }else{
                    personModel.setName(tempName);
                    if(tilPersonDeposit.getEditText().getText().toString().equalsIgnoreCase("")){
                        personModel.setDeposit(0.0);
                    }else{
                        personModel.setDeposit(Double.valueOf(tilPersonDeposit.getEditText().getText().toString()));
                    }
                    personModel.setMobile(tempMobile);
                    personModel.setEmail(tempEmail);

                    tripPersonModels.remove(position);
                    tripPersonModels.add(position,personModel);
                    personsAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }

            }
        });
    }


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean isValidMobile(String phone) {

        boolean check;
        check = !Pattern.matches("[a-zA-Z]+", phone) && !(phone.length() < 6 || phone.length() > 13);
        return check;

    }



}

