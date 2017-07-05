package com.tripmate;

import android.app.models.PersonModel;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class TripDesk extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    
    String trip_id,trip_name,trip_date;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_desk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent tripIdIntent = getIntent();
        trip_id = tripIdIntent.getStringExtra("trip_id");
        trip_name = tripIdIntent.getStringExtra("trip_name");
        trip_date = tripIdIntent.getStringExtra("trip_date");


        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(trip_name);
            getSupportActionBar().setSubtitle(trip_date);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);

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

                }else if(tab.getPosition() == 1){

                    fab.show();
                    fab.setVisibility(View.VISIBLE);
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

                }else if(tab.getPosition() == 2){

                    fab.hide();
                    fab.setVisibility(View.GONE);

                }else if(tab.getPosition() == 3){

                    fab.hide();
                    fab.setVisibility(View.GONE);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        
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
            shortcutIntent.setAction(Intent.ACTION_MAIN);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, trip_name);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(),R.drawable.icon_info));

            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(addIntent);
            Snackbar.make(fab, "Trip shortcut is added to the Home screen successfully", Snackbar.LENGTH_LONG).show();
        }
        else if(item.getItemId() == R.id.action_add_person){


            final View view = getLayoutInflater().inflate(R.layout.layout_add_person, null);
            final TextInputLayout tilPersonName, tilPersonDeposit, tilPersonMobile, tilPersonEmail;
            tilPersonName = (TextInputLayout) view.findViewById(R.id.tilPersonName);
            tilPersonDeposit = (TextInputLayout) view.findViewById(R.id.tilPersonDeposit);
            tilPersonMobile = (TextInputLayout) view.findViewById(R.id.tilPersonMobile);
            tilPersonEmail = (TextInputLayout) view.findViewById(R.id.tilPersonEmail);

            TextView note1 = (TextView) view.findViewById(R.id.note1);
            TextView note2 = (TextView) view.findViewById(R.id.note2);
            note1.setVisibility(View.VISIBLE);
            note2.setVisibility(View.VISIBLE);


            final AlertDialog alertDialog = new AlertDialog.Builder(TripDesk.this).setView(view).setTitle("Add Person")
                    .setPositiveButton("OK",null)
                    .setNegativeButton("CANCEL", null)
                    .create();

            alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationRightToLeft);
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

                        PersonModel personModel = new PersonModel();

                        String tempName = tilPersonName.getEditText().getText().toString().trim().substring(0, 1).toUpperCase() + tilPersonName.getEditText().getText().toString().trim().substring(1);
                        personModel.setName(tempName);

                        personModel.setTrip_id(trip_id);

                        if(tilPersonDeposit.getEditText().getText().toString().equalsIgnoreCase("")){
                            personModel.setDeposit(0.0);
                        }else{
                            personModel.setDeposit(Double.valueOf(tilPersonDeposit.getEditText().getText().toString()));
                        }
                        personModel.setMobile(tilPersonMobile.getEditText().getText().toString());
                        personModel.setEmail(tilPersonEmail.getEditText().getText().toString());
                        personModel.setAdmin(0);

                        dbHelper.addPersonInMiddel(personModel);

                        setupViewPager();

                        alertDialog.dismiss();
                    }
                }
                }
            });


        }
        else if(item.getItemId() == R.id.action_trip_details){
            //view trip details
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
            dialog1.getWindow().setWindowAnimations(R.style.DialogAnimationUpDown);
            dialog1.show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Persons(), "PERSONS");
        adapter.addFragment(new Expenses(), "EXPENSES");
        adapter.addFragment(new Statistics(), "STATISTICS");
        adapter.addFragment(new Notes(), "NOTES");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }








}
