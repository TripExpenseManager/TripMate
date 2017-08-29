package com.tripmate;

import android.app.models.TodoModel;
import android.app.models.TripModel;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class TripDetailsActivity extends AppCompatActivity {

    String trip_id,trip_name,trip_date,trip_url,trip_places,trip_desc;
    LinearLayout llTripDescription,llPlacesToVisit;
    LinearLayout llShowTripDescription,llShowPlacesToVisit ,llShowItems , llShowOrHide;
    RelativeLayout rlTitleShowItems;

    TextView tvTitleOfSelected , tvAddPLacesToVisit;
    NestedScrollView nswTripDetails;
    RecyclerView rvPlacesToVisit;
    ArrayList<String> placesToVisitArrayList = new ArrayList<>();
    PlacesToVisitAdapter placesToVisitAdapter;
    ImageView ivShowOrHide;
    boolean isShown = true;

    TextView tvDate;
    EditText etDescription;
    ImageView ivEditDescription;

    boolean tripDescIsEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        Intent tripIdIntent = getIntent();
        trip_id = tripIdIntent.getStringExtra("trip_id");
        trip_name = tripIdIntent.getStringExtra("trip_name");
        trip_date = tripIdIntent.getStringExtra("trip_date");
        trip_url = tripIdIntent.getStringExtra("trip_url");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



      /*  if(trip_url.equalsIgnoreCase("")){
            Picasso.with(TripDesk.this)
                    .load(R.drawable.image_placeholder)
                    .fit().centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(header_image);
        }else{
            Picasso.with(TripDesk.this)
                    .load(trip_url)
                    .fit().centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(header_image);
        }*/


        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(trip_name);
            getSupportActionBar().setSubtitle(trip_date);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        llTripDescription = (LinearLayout) findViewById(R.id.llTripDescription);
        llPlacesToVisit = (LinearLayout) findViewById(R.id.llPlacesToVisit);

        llShowItems = (LinearLayout) findViewById(R.id.llShowItems);
        llShowTripDescription = (LinearLayout) findViewById(R.id.llShowTripDescription);
        llShowPlacesToVisit = (LinearLayout) findViewById(R.id.llShowPlacesToVisit);
        rlTitleShowItems = (RelativeLayout) findViewById(R.id.rlTitleShowItems);
        llShowOrHide = (LinearLayout) findViewById(R.id.llShowOrHide);




        tvTitleOfSelected = (TextView) findViewById(R.id.tvTitleOfSelected);
        tvAddPLacesToVisit = (TextView) findViewById(R.id.tvAddPLacesToVisit);
        nswTripDetails = (NestedScrollView) findViewById(R.id.nswTripDetails);
        rvPlacesToVisit = (RecyclerView) findViewById(R.id.rvPlacesToVisit);
        ivShowOrHide = (ImageView) findViewById(R.id.ivShowOrHide);

        // Description layout
        tvDate = (TextView) findViewById(R.id.tvDate);
        ivEditDescription = (ImageView) findViewById(R.id.ivEditDescription);
        etDescription = (EditText) findViewById(R.id.etDescription);

        // Disabling show items
        llShowItems.setVisibility(View.GONE);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        TripModel tripModel = dataBaseHelper.getTripData(trip_id);

        trip_desc  = tripModel.getTrip_desc();

        tvDate.setText(trip_date);
        etDescription.setText(trip_desc);

        ivEditDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tripDescIsEditable){
                    etDescription.setFocusable(true);
                    etDescription.setFocusableInTouchMode(true);
                    Drawable d = getResources().getDrawable(R.drawable.icon_save);
                    ivEditDescription.setImageDrawable(d);
                    tripDescIsEditable = !tripDescIsEditable;

                }else{
                    etDescription.setFocusable(false);
                    etDescription.setFocusableInTouchMode(false);
                    Drawable d = getResources().getDrawable(R.drawable.icon_edit);
                    ivEditDescription.setImageDrawable(d);
                    tripDescIsEditable = !tripDescIsEditable;
                }
            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        llShowPlacesToVisit.setVisibility(View.GONE);
        llShowTripDescription.setVisibility(View.GONE);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.llTripDescription :
                        tvTitleOfSelected.setText("Trip Description");
                        llShowItems.setVisibility(View.VISIBLE);
                        nswTripDetails.smoothScrollTo(0,llShowItems.getTop());

                        rlTitleShowItems.setBackgroundColor(getResources().getColor(R.color.tripDesc));
                        llShowTripDescription.setVisibility(View.VISIBLE);
                        llShowPlacesToVisit.setVisibility(View.GONE);

                        llShowItems.setVisibility(View.VISIBLE);
                        break;
                    case R.id.llPlacesToVisit :
                        tvTitleOfSelected.setText("Places to Visit");
                        llShowItems.setVisibility(View.VISIBLE);
                        nswTripDetails.smoothScrollTo(0,llShowItems.getTop());

                        rlTitleShowItems.setBackgroundColor(getResources().getColor(R.color.tripPlaces));
                        llShowPlacesToVisit.setVisibility(View.VISIBLE);
                        llShowTripDescription.setVisibility(View.GONE);

                        llShowItems.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };

        llTripDescription.setOnClickListener(clickListener);
        llPlacesToVisit.setOnClickListener(clickListener);



        trip_places = tripModel.getTrip_places();


        String placesArray[] = tripModel.getTrip_places().split(",");
        for(int i=0;i<placesArray.length;i++){
            placesArray[i] = placesArray[i].trim();
            placesArray[i] = placesArray[i].substring(0,1).toUpperCase() + placesArray[i].substring(1);
        }

        placesToVisitArrayList.clear();
        placesToVisitArrayList.addAll(Arrays.asList(placesArray));


        placesToVisitAdapter = new PlacesToVisitAdapter(this,placesToVisitArrayList);
        rvPlacesToVisit.setLayoutManager(new LinearLayoutManager(this));
        rvPlacesToVisit.setAdapter(placesToVisitAdapter);
        
        ivShowOrHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShown){
                    Drawable d = getResources().getDrawable(R.drawable.ic_hide);
                    ivShowOrHide.setImageDrawable(d);
                    isShown = !isShown;

                    llShowOrHide.setVisibility(View.VISIBLE);
                }else{
                    Drawable d = getResources().getDrawable(R.drawable.ic_show);
                    ivShowOrHide.setImageDrawable(d);
                    isShown = !isShown;
                    llShowOrHide.setVisibility(View.GONE);
                }
            }
        });

        tvAddPLacesToVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placesToVisitArrayList.size() != 0 &&
                        placesToVisitArrayList.get(placesToVisitArrayList.size()-1).equalsIgnoreCase("")){
                    tvAddPLacesToVisit.setVisibility(View.VISIBLE);
                }else {
                    placesToVisitArrayList.add("");
                    placesToVisitAdapter.notifyItemInserted(placesToVisitArrayList.size() - 1);
                    tvAddPLacesToVisit.setVisibility(View.GONE);
                }
            }
        });


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
    }

    class PlacesToVisitAdapter extends RecyclerView.Adapter<PlacesToVisitAdapter.PlacesToVisitViewHolder>{

        Context context;
        ArrayList<String> placesToVisitList = new ArrayList<>();
        int posOfFocus = -1;

        public PlacesToVisitAdapter(Context context, ArrayList<String> placesToVisitList) {
            this.context = context;
            this.placesToVisitList = placesToVisitList;
        }


        class PlacesToVisitViewHolder extends  RecyclerView.ViewHolder{
            ImageView ivIconPlace,ivCancelPlace;
            EditText etPlaceToVisit;
            PlacesToVisitViewHolder(View itemView) {
                super(itemView);
                ivCancelPlace = (ImageView) itemView.findViewById(R.id.ivCancelPlace);
                ivIconPlace = (ImageView) itemView.findViewById(R.id.ivIconPlace);
                etPlaceToVisit = (EditText) itemView.findViewById(R.id.etPlaceToVisit);

            }
        }

        @Override
        public PlacesToVisitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View customView = getLayoutInflater().inflate(R.layout.list_item_places_to_visit,parent,false);
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

            }


            holder.ivCancelPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    placesToVisitArrayList.remove(holder.getAdapterPosition());
                    placesToVisitAdapter.notifyItemRemoved(holder.getAdapterPosition());
                    placesToVisitAdapter.notifyItemRangeChanged(holder.getAdapterPosition(),placesToVisitArrayList.size());


                    if(placesToVisitArrayList.size() == 0){
                        tvAddPLacesToVisit.setVisibility(View.VISIBLE);
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
                        tvAddPLacesToVisit.setVisibility(View.VISIBLE);
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
    public void onBackPressed() {
        encryptPlaces();
        DataBaseHelper db = new DataBaseHelper(TripDetailsActivity.this);
        db.updateTripPlaces(trip_id,trip_places);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }
}
