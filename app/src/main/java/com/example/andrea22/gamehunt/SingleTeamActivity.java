package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import layout.UsernamePickerFragment;

public class SingleTeamActivity extends AppCompatActivity {

    int numTeam, idHunt;
    List<String> usersTeam;
    List<String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_team);

        Toolbar toolbar = (Toolbar) findViewById(R.id.singleTeamToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nome Team");
        Intent intent = getIntent();
        idHunt = Integer.parseInt(intent.getStringExtra("idHunt"));
        numTeam = Integer.parseInt(intent.getStringExtra("numTeam"));
        usersTeam = getUsersTeam();
        users = getUsers();


    }

    public List<String> getUsersTeam(){

        DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = myHelper.getWritableDatabase();

        String stringUsers = myHelper.selectUserAddTeam(db, idHunt, numTeam);
        String[] splitUsers = stringUsers.split("|");
        List<String> users = new ArrayList<String>();

        for (int i = 0; i<splitUsers.length; i++){
            if (!splitUsers[i].equals("")) {
                users.add(splitUsers[i]);
            }        }

        return users;


    }

    public List<String> getUsers(){

        DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = myHelper.getWritableDatabase();

        String stringUsers = myHelper.selectAllUserAddTeam(db, idHunt);
        String[] splitUsers = stringUsers.split("|");
        List<String> users = new ArrayList<String>();

        for (int i = 0; i<splitUsers.length; i++){
            if (!splitUsers[i].equals("")) {
                users.add(splitUsers[i]);
            }
        }

        return users;


    }

    public void newUserFabClick(View view){
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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

}
