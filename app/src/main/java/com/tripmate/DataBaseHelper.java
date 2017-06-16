package com.tripmate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sai Krishna on 6/16/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TripExpenseManager";
    public static final int DATABASE_VERSION = 1;

    public static final String TRIPS_TABLE_NAME = "trips";
    public static final String TRIPS_COLUMN_ID = "key_trip_id";
    public static final String TRIPS_COLUMN_TRIP_NAME = "key_trip_name";
    public static final String TRIPS_COLUMN_TRIP_DESC = "key_trip_desc";
    public static final String TRIPS_COLUMN_TRIP_DATE = "key_trip_date";
    public static final String TRIPS_COLUMN_TRIP_PLACES = "key_trip_places";
    public static final String TRIPS_COLUMN_TRIP_TOTAL_AMOUNT = "key_trip_total_amt";


    public static final String ITEMS_TABLE_NAME = "items";
    public static final String ITEMS_COLUMN_TRIP_ID = "key_trip_id";
    public static final String ITEMS_COLUMN_ITEM_NAME = "key_item_name";
    public static final String ITEMS_COLUMN_ITEM_AMOUNT = "key_item_amt";
    public static final String ITEMS_COLUMN_ITEM_EXP_BY = "key_item_exp_by";
    public static final String ITEMS_COLUMN_ITEM_CAT = "key_item_cat";
    public static final String ITEMS_COLUMN_ITEM_DATE = "key_item_date";
    public static final String ITEMS_COLUMN_ITEM_SHARE_BY = "key_item_share_by";
    public static final String ITEMS_COLUMN_ITEM_DATE_VALUE = "key_item_date_value";
    public static final String ITEMS_COLUMN_ITEM_ID = "key_item_id";


    public static final String PERSONS_TABLE_NAME = "persons";
    public static final String PERSONS_COLUMN_TRIP_ID = "key_trip_id";
    public static final String PERSONS_COLUMN_PERSON_NAME = "key_person_name";
    public static final String PERSONS_COLUMN_PERSON_MOBILE = "key_person_mobile";
    public static final String PERSONS_COLUMN_PERSON_EMAIL = "key_person_email";
    public static final String PERSONS_COLUMN_PERSON_DEPOSIT = "key_person_deposit";
    public static final String PERSONS_COLUMN_PERSON_ADMIN = "key_person_admin";


    public static final String NOTES_TABLE_NAME = "notes";
    public static final String NOTES_COLUMN_NOTE_ID = "key_note_id";
    public static final String NOTES_COLUMN_TRIP_ID = "key_trip_id";
    public static final String NOTES_COLUMN_NOTE_TITLE = "key_note_title";
    public static final String NOTES_COLUMN_NOTE_CONTENT_TYPE = "key_note_content_type";
    public static final String NOTES_COLUMN_NOTE_CONTENT = "key_note_content";
    public static final String NOTES_COLUMN_NOTE_CONTENT_STATUS = "key_note_content_status";




    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TRIPS_TABLE = "CREATE TABLE " + TRIPS_TABLE_NAME + " ( "+ TRIPS_COLUMN_ID + " TEXT PRIMARY KEY, " + TRIPS_COLUMN_TRIP_NAME + " TEXT, "+ TRIPS_COLUMN_TRIP_DESC + " TEXT, "+ TRIPS_COLUMN_TRIP_DATE+" TEXT, "+TRIPS_COLUMN_TRIP_PLACES+" TEXT, "+TRIPS_COLUMN_TRIP_TOTAL_AMOUNT+" TEXT )";
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + ITEMS_TABLE_NAME + "("+ ITEMS_COLUMN_ITEM_ID + " TEXT PRIMARY KEY," + ITEMS_COLUMN_TRIP_ID + " TEXT,"+ ITEMS_COLUMN_ITEM_NAME + " TEXT, "+ ITEMS_COLUMN_ITEM_AMOUNT+ " TEXT, "+ITEMS_COLUMN_ITEM_EXP_BY+" TEXT, "+ITEMS_COLUMN_ITEM_CAT+" TEXT, "+ITEMS_COLUMN_ITEM_DATE+" TEXT, "+ITEMS_COLUMN_ITEM_SHARE_BY+" TEXT, "+ITEMS_COLUMN_ITEM_DATE_VALUE+" TEXT )";
        String CREATE_PERSONS_TABLE = "CREATE TABLE " + PERSONS_TABLE_NAME + "("+ PERSONS_COLUMN_TRIP_ID + " TEXT," + PERSONS_COLUMN_PERSON_NAME + " TEXT,"+ PERSONS_COLUMN_PERSON_MOBILE + " TEXT, "+PERSONS_COLUMN_PERSON_EMAIL+" TEXT,"+ PERSONS_COLUMN_PERSON_DEPOSIT+" TEXT, "+PERSONS_COLUMN_PERSON_ADMIN+" TEXT )";
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
}
