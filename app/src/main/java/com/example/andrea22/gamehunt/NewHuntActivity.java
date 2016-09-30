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


/**
 * Created by Simone on 21/06/2016.
 */
public class NewHuntActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView output;

    EditText name;
    EditText description;
    TextView startDate, finishDate, startTime, finishTime;

    int year = 0, month = 0, day = 0, minute = 0, hour = 0;
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
                .append(day).append("-").append(month + 1).append("-")
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
                .append("-").append(month + 1).append("-").append(year)
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

    public void goToStage(View v) {
        Intent intent = new Intent(this, NewStageActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public static void splitCtrlString(String init,String end) {

        String[] itemsInit = init.split("/");
        String[] itemsEnd = init.split("/");

        for(int i = 3; i > 0; i--){
          //  if((int)itemsInit[i] > (int)itemsEnd[i]){
                return;
                //error
            }

        }


        /*

        for (String item : items) {
                System.out.println("item = " + item);
            }
        }
    */

    public void goToStageManagement(){

        if (name.getText().toString().equals("")){
            CharSequence text = getString(R.string.noNameHunt);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (finishDate.equals(R.string.dateEndHunt) ||  startTime.equals(R.string.timeInitHunt) || finishTime.equals(R.string.timeEndHunt)){
            CharSequence text = getString(R.string.noDateHunt);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Intent intent = new Intent(this, NewStageActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);

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
