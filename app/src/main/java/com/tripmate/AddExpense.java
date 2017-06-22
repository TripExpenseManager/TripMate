package com.tripmate;

import android.app.DatePickerDialog;
import android.app.models.AddExpenseByPersonModel;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Calendar;
import java.util.Date;

public class AddExpense extends AppCompatActivity {

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
    RecyclerView expenseByRecyclerView;

    EditText fromDepositExpenseET;


    ArrayList<AddExpenseByPersonModel> expenseByPersonGlobalList = new ArrayList<>();
    AddExpenseByPersonAdapter addExpenseByPersonAdapter;

    ImageView addExpImageView;

    String descriptionSelected,categorySelected,dateSelected,expShareByPersonsSelected;
    int amount_share_by_type;

    DataBaseHelper dataBaseHelper = new DataBaseHelper(AddExpense.this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Expense");

        Intent intent = getIntent();
        trip_id = intent.getStringExtra("trip_id");

        descTv = (EditText) findViewById(R.id.tvDesc);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        expByAllCB = (CheckBox) findViewById(R.id.expByAllCB);

        personsSpinner = (MultiSelectionSpinner) findViewById(R.id.personsSpinner);
        sharingTempTV = (TextView) findViewById(R.id.sharingTempTV);

        expByDepCB = (CheckBox) findViewById(R.id.expByDepCB);
        expenseFromDepositLL = (LinearLayout) findViewById(R.id.expenseFromDepositLL);
        expenseByLL = (LinearLayout) findViewById(R.id.expenseByLL);
        expenseByRecyclerView = (RecyclerView) findViewById(R.id.expenseByRecyclerView);

        fromDepositExpenseET = (EditText) findViewById(R.id.fromDepositExpenseET);

        addExpImageView = (ImageView) findViewById(R.id.addExpImageView);

        tvDate = (TextView)findViewById(R.id.tvDate);
        ivDate = (ImageView) findViewById(R.id.ivDate);
        dateRL = (RelativeLayout) findViewById(R.id.dateRL);

        // Getting Current Date and Time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        date_value = c.getTimeInMillis();

        // Setting Default Time and Date
        expense_date = mDay + "-" + (mMonth + 1) + "-" + mYear;
        tvDate.setText(expense_date);

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
                    date_value = d.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        };

        dateRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpense.this,dateSetListener,mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });

        categories = dataBaseHelper.getCategories();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerArrayAdapter);
        categorySpinner.setSelection(0);

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


        AddExpenseByPersonModel model = new AddExpenseByPersonModel();
        expenseByPersonGlobalList.add(model);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddExpense.this);
        expenseByRecyclerView.setLayoutManager(linearLayoutManager);

        addExpenseByPersonAdapter = new AddExpenseByPersonAdapter(AddExpense.this,expenseByPersonGlobalList);
        expenseByRecyclerView.setAdapter(addExpenseByPersonAdapter);


        addExpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(expenseByPersonGlobalList.size() == persons.length){
                    Toast.makeText(AddExpense.this, "You cannot add more than "+ persons.length+ " expenses, as the trip contains only "+persons.length+" members.", Toast.LENGTH_SHORT).show();
                }else{
                    AddExpenseByPersonModel model = new AddExpenseByPersonModel();
                    expenseByPersonGlobalList.add(model);
                    addExpenseByPersonAdapter.notifyItemInserted(expenseByPersonGlobalList.size()-1);
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_expenses_menu,menu);
        return  true;
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
                if(expByAllCB.isChecked()){
                    amount_share_by_type = 1;
                    expShareByPersonsSelected = "all";
                    doNext();
                }else{
                    expShareByPersonsSelected = personsSpinner.getSelectedItemsAsString();
                    if(expShareByPersonsSelected.equalsIgnoreCase("")){
                        amount_share_by_type = 2;
                        Toast.makeText(this, "Please select persons sharing the expense", Toast.LENGTH_SHORT).show();
                    }
                    else if(expShareByPersonsSelected.equalsIgnoreCase(tempPersons)){
                        amount_share_by_type = 1;
                        expShareByPersonsSelected = "all";
                        doNext();
                    }else{
                        amount_share_by_type = 2;
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
            amount_type = 1;
            if(fromDepositExpenseET.getText().toString().equalsIgnoreCase("")){
                Toast.makeText(this, "Please select expense amount.", Toast.LENGTH_SHORT).show();
            }else{
                //add expense
                Double fromDepositExpense = Double.parseDouble(fromDepositExpenseET.getText().toString());

                if(dataBaseHelper.addExpense(trip_id,descriptionSelected,categorySelected,dateSelected,amount_share_by_type,expShareByPersonsSelected,amount_type,null,fromDepositExpense,date_value)){
                    Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show();
                }

            }
        }else{
            amount_type = 2;
            if(expenseByPersonGlobalList.size()==0){
                Toast.makeText(this, "Please select expense by persons.", Toast.LENGTH_SHORT).show();
            }else{
                int res =0;
                for(int i=0;i<expenseByPersonGlobalList.size();i++){
                    if(expenseByPersonGlobalList.get(i).getAmount() == null){
                        res = 1;
                        break;
                    }
                }
                if(res == 1){
                    Toast.makeText(this, "Please select expense amount.", Toast.LENGTH_SHORT).show();
                }else{
                    //add expense

                    if(dataBaseHelper.addExpense(trip_id,descriptionSelected,categorySelected,dateSelected,amount_share_by_type,expShareByPersonsSelected,amount_type,expenseByPersonGlobalList,null,date_value)){
                        Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        }

    }

    class AddExpenseByPersonAdapter extends RecyclerView.Adapter<AddExpenseByPersonAdapter.PersonHolder>{
        Context mcontext;
        ArrayList<AddExpenseByPersonModel> expenseByPersonArrayList;

        public AddExpenseByPersonAdapter(Context mcontext, ArrayList<AddExpenseByPersonModel> expenseByPersonArrayList) {
            this.mcontext = mcontext;
            this.expenseByPersonArrayList = expenseByPersonArrayList;
        }

        public class PersonHolder extends  RecyclerView.ViewHolder{
            Spinner expByPersonSpinner;
            EditText expByPersonAmount;
            ImageView closePersonImageView;


            public PersonHolder(View view){
                super(view);
                expByPersonSpinner = (Spinner) view.findViewById(R.id.expByPersonSpinner);
                expByPersonAmount = (EditText) view.findViewById(R.id.expByPersonAmount);
                closePersonImageView = (ImageView) view.findViewById(R.id.closePersonImageView);
            }
        }


        @Override
        public AddExpenseByPersonAdapter.PersonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.expense_person_add_row, parent, false);
            return  new AddExpenseByPersonAdapter.PersonHolder(v);
        }

        @Override
        public void onBindViewHolder(final AddExpenseByPersonAdapter.PersonHolder holder, int position) {
            AddExpenseByPersonModel expensePerson = expenseByPersonArrayList.get(position);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(AddExpense.this, android.R.layout.simple_spinner_item, persons); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.expByPersonSpinner.setAdapter(spinnerArrayAdapter);
            holder.expByPersonSpinner.setSelection(expensePerson.getNamePosition());

            if(expensePerson.getAmount()!=null){
                holder.expByPersonAmount.setText(expensePerson.getAmount()+"");
            }else{
                holder.expByPersonAmount.setText("");
            }

            holder.closePersonImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expenseByPersonGlobalList.remove(holder.getAdapterPosition());
                    addExpenseByPersonAdapter.notifyItemRemoved(holder.getAdapterPosition());
                    addExpenseByPersonAdapter.notifyItemRangeChanged(holder.getAdapterPosition(), expenseByPersonGlobalList.size());
                }
            });


            holder.expByPersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {

                    expenseByPersonGlobalList.get(holder.getAdapterPosition()).setName((String) holder.expByPersonSpinner.getSelectedItem());
                    expenseByPersonGlobalList.get(holder.getAdapterPosition()).setNamePosition(holder.expByPersonSpinner.getSelectedItemPosition());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            holder.expByPersonAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if(holder.expByPersonAmount.getText().toString().equalsIgnoreCase("")){
                        expenseByPersonGlobalList.get(holder.getAdapterPosition()).setAmount(null);
                    }else{
                        expenseByPersonGlobalList.get(holder.getAdapterPosition()).setAmount(Double.valueOf(holder.expByPersonAmount.getText().toString()));
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return expenseByPersonArrayList.size();
        }


    }









}
