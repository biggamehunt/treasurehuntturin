package com.example.andrea22.gamehunt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class HuntActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int idHunt;
    private int idStage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt);

        Intent intent = getIntent();
        idHunt = Integer.parseInt(intent.getStringExtra("idHunt"));

        setStageMap();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            // Show rationale and request permission.
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        //search for best location
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;

            }
        }

        if (bestLocation != null) {
            // Get latitude of the current location
            double latitude = bestLocation.getLatitude();

            // Get longitude of the current location
            double longitude = bestLocation.getLongitude();

            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

            //TODO: aggiungere su R.string titolo e snippet dei marker
            Marker marker =  mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));
            marker.setVisible(false);
        }
    }

    public void setStageMap(){

        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

        try {


            Cursor cu = db.rawQuery("SELECT * FROM TEAM;", null);
            Log.v("Hunt Activity", "numteam: " + cu.getCount());
            if (cu.moveToFirst()) {
                do {
                    Log.v("Hunt Activity", cu.getString(cu.getColumnIndex("idTeam")));

                } while (cu.moveToNext());

            }

            cu = db.rawQuery("SELECT * FROM HUNT;", null);
            Log.v("Hunt Activity", "numhunt: " + cu.getCount());

            cu = db.rawQuery("SELECT * FROM STAGE;", null);
            Log.v("Hunt Activity", "numStage: " + cu.getCount());

            Cursor c = db.rawQuery("" +
                    "SELECT  STAGE.numStage, " +
                            "STAGE.areaLat, " +
                            "STAGE.areaLon, " +
                            "STAGE.lat, " +
                            "STAGE.lon, " +
                            "STAGE.ray, " +
                            "STAGE.clue, " +
                            "STAGE.isLocationRequired, " +
                            "STAGE.isCheckRequired, " +
                            "STAGE.isPhotoRequired " +

                    "FROM    TEAM LEFT JOIN BE ON TEAM.idTeam = BE.idTeam " +
                            "LEFT JOIN USER ON USER.idUser = BE.idUser " +
                            "LEFT JOIN STAGE ON STAGE.idStage = TEAM.idCurrentStage " +

                    "WHERE   TEAM.idHunt = "+idHunt+" AND USER.idUser = " + pref.getInt("idUser", 0)+";", null);
            Log.v("Hunt Activity", "info prelevate: " + c.getCount());

            if (c.moveToFirst()) {
                do {
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("numStage")));
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("areaLat")));
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("areaLon")));
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("lat")));
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("lon")));
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("ray")));
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("clue")));
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("isLocationRequired")));
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("isCheckRequired")));
                    Log.v("Hunt Activity", c.getString(c.getColumnIndex("isPhotoRequired")));



                } while (c.moveToNext());

            }





        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void float1(View view){
        Log.v("Hunt Activity", "float 1");

    }

    public void float2(View view){
        Log.v("Hunt Activity", "float 2");

    }

    public void float3(View view){
        Log.v("Hunt Activity", "float 3");

    }
}
