package com.tripmate;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

public class GettingStartedActivity extends AppCompatActivity {

    WebView wvGettingStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wvGettingStarted = (WebView) findViewById(R.id.wvGettingStarted);
        wvGettingStarted.getSettings().setJavaScriptEnabled(true);

        if(getIntent().getStringExtra("from")!=null && getIntent().getStringExtra("from").equalsIgnoreCase("settings")){
            getSupportActionBar().setTitle("EULA");
            wvGettingStarted.loadUrl("file:///android_asset/eula.html");
        }else{
            getSupportActionBar().setTitle("Getting Started");
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
