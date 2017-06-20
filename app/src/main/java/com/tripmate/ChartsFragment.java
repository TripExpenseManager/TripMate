package com.tripmate;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.tripmate.R.id.pieChart;

/**
 * Created by vinee_000 on 20-06-2017.
 */

public class ChartsFragment extends Fragment{

    ArrayList<PersonModel> personsList = new ArrayList<>();
    ArrayList<ExpenseModel> expenseModelArrayList = new ArrayList<>();
    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<>();

    // Graph Items for 3 Spinner Items
    ArrayList<GraphItem> personGraphItems = new ArrayList<>();
    ArrayList<GraphItem> categoryGraphItems = new ArrayList<>();
    ArrayList<GraphItem> dateGraphItems = new ArrayList<>();

    // Stats in form of Graph && list
    RecyclerView rvGraphItems;
    Spinner spCategory;
    ArrayList<String> spinnerItems = new ArrayList<>();
    PieChart pieChart;
    ArrayList<Integer> colors;

    // Trip Regarding
    Double tripTotalAmount;
    String trip_id;



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
        View customview = inflater.inflate(R.layout.fragment_charts, container, false);

        // Setting up Spinner
        spCategory = (Spinner) customview.findViewById(R.id.spCategory);
        spinnerItems.add("Person"); spinnerItems.add("Category"); spinnerItems.add("Date");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_dropdown_item_1line,spinnerItems);
        spCategory.setAdapter(spinnerAdapter);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category =  spinnerItems.get(position);
                switch (category){
                    case "Person":
                        populateGraph(personGraphItems,"Persons");
                        break;
                    case "Category":
                        populateGraph(categoryGraphItems,"Category");
                        break;
                    case "Date":
                        populateGraph(dateGraphItems,"Date");
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Populate all the data from Database
        populateData();

        // Setting Colors
        colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        // Charts
        pieChart = (PieChart) customview.findViewById(R.id.pieChart);


        //Setting Legend;
        Legend l = pieChart.getLegend();
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Total Expenses \n " + tripTotalAmount);
        pieChart.animateY(1400);

        rvGraphItems = (RecyclerView) customview.findViewById(R.id.rvGraphItems);



        return customview;
    }

    public void populateGraph(ArrayList<GraphItem> graphItems , String Label){
        ArrayList<PieEntry> yValues = new ArrayList<>();
        for(GraphItem graphItem : graphItems) {
            yValues.add(new PieEntry(graphItem.getAmount().floatValue(),graphItem.getName()));
        }

        PieDataSet pieDataSet = new PieDataSet(yValues,Label);
        pieDataSet.setColors(colors);
        pieDataSet.setLabel("Expenses");

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.animateY(1400);

        GraphItemAdapter graphItemAdapter = new GraphItemAdapter(getContext(),graphItems,Label);
        rvGraphItems.setAdapter(graphItemAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvGraphItems.setLayoutManager(linearLayoutManager);

    }


    // Adapter for Graph Items
    class GraphItemAdapter extends RecyclerView.Adapter<GraphItemAdapter.GraphItemHolder>{
        Context mcontext;
        ArrayList<GraphItem> graphItemArrayList;
        String label;

        public GraphItemAdapter(Context mcontext, ArrayList<GraphItem> graphItemArrayList,String label) {
            Collections.sort(graphItemArrayList, new Comparator<GraphItem>() {
                @Override
                public int compare(GraphItem o1, GraphItem o2) {
                    return (int)(o2.getAmount()-o1.getAmount());
                }
            });
            this.mcontext = mcontext;
            this.graphItemArrayList = graphItemArrayList;
            this.label=label;
        }

        public class GraphItemHolder extends  RecyclerView.ViewHolder{
            TextView tvRanking,tvName,tvAmount,tvPercent;


            public GraphItemHolder(View view){
                super(view);
                tvRanking = (TextView) view.findViewById(R.id.tvRanking);
                tvName = (TextView) view.findViewById(R.id.tvName);
                tvAmount = (TextView) view.findViewById(R.id.tvAmount);
                tvPercent = (TextView) view.findViewById(R.id.tvPercent);
            }
        }


        @Override
        public GraphItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_stats, parent, false);
            return  new GraphItemHolder(v);
        }

        @Override
        public void onBindViewHolder(GraphItemHolder holder, int position) {
            GraphItem graphItem = graphItemArrayList.get(position);
            holder.tvRanking.setText(position+1+"");
            holder.tvName.setText(graphItem.getName());
            holder.tvAmount.setText(graphItem.getAmount()+"");
            Double percent = ((graphItem.getAmount()/tripTotalAmount)*100);
            long percentRound = Math.round(percent*100);
            holder.tvPercent.setText((double)percentRound/100+"%");

        }

        @Override
        public int getItemCount() {
            return graphItemArrayList.size();
        }


    }

    public void populateData(){
        tripTotalAmount = 3500.00;

        // Graph Items Persons
        for(PersonModel personModel : personsList){
            GraphItem graphItemPerson = new GraphItem();
            graphItemPerson.setName(personModel.getName());
            graphItemPerson.setAmount(getAmountOfPerson(personModel.getName()));
            personGraphItems.add(graphItemPerson);
        }

        personGraphItems.add(new GraphItem("Vineeth",1500.00));
        personGraphItems.add(new GraphItem("Krishna",2000.00));
        personGraphItems.add(new GraphItem("Sai",500.00));

        // Graph Items Category
        for(String category : categoryList){
            GraphItem graphItemCategory = new GraphItem();
            graphItemCategory.setName(category);
            graphItemCategory.setAmount(getAmountOfPerson(category));
            categoryGraphItems.add(graphItemCategory);
        }


        categoryGraphItems.add(new GraphItem("Food",1500.00));
        categoryGraphItems.add(new GraphItem("Tickets",2000.00));
        categoryGraphItems.add(new GraphItem("Hotel",500.00));

        // Graph Items Date
        for(String date : dateList){
            GraphItem graphItemDate = new GraphItem();
            graphItemDate.setName(date);
            graphItemDate.setAmount(getAmountOfPerson(date));
            dateGraphItems.add(graphItemDate);
        }

        dateGraphItems.add(new GraphItem("17-09-17",1500.00));
        dateGraphItems.add(new GraphItem("18-09-17",2000.00));
        dateGraphItems.add(new GraphItem("19-09-17",500.00));



    }
    public Double getAmountOfPerson(String personName){
        Double amount = 0.0;

        for(ExpenseModel e : expenseModelArrayList){
            if(e.getExpBy().equalsIgnoreCase(personName))
                amount+=e.getAmount();
        }

        return  amount;
    }

    public Double getAmountOfCategory(String category){
        Double amount = 0.0;

        for(ExpenseModel e : expenseModelArrayList){
            if(e.getCategory().equalsIgnoreCase(category))
                amount+=e.getAmount();
        }

        return  amount;
    }

    public Double getAmountOfDate(String date){
        Double amount = 0.0;

        for(ExpenseModel e : expenseModelArrayList){
            if(e.getDate().equalsIgnoreCase(date))
                amount+=e.getAmount();
        }

        return  amount;
    }

}
