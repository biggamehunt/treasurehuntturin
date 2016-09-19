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
import java.util.List;

import layout.UsernamePickerFragment;

public class TeamManagementActivity extends AppCompatActivity {

    private RecyclerView rv;
    private List<SingleTeam> singleTeam;
    private int numTeam;

    Button lastAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_management);

        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

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

    }

    public void turnBack(View v){
        finish();
    }

    public void addTeam(View view){

    }

    public void deleteTeam(View view){

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



    public void addUser(View view){
        Log.v(getLocalClassName(), "addUser");

        UsernamePickerFragment u = new UsernamePickerFragment();

        numTeam = Integer.parseInt(((Button) view).getTag().toString());

        //<todo inserire 30 nelle costanti, e ovviamente anche le string dei toast...
        if (singleTeam.get(numTeam-1).getPlayer().size() >= 30){
            CharSequence text = "Ragiunto il numero massimo di utenti per il team!";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            u.show(getFragmentManager(),"add username");
            lastAddUser = (Button)view;

        }

    }

    public void doPositiveClick(String username) {
        try {
        //controllo se l'username esiste nel db, se sì aggiunge, altrimenti msotra un toast d'errore
            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

            if (username.equals(pref.getString("username", ""))){
                //todo: inserire i messaggi dei toasts in string.xml
                CharSequence text = "Non puoi aggiungere l'utente che ha creato la caccia";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            for (int i = 0; i<singleTeam.size();i++){
                List<String> players = singleTeam.get(i).getPlayer();
                Log.v(getLocalClassName(), "esterno:num player nel team:"+singleTeam.get(i).getCountPlayer());


                for (int j = 0; j < players.size();j++){
                    Log.v(getLocalClassName(), "interno:"+players.get(j));

                    if (username.trim().equals((players.get(j)).trim())){
                        CharSequence text = "User già aggiunto!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(this, text, duration);
                        toast.show();
                        return;
                    }
                }
            }

            String username_url = java.net.URLEncoder.encode(username, "UTF-8");

            String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=checkUser&username=" + username_url;
            String res = new RetrieveFeedTask().execute(u).get();


            Log.v(getLocalClassName(), "res:"+res);


            if (!res.trim().equals("0")) {

                Log.v(getLocalClassName(),"username inserito:"+username);

                DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                SQLiteDatabase db = myHelper.getWritableDatabase();
                if (pref.getInt("idLastHunt",0)!=0) {


                    myHelper.insertUserAddTeam(db, pref.getInt("idLastHunt", 0), numTeam, username);

                    TextView playerView = new TextView(this);
                    playerView.setText(username);
                    singleTeam.get(numTeam-1).getPlayer().add(username.trim());
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

    public void finish(View view){
        Log.v(getLocalClassName(),"entro in finish");

        for (int i = 0; i<singleTeam.size();i++) {
            //todo inserire 1 tra le costanti (minimi user nel team)
            if (singleTeam.get(i).getPlayer().size() < 1) {
                CharSequence text = "Non puoi lasciare dei team vuoti!";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        try {
            JSONBuilder jsonBuilder = new JSONBuilder();
            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
            Cursor c = db.rawQuery("SELECT * FROM ADDTEAM WHERE idHunt = " + pref.getInt("idLastHunt", 0), null);

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







    public void doNegativeClick() {
    }
}