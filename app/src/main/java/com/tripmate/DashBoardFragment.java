package com.tripmate;

import android.app.models.PersonWiseExpensesSummaryModel;
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

/**
 * Created by vinee_000 on 20-06-2017.
 */

public class DashBoardFragment extends Fragment {

    ArrayList<PersonWiseExpensesSummaryModel> expensePersonArrayList = new ArrayList<>();

    String trip_id;
    RecyclerView rvDashBoard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View customview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        expensePersonArrayList = dataBaseHelper.getPersonWiseExpensesSummaryForDashboard(trip_id);

        // DashBoard
        rvDashBoard = (RecyclerView) customview.findViewById(R.id.rvDashboard);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvDashBoard.setLayoutManager(linearLayoutManager);

        DashBoardAdapter dashBoardAdapter = new DashBoardAdapter(getContext(),expensePersonArrayList);
        rvDashBoard.setAdapter(dashBoardAdapter);

        return customview;
    }

    @Override
    public void onResume() {
        super.onResume();

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        expensePersonArrayList = dataBaseHelper.getPersonWiseExpensesSummaryForDashboard(trip_id);
        DashBoardAdapter dashBoardAdapter = new DashBoardAdapter(getContext(),expensePersonArrayList);
        if(rvDashBoard!=null){
            rvDashBoard.setAdapter(dashBoardAdapter);
        }

    }

    // Adapter for DashBoard Items
    class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.PersonHolder>{
        Context mcontext;
        ArrayList<PersonWiseExpensesSummaryModel> expensePersonArrayList;

        public DashBoardAdapter(Context mcontext, ArrayList<PersonWiseExpensesSummaryModel> expensePersonArrayList) {
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
            PersonWiseExpensesSummaryModel expensePerson = expensePersonArrayList.get(position);
            holder.tvRanking.setText(position+1+"");
            holder.tvName.setText(expensePerson.getName());
            holder.tvAmount.setText(expensePerson.getTotalAmountGiven()+"");

            if(expensePerson.getTotalAmountRemaining()>=0){
                holder.tvDue.setText("+" + expensePerson.getTotalAmountRemaining());
                holder.tvDue.setTextColor(getResources().getColor(R.color.green));
            }else{
                holder.tvDue.setText(expensePerson.getTotalAmountRemaining()+"");
                holder.tvDue.setTextColor(getResources().getColor(R.color.red));
            }

        }

        @Override
        public int getItemCount() {
            return expensePersonArrayList.size();
        }


    }
}

