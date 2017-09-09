package com.tripmate;


import android.app.models.TripModel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllTripsDisplayFragment extends Fragment{

    GridView trip_grid_view;

    ArrayList<TripModel> trip_array_list = new ArrayList<>();



    TripAdapter grid_view_adapter;

    FloatingActionButton fab;

    MaterialSearchView searchView;

    RelativeLayout no_trips_RL,no_trips_found_searched_RL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_trips_display, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("TripMate");
        
        setHasOptionsMenu(true);

        no_trips_RL = (RelativeLayout) view.findViewById(R.id.no_trips_RL);
        no_trips_found_searched_RL = (RelativeLayout) view.findViewById(R.id.no_trips_found_searched_RL);


        //for search suggestions (this feature is removed from the app, due to some inconvenience caused by coordinate layout)
        //String tripsStringArray[] = dataBaseHelper.getTripNamesAsStringArray();
        // searchView.setSuggestions(tripsStringArray);

        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                //transfering all contents which match the search word into tempModelList
                final ArrayList<TripModel> tempModelList = new ArrayList<>();
                for(int i=0;i<trip_array_list.size();i++){

                    if(trip_array_list.get(i).getTrip_name().toLowerCase().contains(query.toLowerCase())){
                        tempModelList.add(trip_array_list.get(i));
                    }

                }

                if(tempModelList.size() == 0){
                    no_trips_found_searched_RL.setVisibility(View.VISIBLE);
                    no_trips_RL.setVisibility(View.GONE);
                }else{
                    no_trips_found_searched_RL.setVisibility(View.GONE);
                    no_trips_RL.setVisibility(View.GONE);
                }

                //getting the previously selected sorting position which is stored in sharedpreferences
                SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                int prevSelectedPosition = app_preferences.getInt("get_sort_position",0);



                if(prevSelectedPosition ==0){

                    //sorting date wise
                    Collections.sort(tempModelList, new Comparator<TripModel>() {
                        @Override
                        public int compare(TripModel o1, TripModel o2) {


                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            Date date1 = null,date2 = null;
                            try {
                                date1 = format.parse(o1.getTrip_date());
                                date2 = format.parse(o2.getTrip_date());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            return date2.compareTo(date1);

                        }
                    });

                    grid_view_adapter  = new TripAdapter(getActivity(), tempModelList);

                    trip_grid_view.setAdapter(grid_view_adapter);

                    trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Intent intent = new Intent(getActivity(),TripDesk.class);
                            intent.putExtra("trip_id",tempModelList.get(position).getTrip_id());
                            intent.putExtra("trip_name",tempModelList.get(position).getTrip_name());
                            intent.putExtra("trip_date",tempModelList.get(position).getTrip_date());
                            intent.putExtra("trip_url",tempModelList.get(position).getImageUrl());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                                startActivity(intent, options.toBundle());
                            }
                            else {
                                startActivity(intent);
                            }

                        }
                    });

                }else if(prevSelectedPosition ==1){

                    //sorting amount wise
                    Collections.sort(tempModelList, new Comparator<TripModel>() {
                        @Override
                        public int compare(TripModel o1, TripModel o2) {
                            return o2.getTrip_amount().compareTo(o1.getTrip_amount());
                        }
                    });

                    grid_view_adapter  = new TripAdapter(getActivity(), tempModelList);

                    trip_grid_view.setAdapter(grid_view_adapter);

                    trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Intent intent = new Intent(getActivity(),TripDesk.class);
                            intent.putExtra("trip_id",tempModelList.get(position).getTrip_id());
                            intent.putExtra("trip_name",tempModelList.get(position).getTrip_name());
                            intent.putExtra("trip_date",tempModelList.get(position).getTrip_date());
                            intent.putExtra("trip_url",tempModelList.get(position).getImageUrl());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                                startActivity(intent, options.toBundle());
                            }
                            else {
                                startActivity(intent);
                            }

                        }
                    });

                }else if(prevSelectedPosition ==2){

                    //sorting name wise
                    Collections.sort(tempModelList, new Comparator<TripModel>() {
                        @Override
                        public int compare(TripModel o1, TripModel o2) {
                            return o1.getTrip_name().compareTo(o2.getTrip_name());
                        }
                    });

                    grid_view_adapter  = new TripAdapter(getActivity(), tempModelList);

                    trip_grid_view.setAdapter(grid_view_adapter);

                    trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Intent intent = new Intent(getActivity(),TripDesk.class);
                            intent.putExtra("trip_id",tempModelList.get(position).getTrip_id());
                            intent.putExtra("trip_name",tempModelList.get(position).getTrip_name());
                            intent.putExtra("trip_date",tempModelList.get(position).getTrip_date());
                            intent.putExtra("trip_url",tempModelList.get(position).getImageUrl());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                                startActivity(intent, options.toBundle());
                            }
                            else {
                                startActivity(intent);
                            }

                        }
                    });
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic



                SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                int prevSelectedPosition = app_preferences.getInt("get_sort_position",0);

                if(prevSelectedPosition ==0){
                    //saving the sorting state
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putInt("get_sort_position", 0);
                    editor.commit();

                    final ArrayList<TripModel> tempModelList = new ArrayList<>();

                    for(int i=0;i<trip_array_list.size();i++){

                        if(trip_array_list.get(i).getTrip_name().toLowerCase().contains(newText.toLowerCase())){
                            tempModelList.add(trip_array_list.get(i));
                        }

                    }

                    if(tempModelList.size() == 0){
                        no_trips_found_searched_RL.setVisibility(View.VISIBLE);
                        no_trips_RL.setVisibility(View.GONE);
                    }else{
                        no_trips_found_searched_RL.setVisibility(View.GONE);
                        no_trips_RL.setVisibility(View.GONE);
                    }


                    Collections.sort(tempModelList, new Comparator<TripModel>() {
                        @Override
                        public int compare(TripModel o1, TripModel o2) {


                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            Date date1 = null,date2 = null;
                            try {
                                date1 = format.parse(o1.getTrip_date());
                                date2 = format.parse(o2.getTrip_date());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            return date2.compareTo(date1);

                        }
                    });


                    grid_view_adapter  = new TripAdapter(getActivity(), tempModelList);

                    trip_grid_view.setAdapter(grid_view_adapter);

                    AdapterView.OnItemClickListener listener1 = new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Intent intent = new Intent(getActivity(),TripDesk.class);
                            intent.putExtra("trip_id",tempModelList.get(position).getTrip_id());
                            intent.putExtra("trip_name",tempModelList.get(position).getTrip_name());
                            intent.putExtra("trip_date",tempModelList.get(position).getTrip_date());
                            intent.putExtra("trip_url",tempModelList.get(position).getImageUrl());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                                startActivity(intent, options.toBundle());
                            }
                            else {
                                startActivity(intent);
                            }

                        }
                    };

                    trip_grid_view.setOnItemClickListener(listener1);

                }else if(prevSelectedPosition ==1){

                    //saving the sorting state
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putInt("get_sort_position", 1);
                    editor.commit();


                    final ArrayList<TripModel> tempModelList = new ArrayList<>();

                    for(int i=0;i<trip_array_list.size();i++){

                        if(trip_array_list.get(i).getTrip_name().toLowerCase().contains(newText.toLowerCase())){
                            tempModelList.add(trip_array_list.get(i));
                        }

                    }

                    if(tempModelList.size() == 0){
                        no_trips_found_searched_RL.setVisibility(View.VISIBLE);
                        no_trips_RL.setVisibility(View.GONE);
                    }else{
                        no_trips_found_searched_RL.setVisibility(View.GONE);
                        no_trips_RL.setVisibility(View.GONE);
                    }


                    Collections.sort(tempModelList, new Comparator<TripModel>() {
                        @Override
                        public int compare(TripModel o1, TripModel o2) {
                            return o2.getTrip_amount().compareTo(o1.getTrip_amount());
                        }
                    });


                    grid_view_adapter  = new TripAdapter(getActivity(), tempModelList);

                    trip_grid_view.setAdapter(grid_view_adapter);

                    AdapterView.OnItemClickListener listener1 = new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Intent intent = new Intent(getActivity(),TripDesk.class);
                            intent.putExtra("trip_id",tempModelList.get(position).getTrip_id());
                            intent.putExtra("trip_name",tempModelList.get(position).getTrip_name());
                            intent.putExtra("trip_date",tempModelList.get(position).getTrip_date());
                            intent.putExtra("trip_url",tempModelList.get(position).getImageUrl());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                                startActivity(intent, options.toBundle());
                            }
                            else {
                                startActivity(intent);
                            }

                        }
                    };

                    trip_grid_view.setOnItemClickListener(listener1);

                }else if(prevSelectedPosition ==2){
                    //saving the sorting state
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putInt("get_sort_position", 2);
                    editor.commit();


                    final ArrayList<TripModel> tempModelList = new ArrayList<>();

                    for(int i=0;i<trip_array_list.size();i++){

                        if(trip_array_list.get(i).getTrip_name().toLowerCase().contains(newText.toLowerCase())){
                            tempModelList.add(trip_array_list.get(i));
                        }

                    }

                    if(tempModelList.size() == 0){
                        no_trips_found_searched_RL.setVisibility(View.VISIBLE);
                        no_trips_RL.setVisibility(View.GONE);
                    }else{
                        no_trips_found_searched_RL.setVisibility(View.GONE);
                        no_trips_RL.setVisibility(View.GONE);
                    }


                    Collections.sort(tempModelList, new Comparator<TripModel>() {
                        @Override
                        public int compare(TripModel o1, TripModel o2) {
                            return o1.getTrip_name().compareTo(o2.getTrip_name());
                        }
                    });


                    grid_view_adapter  = new TripAdapter(getActivity(), tempModelList);

                    trip_grid_view.setAdapter(grid_view_adapter);

                    AdapterView.OnItemClickListener listener1 = new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Intent intent = new Intent(getActivity(),TripDesk.class);
                            intent.putExtra("trip_id",tempModelList.get(position).getTrip_id());
                            intent.putExtra("trip_name",tempModelList.get(position).getTrip_name());
                            intent.putExtra("trip_date",tempModelList.get(position).getTrip_date());
                            intent.putExtra("trip_url",tempModelList.get(position).getImageUrl());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                                startActivity(intent, options.toBundle());
                            }
                            else {
                                startActivity(intent);
                            }

                        }
                    };

                    trip_grid_view.setOnItemClickListener(listener1);
                }


                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                if(trip_array_list.size() == 0){
                    no_trips_found_searched_RL.setVisibility(View.GONE);
                    no_trips_RL.setVisibility(View.VISIBLE);
                }else{
                    no_trips_found_searched_RL.setVisibility(View.GONE);
                    no_trips_RL.setVisibility(View.GONE);
                }
            }
        });

        fab = (FloatingActionButton) getActivity().findViewById(R.id.addTripFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(),AddTripNew.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), fab, getString(R.string.activity_image_trans2));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }

            }
        });

        trip_grid_view = (GridView) view.findViewById(R.id.trip_grid_view);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        trip_array_list = dataBaseHelper.getTripsData();

        grid_view_adapter  = new TripAdapter(getActivity(), trip_array_list);

        trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getActivity(),TripDesk.class);
                intent.putExtra("trip_id",trip_array_list.get(position).getTrip_id());
                intent.putExtra("trip_name",trip_array_list.get(position).getTrip_name());
                intent.putExtra("trip_date",trip_array_list.get(position).getTrip_date());
                intent.putExtra("trip_url",trip_array_list.get(position).getImageUrl());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }

            }
        });

        trip_grid_view.setAdapter(grid_view_adapter);

        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(100);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.5f);
        trip_grid_view.setLayoutAnimation(controller);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            trip_grid_view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY < oldScrollY && fab.isShown())
                    {
                        fab.hide();
                    }else{

                        fab.show();

                    }
                }
            });
        }

        new getImageUrlConnection().execute();

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {

            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        //setting the search menu item to the search bar
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);


        //retrieves the last selected icon_sort-by position, which is saved in sharedpreferencse and display the corresponding result
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int prevSelectedPosition = app_preferences.getInt("get_sort_position",0);

        if(prevSelectedPosition ==0){
            MenuItem actionRestart = menu.findItem(R.id.menu_sort_by_date);
            onOptionsItemSelected(actionRestart);
        }else if(prevSelectedPosition ==1){
            MenuItem actionRestart = menu.findItem(R.id.menu_sort_by_amount);
            onOptionsItemSelected(actionRestart);
        }else if(prevSelectedPosition ==2){
            MenuItem actionRestart = menu.findItem(R.id.menu_sort_alphabetically);
            onOptionsItemSelected(actionRestart);
        }
        
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            return true;
        }
        else if(id == R.id.menu_sort_by_date){
            item.setChecked(true);

            //saving the sorting state
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putInt("get_sort_position", 0);
            editor.commit();


            if(trip_array_list.size() == 0){
                no_trips_found_searched_RL.setVisibility(View.GONE);
                no_trips_RL.setVisibility(View.VISIBLE);
            }else{
                no_trips_found_searched_RL.setVisibility(View.GONE);
                no_trips_RL.setVisibility(View.GONE);
            }

            //sorting date wise
            Collections.sort(trip_array_list, new Comparator<TripModel>() {
                @Override
                public int compare(TripModel o1, TripModel o2) {


                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = null,date2 = null;
                    try {
                        date1 = format.parse(o1.getTrip_date());
                        date2 = format.parse(o2.getTrip_date());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return date2.compareTo(date1);

                }
            });

            grid_view_adapter  = new TripAdapter(getActivity(), trip_array_list);

            trip_grid_view.startLayoutAnimation();

            trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    Intent intent = new Intent(getActivity(),TripDesk.class);
                    intent.putExtra("trip_id",trip_array_list.get(position).getTrip_id());
                    intent.putExtra("trip_name",trip_array_list.get(position).getTrip_name());
                    intent.putExtra("trip_date",trip_array_list.get(position).getTrip_date());
                    intent.putExtra("trip_url",trip_array_list.get(position).getImageUrl());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                        startActivity(intent, options.toBundle());
                    }
                    else {
                        startActivity(intent);
                    }

                }
            });

            trip_grid_view.setAdapter(grid_view_adapter);

            //this is for dynamically changing the menu icon on sorting(will be implemented after finding the correct menu icons)
           /* if(menu!=null){
                MenuItem parentItem = menu.findItem(R.id.action_sort);
                if (parentItem != null) {
                    parentItem.setIcon(R.drawable.ic_menu_edit);
                }
            }*/
        }
        else if(id == R.id.menu_sort_by_amount){
            item.setChecked(true);

            //saving the sorting state
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putInt("get_sort_position", 1);
            editor.commit();

            if(trip_array_list.size() == 0){
                no_trips_found_searched_RL.setVisibility(View.GONE);
                no_trips_RL.setVisibility(View.VISIBLE);
            }else{
                no_trips_found_searched_RL.setVisibility(View.GONE);
                no_trips_RL.setVisibility(View.GONE);
            }

            //sorting amount wise
            Collections.sort(trip_array_list, new Comparator<TripModel>() {
                @Override
                public int compare(TripModel o1, TripModel o2) {
                    return o2.getTrip_amount().compareTo(o1.getTrip_amount());
                }
            });

            grid_view_adapter  = new TripAdapter(getActivity(), trip_array_list);

            trip_grid_view.startLayoutAnimation();

            trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    Intent intent = new Intent(getActivity(),TripDesk.class);
                    intent.putExtra("trip_id",trip_array_list.get(position).getTrip_id());
                    intent.putExtra("trip_name",trip_array_list.get(position).getTrip_name());
                    intent.putExtra("trip_date",trip_array_list.get(position).getTrip_date());
                    intent.putExtra("trip_url",trip_array_list.get(position).getImageUrl());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                        startActivity(intent, options.toBundle());
                    }
                    else {
                        startActivity(intent);
                    }

                }
            });

            trip_grid_view.setAdapter(grid_view_adapter);


        }
        else if(id == R.id.menu_sort_alphabetically){
            item.setChecked(true);

            //saving the sorting state
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putInt("get_sort_position", 2);
            editor.commit();


            if(trip_array_list.size() == 0){
                no_trips_found_searched_RL.setVisibility(View.GONE);
                no_trips_RL.setVisibility(View.VISIBLE);
            }else{
                no_trips_found_searched_RL.setVisibility(View.GONE);
                no_trips_RL.setVisibility(View.GONE);
            }

            //sorting name wise
            Collections.sort(trip_array_list, new Comparator<TripModel>() {
                @Override
                public int compare(TripModel o1, TripModel o2) {
                    return o1.getTrip_name().compareTo(o2.getTrip_name());
                }
            });

            grid_view_adapter  = new TripAdapter(getActivity(), trip_array_list);

            trip_grid_view.startLayoutAnimation();

            trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    Intent intent = new Intent(getActivity(),TripDesk.class);
                    intent.putExtra("trip_id",trip_array_list.get(position).getTrip_id());
                    intent.putExtra("trip_name",trip_array_list.get(position).getTrip_name());
                    intent.putExtra("trip_date",trip_array_list.get(position).getTrip_date());
                    intent.putExtra("trip_url",trip_array_list.get(position).getImageUrl());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (ImageView)v.findViewById(R.id.trip_image_view), getString(R.string.activity_image_trans));
                        startActivity(intent, options.toBundle());
                    }
                    else {
                        startActivity(intent);
                    }

                }
            });

            trip_grid_view.setAdapter(grid_view_adapter);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {

        Log.i("saikrishna","onresume Called");

        //refreshing the contents of gridview by retrieving data from backend
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());

        trip_array_list.clear();
        trip_array_list.addAll(dataBaseHelper.getTripsData());

        if(trip_array_list.size() == 0){
            no_trips_found_searched_RL.setVisibility(View.GONE);
            no_trips_RL.setVisibility(View.VISIBLE);
        }else{
            no_trips_found_searched_RL.setVisibility(View.GONE);
            no_trips_RL.setVisibility(View.GONE);
        }

        //retrieves the last selected icon_sort-by position, which is saved in sharedpreferencse and display the corresponding result
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int prevSelectedPosition = app_preferences.getInt("get_sort_position",0);

        if(prevSelectedPosition ==0){
            //sorting date wise
            Collections.sort(trip_array_list, new Comparator<TripModel>() {
                @Override
                public int compare(TripModel o1, TripModel o2) {


                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = null,date2 = null;
                    try {
                        date1 = format.parse(o1.getTrip_date());
                        date2 = format.parse(o2.getTrip_date());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return date2.compareTo(date1);

                }
            });
        }else if(prevSelectedPosition ==1){
            //sorting amount wise
            Collections.sort(trip_array_list, new Comparator<TripModel>() {
                @Override
                public int compare(TripModel o1, TripModel o2) {
                    return o2.getTrip_amount().compareTo(o1.getTrip_amount());
                }
            });
        }else if(prevSelectedPosition ==2){
            //sorting name wise
            Collections.sort(trip_array_list, new Comparator<TripModel>() {
                @Override
                public int compare(TripModel o1, TripModel o2) {
                    return o1.getTrip_name().compareTo(o2.getTrip_name());
                }
            });
        }

        grid_view_adapter.notifyDataSetChanged();
        new getImageUrlConnection().execute();
        super.onResume();

    }

    //gridview adapter
    private class TripAdapter extends BaseAdapter {
        private Context context;
        ArrayList<TripModel> trip_array_list;

        TripAdapter(Context context, ArrayList<TripModel> trip_array_list) {
            this.context = context;
            this.trip_array_list = trip_array_list;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v;
            if(convertView==null)
            {
                LayoutInflater li = LayoutInflater.from(context);
                v = li.inflate(R.layout.trip_item_grid_row, null);
            }else{
                v = convertView;
            }

            TripModel model = trip_array_list.get(position);

            TextView trip_name_tv = (TextView) v.findViewById(R.id.trip_name_tv);
            TextView trip_date_tv = (TextView) v.findViewById(R.id.trip_date_tv);
            TextView trip_amount_tv = (TextView) v.findViewById(R.id.trip_amount_tv);
            TextView total_persons = (TextView) v.findViewById(R.id.total_persons);

            ImageView trip_image_view = (ImageView) v.findViewById(R.id.trip_image_view);


            if(model.getImageUrl().equalsIgnoreCase("")){
                Picasso.with(context)
                        .load(R.drawable.image_placeholder).fit().centerCrop()
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .into(trip_image_view);
            }else{
                Picasso.with(context)
                        .load(model.getImageUrl()).fit().centerCrop()
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .into(trip_image_view);
            }


            trip_name_tv.setText(model.getTrip_name());
            trip_date_tv.setText(model.getTrip_date());
            trip_amount_tv.setText(model.getTrip_amount()+"");
            total_persons.setText(Integer.toString(model.getTotal_persons()));

            return v;
        }

        @Override
        public int getCount() {
            return trip_array_list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    // Title AsyncTask
    private class getImageUrlConnection extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            for(int i=0;i<trip_array_list.size();i++){
                TripModel tripModel = trip_array_list.get(i);
                if(tripModel.getImageUrl().equalsIgnoreCase("")){
                    String query = tripModel.getTrip_name();
                    query = query.toLowerCase().replace("trip", "tour").toLowerCase().replace("holiday", "tour");
                    if (!query.contains("tour")) {
                        query = query + " tour";
                    }
                    try {
                        query = URLEncoder.encode(query, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        Document doc = Jsoup.connect("https://www.google.co.in/search?q=" + query + "&source=lnms&tbm=isch")
                                .userAgent("Mozilla/5.0 Gecko/20100101 Firefox/21.0").get();
                        if (doc != null) {
                            Elements elems = doc.select("div.rg_meta");
                            if (!(elems == null || elems.isEmpty())) {
                                Iterator it = elems.iterator();
                                while (it.hasNext()) {
                                    try {
                                        JSONObject jSONObject = new JSONObject(((Element) it.next()).text());
                                    //    if (jSONObject.getInt("ow") > 500) {
                                            String imageUrl = jSONObject.getString("ou");
                                            tripModel.setImageUrl(imageUrl);
                                            DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
                                            dataBaseHelper.updateTripImageUrl(tripModel.getTrip_id(),imageUrl);
                                            grid_view_adapter.notifyDataSetChanged();
                                            break;
                                      //  }
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
            return "";
        }
    }

}
