package com.tripmate;

import android.app.models.NotesModel;
import android.app.models.TodoModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;
import java.util.regex.Pattern;


public class CheckListActivityNew extends AppCompatActivity implements OnStartDragListener {

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
    boolean isSaved = false;

    ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));

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
        todosAdapter = new TodosAdapter(this,unCompletedTodosArrayList,this);
        rvTodos.setAdapter(todosAdapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(todosAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvTodos);

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


    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
           mItemTouchHelper.startDrag(viewHolder);
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
        boolean empty = true;
        for(TodoModel todoModel : completedTodosArrayList){
            if(!todoModel.getName().equalsIgnoreCase("")){
                empty = false;
                break;
            }
        }
        if(empty) {
            for (TodoModel todoModel : unCompletedTodosArrayList) {
                if (!todoModel.getName().equalsIgnoreCase("")) {
                    empty = false;
                    break;
                }
            }
        }

        if(empty){
            if(!etNotesTitle.getText().toString().equalsIgnoreCase("")){
                empty = false;
            }
        }
        allTodos.addAll(unCompletedTodosArrayList);
        allTodos.addAll(completedTodosArrayList);
        notesContent = encryptTodos(allTodos);

        Log.i("encrypt",notesContent);

        if(editOrAdd.equals("add") && !isSaved && !empty) {
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

            if(!empty) {
                dataBaseHelper.updateNotes(notesModel);
            }else {
                dataBaseHelper.deleteNotes(notesModel);
            }
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



    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private final ItemTouchHelperAdapter mAdapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                      int actionState) {
            // We only want the active item
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof ItemTouchHelperViewHolder) {
                    ItemTouchHelperViewHolder itemViewHolder =
                            (ItemTouchHelperViewHolder) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }

            super.onSelectedChanged(viewHolder, actionState);
        }
        @Override
        public void clearView(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                ItemTouchHelperViewHolder itemViewHolder =
                        (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemClear();
            }
        }

    }



    class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.TodoViewHolder> implements ItemTouchHelperAdapter{
        Context context;
        ArrayList<TodoModel> todosList = new ArrayList<>();
        int posOfFocus = -1;
        OnStartDragListener mDragStartListener;


        @Override
        public void onItemDismiss(int position) {
            unCompletedTodosArrayList.remove(position);
            notifyItemRemoved(position);
            if(unCompletedTodosArrayList.size() == 0){
                tvAddTodo.setVisibility(View.VISIBLE);
            }
            posOfFocus = position;
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(unCompletedTodosArrayList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(unCompletedTodosArrayList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }


        public TodosAdapter(Context context, ArrayList<TodoModel> todosList, OnStartDragListener mDragStartListener) {
            this.context = context;
            this.todosList = todosList;
            this.mDragStartListener = mDragStartListener;
        }

        class TodoViewHolder extends  RecyclerView.ViewHolder  implements ItemTouchHelperViewHolder{
            CheckBox cbTodo;
            ImageView ivCancelTodo;
            TextView tvTodoDragger;
            EditText etTodo;
            TodoViewHolder(View itemView) {
                super(itemView);
                cbTodo = (CheckBox) itemView.findViewById(R.id.cbTodo);
                ivCancelTodo = (ImageView) itemView.findViewById(R.id.ivCancelTodo);
                etTodo = (EditText) itemView.findViewById(R.id.etTodo);
                tvTodoDragger = (TextView) itemView.findViewById(R.id.tvTodoDragger);
            }
            @Override
            public void onItemSelected() {
                itemView.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
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

            if(posOfFocus!= -1 && posOfFocus==position && posOfFocus>0 && posOfFocus<unCompletedTodosArrayList.size()) {
                holder.etTodo.post(new Runnable() {
                    @Override
                    public void run() {
                        if (holder.etTodo.requestFocus()) {
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            InputMethodManager inputMethodManager = (InputMethodManager) holder.etTodo.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(holder.etTodo, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                });
                posOfFocus = -1;
            }

            holder.tvTodoDragger.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) ==
                            MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });

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
                    posOfFocus = holder.getAdapterPosition();


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
                      posOfFocus = holder.getAdapterPosition()+1;
                  }
                  return true;
              }
          });



            holder.etTodo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) {
                        holder.ivCancelTodo.setVisibility(View.VISIBLE);
                        holder.etTodo.setSelection(holder.etTodo.getText().length());
                    }
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
            TextView tvTodoDragger;
            EditText etTodo;
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
                    if(hasFocus) {
                        holder.ivCancelTodo.setVisibility(View.VISIBLE);
                        holder.etTodo.setSelection(holder.etTodo.getText().length());
                    }
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
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        saveCheckList();
        isSaved =true;
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
