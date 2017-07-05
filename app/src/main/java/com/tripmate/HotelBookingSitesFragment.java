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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;


/**
 * A simple {@link Fragment} subclass.
 */
public class HotelBookingSitesFragment extends Fragment {

    RecyclerView hotelBookingRV;

    HotelsAdapter mAdapter;
    ArrayList<HotelsTravelModel> hotelsModels = new ArrayList<>();

    public static final String JSON_URL = "http://tripmateandroid.000webhostapp.com/gethotels.php";
    SwipeRefreshLayout mWaveSwipeRefreshLayout;

    ProgressDialog pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hotel_booking_sites, container, false);

        hotelBookingRV = (RecyclerView) view.findViewById(R.id.hotelBookingRV);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Hotel Booking Sites");

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Please wait...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);


        mWaveSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipetorefresh);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();
            }
        });


        //getting the previously loaded data which is stored in sharedpreferences
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String jsonObject = app_preferences.getString("get_hotels","null");

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


    private void sendRequest(){

        StringRequest stringRequest = new StringRequest(JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = app_preferences.edit();
                            editor.putString("get_hotels",response);
                            editor.commit();

                            showJSON(response);
                            mWaveSwipeRefreshLayout.setRefreshing(false);
                            pd.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        mWaveSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void showJSON(String json) throws JSONException {
        hotelsModels.clear();
        JSONArray result = new JSONArray(json);
        for(int i=0;i<result.length();i++){
            JSONObject object = result.getJSONObject(i);

            HotelsTravelModel model = new HotelsTravelModel();
            model.setName(object.getString("name"));
            model.setUrl(object.getString("url_display"));
            model.setDesc(object.getString("description"));
            model.setReferenseUrl(object.getString("reference_url"));
            model.setIconUrl1(object.getString("icon_url1"));
            model.setIconUrl2(object.getString("icon_url2"));

            hotelsModels.add(model);
        }

        mAdapter = new HotelsAdapter(hotelsModels);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        hotelBookingRV.setLayoutManager(linearLayoutManager);

        hotelBookingRV.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));
        ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(mAdapter);
        adapter.setDuration(400);

        hotelBookingRV.setAdapter(adapter);
    }


    class HotelsAdapter extends RecyclerView.Adapter<HotelsAdapter.HotelsViewHolder>{

        private ArrayList<HotelsTravelModel> hotelsModels = new ArrayList<>();

        public HotelsAdapter(ArrayList<HotelsTravelModel> hotelsModels) {
            this.hotelsModels = hotelsModels;
        }


        public  class HotelsViewHolder extends RecyclerView.ViewHolder{

            TextView nameTv,urlTv,descTv;
            ImageView imageView;
            CardView hotelsCardView;

            public HotelsViewHolder(View itemView) {
                super(itemView);
                nameTv = (TextView) itemView.findViewById(R.id.nameTv);
                urlTv = (TextView) itemView.findViewById(R.id.urlTv);
                descTv = (TextView) itemView.findViewById(R.id.descTv);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                hotelsCardView = (CardView) itemView.findViewById(R.id.hotelsCardView);


            }
        }

        @Override
        public HotelsAdapter.HotelsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hotels_travel_row_view1, parent, false);
            return new HotelsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final HotelsAdapter.HotelsViewHolder holder, int position) {
            final HotelsTravelModel hotelModel = hotelsModels.get(position);

            holder.nameTv.setText(hotelModel.getName());
            holder.urlTv.setText(hotelModel.getUrl());
            holder.descTv.setText(hotelModel.getDesc());

            holder.hotelsCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hotelModel.getReferenseUrl()));
                    startActivity(browserIntent);
                }
            });

           /* Picasso.with(getActivity())
                    .load(hotelModel.getIconUrl1())
                    .placeholder(R.drawable.image_placeholder)   // optional
                    .error(R.drawable.image_placeholder)      // optional
                    //  .resize(135, 135)                       // optional
                    .into(holder.imageView);  */


        }

        @Override
        public int getItemCount() {
            return hotelsModels.size();
        }
    }


}
