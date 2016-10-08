package com.example.andrea22.gamehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RVAdapter;
import com.example.andrea22.gamehunt.utility.RetrieveJson;
import com.example.andrea22.gamehunt.utility.SimpleFragmentPagerAdapter;
import com.example.andrea22.gamehunt.utility.SingleHunt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by Simone on 21/06/2016.
 */
public class HuntListActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Animation rotate_forward, rotate_backward;
    private TextView tv;
    public List<SingleHunt> userHunts, otherHunts;
    private View topLevelLayout;

    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_list);

        fab = (FloatingActionButton) findViewById(R.id.fab);




        topLevelLayout = findViewById(R.id.hunt_list_top_layout);

        if (isFirstTime()) {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }


        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
//        fab.setOnClickListener(this);

        initializeSlogan();
        initializeData();
        initializeAdapter();






/*
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(),HuntListActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Get the colors for tabLayout
        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.drawable.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));

*/



    }

    private void initializeSlogan(){

        ArrayList<String> slogans = new ArrayList<String>();
        slogans.add(getString(R.string.welcome1));
        slogans.add(getString(R.string.welcome2));
        slogans.add(getString(R.string.welcome3));
        slogans.add(getString(R.string.welcome4));
        slogans.add(getString(R.string.welcome5));

        //Log.v("lista_p", ": " + places);

        String random_string = slogans.get(new Random().nextInt(slogans.size()));
        tv = (TextView) findViewById(R.id.slogan);
        tv.setText(random_string);
    }

    private void initializeData() {

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        userHunts = new ArrayList<>();
        otherHunts = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM HUNT", null);
        Log.v("data", "numhunt: " + c.getCount());

        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);


        if (c.moveToFirst()) {

            Log.v("data", "idUser: " + pref.getInt("idUser", 0));
            Log.v("data", "idUser db: " + c.getInt(c.getColumnIndex("idUser")));

            do {
                if(c.getInt(c.getColumnIndex("idUser")) == (pref.getInt("idUser",0))){
                    Log.v("data", "if");

                    userHunts.add(new SingleHunt(c.getInt(c.getColumnIndex("idHunt")),
                            c.getString(c.getColumnIndex("name")),
                            c.getString(c.getColumnIndex("timeStart")),
                            R.drawable.she_mini, c.getString(c.getColumnIndex("description"))));

                } else {
                    Log.v("data", "else");

                    otherHunts.add(new SingleHunt(c.getInt(c.getColumnIndex("idHunt")),
                            c.getString(c.getColumnIndex("name")),
                            c.getString(c.getColumnIndex("timeStart")),
                            R.drawable.she_mini, c.getString(c.getColumnIndex("description"))));
                }
            } while (c.moveToNext());
        }

        Log.v("data", "userHunts: "+userHunts.size());
        Log.v("data", "otherHunts: " + otherHunts.size());


    }

    private void initializeAdapter() {


        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("My Hunt"));
        tabLayout.addTab(tabLayout.newTab().setText("Play Hunt"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        // Get the colors for tabLayout
        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.drawable.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));


        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        final PagerAdapter adapterPage;
        List<SingleHunt> hunts = new ArrayList<SingleHunt>();

        Log.v("data", "getTabCount: "+tabLayout.getTabCount());


        hunts.addAll(otherHunts);
        hunts.addAll(userHunts);

        adapterPage= new SimpleFragmentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),hunts);



        viewPager.setAdapter(adapterPage);





        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                final PagerAdapter adapterPage;
                List<SingleHunt> hunts = new ArrayList<SingleHunt>();


                if (tab.getPosition() == 0) {
                    hunts.addAll(otherHunts);
                    hunts.addAll(userHunts);

                } else if (tab.getPosition() == 1) {
                    hunts.addAll(userHunts);

                } else if (tab.getPosition() == 2) {
                    hunts.addAll(otherHunts);

                }

                //todo: rimuovere sto 3
                adapterPage = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), 3, hunts);


                viewPager.setAdapter(adapterPage);


                viewPager.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        /*ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);*/
    }

    public void createHunt(View view){
        Log.v("db log", "id: " + view.getId());
        Intent intent = new Intent(this, NewHuntActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void goToHunt(String idHunt){
        Intent intent = new Intent(this, HuntActivity.class);
        intent.putExtra("idHunt", idHunt);
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        //Task spinnerTask;
        try {

            try {
                DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                SQLiteDatabase db = myHelper.getWritableDatabase();

                int isLoaded = myHelper.getHuntIsLoaded(db, Integer.parseInt(idHunt));

                if (isLoaded == 0) {
                    Log.d("test debug", "info da caricare!");
                    String username_ut8 = java.net.URLEncoder.encode(pref.getString("username", null), "UTF-8");

                    String u = "http://jbossews-treasurehunto.rhcloud.com/HuntOperation";
                    String p = "action=goToHunt&username=" + username_ut8 + "&idHunt=" + idHunt;
                    String url[]= new String [2];
                    url[0] = u;
                    url[1] = p;
                    String res = new RetrieveJson().execute(url).get();
                    Log.d("test debug", "res = " + res);

                    if (!res.equals("0")) { //toDO sto if non funziona... entra anche con 0


                        Log.d("test debug", "if. res = " + res);

                        myHelper.insertHuntDetail(db,res);
                        myHelper.setHuntIsLoaded(db, Integer.parseInt(idHunt));
                        /*idUser = myHelper.createDB(db, res);

                        connectWebSocket();


                        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("idUser", idUser);
                        editor.putString("username", username);

                        editor.commit();
                        Intent intent = new Intent(this, HuntListActivity.class);
                        startActivity(intent);*/
                    } else {
                        /*CharSequence text = getString(R.string.login_error);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(this, text, duration);
                        toast.show();*/
                        Log.d("test debug", "else");
                    }
                } else {
                    Log.d("test debug", "info già caricate!");
                }

            } catch (Exception e) {
                Log.d("test debug", "eccezione: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.d("test debug", "eccezione: " + e.getMessage());
            e.printStackTrace();

        }

        startActivity(intent);
    }

    private boolean isFirstTime(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("HuntListRanBefore", false);
        if (!ranBefore) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("HuntListRanBefore", true);
            editor.commit();

            topLevelLayout.setVisibility(View.VISIBLE);
            fab.bringToFront();
            topLevelLayout.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    topLevelLayout.setVisibility(View.INVISIBLE);
                    return false;
                }
            });
        }
        return ranBefore;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            if (res.trim().equals("-1")) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            } else if (!res.trim().equals("1")) {

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
}

