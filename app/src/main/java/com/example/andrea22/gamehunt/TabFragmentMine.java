package com.example.andrea22.gamehunt;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.andrea22.gamehunt.Adapter.RVAdapter;
import com.example.andrea22.gamehunt.Adapter.SimpleFragmentPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabFragmentMine#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragmentMine extends Fragment {

    private RecyclerView rv;
    RVAdapter adapter;

    public TabFragmentMine() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TabFragmentMine newInstance() {
        TabFragmentMine fragment = new TabFragmentMine();

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
        Log.v("tabfragmentmine", "onCreateView");

        FrameLayout frame = (FrameLayout) inflater.inflate(R.layout.fragment_tab_mine, container, false);

        rv = (RecyclerView) frame.findViewById(R.id.rv_mine);
        adapter = new RVAdapter(SimpleFragmentPagerAdapter.hunts, this.getContext());
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        return frame;
    }

}
