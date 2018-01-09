package com.tripmate;

import android.app.Activity;
import android.app.models.PersonModel;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


public class TripDesk extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView header_image;
    
    String trip_id = "",trip_name = "",trip_date = "",trip_url = "";

    FloatingActionButton fab;
    FloatingActionMenu fabMenu;
    public static int TRIP_DETAILS_ACTIVITY = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

        setContentView(R.layout.activity_trip_desk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        header_image = (ImageView) findViewById(R.id.header_image);

        Intent tripIdIntent = getIntent();
        trip_id = tripIdIntent.getStringExtra("trip_id");
        trip_name = tripIdIntent.getStringExtra("trip_name");
        trip_date = tripIdIntent.getStringExtra("trip_date");
        trip_url = tripIdIntent.getStringExtra("trip_url");


        if(trip_url.equalsIgnoreCase("")){
            Picasso.with(TripDesk.this)
                    .load(R.drawable.image_placeholder)
                    .fit().centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(header_image);
        }else{
            Picasso.with(TripDesk.this)
                    .load(trip_url)
                    .fit().centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(header_image);
        }


        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(trip_name);
            getSupportActionBar().setSubtitle(trip_date);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripDesk.this,AddExpense.class);
                intent.putExtra("trip_id",trip_id);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TripDesk.this, fab, getString(R.string.activity_image_trans1));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }

            }
        });


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(TripDesk.this);
        if(dataBaseHelper.isTripExists(trip_id)){
            setupViewPager();
        }else{
            Toast.makeText(this, "Trip doesn't exist", Toast.LENGTH_SHORT).show();
            finish();
        }


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                if(tab.getPosition() == 0){

                    fab.show();
                    fab.setVisibility(View.VISIBLE);

                    fabMenu.showMenuButton(false);
                    fabMenu.setVisibility(View.GONE);

                }else if(tab.getPosition() == 1){

                    fab.show();
                    fab.setVisibility(View.VISIBLE);

                    fabMenu.showMenuButton(false);
                    fabMenu.setVisibility(View.GONE);

                }else if(tab.getPosition() == 2){

                    fab.hide();
                    fab.setVisibility(View.GONE);

                    fabMenu.showMenuButton(false);
                    fabMenu.setVisibility(View.GONE);

                }else if(tab.getPosition() == 3){

                    fab.hide();
                    fab.setVisibility(View.GONE);

                    fabMenu.showMenuButton(true);
                    fabMenu.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        TapTargetSequence tapTargetSequence = new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(fab, "Add Expenses","You can add expenses covered in your trip from here. Better add expenses at the time of expending it, rather than adding them at the end of the trip!").cancelable(false)
                                .descriptionTextColor(R.color.white)
                                .titleTextColor(R.color.white)
                                .transparentTarget(true)
                                .textTypeface(Typeface.SANS_SERIF)
                        )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(TripDesk.this);
                        SharedPreferences.Editor editor = app_preferences.edit();
                        editor.putInt("should_display_tripdesk", 0);
                        editor.apply();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(TripDesk.this);
                        SharedPreferences.Editor editor = app_preferences.edit();
                        editor.putInt("should_display_tripdesk", 0);
                        editor.apply();
                    }
                });
        tapTargetSequence.considerOuterCircleCanceled(false);

        SharedPreferences app_preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        int should_display_tripdesk = app_preferences1.getInt("should_display_tripdesk",1);
        if(should_display_tripdesk == 1){
            tapTargetSequence.start();
        }
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TRIP_DETAILS_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                trip_date = data.getStringExtra("tripDate");
                trip_name = data.getStringExtra("tripName");
                if(getSupportActionBar()!=null){
                    getSupportActionBar().setTitle(trip_name);
                    getSupportActionBar().setSubtitle(trip_date);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result

            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_desk_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        else if(item.getItemId() == R.id.action_create_short_cut){

            //Adding shortcut for TripDesk on Home screen
            Intent shortcutIntent = new Intent(getApplicationContext(),TripDesk.class);

            //put Extra
            shortcutIntent.putExtra("trip_id",trip_id);
            shortcutIntent.putExtra("trip_name",trip_name);
            shortcutIntent.putExtra("trip_date",trip_date);
            shortcutIntent.putExtra("trip_url",trip_url);
            shortcutIntent.setAction(Intent.ACTION_MAIN);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, trip_name);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(),R.drawable.icon_shortcut));

            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(addIntent);
            Snackbar.make(fab, "Trip shortcut is added to the Home screen successfully", Snackbar.LENGTH_LONG).show();
        }
        else if(item.getItemId() == R.id.action_add_person){


            final View view = getLayoutInflater().inflate(R.layout.layout_add_person, null);
            final TextInputLayout tilPersonName, tilPersonDeposit, tilPersonMobile, tilPersonEmail;
            final AutoCompleteTextView actvPersonName,actvPersonDeposit,actvPersonMobile,actvPersonEmail;
            tilPersonName = (TextInputLayout) view.findViewById(R.id.tilPersonName);
            tilPersonDeposit = (TextInputLayout) view.findViewById(R.id.tilPersonDeposit);
            tilPersonMobile = (TextInputLayout) view.findViewById(R.id.tilPersonMobile);
            tilPersonEmail = (TextInputLayout) view.findViewById(R.id.tilPersonEmail);


            TextView note1 = (TextView) view.findViewById(R.id.note1);
            TextView note2 = (TextView) view.findViewById(R.id.note2);
            note1.setVisibility(View.VISIBLE);
            note2.setVisibility(View.VISIBLE);

            actvPersonName = (AutoCompleteTextView) view.findViewById(R.id.actvPersonName);
            actvPersonDeposit = (AutoCompleteTextView) view.findViewById(R.id.actvPersonDeposit);
            actvPersonMobile = (AutoCompleteTextView) view.findViewById(R.id.actvPersonMobile);
            actvPersonEmail = (AutoCompleteTextView) view.findViewById(R.id.actvPersonEmail);

            DataBaseHelper db = new DataBaseHelper(TripDesk.this);
            ArrayList<PersonModel> allPersons = db.getAllPersons();

            final ArrayList<String> allPersonsNames = new ArrayList<>();
            final ArrayList<String> allPersonsMobiles= new ArrayList<>();
            final ArrayList<String> allPersonsEmails = new ArrayList<>();

            for(PersonModel p : allPersons){
                allPersonsNames.add(p.getName());
                allPersonsMobiles.add(p.getMobile());
                allPersonsEmails.add(p.getEmail());
            }


            ArrayAdapter personNamesAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,allPersonsNames);
            ArrayAdapter personMobilesAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,allPersonsMobiles);
            ArrayAdapter personEmailsAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,allPersonsEmails);

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


            final AlertDialog alertDialog = new AlertDialog.Builder(TripDesk.this).setView(view).setTitle("Add Person")
                    .setPositiveButton("OK",null)
                    .setNegativeButton("CANCEL", null)
                    .create();

            alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
            alertDialog.show();


            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                if (tilPersonName.getEditText().getText().toString().equals("")) {
                    tilPersonName.setError("Enter Name");

                }
                else {

                        DataBaseHelper dbHelper = new DataBaseHelper(TripDesk.this);
                        String tripPersons[] = dbHelper.getPersonsListAsString(trip_id);
                    int res=0;
                    for(int i=0;i<tripPersons.length;i++){
                        if(tripPersons[i].equalsIgnoreCase(tilPersonName.getEditText().getText().toString().trim())){
                            tilPersonName.setError(tilPersonName.getEditText().getText().toString()+" already exist!");
                            res=1;
                        }
                    }
                    if(res==0){

                        String tempMobile = tilPersonMobile.getEditText().getText().toString();
                        String tempEmail = tilPersonEmail.getEditText().getText().toString();

                        if(!tempMobile.equalsIgnoreCase("")  && !isValidMobile(tempMobile)){
                            tilPersonMobile.setError("Please Enter a valid Mobile no.");
                        }else if(!tempEmail.equalsIgnoreCase("")  && !isValidEmailAddress(tempEmail)){
                            tilPersonEmail.setError("Please Enter a valid email address");
                        }else{
                            PersonModel personModel = new PersonModel();

                            String tempName = tilPersonName.getEditText().getText().toString().trim().substring(0, 1).toUpperCase() + tilPersonName.getEditText().getText().toString().trim().substring(1);
                            personModel.setName(tempName);

                            personModel.setTrip_id(trip_id);

                            if(tilPersonDeposit.getEditText().getText().toString().equalsIgnoreCase("")){
                                personModel.setDeposit(0.0);
                            }else{
                                personModel.setDeposit(Double.valueOf(tilPersonDeposit.getEditText().getText().toString()));
                            }
                            personModel.setMobile(tempMobile);
                            personModel.setEmail(tempEmail);
                            personModel.setAdmin(0);

                            dbHelper.addPersonInMiddle(personModel);

                            setupViewPager();

                            alertDialog.dismiss();
                        }
                    }
                }
                }
            });


        }
        else if(item.getItemId() == R.id.action_trip_details){
            //Showing Trip Details
            Intent tripDetailsIntent = new Intent(getApplicationContext(),TripDetailsActivityNew.class);

            //put Extra
            tripDetailsIntent.putExtra("trip_id",trip_id);
            tripDetailsIntent.putExtra("trip_name",trip_name);
            tripDetailsIntent.putExtra("trip_date",trip_date);
            tripDetailsIntent.putExtra("trip_url",trip_url);

            startActivityForResult(tripDetailsIntent,TRIP_DETAILS_ACTIVITY);


        }
        else if(item.getItemId() == R.id.action_delete_trip){

            AlertDialog.Builder builder1 = new AlertDialog.Builder(TripDesk.this);
            TextView customTitleTextview = new TextView(TripDesk.this);
            customTitleTextview.setTextSize(20);
            customTitleTextview.setText("Are you sure?");
            customTitleTextview.setTextColor(getResources().getColor(R.color.red));
            customTitleTextview.setPadding(10,40,10,10);
            customTitleTextview.setGravity(Gravity.CENTER);


            builder1.setCustomTitle(customTitleTextview);
            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    DataBaseHelper dataBaseHelper = new DataBaseHelper(TripDesk.this);

                    if(dataBaseHelper.deleteTrip(trip_id)){
                        Toast.makeText(TripDesk.this,"Trip "+trip_name+" deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Snackbar.make(fab,"Unexpected error occurred!", Snackbar.LENGTH_LONG).show();
                    }

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder1.setMessage("You want to delete the trip "+trip_name+"?\nYou will lose all the data about the trip(including Notes)");
            builder1.setCancelable(false);
            AlertDialog dialog1 = builder1.create();
            dialog1.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
            dialog1.show();

        }

        return super.onOptionsItemSelected(item);
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

        //return android.util.Patterns.PHONE.matcher(phone).matches();
    }


    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Persons(), "Persons");
        adapter.addFragment(new Expenses(), "Expenses");
        adapter.addFragment(new Statistics(), "Statistics");
        adapter.addFragment(new Notes(), "Notes");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
