package com.tripmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class AppIntroActivity extends AppIntro {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
      /*  addSlide(firstFragment);
        addSlide(secondFragment);
        addSlide(thirdFragment);
        addSlide(fourthFragment);*/

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Trip Mate", "Enjoy the trip without any worries",R.drawable.image_no_trips, getResources().getColor(R.color.green)));
        addSlide(AppIntroFragment.newInstance("Expense Manager", "Enjoy the trip without any worries",R.drawable.image_no_expenses, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Trip Mate", "Enjoy the trip without any worries",R.drawable.image_no_trips, getResources().getColor(R.color.colorAccent)));
        addSlide(AppIntroFragment.newInstance("Expense Manager", "Enjoy the trip without any worries",R.drawable.image_no_expenses, getResources().getColor(R.color.red)));

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
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(AppIntroActivity.this);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt("should_display", 0);
        editor.commit();

        Intent intent = new Intent(AppIntroActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(AppIntroActivity.this);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt("should_display", 0);
        editor.commit();
        Intent intent = new Intent(AppIntroActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
