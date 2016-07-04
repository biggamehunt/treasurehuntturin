package com.example.andrea22.gamehunt;

import android.app.DialogFragment;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;
import com.example.andrea22.gamehunt.utility.SingleTeam;
import com.example.andrea22.gamehunt.utility.TeamCardsAdapter;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;

import layout.UsernamePickerFragment;

public class TeamManagment extends AppCompatActivity {

    private RecyclerView rv;
    private List<SingleTeam> singleTeam;
    private int numTeam;

    Button lastAddUser;

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

        Cursor c = db.rawQuery("SELECT * FROM ADDTEAM WHERE idUser = " + pref.getInt("idUser",0)+" AND idHunt = " + pref.getInt("idLastHunt",0), null);

        if(c!=null && c.getCount()>0){
            Log.v(getLocalClassName(), "lastHunt:"+pref.getInt("idLastHunt", 0));

            if (c.moveToFirst()) {

                do {
                    splitUsers = c.getString(c.getColumnIndex("users")).split("\\|");

                    for(int i=0; i<splitUsers.length; i++){
                        users.add(splitUsers[i]);
                    }


                    singleTeam.add(new SingleTeam(c.getString(c.getColumnIndex("name")), users, c.getInt(c.getColumnIndex("numTeam"))));
                    users = new ArrayList<String>();
                } while (c.moveToNext());
            }
        } else {
            if (pref.getInt("idLastHunt", 0)!=0) {
                singleTeam.add(new SingleTeam("Team 1", new ArrayList<String>(), 1));
                singleTeam.add(new SingleTeam("Team 2", new ArrayList<String>(), 2));

                mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), "alpha", 1, "");
                mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), "beta", 2, "");
            }
        }
    }


    public void initializeAdapter(){

        TeamCardsAdapter adapter = new TeamCardsAdapter(singleTeam, this);
        rv.setAdapter(adapter);
        rv.setAdapter(adapter);

    }

    public void addTeam(View view){


    }

    public void addUser(View view){
        Log.v(getLocalClassName(), "addUser");

        UsernamePickerFragment u = new UsernamePickerFragment();

        u.show(getFragmentManager(),"add username");
        numTeam = Integer.parseInt(((Button) view).getTag().toString());

        lastAddUser = (Button)view;

    }

    private void finish(View view){


    }

    public void doPositiveClick(String username) {
        try {
        //controllo se l'username esiste nel db, se sì aggiunge, altrimenti msotra un toast d'errore


            String username_url = java.net.URLEncoder.encode(username, "UTF-8");


        String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=checkUser&username=" + username_url;
        String res = new RetrieveFeedTask().execute(u).get();


        Log.v(getLocalClassName(), "res:"+res);


        if (!res.trim().equals("0")) {

            Log.v(getLocalClassName(),"username inserito:"+username);

            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
            SQLiteDatabase db = myHelper.getWritableDatabase();
            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
            if (pref.getInt("idLastHunt",0)!=0) {
                myHelper.insertUserAddTeam(db, pref.getInt("idLastHunt", 0), numTeam, username);

                TextView playerView = new TextView(this);
                playerView.setText(username);

                ((LinearLayout)((LinearLayout)lastAddUser.getParent()).getChildAt(1)).addView(playerView);


            }
            //connectWebSocket();


        } else {
            CharSequence text = "Username non esistente";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }





        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void doNegativeClick() {
    }
}