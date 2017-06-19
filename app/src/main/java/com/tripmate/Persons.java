package com.tripmate;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;


/**
 * A simple {@link Fragment} subclass.
 */
public class Persons extends Fragment {

    ArrayList<PersonModel> personsList = new ArrayList<PersonModel>();

    RecyclerView persons_recyclerview;
    String trip_id;

    PersonsAdapter mAdapter = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        personsList = MainActivity.AppBase.getPersons(trip_id);
        mAdapter = new PersonsAdapter(personsList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_persons, container, false);
        persons_recyclerview = (RecyclerView) view.findViewById(R.id.persons_recyclerview);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        persons_recyclerview.setLayoutManager(mLayoutManager);

        persons_recyclerview.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));

        ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(mAdapter);
        adapter.setDuration(400);
        persons_recyclerview.setAdapter(adapter);

        return view;
    }


    public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.MyViewHolder> {

        private ArrayList<PersonModel> personsList;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView title_tv,expend_tv,due_tv,textViewOptions;
            ImageView imageView;

            public MyViewHolder(View view) {
                super(view);
                title_tv = (TextView) view.findViewById(R.id.person_name);
                expend_tv = (TextView) view.findViewById(R.id.amount_expend);
                due_tv = (TextView) view.findViewById(R.id.amount_due);
                textViewOptions = (TextView) view.findViewById(R.id.textViewOptions);
                imageView = (ImageView) view.findViewById(R.id.imageView);

            }
        }


        public PersonsAdapter(ArrayList<PersonModel> personsList) {
            this.personsList = personsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.persons_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            PersonModel model = personsList.get(position);
            holder.title_tv.setText(model.getName());
            holder.expend_tv.setText("12548");
            holder.due_tv.setText("+12548");


            String firstLetter = String.valueOf(model.getName().charAt(0));

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            int color = generator.getColor(model.getName());
            //int color = generator.getRandomColor();

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, color); // radius in px

            holder.imageView.setImageDrawable(drawable);

            holder.textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(), holder.textViewOptions);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.persons_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.make_admin:
                                    //handle menu1 click
                                    break;
                                case R.id.call:
                                    //handle menu2 click
                                    break;
                                case R.id.edit:
                                    //handle menu3 click
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return personsList.size();
        }
    }


}
