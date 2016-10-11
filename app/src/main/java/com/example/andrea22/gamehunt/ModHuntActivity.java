package com.example.andrea22.gamehunt;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RetrieveJson;

import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import layout.ModDatePickerFragment;
import layout.ModTimePickerFragment;


/*
 * Created by Simone on 21/06/2016.
 */
public class ModHuntActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView output;

    EditText name;
    EditText description;
    TextView startDate, finishDate, startTime, finishTime;
    int idHunt;
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

        Intent intent = getIntent();
        idHunt = Integer.parseInt(intent.getStringExtra("idHunt"));
        initializeData();

    }


    private void initializeData() {

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM HUNT WHERE idHunt = " + idHunt, null);
        Log.v("data", "numhunt: " + c.getCount());

        if (c.moveToFirst()) {

            do {

                Log.v("data", "timeStart: " + c.getString(c.getColumnIndex("timeStart")));
                //todo: modificare la variabile false che sta per isStarted
                name.setText( c.getString(c.getColumnIndex("name")));
                description.setText(c.getString(c.getColumnIndex("description")));
                String timeStart = c.getString(c.getColumnIndex("timeStart"));

                startDate.setText(new StringBuilder()
                        // Month is 0 based, just add 1
                        .append(timeStart.substring(8, 10)).append("/").append(timeStart.substring(5, 7)).append("/")
                        .append(timeStart.substring(0, 4)).append(" "));
                startTime.setText(new StringBuilder().append(timeStart.substring(11, 13))
                        .append(":").append(timeStart.substring(14, 16)).append(" "));


                if (c.isNull(c.getColumnIndex("timeEnd"))==false){
                    String timeEnd = c.getString(c.getColumnIndex("timeEnd"));

                    finishDate.setText(new StringBuilder()
                            // Month is 0 based, just add 1
                            .append(timeEnd.substring(8, 10)).append("/").append(timeEnd.substring(5, 7)).append("/")
                            .append(timeEnd.substring(0, 4)).append(" "));
                    finishTime.setText(new StringBuilder().append(timeEnd.substring(11, 13))
                            .append(":").append(timeEnd.substring(14, 16)).append(" "));
                }
/*

        // Get current date by calender
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        // Show current date
        ;*/
            } while (c.moveToNext());
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

    public void turnBack(View v){
        finish();
    }

    public void showTimePickerDialog(View v) {
        output = (TextView)v;
        DialogFragment newFragment = new ModTimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        output = (TextView)v;
        DialogFragment newFragment = new ModDatePickerFragment();
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

    /*public void goToStageManagement(View view){

        String[] itemsDateInit = startDate.getText().toString().split("/");
        String[] itemsDateEnd = finishDate.getText().toString().split("/");
        String[] itemsTimeInit = startTime.getText().toString().split(":");
        String[] itemsTimeEnd = finishTime.getText().toString().split(":");

        Log.v(getLocalClassName(), "date init: " + itemsDateInit[0]);
        Log.v(getLocalClassName(), "date init: " + itemsDateInit[1]);
        Log.v(getLocalClassName(), "date init: " + itemsDateInit[2]);

        Log.v(getLocalClassName(), "date end: " + itemsDateEnd[0]);
        Log.v(getLocalClassName(), "date end: " + itemsDateEnd[1]);
        Log.v(getLocalClassName(), "date end: " + itemsDateEnd[2]);

        Log.v(getLocalClassName(), "time init: " + itemsTimeInit[0]);
        Log.v(getLocalClassName(), "time init: " + itemsTimeInit[1]);

        Log.v(getLocalClassName(), "time finish: " + itemsTimeEnd[0]);
        Log.v(getLocalClassName(), "time finish: " + itemsTimeEnd[1]);




        GregorianCalendar calInit = new GregorianCalendar();
        calInit.set(Calendar.YEAR, Integer.parseInt(itemsDateInit[2].trim()));

        calInit.set(Calendar.MONTH, Integer.parseInt(itemsDateInit[1].trim())-1);
        calInit.set(Calendar.DATE, Integer.parseInt(itemsDateInit[0].trim()));
        calInit.set(Calendar.HOUR_OF_DAY, Integer.parseInt(itemsTimeInit[0].trim()));
        calInit.set(Calendar.MINUTE, Integer.parseInt(itemsTimeInit[1].trim()));
        calInit.set(Calendar.SECOND, 0);

        Log.v(getLocalClassName(),"Gregorio: "+calInit);

        GregorianCalendar calEnd = new GregorianCalendar();
        calEnd.set(Calendar.YEAR, Integer.parseInt(itemsDateEnd[2].trim()));
        calEnd.set(Calendar.MONTH, Integer.parseInt(itemsDateEnd[1].trim())-1);
        calEnd.set(Calendar.DATE, Integer.parseInt(itemsDateEnd[0].trim()));
        calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(itemsTimeEnd[0].trim()));
        calEnd.set(Calendar.MINUTE, Integer.parseInt(itemsTimeEnd[1].trim()));
        calEnd.set(Calendar.SECOND, 0);

        Date timeStart = new Date(calInit.getTime().getTime());
        Date timeEnd = new Date(calEnd.getTime().getTime());

        Log.v(getLocalClassName(),"date init: " + timeStart);
        Log.v(getLocalClassName(),"date init: " + timeEnd);



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
        }

            CharSequence text;
            Toast toast;

            switch (dateControl(timeStart, timeEnd)) {

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
                        hunt.put("hour", calInit.get(Calendar.HOUR_OF_DAY));
                        hunt.put("minute", calInit.get(Calendar.MINUTE));

                        hunt.put("dayEnd", calEnd.get(Calendar.DATE));
                        hunt.put("monthEnd", calEnd.get(Calendar.MONTH));
                        hunt.put("yearEnd", calEnd.get(Calendar.YEAR));
                        hunt.put("hourEnd", calEnd.get(Calendar.HOUR_OF_DAY));
                        hunt.put("minuteEnd", calEnd.get(Calendar.MINUTE));
                        hunt.put("idUser", pref.getInt("idUser", 0));
                        String json = java.net.URLEncoder.encode(hunt.toString(), "UTF-8");

                        Log.v(getLocalClassName(), "JSON VS NIGHTMARE" + json.toString());


                        String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation";


                        String p = "action=addHunt&json=" + json;
                        String url[] = new String[2];
                        url[0] = u;
                        url[1] = p;


                        String res = new RetrieveJson().execute(url).get();

                        if (res.trim().equals("-1")) {
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                        } else if (!res.equals("0")) {
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
                    break;
            }


    }*/

}
