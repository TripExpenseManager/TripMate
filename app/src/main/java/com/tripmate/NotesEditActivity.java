package com.tripmate;

import android.app.AlertDialog;
import android.app.models.NotesModel;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.UUID;

public class NotesEditActivity extends AppCompatActivity {

    RelativeLayout rlNotes;
    EditText etNotesTitle,etNotesBody;
    int mYear,mMonth,mDay;;
    String tripId,notesId;
    String editOrAdd;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!= null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rlNotes = (RelativeLayout) findViewById(R.id.rlNotes);
        etNotesTitle = (EditText) findViewById(R.id.etNotesTitle);
        etNotesBody = (EditText) findViewById(R.id.etNotesBody);
        etNotesTitle.setEnabled(false);
        etNotesBody.setEnabled(false);

        tripId = getIntent().getStringExtra("tripId");
        editOrAdd = getIntent().getStringExtra("editOrAdd");
        if(editOrAdd.equals("add")){
            etNotesTitle.setText("");
            etNotesBody.setText("");
            etNotesTitle.setEnabled(true);
            etNotesBody.setEnabled(true);
            etNotesTitle.setHint("Title");
            etNotesBody.setHint("Note");

        }else {
            notesId = getIntent().getStringExtra("notesId");
            DataBaseHelper dataBaseHelper = new DataBaseHelper(NotesEditActivity.this);
            NotesModel notesModel = dataBaseHelper.getNotes(tripId,notesId);

            if(editOrAdd.equals("edit")){
                etNotesTitle.setEnabled(true);
                etNotesBody.setEnabled(true);
            }

            if(notesModel!=null){
                etNotesTitle.setText(notesModel.getNote_Title());
                etNotesBody.setText(notesModel.getNote_Body());
            }else{
                etNotesTitle.setText("");
                etNotesBody.setText("");
                etNotesTitle.setHint("Title");
                etNotesBody.setHint("Body");
            }

        }

        setColors();





    }

    public void setColors(){
        toolbar.setBackgroundColor(Color.parseColor("#00BFFF"));
        rlNotes.setBackgroundColor(Color.parseColor("#FFFFFF"));

    }


    public void saveNotes(){
        // Getting Current Date and Time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        // Setting Default Time and Date
        String notesDate = mDay + "-" + (mMonth + 1) + "-" + mYear;

        if(editOrAdd.equals("add")) {
            NotesModel notesModel = new NotesModel();
            notesModel.setNote_TripId(tripId);
            notesModel.setNote_Id("Notes" + UUID.randomUUID().toString());
            notesModel.setNote_Title(etNotesTitle.getText().toString());
            notesModel.setNote_Body(etNotesBody.getText().toString());
            notesModel.setNote_ContentType(1);
            notesModel.setNote_Date(notesDate);
            notesModel.setNote_ContentStatus("Notes");

            DataBaseHelper dataBaseHelper = new DataBaseHelper(NotesEditActivity.this);
            dataBaseHelper.addNotes(notesModel);

            Toast.makeText(getApplicationContext(),"Notes Added Successfully",Toast.LENGTH_SHORT).show();
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
            finish();
        }
    }

    public  void  editNotes(){
        etNotesTitle.setEnabled(true);
        etNotesBody.setEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_edit,menu);
        return  true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save :
                saveNotes();
                return true;
            case R.id.action_edit :
                editNotes();
                return true;

            default:
                return true;
        }
    }



    @Override
    public void onBackPressed() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(NotesEditActivity.this);

        dialog.setMessage("Do you want to exit without saving the note?");
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
