package com.tripmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.models.PersonModel;
import android.app.models.TripModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class TripDetailsActivityNew extends AppCompatActivity implements  OnStartDragListener{

    TextInputLayout tilTripName;
    ImageView ivDate;
    TextView tvDate;
    int mYear,mMonth,mDay;
    DatePickerDialog.OnDateSetListener dateSetListener;
    LinearLayout llDate;
    ArrayList<PersonModel> tripPersonModels = new ArrayList<>();

    LinearLayout llAddPlacesToVisit;
    RecyclerView rvPlacesToVisit;
    ArrayList<String> placesToVisitArrayList = new ArrayList<>();
    PlacesToVisitAdapter placesToVisitAdapter;

    int posOfFocus = -1;

    ItemTouchHelper mItemTouchHelper;

    FloatingActionButton fabForward;

    String trip_id,trip_name,trip_date,trip_url,trip_places;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

        setContentView(R.layout.activity_add_trip_new);

        Intent tripIdIntent = getIntent();
        trip_id = tripIdIntent.getStringExtra("trip_id");
        trip_name = tripIdIntent.getStringExtra("trip_name");
        trip_date = tripIdIntent.getStringExtra("trip_date");
        trip_url = tripIdIntent.getStringExtra("trip_url");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(trip_name);
            getSupportActionBar().setSubtitle(trip_date);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tilTripName = (TextInputLayout) findViewById(R.id.tilTripName);
        tvDate = (TextView)findViewById(R.id.tvDate);
        ivDate = (ImageView) findViewById(R.id.ivDate);
        llDate = (LinearLayout) findViewById(R.id.llDate);
        rvPlacesToVisit = (RecyclerView) findViewById(R.id.rvPlacesToVisit);
        llAddPlacesToVisit = (LinearLayout) findViewById(R.id.llAddPlacesToVisit);
        fabForward  = (FloatingActionButton) findViewById(R.id.fabForward);


        // set Trip Name
        tilTripName.getEditText().setText(trip_name);
        tilTripName.getEditText().setSelection(tilTripName.getEditText().getText().length());

         tilTripName.getEditText().addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {

             }

             @Override
             public void afterTextChanged(Editable s) {
                   if(getSupportActionBar()!=null){
                       getSupportActionBar().setTitle(tilTripName.getEditText().getText().toString());
                   }
             }
         });

        fabForward.setVisibility(View.GONE);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        TripModel tripModel = dataBaseHelper.getTripData(trip_id);

        // Getting Current Date and Time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        tvDate.setText(trip_date);

        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        Long todaysOnlyDateValue = null;

        try {
            Date todaysDate = f.parse(mDay + "-" + (mMonth + 1) + "-" + mYear);
            todaysOnlyDateValue = todaysDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Date Picker
        dateSetListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                trip_date = mDay + "-" + (mMonth + 1) + "-" + mYear;
                tvDate.setText(trip_date);
                if(getSupportActionBar()!=null) {
                    getSupportActionBar().setSubtitle(trip_date);
                }



            }
        };

        final Long finalTodaysOnlyDateValue = todaysOnlyDateValue;
        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                DatePickerDialog datePickerDialog = new DatePickerDialog(TripDetailsActivityNew.this,dateSetListener,mYear,mMonth,mDay);
                datePickerDialog.getDatePicker().setMinDate(finalTodaysOnlyDateValue);
                datePickerDialog.getWindow().setWindowAnimations(R.style.DialogAnimationUpDown);
                datePickerDialog.show();
            }
        });


        trip_places = tripModel.getTrip_places();


        String placesArray[] = tripModel.getTrip_places().split(",");
        Log.d("places",placesArray[0]);
        for(int i=0;i<placesArray.length;i++){
            placesArray[i] = placesArray[i].trim();
            if(placesArray[i].length()>0)
                placesArray[i] = placesArray[i].substring(0,1).toUpperCase() + placesArray[i].substring(1);
        }

        placesToVisitArrayList.clear();
        placesToVisitArrayList.addAll(Arrays.asList(placesArray));


        // Setting Up Adapter
        placesToVisitAdapter = new PlacesToVisitAdapter(this,placesToVisitArrayList,this);
        rvPlacesToVisit.setLayoutManager(new LinearLayoutManager(this));
        rvPlacesToVisit.setAdapter(placesToVisitAdapter);

        // Setting up ItemTouchHelper for drag,drop,swipe
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(placesToVisitAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvPlacesToVisit);

        llAddPlacesToVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placesToVisitArrayList.size() != 0 &&
                        placesToVisitArrayList.get(placesToVisitArrayList.size()-1).equalsIgnoreCase("")){
                    llAddPlacesToVisit.setVisibility(View.VISIBLE);
                }else {
                    placesToVisitArrayList.add("");
                    placesToVisitAdapter.notifyItemInserted(placesToVisitArrayList.size() - 1);
                    posOfFocus = placesToVisitArrayList.size() - 1;
                    llAddPlacesToVisit.setVisibility(View.GONE);
                }
            }
        });


    }




    public void addTrip(){
        try {
            if(!tilTripName.getEditText().getText().toString().equalsIgnoreCase("")){
                trip_name = tilTripName.getEditText().getText().toString().trim().substring(0, 1).toUpperCase() + tilTripName.getEditText().getText().toString().trim().substring(1);
            }else{
                trip_name = "";
            }
            encryptPlaces();
        }catch (Exception e){
            return;
        }


        trip_date = tvDate.getText().toString();
        if(!validate(trip_name) ){
            tilTripName.setError("Please Enter Trip Name");
        }else if(!validate(trip_places)){
            tilTripName.setErrorEnabled(false);
            Snackbar.make(findViewById(android.R.id.content),"Please Enter Places to Visit", Snackbar.LENGTH_LONG).show();
        }
        else{
            TripModel trip = new TripModel(trip_name,trip_places,trip_date);
            Intent intent = new Intent(TripDetailsActivityNew.this,TripInfo_AddTrip.class);
            intent.putExtra("TripName",trip.getTrip_name());
            intent.putExtra("TripPlaces",trip.getTrip_places());
            intent.putExtra("TripDate",trip.getTrip_date());
            intent.putExtra("PersonsList",tripPersonModels);

            startActivityForResult(intent,200);

        }

    }

    public boolean validate(String textField){
        return  textField.length()>0;
    }

    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    public void encryptPlaces(){
        String places = "";
        for(String s : placesToVisitArrayList){
            if(s.length()!=0 ){
                places+=s.substring(0,1).toUpperCase()+s.substring(1)+",";
            }
        }
        if(places.length()>=2)
            trip_places =  places.substring(0,places.length()-2);
        else
            trip_places = places;
    }

    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private final ItemTouchHelperAdapter mAdapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            hideSoftKeyboard();
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                      int actionState) {
            // We only want the active item
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof ItemTouchHelperViewHolder) {
                    ItemTouchHelperViewHolder itemViewHolder =
                            (ItemTouchHelperViewHolder) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }

            super.onSelectedChanged(viewHolder, actionState);
        }
        @Override
        public void clearView(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                ItemTouchHelperViewHolder itemViewHolder =
                        (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemClear();
            }
        }

    }

    class PlacesToVisitAdapter extends RecyclerView.Adapter<PlacesToVisitAdapter.PlacesToVisitViewHolder>
            implements ItemTouchHelperAdapter{

        Context context;
        ArrayList<String> placesToVisitList = new ArrayList<>();

        OnStartDragListener mDragStartListener;
        boolean clearFocus = false;

        public PlacesToVisitAdapter(Context context, ArrayList<String> placesToVisitList, OnStartDragListener mDragStartListener) {
            this.context = context;
            this.placesToVisitList = placesToVisitList;
            this.mDragStartListener = mDragStartListener;
        }

        @Override
        public void onItemDismiss(int position) {
            placesToVisitArrayList.remove(position);
            notifyItemRemoved(position);
            if(placesToVisitArrayList.size() == 0){
                llAddPlacesToVisit.setVisibility(View.VISIBLE);
            }
            posOfFocus = position;
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            clearFocus = true;
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(placesToVisitArrayList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(placesToVisitArrayList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            clearFocus=false;
            return true;
        }



        class PlacesToVisitViewHolder extends  RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{
            ImageView ivIconPlace,ivCancelPlace;
            EditText etPlaceToVisit;
            PlacesToVisitViewHolder(View itemView) {
                super(itemView);
                ivCancelPlace = (ImageView) itemView.findViewById(R.id.ivCancelPlace);
                ivIconPlace = (ImageView) itemView.findViewById(R.id.ivIconPlace);
                etPlaceToVisit = (EditText) itemView.findViewById(R.id.etPlaceToVisit);

            }

            @Override
            public void onItemSelected() {
                itemView.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
            }
        }

        @Override
        public PlacesToVisitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View customView = getLayoutInflater().inflate(R.layout.list_item_places_to_visit_new,parent,false);
            return new PlacesToVisitViewHolder(customView);

        }

        @Override
        public void onBindViewHolder(final PlacesToVisitViewHolder holder, int position) {
            holder.etPlaceToVisit.setText(placesToVisitList.get(position));

            if(posOfFocus!= -1 && posOfFocus==position && posOfFocus>0 && posOfFocus<placesToVisitArrayList.size()){
                holder.etPlaceToVisit.post(new Runnable() {
                    @Override
                    public void run() {
                        if (holder.etPlaceToVisit.requestFocus()) {
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            InputMethodManager inputMethodManager = (InputMethodManager) holder.etPlaceToVisit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(holder.etPlaceToVisit, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                });
                posOfFocus = -1;

                if(clearFocus){
                    holder.etPlaceToVisit.clearFocus();
                  /*  holder.etPlaceToVisit.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.etPlaceToVisit.clearFocus();
                        }
                    });*/

                }

            }
            holder.ivIconPlace.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) ==
                            MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });


            holder.ivCancelPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    placesToVisitArrayList.remove(holder.getAdapterPosition());
                    placesToVisitAdapter.notifyItemRemoved(holder.getAdapterPosition());
                    placesToVisitAdapter.notifyItemRangeChanged(holder.getAdapterPosition(),placesToVisitArrayList.size());


                    if(placesToVisitArrayList.size() == 0){
                        llAddPlacesToVisit.setVisibility(View.VISIBLE);
                    }

                    encryptPlaces();
                    DataBaseHelper db = new DataBaseHelper(context);
                    db.updateTripPlaces(trip_id,trip_places);
                }
            });

            holder.etPlaceToVisit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(holder.etPlaceToVisit.getText().toString().equalsIgnoreCase("")){

                    }else {
                        placesToVisitArrayList.set(holder.getAdapterPosition(),holder.etPlaceToVisit.getText().toString());
                        llAddPlacesToVisit.setVisibility(View.VISIBLE);
                        encryptPlaces();
                        DataBaseHelper db = new DataBaseHelper(context);
                        db.updateTripPlaces(trip_id,trip_places);
                    }
                }
            });


            holder.etPlaceToVisit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(!(holder.etPlaceToVisit.getText().toString().equalsIgnoreCase(""))){
                        placesToVisitArrayList.add(holder.getAdapterPosition()+1,"");
                        placesToVisitAdapter.notifyItemInserted(holder.getAdapterPosition()+1);
                        placesToVisitAdapter.notifyItemRangeChanged(holder.getAdapterPosition()+1,placesToVisitArrayList.size());
                        posOfFocus = holder.getAdapterPosition()+1;
                    }
                    return true;
                }
            });



            holder.etPlaceToVisit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        holder.ivCancelPlace.setVisibility(View.VISIBLE);
                        holder.etPlaceToVisit.setSelection(holder.etPlaceToVisit.getText().length());
                    }else {
                        holder.ivCancelPlace.setVisibility(View.GONE);
                    }
                }
            });




        }


        @Override
        public int getItemCount() {
            return placesToVisitList.size();
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


    @Override
    public void onBackPressed() {
        encryptPlaces();
        DataBaseHelper db = new DataBaseHelper(TripDetailsActivityNew.this);
        db.updateTripPlaces(trip_id,trip_places);
        String newTripName = tilTripName.getEditText().getText().toString();

        if(newTripName.equalsIgnoreCase("")){
            Snackbar.make(findViewById(android.R.id.content),"Please enter Trip name",Snackbar.LENGTH_LONG).show();
        }else if(!trip_name.equalsIgnoreCase(newTripName)) {

            // delete url from database if tripname is changed
            db.updateTripImageUrl(trip_id,"");
            // First Letter capital
            newTripName = newTripName.trim().substring(0, 1).toUpperCase() + newTripName.trim().substring(1);
            trip_name = newTripName;
            db.updateTripName(trip_id,newTripName);
            db.updateTripDate(trip_id,trip_date);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("tripDate",trip_date);
            returnIntent.putExtra("tripName",trip_name);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
            overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
            super.onBackPressed();

        }else{
            db.updateTripDate(trip_id,trip_date);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("tripDate",trip_date);
            returnIntent.putExtra("tripName",trip_name);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
            overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
            super.onBackPressed();

        }
    }
}
