package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.example.andrea22.gamehunt.utility.DBHelper;
import com.example.andrea22.gamehunt.utility.SingleTeam;
import com.example.andrea22.gamehunt.utility.TeamCardsAdapter;

import java.util.ArrayList;
import java.util.List;

public class TeamManagment extends AppCompatActivity {

    private RecyclerView rv;
    private List<SingleTeam> singleTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_managment);

        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        //fab = (FloatingActionButton)findViewById(R.id.fab);
        //rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        //fab.setOnClickListener(this);

        initializeData();
        initializeAdapter();
    }
    private void initializeData(){

        //DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        //SQLiteDatabase db = mDbHelper.getWritableDatabase();
        singleTeam = new ArrayList<>();

        singleTeam.add(new SingleTeam("Team1","maira"));
        singleTeam.add(new SingleTeam("Team1","maira"));
        singleTeam.add(new SingleTeam("Team1","maira"));
        singleTeam.add(new SingleTeam("Team1","maira"));

        singleTeam.add(new SingleTeam("Team1","maira"));


        /**
            Cursor c = db.rawQuery("SELECT * FROM HUNT ", null);
            if (c.moveToFirst()) {
                do {

                    singlehunts.add(new SingleHunt(c.getString(c.getColumnIndex("name")),
                            c.getString(c.getColumnIndex("timeStart")),
                            R.drawable.she_mini));
                } while (c.moveToNext());
            }
         **/
    }

    private void initializeAdapter(){
        TeamCardsAdapter adapter = new TeamCardsAdapter(singleTeam);
        rv.setAdapter(adapter);
    }

}