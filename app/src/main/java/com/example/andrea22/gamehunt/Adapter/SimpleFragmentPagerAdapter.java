package com.example.andrea22.gamehunt.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.andrea22.gamehunt.TabFragmentAll;
import com.example.andrea22.gamehunt.TabFragmentMine;
import com.example.andrea22.gamehunt.TabFragmentOthers;
import com.example.andrea22.gamehunt.Entity.SingleHunt;

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

    public List<SingleHunt> getHunts (){
        return hunts;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragmentOthers tab1 = new TabFragmentOthers();
                //Bundle bdl = new Bundle(2);

                //tab1.setArguments(bdl);



                return tab1;
            case 1:
                TabFragmentMine tab2 = new TabFragmentMine();
                return tab2;
            case 2:
                TabFragmentAll tab3 = new TabFragmentAll();

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