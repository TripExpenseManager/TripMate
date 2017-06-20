package com.tripmate;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by vinee_000 on 20-06-2017.
 */

public class Segment extends SegmentedGroup {
    public Segment(Context context) {
        super(context);
    }
    public void setUpWithViewPaper(final ViewPager viewPager){

        int noOfRadioButtons = this.getChildCount();
        SegmentedGroup segmentedGroup= this;
        final ArrayList<RadioButton> radioButtons = new ArrayList<>();
        for(int i=0;i<noOfRadioButtons;i++){
            radioButtons.add((RadioButton) segmentedGroup.getChildAt(i));
        }
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int index=0;
                for(int i=0;i<radioButtons.size();i++){
                    if(radioButtons.get(i).getId() == checkedId) {
                        index = i;
                        break;
                    }
                }
                if(index!=radioButtons.size()){
                    viewPager.setCurrentItem(index);
                }
            }
        });

    }
}
