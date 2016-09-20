package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;

import java.util.ArrayList;
import java.util.List;

public class SingleTeamActivity extends AppCompatActivity {

    int numTeam, idHunt;
    List<String> usersTeam;
    List<String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_team);

        Toolbar toolbar = (Toolbar) findViewById(R.id.singleTeamToolbar);
        TextView slogan = (TextView) findViewById(R.id.teamSub);

        setSupportActionBar(toolbar);
        Intent intent = getIntent();


        numTeam = intent.getIntExtra("numTeam", 0);
        idHunt = intent.getIntExtra("idHunt", 0);
        Log.v(getLocalClassName(), "numTeam:" + numTeam);
        Log.v(getLocalClassName(), "idHunt:" + idHunt);


        String[] nameTeam = getNameSloganTeam();

        getSupportActionBar().setTitle(nameTeam[0]);
        slogan.setText(nameTeam[1]);
        usersTeam = getUsersTeam();
        users = getUsers();


    }

    public List<String> getUsersTeam(){

        DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = myHelper.getWritableDatabase();

        String stringUsers = myHelper.selectUserAddTeam(db, idHunt, numTeam);

        Log.v(getLocalClassName(), "users Team:"+stringUsers);

        String[] splitUsers = stringUsers.split("\\|");
        List<String> users = new ArrayList<String>();

        for (int i = 0; i<splitUsers.length; i++){
            if (!splitUsers[i].equals("")) {
                users.add(splitUsers[i]);
            }
        }

        return users;


    }

    public List<String> getUsers(){

        DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = myHelper.getWritableDatabase();

        String stringUsers = myHelper.selectAllUserAddTeam(db, idHunt);

        Log.v(getLocalClassName(), "users:"+stringUsers);


        String[] splitUsers = stringUsers.split("\\|");
        List<String> users = new ArrayList<String>();

        for (int i = 0; i<splitUsers.length; i++){
            if (!splitUsers[i].equals("")) {
                users.add(splitUsers[i]);
            }
        }

        return users;


    }


    public String[] getNameSloganTeam(){

        DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = myHelper.getWritableDatabase();

        String infoTeam[] = myHelper.selectNameSloganTeam(db, idHunt, numTeam);


        return infoTeam;


    }

    public void turnBack(View v){
        finish();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.team_actions, popup.getMenu());
        popup.show();
    }

    public void newUserFabClick(View view){
        Log.v(getLocalClassName(), "addUser");

        //UsernamePickerFragment u = new UsernamePickerFragment();

        // numTeam = Integer.parseInt(((Button) view).getTag().toString());

        //<todo inserire 30 nelle costanti, e ovviamente anche le string dei toast...



        if (usersTeam.size() >= 30){
            CharSequence text = "Ragiunto il numero massimo di utenti per il team!";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            try {
                String username = ((EditText) findViewById(R.id.singleUserId)).getText().toString();
                Log.v(getLocalClassName(), "username:" + username);

                SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

                if (username.equals(pref.getString("username", ""))) {
                    //todo: inserire i messaggi dei toasts in string.xml
                    CharSequence text = "Non puoi aggiungere l'utente che ha creato la caccia";
                    Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }


                for (int i = 0; i < users.size(); i++) {
                    Log.v(getLocalClassName(), "interno:" + users.get(i));

                    if (username.trim().equals((users.get(i)).trim())) {
                        CharSequence text = "User già aggiunto!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(this, text, duration);
                        toast.show();
                        return;
                    }
                }

                String username_url = java.net.URLEncoder.encode(username, "UTF-8");

                Log.v(getLocalClassName(), "username_url:" + username_url);

                String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=checkUser&username=" + username_url;
                String res = new RetrieveFeedTask().execute(u).get();


                Log.v(getLocalClassName(), "res:" + res);


                if (!res.trim().equals("0")) {

                    Log.v(getLocalClassName(), "username inserito:" + username);

                    DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                    SQLiteDatabase db = myHelper.getWritableDatabase();
                    if (pref.getInt("idLastHunt", 0) != 0) {


                        myHelper.insertUserAddTeam(db, pref.getInt("idLastHunt", 0), numTeam, username);
                        users.add(username);
                        usersTeam.add(username);

                        //aggiungere alla grafica

                        /*TextView playerView = new TextView(this);
                        playerView.setText(username);
                        singleTeam.get(numTeam - 1).getPlayer().add(username.trim());
                        ((LinearLayout) ((LinearLayout) lastAddUser.getParent()).getChildAt(1)).addView(playerView);*/


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

    }



}
