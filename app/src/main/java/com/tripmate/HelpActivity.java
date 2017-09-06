package com.tripmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class HelpActivity extends AppCompatActivity {

    LinearLayout gettingStartedLL,faqLL,initialTourLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Help");

        gettingStartedLL = (LinearLayout) findViewById(R.id.gettingStartedLL);
        faqLL = (LinearLayout) findViewById(R.id.faqLL);
        initialTourLL = (LinearLayout) findViewById(R.id.initialTourLL);

        gettingStartedLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this,GettingStartedActivity.class);
                startActivity(intent);
            }
        });

        faqLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this,FAQsActivity.class);
                startActivity(intent);

            }
        });

        initialTourLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this,AppIntroActivity.class);
                intent.putExtra("from","help");
                startActivity(intent);

            }
        });

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
