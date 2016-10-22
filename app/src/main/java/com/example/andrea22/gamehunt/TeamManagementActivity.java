package com.example.andrea22.gamehunt;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class TeamManagementActivity extends AppCompatActivity {

    private RecyclerView rv;
    public List<SingleTeam> teams;
    private int numTeam;

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

        String[] teamNames = {getString(R.string.team1),getString(R.string.team2),getString(R.string.team3),getString(R.string.team4),getString(R.string.team5),getString(R.string.team6),getString(R.string.team7),getString(R.string.team8)};
        for (int i =0; i<teamNames.length;i++){
            teamNamesFree.add(teamNames[i]);
        }

        initializeData();
        initializeAdapter();
    }

    private void initializeData(){

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        teams = new ArrayList<>();
        String[] splitUsers;

        List<String> users = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM ADDTEAM WHERE idUser = " + pref.getInt("idUser",0)+" AND idHunt = " + pref.getInt("idLastHunt",0), null);

        if(c!=null && c.getCount()>0){
            Log.v(getLocalClassName(), "lastHunt:"+pref.getInt("idLastHunt", 0));

            if (c.moveToFirst()) {

                do {
                    splitUsers = c.getString(c.getColumnIndex("users")).split("\\|");

                    for(int i=0; i<splitUsers.length; i++) {
                        users.add(splitUsers[i]);
                    }

                    teams.add(new SingleTeam(c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("slogan")), c.getInt(c.getColumnIndex("numTeam")),splitUsers.length));
                   // users = new ArrayList<String>();
                } while (c.moveToNext());
            }
        } else {
            if (pref.getInt("idLastHunt", 0)!=0) {

                String name_1 = teamNamesFree.remove(0);
                String name_2 = teamNamesFree.remove(0);
                teamNamesHold.add(name_1);
                teamNamesHold.add(name_2);

                teams.add(new SingleTeam(name_1,"", 1,0));
                teams.add(new SingleTeam(name_2,"", 2,0));

                mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), name_1, 1, "");
                mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), name_2, 2, "");
            }
        }
    }

    public void initializeAdapter(){

        adapter = new TeamCardsAdapter(teams, this);
        rv.setAdapter(adapter);
    }

    public void addTeam(View view){

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        if (teamNamesFree.size()>0) {
            String name = teamNamesFree.remove(0);
            teamNamesHold.add(name);

            int numTeam = teams.size() + 1;
            SingleTeam newTeam = new SingleTeam(name, "", numTeam,0);
            teams.add(newTeam);

            mDbHelper.insertAddTeam(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0), name, numTeam, "");

            initializeAdapter();
        } else {
            CharSequence text = getString(R.string.maxTeamToast);
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
            addTutorial();
        } else {
            CharSequence text = "Errore Sconosciuto!";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void goToSingleTeam(View view){

        //todo: cambiare dopo l'aggiunta del picker. Non c'è bisogno di sto get parent, basta prendere i dati da teams!
        LinearLayout ly =(LinearLayout)view.getParent().getParent().getParent().getParent();

        TextView editName = (TextView) ly.getChildAt(0);
        Log.v(getLocalClassName(), "edit name:" + editName.getText().toString());

        Intent intent = new Intent(this, SingleTeamActivity.class);

        numTeam = Integer.parseInt(view.getTag().toString());

        intent.putExtra("numTeam", numTeam);
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        intent.putExtra("idHunt", pref.getInt("idLastHunt", 0));
        intent.putExtra("name", editName.getText().toString());

        Log.v(getLocalClassName(), "numTeam:" + view.getTag().toString());

        Log.v(getLocalClassName(), "idHunt:" + pref.getInt("idLastHunt", 0));

        startActivityForResult(intent, 1);

        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                int numUsers = data.getIntExtra("numUsers",0);
                int numTeam = data.getIntExtra("numTeam",0);

                Log.v(getLocalClassName(), "numUsers:"+numUsers);
                Log.v(getLocalClassName(), "numTeam:"+numTeam);


                int pos = -1;
                for (int i = 0; i<teams.size();i++){
                    if (teams.get(i).getNumTeam() == numTeam){
                        pos = i;
                        teams.get(i).setNumUsers(numUsers);
                        break;
                    }
                }
                if (pos!=-1) {
                    Log.v(getLocalClassName(), "pos:" + pos);
                    Log.v(getLocalClassName(), "ly:" + rv.getChildAt(pos));

                    TextView numUser = (TextView) (rv.getChildAt(pos)).findViewById(R.id.numUser);
                    Log.v(getLocalClassName(), "numUser:" + numUser);
                    numUser.setText("" + numUsers);


                    Log.v(getLocalClassName(), "ly+1:" + rv.getChildAt(pos + 1));

                }
            }
        }
    }


    public void finish(View view){

        Log.v(getLocalClassName(), "entro in finish");

        if(teamNamesHold.size() < 2){

            CharSequence text = "Devi avere minimo due team, altrimenti non c'è sfida u.u";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
            return;

        }

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
                mDbHelper.updateisTeamsEmpty(db, pref.getInt("idLastHunt", 0), 0);

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

    public void turnBack(View view) {
        onBackPressed();
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
        builder.setTitle(getResources().getString(R.string.sloganPick));
        final TextView v = (TextView)view;
// Set up the input
        final EditText input = new EditText(this);
        /*input.setLines(2);
        input.setSingleLine(false);
        input.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);*/
       //todo costants
        int maxLength = 50;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        input.setFilters(fArray);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.dialogOk), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {




                m_Text = input.getText().toString();
                v.setText(m_Text);
                int i = Integer.parseInt("" + v.getTag());
                teams.get(i).setSlogan(m_Text);

                DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());

                SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);


                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                mDbHelper.updateSlogan(db,m_Text,teams.get(i).getNumTeam(),pref.getInt("idUser",0),pref.getInt("idLastHunt",0));


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

    public void namePicker(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.namePick));
        final TextView v = (TextView)view;
// Set up the input
        final EditText input = new EditText(this);
        /*input.setLines(2);
        input.setSingleLine(false);
        input.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);*/
        //todo costants
        int maxLength = 30;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        input.setFilters(fArray);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.dialogOk), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                v.setText(m_Text);
                int i = Integer.parseInt("" + v.getTag());
                teams.get(i).setName(m_Text);

                DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());

                SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);


                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                mDbHelper.updateName(db, m_Text, teams.get(i).getNumTeam(), pref.getInt("idUser",0), pref.getInt("idLastHunt",0));


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

    public void addTutorial(){
        if (teamNamesHold.size()==0){
            new MaterialTapTargetPrompt.Builder(TeamManagementActivity.this)
                    .setBackgroundColour(getResources().getColor(R.color.colorPrimary))
                    .setTarget(findViewById(R.id.addTeam))
                    //todo: strings
                    .setPrimaryText(getString(R.string.tutorialTeamText1))
                    .setSecondaryText(getString(R.string.tutorialTeamText2))
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
                    {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget)
                        {
                            //Do something such as storing a value so that this prompt is never shown again
                        }

                        @Override
                        public void onHidePromptComplete()
                        {

                        }
                    })
                    .show();
        }
    }



}