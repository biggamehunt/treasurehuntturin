package com.example.andrea22.gamehunt;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.AsyncTask.RetrieveJson;

import org.json.JSONObject;
import layout.DatePickerFragment;
import layout.TimePickerFragment;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;


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

        Log.v(getLocalClassName(),"STAMPO LOG COME UN CAMPIONE");

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

    public static int dateControl(Date timeStart,Date timeEnd) {
        if (timeEnd == null){
            return 1;
        } else if (timeStart.before(timeEnd)){
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

        if (name.getText().toString().equals("")){
            CharSequence text = getString(R.string.noNameHunt);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        } else if (startTime.getText().toString().equals(getResources().getString(R.string.timeInitHunt))){
            CharSequence text = getResources().getString(R.string.noStartTimeToast);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        Log.v(getLocalClassName(),"startTime.getText().toString().trim(): "+startTime.getText().toString().trim());
        Log.v(getLocalClassName(),"R.string.timeInitHunt: "+R.string.timeInitHunt);



        String[] itemsDateInit = startDate.getText().toString().split("/");
        String[] itemsDateEnd = finishDate.getText().toString().split("/");
        String[] itemsTimeInit = startTime.getText().toString().split(":");
        String[] itemsTimeEnd = finishTime.getText().toString().split(":");



        GregorianCalendar calInit = new GregorianCalendar();
        calInit.set(Calendar.YEAR, Integer.parseInt(itemsDateInit[2].trim()));

        calInit.set(Calendar.MONTH, Integer.parseInt(itemsDateInit[1].trim())-1);
        calInit.set(Calendar.DATE, Integer.parseInt(itemsDateInit[0].trim()));
        calInit.set(Calendar.HOUR_OF_DAY, Integer.parseInt(itemsTimeInit[0].trim()));
        calInit.set(Calendar.MINUTE, Integer.parseInt(itemsTimeInit[1].trim()));
        calInit.set(Calendar.SECOND, 0);

        Log.v(getLocalClassName(),"Gregorio: "+calInit);
        java.util.Date timeStart = new java.util.Date(calInit.getTime().getTime());
        java.util.Date timeEnd = null;

        GregorianCalendar calEnd = new GregorianCalendar();
        if (!finishDate.getText().toString().equals(getResources().getString(R.string.dateEndHunt))){

            calEnd.set(Calendar.YEAR, Integer.parseInt(itemsDateEnd[2].trim()));
            calEnd.set(Calendar.MONTH, Integer.parseInt(itemsDateEnd[1].trim())-1);
            calEnd.set(Calendar.DATE, Integer.parseInt(itemsDateEnd[0].trim()));


            if (finishTime.getText().toString().equals(getResources().getString(R.string.timeEndHunt))){
                finishTime.setText(new StringBuilder().append("0:0").append(" "));
                calEnd.set(Calendar.HOUR_OF_DAY, 0);
                calEnd.set(Calendar.MINUTE, 0);
                calEnd.set(Calendar.SECOND, 0);
            } else {
                calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(itemsTimeEnd[0].trim()));
                calEnd.set(Calendar.MINUTE, Integer.parseInt(itemsTimeEnd[1].trim()));
                calEnd.set(Calendar.SECOND, 0);
            }
            timeEnd = new java.util.Date(calEnd.getTime().getTime());
        }







        Log.v(getLocalClassName(),"date init: " + timeStart);
        Log.v(getLocalClassName(),"date init: " + timeEnd);





        CharSequence text;
        Toast toast;
        switch (dateControl(timeStart, timeEnd)) {

            case 0:
                text = getResources().getString(R.string.timeErrorToast);
                toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);

                toast.show();
                break;

            case -1:
                text = getResources().getString(R.string.dateErrorToast);
                toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);

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

                    if (timeEnd != null){
                        hunt.put("dayEnd", calEnd.get(Calendar.DATE));
                        hunt.put("monthEnd", calEnd.get(Calendar.MONTH));
                        hunt.put("yearEnd", calEnd.get(Calendar.YEAR));
                        hunt.put("hourEnd", calEnd.get(Calendar.HOUR_OF_DAY));
                        hunt.put("minuteEnd", calEnd.get(Calendar.MINUTE));
                    }

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
                    } else if (res.trim().equals("-2")) {
                        text = getResources().getString(R.string.invalideDateToast);
                        int duration = Toast.LENGTH_SHORT;

                        toast = Toast.makeText(this, text, duration);
                        toast.setGravity(Gravity.CENTER, 0, 0);

                        toast.show();
                    }else if (!res.trim().equals("0")) {
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
                        text = getResources().getString(R.string.uknownErrorToast);
                        int duration = Toast.LENGTH_SHORT;

                        toast = Toast.makeText(this, text, duration);
                        toast.setGravity(Gravity.CENTER, 0, 0);

                        toast.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
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
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
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
