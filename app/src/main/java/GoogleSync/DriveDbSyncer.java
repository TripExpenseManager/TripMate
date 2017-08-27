package GoogleSync;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.common.api.GoogleApiClient;
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
import com.tripmate.DataBaseHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sai Krishna on 6/29/2017.
 */

public class DriveDbSyncer {

    static GoogleApiClient mGoogleApiClient;

    static String GOOGLE_DRIVE_FILE_NAME = DataBaseHelper.DATABASE_NAME;

    private static DriveFile mfile;

    static DriveId driveId;

    static File db_file;

    static ProgressDialog progressDialog;

    Context context;


    public DriveDbSyncer(GoogleApiClient mGoogleApiClient, File db_file, Context context){

        this.mGoogleApiClient = mGoogleApiClient;
        this.db_file = db_file;
        this.context = context;
        progressDialog = new ProgressDialog(context);

    }

    public static void doDriveBackup () {
         Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(backupContentsCallback);
    }

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
                    contents.discard(mGoogleApiClient);
                }
            };

    static final private ResultCallback<DriveApi.DriveContentsResult> backupContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.i("saikrishna","Error while trying to create new file contents");
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
                // DialogFragment_Sync.dismissDialog();
                return;
            }
            mfile = result.getDriveFile();
            mfile.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, new DriveFile.DownloadProgressListener() {
                @Override
                public void onProgress(long bytesDownloaded, long bytesExpected) {
                    Log.i("saikrishna","Creating backup file... ("+bytesDownloaded+"/"+bytesExpected+")");

                    progressDialog.show();
                    progressDialog.setMessage("Backing up... Please wait\n"+bytesDownloaded+"/"+bytesExpected);
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


                    progressDialog.dismiss();
                    //  DialogFragment_Sync.dismissDialog();
                }
            });
        }
    };


}
