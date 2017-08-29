package com.tripmate;

import android.app.models.AddExpenseByPersonModel;
import android.app.models.GraphItemModel;
import android.app.models.NotesModel;
import android.app.models.ParentExpenseItemModel;
import android.app.models.PersonWiseExpensesSummaryModel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.app.models.ExpenseModel;
import android.app.models.PersonModel;
import android.app.models.TripModel;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Sai Krishna on 6/16/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TripExpenseManager";
    private static final int DATABASE_VERSION = 1;

    private static final String TRIPS_TABLE_NAME = "trips";
    private static final String TRIPS_COLUMN_ID = "key_trip_id";
    private static final String TRIPS_COLUMN_TRIP_NAME = "key_trip_name";
    private static final String TRIPS_COLUMN_TRIP_DESC = "key_trip_desc";
    private static final String TRIPS_COLUMN_TRIP_DATE = "key_trip_date";
    private static final String TRIPS_COLUMN_TRIP_PLACES = "key_trip_places";
    private static final String TRIPS_COLUMN_TRIP_TOTAL_AMOUNT = "key_trip_total_amt";
    private static final String TRIPS_COLUMN_IMAGE_URL = "key_trip_image_url";


    private static final String ITEMS_TABLE_NAME = "items";
    private static final String ITEMS_COLUMN_TRIP_ID = "key_trip_id";
    private static final String ITEMS_COLUMN_ITEM_NAME = "key_item_name";
    private static final String ITEMS_COLUMN_AMOUNT_TYPE = "key_amount_type";
    private static final String ITEMS_COLUMN_ITEM_AMOUNT = "key_item_amt";
    private static final String ITEMS_COLUMN_ITEM_EXP_BY = "key_item_exp_by";
    private static final String ITEMS_COLUMN_ITEM_CAT = "key_item_cat";
    private static final String ITEMS_COLUMN_ITEM_DATE = "key_item_date";
    private static final String ITEMS_COLUMN_ITEM_SHARE_BY = "key_item_share_by";
    private static final String ITEMS_COLUMN_ITEM_DATE_VALUE = "key_item_date_value";
    private static final String ITEMS_COLUMN_ITEM_ID = "key_item_id";


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

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TRIPS_TABLE = "CREATE TABLE " + TRIPS_TABLE_NAME + " ( "+ TRIPS_COLUMN_ID + " TEXT PRIMARY KEY, " + TRIPS_COLUMN_TRIP_NAME + " TEXT, "+ TRIPS_COLUMN_TRIP_DESC + " TEXT, "+ TRIPS_COLUMN_TRIP_DATE+" TEXT, "+TRIPS_COLUMN_TRIP_PLACES+" TEXT, "+TRIPS_COLUMN_TRIP_TOTAL_AMOUNT+" REAL , "+ TRIPS_COLUMN_IMAGE_URL +" TEXT )";
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + ITEMS_TABLE_NAME + "("+ ITEMS_COLUMN_ITEM_ID + " TEXT PRIMARY KEY," + ITEMS_COLUMN_TRIP_ID + " TEXT,"+ ITEMS_COLUMN_ITEM_NAME + " TEXT, "+ITEMS_COLUMN_AMOUNT_TYPE+" INTEGER, "+ ITEMS_COLUMN_ITEM_AMOUNT+ " REAL, "+ITEMS_COLUMN_ITEM_EXP_BY+" TEXT, "+ITEMS_COLUMN_ITEM_CAT+" TEXT, "+ITEMS_COLUMN_ITEM_DATE+" TEXT, "+ITEMS_COLUMN_ITEM_SHARE_BY+" TEXT, "+ITEMS_COLUMN_ITEM_DATE_VALUE+" TEXT )";
        String CREATE_PERSONS_TABLE = "CREATE TABLE " + PERSONS_TABLE_NAME + "("+ PERSONS_COLUMN_TRIP_ID + " TEXT," + PERSONS_COLUMN_PERSON_NAME + " TEXT,"+ PERSONS_COLUMN_PERSON_MOBILE + " TEXT, "+PERSONS_COLUMN_PERSON_EMAIL+" TEXT,"+ PERSONS_COLUMN_PERSON_DEPOSIT+" REAL, "+PERSONS_COLUMN_PERSON_ADMIN+" INTEGER )";
        String CREATE_NOTES_TABLE = "CREATE TABLE " + NOTES_TABLE_NAME + "("+ NOTES_COLUMN_NOTE_ID + " TEXT PRIMARY KEY," + NOTES_COLUMN_TRIP_ID + " TEXT,"+ NOTES_COLUMN_NOTE_TITLE + " TEXT, " +NOTES_COLUMN_NOTE_CONTENT_TYPE+" INTEGER, "+NOTES_COLUMN_NOTE_CONTENT+" TEXT, "+ NOTES_COLUMN_NOTE_CONTENT_STATUS + " TEXT, "+NOTES_COLUMN_NOTE_DATE+" TEXT)";
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE "+CATEGORIES_TABLE_NAME+" ( "+CATEGORIES_COLUMN_CAT_NAME+" TEXT)";

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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TRIPS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ITEMS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+PERSONS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+NOTES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+CATEGORIES_TABLE_NAME);
        onCreate(db);
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




    boolean addTrip(TripModel trip){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_ID,trip.getTrip_id());
        values.put(TRIPS_COLUMN_TRIP_NAME,trip.getTrip_name());
        values.put(TRIPS_COLUMN_TRIP_PLACES,trip.getTrip_places());
        values.put(TRIPS_COLUMN_TRIP_DESC,trip.getTrip_desc());
        values.put(TRIPS_COLUMN_TRIP_DATE,trip.getTrip_date());
        values.put(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT,trip.getTrip_amount());
        values.put(TRIPS_COLUMN_IMAGE_URL,trip.getImageUrl());

        db.insert(TRIPS_TABLE_NAME,null,values);
        return true;
    }

    String[] getCategories(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(CATEGORIES_TABLE_NAME,null,null,null,null,null,null);
        String[] catList = new String[cursor.getCount()];
        if(cursor!=null && cursor.moveToFirst()){
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

    public ArrayList<PersonModel> getPersons(String trip_id){
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

    public void addToTotalAmount(String trip_id,Double amount){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT,amount);

        db.update(TRIPS_TABLE_NAME,values,TRIPS_COLUMN_ID+ "=? ", new String[]{trip_id});


    }

    public ArrayList<PersonModel> getAllPersons(){
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

    public boolean addExpense(String trip_id,String description,String category,String date,String expShareByPersonsSelected,int amount_type,ArrayList<AddExpenseByPersonModel> expenseByPersonList,Double fromDepositExpense,Long date_value){
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

    public ArrayList<TripModel> getTripsData() {
        ArrayList<TripModel> trip_array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TRIPS_TABLE_NAME, null );

        if (cursor.moveToFirst()) {
            do {
                TripModel model = new TripModel();
                model.setTrip_name(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_NAME)));
                model.setTrip_date(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_DATE)));
                model.setTrip_amount(cursor.getDouble(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT)));
                model.setTrip_desc(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_DESC)));
                model.setTrip_id(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_ID)));
                model.setTrip_places(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_PLACES)));
                model.setImageUrl(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_IMAGE_URL)));

                trip_array_list.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trip_array_list;
    }

    public TripModel getTripData(String trip_id) {
        ArrayList<TripModel> trip_array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TRIPS_TABLE_NAME,null,ITEMS_COLUMN_TRIP_ID + "=?",new String[]{trip_id},null,null,null);

        TripModel model = new TripModel();
        if (cursor.moveToFirst()) {
            do {
                model.setTrip_name(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_NAME)));
                model.setTrip_date(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_DATE)));
                model.setTrip_amount(cursor.getDouble(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT)));
                model.setTrip_desc(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_DESC)));
                model.setTrip_id(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_ID)));
                model.setTrip_places(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_PLACES)));
                model.setImageUrl(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_IMAGE_URL)));
            } while (cursor.moveToNext());
        }

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

    public ArrayList<ExpenseModel> getAllExpenses(String trip_id){
        ArrayList<ExpenseModel> expenseModelArrayList = new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();

        Cursor cursor = db.query(ITEMS_TABLE_NAME,null,ITEMS_COLUMN_TRIP_ID + "=?",new String[]{trip_id},null,null,null);

        if(cursor!=null && cursor.moveToFirst()){
            do {
                ExpenseModel expenseModel = new ExpenseModel();
                expenseModel.setTripId(trip_id);
                expenseModel.setItemName(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_NAME)));
                expenseModel.setAmount(cursor.getDouble(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_AMOUNT)));
                expenseModel.setAmountType(cursor.getInt(cursor.getColumnIndex(ITEMS_COLUMN_AMOUNT_TYPE)));
                expenseModel.setExpBy(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_EXP_BY)));
                expenseModel.setCategory(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_CAT)));
                expenseModel.setDate(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_DATE)));
                //expenseModel.setShareByType(cursor.getInt(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_SHARE_BY_TYPE)));
                expenseModel.setShareBy(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_SHARE_BY)));
                expenseModel.setItemId(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_ID)));
                expenseModel.setDateValue(cursor.getLong(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_DATE_VALUE)));

                expenseModelArrayList.add(expenseModel);

            }while(cursor.moveToNext());

            cursor.close();
        }



        return expenseModelArrayList;

    }

    public ArrayList<PersonWiseExpensesSummaryModel> getPersonWiseExpensesSummary(String trip_id){
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<PersonWiseExpensesSummaryModel> result = new ArrayList<>();

        HashMap<String,Double> depositAmountShareByPerson = new HashMap<>();
        HashMap<String,Double> personalAmountShareByPerson = new HashMap<>();
        HashMap<String,Double> personalAmountGiven = new HashMap<>();

        Cursor cursor = db.query(PERSONS_TABLE_NAME,null,PERSONS_COLUMN_TRIP_ID+"=?",new String[]{trip_id},null,null,null);

        String personsListAsString[] = new String[cursor.getCount()];
        int p=0;

        if(cursor!=null && cursor.moveToFirst()){
            do{
                PersonWiseExpensesSummaryModel model = new PersonWiseExpensesSummaryModel();
                model.setName(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)));
                model.setMobile(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_MOBILE)));
                model.setEmail(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_EMAIL)));
                model.setDepositAmountGiven(cursor.getDouble(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_DEPOSIT)));
                model.setAdmin(cursor.getInt(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_ADMIN)));

                depositAmountShareByPerson.put(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)),0.0);
                personalAmountShareByPerson.put(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)),0.0);
                personalAmountGiven.put(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)),0.0);
                personsListAsString[p] = cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME));
                p++;

                result.add(model);

            }while(cursor.moveToNext());
        }
        cursor.close();

        String tempPersons = personsListAsString[0];
        for(int i=1;i<personsListAsString.length;i++){
            tempPersons = tempPersons + ", "+personsListAsString[i];
        }

        int no_of_persons = result.size();

        ArrayList<ExpenseModel> allExpensesList = getAllExpenses(trip_id);

        Double totalDepositShare = 0.0,totalPersonalShare = 0.0;

        for(int i=0;i<allExpensesList.size();i++){
            ExpenseModel expenseModel = allExpensesList.get(i);

            if(expenseModel.getAmountType() == 1){

                if(expenseModel.getShareBy().equalsIgnoreCase(tempPersons)){
                    totalDepositShare += (expenseModel.getAmount()/no_of_persons);
                }else{

                    String persons[] = expenseModel.getShareBy().split(",");
                    int no_of_share_by_deposit = persons.length;
                    Double sharedDepositAmount = expenseModel.getAmount()/no_of_share_by_deposit;

                    for(int j=0;j<no_of_share_by_deposit;j++){
                        Double itemToBeAdded = depositAmountShareByPerson.get(persons[j].trim())+ sharedDepositAmount;
                        depositAmountShareByPerson.put(persons[j].trim(),itemToBeAdded);
                    }

                }

            }else{

                Double itemToBeAddedPersonal = personalAmountGiven.get(expenseModel.getExpBy()) + expenseModel.getAmount();
                personalAmountGiven.put(expenseModel.getExpBy(),itemToBeAddedPersonal);

                if(expenseModel.getShareBy().equalsIgnoreCase(tempPersons)){
                    totalPersonalShare += (expenseModel.getAmount()/no_of_persons);
                }else{

                    String persons[] = expenseModel.getShareBy().split(",");
                    int no_of_share_by_personal_amount = persons.length;
                    Double sharedPersonalAmount = expenseModel.getAmount()/no_of_share_by_personal_amount;

                    for(int j=0;j<no_of_share_by_personal_amount;j++){
                        Double itemToBeAdded = personalAmountShareByPerson.get(persons[j].trim())+ sharedPersonalAmount;
                        personalAmountShareByPerson.put(persons[j].trim(),itemToBeAdded);
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

    public ArrayList<PersonWiseExpensesSummaryModel> getPersonWiseExpensesSummaryForDashboard(String trip_id){

        ArrayList<PersonWiseExpensesSummaryModel> result = getPersonWiseExpensesSummary(trip_id);

        Collections.sort(result, new Comparator<PersonWiseExpensesSummaryModel>() {
            @Override
            public int compare(PersonWiseExpensesSummaryModel o1, PersonWiseExpensesSummaryModel o2) {
                return o2.getTotalAmountRemaining().compareTo(o1.getTotalAmountRemaining());

            }
        });

        return  result;
    }

    public ArrayList<PersonWiseExpensesSummaryModel> getPersonWiseExpensesSummaryForPersonsFragment(String trip_id){

        ArrayList<PersonWiseExpensesSummaryModel> result = getPersonWiseExpensesSummary(trip_id);


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

    public ArrayList<GraphItemModel> getPersonWiseExpensesSummaryForGraphPersons(String trip_id){

        ArrayList<PersonWiseExpensesSummaryModel> list = getPersonWiseExpensesSummary(trip_id);
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

    public ArrayList<GraphItemModel> getPersonWiseExpensesSummaryForGraphCategory(String trip_id){

        ArrayList<GraphItemModel> result = new ArrayList<>();
        ArrayList<ExpenseModel> list = getAllExpenses(trip_id);

        HashMap<String,Double> categoryWiseExpenses = new HashMap<>();
        Double total_expenses= 0.0;
        for(int i=0;i<list.size();i++){
            ExpenseModel model = list.get(i);
            total_expenses += model.getAmount();
            if(categoryWiseExpenses.containsKey(model.getCategory())){
                Double toBeInserted = categoryWiseExpenses.get(model.getCategory()) + model.getAmount();
                categoryWiseExpenses.put(model.getCategory(),toBeInserted);
            }else{
                categoryWiseExpenses.put(model.getCategory(),model.getAmount());
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

    public ArrayList<GraphItemModel> getPersonWiseExpensesSummaryForGraphDate(String trip_id){

        ArrayList<GraphItemModel> result = new ArrayList<>();
        ArrayList<ExpenseModel> list = getAllExpenses(trip_id);

        HashMap<String,Double> dateWiseExpenses = new HashMap<>();
        Double total_expenses= 0.0;
        for(int i=0;i<list.size();i++){
            ExpenseModel model = list.get(i);
            total_expenses += model.getAmount();
            if(dateWiseExpenses.containsKey(model.getDate())){
                Double toBeInserted = dateWiseExpenses.get(model.getDate()) + model.getAmount();
                dateWiseExpenses.put(model.getDate(),toBeInserted);
            }else{
                dateWiseExpenses.put(model.getDate(),model.getAmount());
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

    public Double getTotalExpensesAmount(String trip_id){

        ArrayList<ExpenseModel> list = getAllExpenses(trip_id);

        Double total_expenses= 0.0;
        for(int i=0;i<list.size();i++){
            ExpenseModel model = list.get(i);
            total_expenses += model.getAmount();
        }

        return  total_expenses;
    }

    public void editPerson(String trip_id,PersonModel model){
        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PERSONS_COLUMN_PERSON_MOBILE,model.getMobile());
        cv.put(PERSONS_COLUMN_PERSON_EMAIL,model.getEmail());
        cv.put(PERSONS_COLUMN_PERSON_DEPOSIT,model.getDeposit());

        db.update(PERSONS_TABLE_NAME, cv,PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+PERSONS_COLUMN_PERSON_NAME+" = \""+model.getName()+"\"", null);
    }

    public void addAsAdmin(String trip_id,String name,String pastAdmin){
        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PERSONS_COLUMN_PERSON_ADMIN,1);
        db.update(PERSONS_TABLE_NAME, cv,PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+PERSONS_COLUMN_PERSON_NAME+" = \""+name+"\"", null);


        ContentValues cv1 = new ContentValues();
        cv1.put(PERSONS_COLUMN_PERSON_ADMIN,0);
        db.update(PERSONS_TABLE_NAME, cv1,PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+PERSONS_COLUMN_PERSON_NAME+" = \""+pastAdmin+"\"", null);

    }

    public Double RoundOff(Double d){
        return Math.round(d * 100.0) / 100.0;
    }

    public ArrayList<ParentExpenseItemModel> getAllExpensesToDisplay(String trip_id){
        ArrayList<ParentExpenseItemModel> result = new ArrayList<>();

        ArrayList<ExpenseModel> allExpList = getAllExpenses(trip_id);

        HashMap<String, ParentExpenseItemModel> hashMapExpense = new HashMap<>();
        hashMapExpense.put("Deposit Money",new ParentExpenseItemModel());
        hashMapExpense.put("Personal Money",new ParentExpenseItemModel());

        Double total_amount = 0.0;

        for(int i=0;i<allExpList.size();i++){
            ExpenseModel model = allExpList.get(i);
            total_amount += RoundOff(model.getAmount());
            if(model.getAmountType() == 1){
                ParentExpenseItemModel tempModel = hashMapExpense.get("Deposit Money");
                Double tempAmountToAdd = tempModel.getAmount()+ model.getAmount();
                tempModel.setAmount(RoundOff(tempAmountToAdd));

                ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getChildList();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put("Deposit Money",tempModel);

            }else{
                ParentExpenseItemModel tempModel = hashMapExpense.get("Personal Money");
                Double tempAmountToAdd = tempModel.getAmount()+ model.getAmount();
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

    public ArrayList<ParentExpenseItemModel> getExpensesPersonWiseToDisplay(String trip_id){
        ArrayList<ParentExpenseItemModel> result = new ArrayList<>();

        ArrayList<ExpenseModel> allExpList = getAllExpenses(trip_id);

        HashMap<String, ParentExpenseItemModel> hashMapExpense = new HashMap<>();

        Double total_amount = 0.0;

        for(int i=0;i<allExpList.size();i++){
            ExpenseModel model = allExpList.get(i);
            if(model.getAmountType() != 1){
                total_amount += RoundOff(model.getAmount());

                if(hashMapExpense.containsKey(model.getExpBy())){

                    ParentExpenseItemModel tempModel = hashMapExpense.get(model.getExpBy());
                    tempModel.setAmount(RoundOff(tempModel.getAmount()+model.getAmount()));

                    ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                    tempExpOnlyList.add(model);
                    tempModel.setExpenseList(tempExpOnlyList);

                    hashMapExpense.put(model.getExpBy(),tempModel);

                }else{

                    ParentExpenseItemModel tempModel = new ParentExpenseItemModel();
                    tempModel.setAmount(RoundOff(model.getAmount()));

                    ArrayList<ExpenseModel> tempExpOnlyList = new ArrayList<>();
                    tempExpOnlyList.add(model);
                    tempModel.setExpenseList(tempExpOnlyList);

                    hashMapExpense.put(model.getExpBy(),tempModel);
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

    public ArrayList<ParentExpenseItemModel> getExpensesDateWiseToDisplay(String trip_id){
        ArrayList<ParentExpenseItemModel> result = new ArrayList<>();

        ArrayList<ExpenseModel> allExpList = getAllExpenses(trip_id);

        HashMap<String, ParentExpenseItemModel> hashMapExpense = new HashMap<>();

        Double total_amount = 0.0;

        for(int i=0;i<allExpList.size();i++){
            ExpenseModel model = allExpList.get(i);

            total_amount += RoundOff(model.getAmount());

            if(hashMapExpense.containsKey(model.getDate())){

                ParentExpenseItemModel tempModel = hashMapExpense.get(model.getDate());
                tempModel.setAmount(RoundOff(tempModel.getAmount()+model.getAmount()));

                ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put(model.getDate(),tempModel);

            }else{

                ParentExpenseItemModel tempModel = new ParentExpenseItemModel();
                tempModel.setAmount(RoundOff(model.getAmount()));

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

                return date1.compareTo(date2);
            }
        });

        return result;
    }

    public ArrayList<ParentExpenseItemModel> getExpensesCategoryWiseToDisplay(String trip_id){
        ArrayList<ParentExpenseItemModel> result = new ArrayList<>();

        ArrayList<ExpenseModel> allExpList = getAllExpenses(trip_id);

        HashMap<String, ParentExpenseItemModel> hashMapExpense = new HashMap<>();

        Double total_amount = 0.0;

        for(int i=0;i<allExpList.size();i++){
            ExpenseModel model = allExpList.get(i);

            total_amount += RoundOff(model.getAmount());

            if(hashMapExpense.containsKey(model.getCategory())){

                ParentExpenseItemModel tempModel = hashMapExpense.get(model.getCategory());
                tempModel.setAmount(RoundOff(tempModel.getAmount()+model.getAmount()));

                ArrayList<ExpenseModel> tempExpOnlyList = tempModel.getExpenseList();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put(model.getCategory(),tempModel);

            }else{

                ParentExpenseItemModel tempModel = new ParentExpenseItemModel();
                tempModel.setAmount(RoundOff(model.getAmount()));

                ArrayList<ExpenseModel> tempExpOnlyList = new ArrayList<>();
                tempExpOnlyList.add(model);
                tempModel.setExpenseList(tempExpOnlyList);

                hashMapExpense.put(model.getCategory(),tempModel);
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

    public boolean deleteExpenseItem(ExpenseModel item){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(ITEMS_TABLE_NAME, ITEMS_COLUMN_TRIP_ID + " = \"" + item.getTripId()+"\" AND "+ITEMS_COLUMN_ITEM_ID+" = \""+ item.getItemId()+"\"" , null);
        addToTotalAmount(item.getTripId(),getTotalExpensesAmount(item.getTripId()));
        return true;
    }

    public boolean isTripExists(String trip_id){

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

    public boolean addPersonInMiddle(PersonModel model){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PERSONS_COLUMN_TRIP_ID,model.getTrip_id());
        values.put(PERSONS_COLUMN_PERSON_NAME, model.getName());
        values.put(PERSONS_COLUMN_PERSON_MOBILE, model.getMobile());
        values.put(PERSONS_COLUMN_PERSON_EMAIL, model.getEmail());
        values.put(PERSONS_COLUMN_PERSON_DEPOSIT, model.getDeposit());
        values.put(PERSONS_COLUMN_PERSON_ADMIN, model.getAdmin());
        db.insert(PERSONS_TABLE_NAME,null,values);

        return true;
    }

    public  Double getDepositMoneyRemaining(String trip_id){

        Double result = 0.0,total_deposit_money = 0.0;

        ArrayList<ExpenseModel> expenseList = getAllExpenses(trip_id);
        for(int i=0;i<expenseList.size();i++){
            ExpenseModel model = expenseList.get(i);
            if(model.getAmountType() == 1){
                result += model.getAmount();
            }
        }

        ArrayList<PersonModel> personsList = getPersons(trip_id);
        for(int i=0;i<personsList.size();i++){
            total_deposit_money += personsList.get(i).getDeposit();
        }

        return (total_deposit_money - result);
    }

    public boolean removePerson(PersonWiseExpensesSummaryModel model,String trip_id){

        SQLiteDatabase db = getWritableDatabase();
        return db.delete(PERSONS_TABLE_NAME, PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\" AND "+PERSONS_COLUMN_PERSON_NAME+" = \""+ model.getName()+"\"" , null) > 0;
    }

    public boolean deleteTrip(String trip_id){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(ITEMS_TABLE_NAME,ITEMS_COLUMN_TRIP_ID + " = \"" + trip_id+"\"",null);
        db.delete(PERSONS_TABLE_NAME,PERSONS_COLUMN_TRIP_ID + " = \"" + trip_id+"\"",null);
        db.delete(NOTES_TABLE_NAME,NOTES_COLUMN_TRIP_ID + " = \"" + trip_id+"\"",null);

        return db.delete(TRIPS_TABLE_NAME,TRIPS_COLUMN_ID + " = \"" + trip_id+"\"",null) > 0;

    }

    public boolean editExpense(String trip_id,String description,String category,String date,String expShareByPersonsSelected,int amount_type,ArrayList<AddExpenseByPersonModel> expenseByPersonList,Double fromDepositExpense,Long date_value,String item_id){
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

    public void addNotes(NotesModel notesModel){
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

    public void updateNotes(NotesModel notesModel){
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

    public NotesModel getNotes(String tripId, String notesId){

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

    public ArrayList<NotesModel> getNotes(String trip_id){

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

