package com.tripmate;

import android.app.models.NotesModel;
import android.app.models.TodoModel;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CheckListActivity extends AppCompatActivity {

    RecyclerView rvTodos,rvCompletedTodos;
    TextView tvDisplayCompleted;
    ArrayList<TodoModel> completedTodosArrayList = new ArrayList<>();
    ArrayList<TodoModel> todosArrayList = new ArrayList<>();
    TodosAdapter todosAdapter;
    CompletedTodosAdapter completedTodosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        rvTodos = (RecyclerView)findViewById(R.id.rvTodos);
        rvCompletedTodos = (RecyclerView) findViewById(R.id.rvCompletedTodos);
        tvDisplayCompleted = (TextView) findViewById(R.id.tvDisplayCompleted);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTodos.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        rvCompletedTodos.setLayoutManager(linearLayoutManager1);

        todosArrayList.add(new TodoModel("Go To Temple by 7am",false));
        todosArrayList.add(new TodoModel("Go To Hotel by 9am",false));

        todosAdapter = new TodosAdapter(this,todosArrayList);
        rvTodos.setAdapter(todosAdapter);

        completedTodosAdapter = new CompletedTodosAdapter(this,completedTodosArrayList);
        rvCompletedTodos.setAdapter(completedTodosAdapter);

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
            ImageView ivStarred;
            public TodoViewHolder(View itemView) {
                super(itemView);
                cbTodo = (CheckBox) itemView.findViewById(R.id.cbTodo);
                ivStarred = (ImageView) itemView.findViewById(R.id.ivStarred);


            }
        }

        @Override
        public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View customView = getLayoutInflater().inflate(R.layout.list_item_todo,parent,false);
            return new TodoViewHolder(customView);

        }

        @Override
        public void onBindViewHolder(TodoViewHolder holder, final int position) {
            holder.cbTodo.setText(todosList.get(position).getName());
            holder.cbTodo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        completedTodosArrayList.add(todosArrayList.get(position));
                        todosArrayList.remove(position);
                    todosAdapter.notifyItemRemoved(position);
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
            ImageView ivStarred;
            public CompletedTodoViewHolder(View itemView) {
                super(itemView);
                cbTodo = (CheckBox) itemView.findViewById(R.id.cbTodo);
                ivStarred = (ImageView) itemView.findViewById(R.id.ivStarred);


            }
        }

        @Override
        public CompletedTodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View customView = getLayoutInflater().inflate(R.layout.list_item_completed_todo,parent,false);
            return new CompletedTodoViewHolder(customView);

        }

        @Override
        public void onBindViewHolder(CompletedTodoViewHolder holder, int position) {
            holder.cbTodo.setText(completedTodosList.get(position).getName());




        }

        @Override
        public int getItemCount() {
            return completedTodosList.size();
        }
    }



}
