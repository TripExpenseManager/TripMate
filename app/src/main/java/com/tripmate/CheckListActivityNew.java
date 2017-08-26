package com.tripmate;

import android.app.AlertDialog;
import android.app.models.NotesModel;
import android.app.models.TodoModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.DialogInterface.OnKeyListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Pattern;

import Libraries.DragSortRecycler;

public class CheckListActivityNew extends AppCompatActivity {

    EditText etNotesTitle;
    TextView tvAddTodo,tvTitleCompletedTodos;
    RecyclerView rvTodos,rvCompletedTodos;
    NestedScrollView nswChecklist;
    ImageView ivShowCompleted;
    LinearLayout llTickedItems;
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
    Boolean showCompleted=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_new);

        etNotesTitle = (EditText) findViewById(R.id.etNotesTitle);
        rvTodos = (RecyclerView)findViewById(R.id.rvTodos);
        tvAddTodo = (TextView) findViewById(R.id.tvAddTodo);
        ivShowCompleted = (ImageView) findViewById(R.id.ivShowCompleted);
        tvTitleCompletedTodos = (TextView) findViewById(R.id.tvTitleCompletedTodos);
        rvCompletedTodos = (RecyclerView) findViewById(R.id.rvCompletedTodos);
        llTickedItems = (LinearLayout) findViewById(R.id.llTickedItems);
        nswChecklist  = (NestedScrollView) findViewById(R.id.nswChecklist);

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
            TodoModel todoModel = new TodoModel();
            unCompletedTodosArrayList.add(todoModel);
        }else  {
            notesId = getIntent().getStringExtra("notesId");
            DataBaseHelper dataBaseHelper = new DataBaseHelper(CheckListActivityNew.this);
            NotesModel notesModel = dataBaseHelper.getNotes(tripId,notesId);
            notesContent = notesModel.getNote_Body();
            if(!notesContent.trim().equals(""))
                seperateTodosBasedOnStatus(notesContent);
            if(completedTodosArrayList.size()==1){
                tvTitleCompletedTodos.setText(completedTodosArrayList.size()+ " Ticked Item");
            }else {
                tvTitleCompletedTodos.setText(completedTodosArrayList.size() + " Ticked Items");
            }
            if (unCompletedTodosArrayList.size()==0){
                tvAddTodo.setVisibility(View.VISIBLE);
            }
            etNotesTitle.setText(notesModel.getNote_Title());
            Log.d("note ",notesContent);

        }



        rvTodos.setLayoutManager(new LinearLayoutManager(this));
        todosAdapter = new TodosAdapter(this,unCompletedTodosArrayList);
        rvTodos.setAdapter(todosAdapter);

        rvCompletedTodos.setLayoutManager(new LinearLayoutManager(this));
        completedTodosAdapter = new CompletedTodosAdapter(this,completedTodosArrayList);
        rvCompletedTodos.setAdapter(completedTodosAdapter);


        llTickedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showCompleted){
                    ivShowCompleted.setImageDrawable(getResources().getDrawable(R.drawable.icon_arrow_up));
                    rvCompletedTodos.setVisibility(View.VISIBLE);
                    showCompleted = !showCompleted;
                }else{
                    ivShowCompleted.setImageDrawable(getResources().getDrawable(R.drawable.icon_arrow_down));
                    rvCompletedTodos.setVisibility(View.GONE);
                    showCompleted = !showCompleted;
                }


            }
        });



        //for uncompleted todos
       rvTodos.setItemAnimator(null);

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.tvTodoDragger);
        //dragSortRecycler.setLeftDragArea((int)px);
        dragSortRecycler.setFloatingAlpha(0.4f);
        dragSortRecycler.setFloatingBgColor(0x800000FF);
        dragSortRecycler.setAutoScrollSpeed(0.3f);
        dragSortRecycler.setAutoScrollWindow(0.1f);

        dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMoved(int from, int to) {
                Log.d("DragDrop", "onItemMoved " + from + " to " + to);
                TodoModel todoModel = unCompletedTodosArrayList.remove(from);
                unCompletedTodosArrayList.add(to, todoModel);
                todosAdapter.notifyDataSetChanged();
                saveCheckList();
                //notifyItemMoved does work, but it makes the list scroll pos jump a little when dragging near the top or bottom
                //adapter.notifyItemMoved(from,to);
            }
        });

        dragSortRecycler.setOnDragStateChangedListener(new DragSortRecycler.OnDragStateChangedListener() {
            @Override
            public void onDragStart() {
                Log.d("DragDrop", "Drag Start");
                nswChecklist.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            }

            @Override
            public void onDragStop() {
                Log.d("DragDrop", "Drag Stop");
                nswChecklist.setOnTouchListener(null);
            }
        });;



        rvTodos.addItemDecoration(dragSortRecycler);
        rvTodos.addOnItemTouchListener(dragSortRecycler);
        rvTodos.setOnScrollListener(dragSortRecycler.getScrollListener());



        if(completedTodosArrayList.size()==0){
            llTickedItems.setVisibility(View.GONE);
            rvCompletedTodos.setVisibility(View.GONE);
        }


        tvAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unCompletedTodosArrayList.size() != 0 &&
                        unCompletedTodosArrayList.get(unCompletedTodosArrayList.size()-1).getName().equalsIgnoreCase("")){
                    tvAddTodo.setVisibility(View.VISIBLE);
                }else {
                    TodoModel todoModel = new TodoModel();
                    unCompletedTodosArrayList.add(todoModel);
                    todosAdapter.notifyItemInserted(unCompletedTodosArrayList.size() - 1);
                    tvAddTodo.setVisibility(View.GONE);
                }
            }
        });
    }


    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }


    public void saveCheckList(){
        Long datevalue = Calendar.getInstance().getTimeInMillis();
        String notesDate = String.valueOf(datevalue);

        ArrayList<TodoModel> allTodos = new ArrayList<>();
        allTodos.addAll(unCompletedTodosArrayList);
        allTodos.addAll(completedTodosArrayList);
        notesContent = encryptTodos(allTodos);


        if(editOrAdd.equals("add")) {
            NotesModel notesModel = new NotesModel();
            notesModel.setNote_TripId(tripId);
            String note_id = "Notes" + UUID.randomUUID().toString();
            notesModel.setNote_Id(note_id);
            notesModel.setNote_Title(etNotesTitle.getText().toString()); // needs to be updated()// TODO: 06-07-2017
            notesModel.setNote_Body(notesContent);
            notesModel.setNote_ContentType(2);
            notesModel.setNote_Date(notesDate);
            notesModel.setNote_ContentStatus("CheckList");

            DataBaseHelper dataBaseHelper = new DataBaseHelper(CheckListActivityNew.this);
            dataBaseHelper.addNotes(notesModel);

        }else{
            NotesModel notesModel = new NotesModel();
            notesModel.setNote_TripId(tripId);
            notesModel.setNote_Id(notesId);
            notesModel.setNote_Title(etNotesTitle.getText().toString()); // needs to be updated()// TODO: 06-07-2017
            notesModel.setNote_Body(notesContent);
            notesModel.setNote_ContentType(2);
            notesModel.setNote_Date(notesDate);
            notesModel.setNote_ContentStatus("CheckList");

            DataBaseHelper dataBaseHelper = new DataBaseHelper(CheckListActivityNew.this);
            dataBaseHelper.updateNotes(notesModel);
        }
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
            if(todoModel.getName()!=null)
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

        TodosAdapter(Context context, ArrayList<TodoModel> todosList) {
            this.context = context;
            this.todosList = todosList;
        }

        class TodoViewHolder extends  RecyclerView.ViewHolder{
            CheckBox cbTodo;
            ImageView ivCancelTodo;
            TextView tvTodoDragger,etTodo;
            TodoViewHolder(View itemView) {
                super(itemView);
                cbTodo = (CheckBox) itemView.findViewById(R.id.cbTodo);
                ivCancelTodo = (ImageView) itemView.findViewById(R.id.ivCancelTodo);
                etTodo = (EditText) itemView.findViewById(R.id.etTodo);
                tvTodoDragger = (TextView) itemView.findViewById(R.id.tvTodoDragger);
            }
        }

        @Override
        public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View customView = getLayoutInflater().inflate(R.layout.list_item_todo_new,parent,false);
            return new TodoViewHolder(customView);

        }

        @Override
        public void onBindViewHolder(final TodoViewHolder holder, int position) {
            holder.etTodo.setText(todosList.get(position).getName());
            holder.cbTodo.setChecked(false);
            holder.cbTodo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        unCompletedTodosArrayList.get(holder.getAdapterPosition()).setCompleted(true);
                        completedTodosArrayList.add(unCompletedTodosArrayList.get(holder.getAdapterPosition()));
                        unCompletedTodosArrayList.remove(holder.getAdapterPosition());
                        todosAdapter.notifyItemRemoved(holder.getAdapterPosition());
                        todosAdapter.notifyItemRangeChanged(holder.getAdapterPosition(),unCompletedTodosArrayList.size());
                        completedTodosAdapter.notifyItemInserted(completedTodosArrayList.size()-1);
                        llTickedItems.setVisibility(View.VISIBLE);
                        rvCompletedTodos.setVisibility(View.VISIBLE);
                        if(completedTodosArrayList.size()==1){
                            tvTitleCompletedTodos.setText(completedTodosArrayList.size()+ " Ticked Item");
                        }else {
                            tvTitleCompletedTodos.setText(completedTodosArrayList.size() + " Ticked Items");
                        }
                        if (unCompletedTodosArrayList.size()==0){
                            tvAddTodo.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            holder.ivCancelTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    unCompletedTodosArrayList.remove(holder.getAdapterPosition());
                    todosAdapter.notifyItemRemoved(holder.getAdapterPosition());
                    todosAdapter.notifyItemRangeChanged(holder.getAdapterPosition(),unCompletedTodosArrayList.size());


                    if(unCompletedTodosArrayList.size() == 0){
                        tvAddTodo.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.etTodo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(holder.etTodo.getText().toString().equalsIgnoreCase("")){

                    }else {
                        unCompletedTodosArrayList.get(holder.getAdapterPosition()).setName(holder.etTodo.getText().toString());
                        tvAddTodo.setVisibility(View.VISIBLE);
                    }
                }
            });


         /*  holder.etTodo.setOnKeyListener(new View.OnKeyListener()
            {
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        switch (keyCode)
                        {
                            case KeyEvent.KEYCODE_ENTER:
                                if(!(holder.etTodo.getText().toString().equalsIgnoreCase(""))){
                                    TodoModel todoModel = new TodoModel();
                                    unCompletedTodosArrayList.add(holder.getAdapterPosition()+1,todoModel);
                                    todosAdapter.notifyItemInserted(holder.getAdapterPosition()+1);
                                    todosAdapter.notifyItemRangeChanged(holder.getAdapterPosition()+1,unCompletedTodosArrayList.size());
                                }
                                return true;

                            default:
                                break;
                        }
                    }
                    return true;
                }
            }); */

       holder.etTodo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
              @Override
              public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                  if(!(holder.etTodo.getText().toString().equalsIgnoreCase(""))){
                      TodoModel todoModel = new TodoModel();
                      unCompletedTodosArrayList.add(holder.getAdapterPosition()+1,todoModel);
                      todosAdapter.notifyItemInserted(holder.getAdapterPosition()+1);
                      todosAdapter.notifyItemRangeChanged(holder.getAdapterPosition()+1,unCompletedTodosArrayList.size());
                  }
                  return true;
              }
          });



            holder.etTodo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus)
                        holder.ivCancelTodo.setVisibility(View.VISIBLE);
                    else {
                        holder.ivCancelTodo.setVisibility(View.GONE);
                    }
                }
            });



        }


        @Override
        public int getItemCount() {
            return todosList.size();
        }
    }

    class CompletedTodosAdapter extends RecyclerView.Adapter<CompletedTodosAdapter.CompletedTodoViewHolder>{

        Context context;
        ArrayList<TodoModel> completedTodosList = new ArrayList<>();

        CompletedTodosAdapter(Context context, ArrayList<TodoModel> completedTodosList) {
            this.context = context;
            this.completedTodosList = completedTodosList;
        }

        class CompletedTodoViewHolder extends  RecyclerView.ViewHolder{

            CheckBox cbTodo;
            ImageView ivCancelTodo;
            TextView tvTodoDragger,etTodo;
            CompletedTodoViewHolder(View itemView) {
                super(itemView);
                cbTodo = (CheckBox) itemView.findViewById(R.id.cbTodo);
                ivCancelTodo = (ImageView) itemView.findViewById(R.id.ivCancelTodo);
                etTodo = (EditText) itemView.findViewById(R.id.etTodo);
                tvTodoDragger = (TextView) itemView.findViewById(R.id.tvTodoDragger);
            }
        }

        @Override
        public CompletedTodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View customView = getLayoutInflater().inflate(R.layout.list_item_completed_todo_new,parent,false);
            return new CompletedTodoViewHolder(customView);

        }

        @Override
        public void onBindViewHolder(final CompletedTodoViewHolder holder,int position) {
            holder.etTodo.setText(completedTodosList.get(holder.getAdapterPosition()).getName());
            holder.cbTodo.setChecked(true);
            holder.cbTodo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!(isChecked)){
                        completedTodosArrayList.get(holder.getAdapterPosition()).setCompleted(false);
                        unCompletedTodosArrayList.add(completedTodosArrayList.get(holder.getAdapterPosition()));
                        completedTodosArrayList.remove(holder.getAdapterPosition());
                        completedTodosAdapter.notifyItemRemoved(holder.getAdapterPosition());
                        completedTodosAdapter.notifyItemRangeChanged(holder.getAdapterPosition(),completedTodosArrayList.size());
                        todosAdapter.notifyItemInserted(unCompletedTodosArrayList.size()-1);
                        if(completedTodosArrayList.size()==0){
                            llTickedItems.setVisibility(View.GONE);
                            rvCompletedTodos.setVisibility(View.GONE);
                        }

                        if(completedTodosArrayList.size()==1){
                            tvTitleCompletedTodos.setText(completedTodosArrayList.size()+ " Ticked Item");
                        }else {
                            tvTitleCompletedTodos.setText(completedTodosArrayList.size() + " Ticked Items");
                        }
                    }
                } });

            holder.etTodo.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);


            holder.ivCancelTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    completedTodosArrayList.remove(holder.getAdapterPosition());
                    completedTodosAdapter.notifyItemRemoved(holder.getAdapterPosition());
                    completedTodosAdapter.notifyItemRangeChanged(holder.getAdapterPosition(),completedTodosArrayList.size());

                    if(completedTodosArrayList.size()==1){
                        tvTitleCompletedTodos.setText(completedTodosArrayList.size()+ " Ticked Item");
                    }else {
                        tvTitleCompletedTodos.setText(completedTodosArrayList.size() + " Ticked Items");
                    }
                    if (completedTodosArrayList.size()==0){
                        llTickedItems.setVisibility(View.GONE);
                    }else{
                        llTickedItems.setVisibility(View.VISIBLE);
                    }
                }
            });


            holder.etTodo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(holder.etTodo.getText().toString().equalsIgnoreCase("")){

                    }else {
                        completedTodosArrayList.get(holder.getAdapterPosition()).setName(holder.etTodo.getText().toString());
                    }
                }
            });



            holder.etTodo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus)
                        holder.ivCancelTodo.setVisibility(View.VISIBLE);
                    else {
                        holder.ivCancelTodo.setVisibility(View.GONE);
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return completedTodosList.size();
        }
    }

    @Override
    public void onBackPressed() {
        saveCheckList();
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    @Override
    protected void onPause() {
        saveCheckList();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
