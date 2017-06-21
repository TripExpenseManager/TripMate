package com.tripmate;

import android.app.models.ExpenseModel;
import android.app.models.ExpensePersonModel;
import android.app.models.PersonModel;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by vinee_000 on 20-06-2017.
 */

public class DashBoardFragment extends Fragment {

    ArrayList<PersonModel> personsList = new ArrayList<>();
    ArrayList<ExpensePersonModel> expensePersonArrayList = new ArrayList<>();
    ArrayList<ExpenseModel> expenseModelArrayList = new ArrayList<>();

    String trip_id;
    RecyclerView rvDashBoard;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        personsList = MainActivity.AppBase.getPersons(trip_id);
        expenseModelArrayList = MainActivity.AppBase.getAllExpenses(trip_id);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View customview = inflater.inflate(R.layout.fragment_dashboard, container, false);
        // DashBoard
        rvDashBoard = (RecyclerView) customview.findViewById(R.id.rvDashboard);
        setUpItems();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvDashBoard.setLayoutManager(linearLayoutManager);

        DashBoardAdapter dashBoardAdapter = new DashBoardAdapter(getContext(),expensePersonArrayList);
        rvDashBoard.setAdapter(dashBoardAdapter);

        rvDashBoard.setHasFixedSize(true);



        return customview;
    }

    public  void  setUpItems(){

        for(PersonModel personModel : personsList){
            ExpensePersonModel expensePerson = new ExpensePersonModel();
            expensePerson.setName(personModel.getName());
            expensePerson.setAmountSpent(getAmountOfPerson(personModel.getName()));
            expensePerson.setAmountShared(getAmountShared(personModel.getName()));
            try {
                expensePerson.setAmountDeposit(personModel.getDeposit());
            }catch (Exception e){
                expensePerson.setAmountDeposit(0.0);
            }
            expensePersonArrayList.add(expensePerson);
        }


        Collections.sort(expensePersonArrayList,new Comparator<ExpensePersonModel>() {
            @Override
            public int compare(ExpensePersonModel o1, ExpensePersonModel o2) {
                return (int)(o2.getAmountDue()-o1.getAmountDue());
            }
        });


    }
    public Double getAmountOfPerson(String personName){
        Double amount = 0.0;

        for(ExpenseModel e : expenseModelArrayList){
            if(e.getExpBy().equalsIgnoreCase(personName))
                amount+=e.getAmount();
        }

        return  amount;
    }


    public Double getAmountShared(String personName){
        Double amount = 0.0;
        int noOfPersons = personsList.size();
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

    // Adapter for DashBoard Items
    class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.PersonHolder>{
        Context mcontext;
        ArrayList<ExpensePersonModel> expensePersonArrayList;

        public DashBoardAdapter(Context mcontext, ArrayList<ExpensePersonModel> expensePersonArrayList) {
            this.mcontext = mcontext;
            this.expensePersonArrayList = expensePersonArrayList;
        }

        public class PersonHolder extends  RecyclerView.ViewHolder{
            TextView tvRanking,tvName,tvAmount,tvDue;


            public PersonHolder(View view){
                super(view);
                tvRanking = (TextView) view.findViewById(R.id.tvRanking);
                tvName = (TextView) view.findViewById(R.id.tvName);
                tvAmount = (TextView) view.findViewById(R.id.tvAmount);
                tvDue = (TextView) view.findViewById(R.id.tvDue);
            }
        }


        @Override
        public PersonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_dashboard, parent, false);
            return  new PersonHolder(v);
        }

        @Override
        public void onBindViewHolder(PersonHolder holder, int position) {
            ExpensePersonModel expensePerson = expensePersonArrayList.get(position);
            holder.tvRanking.setText(position+1+"");
            holder.tvName.setText(expensePerson.getName());
            holder.tvAmount.setText(expensePerson.getAmountSpent()+"");

            if(expensePerson.getAmountDue()>=0){
                holder.tvDue.setText("+" + expensePerson.getAmountDue()+"");
                holder.tvDue.setTextColor(Color.GREEN);
            }else{
                holder.tvDue.setText(expensePerson.getAmountDue()+"");
                holder.tvDue.setTextColor(Color.RED);
            }

        }

        @Override
        public int getItemCount() {
            return expensePersonArrayList.size();
        }


    }
}

