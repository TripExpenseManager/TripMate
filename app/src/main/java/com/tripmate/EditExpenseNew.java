package com.tripmate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.models.CurrencyModel;
import android.app.models.ExpenseModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import info.hoang8f.android.segmented.SegmentedGroup;

public class EditExpenseNew extends AppCompatActivity {

    String trip_id;

    EditText etDesc,etAmountSpent,etConversionRate;
    TextView tvDate,tvCategory,tvCurrentCurrency,tvDefaultCurrency,tvCurrencyName,tvExpPaidByMessage;
    TextView tvConversionCurrentCurrency,tvConversionDefaultCurrency;

    ImageView ivCategory,ivShowOrHideESB,ivEditExpBy;
    TextView tvESBEquallyMsg1,tvESBEquallyMsg2,tvESBEquallyMsg3,tvESBUnequallyMsg1,tvESBUnequallyMsg2,tvESBUnequallyMsg3;
    TextView tvESBSharesMsg1,tvESBSharesMsg2,tvESBSharesMsg3;
    TextView tvTitleExpPaidByMultiple;

    RecyclerView rvESBEqually,rvESBUnequally,rvESBShares;
    LinearLayout llESBEqually,llESBUnequally,llESBShares;
    LinearLayout llDate,llCategory,llCurrency,llCurrencyConversionRate,llExpByDetail;

    Spinner spSharedBy;
    CheckBox cbExpFromDepMoney;

    String descriptionEntered = "";
    Double totalAmountSpent = 0.0;


    int mYear,mMonth,mDay;
    String expense_date;
    Long date_value;
    DatePickerDialog.OnDateSetListener dateSetListener;

    ArrayList<String> categoriesList = new ArrayList<>();

    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

    String categorySelected="";


    AlertDialog alertDialogCategories,alertDialogExpPaidBy;

    AlertDialog alertDialogCurrencies;


    EditText etNewCategory;
    ImageView ivCancelNewCategory,ivSaveNewCategory;
    LinearLayout llNewCategory;



    ArrayList<CurrencyModel> currencyArrayList = new ArrayList<>();


    //1-> deposit money
    //2->Personal money
    int expTypeFlag = 1;

    //1-> paid by single person
    //2-> paid by multiple persons
    int expPaidByFlag = 1;

    String expBySingleSelectedName = "";

    //1-> shared equally
    //2-> shared unequally
    //3-> shared by shares
    int expSharedByFlag = 1;

    RecyclerView rvCategories,rvCurrencies;
    RecyclerView rvExpPaidBySingle,rvExpPaidByMultiple;

    LinearLayout llExpPaidBySingle,llExpPaidByMultiple;

    CategoriesAdapter categoriesAdapter;
    CurrenciesAdapter currenciesAdapter;
    ExpBySingleAdapter expBySingleAdapter;
    ExpByMultipleAdapter expByMultipleAdapter;

    int lastCheckedPos = 0;

    ArrayList<String> personsList = new ArrayList<>();

    ArrayList<Double> expByPersonsMultipleList = new ArrayList<>();
    ArrayList<Boolean> esbEquallyCheckedList = new ArrayList<>();
    ArrayList<Double> eSBUnequallyAmountList = new ArrayList<>();
    ArrayList<Integer> eSBSharesAmountList = new ArrayList<>();


    ESBEquallyAdapter esbEquallyAdapter;
    ESBUnequallyAdapter esbUnequallyAdapter;
    ESBSharesAdapter esbSharesAdapter;

    LinearLayout llShowOrHideESB;
    boolean isShownESB = false;

    boolean spinnerClickedFlag = false;

    MaterialSearchView currencySearchView;

    CurrencyModel defaultCurrency,selectedCurrency;

    Double conversionRate = 1.0;

    ExpenseModel model;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_new);

        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Expense");

        Intent intent = getIntent();
        model = (ExpenseModel) intent.getSerializableExtra("modelToEdit");
        trip_id = intent.getStringExtra("trip_id");


        etDesc = (EditText) findViewById(R.id.etDesc);
        etAmountSpent = (EditText) findViewById(R.id.etAmountSpent);
        etConversionRate = (EditText) findViewById(R.id.etConversionRate);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvCurrentCurrency = (TextView) findViewById(R.id.tvCurrentCurrency);
        tvDefaultCurrency = (TextView) findViewById(R.id.tvDefaultCurrency);
        tvCurrencyName = (TextView) findViewById(R.id.tvCurrencyName);
        tvExpPaidByMessage = (TextView) findViewById(R.id.tvExpPaidByMessage);
        tvConversionCurrentCurrency = (TextView) findViewById(R.id.tvConversionCurrentCurrency);
        tvConversionDefaultCurrency = (TextView) findViewById(R.id.tvConversionDefaultCurrency);


        tvESBEquallyMsg1 = (TextView) findViewById(R.id.tvESBEquallyMsg1);
        tvESBEquallyMsg2 = (TextView) findViewById(R.id.tvESBEquallyMsg2);
        tvESBEquallyMsg3 = (TextView) findViewById(R.id.tvESBEquallyMsg3);
        tvESBUnequallyMsg1 = (TextView) findViewById(R.id.tvESBUnequallyMsg1);
        tvESBUnequallyMsg2 = (TextView) findViewById(R.id.tvESBUnequallyMsg2);
        tvESBUnequallyMsg3 = (TextView) findViewById(R.id.tvESBUnequallyMsg3);
        tvESBSharesMsg1 = (TextView) findViewById(R.id.tvESBSharesMsg1);
        tvESBSharesMsg2 = (TextView) findViewById(R.id.tvESBSharesMsg2);
        tvESBSharesMsg3 = (TextView) findViewById(R.id.tvESBSharesMsg3);

        rvESBEqually = (RecyclerView) findViewById(R.id.rvESBEqually);
        rvESBUnequally = (RecyclerView) findViewById(R.id.rvESBUnequally);
        rvESBShares = (RecyclerView) findViewById(R.id.rvESBShares);

        llESBEqually = (LinearLayout) findViewById(R.id.llESBEqually);
        llESBUnequally = (LinearLayout) findViewById(R.id.llESBUnequally);
        llESBShares = (LinearLayout) findViewById(R.id.llESBShares);

        llShowOrHideESB = (LinearLayout) findViewById(R.id.llShowOrHideESB);

        ivCategory = (ImageView) findViewById(R.id.ivCategory);
        ivShowOrHideESB = (ImageView) findViewById(R.id.ivShowOrHideESB);
        ivEditExpBy = (ImageView) findViewById(R.id.ivEditExpBy);

        llDate = (LinearLayout) findViewById(R.id.llDate);
        llCategory = (LinearLayout) findViewById(R.id.llCategory);
        llCurrency = (LinearLayout) findViewById(R.id.llCurrency);
        llCurrencyConversionRate = (LinearLayout) findViewById(R.id.llCurrencyConversionRate);
        llExpByDetail = (LinearLayout) findViewById(R.id.llExpByDetail);

        spSharedBy = (Spinner) findViewById(R.id.spSharedBy);
        cbExpFromDepMoney = (CheckBox) findViewById(R.id.cbExpFromDepMoney);

        String defaultCurrencyCode = dataBaseHelper.getTripData(trip_id).getTripcurrency();
        defaultCurrency = new CurrencyModel(Utils.getCorrespondingCurrencyName(defaultCurrencyCode),defaultCurrencyCode);
        selectedCurrency = new CurrencyModel(Utils.getCorrespondingCurrencyName(model.getCurrency()),model.getCurrency());


        personsList.clear();
        personsList.addAll(Arrays.asList(dataBaseHelper.getPersonsListAsString(trip_id)));



        for (String s : personsList){
            expByPersonsMultipleList.add(0.0);
            esbEquallyCheckedList.add(false);
            eSBUnequallyAmountList.add(0.0);
            eSBSharesAmountList.add(0);
        }

        categoriesList.clear();
        categoriesList.addAll(Arrays.asList(dataBaseHelper.getCategories()));
        categoriesList.add("@#Add@#");

        llCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoriesDialog();
            }
        });

        currencyArrayList = Utils.getCurrienciesList();

        llCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCurrenciesDialog();
            }
        });

        cbExpFromDepMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    expTypeFlag = 1;
                    llExpByDetail.setVisibility(View.GONE);
                }else{
                    expTypeFlag = 2;
                    llExpByDetail.setVisibility(View.VISIBLE);
                    openExpendByDialog();
                }
            }
        });

        ivEditExpBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExpendByDialog();
            }
        });

        llShowOrHideESB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShownESB){
                    isShownESB = false;
                    LinearLayout tempLayout ;
                    if(expSharedByFlag == 1){
                        tempLayout = llESBEqually;
                    }
                    else if(expSharedByFlag == 2){
                        tempLayout = llESBUnequally;
                    }else{
                        tempLayout = llESBShares;
                    }

                    tempLayout.setAlpha(1.0f);
                    // Start the animation
                    tempLayout.animate()
                            .alpha(0.0f)
                            .setListener(null);

                    llESBEqually.setVisibility(View.GONE);
                    llESBUnequally.setVisibility(View.GONE);
                    llESBShares.setVisibility(View.GONE);
                    ivShowOrHideESB.setImageDrawable(getResources().getDrawable(R.drawable.icon_show,null));
                }else{
                    isShownESB = true;

                    if(expSharedByFlag == 1) { //shared equally
                        // Prepare the View for the animation
                        llESBEqually.setVisibility(View.VISIBLE);
                        llESBEqually.setAlpha(0.0f);
                        // Start the animation
                        llESBEqually.animate()
                                .alpha(1.0f)
                                .setListener(null);
                        llESBUnequally.setVisibility(View.GONE);
                        llESBShares.setVisibility(View.GONE);

                    }else if(expSharedByFlag == 2){ //shared unequally
                        llESBEqually.setVisibility(View.GONE);
                        llESBUnequally.setVisibility(View.VISIBLE);
                        llESBUnequally.setAlpha(0.0f);
                        // Start the animation
                        llESBUnequally.animate()
                                .alpha(1.0f)
                                .setListener(null);
                        llESBShares.setVisibility(View.GONE);

                    }else if(expSharedByFlag == 3){ // //shared by shares
                        llESBEqually.setVisibility(View.GONE);
                        llESBUnequally.setVisibility(View.GONE);
                        llESBShares.setVisibility(View.VISIBLE);
                        llESBShares.setAlpha(0.0f);
                        // Start the animation
                        llESBShares.animate()
                                .alpha(1.0f)
                                .setListener(null);

                    }
                    ivShowOrHideESB.setImageDrawable(getResources().getDrawable(R.drawable.icon_hide,null));
                }

            }
        });
        spSharedBy.setSelection(-1);



        spSharedBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ivShowOrHideESB.setImageDrawable(getResources().getDrawable(R.drawable.icon_hide,null));
                isShownESB = true;
                if(position == 0) { //shared equally
                    expSharedByFlag = 1;
                    if(!spinnerClickedFlag){
                        llESBEqually.setVisibility(View.GONE);
                        ivShowOrHideESB.setImageDrawable(getResources().getDrawable(R.drawable.icon_show,null));
                        spinnerClickedFlag=true;
                        isShownESB = false;
                    }else{
                        // Prepare the View for the animation
                        llESBEqually.setVisibility(View.VISIBLE);
                        llESBEqually.setAlpha(0.0f);
                        // Start the animation
                        llESBEqually.animate()
                                .alpha(1.0f)
                                .setListener(null);

                        spinnerClickedFlag=true;
                    }

                    llESBUnequally.setVisibility(View.GONE);
                    llESBShares.setVisibility(View.GONE);

                }else if(position == 1){ //shared unequally
                    expSharedByFlag = 2;
                    llESBEqually.setVisibility(View.GONE);
                    llESBShares.setVisibility(View.GONE);

                    if(!spinnerClickedFlag){
                        llESBUnequally.setVisibility(View.GONE);
                        ivShowOrHideESB.setImageDrawable(getResources().getDrawable(R.drawable.icon_show,null));
                        spinnerClickedFlag=true;
                        isShownESB = false;
                    }else{
                        // Prepare the View for the animation
                        llESBUnequally.setVisibility(View.VISIBLE);
                        llESBUnequally.setAlpha(0.0f);
                        // Start the animation
                        llESBUnequally.animate()
                                .alpha(1.0f)
                                .setListener(null);

                        spinnerClickedFlag=true;
                    }

                }else if(position == 2){ // //shared by shares
                    expSharedByFlag = 3;
                    llESBEqually.setVisibility(View.GONE);
                    llESBUnequally.setVisibility(View.GONE);


                    if(!spinnerClickedFlag){
                        llESBShares.setVisibility(View.GONE);
                        ivShowOrHideESB.setImageDrawable(getResources().getDrawable(R.drawable.icon_show,null));
                        spinnerClickedFlag=true;
                        isShownESB = false;
                    }else{
                        // Prepare the View for the animation
                        llESBShares.setVisibility(View.VISIBLE);
                        llESBShares.setAlpha(0.0f);
                        // Start the animation
                        llESBShares.animate()
                                .alpha(1.0f)
                                .setListener(null);

                        spinnerClickedFlag=true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        rvESBEqually.setLayoutManager(new LinearLayoutManager(this));
        esbEquallyAdapter = new ESBEquallyAdapter(personsList);
        rvESBEqually.setAdapter(esbEquallyAdapter);

        rvESBUnequally.setLayoutManager(new LinearLayoutManager(this));
        esbUnequallyAdapter = new ESBUnequallyAdapter(personsList);
        rvESBUnequally.setAdapter(esbUnequallyAdapter);

        rvESBShares.setLayoutManager(new LinearLayoutManager(this));
        esbSharesAdapter = new ESBSharesAdapter(personsList);
        rvESBShares.setAdapter(esbSharesAdapter);


        etAmountSpent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{
                    totalAmountSpent = roundOff(Double.parseDouble(s.toString()));
                }catch (Exception e){
                    totalAmountSpent = 0.0;
                }

                handleExpSharedByMessages();
                handleTotalAmountCurrencies();

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        etConversionRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    conversionRate = roundOff(Double.parseDouble(s.toString()));
                }catch (Exception e){
                    conversionRate = 0.0;
                }
                handleExpSharedByMessages();
                handleTotalAmountCurrencies();

                esbEquallyAdapter.notifyDataSetChanged();
                esbSharesAdapter.notifyDataSetChanged();
                esbUnequallyAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        expBySingleAdapter = new ExpBySingleAdapter(personsList);
        expByMultipleAdapter = new ExpByMultipleAdapter(personsList);


        initialiseAllForEditing();

    }

    void initialiseAllForEditing(){

        etDesc.setText(model.getItemName());
        etAmountSpent.setText(model.getAmount()+"");
        totalAmountSpent = model.getAmount();

        setDate();

        categorySelected = model.getCategory();

        if(Utils.getCategoriesHashMap().get(categorySelected) != null){
            ivCategory.setImageDrawable(getDrawable(Utils.getCategoriesHashMap().get(categorySelected)));
        }else{
            String firstLetter = String.valueOf(categorySelected.charAt(0));

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            int color = generator.getColor(categorySelected);
            //int color = generator.getRandomColor();
            TextDrawable drawable = TextDrawable.builder().beginConfig()
                    .bold().endConfig()
                    .buildRound(firstLetter, color); // radius in px

            ivCategory.setImageDrawable(drawable);
        }
        tvCategory.setText(categorySelected);




        spinnerClickedFlag = false;

        expSharedByFlag = model.getShareByType();
        if(expSharedByFlag == 1){
            spSharedBy.setSelection(0);
            String persons[] = model.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
            ArrayList<String> personsInvolvedList =  new ArrayList<>();
            personsInvolvedList.addAll(Arrays.asList(persons));
            for(String person : personsInvolvedList){
                Log.i("SKSK",person);
                int pos = personsList.indexOf(person.trim());
                esbEquallyCheckedList.set(pos,true);
            }
            esbEquallyAdapter.notifyDataSetChanged();


        }else if(expSharedByFlag == 2){
            spSharedBy.setSelection(1);
            String personsAndAmount[] = model.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
            ArrayList<String> personsAndAmountList =  new ArrayList<>();
            personsAndAmountList.addAll(Arrays.asList(personsAndAmount));
            for (String aPersonsAndAmount : personsAndAmountList) {
                String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                int pos = personsList.indexOf(eachPersonAndAmount[0]);
                eSBUnequallyAmountList.set(pos,roundOff(Double.parseDouble(eachPersonAndAmount[1])));
            }
            esbUnequallyAdapter.notifyDataSetChanged();

        }else if(expSharedByFlag == 3){
            spSharedBy.setSelection(2);
            String personsAndAmount[] = model.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
            ArrayList<String> personsAndAmountList =  new ArrayList<>();
            personsAndAmountList.addAll(Arrays.asList(personsAndAmount));
            for (String aPersonsAndAmount : personsAndAmountList) {
                String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                int pos = personsList.indexOf(eachPersonAndAmount[0]);
                eSBSharesAmountList.set(pos,Integer.parseInt(eachPersonAndAmount[1]));
            }

            esbSharesAdapter.notifyDataSetChanged();
        }




        expTypeFlag = model.getAmountType();
        if(expTypeFlag == 1){
            cbExpFromDepMoney.setChecked(true);
        }else{
            cbExpFromDepMoney.setChecked(false);

            expPaidByFlag = model.getExpByType();

            if(expPaidByFlag == 1){
                String person = model.getExpBy();
                int pos = personsList.indexOf(person.trim());
                lastCheckedPos = pos;
                expBySingleAdapter.notifyDataSetChanged();
            }else if(expPaidByFlag == 2){
                String personsAndAmount[] = model.getExpBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                ArrayList<String> personsAndAmountList =  new ArrayList<>();
                personsAndAmountList.addAll(Arrays.asList(personsAndAmount));
                for (String aPersonsAndAmount : personsAndAmountList) {
                    String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                    int pos = personsList.indexOf(eachPersonAndAmount[0]);
                    expByPersonsMultipleList.set(pos,roundOff(Double.parseDouble(eachPersonAndAmount[1])));
                }

                expByMultipleAdapter.notifyDataSetChanged();

            }

            alertDialogExpPaidBy.dismiss();
        }






        handleTotalAmountCurrencies();
        handleCurrencyConversions();
        handleExpSharedByMessages();

    }

    void handleExpendByMessage(){
        if(expPaidByFlag == 1) {
            tvExpPaidByMessage.setText(personsList.get(lastCheckedPos));
        }else if(expPaidByFlag == 2){
            String expByMultipleDetail ="";
            for(int i=0 ; i<expByPersonsMultipleList.size();i++){
                if(expByPersonsMultipleList.get(i)>0.0){
                    expByMultipleDetail+= personsList.get(i)+" : "+selectedCurrency.getCurrencyCode() +" "+ expByPersonsMultipleList.get(i)+"\n";
                }
            }
            tvExpPaidByMessage.setText(expByMultipleDetail);
        }
    }

    void handleExpSharedByMessages(){
        // Initialising msg2 for shared by equally
        int noOfPersonsESBequallychecked = 0;
        for(boolean b : esbEquallyCheckedList){
            if(b) noOfPersonsESBequallychecked++;
        }

        if(noOfPersonsESBequallychecked == 0){
            tvESBEquallyMsg2.setText("Please select atleast 1 person");
        }else{
            tvESBEquallyMsg2.setText(roundOff(totalAmountSpent/noOfPersonsESBequallychecked) +" "+selectedCurrency.getCurrencyCode()+
                    "  / person");
        }

        // Initialising msg2 for shared by unequally
        Double sum = 0.0;
        for (Double d : eSBUnequallyAmountList) {
            sum += d;
        }
        tvESBUnequallyMsg2.setText(sum + " "+selectedCurrency.getCurrencyCode()+ "  of " + totalAmountSpent + " "
                +selectedCurrency.getCurrencyCode()+"  entered \n("+ roundOff(totalAmountSpent - sum)
                +" "+selectedCurrency.getCurrencyCode()+ " left )" );

        // Initialising msg2 for shared by shares
        Integer sumOfShares = 0;
        for (Integer in : eSBSharesAmountList) {
            sumOfShares += in;
        }
        if(sumOfShares!=0){
            tvESBSharesMsg2.setText(roundOff(totalAmountSpent/sumOfShares)+ " "+selectedCurrency.getCurrencyCode()+ " / share");
        }else{
            tvESBSharesMsg2.setText("Please enter something");
        }
    }

    void handleCurrencyConversions(){

        handleExpendByMessage();

        if(selectedCurrency.getCurrencyCode().equalsIgnoreCase(defaultCurrency.getCurrencyCode())){
            llCurrencyConversionRate.setVisibility(View.GONE);
            etConversionRate.setText("");
        }else{
            conversionRate = model.getCurrencyConversionRate();
            etConversionRate.setText(conversionRate+"");
            llCurrencyConversionRate.setVisibility(View.VISIBLE);
            tvConversionCurrentCurrency.setText("1 "+ selectedCurrency.getCurrencyCode() + " = ");
            tvConversionDefaultCurrency.setText(defaultCurrency.getCurrencyCode());

            etConversionRate.setSelection(etConversionRate.getText().toString().length());
        }

        tvCurrencyName.setText(selectedCurrency.getCurrencyName() + " - " +
                selectedCurrency.getCurrencyCode());

        esbEquallyAdapter.notifyDataSetChanged();
        esbSharesAdapter.notifyDataSetChanged();
        esbUnequallyAdapter.notifyDataSetChanged();

    }

    void handleTotalAmountCurrencies(){

        if(selectedCurrency.getCurrencyCode().equalsIgnoreCase(defaultCurrency.getCurrencyCode())){
            tvCurrentCurrency.setText(selectedCurrency.getCurrencyCode());
            conversionRate = 1.0;
            tvDefaultCurrency.setVisibility(View.GONE);
        }else{
            tvCurrentCurrency.setText(selectedCurrency.getCurrencyCode());
            tvDefaultCurrency.setVisibility(View.VISIBLE);
            tvDefaultCurrency.setText(":      "+ roundOff(totalAmountSpent*conversionRate)  +" "+defaultCurrency.getCurrencyCode());
        }
    }

    void openExpendByDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.expense_paid_by_dialog, null);
        final AlertDialog.Builder alertDialogBuilderExpPaidBy = new AlertDialog.Builder(this);
        alertDialogBuilderExpPaidBy.setView(promptsView);

        rvExpPaidBySingle = (RecyclerView) promptsView.findViewById(R.id.rvExpPaidBySingle);
        rvExpPaidByMultiple = (RecyclerView) promptsView.findViewById(R.id.rvExpPaidByMultiple);

        llExpPaidBySingle = (LinearLayout) promptsView.findViewById(R.id.llExpPaidBySingle);
        llExpPaidByMultiple = (LinearLayout) promptsView.findViewById(R.id.llExpPaidByMultiple);

        expBySingleAdapter = new ExpBySingleAdapter(personsList);
        rvExpPaidBySingle.setAdapter(expBySingleAdapter);
        rvExpPaidBySingle.setLayoutManager(new LinearLayoutManager(this));

        expByMultipleAdapter = new ExpByMultipleAdapter(personsList);
        rvExpPaidByMultiple.setAdapter(expByMultipleAdapter);
        rvExpPaidByMultiple.setLayoutManager(new LinearLayoutManager(this));

        tvTitleExpPaidByMultiple = (TextView) promptsView.findViewById(R.id.tvTitleExpPaidByMultiple);

        final SegmentedGroup segmentedGroup = (SegmentedGroup) promptsView.findViewById(R.id.segmentedGroup);
        segmentedGroup.check(expPaidByFlag == 1 ? R.id.rbSingle : R.id.rbMultiple);
        if(expPaidByFlag == 1){
            llExpPaidBySingle.setVisibility(View.VISIBLE);
            llExpPaidByMultiple.setVisibility(View.GONE);
        }else if(expPaidByFlag == 2){
            llExpPaidBySingle.setVisibility(View.GONE);
            llExpPaidByMultiple.setVisibility(View.VISIBLE);

            Double sum = 0.0;
            for (Double d : expByPersonsMultipleList) {
                sum += d;
            }
            if(sum != 0.0) tvTitleExpPaidByMultiple.setText(roundOff(totalAmountSpent - sum) + " "
                    +selectedCurrency.getCurrencyCode()+" left of " + totalAmountSpent
                    + " "+selectedCurrency.getCurrencyCode());
        }

        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbSingle:
                        expPaidByFlag = 1;
                        llExpPaidBySingle.setVisibility(View.VISIBLE);
                        alertDialogExpPaidBy.getWindow().clearFlags(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(alertDialogExpPaidBy.getWindow().getDecorView().getWindowToken(), 0);
                        llExpPaidByMultiple.setVisibility(View.GONE);

                        break;
                    case R.id.rbMultiple:
                        expPaidByFlag = 2;
                        llExpPaidBySingle.setVisibility(View.GONE);
                        llExpPaidByMultiple.setVisibility(View.VISIBLE);

                        Double sum = 0.0;
                        for (Double d : expByPersonsMultipleList) {
                            sum += d;
                        }
                        if(sum != 0.0) tvTitleExpPaidByMultiple.setText(roundOff(totalAmountSpent - sum)+
                                " "+selectedCurrency.getCurrencyCode()+ " left of " +
                                totalAmountSpent+ " "+selectedCurrency.getCurrencyCode());

                        break;
                    default:
                        break;
                }
            }
        });

        alertDialogBuilderExpPaidBy.setPositiveButton("Ok",null);

        alertDialogBuilderExpPaidBy.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleExpendByMessage();
            }
        });



        alertDialogBuilderExpPaidBy.setCancelable(false);
        alertDialogExpPaidBy = alertDialogBuilderExpPaidBy.create();
        alertDialogExpPaidBy.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        alertDialogExpPaidBy.show();



        alertDialogExpPaidBy.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expPaidByFlag == 1) {
                    tvExpPaidByMessage.setText(personsList.get(lastCheckedPos));
                    alertDialogExpPaidBy.dismiss();
                }else if(expPaidByFlag == 2){
                    Double sum = 0.0;
                    for (Double d : expByPersonsMultipleList) {
                        sum += d;
                    }
                    if((sum-totalAmountSpent == 0)){
                        String expByMultipleDetail ="";
                        for(int i=0 ; i<expByPersonsMultipleList.size();i++){
                            if(expByPersonsMultipleList.get(i)>0.0){
                                expByMultipleDetail+= personsList.get(i)+" : "+selectedCurrency.getCurrencyCode()+" "
                                        + expByPersonsMultipleList.get(i)+"\n";
                            }
                        }
                        tvExpPaidByMessage.setText(expByMultipleDetail);

                        alertDialogExpPaidBy.dismiss();
                    }else{
                        AlertDialog.Builder alertDialogExpByError = new AlertDialog.Builder(EditExpenseNew.this);
                        alertDialogExpByError.setTitle("Math Error!");
                        if(sum>totalAmountSpent) {
                            alertDialogExpByError.setMessage("Your entries are more than total money \nSum of your entries : "
                                    + sum + " \n" + "Total money entered : " + totalAmountSpent);
                        }else {
                            alertDialogExpByError.setMessage("Your entries are less than total money \nSum of your entries : "
                                    + sum + " \n" + "Total money entered : " + totalAmountSpent);
                        }
                        alertDialogExpByError.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alertDialogExpByError.setCancelable(false);
                        AlertDialog alertDialog = alertDialogExpByError.create();
                        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                        alertDialog.show();



                    }

                }
            }
        });

    }

    void openCurrenciesDialog(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.currency_dialog, null);
        AlertDialog.Builder alertDialogBuilderCurrencies = new AlertDialog.Builder(this);
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

                InputMethodManager imm = (InputMethodManager) EditExpenseNew.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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



/*



        currencySearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                currenciesAdapter =new CurrenciesAdapter(currencyArrayList);
                rvCurrencies.setAdapter(currenciesAdapter);
                return false;
            }
        });


*/



    }

    void openCategoriesDialog(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.categories_layout, null);
        AlertDialog.Builder alertDialogBuilderCategories = new AlertDialog.Builder(this);
        alertDialogBuilderCategories.setView(promptsView);


        alertDialogBuilderCategories.setCancelable(true);
        alertDialogCategories = alertDialogBuilderCategories.create();
        alertDialogCategories.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        alertDialogCategories.show();


        etNewCategory = (EditText) promptsView.findViewById(R.id.etNewCategory);
        ivCancelNewCategory = (ImageView) promptsView.findViewById(R.id.ivCancelNewCategory);
        ivSaveNewCategory = (ImageView) promptsView.findViewById(R.id.ivSaveNewCategory);
        llNewCategory = (LinearLayout) promptsView.findViewById(R.id.llNewCategory);

        rvCategories = (RecyclerView) promptsView.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new GridLayoutManager(getBaseContext(),4, LinearLayoutManager.VERTICAL,false));
        categoriesAdapter =new CategoriesAdapter();
        rvCategories.setAdapter(categoriesAdapter);





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

                    handleCurrencyConversions();
                    handleTotalAmountCurrencies();

                }
            });

        }

        @Override
        public int getItemCount() {
            return currencyAdapterArrayList.size();
        }
    }

    class ExpBySingleAdapter extends  RecyclerView.Adapter<ExpBySingleAdapter.ExpBySingleViewHolder>{

        private RadioButton lastChecked = null;



        ArrayList<String> expBySingleAdapterArrayList = new ArrayList<>();

        public ExpBySingleAdapter(ArrayList<String> expBySingleAdapterArrayList) {
            this.expBySingleAdapterArrayList = expBySingleAdapterArrayList;
        }

        class ExpBySingleViewHolder extends RecyclerView.ViewHolder{
            RadioButton rbPersonNameExpBySingle;
            LinearLayout llLiExpBySingle;
            public ExpBySingleViewHolder(View itemView) {
                super(itemView);
                rbPersonNameExpBySingle = (RadioButton) itemView.findViewById(R.id.rbPersonNameExpBySingle);
                llLiExpBySingle = (LinearLayout) itemView.findViewById(R.id.llLiExpBySingle);


            }
        }

        @Override
        public ExpBySingleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_exp_by_single,parent,false);
            return new ExpBySingleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ExpBySingleViewHolder holder, int position) {

            if(lastCheckedPos == position){
                holder.rbPersonNameExpBySingle.setChecked(true);
                lastChecked = holder.rbPersonNameExpBySingle;
            }else{
                holder.rbPersonNameExpBySingle.setChecked(false);
            }

            holder.rbPersonNameExpBySingle.setText(expBySingleAdapterArrayList.get(position));
            holder.llLiExpBySingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expBySingleSelectedName = expBySingleAdapterArrayList.get(holder.getAdapterPosition());
                    alertDialogExpPaidBy.dismiss();
                }
            });

            holder.rbPersonNameExpBySingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton cb = (RadioButton)v;
                    int clickedPos = holder.getAdapterPosition();
                    if(cb.isChecked())
                    {
                        if(lastChecked != null && lastCheckedPos != holder.getAdapterPosition())
                        {
                            lastChecked.setChecked(false);
                        }

                        lastChecked = cb;
                        lastCheckedPos = clickedPos;
                    }
                    else {
                        lastChecked = null;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return expBySingleAdapterArrayList.size();
        }
    }

    public Double roundOff(Double d){
        return Math.round(d * 100.0) / 100.0;
    }

    class ExpByMultipleAdapter extends  RecyclerView.Adapter<ExpByMultipleAdapter.ExpByMultipleViewHolder>{

        ArrayList<String> expByMultipleAdapterArrayList = new ArrayList<>();

        ExpByMultipleAdapter(ArrayList<String> expByMultipleAdapterArrayList) {
            this.expByMultipleAdapterArrayList = expByMultipleAdapterArrayList;
        }

        class ExpByMultipleViewHolder extends RecyclerView.ViewHolder{
            TextView tvPersonNameExpByMultiple,tvConvertedExpByMultiple,tvCurrentCurrencyCodeExpByMultiple;
            EditText etExpByPersonExpByMultiple;

            ExpByMultipleViewHolder(View itemView) {
                super(itemView);
                tvPersonNameExpByMultiple = (TextView) itemView.findViewById(R.id.tvPersonNameExpByMultiple);
                tvConvertedExpByMultiple = (TextView) itemView.findViewById(R.id.tvConvertedExpByMultiple);
                tvCurrentCurrencyCodeExpByMultiple = (TextView) itemView.findViewById(R.id.tvCurrentCurrencyCodeExpByMultiple);
                etExpByPersonExpByMultiple = (EditText) itemView.findViewById(R.id.etExpByPersonExpByMultiple);

            }
        }

        @Override
        public ExpByMultipleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_exp_by_multiple,parent,false);
            return new ExpByMultipleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ExpByMultipleViewHolder holder, int position) {
            holder.tvPersonNameExpByMultiple.setText(expByMultipleAdapterArrayList.get(position));
            if(expByPersonsMultipleList.get(position) != 0.0)
                holder.etExpByPersonExpByMultiple.setText(""+expByPersonsMultipleList.get(position));
            else
                holder.etExpByPersonExpByMultiple.setText("");

            holder.etExpByPersonExpByMultiple.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        alertDialogExpPaidBy.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        alertDialogExpPaidBy.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });

            if(selectedCurrency.getCurrencyCode().equalsIgnoreCase(defaultCurrency.getCurrencyCode())){
                holder.tvConvertedExpByMultiple.setVisibility(View.GONE);
            }else{
                holder.tvConvertedExpByMultiple.setVisibility(View.VISIBLE);
                holder.tvConvertedExpByMultiple.setText(roundOff(expByPersonsMultipleList.get(position)*conversionRate) +
                        " "+ defaultCurrency.getCurrencyCode());
            }

            holder.tvCurrentCurrencyCodeExpByMultiple.setText(selectedCurrency.getCurrencyCode());

            holder.etExpByPersonExpByMultiple.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    Double dInput = 0.0;
                    try{
                        dInput = Double.parseDouble(s.toString());
                        expByPersonsMultipleList.set(holder.getAdapterPosition(), roundOff(dInput));
                    }catch (Exception e){
                        dInput = 0.0;
                        expByPersonsMultipleList.set(holder.getAdapterPosition(), 0.0);
                    }

                    Double sum = 0.0;
                    for (Double d : expByPersonsMultipleList) {
                        sum += d;
                    }
                    tvTitleExpPaidByMultiple.setText(sum + " "+selectedCurrency.getCurrencyCode()+" of " + totalAmountSpent
                            + " "+selectedCurrency.getCurrencyCode()+ " entered ("+ roundOff(totalAmountSpent - sum)
                            + " "+selectedCurrency.getCurrencyCode()+ " left )");

                    holder.tvConvertedExpByMultiple.setText(roundOff(expByPersonsMultipleList.get(holder.getAdapterPosition())*conversionRate) +
                            " "+ defaultCurrency.getCurrencyCode());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return expByMultipleAdapterArrayList.size();
        }
    }

    class CategoriesAdapter extends  RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>{

        class CategoryViewHolder extends RecyclerView.ViewHolder{
            LinearLayout llLiCategory;
            ImageView ivLiCategoryImage;
            TextView tvLiCategoryName;
            public CategoryViewHolder(View itemView) {
                super(itemView);
                tvLiCategoryName = (TextView) itemView.findViewById(R.id.tvLiCategoryName);
                ivLiCategoryImage = (ImageView) itemView.findViewById(R.id.ivLiCategoryImage);
                llLiCategory = (LinearLayout) itemView.findViewById(R.id.llLiCategory);

            }
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_category,parent,false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            final String category = categoriesList.get(position);
            if(Utils.getCategoriesHashMap().get(category) != null){
                holder.ivLiCategoryImage.setImageDrawable(getDrawable(Utils.getCategoriesHashMap().get(category)));
            }else{
                String firstLetter = String.valueOf(category.charAt(0));

                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                // generate random color
                int color = generator.getColor(category);
                //int color = generator.getRandomColor();

                TextDrawable drawable = TextDrawable.builder().beginConfig()
                        .bold().endConfig()
                        .buildRound(firstLetter, color); // radius in px

                holder.ivLiCategoryImage.setImageDrawable(drawable);
            }

            if(category.equalsIgnoreCase("@#Add@#")){
                holder.tvLiCategoryName.setText("Add Category");
            }else {
                holder.tvLiCategoryName.setText(category);
            }

            holder.llLiCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(category.equalsIgnoreCase("@#Add@#")){
                        llNewCategory.setVisibility(View.VISIBLE);
                        ivCancelNewCategory.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                llNewCategory.setVisibility(View.GONE);
                            }
                        });
                        ivSaveNewCategory.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newCategoryToBeAdded = etNewCategory.getText().toString().trim();
                                etNewCategory.setText("");
                                if(newCategoryToBeAdded.equalsIgnoreCase("")){
                                    etNewCategory.setError("Please enter category name");
                                }else{
                                    newCategoryToBeAdded = newCategoryToBeAdded.substring(0,1).toUpperCase() +
                                            newCategoryToBeAdded.substring(1).toLowerCase();
                                    if(categoriesList.contains(newCategoryToBeAdded)){
                                        int ind = categoriesList.indexOf(newCategoryToBeAdded);

                                        categorySelected = categoriesList.get(ind);
                                        tvCategory.setText(categorySelected);
                                        if(Utils.getCategoriesHashMap().get(categorySelected) != null){
                                            ivCategory.setImageDrawable(getDrawable(Utils.getCategoriesHashMap().get(categorySelected)));
                                        }else{
                                            String firstLetter = String.valueOf(categorySelected.charAt(0));

                                            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                                            // generate random color
                                            int color = generator.getColor(categorySelected);
                                            //int color = generator.getRandomColor();

                                            TextDrawable drawable = TextDrawable.builder().beginConfig()
                                                    .bold().endConfig()
                                                    .buildRound(firstLetter, color); // radius in px

                                            ivCategory.setImageDrawable(drawable);
                                        }
                                        alertDialogCategories.dismiss();
                                    }else {
                                        llNewCategory.setVisibility(View.GONE);
                                        dataBaseHelper.addCategory(newCategoryToBeAdded);
                                        categoriesList.clear();
                                        categoriesList.addAll(Arrays.asList(dataBaseHelper.getCategories()));
                                        categoriesList.add("@#Add@#");

                                        categoriesAdapter.notifyDataSetChanged();


                                    }

                                }


                            }
                        });


                    }else {
                        categorySelected = category;
                        tvCategory.setText(category);
                        if(Utils.getCategoriesHashMap().get(category) != null){
                            ivCategory.setImageDrawable(getDrawable(Utils.getCategoriesHashMap().get(category)));
                        }else{
                            String firstLetter = String.valueOf(category.charAt(0));

                            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                            // generate random color
                            int color = generator.getColor(category);
                            //int color = generator.getRandomColor();

                            TextDrawable drawable = TextDrawable.builder().beginConfig()
                                    .bold().endConfig()
                                    .buildRound(firstLetter, color); // radius in px

                            ivCategory.setImageDrawable(drawable);
                        }
                        alertDialogCategories.dismiss();

                    }
                }
            });



        }

        @Override
        public int getItemCount() {
            return categoriesList.size();
        }
    }

    class ESBEquallyAdapter extends  RecyclerView.Adapter<ESBEquallyAdapter.ESBEquallyViewHolder>{

        ArrayList<String> eSBEquallyAdapterArrayList = new ArrayList<>();

        ESBEquallyAdapter(ArrayList<String> eSBEquallyAdapterArrayList) {
            this.eSBEquallyAdapterArrayList = eSBEquallyAdapterArrayList;
        }

        class ESBEquallyViewHolder extends RecyclerView.ViewHolder{
            CheckBox cbESBEqually;
            ESBEquallyViewHolder(View itemView) {
                super(itemView);
                cbESBEqually = (CheckBox) itemView.findViewById(R.id.cbESBEqually);

            }
        }

        @Override
        public ESBEquallyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_esb_equally,parent,false);
            return new ESBEquallyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ESBEquallyViewHolder holder,int position) {
            if(esbEquallyCheckedList.get(position)){
                holder.cbESBEqually.setChecked(true);
            }else{
                holder.cbESBEqually.setChecked(false);
            }

            holder.cbESBEqually.setText(eSBEquallyAdapterArrayList.get(position));
            holder.cbESBEqually.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        esbEquallyCheckedList.set(holder.getAdapterPosition(),true);
                    }else{
                        esbEquallyCheckedList.set(holder.getAdapterPosition(),false);
                    }

                    int noOfPersonsESBequallychecked = 0;
                    for(boolean b : esbEquallyCheckedList){
                        if(b) noOfPersonsESBequallychecked++;
                    }
                    if(noOfPersonsESBequallychecked == 0){
                        tvESBEquallyMsg2.setText("Please select atleast 1 person");
                    }else{
                        tvESBEquallyMsg2.setText(roundOff(totalAmountSpent/noOfPersonsESBequallychecked) +
                                " "+selectedCurrency.getCurrencyCode()+" / person");
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return eSBEquallyAdapterArrayList.size();
        }
    }

    class ESBUnequallyAdapter extends  RecyclerView.Adapter<ESBUnequallyAdapter.ESBUnequallyViewHolder>{

        ArrayList<String> eSBUnequallyAdapterArrayList = new ArrayList<>();

        ESBUnequallyAdapter(ArrayList<String> eSBUnequallyAdapterArrayList) {
            this.eSBUnequallyAdapterArrayList = eSBUnequallyAdapterArrayList;
        }

        class ESBUnequallyViewHolder extends RecyclerView.ViewHolder{
            TextView tvPersonNameESBUnequally,tvConvertedESBUnequally,tvCurrentCurrencyCodeESBUnequally;
            EditText etExpByPersonESBUnequally;

            ESBUnequallyViewHolder(View itemView) {
                super(itemView);
                tvPersonNameESBUnequally = (TextView) itemView.findViewById(R.id.tvPersonNameESBUnequally);
                tvConvertedESBUnequally = (TextView) itemView.findViewById(R.id.tvConvertedESBUnequally);
                tvCurrentCurrencyCodeESBUnequally = (TextView) itemView.findViewById(R.id.tvCurrentCurrencyCodeESBUnequally);
                etExpByPersonESBUnequally = (EditText) itemView.findViewById(R.id.etExpByPersonESBUnequally);


            }
        }

        @Override
        public ESBUnequallyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_esb_unequally,parent,false);
            return new ESBUnequallyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ESBUnequallyViewHolder holder,int position) {
            holder.tvPersonNameESBUnequally.setText(eSBUnequallyAdapterArrayList.get(position));
            if(eSBUnequallyAmountList.get(position) != 0.0){
                holder.etExpByPersonESBUnequally.setText(""+eSBUnequallyAmountList.get(position));
            }
            else{
                holder.etExpByPersonESBUnequally.setText("");
            }

            if(selectedCurrency.getCurrencyCode().equalsIgnoreCase(defaultCurrency.getCurrencyCode())){
                holder.tvConvertedESBUnequally.setVisibility(View.GONE);
            }else{
                holder.tvConvertedESBUnequally.setVisibility(View.VISIBLE);
                holder.tvConvertedESBUnequally.setText(roundOff(eSBUnequallyAmountList.get(position)*conversionRate) +
                        " "+ defaultCurrency.getCurrencyCode());
            }

            holder.tvCurrentCurrencyCodeESBUnequally.setText(selectedCurrency.getCurrencyCode());

            holder.etExpByPersonESBUnequally.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    Double dInput = 0.0;
                    try{
                        dInput = Double.parseDouble(s.toString());
                        eSBUnequallyAmountList.set(holder.getAdapterPosition(), roundOff(dInput));
                    }catch (Exception e){
                        dInput = 0.0;
                        eSBUnequallyAmountList.set(holder.getAdapterPosition(), 0.0);
                    }



                    Double sum = 0.0;
                    for (Double d : eSBUnequallyAmountList) {
                        sum += d;
                    }
                    tvESBUnequallyMsg2.setText(sum + " "+selectedCurrency.getCurrencyCode()+ " of " + totalAmountSpent
                            + " "+selectedCurrency.getCurrencyCode()+ " entered \n("+
                            roundOff(totalAmountSpent - sum) + " "+selectedCurrency.getCurrencyCode()+ " left )" );

                    holder.tvConvertedESBUnequally.setText(roundOff(eSBUnequallyAmountList.get(holder.getAdapterPosition())*conversionRate) +
                            " "+ defaultCurrency.getCurrencyCode());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return eSBUnequallyAdapterArrayList.size();
        }
    }

    class ESBSharesAdapter extends  RecyclerView.Adapter<ESBSharesAdapter.ESBSharesViewHolder>{

        ArrayList<String> eSBSharesAdapterArrayList = new ArrayList<>();

        ESBSharesAdapter(ArrayList<String> eSBSharesAdapterArrayList) {
            this.eSBSharesAdapterArrayList = eSBSharesAdapterArrayList;
        }

        class ESBSharesViewHolder extends RecyclerView.ViewHolder{
            TextView tvPersonNameESBShares,tvCurrentCurrencyCodeESBShares;
            EditText etExpByPersonESBShares;

            ESBSharesViewHolder(View itemView) {
                super(itemView);
                tvPersonNameESBShares = (TextView) itemView.findViewById(R.id.tvPersonNameESBShares);
                tvCurrentCurrencyCodeESBShares = (TextView) itemView.findViewById(R.id.tvCurrentCurrencyCodeESBShares);
                etExpByPersonESBShares = (EditText) itemView.findViewById(R.id.etExpByPersonESBShares);


            }
        }

        @Override
        public ESBSharesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_esb_shares,parent,false);
            return new ESBSharesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ESBSharesViewHolder holder, int position) {
            holder.tvPersonNameESBShares.setText(eSBSharesAdapterArrayList.get(position));
            if(eSBSharesAmountList.get(position) != 0)
                holder.etExpByPersonESBShares.setText(""+eSBSharesAmountList.get(position));

            holder.etExpByPersonESBShares.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    int dInput = 0;
                    try{
                        dInput = Integer.parseInt(s.toString());
                        eSBSharesAmountList.set(holder.getAdapterPosition(), (dInput));
                    }catch (Exception e){
                        dInput = 0;
                        eSBSharesAmountList.set(holder.getAdapterPosition(), 0);
                    }


                    Integer sumOfShares = 0;
                    for (Integer in : eSBSharesAmountList) {
                        sumOfShares += in;
                    }
                    if(sumOfShares!=0){
                        tvESBSharesMsg2.setText(roundOff(totalAmountSpent/sumOfShares)+ " "+
                                selectedCurrency.getCurrencyCode()+ " / share");
                    }else{
                        tvESBSharesMsg2.setText("Please enter something");
                    }



                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }

        @Override
        public int getItemCount() {
            return eSBSharesAdapterArrayList.size();
        }
    }

    void setDate(){

        date_value = model.getDateValue();
        // Setting Default Time and Date
        expense_date = model.getDate();

        tvDate.setText(new SimpleDateFormat("E, MMM d, yyyy, hh:mm a").format(date_value));

        SimpleDateFormat f1 = new SimpleDateFormat("dd-MM-yyyy");
        Date d1 = null;
        try {
            d1 = f1.parse(expense_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        final long time_to_add = date_value - d1.getTime();

        // Date Picker
        dateSetListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                expense_date = mDay + "-" + (mMonth + 1) + "-" + mYear;

                SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date d = f.parse(expense_date);
                    date_value = d.getTime() + time_to_add;
                    tvDate.setText(new SimpleDateFormat("E, MMM d, yyyy, hh:mm a").format(date_value));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timePicker();
            }
        };

        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditExpenseNew.this,dateSetListener,mYear,mMonth,mDay);
                //  datePickerDialog.getWindow().setWindowAnimations(R.style.DialogAnimationUpDown);
                datePickerDialog.show();
            }
        });


    }

    void timePicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                        Date d = null;
                        try {
                            d = f.parse(expense_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        date_value = d.getTime() + hourOfDay*60*60*1000 + minute*60*1000 ;

                        tvDate.setText(new SimpleDateFormat("E, MMM d, yyyy, hh:mm a").format(date_value));

                    }
                },mHour,mMinute,false);
        timePickerDialog.show();
    }

    void addExpenseToTheDatabase(){
        Double deposit_amount_remaining = dataBaseHelper.getDepositMoneyRemaining(trip_id);
        descriptionEntered = etDesc.getText().toString();
        if(descriptionEntered.equalsIgnoreCase("")){
            etDesc.setError("Please enter description");
        }else if(totalAmountSpent <= 0){
            etAmountSpent.setError("Please enter amount");
        }else if(!selectedCurrency.getCurrencyCode().equalsIgnoreCase(defaultCurrency.getCurrencyCode())
                && conversionRate == 0){
            etConversionRate.setError("Please enter conversion rate");
        }
        else if(expSharedByFlag == 1 && noOfItemsCheckedSharedEqually() == 0){
            Snackbar.make(findViewById(android.R.id.content),"Please select atleast 1 person in expense shared by",Snackbar.LENGTH_LONG).show();
        }else if(expSharedByFlag == 2 &&  !canWeProceedESBUnequally() ){
            displayESBUnequallyError();
        }
        else if(expSharedByFlag == 3 && !canWeProceedESBShares()){
            Snackbar.make(findViewById(android.R.id.content),"Please enter atleast 1 share in expense shared by",Snackbar.LENGTH_LONG).show();
        }else if(!cbExpFromDepMoney.isChecked() && expPaidByFlag == 2 && !canWeProceedExpByMultiple()){
            displayExpByMultipleError();
        }else if(expTypeFlag == 1 && deposit_amount_remaining + getPrevAmountForDMChecking() < getTotalAmountSpentForDepositMoneyChecking()){
            Snackbar.make(findViewById(android.R.id.content), "You do not have sufficient deposit money. Deposit money remaining = "+roundOff(deposit_amount_remaining+getPrevAmountForDMChecking()) +" "+defaultCurrency.getCurrencyCode(), Snackbar.LENGTH_LONG).show();
        }
        else{
            descriptionEntered = descriptionEntered.trim().substring(0, 1).toUpperCase() + descriptionEntered.trim().substring(1);
            String expenseByToBeAdded = encodeExpenseBy();
            String ESBToBeAdded = encodeShareBy();

            Log.d("SKSK",expenseByToBeAdded+" "+ ESBToBeAdded);


            ExpenseModel modelToBeAdded = new ExpenseModel();
            modelToBeAdded.setTripId(trip_id);
            modelToBeAdded.setItemId(model.getItemId());
            modelToBeAdded.setItemName(descriptionEntered);
            modelToBeAdded.setAmountType(expTypeFlag);
            modelToBeAdded.setAmount(totalAmountSpent);
            modelToBeAdded.setExpByType(expPaidByFlag);
            modelToBeAdded.setExpBy(expenseByToBeAdded);
            modelToBeAdded.setCategory(categorySelected);
            modelToBeAdded.setDate(expense_date);
            modelToBeAdded.setDateValue(date_value);
            modelToBeAdded.setShareByType(expSharedByFlag);
            modelToBeAdded.setShareBy(ESBToBeAdded);
            modelToBeAdded.setCurrency(selectedCurrency.getCurrencyCode());
            modelToBeAdded.setCurrencyConversionRate(conversionRate);

            dataBaseHelper.editExpenseNew(modelToBeAdded);

            Toast.makeText(this, "Expense edited successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("isEdited",true);
            setResult(201,intent);
            finish();
            overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);


        }


    }

    Double getTotalAmountSpentForDepositMoneyChecking(){
        if(selectedCurrency.getCurrencyCode().equalsIgnoreCase(defaultCurrency.getCurrencyCode())) return totalAmountSpent;
        else return roundOff(totalAmountSpent*conversionRate);
    }

    Double getPrevAmountForDMChecking(){
        if(model.getCurrency().equalsIgnoreCase(defaultCurrency.getCurrencyCode())) return model.getAmount();
        else return roundOff(model.getAmount()*model.getCurrencyConversionRate());
    }

    String encodeExpenseBy(){
        if(expTypeFlag == 1) return "Deposit Money";
        else{
            if(expPaidByFlag == 1){
                return personsList.get(lastCheckedPos);
            }
            else{
                String ans = "";
                for(int i =0;i<personsList.size();i++){
                    if(expByPersonsMultipleList.get(i) !=0){
                        ans += personsList.get(i);
                        ans += Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT;
                        ans += roundOff(expByPersonsMultipleList.get(i));
                        ans += Utils.DELIMITER_FOR_EXP_PERSONS;
                    }
                }
                ans = ans.substring(0,ans.length() - Utils.DELIMITER_FOR_EXP_PERSONS.length());
                return  ans;
            }
        }
    }

    String encodeShareBy(){
        if(expSharedByFlag == 1){
            String ans = "";
            for(int i =0;i<personsList.size();i++){
                if(esbEquallyCheckedList.get(i)){
                    ans += personsList.get(i);
                    ans += Utils.DELIMITER_FOR_EXP_PERSONS;
                }
            }
            ans = ans.substring(0,ans.length() - Utils.DELIMITER_FOR_EXP_PERSONS.length());
            return  ans;
        }else if(expSharedByFlag == 2){
            String ans = "";
            for(int i =0;i<personsList.size();i++){
                if(eSBUnequallyAmountList.get(i) !=0.0){
                    ans += personsList.get(i);
                    ans += Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT;
                    ans += eSBUnequallyAmountList.get(i);
                    ans += Utils.DELIMITER_FOR_EXP_PERSONS;
                }
            }
            ans = ans.substring(0,ans.length() - Utils.DELIMITER_FOR_EXP_PERSONS.length());
            return  ans;
        }else{
            String ans = "";
            for(int i =0;i<personsList.size();i++){
                if(eSBSharesAmountList.get(i) != 0){
                    ans += personsList.get(i);
                    ans += Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT;
                    ans += eSBSharesAmountList.get(i);
                    ans += Utils.DELIMITER_FOR_EXP_PERSONS;
                }
            }
            ans = ans.substring(0,ans.length() - Utils.DELIMITER_FOR_EXP_PERSONS.length());
            return  ans;
        }

    }

    int noOfItemsCheckedSharedEqually(){
        int ans = 0;
        for (boolean b:esbEquallyCheckedList){
            if(b) ans++;
        }
        return ans;
    }

    boolean canWeProceedESBUnequally(){

        Double sum = 0.0;
        for (Double d : eSBUnequallyAmountList){
            sum += d;
        }

        return (sum - totalAmountSpent == 0);

    }

    void displayESBUnequallyError(){

        Double sum = 0.0;
        for (Double d : eSBUnequallyAmountList) {
            sum += d;
        }

        AlertDialog.Builder alertDialogExpByError = new AlertDialog.Builder(this);
        alertDialogExpByError.setTitle("Math Error in expense shared by!");
        if(sum>totalAmountSpent) {
            alertDialogExpByError.setMessage("Your entries are more than total money \nSum of your entries : "
                    + sum + " \n" + "Total money entered : " + totalAmountSpent);
        }else {
            alertDialogExpByError.setMessage("Your entries are less than total money \nSum of your entries : "
                    + sum + " \n" + "Total money entered : " + totalAmountSpent);
        }
        alertDialogExpByError.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogExpByError.setCancelable(false);
        AlertDialog alertDialog = alertDialogExpByError.create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
        alertDialog.show();

    }

    void displayExpByMultipleError(){

        Double sum = 0.0;
        for (Double d : expByPersonsMultipleList) {
            sum += d;
        }

        AlertDialog.Builder alertDialogExpByError = new AlertDialog.Builder(this);
        alertDialogExpByError.setTitle("Math Error in expense by!");
        if(sum>totalAmountSpent) {
            alertDialogExpByError.setMessage("Your entries are more than total money \nSum of your entries : "
                    + sum + " \n" + "Total money entered : " + totalAmountSpent);
        }else {
            alertDialogExpByError.setMessage("Your entries are less than total money \nSum of your entries : "
                    + sum + " \n" + "Total money entered : " + totalAmountSpent);
        }
        alertDialogExpByError.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogExpByError.setCancelable(false);
        AlertDialog alertDialog = alertDialogExpByError.create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
        alertDialog.show();

    }


    boolean canWeProceedESBShares(){
        for(Integer shares:eSBSharesAmountList){
            if(shares != 0) return  true;
        }
        return false;
    }

    boolean canWeProceedExpByMultiple(){

        Double sum = 0.0;
        for (Double d : expByPersonsMultipleList){
            sum += d;
        }
        return (sum - totalAmountSpent == 0);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_expenses_activity,menu);
        return  super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        else if(item.getItemId() == R.id.action_add_expense){
            addExpenseToTheDatabase();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("isEdited",false);
        setResult(201,intent);

        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);

    }
}
