package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.andrea22.gamehunt.Database.DBHelper;
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

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        singleTeam = new ArrayList<>();
        String[] splitUsers;

        List<String> users = new ArrayList<String>();

        Cursor c = db.rawQuery("SELECT * FROM ADDTEAM WHERE idUser = " + pref.getInt("idUser",0), null);

        if(c!=null && c.getCount()>0){
            if (c.moveToFirst()) {
                do {
                    splitUsers = c.getString(c.getColumnIndex("users")).split("|");

                    for(int i=0; i<splitUsers.length; i++){
                        users.add(splitUsers[i]);
                    }
                    singleTeam.add(new SingleTeam(c.getString(c.getColumnIndex("name")),users));
                } while (c.moveToNext());
            }
        } else {

            singleTeam.add(new SingleTeam("Team 1",new ArrayList<String>()));
            singleTeam.add(new SingleTeam("Team 2",new ArrayList<String>()));
        }
    }


    private void initializeAdapter(){
        TeamCardsAdapter adapter = new TeamCardsAdapter(singleTeam, this);
        rv.setAdapter(adapter);
    }

    private void AddTeam(View view){


    }

}