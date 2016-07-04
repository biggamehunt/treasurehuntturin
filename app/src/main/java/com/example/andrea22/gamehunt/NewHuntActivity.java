package com.example.andrea22.gamehunt;


import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.JSONBuilder;
import com.example.andrea22.gamehunt.utility.RetrieveJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import layout.DatePickerFragment;
import layout.TimePickerFragment;

/**
 * Created by Simone on 21/06/2016.
 */
public class NewHuntActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText name;
    EditText description;
    int year = 0, month = 0, day = 0, minute = 0, hour = 0;
    private JSONObject stage;

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


        try {
            JSONBuilder jsonBuilder = new JSONBuilder();
            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
            Cursor c = db.rawQuery("SELECT * FROM ADDSTAGE WHERE idUser = "+pref.getInt("idUser",0), null);
            JSONObject hunt = new JSONObject();
            hunt.put("name", name.getText().toString());
            hunt.put("description", description.getText().toString());
            hunt.put("maxTeam", 0);
            hunt.put("day", day);
            hunt.put("month", month);
            hunt.put("year", year);
            hunt.put("hour", hour);
            hunt.put("minute", minute);
            hunt.put("idUser", pref.getInt("idUser", 0));



            JSONArray stages = new JSONArray();
            JSONObject stage;
            if (c.moveToFirst()) {
                do {
                        stage = jsonBuilder.getJSONStage(c);
                        stages.put(stage);
                } while (c.moveToNext());

            }
            hunt.put("stages",stages);


            String json = java.net.URLEncoder.encode(hunt.toString(), "UTF-8");

            String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=addHunt&json=" + json;
            String res = new RetrieveJson().execute(u).get();

            if (!res.equals("0")) {


                db.execSQL("DELETE FROM ADDSTAGE WHERE idUser = "+pref.getInt("idUser",0));

                int idHunt = mDbHelper.insertHunt(db, res);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("idLastHunt", idHunt);
                editor.apply();

                CharSequence text = "andiamo spettacolari";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(this, text, duration);
                toast.show();

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
    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }
}
