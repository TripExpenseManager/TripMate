package com.tripmate;

import android.app.models.ParentExpenseItemModel;
import android.app.models.ParentFAQsModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
                    Log.d("Vineeth1",lastExpandedPosition+"");
                }
                Log.d("Vineeth2",lastExpandedPosition+"");
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

        ParentFAQsModel model = new ParentFAQsModel();
        model.setQue("What is TripMate?");
        ArrayList<String> ansList = new ArrayList<>();
        ansList.add("TripMate is a Trip Expense Manager.");
        model.setAnswerList(ansList);

        for(int i=0;i<15;i++)
        parentFaqsList.add(model);

    }

    public class MyParentViewHolder extends ParentViewHolder {

        TextView tvQue;

        public MyParentViewHolder(View itemView) {
            super(itemView);
            tvQue = (TextView) itemView.findViewById(R.id.tvQue);

        }

        public void bind(ParentFAQsModel question,int parentPosition) {
            tvQue.setText((parentPosition+1)+". "+question.getQue());
        }
    }

    public class MyChildViewHolder extends ChildViewHolder {

        TextView tvAns;

        public MyChildViewHolder(View itemView) {
            super(itemView);
            tvAns = (TextView) itemView.findViewById(R.id.tvAns);
        }

        public void bind( String ans, int position) {
            tvAns.setText(ans);
        }
    }

    class FAQsExpandableRecyclerAdapter extends ExpandableRecyclerAdapter<ParentFAQsModel, String, MyParentViewHolder, MyChildViewHolder> {
        private LayoutInflater mInflater;


        public FAQsExpandableRecyclerAdapter(Context context,@NonNull List<ParentFAQsModel> parentList) {
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
