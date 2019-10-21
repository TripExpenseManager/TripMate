package com.tripmate;


import android.app.models.PersonWiseExpensesSummaryModel;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * A simple {@link Fragment} subclass.
 */

public class Statistics extends Fragment {
    ViewPager vpStatistics;
    String trip_id;
    SegmentedGroup segmentedGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View customview = inflater.inflate(R.layout.fragment_statistics, container, false);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        setHasOptionsMenu(true);

        vpStatistics = (ViewPager) customview.findViewById(R.id.vpStatistics);
        segmentedGroup = (SegmentedGroup) customview.findViewById(R.id.segmentedGroup);
        segmentedGroup.check(R.id.rbDashBoard);

        //setting the viewpager
        setUpViewPager();


        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbDashBoard:
                        vpStatistics.setCurrentItem(0);
                        break;
                    case R.id.rbStatistics:
                        vpStatistics.setCurrentItem(1);
                        break;
                    default:
                        break;
                }
            }
        });
        vpStatistics.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    segmentedGroup.check(R.id.rbDashBoard);
                }else if(position==1){
                    segmentedGroup.check(R.id.rbStatistics);
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return customview;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TRIP_DETAILS_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                trip_date = data.getStringExtra("tripDate");
                trip_name = data.getStringExtra("tripName");
                if(getSupportActionBar()!=null){
                    getSupportActionBar().setTitle(trip_name);
                    getSupportActionBar().setSubtitle(trip_date);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result

            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("Tab Number Statistics",((TabLayout)getActivity().findViewById(R.id.tabs)).getSelectedTabPosition()+"");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_statistics_fragment,menu);

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

                    setUpViewPager();

                    vpStatistics.setVisibility(View.GONE);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 100ms
                            vpStatistics.setVisibility(View.VISIBLE);
                        }
                    },50);

                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_share){
            shareDashBoard();
        }

        return super.onOptionsItemSelected(item);
    }

    public  void setUpViewPager(){
        StatsPagerAdapter statsPagerAdapter = new StatsPagerAdapter(getChildFragmentManager());
        statsPagerAdapter.addFragment(new DashBoardFragment(),"DashBoard");
        statsPagerAdapter.addFragment(new ChartsFragment(),"Charts");
        vpStatistics.setAdapter(statsPagerAdapter);

        if(segmentedGroup.getCheckedRadioButtonId() == R.id.rbDashBoard ){
            vpStatistics.setCurrentItem(0);
        }else{
            vpStatistics.setCurrentItem(1);
        }

    }


    class StatsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

         StatsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

         void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void shareDashBoard(){

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        ArrayList<PersonWiseExpensesSummaryModel> expensePersonArrayList
                = dataBaseHelper.getPersonWiseExpensesSummaryForDashboard(trip_id);

        String s="";
        s+="S.No) Name\nAmt. given | Amt. spent | Amt. +/-\n\n";
        for(int pos=0; pos<expensePersonArrayList.size();pos++){
            PersonWiseExpensesSummaryModel model=expensePersonArrayList.get(pos);
            s+=(pos+1)+". "+model.getName()+"\n";
            s+=model.getTotalAmountGiven()+"    "+model.getTotalAmountSpent()+"    ";
            if(model.getTotalAmountRemaining()>=0){
                s+="+" + model.getTotalAmountRemaining();
            }else{
                s+=model.getTotalAmountRemaining()+"";
            }
            s+="\n\n";
        }

        s+="\nNote : Here \"Amount \" refers to total amount given (including deposit money given) and +/- refers to " +
                "total amount due/refund ";

        String shareBody = s;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent,"Share via"));

    }


}

