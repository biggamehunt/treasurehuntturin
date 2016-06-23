package com.example.andrea22.gamehunt;


import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import layout.DatePickerFragment;
import layout.TimePickerFragment;
import layout.newstage;

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
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void goToStage(View v) {
        DialogFragment newFragment = new newstage();
        newFragment.show(getFragmentManager(), "Add Stage");
    }


}
