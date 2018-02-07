package com.tripmate;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SettingsActivity extends AppCompatPreferenceActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    static GoogleApiClient mGoogleApiClient;

    final int RESOLVE_CONNECTION_REQUEST_CODE = 1827;

    static String GOOGLE_DRIVE_FILE_NAME = DataBaseHelper.DATABASE_NAME;

    private static DriveFile mfile;

    static DriveId driveId;

    static File db_file;

    public static Context context;

    static Activity  act ;

    static boolean isChanged = false;


    static ProgressDialog pd;

    RelativeLayout contentSettingsRL;


    /** Directory that files are to be read from and written to **/
    protected static final File DATABASE_DIRECTORY =
            new File(Environment.getExternalStorageDirectory(),"Trip Mate");

    /** File path of Db to be imported **/
    protected static final File IMPORT_FILE =
            new File(DATABASE_DIRECTORY,"TripExpenseManager.db");


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

        super.onCreate(savedInstanceState);



        this.setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contentSettingsRL = (RelativeLayout) findViewById(R.id.contentSettingsRL);


        context = this;
        act = SettingsActivity.this;

        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mGoogleApiClient = new GoogleApiClient.Builder(SettingsActivity.this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(SettingsActivity.this)
                .addOnConnectionFailedListener(this)
                .build();

        db_file = getDbPath();


        ArrayList<String> perm = new ArrayList<>();

        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            perm.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if( !(ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)){
            perm.add(Manifest.permission.GET_ACCOUNTS);
        }
        if( !(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            perm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if(perm.size()!=0) {
            String[] s = new String[perm.size()];
            for(int i=0;i<perm.size();i++){
                s[i] = perm.get(i);
            }
            ActivityCompat.requestPermissions(this, s, 1);
        }
        getFragmentManager().beginTransaction().replace(R.id.contentSettingsRL, new MainPreferenceFragment()).addToBackStack(null).commit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Please give permissions to enjoy all the features", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if(preference.getKey().equalsIgnoreCase("pref_last_backup_gdrive")){

            }

            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private  File getDbPath() {
        return this.getDatabasePath(DataBaseHelper.DATABASE_NAME);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d("DriveDbHandler","connected");

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("gdrive_backup_account", Plus.AccountApi.getAccountName(mGoogleApiClient));
        editor.apply();

        BackupPreferenceFragment.connected(Plus.AccountApi.getAccountName(mGoogleApiClient));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("DriveDbHandler","onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("DriveDbHandler","onConnectionFailed1");
        if (connectionResult.hasResolution()) {
            Log.d("DriveDbHandler","onConnectionFailed");
            try {
                connectionResult.startResolutionForResult(SettingsActivity.this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            Log.d("DriveDbHandler","GooglePlayServicesUtil.getErrorDialog");
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),SettingsActivity.this, 0).show();
        }

    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        static Preference myPref_Backup,myPref_Eula,myPref_About,myPref_Theme,myPref_Initial_Tour,myPref_Edit_Categories;
       // static Preference myPref_Feedback,myPref_RateIt;
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            getActivity().setTitle("Settings");

            // feedback preference click listener
            myPref_Backup = findPreference("pref_backup");
            myPref_Eula = findPreference("pref_eula");
           // myPref_Feedback = findPreference("pref_send_feedback");
           // myPref_RateIt = findPreference("pref_rate_it");
            myPref_Initial_Tour = findPreference("pref_initial_tour");
            myPref_About = findPreference("pref_about_tripmate");
            myPref_Theme = findPreference("pref_theme");
            myPref_Edit_Categories = findPreference("pref_categories");


            myPref_Theme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View promptsView = li.inflate(R.layout.theme_picker_layout, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setView(promptsView);



                    ImageView light_violet = (ImageView) promptsView.findViewById(R.id.light_violet);
                    ImageView dark_violet = (ImageView) promptsView.findViewById(R.id.dark_violet);
                    ImageView darkest_violet = (ImageView) promptsView.findViewById(R.id.darkest_violet);
                    ImageView light_red = (ImageView) promptsView.findViewById(R.id.light_red);
                    ImageView dark_red = (ImageView) promptsView.findViewById(R.id.dark_red);
                    ImageView darkest_red = (ImageView) promptsView.findViewById(R.id.darkest_red);
                    ImageView light_blue = (ImageView) promptsView.findViewById(R.id.light_blue);
                    ImageView dark_blue = (ImageView) promptsView.findViewById(R.id.dark_blue);
                    ImageView darkest_blue = (ImageView) promptsView.findViewById(R.id.darkest_blue);
                    ImageView light_playstore = (ImageView) promptsView.findViewById(R.id.light_playstore);
                    ImageView dark_playstore = (ImageView) promptsView.findViewById(R.id.dark_playstore);
                    ImageView darkest_playstore = (ImageView) promptsView.findViewById(R.id.darkest_playstore);
                    ImageView light_green = (ImageView) promptsView.findViewById(R.id.light_green);
                    ImageView dark_green = (ImageView) promptsView.findViewById(R.id.dark_green);
                    ImageView darkest_green = (ImageView) promptsView.findViewById(R.id.darkest_green);
                    ImageView light_pink = (ImageView) promptsView.findViewById(R.id.light_pink);
                    ImageView dark_pink = (ImageView) promptsView.findViewById(R.id.dark_pink);
                    ImageView darkest_pink = (ImageView) promptsView.findViewById(R.id.darkest_pink);

                    ArrayList<ImageView> imageViewArrayList = new ArrayList<ImageView>();
                    imageViewArrayList.add(light_violet);
                    imageViewArrayList.add(light_red);
                    imageViewArrayList.add(light_blue);
                    imageViewArrayList.add(light_playstore);
                    imageViewArrayList.add(light_green);
                    imageViewArrayList.add(light_pink);

                    imageViewArrayList.add(dark_violet);
                    imageViewArrayList.add(dark_red);
                    imageViewArrayList.add(dark_blue);
                    imageViewArrayList.add(dark_playstore);
                    imageViewArrayList.add(dark_green);
                    imageViewArrayList.add(dark_pink);

                    imageViewArrayList.add(darkest_violet);
                    imageViewArrayList.add(darkest_red);
                    imageViewArrayList.add(darkest_blue);
                    imageViewArrayList.add(darkest_playstore);
                    imageViewArrayList.add(darkest_green);
                    imageViewArrayList.add(darkest_pink);


                    alertDialogBuilder.setTitle("Select Theme").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.setCancelable(true);
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
                    alertDialog.show();

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isChanged = true;
                            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = app_preferences.edit();
                            editor.putInt("tripmate_theme_id", (Integer) v.getTag());
                            editor.apply();
                            act.finish();
                            startActivity(new Intent(act,SettingsActivity.class));
                            alertDialog.dismiss();
                        }
                    };

                    ArrayList<Integer> imageDrawablesList = new ArrayList<Integer>();
                    imageDrawablesList.add(R.drawable.img1);
                    imageDrawablesList.add(R.drawable.img2);
                    imageDrawablesList.add(R.drawable.img3);
                    imageDrawablesList.add(R.drawable.img4);
                    imageDrawablesList.add(R.drawable.img5);
                    imageDrawablesList.add(R.drawable.img6);

                    imageDrawablesList.add(R.drawable.img7);
                    imageDrawablesList.add(R.drawable.img8);
                    imageDrawablesList.add(R.drawable.img9);
                    imageDrawablesList.add(R.drawable.img10);
                    imageDrawablesList.add(R.drawable.img11);
                    imageDrawablesList.add(R.drawable.img12);

                    imageDrawablesList.add(R.drawable.img13);
                    imageDrawablesList.add(R.drawable.img14);
                    imageDrawablesList.add(R.drawable.img15);
                    imageDrawablesList.add(R.drawable.img16);
                    imageDrawablesList.add(R.drawable.img17);
                    imageDrawablesList.add(R.drawable.img18);


                    for(int i = 0;i<imageViewArrayList.size();i++){
                        imageViewArrayList.get(i).setTag(i+1);
                        imageViewArrayList.get(i).setOnClickListener(listener);
                        Picasso.with(getActivity()).load(imageDrawablesList.get(i)).into(imageViewArrayList.get(i));
                    }

                    return false;
                }
            });

       /*     myPref_RateIt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    final String appPackageName = "com.tripmate"; // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }

                    return true;
                }
            });*/

            myPref_Initial_Tour.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent intent = new Intent(getActivity(),AppIntroActivity.class);
                    intent.putExtra("from","help");
                    startActivity(intent);

                    return true;
                }
            });

            myPref_Edit_Categories.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent intent = new Intent(getActivity(),CategoriesEdit.class);
                    startActivity(intent);

                    return true;
                }
            });

            myPref_About.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    startActivity(new Intent(context,AboutTripMateActivity.class));
                    return true;
                }
            });

            myPref_Backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    getFragmentManager().beginTransaction().replace(R.id.contentSettingsRL, new BackupPreferenceFragment()).addToBackStack(null).commit();

                    return true;
                }
            });

            myPref_Eula.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(context,GettingStartedActivity.class);
                    intent.putExtra("from","settings");
                    startActivity(intent);
                    return false;
                }
            });

  /*          myPref_Feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {


                   Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"tripmatedevelopers@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Feedback of TripMate");
                    i.putExtra(Intent.EXTRA_TEXT   , "");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });*/

        }

    }

    public static class BackupPreferenceFragment extends PreferenceFragment {

        static Preference myPrefGDriveAccountChooser,myPrefGDriveBackup,myPrefGDriveRestore,myPrefLastGDBackup,myPrefLastLocalBackup,myPrefLocalBackup,myPrefLocalRestore;
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_backup);

            getActivity().setTitle("Backup Settings");

            myPrefGDriveAccountChooser = findPreference("pref_google_drive_account");
            myPrefGDriveBackup = findPreference("pref_backup_database_gdrive");
            myPrefGDriveRestore = findPreference("pref_restore_database_gdrive");
            myPrefLastGDBackup = findPreference("pref_last_backup_gdrive");
            myPrefLastLocalBackup = findPreference("pref_last_backup_local");
            myPrefLocalBackup = findPreference("pref_backup_database_local");
            myPrefLocalRestore = findPreference("pref_restore_database_local");

            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String gdrive_backup_account = app_preferences.getString("gdrive_backup_account","no");
            String gdrive_last_backup = app_preferences.getString("gdrive_last_backup","no");
            String local_last_backup = app_preferences.getString("local_last_backup","no");

            if(gdrive_last_backup.equalsIgnoreCase("no")){
                myPrefLastGDBackup.setTitle("Google Drive : ");
            }else{
                myPrefLastGDBackup.setTitle("Google Drive : "+gdrive_last_backup);
            }

            if(local_last_backup.equalsIgnoreCase("no")){
                myPrefLastLocalBackup.setTitle("Local : ");
            }else{
                myPrefLastLocalBackup.setTitle("Local : "+local_last_backup);
            }

            if((ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)){
                if(!gdrive_backup_account.equalsIgnoreCase("no")){
                    myPrefGDriveAccountChooser.setSummary(gdrive_backup_account);
                    myPrefGDriveRestore.setEnabled(true);
                    myPrefGDriveBackup.setEnabled(true);
                }else{
                    myPrefGDriveRestore.setEnabled(false);
                    myPrefGDriveBackup.setEnabled(false);
                }
            }

            myPrefGDriveAccountChooser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    if( !(ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)){
                        ActivityCompat.requestPermissions(act,new String[]{Manifest.permission.GET_ACCOUNTS},1);
                    }else{
                        myPrefGDriveAccountChooser.setSummary("");
                        myPrefGDriveBackup.setEnabled(false);
                        myPrefGDriveRestore.setEnabled(false);

                        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = app_preferences.edit();
                        editor.putString("gdrive_backup_account", "no");
                       // editor.putString("gdrive_backup_account_username","no");
                        editor.apply();

                        if(mGoogleApiClient.isConnected()){
                            mGoogleApiClient.clearDefaultAccountAndReconnect();
                        }else{
                            mGoogleApiClient.connect();
                        }

                    }
                    return false;
                }
            });


            myPrefGDriveBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String gdrive_backup_account = app_preferences.getString("gdrive_backup_account","no");

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle("Are you sure?").setPositiveButton("Backup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            pd.show();
                            mGoogleApiClient.connect();
                            doDriveBackup();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setMessage("Backup overwrites any existing backup file in your Google Drive account!\n\nAccount : \n"+gdrive_backup_account);
                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                    dialog.show();

                    return false;
                }
            });

            myPrefGDriveRestore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String gdrive_backup_account = app_preferences.getString("gdrive_backup_account","no");

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle("Are you sure?").setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pd.show();
                            mGoogleApiClient.connect();
                            restoreDriveBackup();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setMessage("Restore overwrites any existing app data!\n\nAccount : \n"+gdrive_backup_account);
                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                    dialog.show();
                    return false;
                }
            });

            myPrefLocalBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    pd.show();
                    exportDb();
                    return false;
                }
            });

            myPrefLocalRestore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    pd.show();
                    restoreDb();
                    return false;
                }
            });
        }

        public static void connected(String email){
            myPrefGDriveAccountChooser.setSummary(email);
            myPrefGDriveBackup.setEnabled(true);
            myPrefGDriveRestore.setEnabled(true);
        }

        public static void gdBackUpCompleted(String date){
            myPrefLastGDBackup.setTitle("Google Drive : "+date);
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putString("gdrive_last_backup", date);
            editor.apply();
        }

        public static void localBackUpCompleted(String date){
            myPrefLastLocalBackup.setTitle("Local : "+date);
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putString("local_last_backup", date);
            editor.apply();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if(count ==1){
            finish();
            super.onBackPressed();
        }else{
            getFragmentManager().popBackStack();
            this.setTitle("Settings");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RESOLVE_CONNECTION_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Log.d("DriveDbHandler","RESULT_OK");
                mGoogleApiClient.connect();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void doDriveBackup() {

       // Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(backupContentsCallback);

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, GOOGLE_DRIVE_FILE_NAME))
                .build();

        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {

                int count = metadataBufferResult.getMetadataBuffer().getCount() - 1;
                Log.i("saikrishna",count+"");

                if(count != -1){

                    DriveId driveId1 = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
                    DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient,driveId1);
                    // Call to delete file.
                    driveFile.delete(mGoogleApiClient).setResultCallback(deleteCallback);
                }else{
                    Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(backupContentsCallback);
                }
            }
        });
    }


     static final private ResultCallback deleteCallback = new ResultCallback() {
         @Override
         public void onResult(@NonNull Result result) {
             Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(backupContentsCallback);
         }
     };

    public static void restoreDriveBackup() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, GOOGLE_DRIVE_FILE_NAME))
                .build();

        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {

            /*for(int i = 0 ;i < metadataBufferResult.getMetadataBuffer().getCount() ;i++) {
                debug("got index "+i);
                debug("filesize in cloud "+ metadataBufferResult.getMetadataBuffer().get(i).getFileSize());
                debug("driveId(1): "+ metadataBufferResult.getMetadataBuffer().get(i).getDriveId());
            }*/

                int count = metadataBufferResult.getMetadataBuffer().getCount() - 1;
                Log.i("saikrishna",count+"");


                if(count == -1){

                    SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String gdrive_backup_account = app_preferences.getString("gdrive_backup_account","no");

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Hey!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setMessage("You don not have any Backup in your Google Drive account!\n\nAccount : \n"+gdrive_backup_account);
                    builder.setCancelable(true);
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
                    dialog.show();

                    pd.dismiss();

                }else{

                    driveId = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();

                    //debug("driveId: " + driveId);
                    //  debug("filesize in cloud " + metadataBufferResult.getMetadataBuffer().get(0).getFileSize());
                    metadataBufferResult.getMetadataBuffer().release();

                    mfile = Drive.DriveApi.getFile(mGoogleApiClient, driveId);

                    mfile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDown, long bytesExpected) {
                            //mToast("Downloading... (" + bytesDown + "/" + bytesExpected + ")");;
                        }
                    })
                            .setResultCallback(restoreContentsCallback);

                }
            }
        });
    }

    static final private ResultCallback<DriveApi.DriveContentsResult> restoreContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i("saikrishna","Unable to open file, try again.");
                        pd.dismiss();
                        showMessage("Unable to open file, try again.");
                        return;
                    }

                    String path = db_file.getPath();

                    if ( !db_file.exists())
                        db_file.delete();

                    db_file = new File(path);

                    DriveContents contents = result.getDriveContents();
                    //debug("driveId:(2)" + contents.getDriveId());

                    try {
                        FileOutputStream fos = new FileOutputStream(db_file);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        BufferedInputStream in = new BufferedInputStream(contents.getInputStream());

                        byte[] buffer = new byte[1024];
                        int n, cnt = 0;

                        //debug("before read " + in.available());

                        while( ( n = in.read(buffer) ) > 0) {
                            bos.write(buffer, 0, n);
                            cnt += n;
                            bos.flush();
                        }

                        //debug(" read done: " + cnt);

                        bos.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //  mToast(act.getResources().getString(R.string.restoreComplete));
                    //   DialogFragment_Sync.dismissDialog();
                    Log.i("saikrishna","restore completed");
                    SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.context);
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putInt("isAppUpdatedJustNow", 1);
                    editor.apply();
                    pd.dismiss();
                    showMessage("Restore completed!");
                    contents.discard(mGoogleApiClient);
                }
            };

    static final private ResultCallback<DriveApi.DriveContentsResult> backupContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.i("saikrishna","Error while trying to create new file contents");
                showMessage("Error while trying to create new file contents");
                pd.dismiss();
                return;
            }

            String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType("db");
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(GOOGLE_DRIVE_FILE_NAME) // Google Drive File name
                    .setMimeType(mimeType)
                    .setStarred(true).build();
            // create a file on root folder
            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                    .createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                    .setResultCallback(backupFileCallback);

        }
    };

    static final private ResultCallback<DriveFolder.DriveFileResult> backupFileCallback = new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            if (!result.getStatus().isSuccess()) {
                // mToast("Error while trying to create the file, try again.");
                Log.i("saikrishna","Error while trying to create the file, try again.");
                showMessage("Error while trying to create the file, try again.");
                pd.dismiss();
                // DialogFragment_Sync.dismissDialog();
                return;
            }
            mfile = result.getDriveFile();
            mfile.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, new DriveFile.DownloadProgressListener() {
                @Override
                public void onProgress(long bytesDownloaded, long bytesExpected) {
                    Log.i("saikrishna","Creating backup file... ("+bytesDownloaded+"/"+bytesExpected+")");
                    // DialogFragment_Sync.setProgressText("Creating backup file... ("+bytesDownloaded+"/"+bytesExpected+")");

                }
            }).setResultCallback(backupContentsOpenedCallback);
        }
    };

    static final private ResultCallback<DriveApi.DriveContentsResult> backupContentsOpenedCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                // DialogFragment_Sync.dismissDialog();
                //  mToast("Error opening file, try again.");
                Log.i("saikrishna","Error opening file, try again.");
                showMessage("Error opening file, try again.");
                pd.dismiss();
                return;
            }

            //DialogFragment_Sync.setProgressText("Backing up..");
            Log.i("saikrishna","Backing up..");

            final DriveContents contents = result.getDriveContents();
            BufferedOutputStream bos = new BufferedOutputStream(contents.getOutputStream());
            byte[] buffer = new byte[1024];
            int n;

            try {
                FileInputStream is = new FileInputStream(db_file);
                BufferedInputStream bis = new BufferedInputStream(is);

                while( ( n = bis.read(buffer) ) > 0 ) {
                    bos.write(buffer, 0, n);
                    //  DialogFragment_Sync.setProgressText("Backing up...");
                    Log.i("saikrishna","Backing up..");
                }
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            contents.commit(mGoogleApiClient, null).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    // DialogFragment_Sync.setProgressText("Backup completed!");
                    //   mToast(act.getResources().getString(R.string.backupComplete));
                    Log.i("saikrishna","Backup completed!");

                    //  DialogFragment_Sync.dismissDialog();
                    pd.dismiss();
                    showMessage("Backup completed!");
                    SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.context);
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putInt("isAppUpdatedJustNow", 1);
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    BackupPreferenceFragment.gdBackUpCompleted(currentDateTimeString);
                }
            });
        }
    };

    public static void showMessage(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Hey!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage(message);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        dialog.show();
    }

    /** Saves the application database to the
     * export directory under TripExpenseManager.db **/
    protected static  boolean exportDb(){
        if( ! SdIsPresent() ) return false;

        File dbFile = context.getDatabasePath(DataBaseHelper.DATABASE_NAME);
        String filename = "TripExpenseManager.db";

        File exportDir = DATABASE_DIRECTORY;
        File file = new File(exportDir, filename);

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        try {
            file.createNewFile();
            copyFile(dbFile, file);
            showMessage("Backup completed successfully!");
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            BackupPreferenceFragment.localBackUpCompleted(currentDateTimeString);
            pd.dismiss();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Unexpected error occurred!\n Please recheck the permissions and try again.");
            pd.dismiss();
            return false;
        }
    }

    /** Replaces current database with the IMPORT_FILE if
     * import database is valid and of the correct type **/
    protected static boolean restoreDb(){
        if( ! SdIsPresent() ) return false;

        File exportFile = context.getDatabasePath(DataBaseHelper.DATABASE_NAME);
        File importFile = IMPORT_FILE;

        if (!importFile.exists()) {
            Log.d("FileBackup", "File does not exist");
            showMessage("File does not exist");
            pd.dismiss();
            return false;
        }

        try {
            exportFile.createNewFile();
            copyFile(importFile, exportFile);
            showMessage("Restore completed successfully!");
            pd.dismiss();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            pd.dismiss();
            return false;
        }
    }


    private static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    /** Returns whether an SD card is present and writable **/
    public static boolean SdIsPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

}
