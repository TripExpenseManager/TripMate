package com.tripmate;

import android.app.models.AddExpenseByPersonModel;
import android.app.models.ExpenseModel;
import android.app.models.GraphItemModel;
import android.app.models.NotesModel;
import android.app.models.ParentExpenseItemModel;
import android.app.models.PersonModel;
import android.app.models.PersonWiseExpensesSummaryModel;
import android.app.models.TodoModel;
import android.app.models.TripModel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import okhttp3.internal.Util;

/**
 * Created by Sai Krishna on 6/16/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TripExpenseManager";
    private static final int DATABASE_VERSION = 2;

    private static final String TRIPS_TABLE_NAME = "trips";
    private static final String TRIPS_COLUMN_ID = "key_trip_id";
    private static final String TRIPS_COLUMN_TRIP_NAME = "key_trip_name";
    private static final String TRIPS_COLUMN_TRIP_DATE = "key_trip_date";
    private static final String TRIPS_COLUMN_TRIP_PLACES = "key_trip_places";
    private static final String TRIPS_COLUMN_TRIP_TOTAL_AMOUNT = "key_trip_total_amt";
    private static final String TRIPS_COLUMN_IMAGE_URL = "key_trip_image_url";
    private static final String TRIPS_COLUMN_TOTAL_PERSONS = "key_trip_total_persons";
    private static final String TRIPS_COLUMN_TRIP_CURRENCY = "key_trip_currency";


    private static final String ITEMS_TABLE_NAME = "items";
    private static final String ITEMS_COLUMN_TRIP_ID = "key_trip_id";
    private static final String ITEMS_COLUMN_ITEM_NAME = "key_item_name";
    private static final String ITEMS_COLUMN_AMOUNT_TYPE = "key_amount_type";
    private static final String ITEMS_COLUMN_ITEM_AMOUNT = "key_item_amt";
    private static final String ITEMS_COLUMN_ITEM_EXP_BY_TYPE = "key_item_exp_by_type";
    private static final String ITEMS_COLUMN_ITEM_EXP_BY = "key_item_exp_by";
    private static final String ITEMS_COLUMN_ITEM_CAT = "key_item_cat";
    private static final String ITEMS_COLUMN_ITEM_DATE = "key_item_date";
    private static final String ITEMS_COLUMN_ITEM_DATE_VALUE = "key_item_date_value";
    private static final String ITEMS_COLUMN_ITEM_SHARE_BY_TYPE = "key_item_share_by_type";
    private static final String ITEMS_COLUMN_ITEM_SHARE_BY = "key_item_share_by";
    private static final String ITEMS_COLUMN_ITEM_ID = "key_item_id";
    private static final String ITEMS_COLUMN_ITEM_CURRENCY = "key_item_currency";
    private static final String ITEMS_COLUMN_ITEM_CURRENCY_CONVERSION_RATE = "key_item_currency_conversion_rate";



    private static final String PERSONS_TABLE_NAME = "persons";
    private static final String PERSONS_COLUMN_TRIP_ID = "key_trip_id";
    private static final String PERSONS_COLUMN_PERSON_NAME = "key_person_name";
    private static final String PERSONS_COLUMN_PERSON_MOBILE = "key_person_mobile";
    private static final String PERSONS_COLUMN_PERSON_EMAIL = "key_person_email";
    private static final String PERSONS_COLUMN_PERSON_DEPOSIT = "key_person_deposit";
    private static final String PERSONS_COLUMN_PERSON_ADMIN = "key_person_admin";


    private static final String NOTES_TABLE_NAME = "notes";
    private static final String NOTES_COLUMN_NOTE_ID = "key_note_id";
    private static final String NOTES_COLUMN_TRIP_ID = "key_trip_id";
    private static final String NOTES_COLUMN_NOTE_TITLE = "key_note_title";
    private static final String NOTES_COLUMN_NOTE_CONTENT_TYPE = "key_note_content_type";
    private static final String NOTES_COLUMN_NOTE_CONTENT = "key_note_content";
    private static final String NOTES_COLUMN_NOTE_CONTENT_STATUS = "key_note_content_status";
    private static final String NOTES_COLUMN_NOTE_DATE = "key_note_date";

    private static final String CATEGORIES_TABLE_NAME = "categories";
    private static final String CATEGORIES_COLUMN_CAT_NAME = "key_cat_name";

    private String CREATE_TRIPS_TABLE = "CREATE TABLE " + TRIPS_TABLE_NAME + " ( "+ TRIPS_COLUMN_ID + " TEXT PRIMARY KEY, "
            + TRIPS_COLUMN_TRIP_NAME + " TEXT, "+ TRIPS_COLUMN_TRIP_DATE+" TEXT, "+TRIPS_COLUMN_TRIP_PLACES+" TEXT, "
            +TRIPS_COLUMN_TRIP_TOTAL_AMOUNT+" REAL , "+ TRIPS_COLUMN_IMAGE_URL +" TEXT , "
            +TRIPS_COLUMN_TOTAL_PERSONS+" INTEGER , " +TRIPS_COLUMN_TRIP_CURRENCY +" TEXT )";

    private String CREATE_ITEMS_TABLE = "CREATE TABLE " + ITEMS_TABLE_NAME + " ( "+ ITEMS_COLUMN_ITEM_ID + " TEXT PRIMARY KEY,"
            + ITEMS_COLUMN_TRIP_ID + " TEXT,"+ ITEMS_COLUMN_ITEM_NAME + " TEXT, "+ITEMS_COLUMN_AMOUNT_TYPE+" INTEGER, "+
            ITEMS_COLUMN_ITEM_AMOUNT+ " REAL, "+ITEMS_COLUMN_ITEM_EXP_BY_TYPE+" INTEGER, "+ITEMS_COLUMN_ITEM_EXP_BY+" TEXT, "
            +ITEMS_COLUMN_ITEM_CAT+" TEXT, "+ITEMS_COLUMN_ITEM_DATE+" TEXT, "+ITEMS_COLUMN_ITEM_DATE_VALUE+" TEXT , "
            +ITEMS_COLUMN_ITEM_SHARE_BY_TYPE+" INTEGER , "+ITEMS_COLUMN_ITEM_SHARE_BY+" TEXT , "+ITEMS_COLUMN_ITEM_CURRENCY+" TEXT , "
            +ITEMS_COLUMN_ITEM_CURRENCY_CONVERSION_RATE+" REAL)";

    private String CREATE_PERSONS_TABLE = "CREATE TABLE " + PERSONS_TABLE_NAME + "("+ PERSONS_COLUMN_TRIP_ID + " TEXT,"
            + PERSONS_COLUMN_PERSON_NAME + " TEXT,"+ PERSONS_COLUMN_PERSON_MOBILE + " TEXT, "
            +PERSONS_COLUMN_PERSON_EMAIL+" TEXT,"+ PERSONS_COLUMN_PERSON_DEPOSIT+" REAL, "
            +PERSONS_COLUMN_PERSON_ADMIN+" INTEGER )";

    private String CREATE_NOTES_TABLE = "CREATE TABLE " + NOTES_TABLE_NAME + "("+ NOTES_COLUMN_NOTE_ID + " TEXT PRIMARY KEY,"
            + NOTES_COLUMN_TRIP_ID + " TEXT,"+ NOTES_COLUMN_NOTE_TITLE + " TEXT, " +NOTES_COLUMN_NOTE_CONTENT_TYPE+" INTEGER, "
            +NOTES_COLUMN_NOTE_CONTENT+" TEXT, "+ NOTES_COLUMN_NOTE_CONTENT_STATUS + " TEXT, "+NOTES_COLUMN_NOTE_DATE+" TEXT)";

    private String CREATE_CATEGORIES_TABLE = "CREATE TABLE "+CATEGORIES_TABLE_NAME+" ( "+CATEGORIES_COLUMN_CAT_NAME+" TEXT)";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TRIPS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_PERSONS_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);

        String cats[] = {"Drink","Entertainment","Food","Hotel","Medical","Miscellaneous","Parking","Shopping","Toll","Travel"};

        for (String cat : cats) {
            ContentValues values = new ContentValues();
            values.put(CATEGORIES_COLUMN_CAT_NAME, cat);
            db.insert(CATEGORIES_TABLE_NAME, null, values);
        }
        //adding sample trip
        String trip_id = "TRIP"+ UUID.randomUUID().toString();
        TripModel trip = new TripModel("Sample trip","Place 1$@#Place 2$@#Place 3$@#Place 4$@#Place5",
                25 + "-" + 1 + "-" + 2018);
        trip.setTrip_id(trip_id);
        trip.setTotal_persons(5);
        trip.setTripcurrency("INR");
        trip.setTrip_amount(500.0);

        addTrip(trip,db);

        ArrayList<PersonModel> tripPersonModels = new ArrayList<>();
        PersonModel model1 = new PersonModel();
        model1.setTrip_id(trip_id);
        model1.setAdmin(1);
        model1.setDeposit(100.0);
        model1.setEmail("person1@gmail.com");
        model1.setMobile("9999999999");
        model1.setName("Person 1");
        tripPersonModels.add(model1);

        PersonModel model2 = new PersonModel();
        model2.setTrip_id(trip_id);
        model2.setAdmin(0);
        model2.setDeposit(200.0);
        model2.setEmail("person2@gmail.com");
        model2.setMobile("9999999998");
        model2.setName("Person 2");
        tripPersonModels.add(model2);

        PersonModel model3 = new PersonModel();
        model3.setTrip_id(trip_id);
        model3.setAdmin(0);
        model3.setDeposit(200.0);
        model3.setEmail("person3@gmail.com");
        model3.setMobile("9999999997");
        model3.setName("Person 3");
        tripPersonModels.add(model3);

        PersonModel model4 = new PersonModel();
        model4.setTrip_id(trip_id);
        model4.setAdmin(0);
        model4.setDeposit(100.0);
        model4.setEmail("person4@gmail.com");
        model4.setMobile("9999999996");
        model4.setName("Person 4");
        tripPersonModels.add(model4);

        PersonModel model5 = new PersonModel();
        model5.setTrip_id(trip_id);
        model5.setAdmin(0);
        model5.setDeposit(100.0);
        model5.setEmail("person5@gmail.com");
        model5.setMobile("9999999995");
        model5.setName("Person 5");
        tripPersonModels.add(model5);

        addPersons(trip_id, tripPersonModels,db);

        //Adding places to visit checklist
        ArrayList<TodoModel> todoModels = new ArrayList<>();

        todoModels.add(new TodoModel("Place 1",false));
        todoModels.add(new TodoModel("Place 2",false));
        todoModels.add(new TodoModel("Place 3",false));
        todoModels.add(new TodoModel("Place 4",false));
        todoModels.add(new TodoModel("Place 5",false));

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

        addNotes(notesModel,db);

        ExpenseModel modelToBeAdded = new ExpenseModel();
        modelToBeAdded.setItemName("Sample expense 1");
        modelToBeAdded.setTripId(trip_id);
        modelToBeAdded.setAmountType(2);
        modelToBeAdded.setAmount(200.0);
        modelToBeAdded.setExpByType(1);
        modelToBeAdded.setExpBy("Person 1");
        modelToBeAdded.setCategory("Drink");
        modelToBeAdded.setDate(2 + "-" + 1 + "-" + 2018);
        modelToBeAdded.setDateValue(1514876793);
        modelToBeAdded.setShareByType(1);
        modelToBeAdded.setShareBy("Person 2");
        modelToBeAdded.setCurrency("INR");
        modelToBeAdded.setCurrencyConversionRate(1.0);


        addExpenseNew(modelToBeAdded,db);


        modelToBeAdded = new ExpenseModel();
        modelToBeAdded.setItemName("Sample expense 2");
        modelToBeAdded.setTripId(trip_id);
        modelToBeAdded.setAmountType(2);
        modelToBeAdded.setAmount(300.0);
        modelToBeAdded.setExpByType(1);
        modelToBeAdded.setExpBy("Person 1");
        modelToBeAdded.setCategory("Travel");
        modelToBeAdded.setDate(2 + "-" + 1 + "-" + 2018);
        modelToBeAdded.setDateValue(1514876797);
        modelToBeAdded.setShareByType(1);
        modelToBeAdded.setShareBy("Person 3");
        modelToBeAdded.setCurrency("INR");
        modelToBeAdded.setCurrencyConversionRate(1.0);


        addExpenseNew(modelToBeAdded,db);

    }

    private String  encryptTodos(ArrayList<TodoModel> todoModels){

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


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("SKSK","onupgradecalled");


        switch (newVersion){
            case 2 : {


                ArrayList<TripModel> allTripsList = getTripsData(db);
                db.execSQL("DROP TABLE IF EXISTS " + TRIPS_TABLE_NAME);
                db.execSQL(CREATE_TRIPS_TABLE);

                for(TripModel trip : allTripsList){

                    ContentValues values = new ContentValues();

                    String placesToVisit = trip.getTrip_places();
                    placesToVisit = placesToVisit.replace(",",Utils.DELIMITER_FOR_PLACES_TO_VISIT);

                    values.put(TRIPS_COLUMN_TRIP_PLACES,placesToVisit);
                    values.put(TRIPS_COLUMN_ID,trip.getTrip_id());
                    values.put(TRIPS_COLUMN_TRIP_NAME,trip.getTrip_name());
                    values.put(TRIPS_COLUMN_TRIP_DATE,trip.getTrip_date());
                    values.put(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT,trip.getTrip_amount());
                    values.put(TRIPS_COLUMN_IMAGE_URL,trip.getImageUrl());
                    values.put(TRIPS_COLUMN_TOTAL_PERSONS,trip.getTotal_persons());
                    values.put(TRIPS_COLUMN_TRIP_CURRENCY,"INR");

                    db.insert(TRIPS_TABLE_NAME,null,values);

                    Log.d("SKSK","onupgradecalled case 2 ");

                }


                ArrayList<ExpenseModel> expensesList = getAllExpensesInAllTrips(db);
                db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME);
                db.execSQL(CREATE_ITEMS_TABLE);

                for(ExpenseModel expenseModel : expensesList){

                    ContentValues values = new ContentValues();
                    values.put(ITEMS_COLUMN_TRIP_ID,expenseModel.getTripId());
                    values.put(ITEMS_COLUMN_ITEM_ID,expenseModel.getItemId());
                    values.put(ITEMS_COLUMN_ITEM_NAME,expenseModel.getItemName());
                    values.put(ITEMS_COLUMN_AMOUNT_TYPE,expenseModel.getAmountType());
                    values.put(ITEMS_COLUMN_ITEM_AMOUNT,RoundOff(expenseModel.getAmount()));
                    values.put(ITEMS_COLUMN_ITEM_EXP_BY_TYPE,1);
                    values.put(ITEMS_COLUMN_ITEM_EXP_BY,expenseModel.getExpBy());
                    values.put(ITEMS_COLUMN_ITEM_CAT,expenseModel.getCategory());
                    values.put(ITEMS_COLUMN_ITEM_DATE,expenseModel.getDate());
                    values.put(ITEMS_COLUMN_ITEM_DATE_VALUE,expenseModel.getDateValue());
                    values.put(ITEMS_COLUMN_ITEM_SHARE_BY_TYPE,1);

                    String expShareBy = expenseModel.getShareBy();
                    expShareBy = expShareBy.replace(", ",Utils.DELIMITER_FOR_EXP_PERSONS);

                    values.put(ITEMS_COLUMN_ITEM_SHARE_BY,expShareBy);
                    values.put(ITEMS_COLUMN_ITEM_CURRENCY,"INR");
                    values.put(ITEMS_COLUMN_ITEM_CURRENCY_CONVERSION_RATE,1.0);
                    db.insert(ITEMS_TABLE_NAME,null,values);

                }

                break;
            }
        }




    }

    //Only for upgrading from version 1 to version 2

    private ArrayList<ExpenseModel> getAllExpensesInAllTrips(SQLiteDatabase db){
        ArrayList<ExpenseModel> expenseModelArrayList = new ArrayList<>();

        Cursor cursor = db.query(ITEMS_TABLE_NAME,null,null,null,null,null,null);

        if(cursor!=null && cursor.moveToFirst()){
            do {
                ExpenseModel expenseModel = new ExpenseModel();
                expenseModel.setTripId(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_TRIP_ID)));
                expenseModel.setItemName(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_NAME)));
                expenseModel.setAmount(cursor.getDouble(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_AMOUNT)));
                expenseModel.setAmountType(cursor.getInt(cursor.getColumnIndex(ITEMS_COLUMN_AMOUNT_TYPE)));
                expenseModel.setExpBy(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_EXP_BY)));
                expenseModel.setCategory(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_CAT)));
                expenseModel.setDate(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_DATE)));
                expenseModel.setShareBy(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_SHARE_BY)));
                expenseModel.setItemId(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_ID)));
                expenseModel.setDateValue(cursor.getLong(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_DATE_VALUE)));

                expenseModelArrayList.add(expenseModel);

            }while(cursor.moveToNext());

            cursor.close();
        }
        return expenseModelArrayList;
    }

    private ArrayList<TripModel> getTripsData(SQLiteDatabase db) {
        ArrayList<TripModel> trip_array_list = new ArrayList<>();

        Cursor cursor =  db.rawQuery( "select * from "+TRIPS_TABLE_NAME, null );

        if (cursor.moveToFirst()) {
            do {
                TripModel model = new TripModel();
                model.setTrip_name(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_NAME)));
                model.setTrip_date(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_DATE)));
                model.setTrip_amount(cursor.getDouble(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT)));
                model.setTrip_id(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_ID)));
                model.setTrip_places(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_PLACES)));
                model.setImageUrl(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_IMAGE_URL)));
                model.setTotal_persons(cursor.getInt(cursor.getColumnIndex(TRIPS_COLUMN_TOTAL_PERSONS)));

                trip_array_list.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trip_array_list;
    }


    //adding sample trip

    boolean addTrip(TripModel trip,SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_ID,trip.getTrip_id());
        values.put(TRIPS_COLUMN_TRIP_NAME,trip.getTrip_name());
        values.put(TRIPS_COLUMN_TRIP_PLACES,trip.getTrip_places());
        values.put(TRIPS_COLUMN_TRIP_DATE,trip.getTrip_date());
        values.put(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT,trip.getTrip_amount());
        values.put(TRIPS_COLUMN_IMAGE_URL,trip.getImageUrl());
        values.put(TRIPS_COLUMN_TOTAL_PERSONS,trip.getTotal_persons());
        values.put(TRIPS_COLUMN_TRIP_CURRENCY,trip.getTripcurrency());
        values.put(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT,trip.getTrip_amount());

        db.insert(TRIPS_TABLE_NAME,null,values);

        return true;
    }

    void addNotes(NotesModel notesModel,SQLiteDatabase db){

        ContentValues values = new ContentValues();
        values.put(NOTES_COLUMN_TRIP_ID,notesModel.getNote_TripId());
        values.put(NOTES_COLUMN_NOTE_ID,notesModel.getNote_Id());
        values.put(NOTES_COLUMN_NOTE_TITLE,notesModel.getNote_Title());
        values.put(NOTES_COLUMN_NOTE_CONTENT,notesModel.getNote_Body());
        values.put(NOTES_COLUMN_NOTE_CONTENT_TYPE,notesModel.getNote_ContentType());
        values.put(NOTES_COLUMN_NOTE_CONTENT_STATUS,notesModel.getNote_ContentStatus());
        values.put(NOTES_COLUMN_NOTE_DATE,notesModel.getNote_Date());

        db.insert(NOTES_TABLE_NAME,null,values);


    }

    void addPersons(String trip_id, ArrayList<PersonModel> personsList,SQLiteDatabase db){
        for (PersonModel personModel : personsList) {
            ContentValues values = new ContentValues();
            values.put(PERSONS_COLUMN_TRIP_ID,trip_id);
            values.put(PERSONS_COLUMN_PERSON_NAME, personModel.getName());
            values.put(PERSONS_COLUMN_PERSON_MOBILE, personModel.getMobile());
            values.put(PERSONS_COLUMN_PERSON_EMAIL, personModel.getEmail());
            values.put(PERSONS_COLUMN_PERSON_DEPOSIT, personModel.getDeposit());
            values.put(PERSONS_COLUMN_PERSON_ADMIN, personModel.getAdmin());
            db.insert(PERSONS_TABLE_NAME,null,values);
        }
    }

    boolean addExpenseNew(ExpenseModel expenseModel,SQLiteDatabase db){

        ContentValues values = new ContentValues();
        values.put(ITEMS_COLUMN_TRIP_ID,expenseModel.getTripId());
        values.put(ITEMS_COLUMN_ITEM_NAME,expenseModel.getItemName());
        values.put(ITEMS_COLUMN_AMOUNT_TYPE,expenseModel.getAmountType());
        values.put(ITEMS_COLUMN_ITEM_AMOUNT,RoundOff(expenseModel.getAmount()));
        values.put(ITEMS_COLUMN_ITEM_EXP_BY_TYPE,expenseModel.getExpByType());
        values.put(ITEMS_COLUMN_ITEM_EXP_BY,expenseModel.getExpBy());
        values.put(ITEMS_COLUMN_ITEM_CAT,expenseModel.getCategory());
        values.put(ITEMS_COLUMN_ITEM_DATE,expenseModel.getDate());
        values.put(ITEMS_COLUMN_ITEM_DATE_VALUE,expenseModel.getDateValue());
        values.put(ITEMS_COLUMN_ITEM_ID,"ITEM"+ UUID.randomUUID().toString());
        values.put(ITEMS_COLUMN_ITEM_SHARE_BY_TYPE,expenseModel.getShareByType());
        values.put(ITEMS_COLUMN_ITEM_SHARE_BY,expenseModel.getShareBy());
        values.put(ITEMS_COLUMN_ITEM_CURRENCY,expenseModel.getCurrency());
        values.put(ITEMS_COLUMN_ITEM_CURRENCY_CONVERSION_RATE,expenseModel.getCurrencyConversionRate());

        db.insert(ITEMS_TABLE_NAME,null,values);

        return true;
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //please update your app

    }





    boolean isTripExists(String trip_id){

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TRIPS_TABLE_NAME,null,TRIPS_COLUMN_ID+"=?",new String[]{trip_id},
                null,null,null);

        if(cursor.getCount() == 0){
            cursor.close();
            return false;
        }else{
            cursor.close();
            return  true;
        }

    }

    boolean deleteTrip(String trip_id){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(ITEMS_TABLE_NAME,ITEMS_COLUMN_TRIP_ID + " = \"" + trip_id+"\"",null);
        db.delete(PERSONS_TABLE_NAME,PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\"",null);
        db.delete(NOTES_TABLE_NAME,NOTES_COLUMN_TRIP_ID + " = \"" + trip_id+"\"",null);

        return db.delete(TRIPS_TABLE_NAME,TRIPS_COLUMN_ID + " = \"" + trip_id+"\"",null) > 0;

    }

    void editPerson(String trip_id, PersonModel model){
        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PERSONS_COLUMN_PERSON_MOBILE,model.getMobile());
        cv.put(PERSONS_COLUMN_PERSON_EMAIL,model.getEmail());
        cv.put(PERSONS_COLUMN_PERSON_DEPOSIT,model.getDeposit());

        db.update(PERSONS_TABLE_NAME, cv,PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+PERSONS_COLUMN_PERSON_NAME+" = \""+model.getName()+"\"", null);
    }

    void addAsAdmin(String trip_id, String name, String pastAdmin){
        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PERSONS_COLUMN_PERSON_ADMIN,1);
        db.update(PERSONS_TABLE_NAME, cv,PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+PERSONS_COLUMN_PERSON_NAME+" = \""+name+"\"", null);


        ContentValues cv1 = new ContentValues();
        cv1.put(PERSONS_COLUMN_PERSON_ADMIN,0);
        db.update(PERSONS_TABLE_NAME, cv1,PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+PERSONS_COLUMN_PERSON_NAME+" = \""+pastAdmin+"\"", null);

    }

    private Double RoundOff(Double d){
        return Math.round(d * 100.0) / 100.0;
    }

    double minDepositThatShouldBeAdded(PersonModel personModel){

        Double dep_money_spent = 0.0,total_deposit_money = 0.0;

        ArrayList<ExpenseModel> expenseList = getAllExpenses(personModel.getTrip_id());
        for(int i=0;i<expenseList.size();i++){
            ExpenseModel model = expenseList.get(i);
            if(model.getAmountType() == 1){
                dep_money_spent += model.getAmount();
            }
        }

        ArrayList<PersonModel> personsList = getPersons(personModel.getTrip_id());
        Double prev_deposit_money = 0.0;
        for(int i=0;i<personsList.size();i++){
            total_deposit_money += personsList.get(i).getDeposit();
            if(personsList.get(i).getName().equalsIgnoreCase(personModel.getName())){
                prev_deposit_money = personsList.get(i).getDeposit();
            }
        }

        total_deposit_money -= prev_deposit_money;

        return  dep_money_spent - total_deposit_money;

    }

    void updateTripImageUrl(String trip_id,String imageUrl){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_IMAGE_URL,imageUrl);
        db.update(TRIPS_TABLE_NAME,values,TRIPS_COLUMN_ID+ "=? ", new String[]{trip_id});
    }

    void updateTripPlaces(String trip_id,String places){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_TRIP_PLACES,places);
        db.update(TRIPS_TABLE_NAME,values,TRIPS_COLUMN_ID+ "=? ", new String[]{trip_id});
    }

    void updateTripName(String trip_id,String tripName){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_TRIP_NAME,tripName);

        db.update(TRIPS_TABLE_NAME,values,TRIPS_COLUMN_ID+ "=? ", new String[]{trip_id});
    }

    void updateTripDate(String trip_id,String tripDate){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_TRIP_DATE,tripDate);

        db.update(TRIPS_TABLE_NAME,values,TRIPS_COLUMN_ID+ "=? ", new String[]{trip_id});
    }

    boolean addTrip(TripModel trip){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_ID,trip.getTrip_id());
        values.put(TRIPS_COLUMN_TRIP_NAME,trip.getTrip_name());
        values.put(TRIPS_COLUMN_TRIP_PLACES,trip.getTrip_places());
        values.put(TRIPS_COLUMN_TRIP_DATE,trip.getTrip_date());
        values.put(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT,trip.getTrip_amount());
        values.put(TRIPS_COLUMN_IMAGE_URL,trip.getImageUrl());
        values.put(TRIPS_COLUMN_TOTAL_PERSONS,trip.getTotal_persons());
        values.put(TRIPS_COLUMN_TRIP_CURRENCY,trip.getTripcurrency());

        db.insert(TRIPS_TABLE_NAME,null,values);

        return true;
    }

    boolean removePerson(PersonWiseExpensesSummaryModel model, String trip_id){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(PERSONS_TABLE_NAME, PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+PERSONS_COLUMN_PERSON_NAME+" = \""+ model.getName()+"\"" , null);

        Cursor cursor = db.query(TRIPS_TABLE_NAME,new String[]{TRIPS_COLUMN_TOTAL_PERSONS},TRIPS_COLUMN_ID+"=?",new String[]{trip_id},null,null,null);
        int no_of_persons =0;
        if(cursor.moveToFirst()){
            no_of_persons = cursor.getInt(cursor.getColumnIndex(TRIPS_COLUMN_TOTAL_PERSONS));
        }
        cursor.close();

        ContentValues valuesPersons = new ContentValues();
        valuesPersons.put(TRIPS_COLUMN_TOTAL_PERSONS,(no_of_persons-1));

        db.update(TRIPS_TABLE_NAME,valuesPersons,TRIPS_COLUMN_ID+ "=? ", new String[]{trip_id});

        return true;
    }

    boolean addCategory(String newCategory){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CATEGORIES_COLUMN_CAT_NAME,newCategory);

        db.insert(CATEGORIES_TABLE_NAME,null,values);

        return true;
    }

    boolean deleteCategory(String category){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CATEGORIES_TABLE_NAME, CATEGORIES_COLUMN_CAT_NAME + " = \"" +category+"\"", null);
        return true;
    }

    boolean updateCategory(String newCategory,String oldCategory){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valuesCategory = new ContentValues();
        valuesCategory.put(CATEGORIES_COLUMN_CAT_NAME,newCategory);

        db.update(CATEGORIES_TABLE_NAME,valuesCategory,CATEGORIES_COLUMN_CAT_NAME+ "=? ", new String[]{oldCategory});
        return true;
    }

    String[] getCategories(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(CATEGORIES_TABLE_NAME,null,null,null,null,null,null);
        String[] catList = new String[cursor.getCount()];
        if(cursor.moveToFirst()){
            int i=0;
            do{
                catList[i] = cursor.getString(cursor.getColumnIndex(CATEGORIES_COLUMN_CAT_NAME));
                i++;
            }while(cursor.moveToNext());
        }
        cursor.close();
        return  catList;
    }

    String[] getPersonsListAsString(String trip_id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(PERSONS_TABLE_NAME,new String[]{PERSONS_COLUMN_PERSON_NAME},PERSONS_COLUMN_TRIP_ID+"=?",new String[]{trip_id},null,null,null);
        String[] personsList = new String[cursor.getCount()];
        if(cursor!=null && cursor.moveToFirst()){
            int i=0;
            do{
                personsList[i] = cursor.getString(0);
                i++;
            }while(cursor.moveToNext());
        }
        cursor.close();
        return  personsList;
    }

    void addPersons(String trip_id, ArrayList<PersonModel> personsList){
        SQLiteDatabase db = getWritableDatabase();
        for (PersonModel personModel : personsList) {
            ContentValues values = new ContentValues();
            values.put(PERSONS_COLUMN_TRIP_ID,trip_id);
            values.put(PERSONS_COLUMN_PERSON_NAME, personModel.getName());
            values.put(PERSONS_COLUMN_PERSON_MOBILE, personModel.getMobile());
            values.put(PERSONS_COLUMN_PERSON_EMAIL, personModel.getEmail());
            values.put(PERSONS_COLUMN_PERSON_DEPOSIT, personModel.getDeposit());
            values.put(PERSONS_COLUMN_PERSON_ADMIN, personModel.getAdmin());
            db.insert(PERSONS_TABLE_NAME,null,values);
        }
    }

    boolean addPersonInMiddle(PersonModel model){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PERSONS_COLUMN_TRIP_ID,model.getTrip_id());
        values.put(PERSONS_COLUMN_PERSON_NAME, model.getName());
        values.put(PERSONS_COLUMN_PERSON_MOBILE, model.getMobile());
        values.put(PERSONS_COLUMN_PERSON_EMAIL, model.getEmail());
        values.put(PERSONS_COLUMN_PERSON_DEPOSIT, model.getDeposit());
        values.put(PERSONS_COLUMN_PERSON_ADMIN, model.getAdmin());
        db.insert(PERSONS_TABLE_NAME,null,values);


        Cursor cursor = db.query(TRIPS_TABLE_NAME,new String[]{TRIPS_COLUMN_TOTAL_PERSONS},TRIPS_COLUMN_ID+"=?",new String[]{model.getTrip_id()},null,null,null);
        int no_of_persons =0;
        if(cursor.moveToFirst()){
            no_of_persons = cursor.getInt(cursor.getColumnIndex(TRIPS_COLUMN_TOTAL_PERSONS));
        }
        cursor.close();

        ContentValues valuesPersons = new ContentValues();
        valuesPersons.put(TRIPS_COLUMN_TOTAL_PERSONS,(no_of_persons+1));

        db.update(TRIPS_TABLE_NAME,valuesPersons,TRIPS_COLUMN_ID+ "=? ", new String[]{model.getTrip_id()});

        return true;
    }

    private ArrayList<PersonModel> getPersons(String trip_id){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<PersonModel> personsList = new ArrayList<>();

        Cursor cursor = db.query(PERSONS_TABLE_NAME,null,PERSONS_COLUMN_TRIP_ID+"=?",new String[]{trip_id},
                null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            do{
                PersonModel personModel = new PersonModel();
                personModel.setName(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)));
                personModel.setMobile(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_MOBILE)));
                personModel.setEmail(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_EMAIL)));
                personModel.setDeposit(cursor.getDouble(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_DEPOSIT)));
                personModel.setAdmin(cursor.getInt(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_ADMIN)));

                personsList.add(personModel);

            }while(cursor.moveToNext());
            cursor.close();
        }


        return  personsList;
    }

    private void addToTotalAmount(String trip_id, Double amount){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT,amount);
        db.update(TRIPS_TABLE_NAME,values,TRIPS_COLUMN_ID+ "=? ", new String[]{trip_id});
    }

    ArrayList<PersonModel> getAllPersons(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<PersonModel> personsList = new ArrayList<>();

        Cursor cursor = db.query(PERSONS_TABLE_NAME,null,null,null,
                null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            do{
                PersonModel personModel = new PersonModel();
                personModel.setName(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)));
                personModel.setMobile(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_MOBILE)));
                personModel.setEmail(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_EMAIL)));
                personModel.setDeposit(cursor.getDouble(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_DEPOSIT)));
                personModel.setAdmin(cursor.getInt(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_ADMIN)));

                personsList.add(personModel);

            }while(cursor.moveToNext());
            cursor.close();
        }


        return  personsList;
    }

    ArrayList<TripModel> getTripsData() {
        ArrayList<TripModel> trip_array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TRIPS_TABLE_NAME, null );

        if (cursor!=null && cursor.moveToFirst()) {
            do {
                TripModel model = new TripModel();
                model.setTrip_name(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_NAME)));
                model.setTrip_date(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_DATE)));
                model.setTrip_amount(cursor.getDouble(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT)));
                model.setTrip_id(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_ID)));
                model.setTrip_places(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_PLACES)));
                model.setImageUrl(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_IMAGE_URL)));
                model.setTotal_persons(cursor.getInt(cursor.getColumnIndex(TRIPS_COLUMN_TOTAL_PERSONS)));
                model.setTripcurrency(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_CURRENCY)));

                trip_array_list.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trip_array_list;
    }

    ArrayList<TripModel> getTripsDataForTheFirstTime() {
        ArrayList<TripModel> trip_array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TRIPS_TABLE_NAME, null );

        if (cursor!=null && cursor.moveToFirst()) {
            do {
                Log.d("SKSK","onupgradecalled case 1 ");
                TripModel model = new TripModel();
                model.setTrip_name(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_NAME)));
                model.setTrip_date(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_DATE)));
                model.setTrip_amount(cursor.getDouble(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT)));
                model.setTrip_id(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_ID)));
                model.setTrip_places(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_PLACES)));
                model.setImageUrl(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_IMAGE_URL)));
                model.setTotal_persons(cursor.getInt(cursor.getColumnIndex(TRIPS_COLUMN_TOTAL_PERSONS)));
               // model.setTripcurrency(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_CURRENCY)));

                trip_array_list.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trip_array_list;
    }


    TripModel getTripData(String trip_id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TRIPS_TABLE_NAME,null,TRIPS_COLUMN_ID + "=?",new String[]{trip_id},null,null,null);

        TripModel model = new TripModel();
        if (cursor!=null && cursor.moveToFirst()) {
            do {
                model.setTrip_name(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_NAME)));
                model.setTrip_date(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_DATE)));
                model.setTrip_amount(cursor.getDouble(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT)));
                model.setTrip_id(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_ID)));
                model.setTrip_places(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_PLACES)));
                model.setImageUrl(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_IMAGE_URL)));
                model.setTotal_persons(cursor.getInt(cursor.getColumnIndex(TRIPS_COLUMN_TOTAL_PERSONS)));
                model.setTripcurrency(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_CURRENCY)));
            } while (cursor.moveToNext());
        }

        assert cursor != null;
        cursor.close();
        return model;
    }

    public String[] getTripNamesAsStringArray() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TRIPS_TABLE_NAME, null );

        String[] trip_name_array = new String[cursor.getCount()];
        int i=0;

        if (cursor.moveToFirst()) {
            do {
                trip_name_array[i] = cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_NAME));
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trip_name_array;
    }



    boolean addExpenseNew(ExpenseModel expenseModel){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEMS_COLUMN_TRIP_ID,expenseModel.getTripId());
        values.put(ITEMS_COLUMN_ITEM_NAME,expenseModel.getItemName());
        values.put(ITEMS_COLUMN_AMOUNT_TYPE,expenseModel.getAmountType());
        values.put(ITEMS_COLUMN_ITEM_AMOUNT,RoundOff(expenseModel.getAmount()));
        values.put(ITEMS_COLUMN_ITEM_EXP_BY_TYPE,expenseModel.getExpByType());
        values.put(ITEMS_COLUMN_ITEM_EXP_BY,expenseModel.getExpBy());
        values.put(ITEMS_COLUMN_ITEM_CAT,expenseModel.getCategory());
        values.put(ITEMS_COLUMN_ITEM_DATE,expenseModel.getDate());
        values.put(ITEMS_COLUMN_ITEM_DATE_VALUE,expenseModel.getDateValue());
        values.put(ITEMS_COLUMN_ITEM_ID,"ITEM"+ UUID.randomUUID().toString());
        values.put(ITEMS_COLUMN_ITEM_SHARE_BY_TYPE,expenseModel.getShareByType());
        values.put(ITEMS_COLUMN_ITEM_SHARE_BY,expenseModel.getShareBy());
        values.put(ITEMS_COLUMN_ITEM_CURRENCY,expenseModel.getCurrency());
        values.put(ITEMS_COLUMN_ITEM_CURRENCY_CONVERSION_RATE,expenseModel.getCurrencyConversionRate());

        db.insert(ITEMS_TABLE_NAME,null,values);

        addToTotalAmount(expenseModel.getTripId(),getTotalExpensesAmount(expenseModel.getTripId()));

        return true;
    }

    boolean editExpenseNew(ExpenseModel expenseModel){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEMS_COLUMN_TRIP_ID,expenseModel.getTripId());
        values.put(ITEMS_COLUMN_ITEM_NAME,expenseModel.getItemName());
        values.put(ITEMS_COLUMN_AMOUNT_TYPE,expenseModel.getAmountType());
        values.put(ITEMS_COLUMN_ITEM_AMOUNT,RoundOff(expenseModel.getAmount()));
        values.put(ITEMS_COLUMN_ITEM_EXP_BY_TYPE,expenseModel.getExpByType());
        values.put(ITEMS_COLUMN_ITEM_EXP_BY,expenseModel.getExpBy());
        values.put(ITEMS_COLUMN_ITEM_CAT,expenseModel.getCategory());
        values.put(ITEMS_COLUMN_ITEM_DATE,expenseModel.getDate());
        values.put(ITEMS_COLUMN_ITEM_DATE_VALUE,expenseModel.getDateValue());
        values.put(ITEMS_COLUMN_ITEM_ID,expenseModel.getItemId());
        values.put(ITEMS_COLUMN_ITEM_SHARE_BY_TYPE,expenseModel.getShareByType());
        values.put(ITEMS_COLUMN_ITEM_SHARE_BY,expenseModel.getShareBy());
        values.put(ITEMS_COLUMN_ITEM_CURRENCY,expenseModel.getCurrency());
        values.put(ITEMS_COLUMN_ITEM_CURRENCY_CONVERSION_RATE,expenseModel.getCurrencyConversionRate());

        db.update(ITEMS_TABLE_NAME,values,ITEMS_COLUMN_TRIP_ID + " = \"" + expenseModel.getTripId()+"\" AND "+ITEMS_COLUMN_ITEM_ID+" = \""+expenseModel.getItemId()+"\"", null);
        addToTotalAmount(expenseModel.getTripId(),getTotalExpensesAmount(expenseModel.getTripId()));

        return true;
    }




    boolean editExpense(String trip_id, String description, String category, String date, String expShareByPersonsSelected, int amount_type, ArrayList<AddExpenseByPersonModel> expenseByPersonList, Double fromDepositExpense, Long date_value, String item_id){
        SQLiteDatabase db = getWritableDatabase();

        if(amount_type == 1){

            ContentValues values = new ContentValues();
            values.put(ITEMS_COLUMN_ITEM_NAME,description);
            values.put(ITEMS_COLUMN_AMOUNT_TYPE,amount_type);
            values.put(ITEMS_COLUMN_ITEM_AMOUNT,RoundOff(fromDepositExpense));
            values.put(ITEMS_COLUMN_ITEM_CAT,category);
            values.put(ITEMS_COLUMN_ITEM_EXP_BY,"Deposit Money");
            values.put(ITEMS_COLUMN_ITEM_SHARE_BY,expShareByPersonsSelected);
            values.put(ITEMS_COLUMN_ITEM_DATE,date);
            values.put(ITEMS_COLUMN_ITEM_DATE_VALUE,date_value);

            db.update(ITEMS_TABLE_NAME,values,ITEMS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+ITEMS_COLUMN_ITEM_ID+" = \""+item_id+"\"", null);
            addToTotalAmount(trip_id,getTotalExpensesAmount(trip_id));
        }
        else if(amount_type == 2){

            ContentValues values = new ContentValues();
            values.put(ITEMS_COLUMN_ITEM_NAME,description);
            values.put(ITEMS_COLUMN_AMOUNT_TYPE,amount_type);
            values.put(ITEMS_COLUMN_ITEM_CAT,category);
            values.put(ITEMS_COLUMN_ITEM_SHARE_BY,expShareByPersonsSelected);
            values.put(ITEMS_COLUMN_ITEM_DATE,date);
            values.put(ITEMS_COLUMN_ITEM_DATE_VALUE,date_value);

            values.put(ITEMS_COLUMN_ITEM_AMOUNT,RoundOff(expenseByPersonList.get(0).getAmount()));
            values.put(ITEMS_COLUMN_ITEM_EXP_BY,expenseByPersonList.get(0).getName());

            db.update(ITEMS_TABLE_NAME,values,ITEMS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+ITEMS_COLUMN_ITEM_ID+" = \""+item_id+"\"", null);
            addToTotalAmount(trip_id,getTotalExpensesAmount(trip_id));
        }

        return true;
    }

    boolean addExpense(String trip_id, String description, String category, String date, String expShareByPersonsSelected, int amount_type, ArrayList<AddExpenseByPersonModel> expenseByPersonList, Double fromDepositExpense, Long date_value){
        SQLiteDatabase db = getWritableDatabase();

        if(amount_type == 1){

            ContentValues values = new ContentValues();
            values.put(ITEMS_COLUMN_TRIP_ID,trip_id);
            values.put(ITEMS_COLUMN_ITEM_NAME,description);
            values.put(ITEMS_COLUMN_AMOUNT_TYPE,amount_type);
            values.put(ITEMS_COLUMN_ITEM_AMOUNT,RoundOff(fromDepositExpense));
            values.put(ITEMS_COLUMN_ITEM_CAT,category);
            values.put(ITEMS_COLUMN_ITEM_EXP_BY,"Deposit Money");
           // values.put(ITEMS_COLUMN_ITEM_SHARE_BY_TYPE,amount_share_by_type);
            values.put(ITEMS_COLUMN_ITEM_SHARE_BY,expShareByPersonsSelected);
            values.put(ITEMS_COLUMN_ITEM_DATE,date);
            values.put(ITEMS_COLUMN_ITEM_DATE_VALUE,date_value);
            values.put(ITEMS_COLUMN_ITEM_ID,"ITEM"+ UUID.randomUUID().toString());

            db.insert(ITEMS_TABLE_NAME,null,values);

            addToTotalAmount(trip_id,getTotalExpensesAmount(trip_id));

        }
        else if(amount_type == 2){

            for(int i=0;i<expenseByPersonList.size();i++){
                ContentValues values = new ContentValues();
                values.put(ITEMS_COLUMN_TRIP_ID,trip_id);
                values.put(ITEMS_COLUMN_ITEM_NAME,description);
                values.put(ITEMS_COLUMN_AMOUNT_TYPE,amount_type);
                values.put(ITEMS_COLUMN_ITEM_CAT,category);
                //values.put(ITEMS_COLUMN_ITEM_SHARE_BY_TYPE,amount_share_by_type);
                values.put(ITEMS_COLUMN_ITEM_SHARE_BY,expShareByPersonsSelected);
                values.put(ITEMS_COLUMN_ITEM_DATE,date);
                values.put(ITEMS_COLUMN_ITEM_DATE_VALUE,date_value);
                values.put(ITEMS_COLUMN_ITEM_ID,"ITEM"+ UUID.randomUUID().toString());

                values.put(ITEMS_COLUMN_ITEM_AMOUNT,RoundOff(expenseByPersonList.get(i).getAmount()));
                values.put(ITEMS_COLUMN_ITEM_EXP_BY,expenseByPersonList.get(i).getName());

                db.insert(ITEMS_TABLE_NAME,null,values);
                addToTotalAmount(trip_id,getTotalExpensesAmount(trip_id));
            }

        }

        return true;
    }

    private ArrayList<ExpenseModel> getAllExpenses(String trip_id){
        ArrayList<ExpenseModel> expenseModelArrayList = new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();

        Cursor cursor = db.query(ITEMS_TABLE_NAME,null,ITEMS_COLUMN_TRIP_ID + "=?",new String[]{trip_id},null,null,null);

        ArrayList<String> categoriesList = new ArrayList<>();
        categoriesList.addAll(Arrays.asList(getCategories()));

        if(cursor!=null && cursor.moveToFirst()){
            do {

                ExpenseModel expenseModel = new ExpenseModel();
                expenseModel.setTripId(trip_id);
                expenseModel.setItemName(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_NAME)));
                expenseModel.setAmountType(cursor.getInt(cursor.getColumnIndex(ITEMS_COLUMN_AMOUNT_TYPE)));
                expenseModel.setAmount(cursor.getDouble(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_AMOUNT)));
                expenseModel.setExpByType(cursor.getInt(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_EXP_BY_TYPE)));
                expenseModel.setExpBy(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_EXP_BY)));

                String currCat = cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_CAT));
                if(categoriesList.contains(currCat)) expenseModel.setCategory(currCat);
                else expenseModel.setCategory("Miscellaneous");

                expenseModel.setDate(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_DATE)));
                expenseModel.setDateValue(cursor.getLong(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_DATE_VALUE)));
                expenseModel.setShareByType(cursor.getInt(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_SHARE_BY_TYPE)));
                expenseModel.setShareBy(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_SHARE_BY)));
                expenseModel.setItemId(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_ID)));
                expenseModel.setCurrency(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_CURRENCY)));
                expenseModel.setCurrencyConversionRate(cursor.getDouble(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_CURRENCY_CONVERSION_RATE)));

                expenseModelArrayList.add(expenseModel);

            }while(cursor.moveToNext());

            cursor.close();
        }



        return expenseModelArrayList;

    }

    private ArrayList<PersonWiseExpensesSummaryModel> getPersonWiseExpensesSummaryNew(String trip_id){
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<PersonWiseExpensesSummaryModel> result = new ArrayList<>();

        HashMap<String,Double> depositAmountShareByPerson = new HashMap<>();
        HashMap<String,Double> personalAmountShareByPerson = new HashMap<>();
        HashMap<String,Double> personalAmountGiven = new HashMap<>();

        Cursor cursor = db.query(PERSONS_TABLE_NAME,null,PERSONS_COLUMN_TRIP_ID+"=?",new String[]{trip_id},null,null,null);

        ArrayList<String> personsListAsString = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                PersonWiseExpensesSummaryModel model = new PersonWiseExpensesSummaryModel();
                model.setName(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)));
                model.setMobile(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_MOBILE)));
                model.setEmail(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_EMAIL)));
                model.setDepositAmountGiven(cursor.getDouble(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_DEPOSIT)));
                model.setAdmin(cursor.getInt(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_ADMIN)));

                depositAmountShareByPerson.put(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)).trim(),0.0);
                personalAmountShareByPerson.put(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)).trim(),0.0);
                personalAmountGiven.put(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)).trim(),0.0);
                personsListAsString.add(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)).trim());

                result.add(model);

            }while(cursor.moveToNext());
        }
        cursor.close();

        String tempPersons ="";
        for(String personName:personsListAsString){
            tempPersons = tempPersons + Utils.DELIMITER_FOR_EXP_PERSONS + personName;
        }

        int no_of_persons = result.size();

        ArrayList<ExpenseModel> allExpensesList = getAllExpenses(trip_id);

        Double totalDepositShare = 0.0,totalPersonalShare = 0.0;

        for(ExpenseModel expenseModel:allExpensesList){

            Double conversionRate = expenseModel.getCurrencyConversionRate();

            if(expenseModel.getAmountType() == 1){

                if(expenseModel.getShareByType() == 1){
                    if(expenseModel.getShareBy().equalsIgnoreCase(tempPersons)){
                        totalDepositShare += RoundOff((expenseModel.getAmount()*conversionRate)/no_of_persons);
                    }else{
                        String persons[] = expenseModel.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                        int no_of_share_by_deposit = persons.length;
                        Double sharedDepositAmount = RoundOff((expenseModel.getAmount()*conversionRate)/no_of_share_by_deposit);

                        for (String person : persons) {
                            Double itemToBeAdded = depositAmountShareByPerson.get(person.trim()) + sharedDepositAmount;
                            depositAmountShareByPerson.put(person.trim(), itemToBeAdded);
                        }
                    }

                }
                else if(expenseModel.getShareByType() == 2){
                    String personsAndAmount[] = expenseModel.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                    for (String aPersonsAndAmount : personsAndAmount) {
                        String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                        Double amountToBeAdded = depositAmountShareByPerson.get(eachPersonAndAmount[0]) + RoundOff(conversionRate*Double.parseDouble(eachPersonAndAmount[1]));
                        depositAmountShareByPerson.put(eachPersonAndAmount[0], amountToBeAdded);
                    }
                }
                else if(expenseModel.getShareByType() == 3){
                    String personsAndShares[] = expenseModel.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                    int totalShares = 0;
                    for (String aPersonsAndShares : personsAndShares) {
                        String eachPersonAndShares[] = aPersonsAndShares.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                        totalShares += Integer.parseInt(eachPersonAndShares[1]);
                    }

                    for (String aPersonsAndShares : personsAndShares) {
                        String eachPersonAndShares[] = aPersonsAndShares.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                        Double amountToBeAdded = depositAmountShareByPerson.get(eachPersonAndShares[0]) +
                                RoundOff(Integer.parseInt(eachPersonAndShares[1])*(expenseModel.getAmount()*conversionRate)/totalShares);
                        depositAmountShareByPerson.put(eachPersonAndShares[0], amountToBeAdded);
                    }

                }

            }else{

                if(expenseModel.getExpByType() == 1){
                    Double itemToBeAddedPersonal = personalAmountGiven.get(expenseModel.getExpBy()) + RoundOff(expenseModel.getAmount()*conversionRate);
                    personalAmountGiven.put(expenseModel.getExpBy(),itemToBeAddedPersonal);
                }else if(expenseModel.getExpByType() == 2){
                    String personsAndAmount[] = expenseModel.getExpBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                    for (String aPersonsAndAmount : personsAndAmount) {
                        String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                        Double amountToBeAdded = personalAmountGiven.get(eachPersonAndAmount[0]) + RoundOff(conversionRate*Double.parseDouble(eachPersonAndAmount[1]));
                        personalAmountGiven.put(eachPersonAndAmount[0], amountToBeAdded);
                    }
                }

                if(expenseModel.getShareByType() == 1){
                    if(expenseModel.getShareBy().equalsIgnoreCase(tempPersons)){
                        totalPersonalShare += RoundOff((expenseModel.getAmount()*conversionRate)/no_of_persons);
                    }else{
                        String persons[] = expenseModel.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                        int no_of_share_by_personal = persons.length;
                        Double sharedPersonalAmount = RoundOff((expenseModel.getAmount()*conversionRate)/no_of_share_by_personal);

                        for (String person : persons) {
                            Double itemToBeAdded = personalAmountShareByPerson.get(person.trim()) + sharedPersonalAmount;
                            personalAmountShareByPerson.put(person.trim(), itemToBeAdded);
                        }
                    }

                }
                else if(expenseModel.getShareByType() == 2){
                    String personsAndAmount[] = expenseModel.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                    for (String aPersonsAndAmount : personsAndAmount) {
                        String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                        Double amountToBeAdded = personalAmountShareByPerson.get(eachPersonAndAmount[0]) + RoundOff(conversionRate*Double.parseDouble(eachPersonAndAmount[1]));
                        personalAmountShareByPerson.put(eachPersonAndAmount[0], amountToBeAdded);
                    }
                }
                else if(expenseModel.getShareByType() == 3){
                    String personsAndShares[] = expenseModel.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                    int totalShares = 0;
                    for (String aPersonsAndShares : personsAndShares) {
                        String eachPersonAndShares[] = aPersonsAndShares.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                        totalShares += Integer.parseInt(eachPersonAndShares[1]);
                    }
                    Double amountPerShare = RoundOff((expenseModel.getAmount()*conversionRate)/totalShares);
                    for (String aPersonsAndShares : personsAndShares) {
                        String eachPersonAndShares[] = aPersonsAndShares.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                        Double amountToBeAdded = personalAmountShareByPerson.get(eachPersonAndShares[0]) + RoundOff(Integer.parseInt(eachPersonAndShares[1])*amountPerShare);
                        personalAmountShareByPerson.put(eachPersonAndShares[0], amountToBeAdded);
                    }

                }

            }
        }

        for(int i=0;i<result.size();i++){

            result.get(i).setDepositAmountSpent(depositAmountShareByPerson.get(result.get(i).getName()));
            result.get(i).setPersonalAmountSpent(personalAmountShareByPerson.get(result.get(i).getName()));
            result.get(i).setPersonalAmountGiven(personalAmountGiven.get(result.get(i).getName()));

            result.get(i).setDepositAmountSpent(result.get(i).getDepositAmountSpent()+totalDepositShare);
            result.get(i).setDepositAmountRemaining(result.get(i).getDepositAmountGiven()-result.get(i).getDepositAmountSpent());

            result.get(i).setPersonalAmountSpent(result.get(i).getPersonalAmountSpent()+totalPersonalShare);
            result.get(i).setPersonalAmountRemaining(result.get(i).getPersonalAmountGiven()-result.get(i).getPersonalAmountSpent());

            result.get(i).setTotalAmountGiven(result.get(i).getDepositAmountGiven()+result.get(i).getPersonalAmountGiven());
            result.get(i).setTotalAmountSpent(result.get(i).getDepositAmountSpent() + result.get(i).getPersonalAmountSpent());
            result.get(i).setTotalAmountRemaining(result.get(i).getDepositAmountRemaining()+result.get(i).getPersonalAmountRemaining());


            result.get(i).setDepositAmountGiven(RoundOff(result.get(i).getDepositAmountGiven()));
            result.get(i).setDepositAmountSpent(RoundOff(result.get(i).getDepositAmountSpent()));
            result.get(i).setDepositAmountRemaining(RoundOff(result.get(i).getDepositAmountRemaining()));
            result.get(i).setPersonalAmountGiven(RoundOff(result.get(i).getPersonalAmountGiven()));
            result.get(i).setPersonalAmountSpent(RoundOff(result.get(i).getPersonalAmountSpent()));
            result.get(i).setPersonalAmountRemaining(RoundOff(result.get(i).getPersonalAmountRemaining()));
            result.get(i).setTotalAmountGiven(RoundOff(result.get(i).getTotalAmountGiven()));
            result.get(i).setTotalAmountSpent(RoundOff(result.get(i).getTotalAmountSpent()));
            result.get(i).setTotalAmountRemaining(RoundOff(result.get(i).getTotalAmountRemaining()));

        }

        return  result;
    }



    ArrayList<PersonWiseExpensesSummaryModel> getPersonWiseExpensesSummaryForDashboard(String trip_id){

        ArrayList<PersonWiseExpensesSummaryModel> result = getPersonWiseExpensesSummaryNew(trip_id);

        Collections.sort(result, new Comparator<PersonWiseExpensesSummaryModel>() {
            @Override
            public int compare(PersonWiseExpensesSummaryModel o1, PersonWiseExpensesSummaryModel o2) {
                return o2.getTotalAmountRemaining().compareTo(o1.getTotalAmountRemaining());

            }
        });

        return  result;
    }

    ArrayList<PersonWiseExpensesSummaryModel> getPersonWiseExpensesSummaryForPersonsFragment(String trip_id){

        ArrayList<PersonWiseExpensesSummaryModel> result = getPersonWiseExpensesSummaryNew(trip_id);


        //checking whether the person can be removed or not
        //if the person's money were found spent anywhere in the trip, he cannot be removed(To remove him users has to edit the expenses so that this person's money will not be accounted for spending anywhere in the trip
        //admin cannot be removed from the trip(To delete admin, users first should remove him as admin and then delete)

        for(int i=0;i<result.size();i++){

            PersonWiseExpensesSummaryModel model = result.get(i);

            if(model.getAdmin() == 1 || model.getDepositAmountSpent() != 0.0 || model.getPersonalAmountGiven() != 0.0 || model.getPersonalAmountSpent() != 0.0){
                model.setCanRemove(false);
            }
            else if(getDepositMoneyRemaining(trip_id) < model.getDepositAmountGiven()){
                model.setCanRemove(false);
            }
            else if(model.getDepositAmountGiven() > getDepositMoneyRemaining(trip_id)){
                model.setCanRemove(false);
            }
            else{
                model.setCanRemove(true);
            }

            result.remove(i);
            result.add(i,model);
        }


        Collections.sort(result, new Comparator<PersonWiseExpensesSummaryModel>() {
            @Override
            public int compare(PersonWiseExpensesSummaryModel o1, PersonWiseExpensesSummaryModel o2) {
                return o2.getTotalAmountGiven().compareTo(o1.getTotalAmountGiven());

            }
        });

        return  result;
    }

    ArrayList<GraphItemModel> getPersonWiseExpensesSummaryForGraphPersons(String trip_id){

        ArrayList<PersonWiseExpensesSummaryModel> list = getPersonWiseExpensesSummaryNew(trip_id);
        ArrayList<GraphItemModel> result = new ArrayList<>();

        Double totalexpense = 0.0;
        for(int i=0;i<list.size();i++){
            totalexpense += list.get(i).getTotalAmountSpent();
        }

        for(int i=0;i<list.size();i++){
            GraphItemModel model = new GraphItemModel();

            model.setName(list.get(i).getName());
            model.setAmount(list.get(i).getTotalAmountSpent());
            if(totalexpense!=0.0){
                Double percent = RoundOff((model.getAmount()/totalexpense)*100);
                model.setPercentage(percent);
            }else{
                model.setPercentage(0.0);
            }

            result.add(model);
        }

        Collections.sort(result, new Comparator<GraphItemModel>() {
            @Override
            public int compare(GraphItemModel o1, GraphItemModel o2) {
                return o2.getPercentage().compareTo(o1.getPercentage());

            }
        });

        return  result;
    }

    ArrayList<GraphItemModel> getPersonWiseExpensesSummaryForGraphCategory(String trip_id){

        ArrayList<GraphItemModel> result = new ArrayList<>();
        ArrayList<ExpenseModel> list = getAllExpenses(trip_id);



        HashMap<String,Double> categoryWiseExpenses = new HashMap<>();
        ArrayList<String> allCategoriesList =new ArrayList<>();
        allCategoriesList.addAll(Arrays.asList(getCategories()));

        Double total_expenses= 0.0;
        for(int i=0;i<list.size();i++){
            ExpenseModel model = list.get(i);
            total_expenses += RoundOff(model.getAmount()*model.getCurrencyConversionRate());

            String categoryToBeAdded = "";
            if(allCategoriesList.contains(model.getCategory())){
                categoryToBeAdded = model.getCategory();
            }else{
                categoryToBeAdded = "Miscellaneous";
            }

            if(categoryWiseExpenses.containsKey(categoryToBeAdded)){
                Double toBeInserted = categoryWiseExpenses.get(categoryToBeAdded) + RoundOff(model.getAmount()*model.getCurrencyConversionRate());
                categoryWiseExpenses.put(categoryToBeAdded,toBeInserted);
            }else{
                categoryWiseExpenses.put(categoryToBeAdded,RoundOff(model.getAmount()*model.getCurrencyConversionRate()));
            }

        }

        Set<String> finalCategories = categoryWiseExpenses.keySet();
        Iterator it = finalCategories.iterator();

        for(int i=0;i<categoryWiseExpenses.size();i++){
            GraphItemModel model = new GraphItemModel();
            String catname = (String) it.next();
            model.setName(catname);
            model.setAmount(categoryWiseExpenses.get(catname));
            Double percent = RoundOff((categoryWiseExpenses.get(catname)/total_expenses)*100);
            model.setPercentage(percent);
            result.add(model);
        }

        Collections.sort(result, new Comparator<GraphItemModel>() {
            @Override
            public int compare(GraphItemModel o1, GraphItemModel o2) {
                return o2.getPercentage().compareTo(o1.getPercentage());

            }
        });

        return  result;
    }

    ArrayList<GraphItemModel> getPersonWiseExpensesSummaryForGraphDate(String trip_id){

        ArrayList<GraphItemModel> result = new ArrayList<>();
        ArrayList<ExpenseModel> list = getAllExpenses(trip_id);

        HashMap<String,Double> dateWiseExpenses = new HashMap<>();
        Double total_expenses= 0.0;
        for(int i=0;i<list.size();i++){
            ExpenseModel model = list.get(i);
            total_expenses += RoundOff(model.getAmount()*model.getCurrencyConversionRate());
            if(dateWiseExpenses.containsKey(model.getDate())){
                Double toBeInserted = dateWiseExpenses.get(model.getDate()) + RoundOff(model.getAmount()*model.getCurrencyConversionRate());
                dateWiseExpenses.put(model.getDate(),toBeInserted);
            }else{
                dateWiseExpenses.put(model.getDate(),RoundOff(model.getAmount()*model.getCurrencyConversionRate()));
            }
        }

        Set<String> finalDates = dateWiseExpenses.keySet();
        Iterator it = finalDates.iterator();

        for(int i=0;i<dateWiseExpenses.size();i++){
            GraphItemModel model = new GraphItemModel();
            String dateStr = (String) it.next();
            model.setName(dateStr);
            model.setAmount(dateWiseExpenses.get(dateStr));
            Double percent = RoundOff((dateWiseExpenses.get(dateStr)/total_expenses)*100);
            model.setPercentage(percent);
            result.add(model);
        }

        Collections.sort(result, new Comparator<GraphItemModel>() {
            @Override
            public int compare(GraphItemModel o1, GraphItemModel o2) {
                return o2.getPercentage().compareTo(o1.getPercentage());

            }
        });

        return  result;
    }

    Double getTotalExpensesAmount(String trip_id){

        ArrayList<ExpenseModel> list = getAllExpenses(trip_id);

        Double total_expenses= 0.0;
        for(int i=0;i<list.size();i++){
            ExpenseModel model = list.get(i);
            total_expenses += RoundOff(model.getAmount()*model.getCurrencyConversionRate());
        }

        return  total_expenses;
    }

    ArrayList<ParentExpenseItemModel> getAllExpensesToDisplay(String trip_id){
        ArrayList<ParentExpenseItemModel> result = new ArrayList<>();

        ArrayList<ExpenseModel> allExpList = getAllExpenses(trip_id);

        HashMap<String, ParentExpenseItemModel> hashMapExpense = new HashMap<>();
        hashMapExpense.put("Deposit Money",new ParentExpenseItemModel());
        hashMapExpense.put("Personal Money",new ParentExpenseItemModel());

        Double total_amount = 0.0;

        for(int i=0;i<allExpList.size();i++){
            ExpenseModel model = allExpList.get(i);
            total_amount += RoundOff(model.getAmount()*model.getCurrencyConversionRate());
            if(model.getAmountType() == 1){
                ParentExpenseItemModel tempModel = hashMapExpense.get("Deposit Money");
                Double tempAmountToAdd = tempModel.getAmount()+ RoundOff(model.getAmount()*model.getCurrencyConversionRate());
                tempModel.setAmount(RoundOff(tempAmountToAdd));

                ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getChildList();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put("Deposit Money",tempModel);

            }else{
                ParentExpenseItemModel tempModel = hashMapExpense.get("Personal Money");
                Double tempAmountToAdd = tempModel.getAmount()+ RoundOff(model.getAmount()*model.getCurrencyConversionRate());
                tempModel.setAmount(RoundOff(tempAmountToAdd));

                ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getChildList();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put("Personal Money",tempModel);
            }
        }

        Set<String> keys = hashMapExpense.keySet();
        Iterator<String> itr = keys.iterator();
        while(itr.hasNext()){
            String tempName = itr.next();

            ParentExpenseItemModel finalModel = hashMapExpense.get(tempName);
            finalModel.setName(tempName);
            if(total_amount!=0){
                Double percentage = RoundOff((finalModel.getAmount()/total_amount)*100);
                finalModel.setPercentage(percentage);
            }else{
                finalModel.setPercentage(0.0);
            }

            if(finalModel.getExpenseList().size() != 0){

                ArrayList<ExpenseModel> forSortingModel = finalModel.getExpenseList();

                Collections.sort(forSortingModel, new Comparator<ExpenseModel>() {
                    @Override
                    public int compare(ExpenseModel o1, ExpenseModel o2) {
                        if(o2.getDateValue()>o1.getDateValue()){
                            return 1;
                        }else if(o2.getDateValue()<o1.getDateValue()){
                            return -1;
                        }else{
                            return 0;
                        }
                    }
                });

                finalModel.setExpenseList(forSortingModel);

                result.add(finalModel);
            }

        }


        Collections.sort(result, new Comparator<ParentExpenseItemModel>() {
            @Override
            public int compare(ParentExpenseItemModel o1, ParentExpenseItemModel o2) {
                return o2.getPercentage().compareTo(o1.getPercentage());
            }
        });

        return result;
    }

    ArrayList<ParentExpenseItemModel> getExpensesDateWiseToDisplay(String trip_id){
        ArrayList<ParentExpenseItemModel> result = new ArrayList<>();

        ArrayList<ExpenseModel> allExpList = getAllExpenses(trip_id);

        HashMap<String, ParentExpenseItemModel> hashMapExpense = new HashMap<>();

        Double total_amount = 0.0;

        for(int i=0;i<allExpList.size();i++){
            ExpenseModel model = allExpList.get(i);

            total_amount += RoundOff(model.getAmount()*model.getCurrencyConversionRate());

            if(hashMapExpense.containsKey(model.getDate())){

                ParentExpenseItemModel tempModel = hashMapExpense.get(model.getDate());
                tempModel.setAmount(RoundOff(tempModel.getAmount()+RoundOff(model.getAmount()*model.getCurrencyConversionRate())));

                ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put(model.getDate(),tempModel);

            }else{

                ParentExpenseItemModel tempModel = new ParentExpenseItemModel();
                tempModel.setAmount(RoundOff(model.getAmount()*model.getCurrencyConversionRate()));

                ArrayList<ExpenseModel> tempExpOnlyList = new ArrayList<>();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put(model.getDate(),tempModel);
            }
        }

        Set<String> keys = hashMapExpense.keySet();
        Iterator<String> itr = keys.iterator();
        while(itr.hasNext()){
            String tempName = itr.next();

            ParentExpenseItemModel finalModel = hashMapExpense.get(tempName);
            finalModel.setName(tempName);
            if(total_amount!=0){
                Double percentage = RoundOff((finalModel.getAmount()/total_amount)*100);
                finalModel.setPercentage(percentage);
            }else{
                finalModel.setPercentage(0.0);
            }

            if(finalModel.getExpenseList().size() != 0){

                ArrayList<ExpenseModel> forSortingModel = finalModel.getExpenseList();

                Collections.sort(forSortingModel, new Comparator<ExpenseModel>() {
                    @Override
                    public int compare(ExpenseModel o1, ExpenseModel o2) {
                        if(o2.getDateValue()>o1.getDateValue()){
                            return 1;
                        }else if(o2.getDateValue()<o1.getDateValue()){
                            return -1;
                        }else{
                            return 0;
                        }
                    }
                });

                finalModel.setExpenseList(forSortingModel);
                result.add(finalModel);
            }

        }

        Collections.sort(result, new Comparator<ParentExpenseItemModel>() {
            @Override
            public int compare(ParentExpenseItemModel o1, ParentExpenseItemModel o2) {

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date date1 = null,date2 = null;
                try {
                    date1 = format.parse(o1.getName());
                    date2 = format.parse(o2.getName());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return date2.compareTo(date1);
            }
        });

        return result;
    }

    ArrayList<ParentExpenseItemModel> getExpensesCategoryWiseToDisplay(String trip_id) {
        ArrayList<ParentExpenseItemModel> result = new ArrayList<>();

        ArrayList<ExpenseModel> allExpList = getAllExpenses(trip_id);

        HashMap<String, ParentExpenseItemModel> hashMapExpense = new HashMap<>();
        ArrayList<String> allCategoriesList = new ArrayList<>();
        allCategoriesList.addAll(Arrays.asList(getCategories()));

        Double total_amount = 0.0;

        for(int i=0;i<allExpList.size();i++){
            ExpenseModel model = allExpList.get(i);

            total_amount += RoundOff(model.getAmount()*model.getCurrencyConversionRate());
            String categoryToBeAdded = "";

            if(allCategoriesList.contains(model.getCategory())){
                categoryToBeAdded = model.getCategory();
            }else{
                categoryToBeAdded = "Miscellaneous";
            }

            if(hashMapExpense.containsKey(categoryToBeAdded)){

                ParentExpenseItemModel tempModel = hashMapExpense.get(categoryToBeAdded);
                tempModel.setAmount(RoundOff(tempModel.getAmount()+RoundOff(model.getAmount()*model.getCurrencyConversionRate())));

                ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put(categoryToBeAdded,tempModel);

            }else{

                ParentExpenseItemModel tempModel = new ParentExpenseItemModel();
                tempModel.setAmount(RoundOff(model.getAmount()*model.getCurrencyConversionRate()));

                ArrayList<ExpenseModel> tempExpOnlyList = new ArrayList<>();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put(categoryToBeAdded,tempModel);
            }
        }

        Set<String> keys = hashMapExpense.keySet();
        Iterator<String> itr = keys.iterator();
        while(itr.hasNext()){
            String tempName = itr.next();

            ParentExpenseItemModel finalModel = hashMapExpense.get(tempName);
            finalModel.setName(tempName);
            if(total_amount!=0){
                Double percentage = RoundOff((finalModel.getAmount()/total_amount)*100);
                finalModel.setPercentage(percentage);
            }else{
                finalModel.setPercentage(0.0);
            }

            if(finalModel.getExpenseList().size() != 0){

                ArrayList<ExpenseModel> forSortingModel = finalModel.getExpenseList();

               Collections.sort(forSortingModel, new Comparator<ExpenseModel>() {
                   @Override
                   public int compare(ExpenseModel o1, ExpenseModel o2) {
                       if(o2.getDateValue()>o1.getDateValue()){
                           return 1;
                       }else if(o2.getDateValue()<o1.getDateValue()){
                           return -1;
                       }else{
                           return 0;
                       }
                   }
               });

                finalModel.setExpenseList(forSortingModel);


                result.add(finalModel);
            }

        }


        Collections.sort(result, new Comparator<ParentExpenseItemModel>() {
            @Override
            public int compare(ParentExpenseItemModel o1, ParentExpenseItemModel o2) {
                return o2.getPercentage().compareTo(o1.getPercentage());
            }
        });



        return result;
    }

    ArrayList<ParentExpenseItemModel> getExpensesByPersonWiseToDisplay(String trip_id){
        ArrayList<ParentExpenseItemModel> result = new ArrayList<>();

        ArrayList<ExpenseModel> allExpList = getAllExpenses(trip_id);

        HashMap<String, ParentExpenseItemModel> hashMapExpense = new HashMap<>();

        Double total_amount = 0.0;

        for(int i=0;i<allExpList.size();i++){
            ExpenseModel model = allExpList.get(i);
            if(model.getAmountType() != 1){
                total_amount += RoundOff(model.getAmount()*model.getCurrencyConversionRate());

                if(model.getExpByType() == 1){

                    if(hashMapExpense.containsKey(model.getExpBy())){

                        ParentExpenseItemModel tempModel = hashMapExpense.get(model.getExpBy());
                        tempModel.setAmount(RoundOff(tempModel.getAmount()+RoundOff(model.getAmount()*model.getCurrencyConversionRate())));

                        ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                        tempExpOnlyList.add(model);
                        tempModel.setExpenseList(tempExpOnlyList);

                        hashMapExpense.put(model.getExpBy(),tempModel);

                    }else{

                        ParentExpenseItemModel tempModel = new ParentExpenseItemModel();
                        tempModel.setAmount(RoundOff(model.getAmount()*model.getCurrencyConversionRate()));

                        ArrayList<ExpenseModel> tempExpOnlyList = new ArrayList<>();
                        tempExpOnlyList.add(model);
                        tempModel.setExpenseList(tempExpOnlyList);

                        hashMapExpense.put(model.getExpBy(),tempModel);
                    }

                }else if(model.getExpByType() == 2){


                    String personsAndAmount[] = model.getExpBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                    for (String aPersonsAndAmount : personsAndAmount) {
                        String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));

                        ExpenseModel tempModel = new ExpenseModel(model);
                        tempModel.setPersonNameForGeneration(eachPersonAndAmount[0]);
                        tempModel.setAmountByPersonForGeneration(RoundOff(model.getCurrencyConversionRate()*Double.parseDouble(eachPersonAndAmount[1])));



                        if(hashMapExpense.containsKey(tempModel.getPersonNameForGeneration())){

                            ParentExpenseItemModel tempModel1 = hashMapExpense.get(tempModel.getPersonNameForGeneration());
                            tempModel1.setAmount(RoundOff(tempModel1.getAmount()+tempModel.getAmountByPersonForGeneration()));

                            ArrayList<ExpenseModel> tempExpOnlyList = tempModel1.getExpenseList();
                            tempExpOnlyList.add(tempModel);
                            tempModel1.setExpenseList(tempExpOnlyList);

                            hashMapExpense.put(tempModel.getPersonNameForGeneration(),tempModel1);

                        }else{

                            ParentExpenseItemModel tempModel1 = new ParentExpenseItemModel();
                            tempModel1.setAmount(tempModel.getAmountByPersonForGeneration());

                            ArrayList<ExpenseModel> tempExpOnlyList = new ArrayList<>();
                            tempExpOnlyList.add(tempModel);
                            tempModel1.setExpenseList(tempExpOnlyList);

                            hashMapExpense.put(tempModel.getPersonNameForGeneration(),tempModel1);
                        }

                    }

                }

            }
        }

        Set<String> keys = hashMapExpense.keySet();
        Iterator<String> itr = keys.iterator();
        while(itr.hasNext()){
            String tempName = itr.next();

            ParentExpenseItemModel finalModel = hashMapExpense.get(tempName);
            finalModel.setName(tempName);
            if(total_amount!=0){
                Double percentage = RoundOff((finalModel.getAmount()/total_amount)*100);
                finalModel.setPercentage(percentage);
            }else{
                finalModel.setPercentage(0.0);
            }

            if(finalModel.getExpenseList().size() != 0){

                ArrayList<ExpenseModel> forSortingModel = finalModel.getExpenseList();

                Collections.sort(forSortingModel, new Comparator<ExpenseModel>() {
                    @Override
                    public int compare(ExpenseModel o1, ExpenseModel o2) {
                        if(o2.getDateValue()>o1.getDateValue()){
                            return 1;
                        }else if(o2.getDateValue()<o1.getDateValue()){
                            return -1;
                        }else{
                            return 0;
                        }
                    }
                });

                finalModel.setExpenseList(forSortingModel);

                result.add(finalModel);
            }

        }


        Collections.sort(result, new Comparator<ParentExpenseItemModel>() {
            @Override
            public int compare(ParentExpenseItemModel o1, ParentExpenseItemModel o2) {
                return o2.getPercentage().compareTo(o1.getPercentage());
            }
        });

        return result;
    }



    ArrayList<ParentExpenseItemModel> getExpensesForPersonWiseToDisplay(String trip_id){
        ArrayList<ParentExpenseItemModel> result = new ArrayList<>();

        ArrayList<ExpenseModel> allExpList = getAllExpenses(trip_id);

        HashMap<String, ParentExpenseItemModel> hashMapExpense = new HashMap<>();

        Double total_amount = 0.0;

        for(int i=0;i<allExpList.size();i++){
            ExpenseModel model = allExpList.get(i);
            total_amount += RoundOff(model.getAmount()*model.getCurrencyConversionRate());

            if(model.getShareByType() == 1){

                String persons[] = model.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
                Double amountTobeAdded = RoundOff(model.getAmount()*model.getCurrencyConversionRate()/persons.length);

                for(String person : persons){

                    if(hashMapExpense.containsKey(person)){

                        ParentExpenseItemModel tempModel = hashMapExpense.get(person);
                        tempModel.setAmount(RoundOff(tempModel.getAmount()+amountTobeAdded));

                        ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                        ExpenseModel updatedModel = new ExpenseModel(model);
                        updatedModel.setPersonNameForGeneration(person);
                        updatedModel.setAmountByPersonForGeneration(amountTobeAdded);
                        tempExpOnlyList.add(updatedModel);
                        tempModel.setExpenseList(tempExpOnlyList);

                        hashMapExpense.put(person,tempModel);

                    }else{

                        ParentExpenseItemModel tempModel = new ParentExpenseItemModel();
                        tempModel.setAmount(amountTobeAdded);

                        ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                        ExpenseModel updatedModel = new ExpenseModel(model);
                        updatedModel.setPersonNameForGeneration(person);
                        updatedModel.setAmountByPersonForGeneration(amountTobeAdded);
                        tempExpOnlyList.add(updatedModel);
                        tempModel.setExpenseList(tempExpOnlyList);

                        hashMapExpense.put(person,tempModel);
                    }

                }



            }else if(model.getShareByType() == 2){

                String personsAndAmount[] = model.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));

                for (String aPersonsAndAmount : personsAndAmount) {
                    String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                    Double amountTobeAdded = RoundOff(Double.parseDouble(eachPersonAndAmount[1])*model.getCurrencyConversionRate());
                    String person = eachPersonAndAmount[0];

                    if(hashMapExpense.containsKey(person)){

                        ParentExpenseItemModel tempModel = hashMapExpense.get(person);
                        tempModel.setAmount(RoundOff(tempModel.getAmount()+amountTobeAdded));

                        ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                        ExpenseModel updatedModel = new ExpenseModel(model);
                        updatedModel.setPersonNameForGeneration(person);
                        updatedModel.setAmountByPersonForGeneration(amountTobeAdded);
                        tempExpOnlyList.add(updatedModel);
                        tempModel.setExpenseList(tempExpOnlyList);

                        hashMapExpense.put(person,tempModel);

                    }else{

                        ParentExpenseItemModel tempModel = new ParentExpenseItemModel();
                        tempModel.setAmount(amountTobeAdded);

                        ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                        ExpenseModel updatedModel = new ExpenseModel(model);
                        updatedModel.setPersonNameForGeneration(person);
                        updatedModel.setAmountByPersonForGeneration(amountTobeAdded);
                        tempExpOnlyList.add(updatedModel);
                        tempModel.setExpenseList(tempExpOnlyList);

                        hashMapExpense.put(person,tempModel);
                    }

                }


            }else if(model.getShareByType() == 3){


                String personsAndShares[] = model.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));

                int totalShares=0;
                for(String aPersonsAndShare : personsAndShares ){
                    String eachPersonAndShare[] = aPersonsAndShare.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                    totalShares+=Integer.parseInt(eachPersonAndShare[1]);
                }

                double amountPerShare = RoundOff(model.getAmount()*model.getCurrencyConversionRate()/totalShares);

                for (String aPersonsAndShare : personsAndShares) {
                    String eachPersonAndShare[] = aPersonsAndShare.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                    Double amountTobeAdded = RoundOff(Integer.parseInt(eachPersonAndShare[1])*amountPerShare);
                    String person = eachPersonAndShare[0];

                    if(hashMapExpense.containsKey(person)){

                        ParentExpenseItemModel tempModel = hashMapExpense.get(person);
                        tempModel.setAmount(RoundOff(tempModel.getAmount()+amountTobeAdded));

                        ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                        ExpenseModel updatedModel = new ExpenseModel(model);
                        updatedModel.setPersonNameForGeneration(person);
                        updatedModel.setAmountByPersonForGeneration(amountTobeAdded);
                        tempExpOnlyList.add(updatedModel);
                        tempModel.setExpenseList(tempExpOnlyList);

                        hashMapExpense.put(person,tempModel);

                    }else{

                        ParentExpenseItemModel tempModel = new ParentExpenseItemModel();
                        tempModel.setAmount(amountTobeAdded);

                        ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                        ExpenseModel updatedModel = new ExpenseModel(model);
                        updatedModel.setPersonNameForGeneration(person);
                        updatedModel.setAmountByPersonForGeneration(amountTobeAdded);
                        tempExpOnlyList.add(updatedModel);
                        tempModel.setExpenseList(tempExpOnlyList);

                        hashMapExpense.put(person,tempModel);
                    }

                }

            }
        }

        Set<String> keys = hashMapExpense.keySet();
        Iterator<String> itr = keys.iterator();
        while(itr.hasNext()){
            String tempName = itr.next();

            ParentExpenseItemModel finalModel = hashMapExpense.get(tempName);
            finalModel.setName(tempName);
            if(total_amount!=0){
                Double percentage = RoundOff((finalModel.getAmount()/total_amount)*100);
                finalModel.setPercentage(percentage);
            }else{
                finalModel.setPercentage(0.0);
            }

            if(finalModel.getExpenseList().size() != 0){

                ArrayList<ExpenseModel> forSortingModel = finalModel.getExpenseList();

                Collections.sort(forSortingModel, new Comparator<ExpenseModel>() {
                    @Override
                    public int compare(ExpenseModel o1, ExpenseModel o2) {
                        if(o2.getDateValue()>o1.getDateValue()){
                            return 1;
                        }else if(o2.getDateValue()<o1.getDateValue()){
                            return -1;
                        }else{
                            return 0;
                        }
                    }
                });

                finalModel.setExpenseList(forSortingModel);

                result.add(finalModel);
            }

        }


        Collections.sort(result, new Comparator<ParentExpenseItemModel>() {
            @Override
            public int compare(ParentExpenseItemModel o1, ParentExpenseItemModel o2) {
                return o2.getPercentage().compareTo(o1.getPercentage());
            }
        });

        return result;
    }




    boolean deleteExpenseItem(ExpenseModel item){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(ITEMS_TABLE_NAME, ITEMS_COLUMN_TRIP_ID + " = \"" + item.getTripId()+"\" AND "+ITEMS_COLUMN_ITEM_ID+" = \""+ item.getItemId()+"\"" , null);
        addToTotalAmount(item.getTripId(),getTotalExpensesAmount(item.getTripId()));
        return true;
    }

    Double getDepositMoneyRemaining(String trip_id){

        Double result = 0.0,total_deposit_money = 0.0;

        ArrayList<ExpenseModel> expenseList = getAllExpenses(trip_id);
        for(int i=0;i<expenseList.size();i++){
            ExpenseModel model = expenseList.get(i);
            if(model.getAmountType() == 1){
                result += RoundOff(model.getAmount()*model.getCurrencyConversionRate());
            }
        }

        ArrayList<PersonModel> personsList = getPersons(trip_id);
        for(int i=0;i<personsList.size();i++){
            total_deposit_money += personsList.get(i).getDeposit();
        }

        return (total_deposit_money - result);
    }






    //Notes and checklists

    void addNotes(NotesModel notesModel){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NOTES_COLUMN_TRIP_ID,notesModel.getNote_TripId());
        values.put(NOTES_COLUMN_NOTE_ID,notesModel.getNote_Id());
        values.put(NOTES_COLUMN_NOTE_TITLE,notesModel.getNote_Title());
        values.put(NOTES_COLUMN_NOTE_CONTENT,notesModel.getNote_Body());
        values.put(NOTES_COLUMN_NOTE_CONTENT_TYPE,notesModel.getNote_ContentType());
        values.put(NOTES_COLUMN_NOTE_CONTENT_STATUS,notesModel.getNote_ContentStatus());
        values.put(NOTES_COLUMN_NOTE_DATE,notesModel.getNote_Date());

        db.insert(NOTES_TABLE_NAME,null,values);


    }

    void updateNotes(NotesModel notesModel){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NOTES_COLUMN_TRIP_ID,notesModel.getNote_TripId());
        values.put(NOTES_COLUMN_NOTE_ID,notesModel.getNote_Id());
        values.put(NOTES_COLUMN_NOTE_TITLE,notesModel.getNote_Title());
        values.put(NOTES_COLUMN_NOTE_CONTENT,notesModel.getNote_Body());
        values.put(NOTES_COLUMN_NOTE_CONTENT_TYPE,notesModel.getNote_ContentType());
        values.put(NOTES_COLUMN_NOTE_CONTENT_STATUS,notesModel.getNote_ContentStatus());
        values.put(NOTES_COLUMN_NOTE_DATE,notesModel.getNote_Date());

        db.update(NOTES_TABLE_NAME,values,NOTES_COLUMN_TRIP_ID+ "=? AND " + NOTES_COLUMN_NOTE_ID + "=?", new String[]{notesModel.getNote_TripId(),notesModel.getNote_Id()});
    }

    NotesModel getNotes(String tripId, String notesId){

        SQLiteDatabase db = getReadableDatabase();
        NotesModel notesModel = null;

        Cursor cursor = db.query(NOTES_TABLE_NAME,null,NOTES_COLUMN_TRIP_ID+ "=? AND " + NOTES_COLUMN_NOTE_ID + "=?"
                ,new String[]{tripId,notesId},null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            notesModel = new NotesModel();
            notesModel.setNote_Body(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_CONTENT)));
            notesModel.setNote_ContentStatus(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_CONTENT_STATUS)));
            notesModel.setNote_ContentType(cursor.getInt(cursor.getColumnIndex(NOTES_COLUMN_NOTE_CONTENT_TYPE)));
            notesModel.setNote_Date(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_DATE)));
            notesModel.setNote_Id(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_ID)));
            notesModel.setNote_Title(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_TITLE)));
            notesModel.setNote_TripId(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_TRIP_ID)));

            cursor.close();
        }



        return  notesModel;

    }

    ArrayList<NotesModel> getNotes(String trip_id){

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NotesModel> notesList = new ArrayList<>();

        Cursor cursor = db.query(NOTES_TABLE_NAME,null,NOTES_COLUMN_TRIP_ID+"=?",new String[]{trip_id},
                null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            do{
                NotesModel notesModel = new NotesModel();
                notesModel.setNote_Body(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_CONTENT)));
                notesModel.setNote_ContentStatus(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_CONTENT_STATUS)));
                notesModel.setNote_ContentType(cursor.getInt(cursor.getColumnIndex(NOTES_COLUMN_NOTE_CONTENT_TYPE)));
                notesModel.setNote_Date(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_DATE)));
                notesModel.setNote_Id(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_ID)));
                notesModel.setNote_Title(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_NOTE_TITLE)));
                notesModel.setNote_TripId(cursor.getString(cursor.getColumnIndex(NOTES_COLUMN_TRIP_ID)));

                notesList.add(notesModel);

            }while(cursor.moveToNext());

            cursor.close();
        }



        return  notesList;

    }

    void deleteNotes(NotesModel notesModel){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(NOTES_TABLE_NAME,NOTES_COLUMN_TRIP_ID+ "=? AND " + NOTES_COLUMN_NOTE_ID + "=?",
                new String[]{notesModel.getNote_TripId(),notesModel.getNote_Id()});
    }

}

