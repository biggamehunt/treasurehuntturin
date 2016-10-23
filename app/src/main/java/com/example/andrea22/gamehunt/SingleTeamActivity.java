package com.example.andrea22.gamehunt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.AsyncTask.RetrieveFeedTask;
import com.example.andrea22.gamehunt.AsyncTask.RetrieveJson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SingleTeamActivity extends AppCompatActivity {

    int numTeam, idHunt, pos;
    List<String> usersTeam;
    List<String> users;
    LinearLayout conteinerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_team);

        Toolbar toolbar = (Toolbar) findViewById(R.id.singleTeamToolbar);
        conteinerUser = (LinearLayout) findViewById(R.id.containerUser);


        setSupportActionBar(toolbar);
        Intent intent = getIntent();


        numTeam = intent.getIntExtra("numTeam", 0);
        pos = intent.getIntExtra("pos", 0);

        idHunt = intent.getIntExtra("idHunt", 0);



        getSupportActionBar().setTitle(intent.getStringExtra("name"));
        usersTeam = getUsersTeam();


        for (int i = 0; i < usersTeam.size(); i++){
            LinearLayout ll = (LinearLayout)getLayoutInflater().inflate(R.layout.content_activity_single_team, null, true);
            ((TextView)ll.getChildAt(1)).setText(usersTeam.get(i));
            conteinerUser.addView(ll);
        }


        users = getUsers();


    }

    public List<String> getUsersTeam(){

        DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = myHelper.getWritableDatabase();

        String stringUsers = myHelper.selectUserAddTeam(db, idHunt, numTeam);

        Log.v(getLocalClassName(), "users Team:" + stringUsers);

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

        Log.v(getLocalClassName(), "users:" + stringUsers);


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

        Intent intent = new Intent();

        intent.putExtra("numUsers", usersTeam.size());
        intent.putExtra("numTeam", numTeam);


        setResult(RESULT_OK, intent);

        finish();
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();

        intent.putExtra("numUsers", usersTeam.size());
        intent.putExtra("numTeam", numTeam);


        setResult(RESULT_OK, intent);

        finish();
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);

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
            toast.setGravity(Gravity.CENTER, 0, 0);
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
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }


                for (int i = 0; i < users.size(); i++) {
                    Log.v(getLocalClassName(), "interno:" + users.get(i));

                    if (username.trim().equals((users.get(i)).trim())) {
                        CharSequence text = "User già aggiunto!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(this, text, duration);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }

                String username_url = java.net.URLEncoder.encode(username, "UTF-8");

                Log.v(getLocalClassName(), "username_url:" + username_url);

                String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=checkUser&username=" + username_url;
                String res = new RetrieveFeedTask().execute(u).get();


                Log.v(getLocalClassName(), "res:" + res);


                if (res.trim().equals("-1")) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                } else if (!res.trim().equals("0")) {

                    Log.v(getLocalClassName(), "username inserito:" + username);

                    DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                    SQLiteDatabase db = myHelper.getWritableDatabase();
                    if (pref.getInt("idLastHunt", 0) != 0) {


                        myHelper.insertUserAddTeam(db, pref.getInt("idLastHunt", 0), numTeam, username);
                        users.add(username);
                        usersTeam.add(username);

                        //aggiungere alla grafica

                        LinearLayout ll = (LinearLayout)getLayoutInflater().inflate(R.layout.content_activity_single_team, null, true);
                        ((TextView)ll.getChildAt(1)).setText(username);
                        conteinerUser.addView(ll);

                        ((EditText) findViewById(R.id.singleUserId)).setText("");

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
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public void removeUser(View view){
        Log.v(getLocalClassName(), "removeUser");

        //UsernamePickerFragment u = new UsernamePickerFragment();

        // numTeam = Integer.parseInt(((Button) view).getTag().toString());

        //<todo inserire 30 nelle costanti, e ovviamente anche le string dei toast...
         try {

             String username = ((TextView) ((LinearLayout) (view.getParent()).getParent()).getChildAt(1)).getText().toString();
             Log.v(getLocalClassName(), "username:" + username);

             SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

             if (pref.getInt("idLastHunt", 0) != 0) {

                 DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                 SQLiteDatabase db = myHelper.getWritableDatabase();

                 myHelper.removeUserAddTeam(db, pref.getInt("idLastHunt", 0), numTeam, username);
                 users.remove(username);
                 usersTeam.remove(username);

                 //rimuovere alla grafica
                 //i parte da 1 perché il primo figlio è l'editText
                 for (int i = 1; i < conteinerUser.getChildCount(); i++) {
                     if (((TextView)((LinearLayout)conteinerUser.getChildAt(i)).getChildAt(1)).getText().toString().equals(username)){
                         conteinerUser.removeViewAt(i);
                         break;
                     }
                 }

                    /*TextView playerView = new TextView(this);
                    playerView.setText(username);
                    singleTeam.get(numTeam - 1).getPlayer().add(username.trim());
                    ((LinearLayout) ((LinearLayout) lastAddUser.getParent()).getChildAt(1)).addView(playerView);*/


             } else {
                 Intent intent = new Intent(this, LoginActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                 startActivity(intent);
             }

        } catch (Exception e) {
            e.printStackTrace();
        }



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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }


}
