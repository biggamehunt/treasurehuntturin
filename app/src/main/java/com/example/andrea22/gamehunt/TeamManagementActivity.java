package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.JSONBuilder;
import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;
import com.example.andrea22.gamehunt.utility.RetrieveJson;
import com.example.andrea22.gamehunt.utility.SingleTeam;
import com.example.andrea22.gamehunt.utility.TeamCardsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TeamManagementActivity extends AppCompatActivity {

    private RecyclerView rv;
    private List<SingleTeam> singleTeam;
    private int numTeam;

    Button lastAddUser;
    List<String> teamNamesFree;
    List<String> teamNamesHold;
    TeamCardsAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_management);

        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        teamNamesFree= new ArrayList<String>();
        teamNamesHold= new ArrayList<String>();

        String[] teamNames = {"Team Red","Team Blue","Team Green","Team Yellow","Team Orange","Team Purple","Team Pink","Team Brown"};

        for (int i =0; i<teamNames.length;i++){
            teamNamesFree.add(teamNames[i]);

        }

        initializeData();
        initializeAdapter();
        //fab = (FloatingActionButton)findViewById(R.id.fab);
        //rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        //fab.setOnClickListener(this);


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

                String name_1 = teamNamesFree.remove(0);
                String name_2 = teamNamesFree.remove(0);
                teamNamesHold.add(name_1);
                teamNamesHold.add(name_2);



                singleTeam.add(new SingleTeam(name_1, new ArrayList<String>(), 1));
                singleTeam.add(new SingleTeam(name_2, new ArrayList<String>(), 2));

                mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), name_1, 1, "");
                mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), name_2, 2, "");
            }
        }
    }




    public void initializeAdapter(){

        adapter = new TeamCardsAdapter(singleTeam, this);
        rv.setAdapter(adapter);


    }

    public void turnBack(View v){
        finish();
    }

    public void addTeam(View view){

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        if (teamNamesFree.size()>0) {
            String name = teamNamesFree.remove(0);
            teamNamesHold.add(name);

            int numTeam = singleTeam.size() + 1;
            SingleTeam newTeam = new SingleTeam(name, new ArrayList<String>(), numTeam);
            singleTeam.add(newTeam);

            mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), name, numTeam, "");


            initializeAdapter();
        } else {
            CharSequence text = "Massimo numero dei team raggiunto!";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    public void deleteTeam(View view){
        numTeam = Integer.parseInt(view.getTag().toString());
        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        Log.v(getLocalClassName(), "numTeam:" + numTeam);
        boolean res = mDbHelper.deleteAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), numTeam);
        Log.v(getLocalClassName(), "res:" + res);

        if (res) {
            String name = "";
            for (int i = 0; i < singleTeam.size(); i++) {
                if (singleTeam.get(i).getNumTeam() == numTeam) {
                    name = singleTeam.get(i).getName();
                    singleTeam.remove(i);
                    Log.v(getLocalClassName(), "remove!");
                    break;
                }

            }
            //modificarenumteam
            for (int i = 0; i < singleTeam.size(); i++) {
                if (singleTeam.get(i).getNumTeam() > numTeam) {
                    singleTeam.get(i).setNumTeam(singleTeam.get(i).getNumTeam() - 1);
                    Log.v(getLocalClassName(), "modificato numTeam!");
                }

            }


            teamNamesFree.add(name);
            teamNamesHold.remove(name);

            Log.v(getLocalClassName(), "teamNamesFree:" + teamNamesFree.toString());
            Log.v(getLocalClassName(), "teamNamesHold:" + teamNamesHold.toString());





            initializeAdapter();
        } else {
            CharSequence text = "Errore Sconosciuto!";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }



    }




    public void goToSingleTeam(View view){
        Intent intent = new Intent(this, SingleTeamActivity.class);

        numTeam = Integer.parseInt(view.getTag().toString());

        intent.putExtra("numTeam", numTeam);
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        intent.putExtra("idHunt", pref.getInt("idLastHunt", 0));

        Log.v(getLocalClassName(), "numTeam:" + view.getTag().toString());

        Log.v(getLocalClassName(), "idHunt:" + pref.getInt("idLastHunt", 0));


        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }


    public void finish(View view){
        Log.v(getLocalClassName(),"entro in finish");
        try {
            DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());

            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);


            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM ADDTEAM WHERE idHunt = " + pref.getInt("idLastHunt", 0), null);

            if (c.moveToFirst()) {
                do {

                    String users = c.getString(c.getColumnIndex("users"));

                    if (users==null || users.equals("")) {
                        CharSequence text = "Non puoi lasciare dei team vuoti!";
                        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                } while (c.moveToNext());

            }


            JSONBuilder jsonBuilder = new JSONBuilder();


            JSONObject teams = new JSONObject();

            teams.put("idHunt",pref.getInt("idLastHunt", 0));

            JSONArray jTeams = new JSONArray();
            int i = 0;
            JSONObject team;
            if (c.moveToFirst()) {
                do {
                    team = jsonBuilder.getJSONTeam(c);
                    jTeams.put(team);
                } while (c.moveToNext());

            }

            teams.put("teams",jTeams);

            String json = java.net.URLEncoder.encode(teams.toString(), "UTF-8");

            String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=addTeams&json=" + json;
            String res = new RetrieveJson().execute(u).get();

            if (!res.equals("0")) {
                Log.v(getLocalClassName(), "idLastHunt"+pref.getInt("idLastHunt", 0));


                db.execSQL("DELETE FROM ADDTEAM WHERE idHunt = " + pref.getInt("idLastHunt", 0));
                Log.v(getLocalClassName(), "dopo la delete");
                Log.v(getLocalClassName(), "res: "+res);
                mDbHelper.insertCreateTeams(db, res);
                SharedPreferences.Editor editor = pref.edit();
                editor.apply();

                Intent intent = new Intent(this, HuntListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            } else {
                CharSequence text = "c'è stato qualche errore";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(this, text, duration);
                toast.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HuntListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
    }


}