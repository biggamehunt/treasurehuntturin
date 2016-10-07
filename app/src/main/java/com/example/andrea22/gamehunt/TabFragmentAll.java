package com.example.andrea22.gamehunt;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.andrea22.gamehunt.utility.RVAdapter;
import com.example.andrea22.gamehunt.utility.SimpleFragmentPagerAdapter;


public class TabFragmentAll extends Fragment {

    private RecyclerView rv;
    RVAdapter adapter;


    public TabFragmentAll() {
        // Required empty public constructor
    }

    public static TabFragmentAll newInstance() {
        TabFragmentAll fragment = new TabFragmentAll();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.v("tabfragmentall", "onCreateView");

        FrameLayout frame = (FrameLayout) inflater.inflate(R.layout.fragment_tab_all, container, false);

        rv = (RecyclerView) frame.findViewById(R.id.rv_all);
        adapter = new RVAdapter(SimpleFragmentPagerAdapter.hunts, this.getContext());
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        return frame;



    }

}
