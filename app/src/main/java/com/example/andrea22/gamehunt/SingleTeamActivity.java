package com.example.andrea22.gamehunt;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SingleTeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_team);

        Toolbar toolbar = (Toolbar) findViewById(R.id.singleTeamToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nome Team");
        getSupportActionBar().setSubtitle("");

    }

    public void newUserFabClick(View view){
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
