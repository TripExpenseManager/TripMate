package com.tripmate;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.plus.Plus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class BackupActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    Button restoreFromLocalBtn,restoreFromDriveBtn,skipDoneButton;
    TextView chooseanaccount;

    /** Directory that files are to be read from and written to **/
    protected static final File DATABASE_DIRECTORY =
            new File(Environment.getExternalStorageDirectory(),"Trip Mate");

    /** File path of Db to be imported **/
    protected static final File IMPORT_FILE =
            new File(DATABASE_DIRECTORY,"TripExpenseManager.db");



    static GoogleApiClient mGoogleApiClient;

    final int RESOLVE_CONNECTION_REQUEST_CODE = 1827;

    static String GOOGLE_DRIVE_FILE_NAME = DataBaseHelper.DATABASE_NAME;

    private static DriveFile mfile;

    static DriveId driveId;

    static File db_file;

    public static Context context;

    static ProgressDialog pd;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        context = this;

        restoreFromLocalBtn = (Button) findViewById(R.id.restoreFromLocalBtn);
        restoreFromDriveBtn = (Button) findViewById(R.id.restoreFromDriveBtn);
        skipDoneButton = (Button) findViewById(R.id.skipDoneButton);
        chooseanaccount = (TextView) findViewById(R.id.chooseanaccount);

        mGoogleApiClient = new GoogleApiClient.Builder(BackupActivity.this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(BackupActivity.this)
                .addOnConnectionFailedListener(this)
                .build();

        db_file = getDbPath();


        chooseanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !(ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)){
                    ActivityCompat.requestPermissions(BackupActivity.this,new String[]{Manifest.permission.GET_ACCOUNTS},1);
                }else{
                    SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(BackupActivity.this);
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putString("gdrive_backup_account", "no");
                    editor.apply();

                    chooseanaccount.setText("Choose an account");

                    if(mGoogleApiClient.isConnected()){
                        mGoogleApiClient.clearDefaultAccountAndReconnect();
                    }else{
                        mGoogleApiClient.connect();
                    }

                }
            }
        });


        restoreFromLocalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((ActivityCompat.checkSelfPermission(BackupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
                    restoreLocalDb();
                }else{
                    ActivityCompat.requestPermissions(BackupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
            }
        });

        restoreFromDriveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(BackupActivity.this);
                String gdrive_backup_account = app_preferences.getString("gdrive_backup_account","no");



                if((ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)){
                    if(gdrive_backup_account.equalsIgnoreCase("no")){
                        Snackbar.make(findViewById(android.R.id.content), "Please select a Google account from which you want to restore data", Snackbar.LENGTH_LONG).show();
                    }else{
                        pd.show();
                        restoreDriveBackup();
                    }
                }else{
                    ActivityCompat.requestPermissions(BackupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }

            }
        });

        skipDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BackupActivity.this,MainActivity.class));
                finish();
            }
        });


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

        if( !(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)){
            perm.add(Manifest.permission.CALL_PHONE);
        }


        if(perm.size()!=0) {
            String[] s = new String[perm.size()];
            for(int i=0;i<perm.size();i++){
                s[i] = perm.get(i);
            }
            ActivityCompat.requestPermissions(this, s, 1);
        }

    }


    private  File getDbPath() {
        return this.getDatabasePath(DataBaseHelper.DATABASE_NAME);
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


    /** Replaces current database with the IMPORT_FILE if
     * import database is valid and of the correct type **/
    protected  boolean restoreLocalDb(){
        if( ! SdIsPresent() ) return false;

        File exportFile = BackupActivity.this.getDatabasePath(DataBaseHelper.DATABASE_NAME);
        File importFile = IMPORT_FILE;

        if (!importFile.exists()) {
            Log.d("FileBackup", "File does not exist");
            Snackbar.make(findViewById(android.R.id.content), "Sorry! You do not have any local backups", Snackbar.LENGTH_LONG).show();
            return false;
        }

        try {
            exportFile.createNewFile();
            copyFile(importFile, exportFile);
            //showMessage("Restore completed successfully!");
            Snackbar.make(findViewById(android.R.id.content), "Restore from local successful!", Snackbar.LENGTH_LONG).show();
            restoreFromLocalBtn.setText("Local restore done!");
            skipDoneButton.setText("Done");
            restoreFromDriveBtn.setText("Restore from drive");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            outChannel.close();
        }
    }

    /** Returns whether an SD card is present and writable **/
    public boolean SdIsPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("DriveDbHandler","connected");

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("gdrive_backup_account", Plus.AccountApi.getAccountName(mGoogleApiClient));
        editor.apply();

        chooseanaccount.setText(Plus.AccountApi.getAccountName(mGoogleApiClient));
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
                connectionResult.startResolutionForResult(BackupActivity.this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            Log.d("DriveDbHandler","GooglePlayServicesUtil.getErrorDialog");
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),BackupActivity.this, 0).show();
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


    public  void restoreDriveBackup() {
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
                    Snackbar.make(BackupActivity.this.findViewById(android.R.id.content), "You don not have any Backup in your Google Drive account!", Snackbar.LENGTH_LONG).show();
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

     final private ResultCallback<DriveApi.DriveContentsResult> restoreContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i("saikrishna","Unable to open file, try again.");
                        pd.dismiss();
                        Snackbar.make(BackupActivity.this.findViewById(android.R.id.content), "You don not have any Backup in your Google Drive account!", Snackbar.LENGTH_LONG).show();
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
                    pd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Restore from Drive successful!", Snackbar.LENGTH_LONG).show();
                    restoreFromDriveBtn.setText("Drive restore done!");
                    skipDoneButton.setText("Done");
                    restoreFromLocalBtn.setText("Restore from local");

                    contents.discard(mGoogleApiClient);
                }
            };



}
