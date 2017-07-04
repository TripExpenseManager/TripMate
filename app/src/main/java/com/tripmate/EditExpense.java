package com.tripmate;

import android.app.DatePickerDialog;
import android.app.models.AddExpenseByPersonModel;
import android.app.models.ExpenseModel;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditExpense extends AppCompatActivity {

    ExpenseModel model;

    EditText descTv;
    Spinner categorySpinner;
    CheckBox expByAllCB;

    ImageView ivDate;
    TextView tvDate;
    int mYear,mMonth,mDay;
    String expense_date;
    Long date_value;
    DatePickerDialog.OnDateSetListener dateSetListener;
    RelativeLayout dateRL;

    MultiSelectionSpinner personsSpinner;
    TextView sharingTempTV;

    String categories[];
    String persons[];

    String trip_id;

    CheckBox expByDepCB;
    LinearLayout expenseByLL,expenseFromDepositLL;
    EditText fromDepositExpenseET;

    Spinner expByPersonSpinner;
    EditText expByPersonAmount;

    String descriptionSelected,categorySelected,dateSelected,expShareByPersonsSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Expense");

        Intent intent = getIntent();
        model = (ExpenseModel) intent.getSerializableExtra("modelToEdit");
        trip_id = intent.getStringExtra("trip_id");

        descTv = (EditText) findViewById(R.id.tvDesc);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        expByAllCB = (CheckBox) findViewById(R.id.expByAllCB);

        personsSpinner = (MultiSelectionSpinner) findViewById(R.id.personsSpinner);
        sharingTempTV = (TextView) findViewById(R.id.sharingTempTV);

        expByDepCB = (CheckBox) findViewById(R.id.expByDepCB);
        expenseFromDepositLL = (LinearLayout) findViewById(R.id.expenseFromDepositLL);
        expenseByLL = (LinearLayout) findViewById(R.id.expenseByLL);

        fromDepositExpenseET = (EditText) findViewById(R.id.fromDepositExpenseET);

        expByPersonSpinner = (Spinner) findViewById(R.id.expByPersonSpinner);
        expByPersonAmount = (EditText) findViewById(R.id.expByPersonAmount);

        tvDate = (TextView)findViewById(R.id.tvDate);
        ivDate = (ImageView) findViewById(R.id.ivDate);
        dateRL = (RelativeLayout) findViewById(R.id.dateRL);

        // Setting Default Time and Date
        expense_date = model.getDate();
        date_value = model.getDateValue();
        tvDate.setText(expense_date);

        SimpleDateFormat f1 = new SimpleDateFormat("dd-MM-yyyy");
        Date d1 = null,d2=null;
        try {
            d1 = f1.parse(expense_date);
            d2 = new Date(date_value);



        } catch (ParseException e) {
            e.printStackTrace();
        }


        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        mYear = Integer.parseInt(df.format(d2));
        mMonth = d2.getMonth();
        mDay = d2.getDate();


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
                tvDate.setText(expense_date);

                SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date d = f.parse(expense_date);

                    date_value = d.getTime() + time_to_add;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        dateRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditExpense.this,dateSetListener,mYear,mMonth,mDay);
                datePickerDialog.getWindow().setWindowAnimations(R.style.DialogAnimationUpDown);
                datePickerDialog.show();
            }
        });

        DataBaseHelper dataBaseHelper = new DataBaseHelper(EditExpense.this);
        categories = dataBaseHelper.getCategories();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerArrayAdapter);
        int i =0;
        for( i=0;i<categories.length;i++){
            if(categories[i].equalsIgnoreCase(model.getCategory())){
                break;
            }
        }

        categorySpinner.setSelection(i);

        expByAllCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    personsSpinner.setVisibility(View.GONE);
                    sharingTempTV.setVisibility(View.GONE);
                }else{
                    personsSpinner.setVisibility(View.VISIBLE);
                    sharingTempTV.setVisibility(View.VISIBLE);
                }
            }
        });

        persons = dataBaseHelper.getPersonsListAsString(trip_id);
        personsSpinner.setItems(persons);
        personsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        expByDepCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    expenseFromDepositLL.setVisibility(View.VISIBLE);
                    expenseByLL.setVisibility(View.GONE);
                }else{
                    expenseFromDepositLL.setVisibility(View.GONE);
                    expenseByLL.setVisibility(View.VISIBLE);
                }
            }
        });

        String selectedPersonsTemp = model.getShareBy();
        String selectedPersonsTempArray[] = selectedPersonsTemp.split(",");
        for(int j=0;j<selectedPersonsTempArray.length;j++){
            selectedPersonsTempArray[j] = selectedPersonsTempArray[j].trim();
        }

        personsSpinner.setSelection(selectedPersonsTempArray);

        String tempPersons = persons[0];
        for(int k=1;k<persons.length;k++){
            tempPersons = tempPersons + ", "+persons[k];
        }


        if(tempPersons.equalsIgnoreCase(model.getExpBy())){
            expByAllCB.setChecked(true);
        }else{
            expByAllCB.setChecked(false);
        }

        descTv.setText(model.getItemName());

        ArrayAdapter<String> spinnerPersonExpByArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, persons); //selected item will look like a spinner set from XML
        spinnerPersonExpByArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expByPersonSpinner.setAdapter(spinnerPersonExpByArrayAdapter);

        if(model.getAmountType() == 1){
            expByDepCB.setChecked(true);

            fromDepositExpenseET.setText(model.getAmount()+"");

        }else{
            expByDepCB.setChecked(false);


            int l =0;
            for( l=0;l<persons.length;l++){
                if(persons[l].equalsIgnoreCase(model.getExpBy())){
                    break;
                }
            }

            expByPersonSpinner.setSelection(l);
            expByPersonAmount.setText(model.getAmount()+"");

        }

    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("isEdited",false);
        setResult(201,intent);

        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);

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

            String tempPersons = persons[0];
            for(int i=1;i<persons.length;i++){
                tempPersons = tempPersons + ", "+persons[i];
            }

            descriptionSelected = descTv.getText().toString();
            categorySelected = (String) categorySpinner.getSelectedItem();
            dateSelected = expense_date;

            if(descriptionSelected.equalsIgnoreCase("")){
                descTv.setError("Please enter description.");
            }else{

                descriptionSelected = descriptionSelected.trim().substring(0, 1).toUpperCase() + descriptionSelected.trim().substring(1);

                if(expByAllCB.isChecked()){

                    expShareByPersonsSelected = tempPersons;
                    doNext();
                }else{

                    expShareByPersonsSelected = personsSpinner.getSelectedItemsAsString();
                    if(expShareByPersonsSelected.equalsIgnoreCase("")){

                        Toast.makeText(this, "Please select persons sharing the expense", Toast.LENGTH_SHORT).show();
                    }else{
                        doNext();
                    }
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }

    void doNext(){

        int amount_type;

        if(expByDepCB.isChecked()){

            //amount type = 1 refers to deposit money spent
            //amount type = 2 refers to personal money spent

            amount_type = 1;
            if(fromDepositExpenseET.getText().toString().equalsIgnoreCase("")){
                Toast.makeText(this, "Please select expense amount.", Toast.LENGTH_SHORT).show();
            }else{
                DataBaseHelper dataBaseHelper = new DataBaseHelper(EditExpense.this);
                Double deposit_amount_remaining = dataBaseHelper.getDepositMoneyRemaining(trip_id);
                Double fromDepositExpense = Double.parseDouble(fromDepositExpenseET.getText().toString());
                deposit_amount_remaining += model.getAmount();

                if(deposit_amount_remaining >= fromDepositExpense){
                    //add expense

                    if(dataBaseHelper.editExpense(trip_id,descriptionSelected,categorySelected,dateSelected,expShareByPersonsSelected,amount_type,null,fromDepositExpense,date_value,model.getItemId())){
                        Toast.makeText(this, "Expense edited successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("isEdited",true);
                        setResult(201,intent);
                        finish();
                        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);

                    }else{
                        Snackbar.make(findViewById(android.R.id.content), "Unexpected error occurred", Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    Snackbar.make(findViewById(android.R.id.content), "You do not have sufficient deposit money. Deposit money remaining = "+deposit_amount_remaining, Snackbar.LENGTH_LONG).show();
                }
            }
        }else{

            //amount type = 1 refers to deposit money spent
            //amount type = 2 refers to personal money spent

            amount_type = 2;

            if(expByPersonAmount.getText().toString().equalsIgnoreCase("")){
                Snackbar.make(findViewById(android.R.id.content), "Please select expense by person", Snackbar.LENGTH_LONG).show();

            }else{
                //add expense

                ArrayList<AddExpenseByPersonModel> expenseByPersonGlobalList = new ArrayList<>();

                AddExpenseByPersonModel expenseByPersonGlobalModel = new AddExpenseByPersonModel();
                expenseByPersonGlobalModel.setName((String) expByPersonSpinner.getSelectedItem());
                expenseByPersonGlobalModel.setAmount(Double.valueOf(expByPersonAmount.getText().toString()));

                expenseByPersonGlobalList.add(expenseByPersonGlobalModel);

                DataBaseHelper dataBaseHelper = new DataBaseHelper(EditExpense.this);
                if(dataBaseHelper.editExpense(trip_id,descriptionSelected,categorySelected,dateSelected,expShareByPersonsSelected,amount_type,expenseByPersonGlobalList,null,date_value,model.getItemId())){
                    Toast.makeText(this, "Expense edited successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("isEdited",true);
                    setResult(201,intent);
                    finish();
                    overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);

                }else{
                    Snackbar.make(findViewById(android.R.id.content), "Some error occurred", Snackbar.LENGTH_LONG).show();
                }

            }
        }

    }

}
