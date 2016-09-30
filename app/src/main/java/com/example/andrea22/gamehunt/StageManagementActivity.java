package com.example.andrea22.gamehunt;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.SingleStage;

import java.util.ArrayList;
import java.util.List;

public class StageManagementActivity extends AppCompatActivity {

    private int maxStages;
    private List<SingleStage> singleStage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_management);

        maxStages = 20;
    }



    public void addStage(View view){

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        /*

        if(singleStage.getNumStage() <= maxStages){

            int numTeam = singleStage.size() + 1;
            SingleStage newStage = new SingleStage(numStage, loc, check, photo);
            singleStage.add(newStage);

        } else {
            CharSequence text = getString(R.string.maxNumStage);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }
*/


    }

    public void deleteStage(View view){

    }
}
