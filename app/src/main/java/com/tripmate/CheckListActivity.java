package com.tripmate;

import android.app.AlertDialog;
import android.app.models.NotesModel;
import android.app.models.TodoModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import Libraries.DragSortRecycler;

public class CheckListActivity extends AppCompatActivity {

    EditText etAddTodo;
    RecyclerView rvTodos,rvCompletedTodos;
    TextView tvDisplayCompleted;
    int mYear,mMonth,mDay;
    ArrayList<TodoModel> allTodosArrayList = new ArrayList<>();
    ArrayList<TodoModel> completedTodosArrayList = new ArrayList<>();
    ArrayList<TodoModel> unCompletedTodosArrayList = new ArrayList<>();
    TodosAdapter todosAdapter;
    CompletedTodosAdapter completedTodosAdapter;
    String tripId,notesId=null;
    String editOrAdd;
    Toolbar toolbar;
    String notesContent = "";
    public static String DELIMETER_FOR_TODOS ="$*^";
    public static String DELIMETER_FOR_A_TODO ="@+&";
    boolean isEdited = false;
    final String TAG = "CheckListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        etAddTodo = (EditText) findViewById(R.id.etAddTodo);
        rvTodos = (RecyclerView)findViewById(R.id.rvTodos);
        rvCompletedTodos = (RecyclerView) findViewById(R.id.rvCompletedTodos);
        tvDisplayCompleted = (TextView) findViewById(R.id.tvDisplayCompleted);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getStringExtra("anim").equalsIgnoreCase("yes")){
            overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
        }

        if(getSupportActionBar()!= null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        tripId = getIntent().getStringExtra("tripId");
        editOrAdd = getIntent().getStringExtra("editOrAdd");
        if(editOrAdd.equals("add")){


        }else {
            notesId = getIntent().getStringExtra("notesId");
            DataBaseHelper dataBaseHelper = new DataBaseHelper(CheckListActivity.this);
            NotesModel notesModel = dataBaseHelper.getNotes(tripId,notesId);
            notesContent = notesModel.getNote_Body();
            if(!notesContent.trim().equals(""))
                seperateTodosBasedOnStatus(notesContent);
            Log.d("note ",notesContent);

            }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTodos.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        rvCompletedTodos.setLayoutManager(linearLayoutManager1);

        todosAdapter = new TodosAdapter(this,unCompletedTodosArrayList);
        rvTodos.setAdapter(todosAdapter);

        completedTodosAdapter = new CompletedTodosAdapter(this,completedTodosArrayList);
        rvCompletedTodos.setAdapter(completedTodosAdapter);


        rvCompletedTodos.setVisibility(View.GONE);

        tvDisplayCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvDisplayCompleted.getText().toString().contains("Show")){
                    rvCompletedTodos.setVisibility(View.VISIBLE);
                    tvDisplayCompleted.setText("Completed TO-DOS");
                }else {
                    rvCompletedTodos.setVisibility(View.GONE);
                    tvDisplayCompleted.setText("Show Completed TO-DOS");
                }

            }
        });
        //Adding a Todo
        etAddTodo.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            addTodo();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });



        rvTodos.setItemAnimator(null);

        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.cbTodo);
        dragSortRecycler.setFloatingAlpha(0.4f);
        dragSortRecycler.setFloatingBgColor(0x800000FF);
        dragSortRecycler.setAutoScrollSpeed(0.3f);
        dragSortRecycler.setAutoScrollWindow(0.1f);

        dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMoved(int from, int to) {
                Log.d(TAG, "onItemMoved " + from + " to " + to);
                TodoModel todoModel = unCompletedTodosArrayList.remove(from);
                unCompletedTodosArrayList.add(to, todoModel);
                todosAdapter.notifyDataSetChanged();
                //notifyItemMoved does work, but it makes the list scroll pos jump a little when dragging near the top or bottom
                //adapter.notifyItemMoved(from,to);
            }
        });

        dragSortRecycler.setOnDragStateChangedListener(new DragSortRecycler.OnDragStateChangedListener() {
            @Override
            public void onDragStart() {
                Log.d(TAG, "Drag Start");
            }

            @Override
            public void onDragStop() {
                Log.d(TAG, "Drag Stop");
            }
        });

        rvTodos.addItemDecoration(dragSortRecycler);
        rvTodos.addOnItemTouchListener(dragSortRecycler);

        DragSortRecycler dragSortRecyclerCompleted = new DragSortRecycler();
        dragSortRecyclerCompleted.setViewHandleId(R.id.cbTodo);
        dragSortRecyclerCompleted.setFloatingAlpha(0.4f);
        dragSortRecyclerCompleted.setFloatingBgColor(0x800000FF);
        dragSortRecyclerCompleted.setAutoScrollSpeed(0.3f);
        dragSortRecyclerCompleted.setAutoScrollWindow(0.1f);

        dragSortRecyclerCompleted.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMoved(int from, int to) {
                Log.d(TAG, "onItemMoved " + from + " to " + to);
                TodoModel todoModel = completedTodosArrayList.remove(from);
                completedTodosArrayList.add(to, todoModel);
                completedTodosAdapter.notifyDataSetChanged();
                //notifyItemMoved does work, but it makes the list scroll pos jump a little when dragging near the top or bottom
                //adapter.notifyItemMoved(from,to);
            }
        });

        dragSortRecyclerCompleted.setOnDragStateChangedListener(new DragSortRecycler.OnDragStateChangedListener() {
            @Override
            public void onDragStart() {
                Log.d(TAG, "Drag Start");
            }

            @Override
            public void onDragStop() {
                Log.d(TAG, "Drag Stop");
            }
        });

        rvCompletedTodos.addItemDecoration(dragSortRecyclerCompleted);
        rvCompletedTodos.addOnItemTouchListener(dragSortRecyclerCompleted);
    }


    public void addTodo(){
        if(etAddTodo.getText().toString().trim().equals("")){}
        else
            unCompletedTodosArrayList.add(new TodoModel(etAddTodo.getText().toString().trim(),false));

        todosAdapter.notifyItemInserted(unCompletedTodosArrayList.size());
        etAddTodo.setText("");
        isEdited = true;
        // Focus edit text after a todo is aaded // // TODO: 06-07-2017

    }

    public void saveCheckList(){
        Long datevalue = Calendar.getInstance().getTimeInMillis();
        String notesDate = String.valueOf(datevalue);

        ArrayList<TodoModel> allTodos = unCompletedTodosArrayList;
        allTodos.addAll(completedTodosArrayList);
        notesContent = encryptTodos(allTodos);


        if(editOrAdd.equals("add")) {
            NotesModel notesModel = new NotesModel();
            notesModel.setNote_TripId(tripId);
            String note_id = "Notes" + UUID.randomUUID().toString();
            notesModel.setNote_Id(note_id);
            notesModel.setNote_Title("CheckList"); // needs to be updated()// TODO: 06-07-2017
            notesModel.setNote_Body(notesContent);
            notesModel.setNote_ContentType(2);
            notesModel.setNote_Date(notesDate);
            notesModel.setNote_ContentStatus("CheckList");

            DataBaseHelper dataBaseHelper = new DataBaseHelper(CheckListActivity.this);
            dataBaseHelper.addNotes(notesModel);

            Toast.makeText(getApplicationContext(),"CheckList Added Successfully",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CheckListActivity.this,CheckListActivity.class);
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
            notesModel.setNote_Title("CheckList"); // needs to be updated()// TODO: 06-07-2017
            notesModel.setNote_Body(notesContent);
            notesModel.setNote_ContentType(2);
            notesModel.setNote_Date(notesDate);
            notesModel.setNote_ContentStatus("CheckList");

            DataBaseHelper dataBaseHelper = new DataBaseHelper(CheckListActivity.this);
            dataBaseHelper.updateNotes(notesModel);

            Toast.makeText(getApplicationContext(),"CheckList Updated Successfully",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CheckListActivity.this,CheckListActivity.class);
            intent.putExtra("tripId",tripId);
            intent.putExtra("editOrAdd","view");
            intent.putExtra("notesId",notesId);
            intent.putExtra("anim","no");
            startActivity(intent);
            finish();
        }
    }


    public void populateRecyclerViewItems(){

        /*String noteContent = " Go to Temple at 7 am" + DELIMETER_FOR_A_TODO + " T,T " + DELIMETER_FOR_TODOS +
                " Go to Hotel at 9 am" + DELIMETER_FOR_A_TODO +"T,T " + DELIMETER_FOR_TODOS +
                " Go  to Shopping at 10 am" + DELIMETER_FOR_A_TODO +"T,T " + DELIMETER_FOR_TODOS +
                " Have Lunch at 1pm" + DELIMETER_FOR_A_TODO +"F,T " + DELIMETER_FOR_TODOS +
                " Have snacks at 4pm" + DELIMETER_FOR_A_TODO +"F,T " ; */

        seperateTodosBasedOnStatus(notesContent);

        todosAdapter.notifyDataSetChanged();
        completedTodosAdapter.notifyDataSetChanged();
    }


    public void seperateTodosBasedOnStatus(String noteContent){
        ArrayList<TodoModel> todoModelArrayList = decryptTodos(noteContent);

        for(TodoModel todoModel : todoModelArrayList){
            if(todoModel.isCompleted())
                completedTodosArrayList.add(todoModel);
            else{
                unCompletedTodosArrayList.add(todoModel);
            }
        }
    }

    public String  encryptTodos(ArrayList<TodoModel> todoModels){

        String noteContent = "";

        for(TodoModel  todoModel : todoModels) {
            // Name of todos
            noteContent+=todoModel.getName().trim();
            noteContent+=DELIMETER_FOR_A_TODO;
            // Status of the todos
            if(todoModel.isCompleted())
                noteContent+="T";
            else
                noteContent+="F";

            // Delimiter after a todo is completed
            noteContent+=DELIMETER_FOR_TODOS;

        }

        return  noteContent;
    }


    public ArrayList<TodoModel>  decryptTodos(String noteContent){
        String[] todosModelsasStrings = noteContent.split(Pattern.quote(DELIMETER_FOR_TODOS));
        ArrayList<TodoModel> todoModels = new ArrayList<>();

        for(String s : todosModelsasStrings) {
           TodoModel todoModel = new TodoModel();
            String[] todo = s.split(Pattern.quote(DELIMETER_FOR_A_TODO));
            todoModel.setName(todo[0].trim());
            if(todo[1].trim().equalsIgnoreCase("t"))
                todoModel.setCompleted(true);
            else
                todoModel.setCompleted(false);
            todoModels.add(todoModel);
        }
        return  todoModels;
    }

    class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.TodoViewHolder>{

        Context context;
        ArrayList<TodoModel> todosList = new ArrayList<>();

        public TodosAdapter(Context context, ArrayList<TodoModel> todosList) {
            this.context = context;
            this.todosList = todosList;
        }

        public class TodoViewHolder extends  RecyclerView.ViewHolder{
            CheckBox cbTodo;
            public TodoViewHolder(View itemView) {
                super(itemView);
                cbTodo = (CheckBox) itemView.findViewById(R.id.cbTodo);

            }
        }

        @Override
        public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View customView = getLayoutInflater().inflate(R.layout.list_item_todo,parent,false);
            return new TodoViewHolder(customView);

        }

        @Override
        public void onBindViewHolder(final TodoViewHolder holder, final int position) {
            holder.cbTodo.setText(todosList.get(position).getName());
            holder.cbTodo.setChecked(false);
            holder.cbTodo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        unCompletedTodosArrayList.get(position).setCompleted(true);
                        completedTodosArrayList.add(unCompletedTodosArrayList.get(position));
                        unCompletedTodosArrayList.remove(position);
                        todosAdapter.notifyItemRemoved(position);
                        todosAdapter.notifyDataSetChanged();
                        completedTodosAdapter.notifyDataSetChanged();

                }
            } });

        }

        @Override
        public int getItemCount() {
            return todosList.size();
        }
    }

    class CompletedTodosAdapter extends RecyclerView.Adapter<CompletedTodosAdapter.CompletedTodoViewHolder>{

        Context context;
        ArrayList<TodoModel> completedTodosList = new ArrayList<>();

        public CompletedTodosAdapter(Context context, ArrayList<TodoModel> completedTodosList) {
            this.context = context;
            this.completedTodosList = completedTodosList;
        }

        public class CompletedTodoViewHolder extends  RecyclerView.ViewHolder{
            CheckBox cbTodo;
            public CompletedTodoViewHolder(View itemView) {
                super(itemView);
                cbTodo = (CheckBox) itemView.findViewById(R.id.cbTodo);


            }
        }

        @Override
        public CompletedTodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View customView = getLayoutInflater().inflate(R.layout.list_item_completed_todo,parent,false);
            return new CompletedTodoViewHolder(customView);

        }

        @Override
        public void onBindViewHolder(final CompletedTodoViewHolder holder, final int position) {
            holder.cbTodo.setText(completedTodosList.get(position).getName());
            holder.cbTodo.setChecked(true);

            holder.cbTodo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!(isChecked)){
                        completedTodosArrayList.get(position).setCompleted(false);
                        unCompletedTodosArrayList.add(completedTodosArrayList.get(position));
                        completedTodosArrayList.remove(position);
                        completedTodosAdapter.notifyItemRemoved(position);
                        completedTodosAdapter.notifyDataSetChanged();
                        todosAdapter.notifyDataSetChanged();

                    }
                } });



        }

        @Override
        public int getItemCount() {
            return completedTodosList.size();
        }
    }


    static Menu MenuTemp = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_edit,menu);

        menu.findItem(R.id.action_edit).setVisible(false);
        return  true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save :
                if(completedTodosArrayList.size()==0 && unCompletedTodosArrayList.size()==0){
                    Snackbar.make(findViewById(android.R.id.content),"Please enter the content of the Note", Snackbar.LENGTH_LONG).show();
                }else{
                    saveCheckList();
                }
                return true;

            case R.id.action_cancel :

                if(notesId != null && tripId != null){
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(CheckListActivity.this);
                    NotesModel notesModel = dataBaseHelper.getNotes(tripId,notesId);
                    notesContent = notesModel.getNote_Body();
                    if(!notesContent.trim().equals("")) {
                        unCompletedTodosArrayList.clear();
                        completedTodosArrayList.clear();
                        seperateTodosBasedOnStatus(notesContent);
                        todosAdapter.notifyDataSetChanged();
                        completedTodosAdapter.notifyDataSetChanged();
                    }
                }else{
                    unCompletedTodosArrayList.clear();
                    completedTodosArrayList.clear();
                    todosAdapter.notifyDataSetChanged();
                    completedTodosAdapter.notifyDataSetChanged();
                }

                return true;
            default:
                return true;
        }
    }



    @Override
    public void onBackPressed() {

        if(isEdited){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(CheckListActivity.this);

            dialog.setMessage("Do you want to exit without saving the CheckList?");
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
