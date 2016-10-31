package com.example.andrea22.gamehunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.Adapter.GalleryAdapter;
import com.example.andrea22.gamehunt.AsyncTask.GetPhoto;
import com.example.andrea22.gamehunt.Entity.Image;
import com.example.andrea22.gamehunt.Entity.InfoHuntForCheck;

import java.util.ArrayList;


public class GalleryActivity extends AppCompatActivity {

    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    public GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        ArrayList<InfoHuntForCheck> hunts = mDbHelper.getHuntsFromUser(db, pref.getInt("idUser", 0));

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        new GetPhoto(this).execute(hunts);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HuntListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}