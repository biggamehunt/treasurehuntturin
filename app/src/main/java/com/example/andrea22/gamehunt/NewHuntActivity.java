package com.example.andrea22.gamehunt;


import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrea22.gamehunt.utility.DBHelper;
import com.example.andrea22.gamehunt.utility.JSONBuilder;
import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;
import com.example.andrea22.gamehunt.utility.RetrieveJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import layout.DatePickerFragment;
import layout.TimePickerFragment;
import layout.newstage;

/**
 * Created by Simone on 21/06/2016.
 */
public class NewHuntActivity extends AppCompatActivity {

    EditText name;
    EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newhunt);

        name = (EditText) findViewById(R.id.nameHunt);
        description = (EditText) findViewById(R.id.descriptionHunt);


    }



    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void goToStage(View v) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    public void goToTeam (View v) {
        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        JSONBuilder jsonBuilder = new JSONBuilder();
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        Log.v ("iduser",""+pref.getInt("idUser",0));
        Cursor c = db.rawQuery("SELECT * FROM ADDSTAGE WHERE idUser = "+pref.getInt("idUser",0), null);
        try {
            JSONObject hunt = new JSONObject();
            hunt.put("name", name.getText().toString());
            hunt.put("description", description.getText().toString());
            hunt.put("maxTeam", 0);
            hunt.put("day", "07");
            hunt.put("month", "07");
            hunt.put("year", "2016");
            hunt.put("hour", "18");
            hunt.put("minute", "30");

            hunt.put("idUser", 1);
            JSONArray stages = new JSONArray();
            JSONObject stage;
            if (c.moveToFirst()) {
                do {
                        stage = jsonBuilder.getJSONStage(c);
                        stages.put(stage);
                    Log.v("json","stage inserito nel json");
                } while (c.moveToNext());

            }
            hunt.put("stages",stages);


            try {


                String json = java.net.URLEncoder.encode(hunt.toString(), "UTF-8");
                String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=addHunt&json=" + json;
                String res = new RetrieveJson().execute(u).get();

                if (!res.equals("0")) {
                    CharSequence text = "andiamo spettacolari";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(this, text, duration);
                    toast.show();

                    /*DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                    SQLiteDatabase db = myHelper.getWritableDatabase();
                    myHelper.createDB(db, res);
                    Intent intent = new Intent(this, HuntListActivity.class);
                    startActivity(intent);*/
                } else {
                    CharSequence text = "c'è stato qualche errore";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(this, text, duration);
                    toast.show();
                }


            } catch (Exception e) {
                Log.d("test debug", "eccezione: " + e.getMessage());
                e.printStackTrace();
            }









        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
