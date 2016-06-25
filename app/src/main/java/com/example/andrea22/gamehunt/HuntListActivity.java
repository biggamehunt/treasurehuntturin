package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.andrea22.gamehunt.utility.DBHelper;

/**
 * Created by Simone on 21/06/2016.
 */
public class HuntListActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private Animation rotate_forward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huntlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.menuToolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);

        fab.setOnClickListener(this);

        LinearLayout huntsContainer = (LinearLayout) findViewById(R.id.huntsContainer);

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        Cursor c = db.rawQuery("SELECT * FROM HUNT ", null);
        if (c.moveToFirst()) {
            do {
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

            } while (c.moveToNext());
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite2:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void goToHunt(View view) {

        Log.v("db log", "id: " + view.getId());
        Intent intent = new Intent(this, NewHuntActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab){
            fab.startAnimation(rotate_forward);
            Log.d("animazione completata", "");
            Intent intent = new Intent(this, NewHuntActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);


        }
    }
}

