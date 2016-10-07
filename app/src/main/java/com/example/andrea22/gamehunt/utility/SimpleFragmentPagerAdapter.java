package com.example.andrea22.gamehunt.utility;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.andrea22.gamehunt.TabFragmentAll;
import com.example.andrea22.gamehunt.TabFragmentMine;
import com.example.andrea22.gamehunt.TabFragmentOthers;

import java.util.List;

/**
 * Created by Simone on 06/10/2016.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    public static List<SingleHunt> hunts;
    public SimpleFragmentPagerAdapter(FragmentManager fm, int NumOfTabs, List<SingleHunt> hunts) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.hunts = hunts;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragmentAll tab1 = new TabFragmentAll();
                //Bundle bdl = new Bundle(2);

                //tab1.setArguments(bdl);



                return tab1;
            case 1:
                TabFragmentMine tab2 = new TabFragmentMine();
                return tab2;
            case 2:
                TabFragmentOthers tab3 = new TabFragmentOthers();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}