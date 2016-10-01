package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.SingleStage;
import com.example.andrea22.gamehunt.utility.StageCardsAdapter;
import com.example.andrea22.gamehunt.utility.TeamCardsAdapter;

import java.util.ArrayList;
import java.util.List;

public class StageManagementActivity extends AppCompatActivity {

    private int maxStages;
    private List<SingleStage> stages;
    StageCardsAdapter adapter;
    private RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_management);
        rv=(RecyclerView)findViewById(R.id.rv_stages);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        stages= new ArrayList<>();
        maxStages = 20;

        initializeAdapter();
    }

    public void initializeAdapter(){
        adapter = new StageCardsAdapter(stages, this);
        rv.setAdapter(adapter);
    }

    public void addStage(View view) {


        if (stages.size() <= maxStages) {

            Intent intent = new Intent(this, NewStageActivity.class);
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.enter, R.anim.exit);


        } else {
            CharSequence text = getString(R.string.maxNumStage);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    public void deleteStage(View view) {

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                int numStage = stages.size() + 1;
                int isLocationRequired = data.getIntExtra("isLocationRequired", 0);
                int isCheckRequired = data.getIntExtra("isCheckRequired", 0);
                int isPhotoRequired = data.getIntExtra("isPhotoRequired", 0);
                SingleStage newStage = new SingleStage(numStage, isLocationRequired, isCheckRequired, isPhotoRequired);
                stages.add(newStage);
                initializeAdapter();

            }
        }
    }

}

