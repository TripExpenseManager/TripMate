package com.tripmate;


import android.app.models.ExpDialogPersonAndAmountModel;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class Expenses extends Fragment {



    //for expense list sharing
    boolean isAllExpListShare;
    int someShareFlag,personSelectedForSharing;


    RecyclerView mRecyclerView;
    ExpensesExpandableRecyclerAdapter mAdapter;

    ArrayList<ParentExpenseItemModel> allList = new ArrayList<>();
    ArrayList<ParentExpenseItemModel> byPersonsList = new ArrayList<>();
    ArrayList<ParentExpenseItemModel> forPersonsList = new ArrayList<>();
    ArrayList<ParentExpenseItemModel> categoriesList = new ArrayList<>();
    ArrayList<ParentExpenseItemModel> datesList = new ArrayList<>();

    int previouslySelected = 0;
    int lastExpandedPosition = -1;

    RelativeLayout expensesRL;

    Spinner spinner;

    String trip_id,tempPersons="";

    RelativeLayout no_expenses_RL;
    TextView persons_deposit_spent;

    DataBaseHelper dataBaseHelper;

    String defaultCurrencyCode ="" ;

    String toBeShared;

    boolean isDefaultCurrency;

    ExpDialogPersonAndAmountAdapter expDialogPersonAndAmountAdapterSharedBy;
    ExpDialogPersonAndAmountAdapter expDialogPersonAndAmountAdapterPaidBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        dataBaseHelper  = new DataBaseHelper(getActivity());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.expensesRecyclerView);
        expensesRL = (RelativeLayout) view.findViewById(R.id.expensesRL);
        no_expenses_RL = (RelativeLayout) view.findViewById(R.id.no_expenses_RL);
        persons_deposit_spent = (TextView) view.findViewById(R.id.persons_deposit_spent);

        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        defaultCurrencyCode = dataBaseHelper.getTripData(trip_id).getTripcurrency();

        String personsListAsString[] = dataBaseHelper.getPersonsListAsString(trip_id);

        tempPersons = personsListAsString[0];
        for(int i=1;i<personsListAsString.length;i++){
            tempPersons = tempPersons + ", "+personsListAsString[i];
        }

        Log.i("SKSK",tempPersons);

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


    class MyParentViewHolder extends ParentViewHolder {

        ImageView imageView;
        TextView nameTv,percentageTv,totalAmountTv;

         MyParentViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            percentageTv = (TextView) itemView.findViewById(R.id.percentageTv);
            totalAmountTv = (TextView) itemView.findViewById(R.id.totalAmountTv);
        }

         void bind(ParentExpenseItemModel item) {


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
            totalAmountTv.setText(defaultCurrencyCode +" " + item.getAmount());



            if(previouslySelected == 4){

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


            }else if(previouslySelected == 3 && Utils.getCategoriesHashMap().get(item.getName())!=null){
                imageView.setImageDrawable(getResources().getDrawable(Utils.getCategoriesHashMap().get(item.getName()),null));
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

     class MyChildViewHolder extends ChildViewHolder {

        TextView nameTextView,amountTextView,headingTextView0,valueTextView0,headingTextView1,valueTextView1,headingTextView2,valueTextView2,dateTextView;
         CardView expChildCardView;

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
            expChildCardView = (CardView) itemView.findViewById(R.id.expChildCardView);

        }

        public void bind(final ExpenseModel item, int position) {


            nameTextView.setText((position+1)+". "+item.getItemName());

            if(previouslySelected == 3){
                headingTextView0.setVisibility(View.GONE);
                valueTextView0.setVisibility(View.GONE);
            }else{
                headingTextView0.setVisibility(View.VISIBLE);
                valueTextView0.setVisibility(View.VISIBLE);
            }

            if(previouslySelected == 1){
                headingTextView2.setVisibility(View.GONE);
                valueTextView2.setVisibility(View.GONE);
            }else{
                headingTextView2.setVisibility(View.VISIBLE);
                valueTextView2.setVisibility(View.VISIBLE);
            }

            if(previouslySelected == 2){
                headingTextView1.setVisibility(View.GONE);
                valueTextView1.setVisibility(View.GONE);
            }else{
                headingTextView1.setVisibility(View.VISIBLE);
                valueTextView1.setVisibility(View.VISIBLE);
            }

            headingTextView0.setText("Category : ");
            valueTextView0.setText(item.getCategory());


            if(previouslySelected == 1 || previouslySelected == 2){
                amountTextView.setText(defaultCurrencyCode + " " + item.getAmountByPersonForGeneration());
            }else {
                amountTextView.setText(defaultCurrencyCode + " " + roundOff(item.getAmount()*item.getCurrencyConversionRate())+"");
            }

            headingTextView1.setText("Shared by : ");


            String expShareByNames = decodePersonNamesToBeDisplayed(item.getShareBy());
            if(expShareByNames.equalsIgnoreCase(tempPersons)){
                valueTextView1.setText("All");
            }else{
                valueTextView1.setText(expShareByNames);
            }

            String expByNames = decodePersonNamesToBeDisplayed(item.getExpBy());
            headingTextView2.setText("Expend by : ");
            valueTextView2.setText(expByNames);

            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = new Date(item.getDateValue());
            dateTextView.setText(format1.format(date));

            expChildCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openExpenseDetailsDialog(item);
                }
            });


        }
    }

    void openExpenseDetailsDialog(final ExpenseModel expenseModel){



        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.expense_detail_dialog, null);
        final AlertDialog.Builder alertDialogBuilderExpDetails = new AlertDialog.Builder(getActivity());
        alertDialogBuilderExpDetails.setView(promptsView);


        alertDialogBuilderExpDetails.setCancelable(true);
        final AlertDialog alertDialogExpDetails = alertDialogBuilderExpDetails.create();
        alertDialogExpDetails.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        alertDialogExpDetails.show();


        ImageView ivDescFirstLetter;
        final TextView tvDescName,tvAmountToDisplay,tvExpenseDetailHeading,tvExpenseDetailValue,tvExpDetailActualAmount
                ,tvCategoryValue,tvDateValue,tvTotalAmount,tvCurrency,tvConversionRate,tvActualAmount,textViewOptions;
        CardView expenseDetailsCardview;
        RelativeLayout rlExpenseDetailCurChange;
        LinearLayout llCurrencyChangeLayout;

        TextView tvSharedByTitle,tvPaidByTitle;
        final RecyclerView rvSharedBy,rvPaidBy;
        Switch switchCurrencyType;
        final TextView tvCurrencyType;

        LinearLayout llSwitchLayout;




        toBeShared = "";
        tvDescName = (TextView) promptsView.findViewById(R.id.tvDescName);
        tvAmountToDisplay = (TextView) promptsView.findViewById(R.id.tvAmountToDisplay);
        tvExpenseDetailHeading = (TextView) promptsView.findViewById(R.id.tvExpenseDetailHeading);
        tvExpenseDetailValue = (TextView) promptsView.findViewById(R.id.tvExpenseDetailValue);
        tvExpDetailActualAmount = (TextView) promptsView.findViewById(R.id.tvExpDetailActualAmount);
        tvCategoryValue = (TextView) promptsView.findViewById(R.id.tvCategoryValue);
        tvDateValue = (TextView) promptsView.findViewById(R.id.tvDateValue);
        tvTotalAmount = (TextView) promptsView.findViewById(R.id.tvTotalAmount);
        tvCurrency = (TextView) promptsView.findViewById(R.id.tvCurrency);
        tvConversionRate = (TextView) promptsView.findViewById(R.id.tvConversionRate);
        tvActualAmount = (TextView) promptsView.findViewById(R.id.tvActualAmount);
        textViewOptions = (TextView) promptsView.findViewById(R.id.textViewOptions);
        tvSharedByTitle = (TextView) promptsView.findViewById(R.id.tvSharedByTitle);
        tvPaidByTitle = (TextView) promptsView.findViewById(R.id.tvPaidByTitle);

        rvSharedBy = (RecyclerView) promptsView.findViewById(R.id.rvSharedBy);
        rvPaidBy = (RecyclerView) promptsView.findViewById(R.id.rvPaidBy);

        rvSharedBy.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPaidBy.setLayoutManager(new LinearLayoutManager(getContext()));


        switchCurrencyType = (Switch) promptsView.findViewById(R.id.switchCurrencyType);
        tvCurrencyType = (TextView) promptsView.findViewById(R.id.tvCurrencyType);
        llSwitchLayout = (LinearLayout) promptsView.findViewById(R.id.llSwitchLayout);





        ivDescFirstLetter = (ImageView) promptsView.findViewById(R.id.ivDescFirstLetter);


        rlExpenseDetailCurChange = (RelativeLayout) promptsView.findViewById(R.id.rlExpenseDetailCurChange);
        llCurrencyChangeLayout = (LinearLayout) promptsView.findViewById(R.id.llCurrencyChangeLayout);

        expenseDetailsCardview = (CardView) promptsView.findViewById(R.id.expenseDetailsCardview);



        tvDescName.setText(expenseModel.getItemName());
        toBeShared+= expenseModel.getItemName()+" - ";

        if(previouslySelected == 1 || previouslySelected == 2){

            tvAmountToDisplay.setText(defaultCurrencyCode + " "+ expenseModel.getAmountByPersonForGeneration());

            toBeShared += defaultCurrencyCode + " "+ expenseModel.getAmountByPersonForGeneration()+"\n\n";

            toBeShared += "Expense Details : \n";

            expenseDetailsCardview.setVisibility(View.VISIBLE);
            if(previouslySelected == 1){
                tvExpenseDetailHeading.setText("Paid by");
                toBeShared += "Paid by : ";
            }else{
                tvExpenseDetailHeading.setText("Paid for");
                toBeShared += "Paid for : ";
            }
            tvExpenseDetailValue.setText(expenseModel.getPersonNameForGeneration());
            toBeShared += expenseModel.getPersonNameForGeneration()+"\n";

            if(!defaultCurrencyCode.equalsIgnoreCase(expenseModel.getCurrency())){
                toBeShared += "Actual amount entered : ";
                rlExpenseDetailCurChange.setVisibility(View.VISIBLE);
                tvExpDetailActualAmount.setText(expenseModel.getCurrency() +" "
                        +roundOff(expenseModel.getAmountByPersonForGeneration()/expenseModel.getCurrencyConversionRate()));
                toBeShared += expenseModel.getCurrency() +" "
                        +roundOff(expenseModel.getAmountByPersonForGeneration()/expenseModel.getCurrencyConversionRate()) +"\n\n";
            }



        }else{
            tvAmountToDisplay.setText(defaultCurrencyCode + " "+
                    roundOff(expenseModel.getAmount()*expenseModel.getCurrencyConversionRate()));

            toBeShared += defaultCurrencyCode + " "+
                    roundOff(expenseModel.getAmount()*expenseModel.getCurrencyConversionRate())+"\n";
        }

        String firstLetter = String.valueOf(expenseModel.getItemName().charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(expenseModel.getItemName());
        //int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        ivDescFirstLetter.setImageDrawable(drawable);

        toBeShared += "Full expense details : \n";

        tvCategoryValue.setText(expenseModel.getCategory());
        toBeShared += "Category : ";
        toBeShared += expenseModel.getCategory()+"\n";

        String dateToDisplay = new SimpleDateFormat("MMM d, yyyy, hh:mm a").format(expenseModel.getDateValue());
        toBeShared += "Date and Time :";
        toBeShared += dateToDisplay + "\n";

        tvDateValue.setText(dateToDisplay);

        tvTotalAmount.setText(defaultCurrencyCode+" "
                +roundOff(expenseModel.getAmount()*expenseModel.getCurrencyConversionRate())+"");

        toBeShared += "Total Amount :";
        toBeShared += defaultCurrencyCode+" "
                +roundOff(expenseModel.getAmount()*expenseModel.getCurrencyConversionRate()) + "\n";


        if(!defaultCurrencyCode.equalsIgnoreCase(expenseModel.getCurrency())){
            llCurrencyChangeLayout.setVisibility(View.VISIBLE);

            toBeShared+="Currency : ";
            toBeShared+=expenseModel.getCurrency()+"\n";

            toBeShared+="Conversion rate : ";
            toBeShared+=expenseModel.getCurrencyConversionRate()+"\n";

            toBeShared+="Actual amount entered : ";
            toBeShared+= expenseModel.getCurrency() +" "+expenseModel.getAmount();

            tvCurrency.setText(expenseModel.getCurrency());
            tvConversionRate.setText(expenseModel.getCurrencyConversionRate()+"");
            tvActualAmount.setText(expenseModel.getCurrency() +" "+expenseModel.getAmount());
        }



        if(expenseModel.getShareByType() == 1){
            tvSharedByTitle.setText("Shared by (equally)");
            toBeShared += "Shared by (equally)\n";

            ArrayList<ExpDialogPersonAndAmountModel> expDialogPersonAndAmountModels = new ArrayList<>();
            String persons[] = expenseModel.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
            double amountSharedBy = roundOff(expenseModel.getAmount()/persons.length);
            for(String person : persons){
                expDialogPersonAndAmountModels.add(new ExpDialogPersonAndAmountModel(person,amountSharedBy));
                toBeShared+=(person + " : " + amountSharedBy);
                toBeShared+="\n";
            }

            expDialogPersonAndAmountAdapterSharedBy = new ExpDialogPersonAndAmountAdapter(expDialogPersonAndAmountModels,false,
                    expenseModel);
            rvSharedBy.setAdapter(expDialogPersonAndAmountAdapterSharedBy);


        }else if(expenseModel.getShareByType() == 2){
            tvSharedByTitle.setText("Shared by (unequally)");
            toBeShared += "Shared by (unequally)\n";


            ArrayList<ExpDialogPersonAndAmountModel> expDialogPersonAndAmountModels = new ArrayList<>();
            String personsAndAmountList[] = expenseModel.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));

            for (String aPersonsAndAmount : personsAndAmountList) {
                String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));

                expDialogPersonAndAmountModels.add(new ExpDialogPersonAndAmountModel(eachPersonAndAmount[0],
                        roundOff(Double.parseDouble(eachPersonAndAmount[1]))));

                toBeShared+=(eachPersonAndAmount[0] + " : " + roundOff(Double.parseDouble(eachPersonAndAmount[1])));
                toBeShared+="\n";

            }
            expDialogPersonAndAmountAdapterSharedBy = new ExpDialogPersonAndAmountAdapter(expDialogPersonAndAmountModels,false,
                    expenseModel);
            rvSharedBy.setAdapter(expDialogPersonAndAmountAdapterSharedBy);

        }else if(expenseModel.getShareByType() == 3){
            tvSharedByTitle.setText("Shared by (shares)");
            toBeShared += "Shared by (shares)\n";

            ArrayList<ExpDialogPersonAndAmountModel> expDialogPersonAndAmountModels = new ArrayList<>();
            String personsAndAmountList[] = expenseModel.getShareBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));

            int totalShares=0;
            for (String aPersonsAndAmount : personsAndAmountList) {
                String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
                totalShares+=Integer.parseInt(eachPersonAndAmount[1]);
            }

          //  double amountPerShare = roundOff();

            for (String aPersonsAndAmount : personsAndAmountList) {
                String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));

                int noOfShares =Integer.parseInt(eachPersonAndAmount[1]);
                expDialogPersonAndAmountModels.add(new ExpDialogPersonAndAmountModel(eachPersonAndAmount[0],
                        roundOff(noOfShares*expenseModel.getAmount()/totalShares)
                        ,noOfShares));

                toBeShared+=(eachPersonAndAmount[0] + " : " + roundOff(noOfShares*expenseModel.getAmount()/totalShares) + "("+ noOfShares + ")");
                toBeShared+="\n";


            }
            expDialogPersonAndAmountAdapterSharedBy = new ExpDialogPersonAndAmountAdapter(expDialogPersonAndAmountModels,true,
                    expenseModel);
            rvSharedBy.setAdapter(expDialogPersonAndAmountAdapterSharedBy);

        }









        if(expenseModel.getExpByType() == 1){
            tvPaidByTitle.setText("Paid by (single)");
            toBeShared += "Paid by (single)\n";


            ArrayList<ExpDialogPersonAndAmountModel> expDialogPersonAndAmountModels = new ArrayList<>();
            expDialogPersonAndAmountModels.add(new ExpDialogPersonAndAmountModel(expenseModel.getExpBy(),expenseModel.getAmount()));
            toBeShared+=(expenseModel.getExpBy() + " : " + expenseModel.getAmount());
            toBeShared+="\n";

            expDialogPersonAndAmountAdapterPaidBy = new ExpDialogPersonAndAmountAdapter(expDialogPersonAndAmountModels,false,
                    expenseModel);
            rvPaidBy.setAdapter(expDialogPersonAndAmountAdapterPaidBy);




        }else  if(expenseModel.getExpByType() == 2){
            tvPaidByTitle.setText("Paid by (multiple)");
            toBeShared += "Paid by (multiple)\n";

            ArrayList<ExpDialogPersonAndAmountModel> expDialogPersonAndAmountModels = new ArrayList<>();
            String personsAndAmountList[] = expenseModel.getExpBy().split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));

            for (String aPersonsAndAmount : personsAndAmountList) {
                String eachPersonAndAmount[] = aPersonsAndAmount.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));

                expDialogPersonAndAmountModels.add(new ExpDialogPersonAndAmountModel(eachPersonAndAmount[0],
                        roundOff(Double.parseDouble(eachPersonAndAmount[1]))));

                toBeShared+=(eachPersonAndAmount[0] + " : " + roundOff(Double.parseDouble(eachPersonAndAmount[1])));
                toBeShared+="\n";

            }
            expDialogPersonAndAmountAdapterPaidBy = new ExpDialogPersonAndAmountAdapter(expDialogPersonAndAmountModels,false,
                    expenseModel);
            rvPaidBy.setAdapter(expDialogPersonAndAmountAdapterPaidBy);




        }


        switchCurrencyType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isDefaultCurrency = false;
                    tvCurrencyType.setText("Selected Currency");
                    expDialogPersonAndAmountAdapterSharedBy.notifyDataSetChanged();
                    expDialogPersonAndAmountAdapterPaidBy.notifyDataSetChanged();
                }else{
                    isDefaultCurrency = true;
                    tvCurrencyType.setText("Default Currency");
                    expDialogPersonAndAmountAdapterSharedBy.notifyDataSetChanged();
                    expDialogPersonAndAmountAdapterPaidBy.notifyDataSetChanged();
                }
            }
        });


        if(defaultCurrencyCode.equalsIgnoreCase(expenseModel.getCurrency())){
            llSwitchLayout.setVisibility(View.GONE);
        }else{
            llSwitchLayout.setVisibility(View.VISIBLE);
            switchCurrencyType.setChecked(true);
        }

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

                                Intent intent = new Intent(getActivity(),EditExpenseNew.class);
                                intent.putExtra("modelToEdit",expenseModel);
                                intent.putExtra("trip_id",trip_id);
                                startActivityForResult(intent,201);
                                alertDialogExpDetails.dismiss();

                                break;
                            case R.id.delete:
                                //handle menu2 click

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setCancelable(true);
                                builder.setTitle("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(dataBaseHelper.deleteExpenseItem(expenseModel)){
                                            alertDialogExpDetails.dismiss();

                                            handleRefreshWithPosition();


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
                                builder.setMessage("You want to delete "+expenseModel.getItemName()+"?");
                                builder.setCancelable(false);
                                AlertDialog dialog = builder.create();
                                dialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                                dialog.show();

                                break;

                            case R.id.share:

                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, toBeShared);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);


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

    double roundOff(Double d){
        return Math.round(d*100.0)/100.0;
    }

    String decodePersonNamesToBeDisplayed(String encrypted){
        String decrypted="";
        String personAndSomething[] = encrypted.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSONS));
        for(String aPersonAndSomething : personAndSomething){
            String eachPersonAndSomething[] = aPersonAndSomething.split(Pattern.quote(Utils.DELIMITER_FOR_EXP_PERSON_AND_AMOUNT));
            decrypted += eachPersonAndSomething[0];
            decrypted += ", ";
        }

        decrypted = decrypted.substring(0,decrypted.length()-2);
        return  decrypted;
    }

    class ExpDialogPersonAndAmountAdapter extends RecyclerView.Adapter<ExpDialogPersonAndAmountAdapter.ExpDialogPersonAndAmountHolder>{

        ArrayList<ExpDialogPersonAndAmountModel> expDialogPersonAndAmountModels;
        boolean isSharedByShares = false;
        ExpenseModel model;

        public ExpDialogPersonAndAmountAdapter(ArrayList<ExpDialogPersonAndAmountModel> expDialogPersonAndAmountModels, boolean isSharedByShares, ExpenseModel model) {
            this.expDialogPersonAndAmountModels = expDialogPersonAndAmountModels;
            this.isSharedByShares = isSharedByShares;
            this.model = model;
        }

        public class ExpDialogPersonAndAmountHolder extends  RecyclerView.ViewHolder{
            TextView tvName,tvValue;


            public ExpDialogPersonAndAmountHolder(View view){
                super(view);
                tvName = (TextView) view.findViewById(R.id.tvName);
                tvValue = (TextView) view.findViewById(R.id.tvValue);
            }
        }


        @Override
        public ExpDialogPersonAndAmountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_exp_dialog_person_and_amount, parent, false);
            return  new ExpDialogPersonAndAmountHolder(v);
        }

        @Override
        public void onBindViewHolder( ExpDialogPersonAndAmountHolder holder, int position) {

            String personName =expDialogPersonAndAmountModels.get(position).getName();
            double amount = expDialogPersonAndAmountModels.get(position).getAmount();
            double conversionRate = model.getCurrencyConversionRate();

            if(isSharedByShares){
                if(isDefaultCurrency){
                    holder.tvName.setText(personName);
                    holder.tvValue.setText(defaultCurrencyCode + " " + roundOff(amount*conversionRate) + " (" +
                            expDialogPersonAndAmountModels.get(position).getShares()+")");
                }else{
                    holder.tvName.setText(personName);
                    holder.tvValue.setText(model.getCurrency() + " " + amount + " (" +
                            expDialogPersonAndAmountModels.get(position).getShares()+")");
                }


            }else{
                if(isDefaultCurrency){
                    holder.tvName.setText(personName);
                    holder.tvValue.setText(defaultCurrencyCode + " " + roundOff(amount*conversionRate));
                }else{
                    holder.tvName.setText(personName);
                    holder.tvValue.setText(model.getCurrency() + " " + amount);
                }
            }
        }

        @Override
        public int getItemCount() {
            return expDialogPersonAndAmountModels.size();
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


    void handleRefreshWithPosition(){
        allList = dataBaseHelper.getAllExpensesToDisplay(trip_id);
        byPersonsList = dataBaseHelper.getExpensesByPersonWiseToDisplay(trip_id);
        forPersonsList = dataBaseHelper.getExpensesForPersonWiseToDisplay(trip_id);
        categoriesList = dataBaseHelper.getExpensesCategoryWiseToDisplay(trip_id);
        datesList = dataBaseHelper.getExpensesDateWiseToDisplay(trip_id);

        if(spinner!=null){
            spinner.setSelection(previouslySelected);
            if(previouslySelected ==0){
                displayExpenses(allList,lastExpandedPosition,0);
            }else if(previouslySelected == 1){
                displayExpenses(byPersonsList,lastExpandedPosition,1);
            }else if(previouslySelected == 2){
                displayExpenses(forPersonsList,lastExpandedPosition,2);
            }else if(previouslySelected == 3){
                displayExpenses(categoriesList,lastExpandedPosition,3);
            }else if(previouslySelected == 4){
                displayExpenses(datesList,lastExpandedPosition,4);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==201)
        {
            if(data.getBooleanExtra("isEdited",false)){
                handleRefreshWithPosition();
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
           handleRefreshWithPosition();
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

                    handleRefreshWithPosition();

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

        String[] arr = {"Total","By person","For person","Category","Date"};

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
                    displayExpenses(byPersonsList);
                    previouslySelected = 1;
                }else if(position == 2){
                    displayExpenses(forPersonsList);
                    previouslySelected = 2;
                }else if(position == 3){
                    displayExpenses(categoriesList);
                    previouslySelected = 3;
                }else if(position == 4){
                    displayExpenses(datesList);
                    previouslySelected = 4;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        MenuItem item1 = menu.findItem(R.id.action_share);
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                openExpenseListShareDialog();

                return false;
            }
        });





        super.onCreateOptionsMenu(menu, inflater);

    }


    void openExpenseListShareDialog(){
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.expense_list_share_dialog, null);
        AlertDialog.Builder alertDialogBuilderExpShare = new AlertDialog.Builder(getActivity());
        alertDialogBuilderExpShare.setView(promptsView);

        CheckBox cbShareAllExpenses;
        final LinearLayout llShareSomeExpenses;
        RadioButton rbExpFor,rbExpBy;
        Spinner spPersons;
        Button btnShare;

        cbShareAllExpenses = (CheckBox) promptsView.findViewById(R.id.cbShareAllExpenses);
        llShareSomeExpenses = (LinearLayout) promptsView.findViewById(R.id.llShareSomeExpenses);
        rbExpFor = (RadioButton) promptsView.findViewById(R.id.rbExpFor);
        rbExpBy = (RadioButton) promptsView.findViewById(R.id.rbExpBy);
        spPersons = (Spinner) promptsView.findViewById(R.id.spPersons);
        btnShare = (Button) promptsView.findViewById(R.id.btnShare);

        alertDialogBuilderExpShare.setCancelable(true);
        final AlertDialog alertDialogExpShare = alertDialogBuilderExpShare.create();
        alertDialogExpShare.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
        alertDialogExpShare.show();


        isAllExpListShare = true;
        //someShareflag  = 1 means expense for person
        //someShareflag  = 2 means expense by person

        someShareFlag = 1;
        personSelectedForSharing = 0;

        cbShareAllExpenses.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isAllExpListShare = true;
                    llShareSomeExpenses.setVisibility(View.GONE);
                }else{
                    isAllExpListShare = false;
                    llShareSomeExpenses.setVisibility(View.VISIBLE);
                }
            }
        });

        ArrayList spinnerItems = new ArrayList();
        spinnerItems.addAll(Arrays.asList(dataBaseHelper.getPersonsListAsString(trip_id)));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item,spinnerItems);
        spPersons.setAdapter(spinnerAdapter);

        spPersons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                personSelectedForSharing = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }


}
