package com.tripmate;

import android.app.ProgressDialog;
import android.app.models.HotelsTravelModel;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ramotion.foldingcell.FoldingCell;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

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
        pd.setMessage("Please wait...");
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
                            editor.apply();

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
            model.setUrl_display(object.getString("url_display"));
            model.setApp_url(object.getString("app_url"));
            model.setImg_url1(object.getString("img_url1"));
            model.setImg_url2(object.getString("img_url2"));
            model.setDescription(object.getString("description"));
            model.setServices_offered(object.getString("services_offered"));

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
        private HashSet<Integer> unfoldedIndexes = new HashSet<>();

        HotelsAdapter(ArrayList<HotelsTravelModel> hotelsModels) {
            this.hotelsModels = hotelsModels;
        }


        class HotelsViewHolder extends RecyclerView.ViewHolder{

            TextView nameTv,urlTv,servicesOfferedTv,titleBig,servicesOfferedTvBig,descBig,urlDisplayBig,getAppBig;
            ImageView smallImageView,bigImageView;
            FoldingCell foldingCell;
            LinearLayout getAppLayout;

            HotelsViewHolder(View itemView) {
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
        public HotelsAdapter.HotelsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hotels_travel_row_view1, parent, false);
            return new HotelsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final HotelsAdapter.HotelsViewHolder holder, int position) {
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
