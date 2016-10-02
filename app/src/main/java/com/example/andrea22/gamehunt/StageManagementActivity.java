package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.JSONBuilder;
import com.example.andrea22.gamehunt.utility.RetrieveJson;
import com.example.andrea22.gamehunt.utility.SingleStage;
import com.example.andrea22.gamehunt.utility.StageCardsAdapter;
import com.example.andrea22.gamehunt.utility.TeamCardsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

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



    public void goToTeamManagement(View view){

        if (stages.size() < 1){
            CharSequence text = "Devi inseire almeno uno stage!";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        } /*else if (finishDate.equals(R.string.dateEndHunt) ||  startTime.equals(R.string.timeInitHunt) || finishTime.equals(R.string.timeEndHunt)){
            CharSequence text = getString(R.string.noDateHunt);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }*/

        try {
            DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            JSONBuilder jsonBuilder = new JSONBuilder();
            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
            Cursor c = db.rawQuery("SELECT * FROM ADDSTAGE WHERE idUser = " + pref.getInt("idUser", 0), null);


            JSONObject hunt = new JSONObject();
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
            hunt.put("idHunt",pref.getInt("idLastHunt", 0));


            String json = java.net.URLEncoder.encode(hunt.toString(), "UTF-8");

            String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation";
            String p = "action=addStages&json=" + json;
            String url[]= new String [2];
            url[0] = u;
            url[1] = p;
            String res = new RetrieveJson().execute(url).get();

            if (!res.equals("0")) {
                db.execSQL("DELETE FROM ADDSTAGE WHERE idUser = " + pref.getInt("idUser", 0));

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

