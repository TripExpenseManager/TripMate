package com.tripmate;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;
    MaterialSearchView searchView;

    NavigationView navigationView;
    TextView  nav_user;

    int prevThemeId = 1;

    int prevId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        prevThemeId = tripmate_theme_id;
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int should_display = app_preferences.getInt("should_display", 1);

        if (should_display == 1) {
            Intent intent = new Intent(MainActivity.this, AppIntroActivity.class);
            intent.putExtra("from", "mainactivity");
            startActivity(intent);
        }

        fab = (FloatingActionButton) findViewById(R.id.addTripFab);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        View hView = navigationView.getHeaderView(0);
        nav_user = (TextView) hView.findViewById(R.id.username);

        String gdrive_backup_account = app_preferences.getString("gdrive_backup_account", "no");
        // String gdrive_backup_account_username = app_preferences.getString("gdrive_backup_account_username","no");

        if (!gdrive_backup_account.equalsIgnoreCase("no")) {
            nav_user.setText(gdrive_backup_account);
            //   nav_email.setText(gdrive_backup_account);
        } else {
            nav_user.setText("TripMate");
        }




        TapTargetSequence tapTargetSequence = new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(fab, "Waiting for what?","Create a trip here")
                                .cancelable(false)
                                .descriptionTextColor(R.color.white)
                                .titleTextColor(R.color.white)
                                .transparentTarget(true)
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                      )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = app_preferences.edit();
                        editor.putInt("should_display_mainactivity", 0);
                        editor.apply();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = app_preferences.edit();
                        editor.putInt("should_display_mainactivity", 0);
                        editor.apply();
                    }
                });
        tapTargetSequence.considerOuterCircleCanceled(false);

        SharedPreferences app_preferences1 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        int should_display_mainactivity = app_preferences1.getInt("should_display_mainactivity",1);
        if(should_display_mainactivity == 1){
            tapTargetSequence.start();
        }

    }

    boolean doubleBackToExitPressedOnce = false;
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {

            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 1) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finish();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Snackbar.make(fab, "Please click BACK again to exit", Snackbar.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);


            } else {
                getSupportFragmentManager().popBackStack();
                int pos = Integer.parseInt(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName());

                Log.i("saikrishna", String.valueOf(pos));

                switch (pos) {
                    case 0:
                        fab.setVisibility(View.VISIBLE);
                        searchView.setVisibility(View.VISIBLE);
                        navigationView.setCheckedItem(R.id.nav_trips);
                        prevId = R.id.nav_trips;
                        break;
                    case 1:
                        fab.setVisibility(View.GONE);
                        searchView.setVisibility(View.GONE);
                        navigationView.setCheckedItem(R.id.nav_hotels);
                        prevId = R.id.nav_hotels;
                        break;
                    case 2:
                        fab.setVisibility(View.GONE);
                        searchView.setVisibility(View.GONE);
                        navigationView.setCheckedItem(R.id.nav_travel);
                        prevId = R.id.nav_travel;
                        break;
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String gdrive_backup_account = app_preferences.getString("gdrive_backup_account", "no");
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id", 1);
        // String gdrive_backup_account_username = app_preferences.getString("gdrive_backup_account_username","no");

        if (!gdrive_backup_account.equalsIgnoreCase("no")) {
            nav_user.setText(gdrive_backup_account);
            // nav_email.setText(gdrive_backup_account);
        } else {
            nav_user.setText("TripMate");
        }

        if(prevThemeId != tripmate_theme_id){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //donot reload on same item is reselected

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_trips) {

            fab.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);

            if (prevId != id) {
                AllTripsDisplayFragment allTripsFragment = new AllTripsDisplayFragment();
                transaction.replace(R.id.fragment_container, allTripsFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack("0");
                transaction.commit();
                prevId = id;
            }

        } else if (id == R.id.nav_hotels) {

            fab.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);

            if (prevId != id) {
                HotelBookingSitesFragment hotelsFragment = new HotelBookingSitesFragment();
                transaction.replace(R.id.fragment_container, hotelsFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack("1");
                transaction.commit();
                prevId = id;
            }

        } else if (id == R.id.nav_travel) {

            fab.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);

            if (prevId != id) {
                TravelBookingSitesFragment travelFragment = new TravelBookingSitesFragment();
                transaction.replace(R.id.fragment_container, travelFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack("2");
                transaction.commit();
                prevId = id;
            }

        }

        if (id == R.id.nav_share) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "TripMate is an app which manages expenses in your trip and ensures a smooth and hassle free trip for you");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_help){
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}