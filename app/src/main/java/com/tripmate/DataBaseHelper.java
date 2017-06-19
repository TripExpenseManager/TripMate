package com.tripmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Sai Krishna on 6/16/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TripExpenseManager";
    private static final int DATABASE_VERSION = 1;

    private static final String TRIPS_TABLE_NAME = "trips";
    private static final String TRIPS_COLUMN_ID = "key_trip_id";
    private static final String TRIPS_COLUMN_TRIP_NAME = "key_trip_name";
    private static final String TRIPS_COLUMN_TRIP_DESC = "key_trip_desc";
    private static final String TRIPS_COLUMN_TRIP_DATE = "key_trip_date";
    private static final String TRIPS_COLUMN_TRIP_PLACES = "key_trip_places";
    private static final String TRIPS_COLUMN_TRIP_TOTAL_AMOUNT = "key_trip_total_amt";


    private static final String ITEMS_TABLE_NAME = "items";
    private static final String ITEMS_COLUMN_TRIP_ID = "key_trip_id";
    private static final String ITEMS_COLUMN_ITEM_NAME = "key_item_name";
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




    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TRIPS_TABLE = "CREATE TABLE " + TRIPS_TABLE_NAME + " ( "+ TRIPS_COLUMN_ID + " TEXT PRIMARY KEY, " + TRIPS_COLUMN_TRIP_NAME + " TEXT, "+ TRIPS_COLUMN_TRIP_DESC + " TEXT, "+ TRIPS_COLUMN_TRIP_DATE+" TEXT, "+TRIPS_COLUMN_TRIP_PLACES+" TEXT, "+TRIPS_COLUMN_TRIP_TOTAL_AMOUNT+" REAL )";
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + ITEMS_TABLE_NAME + "("+ ITEMS_COLUMN_ITEM_ID + " TEXT PRIMARY KEY," + ITEMS_COLUMN_TRIP_ID + " TEXT,"+ ITEMS_COLUMN_ITEM_NAME + " TEXT, "+ ITEMS_COLUMN_ITEM_AMOUNT+ " REAL, "+ITEMS_COLUMN_ITEM_EXP_BY+" TEXT, "+ITEMS_COLUMN_ITEM_CAT+" TEXT, "+ITEMS_COLUMN_ITEM_DATE+" TEXT, "+ITEMS_COLUMN_ITEM_SHARE_BY+" TEXT, "+ITEMS_COLUMN_ITEM_DATE_VALUE+" TEXT )";
        String CREATE_PERSONS_TABLE = "CREATE TABLE " + PERSONS_TABLE_NAME + "("+ PERSONS_COLUMN_TRIP_ID + " TEXT," + PERSONS_COLUMN_PERSON_NAME + " TEXT,"+ PERSONS_COLUMN_PERSON_MOBILE + " TEXT, "+PERSONS_COLUMN_PERSON_EMAIL+" TEXT,"+ PERSONS_COLUMN_PERSON_DEPOSIT+" REAL, "+PERSONS_COLUMN_PERSON_ADMIN+" TEXT )";
        String CREATE_NOTES_TABLE = "CREATE TABLE " + NOTES_TABLE_NAME + "("+ NOTES_COLUMN_NOTE_ID + " TEXT PRIMARY KEY," + NOTES_COLUMN_TRIP_ID + " TEXT,"+ NOTES_COLUMN_NOTE_TITLE + " TEXT, " +NOTES_COLUMN_NOTE_CONTENT_TYPE+" TEXT, "+NOTES_COLUMN_NOTE_CONTENT+" TEXT, "+ NOTES_COLUMN_NOTE_CONTENT_STATUS + " TEXT )";

        db.execSQL(CREATE_TRIPS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_PERSONS_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TRIPS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ITEMS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+PERSONS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+NOTES_TABLE_NAME);
        onCreate(db);
    }

    public boolean addTrip(TripModel trip){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIPS_COLUMN_ID,trip.getTrip_id());
        values.put(TRIPS_COLUMN_TRIP_NAME,trip.getTrip_name());
        values.put(TRIPS_COLUMN_TRIP_PLACES,trip.getTrip_places());
        values.put(TRIPS_COLUMN_TRIP_DESC,trip.getTrip_desc());
        values.put(TRIPS_COLUMN_TRIP_DATE,trip.getTrip_date());
        values.put(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT,trip.getTrip_amount());

        db.insert(TRIPS_TABLE_NAME,null,values);
        return true;
    }


    public void addPersons(String trip_id , ArrayList<PersonModel> personsList){
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
        ArrayList<PersonModel> personsList = new ArrayList<PersonModel>();

        Cursor cursor = db.query(PERSONS_TABLE_NAME,null,PERSONS_COLUMN_TRIP_ID+"=?",new String[]{trip_id},
                null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            do{
                PersonModel personModel = new PersonModel();
                personModel.setName(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_NAME)));
                personModel.setMobile(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_MOBILE)));
                personModel.setEmail(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_EMAIL)));
                personModel.setDeposit(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_DEPOSIT)));
                personModel.setAdmin(cursor.getString(cursor.getColumnIndex(PERSONS_COLUMN_PERSON_ADMIN)));

                personsList.add(personModel);

            }while(cursor.moveToNext());
        }
        cursor.close();

        return  personsList;
    }

    public boolean addExpense(String trip_id , ExpenseModel expenseModel){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEMS_COLUMN_TRIP_ID,trip_id);
        values.put(ITEMS_COLUMN_ITEM_NAME,expenseModel.getName());
        values.put(ITEMS_COLUMN_ITEM_CAT,expenseModel.getCategory());
        values.put(ITEMS_COLUMN_ITEM_AMOUNT,expenseModel.getAmount());
        values.put(ITEMS_COLUMN_ITEM_EXP_BY,expenseModel.getExpBy());
        values.put(ITEMS_COLUMN_ITEM_SHARE_BY,expenseModel.getShareBy());
        values.put(ITEMS_COLUMN_ITEM_DATE,expenseModel.getDate());
        values.put(ITEMS_COLUMN_ITEM_ID,expenseModel.getExp_id());
        db.insert(ITEMS_TABLE_NAME,null,values);

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
                model.setTrip_amount(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_TOTAL_AMOUNT)));
                model.setTrip_desc(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_DESC)));
                model.setTrip_id(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_ID)));
                model.setTrip_places(cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_TRIP_PLACES)));

                trip_array_list.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trip_array_list;
    }



    public ArrayList<ExpenseModel> getAllExpenses(String trip_id){
        ArrayList<ExpenseModel> expenseModelArrayList = new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();

        Cursor cursor = db.query(ITEMS_TABLE_NAME,null,ITEMS_COLUMN_TRIP_ID + "=?",new String[]{trip_id},null,null,null);

        if(cursor!=null && cursor.moveToFirst()){
            do {
                ExpenseModel expenseModel = new ExpenseModel();
                expenseModel.setTrip_id(trip_id);
                expenseModel.setName(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_NAME)));
                expenseModel.setCategory(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_CAT)));
                expenseModel.setAmount(cursor.getDouble(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_AMOUNT)));
                expenseModel.setExpBy(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_EXP_BY)));
                expenseModel.setShareBy(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_SHARE_BY)));
                expenseModel.setExp_id(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_ID)));
                expenseModel.setDate(cursor.getString(cursor.getColumnIndex(ITEMS_COLUMN_ITEM_DATE)));

                expenseModelArrayList.add(expenseModel);

            }while(cursor.moveToNext());
        }


        cursor.close();

        return expenseModelArrayList;

    }


    public Double getAmountOfPerson(String trip_id,String personName){
        Double amount = 0.0;
        ArrayList<ExpenseModel> expenseModelArrayList = getAllExpenses(trip_id);

        for(ExpenseModel e : expenseModelArrayList){
            if(e.getExpBy().equalsIgnoreCase(personName))
                amount+=e.getAmount();
        }

        return  amount;
    }

    public Double getAmountOfCategory(String trip_id,String category){
        Double amount = 0.0;
        ArrayList<ExpenseModel> expenseModelArrayList = getAllExpenses(trip_id);

        for(ExpenseModel e : expenseModelArrayList){
            if(e.getCategory().equalsIgnoreCase(category))
                amount+=e.getAmount();
        }

        return  amount;
    }

    public Double getAmountOfDate(String trip_id,String date){
        Double amount = 0.0;
        ArrayList<ExpenseModel> expenseModelArrayList = getAllExpenses(trip_id);

        for(ExpenseModel e : expenseModelArrayList){
            if(e.getDate().equalsIgnoreCase(date))
                amount+=e.getAmount();
        }

        return  amount;
    }

    public Double getAmountShared(String trip_id,String personName){
        Double amount = 0.0;
        int noOfPersons = getPersons(trip_id).size();
        ArrayList<ExpenseModel> expenseModelArrayList = getAllExpenses(trip_id);

        for(ExpenseModel e : expenseModelArrayList){
            if(e.getShareBy().equalsIgnoreCase("all")){
                amount+=e.getAmount()/noOfPersons;
            }else if(e.getShareBy().toLowerCase().contains(personName.toLowerCase())){
                int noOfSharedPersons = Arrays.asList(e.getShareBy().split(",")).size();
                amount+=e.getAmount()/noOfSharedPersons;
            }
        }

        return  amount;
    }




}
