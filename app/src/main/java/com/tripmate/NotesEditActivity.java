package com.tripmate;

import android.app.AlertDialog;
import android.app.models.NotesModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.UUID;

public class NotesEditActivity extends AppCompatActivity {

    RelativeLayout rlNotes;
    EditText etNotesTitle,etNotesBody;
    String tripId,notesId;
    String editOrAdd;
    Toolbar toolbar;
    FloatingActionButton fabEdit;

    boolean isAdd = false;
    boolean editable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

        setContentView(R.layout.activity_notes_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getStringExtra("anim").equalsIgnoreCase("yes")){
            overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
        }

        getSupportActionBar().setTitle("Notes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        rlNotes = (RelativeLayout) findViewById(R.id.rlNotes);
        etNotesTitle = (EditText) findViewById(R.id.etNotesTitle);
        etNotesBody = (EditText) findViewById(R.id.etNotesBody);

        fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);



        tripId = getIntent().getStringExtra("tripId");
        editOrAdd = getIntent().getStringExtra("editOrAdd");
        if(editOrAdd.equals("add")){
            etNotesTitle.setText("");
            etNotesBody.setText("");
            etNotesTitle.setFocusableInTouchMode(true);
            etNotesBody.setFocusableInTouchMode(true);
            etNotesTitle.setClickable(true);
            etNotesBody.setClickable(true);
            enableEditing();
            editable = true;
            isAdd = true;

        }else {

            if(editOrAdd.equals("view")){
                etNotesTitle.setFocusable(false);
                etNotesTitle.setClickable(false);
                etNotesBody.setClickable(false);
                etNotesBody.setFocusable(false);
                disableEditing();
                editable = false;
            }else{
                enableEditing();
                editable = true;
            }
            notesId = getIntent().getStringExtra("notesId");
            DataBaseHelper dataBaseHelper = new DataBaseHelper(NotesEditActivity.this);
            NotesModel notesModel = dataBaseHelper.getNotes(tripId,notesId);

            etNotesTitle.setText(notesModel.getNote_Title());
            etNotesBody.setText(notesModel.getNote_Body());

        }

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditing();
            }
        });




    }





    public void saveNotes(){


        Long datevalue = Calendar.getInstance().getTimeInMillis();
        String notesDate = String.valueOf(datevalue);

        if(editOrAdd.equals("add") && isAdd && !(etNotesBody.getText().toString().equalsIgnoreCase("")
                && etNotesTitle.getText().toString().equalsIgnoreCase("")) ){
            NotesModel notesModel = new NotesModel();
            notesModel.setNote_TripId(tripId);
            String note_id = "Notes" + UUID.randomUUID().toString();
            notesModel.setNote_Id(note_id);
            notesModel.setNote_Title(etNotesTitle.getText().toString());
            notesModel.setNote_Body(etNotesBody.getText().toString());
            notesModel.setNote_ContentType(1);
            notesModel.setNote_Date(notesDate);
            notesModel.setNote_ContentStatus("Notes");

            DataBaseHelper dataBaseHelper = new DataBaseHelper(NotesEditActivity.this);
            dataBaseHelper.addNotes(notesModel);

            notesId = note_id;
            isAdd =false;

            disableEditing();
            invalidateOptionsMenu();


           /* Toast.makeText(getApplicationContext(),"Notes Added Successfully",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NotesEditActivity.this,NotesEditActivity.class);
            intent.putExtra("tripId",tripId);
            intent.putExtra("editOrAdd","view");
            intent.putExtra("notesId",note_id);
            intent.putExtra("anim","no");
            startActivity(intent);
            finish();*/
        }else{
            NotesModel notesModel = new NotesModel();
            notesModel.setNote_TripId(tripId);
            notesModel.setNote_Id(notesId);
            notesModel.setNote_Title(etNotesTitle.getText().toString());
            notesModel.setNote_Body(etNotesBody.getText().toString());
            notesModel.setNote_ContentType(1);
            notesModel.setNote_Date(notesDate);
            notesModel.setNote_ContentStatus("Notes");

            if((etNotesBody.getText().toString().equalsIgnoreCase("")
                    && etNotesTitle.getText().toString().equalsIgnoreCase(""))){

                DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
                dataBaseHelper.deleteNotes(notesModel);
                finish();

            }else {

                DataBaseHelper dataBaseHelper = new DataBaseHelper(NotesEditActivity.this);
                dataBaseHelper.updateNotes(notesModel);
            }

            disableEditing();
            invalidateOptionsMenu();


           /* Toast.makeText(getApplicationContext(),"Notes Updated Successfully",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NotesEditActivity.this,NotesEditActivity.class);
            intent.putExtra("tripId",tripId);
            intent.putExtra("editOrAdd","view");
            intent.putExtra("notesId",notesId);
            intent.putExtra("anim","no");
            startActivity(intent);
            finish();*/
        }
    }

    public  void  editNotes(){
        etNotesTitle.setFocusableInTouchMode(true);
        etNotesBody.setFocusableInTouchMode(true);
        etNotesTitle.setClickable(true);
        etNotesBody.setClickable(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_edit, menu);

        if (editable)
            menu.findItem(R.id.action_save).setVisible(true);
        else
            menu.findItem(R.id.action_save).setVisible(false);
        return  true;

    }

    public void  enableEditing(){
        editable = true;
        invalidateOptionsMenu();
        etNotesTitle.setFocusableInTouchMode(true);
        etNotesBody.setFocusableInTouchMode(true);
        etNotesTitle.setFocusable(true);
        etNotesBody.setFocusable(true);
        etNotesTitle.setClickable(true);
        etNotesBody.setClickable(true);
        fabEdit.setVisibility(View.GONE);
        etNotesBody.requestFocus();
        etNotesBody.setSelection(etNotesBody.getText().toString().length());

    }
    public void  disableEditing(){
        editable = false;
        invalidateOptionsMenu();
        etNotesTitle.clearFocus();
        etNotesBody.clearFocus();
        etNotesTitle.setFocusableInTouchMode(false);
        etNotesBody.setFocusableInTouchMode(false);
        etNotesTitle.setFocusable(false);
        etNotesBody.setFocusable(false);
        etNotesTitle.setClickable(false);
        etNotesBody.setClickable(false);
        fabEdit.setVisibility(View.VISIBLE);


        // hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rlNotes.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                saveNotes();
                disableEditing();
                return true;
            default:
                return true;
        }
    }



    @Override
    public void onBackPressed() {
        if (editable) {
            saveNotes();
            disableEditing();
        } else {
            finish();
            overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
            super.onBackPressed();
        }
    }

}
