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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.JSONBuilder;
import com.example.andrea22.gamehunt.utility.RetrieveJson;
import org.json.JSONArray;
import org.json.JSONObject;
import layout.DatePickerFragment;
import layout.TimePickerFragment;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/*
 * Created by Simone on 21/06/2016.
 */
public class NewHuntActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView output;

    EditText name;
    EditText description;
    TextView startDate, finishDate, startTime, finishTime;

    int year = 0, month = 0, day = 0, minute = 0, hour = 0;
    //todo: aggiugere in costanti
    public final static long MILLIS_PER_DAY = 3 * 60 * 60 * 1000L;

    private JSONObject stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_hunt);

        name = (EditText) findViewById(R.id.nameHunt);
        description = (EditText) findViewById(R.id.descriptionHunt);
        startDate = (TextView) findViewById(R.id.dateStartPick);
        finishDate = (TextView) findViewById(R.id.dateEndPick);
        startTime = (TextView) findViewById(R.id.timeStartPick);
        finishTime = (TextView) findViewById(R.id.timeEndPick);
        output = (TextView) findViewById(R.id.dateStartPick);

        // Get current date by calender
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        // Show current date
        output.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("/").append(month + 1).append("/")
                .append(year).append(" "));
    }

    public void turnBack(View v){
        finish();
    }

    public void showTimePickerDialog(View v) {
        output = (TextView)v;
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        output = (TextView)v;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

        output.setText(new StringBuilder().append(day)
                .append("/").append(month + 1).append("/").append(year)
                .append(" "));
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;

        output.setText(new StringBuilder().append(hour)
                .append(":").append(minute).append(" "));
    }

    public void goToTeamDirect(View view){
        Intent intent = new Intent(this, TeamManagementActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public static int dateControl(Date timeStart,Date timeEnd) {

        if (timeStart.before(timeEnd)){
            if (Math.abs(timeEnd.getTime() - timeStart.getTime()) > MILLIS_PER_DAY){
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    public void goToStageManagement(View view){

        String[] itemsDateInit = startDate.getText().toString().split("/");
        String[] itemsDateEnd = finishDate.getText().toString().split("/");
        String[] itemsTimeInit = startTime.getText().toString().split(":");
        String[] itemsTimeEnd = finishTime.getText().toString().split(":");

        Log.v("","date init: " + itemsDateInit);
        Log.v("","time init: " + itemsTimeInit);
        Log.v("","date end: " + itemsDateEnd);
        Log.v("","time end: " + itemsTimeEnd);

        GregorianCalendar calInit = new GregorianCalendar();
        calInit.set(Calendar.YEAR, Integer.parseInt(itemsDateInit[2]));
        calInit.set(Calendar.MONTH, Integer.parseInt(itemsDateInit[1]));
        calInit.set(Calendar.DATE, Integer.parseInt(itemsDateInit[0]));
        calInit.set(Calendar.HOUR_OF_DAY, Integer.parseInt(itemsTimeInit[1]));
        calInit.set(Calendar.MINUTE, Integer.parseInt(itemsTimeInit[0]));
        calInit.set(Calendar.SECOND, 0);

        GregorianCalendar calEnd = new GregorianCalendar();
        calEnd.set(Calendar.YEAR, Integer.parseInt(itemsDateEnd[2]));
        calEnd.set(Calendar.MONTH, Integer.parseInt(itemsDateEnd[1]));
        calEnd.set(Calendar.DATE, Integer.parseInt(itemsDateEnd[0]));
        calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(itemsTimeEnd[1]));
        calEnd.set(Calendar.MINUTE, Integer.parseInt(itemsTimeEnd[0]));
        calEnd.set(Calendar.SECOND, 0);

        java.util.Date timeStart = new java.util.Date(calInit.getTime().getTime());
        java.util.Date timeEnd = new java.util.Date(calEnd.getTime().getTime());

        if (name.getText().toString().equals("")){
            CharSequence text = getString(R.string.noNameHunt);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (startTime.equals(R.string.timeInitHunt)){
            CharSequence text = "Nessun tempo di inizio";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {

            CharSequence text;
            Toast toast;

            switch (dateControl(timeStart, timeEnd)){

                case 0:
                    text = "Meno di tre ore di differenza da inizio a fine";
                    toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                    toast.show();
                    break;

                case -1:
                    text = "data iniziale più grande della finale";
                    toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                    toast.show();
                    break;

                case 1:

                    SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

                    try {
                        JSONObject hunt = new JSONObject();
                        hunt.put("name", name.getText().toString());
                        hunt.put("description", description.getText().toString());
                        hunt.put("maxTeam", 0);
                        hunt.put("day", calInit.get(Calendar.DATE));
                        hunt.put("month", calInit.get(Calendar.MONTH));
                        hunt.put("year", calInit.get(Calendar.YEAR));
                        hunt.put("hour", calInit.get(Calendar.HOUR));
                        hunt.put("minute", calInit.get(Calendar.MINUTE));
                        hunt.put("dayEnd", calEnd.get(Calendar.DATE));
                        hunt.put("monthEnd", calEnd.get(Calendar.MONTH));
                        hunt.put("yearEnd", calEnd.get(Calendar.YEAR));
                        hunt.put("hourEnd", calEnd.get(Calendar.HOUR));
                        hunt.put("minuteEnd", calEnd.get(Calendar.MINUTE));
                        hunt.put("idUser", pref.getInt("idUser", 0));
                        String json = java.net.URLEncoder.encode(hunt.toString(), "UTF-8");

                        Log.v("","JSON VS NIGHTMARE" + json.toString());


                        String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=addHunt&json=" + json;
                        String res = new RetrieveJson().execute(u).get();

                        if (!res.equals("0")) {
                            DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
                            SQLiteDatabase db = mDbHelper.getWritableDatabase();

                            int idHunt = mDbHelper.insertCreateHunt(db, res);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("idLastHunt", idHunt);
                            editor.apply();

                            Intent intent = new Intent(this, StageManagementActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);

                        } else {
                            text = "c'è stato qualche errore";
                            int duration = Toast.LENGTH_SHORT;

                            toast = Toast.makeText(this, text, duration);
                            toast.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    public void goToTeam (View v) {
        /*Intent intent = new Intent(this, NewStageActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);*/


        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {
            JSONBuilder jsonBuilder = new JSONBuilder();
            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
            Cursor c = db.rawQuery("SELECT * FROM ADDSTAGE WHERE idUser = "+pref.getInt("idUser",0), null);

            //todo: inserire 1 tra le costanti, come minimi stage
            if (c.getCount() < 1){
                CharSequence text = "Devi inseire almeno uno stage!";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else if (name.getText().toString().equals("")){
                CharSequence text = "Inserire un nome della caccia!";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }


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
            int numStage = 0;
            if (c.moveToFirst()) {
                do {
                    stage = jsonBuilder.getJSONStage(c,numStage);
                    stages.put(stage);
                    numStage++;
                } while (c.moveToNext());

            }
            hunt.put("stages",stages);

            String json = java.net.URLEncoder.encode(hunt.toString(), "UTF-8");

            String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation?action=addHunt&json=" + json;
            String res = new RetrieveJson().execute(u).get();

            if (!res.equals("0")) {

                db.execSQL("DELETE FROM ADDSTAGE WHERE idUser = "+pref.getInt("idUser",0));

                int idHunt = mDbHelper.insertCreateHunt(db, res);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("idLastHunt", idHunt);
                editor.apply();

                Intent intent = new Intent(this, TeamManagementActivity.class);
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

}
