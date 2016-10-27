package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.JSONBuilder;
import com.example.andrea22.gamehunt.Graphics.OnStartDragListener;
import com.example.andrea22.gamehunt.AsyncTask.RetrieveJson;
import com.example.andrea22.gamehunt.Graphics.SimpleItemTouchHelperCallback;
import com.example.andrea22.gamehunt.Entity.SingleStage;
import com.example.andrea22.gamehunt.Adapter.StageCardsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class StageManagementActivity extends AppCompatActivity implements OnStartDragListener {

    private int maxStages;
    private List<SingleStage> stages;
    StageCardsAdapter adapter;
    private RecyclerView rv;
    private ItemTouchHelper mItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_management);

        stages= initializeStages();
        addTutorial();

        //todo aggiungere in constants
        maxStages = 20;

        initializeAdapter();
    }

    public List<SingleStage> initializeStages(){

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        return mDbHelper.selectAddStage(db,pref.getInt("idLastHunt",0),pref.getInt("idUser",0));
    }

    public void initializeAdapter(){

        adapter = new StageCardsAdapter(stages, this, this);

        rv=(RecyclerView)findViewById(R.id.rv_stages);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);
    }

    public void updateAdapter(){

        adapter.notifyData(stages);
        adapter.notifyDataSetChanged();
    }


    public void addStage(View view) {

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (stages.size() <= maxStages) {

            Intent intent = new Intent(this, NewStageActivity.class);
            intent.putExtra("numStage",stages.size());
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
            CharSequence text = getResources().getString(R.string.noStageErrorToast);
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
            Cursor c = db.rawQuery("SELECT * FROM ADDSTAGE WHERE idUser = " + pref.getInt("idUser", 0) + " AND idHunt = " + pref.getInt("idLastHunt", 0) + " ORDER BY numStage ASC", null);


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

            if (res.trim().equals("-1")) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            } else if (!res.equals("0")) {
                db.execSQL("DELETE FROM ADDSTAGE WHERE idHunt = " + pref.getInt("idLastHunt", 0));
                mDbHelper.updateisStagesEmpty(db,pref.getInt("idLastHunt", 0),0);
                Intent intent = new Intent(this, TeamManagementActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            } else {
                CharSequence text = getResources().getString(R.string.uknownErrorToast);
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

                String name = data.getStringExtra("name");
                int numStage = stages.size();
                int isLocationRequired = data.getIntExtra("isLocationRequired", 0);
                int isCheckRequired = data.getIntExtra("isCheckRequired", 0);
                int isPhotoRequired = data.getIntExtra("isPhotoRequired", 0);
                SingleStage newStage = new SingleStage(name, numStage, isLocationRequired, isCheckRequired, isPhotoRequired);
                stages.add(newStage);

                //initializeAdapter();

                updateAdapter();
            }
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateAdapter();
        addTutorial();
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

    public void addTutorial(){
        if (stages.size()==0){
            new MaterialTapTargetPrompt.Builder(StageManagementActivity.this)
                    .setBackgroundColour(getResources().getColor(R.color.colorPrimary))
                    .setTarget(findViewById(R.id.addStage))
                    //todo: strings
                    .setPrimaryText(getString(R.string.tutorialStageText1))
                    .setSecondaryText(getString(R.string.tutorialStageText2))
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
                    {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget)
                        {
                            //Do something such as storing a value so that this prompt is never shown again
                        }

                        @Override
                        public void onHidePromptComplete()
                        {

                        }
                    })
                    .show();
        }
    }
}

