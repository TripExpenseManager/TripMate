package com.tripmate;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.models.CurrencyModel;
import android.app.models.NotesModel;
import android.app.models.PersonModel;
import android.app.models.TodoModel;
import android.app.models.TripModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

public class AddTripNew extends AppCompatActivity implements OnStartDragListener {

    TextInputLayout tilTripName;
    ImageView ivDate;
    TextView tvDate;
    int mYear,mMonth,mDay;
    DatePickerDialog.OnDateSetListener dateSetListener;
    LinearLayout llDate;
    String trip_id="",trip_name="",trip_date="",trip_places="";

    LinearLayout llAddPlacesToVisit;
    RecyclerView rvPlacesToVisit;
    ArrayList<String> placesToVisitArrayList = new ArrayList<>();
    PlacesToVisitAdapter placesToVisitAdapter;
    ImageView ivShowOrHide;
    boolean isShown = true;

    int posOfFocus = -1;


    boolean tripDescIsEditable = false;
    ItemTouchHelper mItemTouchHelper;



    RecyclerView rvPersonDetails;
    ArrayList<PersonModel> tripPersonModels = new ArrayList<>();
    FloatingActionButton fabAddPerson;

    PersonsAdapter personsAdapter;

    TextView tvCurrencyName;
    LinearLayout llCurrency;

    RecyclerView rvCurrencies;
    CurrenciesAdapter currenciesAdapter;
    CurrencyModel selectedCurrency;
    android.support.v7.app.AlertDialog alertDialogCurrencies;
    ArrayList<CurrencyModel> currencyArrayList = new ArrayList<>();
    MaterialSearchView currencySearchView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

        setContentView(R.layout.activity_add_trip_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Create New Trip");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tilTripName = (TextInputLayout) findViewById(R.id.tilTripName);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvCurrencyName = (TextView)findViewById(R.id.tvCurrencyName);
        ivDate = (ImageView) findViewById(R.id.ivDate);
        llDate = (LinearLayout) findViewById(R.id.llDate);
        rvPlacesToVisit = (RecyclerView) findViewById(R.id.rvPlacesToVisit);
        llAddPlacesToVisit = (LinearLayout) findViewById(R.id.llAddPlacesToVisit);
        llCurrency = (LinearLayout) findViewById(R.id.llCurrency);

        // Getting Current Date and Time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // Setting Default Time and Date
        trip_date = mDay + "-" + (mMonth + 1) + "-" + mYear;
        tvDate.setText(trip_date);

        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        Long todaysOnlyDateValue = null;

        try {
            Date todaysDate = f.parse(trip_date);
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


            }
        };

        final Long finalTodaysOnlyDateValue = todaysOnlyDateValue;
        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTripNew.this,dateSetListener,mYear,mMonth,mDay);
               // datePickerDialog.getDatePicker().setMinDate(finalTodaysOnlyDateValue);
                datePickerDialog.getWindow().setWindowAnimations(R.style.DialogAnimationUpDown);
                datePickerDialog.show();
            }
        });

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

        rvPersonDetails = (RecyclerView) findViewById(R.id.rvPersonDetails);
        fabAddPerson = (FloatingActionButton) findViewById(R.id.fabAddPerson);

        // Persons
        rvPersonDetails.setLayoutManager(new LinearLayoutManager(this));
        personsAdapter = new PersonsAdapter(this);
        rvPersonDetails.setAdapter(personsAdapter);


        // Add PersonModel
        fabAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View view = getLayoutInflater().inflate(R.layout.layout_add_person, null);
                final TextInputLayout tilPersonName, tilPersonDeposit, tilPersonMobile, tilPersonEmail;
                final AutoCompleteTextView actvPersonName,actvPersonDeposit,actvPersonMobile,actvPersonEmail;
                TextView tvDepositMoneyCurrencyNotice;

                tilPersonName = (TextInputLayout) view.findViewById(R.id.tilPersonName);
                tilPersonDeposit = (TextInputLayout) view.findViewById(R.id.tilPersonDeposit);
                tilPersonMobile = (TextInputLayout) view.findViewById(R.id.tilPersonMobile);
                tilPersonEmail = (TextInputLayout) view.findViewById(R.id.tilPersonEmail);
                tvDepositMoneyCurrencyNotice = (TextView) view.findViewById(R.id.tvDepositMoneyCurrencyNotice);


                actvPersonName = (AutoCompleteTextView) view.findViewById(R.id.actvPersonName);
                actvPersonDeposit = (AutoCompleteTextView) view.findViewById(R.id.actvPersonDeposit);
                actvPersonMobile = (AutoCompleteTextView) view.findViewById(R.id.actvPersonMobile);
                actvPersonEmail = (AutoCompleteTextView) view.findViewById(R.id.actvPersonEmail);

                DataBaseHelper db = new DataBaseHelper(AddTripNew.this);
                ArrayList<PersonModel> allPersons = db.getAllPersons();


                tvDepositMoneyCurrencyNotice.setText("*Deposit money value is in "+Utils.getCorrespondingCurrencyName(selectedCurrency.getCurrencyCode())+" - "+selectedCurrency.getCurrencyCode());

                final ArrayList<String> allPersonsNames = new ArrayList<>();
                final ArrayList<String> allPersonsMobiles= new ArrayList<>();
                final ArrayList<String> allPersonsEmails = new ArrayList<>();

                for(PersonModel p : allPersons){
                    allPersonsNames.add(p.getName());
                    allPersonsMobiles.add(p.getMobile());
                    allPersonsEmails.add(p.getEmail());
                }


                ArrayAdapter personNamesAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_list_item_1,allPersonsNames);
                ArrayAdapter personMobilesAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_list_item_1,allPersonsMobiles);
                ArrayAdapter personEmailsAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_list_item_1,allPersonsEmails);

                actvPersonName.setAdapter(personNamesAdapter);
                actvPersonMobile.setAdapter(personMobilesAdapter);
                actvPersonEmail.setAdapter(personEmailsAdapter);


                actvPersonName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String personName = ((TextView)view).getText().toString().trim();
                        int index = allPersonsNames.indexOf(personName);
                        if(index!=-1) {
                            actvPersonMobile.setText(allPersonsMobiles.get(index));
                            actvPersonEmail.setText(allPersonsEmails.get(index));
                        }
                    }
                });

                final AlertDialog alertDialog = new AlertDialog.Builder(AddTripNew.this).setView(view).setTitle("Add Person")
                        .setPositiveButton("OK",null)
                        .setNegativeButton("CANCEL", null)
                        .create();

                alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
                alertDialog.show();


                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        rvPlacesToVisit.clearFocus();
                        tilTripName.clearFocus();
                        hideSoftKeyboard();

                        if (tilPersonName.getEditText().getText().toString().equals("")) {
                        tilPersonName.setError("Enter Name");

                    }
                    else {
                        int res=0;
                        for(int i=0;i<tripPersonModels.size();i++){
                            if(tripPersonModels.get(i).getName().equalsIgnoreCase(tilPersonName.getEditText().getText().toString().trim())){
                                tilPersonName.setError(tilPersonName.getEditText().getText().toString()+" already exist!");
                                res=1;
                            }
                        }
                        if(res==0){

                            PersonModel personModel = new PersonModel();

                            String tempName = tilPersonName.getEditText().getText().toString().trim().substring(0, 1).toUpperCase() + tilPersonName.getEditText().getText().toString().trim().substring(1);
                            personModel.setName(tempName);

                            try{
                                personModel.setDeposit(Double.valueOf(tilPersonDeposit.getEditText().getText().toString()));
                            }catch (Exception e){
                                personModel.setDeposit(0.0);
                            }


                            personModel.setMobile(tilPersonMobile.getEditText().getText().toString());
                            personModel.setEmail(tilPersonEmail.getEditText().getText().toString());
                            personModel.setAdmin(0);
                            tripPersonModels.add(personModel);
                            // Refresh Adapter
                            personsAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    }
                    }
                });
            }
        });

        int should_display_add_person = app_preferences.getInt("should_display_add_person",1);
        if(should_display_add_person == 1){
            TapTargetView.showFor(this,
                    TapTarget.forView(findViewById(R.id.fabAddPerson), "Add persons", "Click on the plus(+) button to add persons.")
                            .cancelable(false)
                            .descriptionTextColor(R.color.white)
                            .titleTextColor(R.color.white)
                            .transparentTarget(true)
                            .textTypeface(Typeface.SANS_SERIF),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                        }
                    });

            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putInt("should_display_add_person", 0);
            editor.apply();
        }

        String defaultTripCurrency = app_preferences.getString("default_currency_for_trips","INR");
        selectedCurrency = new CurrencyModel(Utils.getCorrespondingCurrencyName(defaultTripCurrency),defaultTripCurrency);
        tvCurrencyName.setText(selectedCurrency.getCurrencyName() +" - " + selectedCurrency.getCurrencyCode());

        currencyArrayList = Utils.getCurrienciesList();

        llCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCurrenciesDialog();
            }
        });




    }


    void openCurrenciesDialog(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.currency_dialog, null);
        android.support.v7.app.AlertDialog.Builder alertDialogBuilderCurrencies = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilderCurrencies.setView(promptsView);


        alertDialogBuilderCurrencies.setCancelable(true);
        alertDialogCurrencies = alertDialogBuilderCurrencies.create();
        alertDialogCurrencies.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        alertDialogCurrencies.show();

        rvCurrencies = (RecyclerView) promptsView.findViewById(R.id.rvCurrencies);
        rvCurrencies.setLayoutManager(new LinearLayoutManager(this));
        currenciesAdapter =new CurrenciesAdapter(currencyArrayList);
        rvCurrencies.setAdapter(currenciesAdapter);


        currencySearchView = (MaterialSearchView) promptsView.findViewById(R.id.currencySearchView);

        ImageView ivCurrencySearch = (ImageView) promptsView.findViewById(R.id.ivCurrencySearch);
        ivCurrencySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencySearchView.showSearch(false);
            }
        });

        currencySearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                InputMethodManager imm = (InputMethodManager) AddTripNew.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currencySearchView.getWindowToken(), 0);

                ArrayList<CurrencyModel> tempCurrencyArrayList = new ArrayList<>();
                for(CurrencyModel c : currencyArrayList){
                    if(c.getCurrencyName().toLowerCase().contains(query.toLowerCase())||
                            c.getCurrencyCode().toLowerCase().contains(query.toLowerCase())){
                        tempCurrencyArrayList.add(c);
                    }
                }
                currenciesAdapter =new CurrenciesAdapter(tempCurrencyArrayList);
                rvCurrencies.setAdapter(currenciesAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("tag",newText);
                ArrayList<CurrencyModel> tempCurrencyArrayList = new ArrayList<>();
                for(CurrencyModel c : currencyArrayList){
                    if(c.getCurrencyName().toLowerCase().contains(newText.toLowerCase())||
                            c.getCurrencyCode().toLowerCase().contains(newText.toLowerCase())){
                        tempCurrencyArrayList.add(c);
                    }
                }
                currenciesAdapter =new CurrenciesAdapter(tempCurrencyArrayList);
                rvCurrencies.setAdapter(currenciesAdapter);

                return false;
            }
        });






    }

    class CurrenciesAdapter extends  RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>{

        ArrayList<CurrencyModel> currencyAdapterArrayList = new ArrayList<>();

        CurrenciesAdapter(ArrayList<CurrencyModel> currencyAdapterArrayList) {
            this.currencyAdapterArrayList = currencyAdapterArrayList;
        }

        class CurrencyViewHolder extends RecyclerView.ViewHolder{
            TextView tvLiCurrencyName;
            CurrencyViewHolder(View itemView) {
                super(itemView);
                tvLiCurrencyName = (TextView) itemView.findViewById(R.id.tvLiCurrencyName);


            }
        }

        @Override
        public CurrencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_currency,parent,false);
            return new CurrencyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CurrencyViewHolder holder,int position) {
            holder.tvLiCurrencyName.setText(currencyAdapterArrayList.get(position).getCurrencyName() + " - " +
                    currencyAdapterArrayList.get(position).getCurrencyCode());
            holder.tvLiCurrencyName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCurrency = new CurrencyModel(currencyAdapterArrayList.get(holder.getAdapterPosition()).getCurrencyName(),
                            currencyAdapterArrayList.get(holder.getAdapterPosition()).getCurrencyCode());
                    alertDialogCurrencies.dismiss();

                    tvCurrencyName.setText(selectedCurrency.getCurrencyName() +" - " + selectedCurrency.getCurrencyCode());

                }
            });

        }

        @Override
        public int getItemCount() {
            return currencyAdapterArrayList.size();
        }
    }

    class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.MyViewHolder> {


        Context mContext;

        PersonsAdapter(Context mContext) {
            this.mContext = mContext;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tvPersonName,tvPhone,tvDeposit,tvEmail,textViewOptions;
            ImageView imageView;
            LinearLayout clickLL,llPhone,llDeposit,llEmail;

            MyViewHolder(View view) {
                super(view);
                tvPersonName = (TextView) view.findViewById(R.id.tvPersonName);
                tvPhone = (TextView) view.findViewById(R.id.tvPhone);
                tvDeposit = (TextView) view.findViewById(R.id.tvDeposit);
                tvEmail = (TextView) view.findViewById(R.id.tvEmail);
                textViewOptions = (TextView) view.findViewById(R.id.textViewOptions);
                imageView = (ImageView) view.findViewById(R.id.imageView);
                clickLL = (LinearLayout) view.findViewById(R.id.clickLL);
                llPhone = (LinearLayout) view.findViewById(R.id.llPhone);
                llDeposit = (LinearLayout) view.findViewById(R.id.llDeposit);
                llEmail = (LinearLayout) view.findViewById(R.id.llEmail);


            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_person_add_trip, parent, false);

            return new MyViewHolder(itemView);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            // Loading data to view holders
            final PersonModel personModel = tripPersonModels.get(holder.getAdapterPosition());
            if(personModel.getMobile().equalsIgnoreCase("")){
                holder.llPhone.setVisibility(View.GONE);
            }else{
                holder.llPhone.setVisibility(View.VISIBLE);
                holder.tvPhone.setText(personModel.getMobile());
            }
            if(personModel.getEmail().equalsIgnoreCase("")){
                holder.llEmail.setVisibility(View.GONE);
            }else{
                holder.llEmail.setVisibility(View.VISIBLE);
                holder.tvEmail.setText(personModel.getEmail());
            }
            if(personModel.getDeposit()==0.0){
                holder.llDeposit.setVisibility(View.GONE);
            }else{
                holder.llDeposit.setVisibility(View.VISIBLE);
                holder.tvDeposit.setText(personModel.getDeposit()+" "+selectedCurrency.getCurrencyCode());
            }

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate color based on a key (same key returns the same color), useful for list/grid views
            String personName = personModel.getName();
            int color = generator.getColor(personName);
            TextDrawable drawable = TextDrawable.builder().buildRound((personName.charAt(0)+"").toUpperCase(),color);


            if(personModel.getAdmin() == 1){
                holder.tvPersonName.setText(personName + " (Admin)");
            }else{
                holder.tvPersonName.setText(personName);
            }

            holder.imageView.setImageDrawable(drawable);


            View.OnClickListener adminClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tripPersonModels.get(holder.getAdapterPosition()).setAdmin(1);
                    String adminName=tripPersonModels.get(holder.getAdapterPosition()).getName();

                    if(adminName.length()>20){
                        adminName = adminName.substring(0,17)+"...";
                    }
                    Snackbar.make(fabAddPerson,adminName+ " is set as admin for the trip. He should look after all the money related matters.", Snackbar.LENGTH_LONG).show();

                    for(int j=0;j<tripPersonModels.size();j++){
                        if(j!=holder.getAdapterPosition()){
                            tripPersonModels.get(j).setAdmin(0);
                        }
                    }
                    if(personsAdapter!=null){
                        personsAdapter.notifyDataSetChanged();
                    }

                }
            };


            holder.clickLL.setOnClickListener(adminClickListener);
            holder.imageView.setOnClickListener(adminClickListener);


            //popup menu
            holder.textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(mContext, holder.textViewOptions);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_persons_add_trip);

                    MenuItem edit = popup.getMenu().getItem(0);
                    MenuItem delete = popup.getMenu().getItem(1);


                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    editPerson(personModel,holder.getAdapterPosition());
                                    break;
                                case R.id.delete:

                                    android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(mContext);

                                    TextView customTitleTextview = new TextView(mContext);
                                    customTitleTextview.setTextSize(20);
                                    customTitleTextview.setText("Are you sure?");
                                    customTitleTextview.setTextColor(getResources().getColor(R.color.red));
                                    customTitleTextview.setPadding(10,40,10,10);
                                    customTitleTextview.setGravity(Gravity.CENTER);

                                    builder1.setCustomTitle(customTitleTextview);

                                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            tripPersonModels.remove(holder.getAdapterPosition());
                                            personsAdapter.notifyItemRemoved(holder.getAdapterPosition());

                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder1.setMessage("You want to delete "+personModel.getName()+" from Trip?");
                                    builder1.setCancelable(false);
                                    android.support.v7.app.AlertDialog dialog1 = builder1.create();
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
            return tripPersonModels.size();
        }
    }

    void editPerson(final PersonModel personModel , final int position){

        final View view = getLayoutInflater().inflate(R.layout.layout_add_person, null);
        final TextInputLayout tilPersonName, tilPersonDeposit, tilPersonMobile, tilPersonEmail;
        TextView tvDepositMoneyCurrencyNotice;
        tilPersonName = (TextInputLayout) view.findViewById(R.id.tilPersonName);
        tilPersonDeposit = (TextInputLayout) view.findViewById(R.id.tilPersonDeposit);
        tilPersonMobile = (TextInputLayout) view.findViewById(R.id.tilPersonMobile);
        tilPersonEmail = (TextInputLayout) view.findViewById(R.id.tilPersonEmail);
        tvDepositMoneyCurrencyNotice = (TextView) view.findViewById(R.id.tvDepositMoneyCurrencyNotice);
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).setView(view).setTitle("Edit Person")
                .setPositiveButton("OK",null)
                .setNegativeButton("CANCEL", null)
                .create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        alertDialog.show();

        tvDepositMoneyCurrencyNotice.setText("*Deposit money value is in "+Utils.getCorrespondingCurrencyName(selectedCurrency.getCurrencyCode())+" - "+selectedCurrency.getCurrencyCode());


        tilPersonName.getEditText().setText(personModel.getName());
        tilPersonDeposit.getEditText().setText(personModel.getDeposit()+"");
        tilPersonMobile.getEditText().setText(personModel.getMobile());
        tilPersonEmail.getEditText().setText(personModel.getEmail());



        alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PersonModel personModel = new PersonModel();

                String tempName = tilPersonName.getEditText().getText().toString().trim().substring(0, 1).toUpperCase() + tilPersonName.getEditText().getText().toString().trim().substring(1);
                String tempMobile = tilPersonMobile.getEditText().getText().toString();
                String tempEmail = tilPersonEmail.getEditText().getText().toString();

                if(tempName.equalsIgnoreCase("")){
                    tilPersonName.setError("Please Enter Name");
                }else if(!tempMobile.equalsIgnoreCase("")  && !isValidMobile(tempMobile)){
                    tilPersonMobile.setError("Please Enter a valid Mobile no.");
                }else if(!tempEmail.equalsIgnoreCase("")  && !isValidEmailAddress(tempEmail)){
                    tilPersonEmail.setError("Please Enter a valid email address");
                }else{
                    personModel.setName(tempName);
                    if(tilPersonDeposit.getEditText().getText().toString().equalsIgnoreCase("")){
                        personModel.setDeposit(0.0);
                    }else{
                        personModel.setDeposit(Double.valueOf(tilPersonDeposit.getEditText().getText().toString()));
                    }
                    personModel.setMobile(tempMobile);
                    personModel.setEmail(tempEmail);

                    tripPersonModels.remove(position);
                    tripPersonModels.add(position,personModel);
                    personsAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }

            }
        });
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean isValidMobile(String phone) {

        boolean check;
        check = !Pattern.matches("[a-zA-Z]+", phone) && Pattern.matches("[0-9]+", phone) && !(phone.length() < 6 || phone.length() > 13);
        return check;

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

        }


        trip_date = tvDate.getText().toString();
        if(!validate(trip_name) ){
            tilTripName.setError("Please enter the Trip name");
        }else if(!validate(trip_places)){
            tilTripName.setErrorEnabled(false);
            Snackbar.make(findViewById(android.R.id.content),"Please Enter Places to Visit", Snackbar.LENGTH_LONG).show();
        }
        else if(tripPersonModels.size()!=0){

            int res = 0;
            for(int i=0;i<tripPersonModels.size();i++){
                if(tripPersonModels.get(i).getAdmin() == 1){
                    res=1;
                }
            }

            if(res == 1){

                String trip_id = "TRIP"+ UUID.randomUUID().toString();

                TripModel trip = new TripModel(trip_name,trip_places,trip_date);
                trip.setTrip_id(trip_id);
                trip.setTotal_persons(tripPersonModels.size());
                trip.setTripcurrency(selectedCurrency.getCurrencyCode());
                DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
                dataBaseHelper.addTrip(trip);
                //Adding places to visit checklist

                ArrayList<TodoModel> todoModels = new ArrayList<>();

                for(String  place : placesToVisitArrayList){
                    todoModels.add(new TodoModel(place,false));
                }

                String notesContent = encryptTodos(todoModels);
                NotesModel notesModel = new NotesModel();
                notesModel.setNote_TripId(trip_id);
                String note_id = "Notes" + UUID.randomUUID().toString();
                notesModel.setNote_Id(note_id);
                notesModel.setNote_Title("Places to Visit");
                notesModel.setNote_Body(notesContent);
                notesModel.setNote_ContentType(2);
                notesModel.setNote_Date(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                notesModel.setNote_ContentStatus("CheckList");

                dataBaseHelper.addNotes(notesModel);
                dataBaseHelper.addPersons(trip_id, tripPersonModels);
                Intent intent = new Intent(AddTripNew.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(this, "Trip created successfully", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Snackbar.make(fabAddPerson, "Please select a person as admin (by clicking on the name of the person)", Snackbar.LENGTH_LONG).show();
            }


        }else{
            Snackbar.make(fabAddPerson, "Please add atleast one person.", Snackbar.LENGTH_LONG).show();

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

    public String  encryptTodos(ArrayList<TodoModel> todoModels){

        String noteContent = "";

        for(TodoModel  todoModel : todoModels) {
            // Name of todos
            if(todoModel.getName()!=null)
                noteContent+=todoModel.getName().trim();
            noteContent+=Utils.DELIMETER_FOR_A_TODO;
            // Status of the todos
            if(todoModel.isCompleted())
                noteContent+="T";
            else
                noteContent+="F";

            // Delimiter after a todo is completed
            noteContent+=Utils.DELIMETER_FOR_TODOS;

        }

        return  noteContent;
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
        int i;
        for(i=0;i< placesToVisitArrayList.size()-1;i++) {
            String s = placesToVisitArrayList.get(i);
            if (s.length() != 0) {
                places += s.substring(0, 1).toUpperCase() + s.substring(1) + Utils.DELIMITER_FOR_PLACES_TO_VISIT;
            }
        }
        String s = placesToVisitArrayList.get(i);
        trip_places =  places + s.substring(0, 1).toUpperCase() + s.substring(1);
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
            return false;
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

        /*@Override
        public void onItemDismiss(int position) {
            placesToVisitArrayList.remove(position);
            notifyItemRemoved(position);
            if(placesToVisitArrayList.size() == 0){
                llAddPlacesToVisit.setVisibility(View.VISIBLE);
            }
            posOfFocus = position;
        }*/

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

        @Override
        public void onItemDismiss(int position) {
            return;
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
            holder.ivCancelPlace.setVisibility(View.GONE);
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
                    }
                }
            });

            holder.etPlaceToVisit.setImeOptions(EditorInfo.IME_ACTION_DONE);
            holder.etPlaceToVisit.setHorizontallyScrolling(false);
            holder.etPlaceToVisit.setMaxLines(Integer.MAX_VALUE);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_info_activity,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_ok:
              addTrip();
            default:
                return true;
        }
    }



    @Override
    public void onBackPressed() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddTripNew.this);

        dialog.setMessage("Do you want to exit without creating the trip?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog.show();

    }
}


