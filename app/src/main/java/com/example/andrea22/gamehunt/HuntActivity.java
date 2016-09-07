package com.example.andrea22.gamehunt;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RetrieveJson;
import com.example.andrea22.gamehunt.utility.SendPhoto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class HuntActivity extends FragmentActivity implements OnMapReadyCallback {
    private Uri mImageUri;
    private GoogleMap mMap;
    private int idHunt, idTeam, idStage, idUser;
    private static HuntActivity parent;

    File photo;
    final int TAKE_PHOTO_REQ = 100;

    private String clue;
    private int numStage, ray, isLocationRequired, isCheckRequired, isPhotoRequired;
    private float areaLat, areaLon, lat, lon;
    FloatingActionButton photoButton;
    Bitmap resized;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt);
        parent = this;
        Intent intent = getIntent();
        idHunt = Integer.parseInt(intent.getStringExtra("idHunt"));

        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        idUser = pref.getInt("idUser", 0);

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


            //TODO: aggiungere su R.string titolo e snippet dei marker
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));
            //marker.setVisible(false);


            if (isLocationRequired == 1) {
                LatLng latLng = new LatLng(areaLat, areaLon);
                // Show the current goal in Google Map
                mMap.addCircle(new CircleOptions()
                        .center(latLng)
                                //// TODO: fare il get dalla textbox
                                // TODO: METTERE IL 50 TRA LE COSTANTI
                        .radius(ray)
                        .strokeColor(0x3500ff00)
                        .strokeWidth(3)
                                //// TODO: inserire sta roba in colors.xml
                                // 0x represents, this is an hexadecimal code
                                // 55 represents percentage of transparency. For 100% transparency, specify 00.
                                // For 0% transparency ( ie, opaque ) , specify ff
                                // The remaining 6 characters(00ff00) specify the fill color
                        .fillColor(0x2500ff00));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            }
            photoButton = (FloatingActionButton) findViewById(R.id.photo);
            if (isPhotoRequired == 0) {
                photoButton.setVisibility(View.INVISIBLE);
            }



        }
    }

    public void setStageMap(){
        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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

            Cursor c = db.rawQuery(
                    "SELECT  STAGE.idStage, " +
                            "STAGE.numStage, " +
                            "STAGE.areaLat, " +
                            "STAGE.areaLon, " +
                            "STAGE.lat, " +
                            "STAGE.lon, " +
                            "STAGE.ray, " +
                            "STAGE.clue, " +
                            "STAGE.isLocationRequired, " +
                            "STAGE.isCheckRequired, " +
                            "STAGE.isPhotoRequired, " +
                            "TEAM.idTeam " +

                    "FROM    TEAM LEFT JOIN BE ON TEAM.idTeam = BE.idTeam " +
                            "LEFT JOIN USER ON USER.idUser = BE.idUser " +
                            "LEFT JOIN STAGE ON STAGE.idStage = TEAM.idCurrentStage " +

                    "WHERE   TEAM.idHunt = "+idHunt+" AND USER.idUser = " + idUser, null);
            Log.v("Hunt Activity", "info prelevate: " + c.getCount());

            if (c.moveToFirst()) {
                do {
                    idStage = c.getInt(c.getColumnIndex("idStage"));
                    numStage = c.getInt(c.getColumnIndex("numStage"));
                    areaLat = c.getFloat(c.getColumnIndex("areaLat"));
                    areaLon = c.getFloat(c.getColumnIndex("areaLon"));
                    lat = c.getFloat(c.getColumnIndex("lat"));
                    lon = c.getFloat(c.getColumnIndex("lon"));
                    ray = c.getInt(c.getColumnIndex("ray"));
                    clue =  c.getString(c.getColumnIndex("clue"));
                    isLocationRequired = c.getInt(c.getColumnIndex("isLocationRequired"));
                    isCheckRequired = c.getInt(c.getColumnIndex("isCheckRequired"));
                    isPhotoRequired = c.getInt(c.getColumnIndex("isPhotoRequired"));
                    idTeam = c.getInt(c.getColumnIndex("idTeam"));


                } while (c.moveToNext());

            }





        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQ && resultCode == RESULT_OK) {


            boolean upload = uploadImage();

            if (upload) {
                String u = "http://jbossews-treasurehunto.rhcloud.com/StageOperation?action=setIsPhotoSended&idStage=" + idStage + "&idUser=" + idUser;
                String res = null;
                try {
                    res = new RetrieveJson().execute(u).get();
                    if (!res.trim().equals("0")) {
                        Log.d("Hunt Activity", "res: " + res);

                        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        db.execSQL("UPDATE STAGE SET isSended = 1 WHERE idStage = " + idStage + ";");
                        Log.d("Hunt Activity", "dopo l'upload");

                    } else {
                        //toDo mettere il text di tutti i toast nelle variabili
                        Toast toast = Toast.makeText(this, "Si è verificato un errore. Prova a ricarare la foto.", Toast.LENGTH_SHORT);
                        toast.show();
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }






        }

    }

    public boolean uploadImage() {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            while (width > 1000 || height > 1000) {
                width = (int) (width * 0.5);
                height = (int) (height * 0.5);
            }
            resized = Bitmap.createScaledBitmap(bitmap, width, height, true);

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);


            //write the bytes in file
            FileOutputStream fos;

            byte[] bitmapdata = bos.toByteArray();
            fos = new FileOutputStream(photo);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            String path = idHunt+"/"+idTeam+"/"+idUser;
            ArrayList<Object> list = new ArrayList<>();
            list.add(photo);
            list.add(path);

            Log.d("Hunt Activity", "list size: " + list.size());

            int res = new SendPhoto().execute(list).get();

            if (res == 1) {
                Toast.makeText(this, "Image Upload!", Toast.LENGTH_SHORT).show();
                return true;

            } else {

                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("Hunt Activity", "Failed to load", e);
            return false;
        }
    }

    public void float1(View view){
        Log.v("Hunt Activity", "float 1");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photo = null;
        try
        {
            // place where to store camera taken picture
            photo =  createTemporaryFile("picture", ".jpg");

        }
        catch(Exception e)
        {
            Log.v("hunt", "Can't create file to take picture!");

        }
        mImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        // start camera activity
        startActivityForResult(intent, TAKE_PHOTO_REQ);
    }

    public void float2(View view){
        Log.v("Hunt Activity", "float 2");

    }

    public void float3(View view){
        Log.v("Hunt Activity", "float 3");

    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        Log.v("Hunt Activity", "File.createTempFile(part, ext, tempDir) : "+File.createTempFile(part, ext, tempDir).toString());

        return File.createTempFile(part, ext, tempDir);
    }

}
