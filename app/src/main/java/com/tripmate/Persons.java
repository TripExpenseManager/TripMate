package com.tripmate;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.models.PersonModel;
import android.app.models.PersonWiseExpensesSummaryModel;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;


/**
 * A simple {@link Fragment} subclass.
 */
public class Persons extends Fragment {

    ArrayList<PersonWiseExpensesSummaryModel> personsList = new ArrayList<>();

    RecyclerView persons_recyclerview;
    String trip_id;

    PersonsAdapter mAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_persons, container, false);


        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        persons_recyclerview = (RecyclerView) view.findViewById(R.id.persons_recyclerview);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        personsList = dataBaseHelper.getPersonWiseExpensesSummaryForPersonsFragment(trip_id);
        mAdapter = new PersonsAdapter(personsList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        persons_recyclerview.setLayoutManager(mLayoutManager);

        persons_recyclerview.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(12f)));
        ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(mAdapter);
        adapter.setDuration(200);

        persons_recyclerview.setAdapter(adapter);

        final FloatingActionButton fab = (FloatingActionButton)  getActivity().findViewById(R.id.fab);
        persons_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 && fab.isShown())
                {
                    fab.hide();
                }else if(((TabLayout)getActivity().findViewById(R.id.tabs)).getSelectedTabPosition() == 0){
                    fab.show();

                }
            }
        });

        return view;
    }


    class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.MyViewHolder> {

        private ArrayList<PersonWiseExpensesSummaryModel> personsList;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView title_tv,expend_tv,due_tv,textViewOptions;
            ImageView imageView;
            LinearLayout clickLL;

            MyViewHolder(View view) {
                super(view);
                title_tv = (TextView) view.findViewById(R.id.person_name);
                expend_tv = (TextView) view.findViewById(R.id.amount_expend);
                due_tv = (TextView) view.findViewById(R.id.amount_due);
                textViewOptions = (TextView) view.findViewById(R.id.textViewOptions);
                imageView = (ImageView) view.findViewById(R.id.imageView);
                clickLL = (LinearLayout) view.findViewById(R.id.clickLL);

            }
        }


        PersonsAdapter(ArrayList<PersonWiseExpensesSummaryModel> personsList) {
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

            final PersonWiseExpensesSummaryModel model = personsList.get(position);

            if(model.getAdmin()==1){
                holder.title_tv.setText(model.getName()+" (Admin)");
            }else{
                holder.title_tv.setText(model.getName());
            }

            if(model.getTotalAmountRemaining()>=0){
                holder.due_tv.setTextColor(getResources().getColor(R.color.green));
                holder.due_tv.setText("(+"+model.getTotalAmountRemaining()+")");
            }else{
                holder.due_tv.setTextColor(getResources().getColor(R.color.red));
                holder.due_tv.setText("("+model.getTotalAmountRemaining()+")");
            }

            holder.expend_tv.setText(model.getTotalAmountGiven()+"");

            String firstLetter = String.valueOf(model.getName().charAt(0));

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            int color = generator.getColor(model.getName());
            //int color = generator.getRandomColor();

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, color); // radius in px

            holder.imageView.setImageDrawable(drawable);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View promptsView = li.inflate(R.layout.person_details_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setView(promptsView);


                    final ImageView user_first_letter = (ImageView) promptsView.findViewById(R.id.user_first_letter);
                    final TextView user_profile_name = (TextView) promptsView.findViewById(R.id.user_profile_name);
                    final TextView user_email_id = (TextView) promptsView.findViewById(R.id.user_email_id);
                    final TextView user_mobile_no = (TextView) promptsView.findViewById(R.id.user_mobile_no);

                    final CardView group_details_cardview = (CardView) promptsView.findViewById(R.id.group_details_cardview);

                    final TextView group_deposit_amount_received = (TextView) promptsView.findViewById(R.id.group_deposit_amount_received);
                    final TextView group_deposit_amount_spent = (TextView) promptsView.findViewById(R.id.group_deposit_amount_spent);
                    final TextView group_deposit_amount_remaining = (TextView) promptsView.findViewById(R.id.group_deposit_amount_remaining);
                    final TextView deposit_amount_given = (TextView) promptsView.findViewById(R.id.deposit_amount_given);
                    final TextView deposit_amount_spent = (TextView) promptsView.findViewById(R.id.deposit_amount_spent);
                    final TextView deposit_amount_remaining = (TextView) promptsView.findViewById(R.id.deposit_amount_remaining);
                    final TextView personal_amount_given = (TextView) promptsView.findViewById(R.id.personal_amount_given);
                    final TextView personal_amount_spent = (TextView) promptsView.findViewById(R.id.personal_amount_spent);
                    final TextView total_amount_given = (TextView) promptsView.findViewById(R.id.total_amount_given);
                    final TextView total_amount_spent = (TextView) promptsView.findViewById(R.id.total_amount_spent);
                    final TextView total_amount_remaining = (TextView) promptsView.findViewById(R.id.total_amount_remaining);

                    Double total_deposit_received = 0.0,total_deposit_spent = 0.0,total_deposit_remaining = 0.0;

                    for(int k = 0;k<personsList.size();k++){
                        total_deposit_received += personsList.get(k).getDepositAmountGiven();
                        total_deposit_spent += personsList.get(k).getDepositAmountSpent();
                        total_deposit_remaining += personsList.get(k).getDepositAmountRemaining();
                    }


                    String firstLetter = String.valueOf(model.getName().charAt(0));
                    ColorGenerator generator = ColorGenerator.MATERIAL;
                    int color = generator.getColor(model.getName());
                    TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color);
                    user_first_letter.setImageDrawable(drawable);

                    if(model.getAdmin()==1){
                        user_profile_name.setText(model.getName()+" (Admin)");
                        group_details_cardview.setVisibility(View.VISIBLE);

                        group_deposit_amount_received.setText(RoundOff(total_deposit_received)+"");
                        group_deposit_amount_spent.setText(RoundOff(total_deposit_spent)+"");
                        group_deposit_amount_remaining.setText(RoundOff(total_deposit_remaining)+"");


                    }else{
                        user_profile_name.setText(model.getName());
                        group_details_cardview.setVisibility(View.GONE);
                    }

                    if(!model.getMobile().equalsIgnoreCase("")){
                        user_mobile_no.setText(model.getMobile());
                    }else{
                        user_mobile_no.setVisibility(View.GONE);
                    }

                    //calling the user on clicking the mobile number
                    user_mobile_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+model.getMobile()));
                                startActivity(callIntent);
                            }else{
                                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE},1);
                            }
                        }
                    });

                    if(!model.getEmail().equalsIgnoreCase("")){
                        user_email_id.setText(model.getEmail());
                    }else{
                        user_email_id.setVisibility(View.GONE);
                    }

                    deposit_amount_given.setText(model.getDepositAmountGiven()+"");

                    if(model.getDepositAmountSpent()>model.getDepositAmountGiven()){
                        deposit_amount_spent.setTextColor(getResources().getColor(R.color.red));
                        deposit_amount_spent.setText(model.getDepositAmountSpent()+"");
                    }else{
                        deposit_amount_spent.setTextColor(getResources().getColor(R.color.green));
                        deposit_amount_spent.setText(model.getDepositAmountSpent()+"");
                    }

                    if(model.getDepositAmountRemaining()>=0){
                        deposit_amount_remaining.setTextColor(getResources().getColor(R.color.green));
                        deposit_amount_remaining.setText("+"+model.getDepositAmountRemaining());
                    }else{
                        deposit_amount_remaining.setTextColor(getResources().getColor(R.color.red));
                        deposit_amount_remaining.setText(model.getDepositAmountRemaining()+"");
                    }

                    personal_amount_given.setText(model.getPersonalAmountGiven()+"");

                    if(model.getPersonalAmountSpent()>model.getPersonalAmountGiven()){
                        personal_amount_spent.setTextColor(getResources().getColor(R.color.red));
                        personal_amount_spent.setText(model.getPersonalAmountSpent()+"");
                    }else{
                        personal_amount_spent.setTextColor(getResources().getColor(R.color.green));
                        personal_amount_spent.setText(model.getPersonalAmountSpent()+"");
                    }

                    total_amount_given.setText(model.getTotalAmountGiven()+"");

                    if(model.getTotalAmountSpent()>model.getTotalAmountGiven()){
                        total_amount_spent.setTextColor(getResources().getColor(R.color.red));
                        total_amount_spent.setText(model.getTotalAmountSpent()+"");
                    }else{
                        total_amount_spent.setTextColor(getResources().getColor(R.color.green));
                        total_amount_spent.setText(model.getTotalAmountSpent()+"");
                    }

                    if(model.getTotalAmountRemaining()>=0){
                        total_amount_remaining.setTextColor(getResources().getColor(R.color.green));
                        total_amount_remaining.setText("+"+model.getTotalAmountRemaining());
                    }else{
                        total_amount_remaining.setTextColor(getResources().getColor(R.color.red));
                        total_amount_remaining.setText(""+model.getTotalAmountRemaining());
                    }

                    alertDialogBuilder.setCancelable(true);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
                    alertDialog.show();
                }
            };

            holder.clickLL.setOnClickListener(listener);
            holder.imageView.setOnClickListener(listener);

            //popup menu
            holder.textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(), holder.textViewOptions);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_persons_item);

                    MenuItem make_admin_item  = popup.getMenu().getItem(0);
                    MenuItem make_call = popup.getMenu().getItem(1);
                    MenuItem delete = popup.getMenu().getItem(3);


                    //setting the visibilities of popup menu items
                    if(model.getAdmin()==1){
                        make_admin_item.setVisible(false);
                    }
                    else{
                        make_admin_item.setVisible(true);
                    }

                    if(model.getMobile().equalsIgnoreCase("")){
                        make_call.setVisible(false);
                    }else{
                        make_call.setVisible(true);
                    }

                    if(model.getCanRemove()){
                        delete.setVisible(true);
                    }else{
                        delete.setVisible(false);
                    }


                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.make_admin:


                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setCancelable(true);
                                    builder.setTitle("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            makeAdmin(model);
                                            dialog.cancel();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.setMessage("You want to make "+model.getName()+" as Admin?");
                                    builder.setCancelable(false);
                                    AlertDialog dialog = builder.create();
                                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                                    dialog.show();
                                    break;
                                case R.id.call:

                                    if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:"+model.getMobile()));
                                        startActivity(callIntent);
                                    }else{
                                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE},1);
                                    }
                                    break;
                                case R.id.edit:
                                    editPerson(model);
                                    break;
                                case R.id.delete:

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                                    TextView customTitleTextview = new TextView(getActivity());
                                    customTitleTextview.setTextSize(20);
                                    customTitleTextview.setText("Are you sure?");
                                    customTitleTextview.setTextColor(getResources().getColor(R.color.red));
                                    customTitleTextview.setPadding(10,40,10,10);
                                    customTitleTextview.setGravity(Gravity.CENTER);

                                    builder1.setCustomTitle(customTitleTextview);

                                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());

                                            if(dataBaseHelper.removePerson(model,trip_id)){
                                                Snackbar.make(getActivity().findViewById(R.id.fab),"Deleted "+model.getName()+" successfully", Snackbar.LENGTH_LONG).show();
                                                onResume();
                                            }else{
                                                Snackbar.make(getActivity().findViewById(R.id.fab),"Unexpected error occurred!", Snackbar.LENGTH_LONG).show();
                                            }

                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder1.setMessage("You want to delete "+model.getName()+" from Trip?");
                                    builder1.setCancelable(false);
                                    AlertDialog dialog1 = builder1.create();
                                    dialog1.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                                    dialog1.show();

                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return personsList.size();
        }
    }

    void makeAdmin(final PersonWiseExpensesSummaryModel model){

        String pastAdmin = "";
        for(int i=0;i<personsList.size();i++){
            if(personsList.get(i).getAdmin()==1){
                pastAdmin = personsList.get(i).getName();
                break;
            }
        }

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        dataBaseHelper.addAsAdmin(trip_id,model.getName(),pastAdmin);
        personsList = dataBaseHelper.getPersonWiseExpensesSummaryForPersonsFragment(trip_id);

        mAdapter = new PersonsAdapter(personsList);

        ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(mAdapter);
        adapter.setDuration(200);

        persons_recyclerview.setAdapter(adapter);

        Snackbar.make(getActivity().findViewById(R.id.fab),model.getName() +" is now Admin", Snackbar.LENGTH_LONG).show();

    }

    void editPerson(final PersonWiseExpensesSummaryModel model){

        final View view = getActivity().getLayoutInflater().inflate(R.layout.layout_add_person, null);
        final TextInputLayout tilPersonName, tilPersonDeposit, tilPersonMobile, tilPersonEmail;
        tilPersonName = (TextInputLayout) view.findViewById(R.id.tilPersonName);
        tilPersonDeposit = (TextInputLayout) view.findViewById(R.id.tilPersonDeposit);
        tilPersonMobile = (TextInputLayout) view.findViewById(R.id.tilPersonMobile);
        tilPersonEmail = (TextInputLayout) view.findViewById(R.id.tilPersonEmail);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(view).setTitle("Edit Person")
                .setPositiveButton("OK",null)
                .setNegativeButton("CANCEL", null)
                .create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        alertDialog.show();


        tilPersonName.getEditText().setText(model.getName());
        tilPersonName.setEnabled(false);

        tilPersonDeposit.getEditText().setText(model.getDepositAmountGiven()+"");
        tilPersonMobile.getEditText().setText(model.getMobile());
        tilPersonEmail.getEditText().setText(model.getEmail());



        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PersonModel personModel = new PersonModel();

                String tempName = tilPersonName.getEditText().getText().toString().trim().substring(0, 1).toUpperCase() + tilPersonName.getEditText().getText().toString().trim().substring(1);
                personModel.setName(tempName);

                if(tilPersonDeposit.getEditText().getText().toString().equalsIgnoreCase("")){
                    personModel.setDeposit(0.0);
                }else{
                    personModel.setDeposit(Double.valueOf(tilPersonDeposit.getEditText().getText().toString()));
                }
                personModel.setMobile(tilPersonMobile.getEditText().getText().toString());
                personModel.setEmail(tilPersonEmail.getEditText().getText().toString());

                DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());

                dataBaseHelper.editPerson(trip_id,personModel);

                personsList = dataBaseHelper.getPersonWiseExpensesSummaryForPersonsFragment(trip_id);

                mAdapter = new PersonsAdapter(personsList);
                ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(mAdapter);
                adapter.setDuration(100);
                persons_recyclerview.setAdapter(adapter);

                Snackbar.make(getActivity().findViewById(R.id.fab), "Person details edited successfully", Snackbar.LENGTH_LONG).show();

                alertDialog.dismiss();
            }
        });
    }

    public Double RoundOff(Double d){
        return Math.round(d * 100.0) / 100.0;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_persons_fragment,menu);

        final ImageView reloadButton = (ImageView) menu.findItem(R.id.action_refresh).getActionView();

        final Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);

        if (reloadButton != null) {
            reloadButton.setImageResource(R.drawable.icon_refresh);
            reloadButton.setPadding(10,10,10,10);

            // Set onClick listener for button press action
            reloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(rotation);

                    DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());

                    personsList = dataBaseHelper.getPersonWiseExpensesSummaryForPersonsFragment(trip_id);
                    mAdapter = new PersonsAdapter(personsList);
                    ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(mAdapter);
                    adapter.setDuration(200);

                    persons_recyclerview.setAdapter(adapter);

                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied to make calls", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //refreshing the contents onResume
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        personsList = dataBaseHelper.getPersonWiseExpensesSummaryForPersonsFragment(trip_id);

        mAdapter = new PersonsAdapter(personsList);
        persons_recyclerview.setAdapter(mAdapter);

        Log.d("Tab Number Persons",((TabLayout)getActivity().findViewById(R.id.tabs)).getSelectedTabPosition()+"");

    }

}
