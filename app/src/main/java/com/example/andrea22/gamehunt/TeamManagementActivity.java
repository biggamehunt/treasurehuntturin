package com.example.andrea22.gamehunt;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.concurrent.ExecutionException;

public class TeamManagementActivity extends AppCompatActivity {

    private RecyclerView rv;
    public List<SingleTeam> teams;
    private int numTeam;

    Button lastAddUser;
    List<String> teamNamesFree;
    List<String> teamNamesHold;
    TeamCardsAdapter adapter;
    private String m_Text = "";



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

        teams = new ArrayList<>();
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


                    teams.add(new SingleTeam(c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("slogan")), users, c.getInt(c.getColumnIndex("numTeam"))));
                    users = new ArrayList<String>();
                } while (c.moveToNext());
            }
        } else {
            if (pref.getInt("idLastHunt", 0)!=0) {

                String name_1 = teamNamesFree.remove(0);
                String name_2 = teamNamesFree.remove(0);
                teamNamesHold.add(name_1);
                teamNamesHold.add(name_2);



                teams.add(new SingleTeam(name_1,"", new ArrayList<String>(), 1));
                teams.add(new SingleTeam(name_2,"", new ArrayList<String>(), 2));

                mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), name_1, 1, "");
                mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), name_2, 2, "");
            }
        }
    }




    public void initializeAdapter(){

        adapter = new TeamCardsAdapter(teams, this);
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

            int numTeam = teams.size() + 1;
            SingleTeam newTeam = new SingleTeam(name, "", new ArrayList<String>(), numTeam);
            teams.add(newTeam);

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
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).getNumTeam() == numTeam) {
                    name = teams.get(i).getName();
                    teams.remove(i);
                    Log.v(getLocalClassName(), "remove!");
                    break;
                }

            }
            //modificarenumteam
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).getNumTeam() > numTeam) {
                    teams.get(i).setNumTeam(teams.get(i).getNumTeam() - 1);
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

        //todo: cambiare dopo l'aggiunta del picker. Non c'è bisogno di sto get parent, basta prendere i dati da teams!
        LinearLayout ly =(LinearLayout)view.getParent().getParent().getParent().getParent();

        EditText editName = (EditText) ly.getChildAt(0);
        Log.v(getLocalClassName(), "edit name:" + editName.getText().toString());

        Intent intent = new Intent(this, SingleTeamActivity.class);

        numTeam = Integer.parseInt(view.getTag().toString());

        intent.putExtra("numTeam", numTeam);
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        intent.putExtra("idHunt", pref.getInt("idLastHunt", 0));
        intent.putExtra("name", editName.getText().toString());

        Log.v(getLocalClassName(), "numTeam:" + view.getTag().toString());

        Log.v(getLocalClassName(), "idHunt:" + pref.getInt("idLastHunt", 0));

        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }


    public void finish(View view){

        Log.v(getLocalClassName(), "entro in finish");
        /*
        EditText editName = (EditText) rv.getChildAt(0).findViewById(R.id.team_name);
        EditText editSlogan = (EditText) rv.getChildAt(0).findViewById(R.id.slogan);


        Log.v(getLocalClassName(), "editName 1:" + editName.getText().toString());
        Log.v(getLocalClassName(), "editSlogan 1:" + editSlogan.getText().toString());



        Log.v(getLocalClassName(), "nome 1:" + ((EditText)((LinearLayout)((LinearLayout)((CardView)((LinearLayout)rv.getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1)).getChildAt(0)).getText().toString());
        Log.v(getLocalClassName(), "slogan 1:" + ((EditText)((LinearLayout)((LinearLayout)((CardView)((LinearLayout)rv.getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1)).getChildAt(1)).getText().toString());

        Log.v(getLocalClassName(), "nome 2:" + ((EditText)((LinearLayout)((LinearLayout)((CardView)((LinearLayout)rv.getChildAt(1)).getChildAt(0)).getChildAt(0)).getChildAt(1)).getChildAt(0)).getText().toString());
        Log.v(getLocalClassName(), "slogan 2:" + ((EditText)((LinearLayout)((LinearLayout)((CardView)((LinearLayout)rv.getChildAt(1)).getChildAt(0)).getChildAt(0)).getChildAt(1)).getChildAt(1)).getText().toString());
*/




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
            //todo: costante
            int minTeam=31;
            JSONObject team;
            if (c.moveToFirst()) {
                do {
                    team = jsonBuilder.getJSONTeam(c);
                    if (minTeam > team.getJSONArray("users").length()){
                        minTeam = team.getJSONArray("users").length();
                    }
                    jTeams.put(team);
                } while (c.moveToNext());

            }

            teams.put("teams",jTeams);
            teams.put("minTeam",minTeam);

            String json = java.net.URLEncoder.encode(teams.toString(), "UTF-8");

            String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation";

            String p = "action=addTeams&json=" + json;
            String url[]= new String [2];
            url[0] = u;
            url[1] = p;

            String res = new RetrieveJson().execute(url).get();

            if (res.trim().equals("-1")) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            } else if (!res.equals("0")) {
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(getLocalClassName(), "onRestart");

        String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation";

        try {
            String p = "action=checkSession";
            String url[] = new String[2];
            url[0] = u;
            url[1] = p;
            String res = new RetrieveJson().execute(url).get();
            Log.v(getLocalClassName(), "onRestart, res: " + res);

            if (!res.trim().equals("1")) {

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void sloganPicker(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.namePick));
        final EditText v = (EditText)view;
// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                v.setText(m_Text);
                int i = Integer.parseInt(""+v.getTag());
                teams.get(i).setSlogan(m_Text);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void namePicker(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.sloganPick));
        final EditText v = (EditText)view;
// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.dialogOk), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                v.setText(m_Text);
                int i = Integer.parseInt(""+v.getTag());
                teams.get(i).setName(m_Text);

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.dialogCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }



}