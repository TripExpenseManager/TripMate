package com.tripmate;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

    }

    public Statistics() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View customview = inflater.inflate(R.layout.fragment_statistics, container, false);
        vpStatistics = (ViewPager) customview.findViewById(R.id.vpStatistics);
        setUpViewPager();
        segmentedGroup = (SegmentedGroup) customview.findViewById(R.id.segmentedGroup);
        segmentedGroup.check(R.id.rbDashBoard);
        vpStatistics.setCurrentItem(0);

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
                }else{
                    if(position==1){
                        segmentedGroup.check(R.id.rbStatistics);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




         return customview;
    }

    public  void setUpViewPager(){
        StatsPagerAdapter statsPagerAdapter = new StatsPagerAdapter(getChildFragmentManager());
        statsPagerAdapter.addFragment(new DashBoardFragment(),"DashBoard");
        statsPagerAdapter.addFragment(new ChartsFragment(),"Charts");
        vpStatistics.setAdapter(statsPagerAdapter);

    }


     class StatsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public StatsPagerAdapter(FragmentManager fm) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

