package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andrea22.gamehunt.utility.DBHelper;

/**
 * Created by Simone on 21/06/2016.
 */
public class HuntListActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_huntlist);

            LinearLayout huntsContainer = (LinearLayout) findViewById(R.id.huntsContainer);

            DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();



            Cursor c = db.rawQuery("SELECT * FROM HUNT ", null);
            if(c.moveToFirst()){
                do{
                    LayoutInflater factory = LayoutInflater.from(this);
                    LinearLayout myView = (LinearLayout) factory.inflate(R.layout.hunt, null);
                    LinearLayout child = (LinearLayout) myView.getChildAt(0);
                    TextView name = (TextView) child.getChildAt(0);
                    TextView timeStart = (TextView) child.getChildAt(1);
                    Log.v("db log", "name db: " + c.getString(0)
                    );
                    name.setText(c.getString(c.getColumnIndex("name")));
                    timeStart.setText(c.getString(c.getColumnIndex("timeStart")));
                    myView.setId(Integer.parseInt(c.getString(c.getColumnIndex("idHunt"))));
                    //toDo: ProgressBar e Matita

                    huntsContainer.addView(myView);
                    Log.v("db log", "aggiunta view");

                }while(c.moveToNext());
            }


        }

    public void goToHunt(View view)  {

        Log.v("db log", "id: " +view.getId());
        Intent intent = new Intent(this, NewHuntActivity.class);
        startActivity(intent);
    }
}