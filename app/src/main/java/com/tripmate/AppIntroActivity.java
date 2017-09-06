package com.tripmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class AppIntroActivity extends AppIntro {

    String from= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

        super.onCreate(savedInstanceState);


        from = getIntent().getStringExtra("from");

        // Note here that we DO NOT use setContentView();

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Welcome!", "Worried about managing expenses in group travel?\nHere you go!",R.drawable.image_no_trips, getResources().getColor(R.color.intro1)));
        addSlide(AppIntroFragment.newInstance("Manage your Trip Expenses", "Get a glimpse of \"everyone's\" expenses managed in realtime",R.drawable.intro_img2, getResources().getColor(R.color.intro2)));
        addSlide(AppIntroFragment.newInstance("Make a note of everything!", "Weak in memory?\nDon't worry, note everything in every trip!",R.drawable.intro_img3, getResources().getColor(R.color.intro3)));
        addSlide(AppIntroFragment.newInstance("Don't know where to book hotels and travel?", "Don't worry, we will provide you with all the details!",R.drawable.intro_img4, getResources().getColor(R.color.intro4)));

        // OPTIONAL METHODS
        // Override bar/separator color.
       /* setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));*/

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
        setSeparatorColor(getResources().getColor(R.color.white));

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);

        //setZoomAnimation();
        setFlowAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.

        if(!from.equalsIgnoreCase("settings")){
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(AppIntroActivity.this);
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putInt("should_display", 0);
            editor.apply();

            Intent intent = new Intent(AppIntroActivity.this,BackupActivity.class);
            startActivity(intent);
            finish();
        }else{
            finish();
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.

        if(!from.equalsIgnoreCase("settings")){
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(AppIntroActivity.this);
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putInt("should_display", 0);
            editor.apply();
            Intent intent = new Intent(AppIntroActivity.this,BackupActivity.class);
            startActivity(intent);
            finish();
        }else{
            finish();
        }

    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
