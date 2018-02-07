package com.tripmate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ReportRequestActivity extends AppCompatActivity {

    String from;
    TextInputLayout tilEmailId,tilMessage;
    Button submitBtn;

    String emailId = "",message = "";

    ProgressDialog pd;

    public static final String URL_FEATURE = "http://tripmateandroid.000webhostapp.com/requestfeature.php";
    public static final String URL_BUG = "gs://tripmate-172019.appspot.com/report_bug.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);

        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);

        tilEmailId = (TextInputLayout) findViewById(R.id.tilEmailId);
        tilMessage = (TextInputLayout) findViewById(R.id.tilMessage);
        submitBtn = (Button) findViewById(R.id.submitBtn);

        String gdrive_backup_account = app_preferences.getString("gdrive_backup_account", "no");

        if (!gdrive_backup_account.equalsIgnoreCase("no")) {
            tilEmailId.getEditText().setText(gdrive_backup_account);
        }

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        if(from.equalsIgnoreCase("report")){
            getSupportActionBar().setTitle("Report a Bug");
            tilMessage.setHint("Report a Bug");
        }else if(from.equalsIgnoreCase("request")){
            getSupportActionBar().setTitle("Request a Feature");
            tilMessage.setHint("Request a Feature");
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    emailId = tilEmailId.getEditText().getText().toString();
                    message = tilMessage.getEditText().getText().toString().trim();

                    if(!isValidEmailAddress(emailId)){
                        tilEmailId.setError("Please Enter a valid email address");
                    }else if(message.length() == 0){
                        tilMessage.setError("Please Enter something");
                    }
                    else{
                      sendRequest();
                    }

                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Please check your Internet connection.",Snackbar.LENGTH_LONG).show();
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


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void sendRequest(){
        pd.show();

        hideSoftKeyboard();

        Map<String,String> mp = new HashMap<>();
        mp.put("email",emailId);
        mp.put("message",message);
        String URL_FINAL = "";
        if(from.equalsIgnoreCase("report")){
            URL_FINAL = URL_BUG;
        }else if(from.equalsIgnoreCase("request")){
            URL_FINAL = URL_FEATURE;
        }
        AndroidNetworking.post(URL_FINAL)
                .addBodyParameter(mp)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // do anything with response
                        tilMessage.getEditText().setText("");
                        tilEmailId.getEditText().setText("");

                        ReportRequestActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                            }
                        });

                        final AlertDialog.Builder dialog = new AlertDialog.Builder(ReportRequestActivity.this);

                        dialog.setMessage("Thank you for your valuable feedback.");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.dismiss();
                            }
                        });
                        dialog.show();


                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        if(error != null && !error.getErrorDetail().equalsIgnoreCase("requestCancelledError")){
                            Snackbar.make(findViewById(android.R.id.content), error.getErrorCode() +" " + error.getResponse() + " " + error.getErrorBody()+ " "+ error.getErrorDetail(), Snackbar.LENGTH_LONG).show();
                        }

                        ReportRequestActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                            }
                        });
                    }
                });

    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return true;
        }
    }
}
