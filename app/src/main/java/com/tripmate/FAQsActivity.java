package com.tripmate;

import android.app.models.ParentFAQsModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import java.util.ArrayList;
import java.util.List;

public class FAQsActivity extends AppCompatActivity {

    RecyclerView faqsRecyclerView;
    FAQsExpandableRecyclerAdapter  mFaqsAdapter;
    ArrayList<ParentFAQsModel> parentFaqsList = new ArrayList<>();

    int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FAQs");

        faqsRecyclerView = (RecyclerView) findViewById(R.id.faqsRecyclerView);
        prepareData();

        mFaqsAdapter = new FAQsExpandableRecyclerAdapter(this,parentFaqsList);

        faqsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        lastExpandedPosition = -1;

        mFaqsAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {

            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                if (lastExpandedPosition != -1
                        && parentPosition != lastExpandedPosition) {
                    mFaqsAdapter.collapseParent(lastExpandedPosition);
                }
                lastExpandedPosition = parentPosition;
                LinearLayoutManager llm = (LinearLayoutManager) faqsRecyclerView.getLayoutManager();
                llm.scrollToPositionWithOffset(parentPosition, 0);
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {

            }
        });

        faqsRecyclerView.setAdapter(mFaqsAdapter);



    }

    void prepareData(){

        parentFaqsList.clear();

        ParentFAQsModel model;
        ArrayList<String> ansList;

        model = new ParentFAQsModel();
        model.setQue("What is TripMate?");
        ansList = new ArrayList<>();
        ansList.add("TripMate is an application, specially designed for managing expenses covered in a group/solo trip");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("How to add/create a new Trip?");
        ansList = new ArrayList<>();
        ansList.add("1. Select \"Trips\" in Navigation drawer.\n" +
                "2. Click on the plus button located at the bottom right corner of the screen.\n" +
                "3. There you go! Enter the details and proceed further.");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("How to easily access a Trip?");
        ansList = new ArrayList<>();
        ansList.add("You can create a shortcut for any trip by simply clicking on \"Add Shortcut to Home\" in the" +
                "popup menu that appears when the options menu is clicked in that trip. After creating the shortcut " +
                "you can easily the trip by simply clicking on it.");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("Who is Admin?");
        ansList = new ArrayList<>();
        ansList.add("Admin is the person who manages all the expenses covered in the trip." +
                " He is the person to whom all the deposit money is submitted and " +
                "he looks after all the money related matters in the trip");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("What is Deposit Money?");
        ansList = new ArrayList<>();
        ansList.add("Deposit money is the money given by the persons to the admin at any point of the trip" +
                "(mostly at the commencement of trip for pre-booking of travel, stay, etc..)");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("How to assign/change Admin?");
        ansList = new ArrayList<>();
        ansList.add("1. During the creation of the trip :\n Simply click on the name of the person after adding the persons " +
                "in the persons list\n" +
                "2. After the creation of trip : \n" +
                "Go to Persons screen in the trip and click on the options menu of the person whom you want to make as admin." +
                " Then click \"Make Admin\" in the popup menu that appears");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("How to add/edit Trip details(Trip Name, Date & Places) after creation of trip?");
        ansList = new ArrayList<>();
        ansList.add("Open the respective trip and click on the options menu located at the top right corner of the screen." +
                " Then click on \"Trip Details\" in the popup menu that appears. There you go.");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("Can we add a person after the creation of trip?");
        ansList = new ArrayList<>();
        ansList.add("Yes, you can.\n" +
                "Simply open the respective trip and click on the options menu located at the top right corner of the screen." +
                " Then click on \"Add Person\" in the popup menu that appears. There you go.");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("Can we delete a person after the creation of trip?");
        ansList = new ArrayList<>();
        ansList.add("Yes, you can. But conditions apply.\n" +
                "1. If a person's money were found spent anywhere in the trip, he cannot be removed" +
                "(To remove him, expenses recorded needed to be modified so that his money will not " +
                "be accounted for spending anywhere in the trip\n" +
                "2. Admin cannot be removed from the trip(To remove admin, change the admin and try to delete)");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("Can we edit an expense?");
        ansList = new ArrayList<>();
        ansList.add("Yes, you can\n" +
                "Simply traverse to your respective expense in the expenses screen and click on the options menu." +
                " Then click on \"Edit\". There you go.");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("What is Dashboard?");
        ansList = new ArrayList<>();
        ansList.add("Dashboard showcases the total amount given and total amount due/refund of each person." +
                " The persons are sorted based on the total amount due/refund.");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("What is Expenses tab in Statistics screen?");
        ansList = new ArrayList<>();
        ansList.add("Expenses tab showcases the total amount spent \n" +
                "1. By each person\n" +
                "2. On each category\n" +
                "3. On each date\n" +
                "The persons/categories/dates are sorted based on the total amount spent.");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("How to change the theme of the App?");
        ansList = new ArrayList<>();
        ansList.add("Go to Settings -> Theme");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

        model = new ParentFAQsModel();
        model.setQue("How to backup data?");
        ansList = new ArrayList<>();
        ansList.add("We provide local and Google Drive backup facility i.e." +
                " You can either backup the data into your phone memory or " +
                "you can sign in to your google account to backup the data into your drive.");
        model.setAnswerList(ansList);
        parentFaqsList.add(model);

    }

    class MyParentViewHolder extends ParentViewHolder {

        TextView tvQue;

        MyParentViewHolder(View itemView) {
            super(itemView);
            tvQue = (TextView) itemView.findViewById(R.id.tvQue);

        }

        void bind(ParentFAQsModel question, int parentPosition) {
            tvQue.setText((parentPosition+1)+". "+question.getQue());
        }
    }

    class MyChildViewHolder extends ChildViewHolder {

        TextView tvAns;

        MyChildViewHolder(View itemView) {
            super(itemView);
            tvAns = (TextView) itemView.findViewById(R.id.tvAns);
        }

        void bind(String ans, int position) {
            tvAns.setText(ans);
        }
    }

    private class FAQsExpandableRecyclerAdapter extends ExpandableRecyclerAdapter<ParentFAQsModel, String, MyParentViewHolder, MyChildViewHolder> {
        private LayoutInflater mInflater;


        FAQsExpandableRecyclerAdapter(Context context, @NonNull List<ParentFAQsModel> parentList) {
            super(parentList);
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public MyParentViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
            View headerView = mInflater.inflate(R.layout.list_item_faq_que, parentViewGroup, false);
            return new MyParentViewHolder(headerView);
        }

        @NonNull
        @Override
        public MyChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
            View ingredientView = mInflater.inflate(R.layout.list_item_faq_ans, childViewGroup, false);
            return new MyChildViewHolder(ingredientView);
        }

        @Override
        public void onBindParentViewHolder(@NonNull MyParentViewHolder parentViewHolder, int parentPosition, @NonNull ParentFAQsModel parent) {
            parentViewHolder.bind(parent,parentPosition);
        }

        @Override
        public void onBindChildViewHolder(@NonNull MyChildViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull String child) {
            childViewHolder.bind(child,childPosition);
        }




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return true;
        }
    }

}
