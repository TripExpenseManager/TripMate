package com.tripmate;


import android.app.models.HotelsTravelModel;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hotel_booking_sites, container, false);

        hotelBookingRV = (RecyclerView) view.findViewById(R.id.hotelBookingRV);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Hotel Booking Sites");

        HotelsTravelModel model = new HotelsTravelModel();
        model.setName("Make My Trip");
        model.setUrl("www.makemytrip.com");
        model.setDesc("Make my trip is a website where you can book hotels, buses, trains and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.makemytrip.com");

        hotelsModels.add(model);

        model = new HotelsTravelModel();
        model.setName("Oyo rooms");
        model.setUrl("www.oyorooms.com");
        model.setDesc("Oyo rooms is a website where you can book hotels, buses, trains and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.oyorooms.com");

        hotelsModels.add(model);

        model = new HotelsTravelModel();
        model.setName("Booking.com");
        model.setUrl("www.booking.com");
        model.setDesc("Booking.com is a website where you can book hotels, buses, trains and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.booking.com");

        hotelsModels.add(model);

        model = new HotelsTravelModel();
        model.setName("Air BNB");
        model.setUrl("www.airbnb.com");
        model.setDesc("Air BNB is a website where you can book hotels, buses, trains and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.airbnb.com");

        hotelsModels.add(model);

        model = new HotelsTravelModel();
        model.setName("GoIbibo");
        model.setUrl("www.goibibo.com");
        model.setDesc("GoIbibo is a website where you can book hotels, buses, trains and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.goibibo.com");

        hotelsModels.add(model);


        mAdapter = new HotelsAdapter(hotelsModels);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        hotelBookingRV.setLayoutManager(linearLayoutManager);

        hotelBookingRV.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));
        ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(mAdapter);
        adapter.setDuration(400);

        hotelBookingRV.setAdapter(adapter);


        return view;
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
