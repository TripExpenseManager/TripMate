package com.tripmate;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,BillingProcessor.IBillingHandler {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    FloatingActionButton fab;
    MaterialSearchView searchView;

    NavigationView navigationView;
    TextView  nav_user;

    int prevThemeId = 1;
    int prevFontId = 1;

    int prevId = 0;

    int navItem = 0;  // 0 - trips 1- hotels & travel

    int noOfTimesAppisOpened = 0;


    BillingProcessor bp;

    AlertDialog alertDialogContribute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        prevThemeId = tripmate_theme_id;
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

        int tripmate_font_id = app_preferences.getInt("tripmate_font_id",1);
        prevFontId = tripmate_font_id;

       // TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", Utils.getFontsHashMap().get(tripmate_font_id));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bp = BillingProcessor.newBillingProcessor(this, getResources().getString(R.string.google_play_public_key_billing),getResources().getString(R.string.google_play_public_merchant_id), this); // doesn't bind
        bp.initialize();

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

        int should_display_mainactivity = app_preferences.getInt("should_display_mainactivity",1);
        if(should_display_mainactivity == 1){
            tapTargetSequence.start();
        }

        noOfTimesAppisOpened = app_preferences.getInt("noOfTimesAppisOpened",0);
        if(noOfTimesAppisOpened != -1) {noOfTimesAppisOpened++;}
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt("noOfTimesAppisOpened",noOfTimesAppisOpened);
        editor.apply();

        if(should_display_mainactivity != 1 && noOfTimesAppisOpened != -1 && noOfTimesAppisOpened%20==0){
            final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(MainActivity.this);

            dialog.setMessage("Do you like TripMate? Maybe you would like to give us a review on the store!");
            dialog.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    final String appPackageName = "com.tripmate"; // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putInt("noOfTimesAppisOpened",-1);
                    editor.apply();
                    dialogInterface.cancel();
                }
            });
            dialog.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            dialog.setNeutralButton("Never", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putInt("noOfTimesAppisOpened",-1);
                    editor.apply();
                    dialogInterface.cancel();
                }
            });
            dialog.show();

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


            if (navItem == 0) {
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

                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                AllTripsDisplayFragment allTripsFragment = new AllTripsDisplayFragment();
                transaction.replace(R.id.fragment_container, allTripsFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                navItem=0;
                transaction.commit();

                fab.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                navigationView.setCheckedItem(R.id.nav_trips);
                prevId = R.id.nav_trips;


                /*getSupportFragmentManager().popBackStack();
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
                }*/
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
        int tripmate_font_id = app_preferences.getInt("tripmate_font_id", 1);
        // String gdrive_backup_account_username = app_preferences.getString("gdrive_backup_account_username","no");

        if (!gdrive_backup_account.equalsIgnoreCase("no")) {
            nav_user.setText(gdrive_backup_account);
            // nav_email.setText(gdrive_backup_account);
        } else {
            nav_user.setText("TripMate");
        }
        if(prevThemeId != tripmate_theme_id || prevFontId != tripmate_font_id ){
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
                transaction.commit();
                prevId = id;
                navItem=0;
            }

        } else if (id == R.id.nav_hotels) {

            fab.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);

            if (prevId != id) {
                HotelBookingSitesFragment hotelsFragment = new HotelBookingSitesFragment();
                transaction.replace(R.id.fragment_container, hotelsFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                prevId = id;
                navItem=1;
            }

        } else if (id == R.id.nav_travel) {

            fab.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);

            if (prevId != id) {
                TravelBookingSitesFragment travelFragment = new TravelBookingSitesFragment();
                transaction.replace(R.id.fragment_container, travelFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                prevId = id;
                navItem=1;
            }

        }

        if (id == R.id.nav_share) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "TripMate is an app which manages expenses in your trip and ensures a smooth and hassle free trip for you." +
                    "\nDownload it here : https://play.google.com/store/apps/details?id=com.tripmate");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_contribute) {
            openContributeDialog();
        }
        else if(id == R.id.nav_help){
            Intent intent = new Intent(MainActivity.this, FAQsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_rate_us){
            final String appPackageName = "com.tripmate"; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

        }
        else if(id == R.id.nav_request_feature){
            Intent intent = new Intent(MainActivity.this, ReportRequestActivity.class);
            intent.putExtra("from","request");
            startActivity(intent);
        }
        else if(id == R.id.nav_report_bug){
            Intent intent = new Intent(MainActivity.this, ReportRequestActivity.class);
            intent.putExtra("from","report");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void openContributeDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.donate_dialog_layout, null);
        final AlertDialog.Builder alertDialogBuilderContribute = new AlertDialog.Builder(this);
        alertDialogBuilderContribute.setView(promptsView);

        CardView cvContribute1,cvContribute2,cvContribute3,cvContribute4,cvContribute5,cvContribute6;

        cvContribute1 = (CardView) promptsView.findViewById(R.id.cvContribute1);
        cvContribute2 = (CardView) promptsView.findViewById(R.id.cvContribute2);
        cvContribute3 = (CardView) promptsView.findViewById(R.id.cvContribute3);
        cvContribute4 = (CardView) promptsView.findViewById(R.id.cvContribute4);
        cvContribute5 = (CardView) promptsView.findViewById(R.id.cvContribute5);
        cvContribute6 = (CardView) promptsView.findViewById(R.id.cvContribute6);

        alertDialogBuilderContribute.setCancelable(true).setNegativeButton("Cancel",null);
        alertDialogContribute = alertDialogBuilderContribute.create();
        alertDialogContribute.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        alertDialogContribute.show();

        cvContribute1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(MainActivity.this,"id_first_item");
            }
        });

        cvContribute2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(MainActivity.this,"id_second_item");
            }
        });

        cvContribute3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(MainActivity.this,"id_third_item");
            }
        });

        cvContribute4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(MainActivity.this,"id_fourth_item");
            }
        });

        cvContribute5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(MainActivity.this,"id_fifth_item");
            }
        });

        cvContribute6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(MainActivity.this,"id_sixth_item");
            }
        });




    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        alertDialogContribute.dismiss();
        Snackbar.make(fab,"Thank you for your support",Snackbar.LENGTH_LONG);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }
}