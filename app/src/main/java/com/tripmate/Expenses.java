package com.tripmate;


import android.app.models.ExpenseModel;
import android.app.models.ParentExpenseItemModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Expenses extends Fragment {

    RecyclerView mRecyclerView;
    ExpensesExpandableRecyclerAdapter mAdapter;

    ArrayList<ParentExpenseItemModel> allList = new ArrayList<>();
    ArrayList<ParentExpenseItemModel> personsList = new ArrayList<>();
    ArrayList<ParentExpenseItemModel> categoriesList = new ArrayList<>();
    ArrayList<ParentExpenseItemModel> datesList = new ArrayList<>();

    int previouslySelected = 0;
    int lastExpandedPosition = -1;

    RelativeLayout expensesRL;

    Spinner spinner;

    String trip_id,tempPersons="";

    RelativeLayout no_expenses_RL;
    TextView persons_deposit_spent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.expensesRecyclerView);
        expensesRL = (RelativeLayout) view.findViewById(R.id.expensesRL);
        no_expenses_RL = (RelativeLayout) view.findViewById(R.id.no_expenses_RL);
        persons_deposit_spent = (TextView) view.findViewById(R.id.persons_deposit_spent);

        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        allList = dataBaseHelper.getAllExpensesToDisplay(trip_id);
        personsList = dataBaseHelper.getExpensesPersonWiseToDisplay(trip_id);
        categoriesList = dataBaseHelper.getExpensesCategoryWiseToDisplay(trip_id);
        datesList = dataBaseHelper.getExpensesDateWiseToDisplay(trip_id);

        String personsListAsString[] = dataBaseHelper.getPersonsListAsString(trip_id);

        tempPersons = personsListAsString[0];
        for(int i=1;i<personsListAsString.length;i++){
            tempPersons = tempPersons + ", "+personsListAsString[i];
        }

        final FloatingActionButton fab = (FloatingActionButton)  getActivity().findViewById(R.id.fab);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 && fab.isShown())
                {
                    fab.hide();
                }else if(((TabLayout)getActivity().findViewById(R.id.tabs)).getSelectedTabPosition() == 1){

                    fab.show();

                }
            }
        });

        // Inflate the layout for this fragment
        return view;

    }


    public class MyParentViewHolder extends ParentViewHolder {

        ImageView imageView;
        TextView nameTv,percentageTv,totalAmountTv;

        public MyParentViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            percentageTv = (TextView) itemView.findViewById(R.id.percentageTv);
            totalAmountTv = (TextView) itemView.findViewById(R.id.totalAmountTv);
        }

        public void bind(ParentExpenseItemModel item) {


          /*  arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(arrow.getDrawable() == getResources().getDrawable(R.drawable.expand_arrow)){
                        arrow.setImageDrawable( getResources().getDrawable(R.drawable.collapse_arrow));
                    }else{
                        arrow.setImageDrawable( getResources().getDrawable(R.drawable.expand_arrow));
                    }
                }
            });*/


            nameTv.setText(item.getName());
            percentageTv.setText("("+item.getPercentage()+"%)");
            totalAmountTv.setText(item.getAmount()+"");



            if(previouslySelected == 3){

                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                Date date = null;
                try {
                     date = format1.parse(item.getName());
                } catch (ParseException e) {
                    e.printStackTrace();
                }



                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                // generate random color
                int color = generator.getColor(date.getDate());
                //int color = generator.getRandomColor();

                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(date.getDate()+"", color); // radius in px

                imageView.setImageDrawable(drawable);


            }else{
                String firstLetter = String.valueOf(item.getName().charAt(0));

                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                // generate random color
                int color = generator.getColor(item.getName());
                //int color = generator.getRandomColor();

                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(firstLetter, color); // radius in px

                imageView.setImageDrawable(drawable);
            }

        }
    }

    public class MyChildViewHolder extends ChildViewHolder {

        TextView nameTextView,amountTextView,headingTextView0,valueTextView0,headingTextView1,valueTextView1,headingTextView2,valueTextView2,dateTextView,textViewOptions;


        public MyChildViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            amountTextView = (TextView) itemView.findViewById(R.id.amountTextView);
            headingTextView0 = (TextView) itemView.findViewById(R.id.headingTextView0);
            valueTextView0 = (TextView) itemView.findViewById(R.id.valueTextView0);
            headingTextView1 = (TextView) itemView.findViewById(R.id.headingTextView1);
            valueTextView1 = (TextView) itemView.findViewById(R.id.valueTextView1);
            headingTextView2 = (TextView) itemView.findViewById(R.id.headingTextView2);
            valueTextView2 = (TextView) itemView.findViewById(R.id.valueTextView2);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            textViewOptions = (TextView) itemView.findViewById(R.id.textViewOptions);

        }

        public void bind(final ExpenseModel item, int position) {

            nameTextView.setText((position+1)+". "+item.getItemName());

            if(previouslySelected == 2){
                headingTextView0.setVisibility(View.GONE);
                valueTextView0.setVisibility(View.GONE);
            }else{
                headingTextView0.setVisibility(View.VISIBLE);
                valueTextView0.setVisibility(View.VISIBLE);
            }

            headingTextView0.setText("Category : ");
            valueTextView0.setText(item.getCategory());

            amountTextView.setText(item.getAmount()+"");
            headingTextView1.setText("Shared by : ");

            if(item.getShareBy().equalsIgnoreCase(tempPersons)){
                valueTextView1.setText("All");
            }else{
                valueTextView1.setText(item.getShareBy());
            }

            headingTextView2.setText("Expend by : ");
            valueTextView2.setText(item.getExpBy());

            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = new Date(item.getDateValue());
            dateTextView.setText(format1.format(date));

            textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(),textViewOptions);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_expenses_item);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.edit:
                                    //handle menu1 click

                                    Intent intent = new Intent(getActivity(),EditExpense.class);
                                    intent.putExtra("modelToEdit",item);
                                    intent.putExtra("trip_id",trip_id);
                                    startActivityForResult(intent,201);

                                    break;
                                case R.id.delete:
                                    //handle menu2 click

                                    final DataBaseHelper dbHelper = new DataBaseHelper(getActivity());

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setCancelable(true);
                                    builder.setTitle("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if(dbHelper.deleteExpenseItem(item)){

                                                allList = dbHelper.getAllExpensesToDisplay(trip_id);
                                                personsList = dbHelper.getExpensesPersonWiseToDisplay(trip_id);
                                                categoriesList = dbHelper.getExpensesCategoryWiseToDisplay(trip_id);
                                                datesList = dbHelper.getExpensesDateWiseToDisplay(trip_id);

                                                spinner.setSelection(previouslySelected);
                                                if(previouslySelected ==0){
                                                    displayExpenses(allList,lastExpandedPosition,0);
                                                }else if(previouslySelected == 1){
                                                    displayExpenses(personsList,lastExpandedPosition,1);
                                                }else if(previouslySelected == 2){
                                                    displayExpenses(categoriesList,lastExpandedPosition,2);
                                                }else if(previouslySelected == 3){
                                                    displayExpenses(datesList,lastExpandedPosition,3);
                                                }


                                                Snackbar.make(getActivity().findViewById(R.id.fab), "Item deleted successfully", Snackbar.LENGTH_LONG).show();
                                            }else{
                                                Snackbar.make(getActivity().findViewById(R.id.fab), "Unexpected error occurred", Snackbar.LENGTH_LONG).show();
                                            }

                                            dialog.cancel();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.setMessage("You want to delete "+item.getItemName()+"("+item.getAmount()+") expended by "+item.getExpBy()+" and shared by "+item.getShareBy()+"?");
                                    builder.setCancelable(false);
                                    AlertDialog dialog = builder.create();
                                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                                    dialog.show();

                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });


        }
    }

    class ExpensesExpandableRecyclerAdapter extends ExpandableRecyclerAdapter<ParentExpenseItemModel, ExpenseModel, MyParentViewHolder, MyChildViewHolder> {
        private LayoutInflater mInflater;

        public ExpensesExpandableRecyclerAdapter(Context context, @NonNull List<ParentExpenseItemModel> itemList) {
            super(itemList);
            mInflater = LayoutInflater.from(context);
        }

        // onCreate ...
        @Override
        public MyParentViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
            View headerView = mInflater.inflate(R.layout.expenses_parent_list_item, parentViewGroup, false);
            return new MyParentViewHolder(headerView);
        }

        @Override
        public MyChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
            View ingredientView = mInflater.inflate(R.layout.expenses_child_list_item, childViewGroup, false);
            return new MyChildViewHolder(ingredientView);
        }


        @Override
        public void onBindParentViewHolder(@NonNull MyParentViewHolder recipeViewHolder, int parentPosition, @NonNull ParentExpenseItemModel item) {
            recipeViewHolder.bind(item);
        }

        @Override
        public void onBindChildViewHolder(@NonNull MyChildViewHolder ingredientViewHolder, int parentPosition, int childPosition, @NonNull ExpenseModel item) {
            ingredientViewHolder.bind(item,childPosition);
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==201)
        {
            if(data.getBooleanExtra("isEdited",false)){

                DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
                allList = dbHelper.getAllExpensesToDisplay(trip_id);
                personsList = dbHelper.getExpensesPersonWiseToDisplay(trip_id);
                categoriesList = dbHelper.getExpensesCategoryWiseToDisplay(trip_id);
                datesList = dbHelper.getExpensesDateWiseToDisplay(trip_id);

                spinner.setSelection(previouslySelected);
                if(previouslySelected ==0){
                    displayExpenses(allList,lastExpandedPosition,0);
                }else if(previouslySelected == 1){
                    displayExpenses(personsList,lastExpandedPosition,1);
                }else if(previouslySelected == 2){
                    displayExpenses(categoriesList,lastExpandedPosition,2);
                }else if(previouslySelected == 3){
                    displayExpenses(datesList,lastExpandedPosition,3);
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    void displayExpenses(ArrayList<ParentExpenseItemModel> list){

        mAdapter = new ExpensesExpandableRecyclerAdapter(getActivity(), list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        lastExpandedPosition = -1;

        mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {

            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                if (lastExpandedPosition != -1
                        && parentPosition != lastExpandedPosition) {
                    mAdapter.collapseParent(lastExpandedPosition);
                }
                lastExpandedPosition = parentPosition;
                LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                llm.scrollToPositionWithOffset(parentPosition, 0);
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {

            }
        });

        mRecyclerView.setAdapter(mAdapter);


        if(list.size() == 0){

            if(allList.size()> 0){
                no_expenses_RL.setVisibility(View.GONE);
                persons_deposit_spent.setVisibility(View.VISIBLE);

            }else{
                no_expenses_RL.setVisibility(View.VISIBLE);
                persons_deposit_spent.setVisibility(View.GONE);
            }
        }else{
            no_expenses_RL.setVisibility(View.GONE);
            persons_deposit_spent.setVisibility(View.GONE);
        }

    }


    void displayExpenses(ArrayList<ParentExpenseItemModel> list,int indexOfExpandedParent,int spinnerPosition){


        mAdapter = new ExpensesExpandableRecyclerAdapter(getActivity(), list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(indexOfExpandedParent<list.size()){
            if(indexOfExpandedParent != -1){
                mAdapter.expandParent(indexOfExpandedParent);
            }
            lastExpandedPosition = indexOfExpandedParent;
        }else{
            lastExpandedPosition = -1;
        }

        spinner.setSelection(spinnerPosition);

        mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {

            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                if (lastExpandedPosition != -1
                        && parentPosition != lastExpandedPosition) {
                    mAdapter.collapseParent(lastExpandedPosition);
                }
                lastExpandedPosition = parentPosition;
                LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                llm.scrollToPositionWithOffset(parentPosition, 0);
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {

            }
        });

        mRecyclerView.setAdapter(mAdapter);

        if(list.size() == 0){
            if(spinnerPosition == 1 && allList.size()> 0 ){
                no_expenses_RL.setVisibility(View.GONE);
                persons_deposit_spent.setVisibility(View.VISIBLE);
            }else{
                no_expenses_RL.setVisibility(View.VISIBLE);
                persons_deposit_spent.setVisibility(View.GONE);
            }
        }else{
            no_expenses_RL.setVisibility(View.GONE);
            persons_deposit_spent.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(trip_id!=null){
            DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
            allList = dbHelper.getAllExpensesToDisplay(trip_id);
            personsList = dbHelper.getExpensesPersonWiseToDisplay(trip_id);
            categoriesList = dbHelper.getExpensesCategoryWiseToDisplay(trip_id);
            datesList = dbHelper.getExpensesDateWiseToDisplay(trip_id);

            if (spinner != null) {
                if(previouslySelected ==0){
                    displayExpenses(allList,lastExpandedPosition,0);
                }else if(previouslySelected == 1){
                    displayExpenses(personsList,lastExpandedPosition,1);
                }else if(previouslySelected == 2){
                    displayExpenses(categoriesList,lastExpandedPosition,2);
                }else if(previouslySelected == 3){
                    displayExpenses(datesList,lastExpandedPosition,3);
                }
            }
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_expenses_fragment,menu);

        final ImageView reloadButton = (ImageView) menu.findItem(R.id.action_refresh).getActionView();

        final Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);

        if (reloadButton != null) {
            reloadButton.setImageResource(R.drawable.icon_refresh);
            reloadButton.setPadding(10,10,10,10);

            // Set onClick listener for button press action
            reloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(rotation);

                    DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
                    allList = dataBaseHelper.getAllExpensesToDisplay(trip_id);
                    personsList = dataBaseHelper.getExpensesPersonWiseToDisplay(trip_id);
                    categoriesList = dataBaseHelper.getExpensesCategoryWiseToDisplay(trip_id);
                    datesList = dataBaseHelper.getExpensesDateWiseToDisplay(trip_id);

                    if(spinner != null){
                        spinner.setSelection(previouslySelected);
                        if(previouslySelected ==0){
                            displayExpenses(allList,lastExpandedPosition,0);
                        }else if(previouslySelected == 1){
                            displayExpenses(personsList,lastExpandedPosition,1);
                        }else if(previouslySelected == 2){
                            displayExpenses(categoriesList,lastExpandedPosition,2);
                        }else if(previouslySelected == 3){
                            displayExpenses(datesList,lastExpandedPosition,3);
                        }
                    }

                    expensesRL.setVisibility(View.GONE);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 100ms
                            expensesRL.setVisibility(View.VISIBLE);
                        }
                    },50);

                }
            });
        }


        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        String[] arr = {"Total","Person","Category","Date"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item_for_expenses_menu,arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(previouslySelected);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position ==0){
                    displayExpenses(allList);
                    previouslySelected = 0;
                }else if(position == 1){
                    displayExpenses(personsList);
                    previouslySelected = 1;
                }else if(position == 2){
                    displayExpenses(categoriesList);
                    previouslySelected = 2;
                }else if(position == 3){
                    displayExpenses(datesList);
                    previouslySelected = 3;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        super.onCreateOptionsMenu(menu, inflater);

    }
}
