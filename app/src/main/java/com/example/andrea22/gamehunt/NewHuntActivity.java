package com.example.andrea22.gamehunt;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andrea22.gamehunt.utility.DBHelper;

import layout.TimePickerFragment;

/**
 * Created by Simone on 21/06/2016.
 */
public class NewHuntActivity extends FragmentActivity {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_newhunt);
        }



    public void showTimePickerDialog(View v) {
      DialogFragment newFragment = new TimePickerFragment();
         newFragment.show(getFragmentManager(), "timePicker2222");
    }


}
