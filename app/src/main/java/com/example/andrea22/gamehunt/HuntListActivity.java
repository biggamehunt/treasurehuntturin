package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.support.design.widget.FloatingActionButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RVAdapter;
import com.example.andrea22.gamehunt.utility.RetrieveJson;
import com.example.andrea22.gamehunt.utility.SingleHunt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Simone on 21/06/2016.
 */
public class HuntListActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private Animation rotate_forward, rotate_backward;
    private RecyclerView rv;
    private TextView tv;
    private List<SingleHunt> singlehunts;
    List<String> slogans = Arrays.asList("Choose your hunt and JOIN!", "Play Now! Choose your hunt. ", "I don't now what I'm doing");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hunt_list);

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
//        fab.setOnClickListener(this);

        initializeSlogan();
        initializeData();
        initializeAdapter();
    }

    private void initializeSlogan(){

        String random_string = slogans.get(new Random().nextInt(slogans.size()));
        tv = (TextView) findViewById(R.id.slogan);
        tv.setText(random_string);

    }


    private void initializeData() {

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        singlehunts = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM HUNT ", null);
        Log.v("data", "numhunt: " + c.getCount());

        if (c.moveToFirst()) {
            do {

                singlehunts.add(new SingleHunt(c.getInt(c.getColumnIndex("idHunt")),
                        c.getString(c.getColumnIndex("name")),
                        c.getString(c.getColumnIndex("timeStart")),
                        R.drawable.she_mini, c.getString(c.getColumnIndex("description"))));
            } while (c.moveToNext());
        }
    }

    private void initializeAdapter() {
        RVAdapter adapter = new RVAdapter(singlehunts, this);
        rv.setAdapter(adapter);
    }


    public void createHunt(View view){
        Log.v("db log", "id: " + view.getId());
        Intent intent = new Intent(this, NewHuntActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void goToHunt(String idHunt){
        Intent intent = new Intent(this, HuntActivity.class);
        intent.putExtra("idHunt",idHunt);
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        //Task spinnerTask;
        try {

            try {
                DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                SQLiteDatabase db = myHelper.getWritableDatabase();

                int isLoaded = myHelper.getHuntIsLoaded(db, Integer.parseInt(idHunt));

                if (isLoaded == 0) {
                    Log.d("test debug", "info da caricare!");
                    String username_ut8 = java.net.URLEncoder.encode(pref.getString("username", null), "UTF-8");
                    String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=goToHunt&username=" + username_ut8 + "&idHunt=" + idHunt;
                    String res = new RetrieveJson().execute(u).get();


                    if (!res.equals("0")) { //toDO sto if non funziona... entra anche con 0


                        Log.d("test debug", "if. res = " + res);

                        myHelper.insertHuntDetail(db,res);
                        myHelper.setHuntIsLoaded(db, Integer.parseInt(idHunt));
                        /*idUser = myHelper.createDB(db, res);

                        connectWebSocket();


                        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("idUser", idUser);
                        editor.putString("username", username);

                        editor.commit();
                        Intent intent = new Intent(this, HuntListActivity.class);
                        startActivity(intent);*/
                    } else {
                        /*CharSequence text = getString(R.string.login_error);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(this, text, duration);
                        toast.show();*/
                        Log.d("test debug", "else");
                    }
                } else {
                    Log.d("test debug", "info già caricate!");
                }


            } catch (Exception e) {
                Log.d("test debug", "eccezione: " + e.getMessage());
                e.printStackTrace();
            }


        } catch (Exception e) {
            Log.d("test debug", "eccezione: " + e.getMessage());
            e.printStackTrace();

        }


        startActivity(intent);
    }

}

