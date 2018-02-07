package com.tripmate;


import android.app.ProgressDialog;
import android.app.models.HotelsTravelModel;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.ramotion.foldingcell.FoldingCell;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import okhttp3.OkHttpClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class TravelBookingSitesFragment extends Fragment {

    RecyclerView travelBookingRV;

    TravelAdapter mAdapter;
    ArrayList<HotelsTravelModel> travelModels = new ArrayList<>();

    public static final String JSON_URL = "http://tripmateandroid.000webhostapp.com/gettravel.php";
    SwipeRefreshLayout mWaveSwipeRefreshLayout;
    ProgressDialog pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel_booking_sites, container, false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getContext(),okHttpClient);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Travel Booking Sites");

        travelBookingRV = (RecyclerView) view.findViewById(R.id.travelBookingRV);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Please wait...");
        pd.setCancelable(false);


        mWaveSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipetorefresh);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();
            }
        });


        //getting the previously loaded data which is stored in sharedpreferences
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String jsonObject = app_preferences.getString("get_travel","null");

        if(!jsonObject.equalsIgnoreCase("null")){
            try {
                showJSON(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            pd.show();
            sendRequest();
        }


        return view;
    }

    public void sendRequest(){

        AndroidNetworking.get(JSON_URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response

                        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = app_preferences.edit();
                        editor.putString("get_travel",response.toString());
                        editor.apply();

                        try {
                            showJSON(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mWaveSwipeRefreshLayout.setRefreshing(false);
                        pd.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                        mWaveSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void showJSON(String json) throws JSONException {
        travelModels.clear();
        JSONArray result = new JSONArray(json);
        for(int i=0;i<result.length();i++){
            JSONObject object = result.getJSONObject(i);

            HotelsTravelModel model = new HotelsTravelModel();
            model.setName(object.getString("name"));
            model.setUrl_display(object.getString("url_display"));
            model.setApp_url(object.getString("app_url"));
            model.setImg_url1(object.getString("img_url1"));
            model.setImg_url2(object.getString("img_url2"));
            model.setDescription(object.getString("description"));
            model.setServices_offered(object.getString("services_offered"));

            travelModels.add(model);
        }

        mAdapter = new TravelAdapter(travelModels);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        travelBookingRV.setLayoutManager(linearLayoutManager);

        travelBookingRV.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));
        ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(mAdapter);
        adapter.setDuration(400);

        travelBookingRV.setAdapter(adapter);
    }

    class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder>{

        private ArrayList<HotelsTravelModel> hotelsModels = new ArrayList<>();
        private HashSet<Integer> unfoldedIndexes = new HashSet<>();

        TravelAdapter(ArrayList<HotelsTravelModel> hotelsModels) {
            this.hotelsModels = hotelsModels;
        }


        class TravelViewHolder extends RecyclerView.ViewHolder{

            TextView nameTv,urlTv,servicesOfferedTv,titleBig,servicesOfferedTvBig,descBig,urlDisplayBig,getAppBig;
            ImageView smallImageView,bigImageView;
            FoldingCell foldingCell;
            LinearLayout getAppLayout;

            TravelViewHolder(View itemView) {
                super(itemView);
                nameTv = (TextView) itemView.findViewById(R.id.nameTv);
                urlTv = (TextView) itemView.findViewById(R.id.urlTv);
                servicesOfferedTv = (TextView) itemView.findViewById(R.id.servicesOfferedTv);
                smallImageView = (ImageView) itemView.findViewById(R.id.smallImageView);
                foldingCell = (FoldingCell) itemView.findViewById(R.id.folding_cell);

                titleBig = (TextView) itemView.findViewById(R.id.titleBig);
                bigImageView = (ImageView) itemView.findViewById(R.id.bigImageView);
                servicesOfferedTvBig = (TextView) itemView.findViewById(R.id.servicesOfferedTvBig);
                descBig = (TextView) itemView.findViewById(R.id.descBig);
                urlDisplayBig = (TextView) itemView.findViewById(R.id.urlDisplayBig);
                getAppBig = (TextView) itemView.findViewById(R.id.getAppBig);
                getAppLayout = (LinearLayout) itemView.findViewById(R.id.getAppLayout);

            }
        }

        @Override
        public TravelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hotels_travel_row_view1, parent, false);
            return new TravelViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final TravelViewHolder holder, int position) {
            final HotelsTravelModel hotelModel = hotelsModels.get(position);

            holder.nameTv.setText(hotelModel.getName());
            holder.urlTv.setText(hotelModel.getUrl_display());
            holder.servicesOfferedTv.setText(hotelModel.getServices_offered());

            holder.foldingCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.foldingCell.toggle(false);
                    registerToggle(holder.getAdapterPosition());
                }
            });


            Picasso.with(getActivity())
                    .load(hotelModel.getImg_url1())
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(holder.smallImageView);

            Picasso.with(getActivity())
                    .load(hotelModel.getImg_url2())
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(holder.bigImageView);

            holder.titleBig.setText(hotelModel.getName());
            holder.servicesOfferedTvBig.setText(hotelModel.getServices_offered());
            holder.descBig.setText(hotelModel.getDescription());
            holder.urlDisplayBig.setText(hotelModel.getUrl_display());
            if(hotelModel.getApp_url().equalsIgnoreCase("NULL")){
                holder.getAppLayout.setVisibility(View.GONE);
            }else{
                holder.getAppLayout.setVisibility(View.VISIBLE);
                holder.getAppLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String appPackageName = hotelModel.getApp_url(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
            }

            if (unfoldedIndexes.contains(holder.getAdapterPosition())) {
                holder.foldingCell.unfold(true);
            } else {
                holder.foldingCell.fold(true);
            }

        }

        public void registerToggle(int position) {
            if (unfoldedIndexes.contains(position))
                registerFold(position);
            else
                registerUnfold(position);
        }

        public void registerFold(int position) {
            unfoldedIndexes.remove(position);
        }

        public void registerUnfold(int position) {
            unfoldedIndexes.add(position);
        }

        @Override
        public int getItemCount() {
            return hotelsModels.size();
        }
    }
}
