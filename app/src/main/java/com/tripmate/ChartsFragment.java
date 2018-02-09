package com.tripmate;

import android.app.models.GraphItemModel;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by vinee_000 on 20-06-2017.
 */

public class ChartsFragment extends Fragment{


    // Graph Items for 3 Spinner Items
    ArrayList<GraphItemModel> personGraphItems = new ArrayList<>();
    ArrayList<GraphItemModel> categoryGraphItems = new ArrayList<>();
    ArrayList<GraphItemModel> dateGraphItems = new ArrayList<>();

    // Stats in form of Graph && list
    RecyclerView rvGraphItems;
    Spinner spCategory;
    ArrayList<String> spinnerItems = new ArrayList<>();
    PieChart pieChart;
    ArrayList<Integer> colors;

    // Trip Regarding
    Double tripTotalAmount;
    String trip_id;

    TextView tvCurrencyNotice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View customview = inflater.inflate(R.layout.fragment_charts, container, false);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        // Setting up Spinner
        spCategory = (Spinner) customview.findViewById(R.id.spCategory);
        spinnerItems.clear();
        spinnerItems.add("Person"); spinnerItems.add("Category"); spinnerItems.add("Date");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item,spinnerItems);
        spCategory.setAdapter(spinnerAdapter);


        //getting data of for all the items in spinner
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());

        tvCurrencyNotice = (TextView) customview.findViewById(R.id.tvCurrencyNotice);
        tvCurrencyNotice.setText("*all the above values are in "+  Utils.getCorrespondingCurrencyName(dataBaseHelper.getTripData(trip_id).getTripcurrency())+" - "+ dataBaseHelper.getTripData(trip_id).getTripcurrency());


        personGraphItems = dataBaseHelper.getPersonWiseExpensesSummaryForGraphPersons(trip_id);
        categoryGraphItems = dataBaseHelper.getPersonWiseExpensesSummaryForGraphCategory(trip_id);
        dateGraphItems = dataBaseHelper.getPersonWiseExpensesSummaryForGraphDate(trip_id);
        tripTotalAmount = dataBaseHelper.getTotalExpensesAmount(trip_id);


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

        // Adding colors

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

        // Setting Legend(actually removing the legend)
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setRotationEnabled(false);
        pieChart.setCenterText("Total Expenses \n " + RoundOff(tripTotalAmount));
        pieChart.animateY(1000);

        rvGraphItems = (RecyclerView) customview.findViewById(R.id.rvGraphItems);

        return customview;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(trip_id!=null){
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
            personGraphItems = dataBaseHelper.getPersonWiseExpensesSummaryForGraphPersons(trip_id);
            categoryGraphItems = dataBaseHelper.getPersonWiseExpensesSummaryForGraphCategory(trip_id);
            dateGraphItems = dataBaseHelper.getPersonWiseExpensesSummaryForGraphDate(trip_id);
            tripTotalAmount = dataBaseHelper.getTotalExpensesAmount(trip_id);
        }

    }

    public void populateGraph(ArrayList<GraphItemModel> graphItems , String Label){
        ArrayList<PieEntry> yValues = new ArrayList<>();
        for(GraphItemModel graphItem : graphItems) {

            if(graphItem.getAmount().floatValue() != 0f){
                yValues.add(new PieEntry(graphItem.getAmount().floatValue(),graphItem.getName()));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(yValues,Label);
        pieDataSet.setColors(colors);
        pieDataSet.setLabel("Expenses");
        pieDataSet.setSliceSpace(2f);

        IValueFormatter formatter = new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return value+"";
            }
        };

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(11f);
        pieData.setValueFormatter(formatter);

        pieChart.setData(pieData);
        pieChart.animateY(1400);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvGraphItems.setLayoutManager(linearLayoutManager);

        GraphItemAdapter graphItemAdapter = new GraphItemAdapter(getContext(),graphItems,Label);
        rvGraphItems.setAdapter(graphItemAdapter);


    }

    public Double RoundOff(Double d){
        return Math.round(d * 100.0) / 100.0;
    }

    // Adapter for Graph Items
    class GraphItemAdapter extends RecyclerView.Adapter<GraphItemAdapter.GraphItemHolder>{
        Context mcontext;
        ArrayList<GraphItemModel> graphItemArrayList;
        String label;

        GraphItemAdapter(Context mcontext, ArrayList<GraphItemModel> graphItemArrayList, String label) {
            Collections.sort(graphItemArrayList, new Comparator<GraphItemModel>() {
                @Override
                public int compare(GraphItemModel o1, GraphItemModel o2) {
                    return (int)(o2.getAmount()-o1.getAmount());
                }
            });
            this.mcontext = mcontext;
            this.graphItemArrayList = graphItemArrayList;
            this.label=label;
        }

        class GraphItemHolder extends  RecyclerView.ViewHolder{
            TextView tvRanking,tvName,tvAmount,tvPercent;


            GraphItemHolder(View view){
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
            GraphItemModel graphItem = graphItemArrayList.get(position);
            holder.tvRanking.setText(position+1+"");
            holder.tvName.setText(graphItem.getName());
            holder.tvAmount.setText(graphItem.getAmount()+"");
            holder.tvPercent.setText(graphItem.getPercentage()+" %");

        }

        @Override
        public int getItemCount() {
            return graphItemArrayList.size();
        }


    }

}
