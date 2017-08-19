package com.tripmate;

import android.app.AlertDialog;
import android.app.models.NotesModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

    boolean isEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getStringExtra("anim").equalsIgnoreCase("yes")){
            overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
        }

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        rlNotes = (RelativeLayout) findViewById(R.id.rlNotes);
        etNotesTitle = (EditText) findViewById(R.id.etNotesTitle);
        etNotesBody = (EditText) findViewById(R.id.etNotesBody);

        tripId = getIntent().getStringExtra("tripId");
        editOrAdd = getIntent().getStringExtra("editOrAdd");
        if(editOrAdd.equals("add")){
            etNotesTitle.setText("");
            etNotesBody.setText("");

            etNotesTitle.setFocusableInTouchMode(true);
            etNotesBody.setFocusableInTouchMode(true);
            etNotesTitle.setClickable(true);
            etNotesBody.setClickable(true);

            isEdited = true;

        }else {

            if(editOrAdd.equals("view")){
                etNotesTitle.setFocusable(false);
                etNotesTitle.setClickable(false);
                etNotesBody.setClickable(false);
                etNotesBody.setFocusable(false);
            }else{
                isEdited = true;
            }
            notesId = getIntent().getStringExtra("notesId");
            DataBaseHelper dataBaseHelper = new DataBaseHelper(NotesEditActivity.this);
            NotesModel notesModel = dataBaseHelper.getNotes(tripId,notesId);

            etNotesTitle.setText(notesModel.getNote_Title());
            etNotesBody.setText(notesModel.getNote_Body());

        }

    }




    public void saveNotes(){

        Long datevalue = Calendar.getInstance().getTimeInMillis();
        String notesDate = String.valueOf(datevalue);

        if(editOrAdd.equals("add")) {
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

            Toast.makeText(getApplicationContext(),"Notes Added Successfully",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NotesEditActivity.this,NotesEditActivity.class);
            intent.putExtra("tripId",tripId);
            intent.putExtra("editOrAdd","view");
            intent.putExtra("notesId",note_id);
            intent.putExtra("anim","no");
            startActivity(intent);
            finish();
        }else{
            NotesModel notesModel = new NotesModel();
            notesModel.setNote_TripId(tripId);
            notesModel.setNote_Id(notesId);
            notesModel.setNote_Title(etNotesTitle.getText().toString());
            notesModel.setNote_Body(etNotesBody.getText().toString());
            notesModel.setNote_ContentType(1);
            notesModel.setNote_Date(notesDate);
            notesModel.setNote_ContentStatus("Notes");

            DataBaseHelper dataBaseHelper = new DataBaseHelper(NotesEditActivity.this);
            dataBaseHelper.updateNotes(notesModel);

            Toast.makeText(getApplicationContext(),"Notes Updated Successfully",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NotesEditActivity.this,NotesEditActivity.class);
            intent.putExtra("tripId",tripId);
            intent.putExtra("editOrAdd","view");
            intent.putExtra("notesId",notesId);
            intent.putExtra("anim","no");
            startActivity(intent);
            finish();
        }
    }

    public  void  editNotes(){
        etNotesTitle.setFocusableInTouchMode(true);
        etNotesBody.setFocusableInTouchMode(true);
        etNotesTitle.setClickable(true);
        etNotesBody.setClickable(true);
    }


    static Menu MenuTemp = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_edit,menu);
        MenuTemp = menu;

        if(editOrAdd != null){
            if(editOrAdd.equals("add") || editOrAdd.equals("edit")){
                menu.findItem(R.id.action_edit).setVisible(false);
            }

            if(!editOrAdd.equals("edit")){
                menu.findItem(R.id.action_cancel).setVisible(false);
            }

            if(editOrAdd.equals("view")){
                menu.findItem(R.id.action_save).setVisible(false);
            }



        }

        return  true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save :
                if(etNotesBody.getText().toString().equalsIgnoreCase("")){
                    Snackbar.make(findViewById(android.R.id.content),"Please enter the content of the Note", Snackbar.LENGTH_LONG).show();
                }else{
                    saveNotes();
                }
                return true;
            case R.id.action_edit :
                editNotes();
                isEdited = true;
                if(MenuTemp != null){
                    MenuTemp.findItem(R.id.action_cancel).setVisible(true);
                    MenuTemp.findItem(R.id.action_save).setVisible(true);
                }
                item.setVisible(false);
                return true;
            case R.id.action_cancel :

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rlNotes.getWindowToken(), 0);

                isEdited = false;
                if(notesId != null && tripId != null){
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(NotesEditActivity.this);
                    NotesModel notesModel = dataBaseHelper.getNotes(tripId,notesId);
                    etNotesTitle.setText(notesModel.getNote_Title());
                    etNotesBody.setText(notesModel.getNote_Body());
                }
                item.setVisible(false);
                if(MenuTemp != null){
                    MenuTemp.findItem(R.id.action_save).setVisible(false);
                    MenuTemp.findItem(R.id.action_edit).setVisible(true);
                }
                etNotesTitle.setFocusable(false);
                etNotesBody.setFocusable(false);
                etNotesTitle.setClickable(false);
                etNotesBody.setClickable(false);

                return true;
            default:
                return true;
        }
    }



    @Override
    public void onBackPressed() {

        if(isEdited){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(NotesEditActivity.this);

            dialog.setMessage("Do you want to exit without saving the note?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            dialog.show();
        }else{
            finish();
            overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
        }

    }
}
