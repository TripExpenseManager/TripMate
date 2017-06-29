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
public class TravelBookingSitesFragment extends Fragment {

    RecyclerView travelBookingRV;

    TravelAdapter mAdapter;
    ArrayList<HotelsTravelModel> travelModels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel_booking_sites, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Travel Booking Sites");

        travelBookingRV = (RecyclerView) view.findViewById(R.id.travelBookingRV);

        HotelsTravelModel model = new HotelsTravelModel();
        model.setName("Make My Trip");
        model.setUrl("www.makemytrip.com");
        model.setDesc("Make my trip is a website where you can book hotels, buses, trains and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.makemytrip.com");

        travelModels.add(model);

        model = new HotelsTravelModel();
        model.setName("IRCTC");
        model.setUrl("www.irctc.co.in");
        model.setDesc("IRCTC is a website where you can book trains and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.irctc.co.in");

        travelModels.add(model);

        model = new HotelsTravelModel();
        model.setName("redBus");
        model.setUrl("www.redbus.com");
        model.setDesc("redBus is a website where you can book buses and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.redbus.com");

        travelModels.add(model);

        model = new HotelsTravelModel();
        model.setName("AbhiBus");
        model.setUrl("www.abhibus.com");
        model.setDesc("AbhiBus is a website where you can book buses and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.abhibus.com");

        travelModels.add(model);

        model = new HotelsTravelModel();
        model.setName("GoIbibo");
        model.setUrl("www.goibibo.com");
        model.setDesc("GoIbibo is a website where you can book hotels, buses, trains and ensures a hassle free trip for you");
        model.setReferenseUrl("https://www.goibibo.com");

        travelModels.add(model);


        mAdapter = new TravelAdapter(travelModels);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        travelBookingRV.setLayoutManager(linearLayoutManager);

        travelBookingRV.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));
        ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(mAdapter);
        adapter.setDuration(400);

        travelBookingRV.setAdapter(adapter);

        return view;
    }




    class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder>{

        private ArrayList<HotelsTravelModel> travelModels = new ArrayList<>();

        public TravelAdapter(ArrayList<HotelsTravelModel> travelModels) {
            this.travelModels = travelModels;
        }


        public  class TravelViewHolder extends RecyclerView.ViewHolder{

            TextView nameTv,urlTv,descTv;
            ImageView imageView;
            CardView hotelsCardView;

            public TravelViewHolder(View itemView) {
                super(itemView);
                nameTv = (TextView) itemView.findViewById(R.id.nameTv);
                urlTv = (TextView) itemView.findViewById(R.id.urlTv);
                descTv = (TextView) itemView.findViewById(R.id.descTv);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                hotelsCardView = (CardView) itemView.findViewById(R.id.hotelsCardView);


            }
        }

        @Override
        public TravelAdapter.TravelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hotels_travel_row_view1, parent, false);
            return new TravelViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final TravelAdapter.TravelViewHolder holder, int position) {
            final HotelsTravelModel hotelModel = travelModels.get(position);

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
            return travelModels.size();
        }
    }

}
