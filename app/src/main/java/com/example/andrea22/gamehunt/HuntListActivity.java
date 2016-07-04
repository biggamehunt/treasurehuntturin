package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.support.design.widget.FloatingActionButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RVAdapter;
import com.example.andrea22.gamehunt.utility.SingleHunt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simone on 21/06/2016.
 */
public class HuntListActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private Animation rotate_forward, rotate_backward;
    private RecyclerView rv;
    private List<SingleHunt> singlehunts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_huntlist);

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
//        fab.setOnClickListener(this);

        initializeData();
        initializeAdapter();
    }


    private void initializeData() {

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        singlehunts = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM HUNT ", null);
        Log.v("data", "numhunt: " + c.getCount());

        if (c.moveToFirst()) {
            do {

                singlehunts.add(new SingleHunt(c.getInt(c.getColumnIndex("idHunt")),
                        c.getString(c.getColumnIndex("name")),
                        c.getString(c.getColumnIndex("timeStart")),
                        R.drawable.she_mini, c.getString(c.getColumnIndex("description"))));
            } while (c.moveToNext());
        }
    }

    private void initializeAdapter() {
        RVAdapter adapter = new RVAdapter(singlehunts, this);
        rv.setAdapter(adapter);
    }


    public void createHunt(View view){
        Log.v("db log", "id: " + view.getId());
        Intent intent = new Intent(this, NewHuntActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

}

