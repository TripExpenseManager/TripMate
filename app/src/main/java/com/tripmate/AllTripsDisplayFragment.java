package com.tripmate;


import android.app.models.TripModel;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.yalantis.taurus.PullToRefreshView;
import com.yalantis.taurus.RefreshView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllTripsDisplayFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    PullToRefreshView mPullToRefreshView;

    GridView trip_grid_view;

    ArrayList<TripModel> trip_array_list = new ArrayList<>();

    TripAdapter grid_view_adapter;

    FloatingActionButton fab;

    MaterialSearchView searchView;

    RelativeLayout no_trips_RL,no_trips_found_searched_RL;

    //google drive stuff
    static GoogleApiClient mGoogleApiClient;

    final int RESOLVE_CONNECTION_REQUEST_CODE = 1827;

    static File db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_trips_display, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Trip Mate");
        
        setHasOptionsMenu(true);

        //googledrive stuff
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        db = getDbPath();


        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 500);
            }
        });


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

                Intent intent = new Intent(getActivity(),AddTrip.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), fab, getString(R.string.activity_image_trans2));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }

            }
        });
        

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        trip_array_list = dataBaseHelper.getTripsData();

        trip_grid_view = (GridView) view.findViewById(R.id.trip_grid_view);


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

        else if(requestCode == RESOLVE_CONNECTION_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Log.d("DriveDbHandler","RESULT_OK");
                mGoogleApiClient.connect();
            }
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
        super.onResume();

        //closing the searchview if it is open
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }

        //refreshing the contents of gridview by retrieving data from backend
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());

        trip_array_list = dataBaseHelper.getTripsData();

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

            grid_view_adapter  = new TripAdapter(getActivity(), trip_array_list);

            trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    Intent intent = new Intent(getActivity(),TripDesk.class);
                    intent.putExtra("trip_id",trip_array_list.get(position).getTrip_id());
                    intent.putExtra("trip_name",trip_array_list.get(position).getTrip_name());
                    intent.putExtra("trip_date",trip_array_list.get(position).getTrip_date());

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

        }else if(prevSelectedPosition ==1){

            //sorting amount wise
            Collections.sort(trip_array_list, new Comparator<TripModel>() {
                @Override
                public int compare(TripModel o1, TripModel o2) {
                    return o2.getTrip_amount().compareTo(o1.getTrip_amount());
                }
            });

            grid_view_adapter  = new TripAdapter(getActivity(), trip_array_list);

            trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    Intent intent = new Intent(getActivity(),TripDesk.class);
                    intent.putExtra("trip_id",trip_array_list.get(position).getTrip_id());
                    intent.putExtra("trip_name",trip_array_list.get(position).getTrip_name());
                    intent.putExtra("trip_date",trip_array_list.get(position).getTrip_date());

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

        }else if(prevSelectedPosition ==2){


            //sorting name wise
            Collections.sort(trip_array_list, new Comparator<TripModel>() {
                @Override
                public int compare(TripModel o1, TripModel o2) {
                    return o1.getTrip_name().compareTo(o2.getTrip_name());
                }
            });

            grid_view_adapter  = new TripAdapter(getActivity(), trip_array_list);

            trip_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    Intent intent = new Intent(getActivity(),TripDesk.class);
                    intent.putExtra("trip_id",trip_array_list.get(position).getTrip_id());
                    intent.putExtra("trip_name",trip_array_list.get(position).getTrip_name());
                    intent.putExtra("trip_date",trip_array_list.get(position).getTrip_date());

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

    }

    //gridview adapter
    public class TripAdapter extends BaseAdapter {
        private Context context;
        ArrayList<TripModel> trip_array_list;

        public TripAdapter(Context context, ArrayList<TripModel> trip_array_list) {
            this.context = context;
            this.trip_array_list = trip_array_list;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v;
            if(convertView==null)
            {
                LayoutInflater li = getActivity().getLayoutInflater();
                v = li.inflate(R.layout.trip_item_grid_row, null);
            }else{
                v = convertView;
            }

            TripModel model = trip_array_list.get(position);

            TextView trip_name_tv = (TextView) v.findViewById(R.id.trip_name_tv);
            TextView trip_date_tv = (TextView) v.findViewById(R.id.trip_date_tv);
            TextView trip_amount_tv = (TextView) v.findViewById(R.id.trip_amount_tv);


            ImageView trip_image_view = (ImageView) v.findViewById(R.id.trip_image_view);


          /*  Picasso.with(getActivity())
                    .load("https://www.simplifiedcoding.net/wp-content/uploads/2015/10/advertise.png")
                    .image_placeholder(R.drawable.image_placeholder)   // optional
                    .error(R.drawable.image_placeholder)      // optional
                  //  .resize(135, 135)                       // optional
                    .into(trip_image_view);*/

            trip_name_tv.setText(model.getTrip_name());
            trip_date_tv.setText(model.getTrip_date());
            trip_amount_tv.setText(model.getTrip_amount()+"");

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



    private  File getDbPath() {
        return getActivity().getDatabasePath(DataBaseHelper.DATABASE_NAME);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("DriveDbHandler","connected");
        // DriveDbHandler.tryCreatingDbOnDrive(mGoogleApiClient);

        //doDriveBackup();

        // Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(contentsCallback);

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
                connectionResult.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {

                // Unable to resolve, message user appropriately
            }
        } else {
            Log.d("DriveDbHandler","GooglePlayServicesUtil.getErrorDialog");
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(), 0).show();
        }
    }

}
