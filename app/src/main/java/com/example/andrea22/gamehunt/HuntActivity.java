package com.example.andrea22.gamehunt;

import android.Manifest;
import android.app.AlertDialog;
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
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.DistanceCalculator;
import com.example.andrea22.gamehunt.utility.RetrieveJson;
import com.example.andrea22.gamehunt.utility.SendPhoto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HuntActivity extends FragmentActivity implements OnMapReadyCallback {

    private Uri mImageUri;
    private GoogleMap mMap;
    private int idHunt, idTeam, idStage, idUser;
    private static HuntActivity parent;

    File photo;
    final int TAKE_PHOTO_REQ = 100;

    private String clue, nameHunt, nameStage;
    private int numStage, ray, isLocationRequired, isCheckRequired, isPhotoRequired, isCompleted;
    private int isStarted, isEnded;
    private float areaLat, areaLon, lat, lon;
    boolean click;
    ImageButton centralButton, clueButton, teamButton;
    TextView clueText;
    Toolbar bottomBar;

    //FloatingActionButton photoButton, position, info;
    Bitmap resized;
    //WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt);
        parent = this;
        Intent intent = getIntent();

        click = false;
        idHunt = Integer.parseInt(intent.getStringExtra("idHunt"));

        isStarted = intent.getIntExtra("isStarted", -1);
        isEnded = intent.getIntExtra("isEnded",-1);

        Log.v("Hunt Activity", "isStarted:"+isStarted);
        Log.v("Hunt Activity", "isEnded:"+isEnded);

        //photoButton = (FloatingActionButton) findViewById(R.id.photo);
        //position = (FloatingActionButton) findViewById(R.id.position);
        //info = (FloatingActionButton) findViewById(R.id.info);

        bottomBar = (Toolbar) findViewById(R.id.bot);
        centralButton = (ImageButton)findViewById(R.id.central);
        clueButton = (ImageButton)findViewById(R.id.clue);
        teamButton = (ImageButton)findViewById(R.id.team);
        clueText = (TextView)findViewById(R.id.clueText);

        if (isStarted==1 && isEnded==0) {
            Log.v("Hunt Activity", "in corso!");
            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

            idUser = pref.getInt("idUser", 0);

            setStageMap();

            //todo: da rivedere... isComplete lo setto dopo l'if!!!

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        } else if (isStarted==0 && isEnded==0){
            Log.v("Hunt Activity", "deve partire ancora...");

            String timeStart = intent.getStringExtra("timeStart");
            Log.v("Hunt Activity", "timeStart:"+timeStart);

            new AlertDialog.Builder(this)
            .setTitle("Devi ancora aspettare!")
                    .setMessage("La caccia partirà il " + timeStart)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            bottomBar.setVisibility(View.GONE);
            //photoButton.setVisibility(View.INVISIBLE);
            //info.setVisibility(View.INVISIBLE);
            //position.setVisibility(View.INVISIBLE);
        } else if (isStarted==1 && isEnded==1){
            Log.v("Hunt Activity", "è finita...");

            new AlertDialog.Builder(this)
            .setTitle("La caccia è terminata!")
                    .setMessage("Grazie per aver giocato con noi.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            //photoButton.setVisibility(View.INVISIBLE);
            //info.setVisibility(View.INVISIBLE);
            //position.setVisibility(View.INVISIBLE);
            bottomBar.setVisibility(View.GONE);


        } else {
            //todo da gestire
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v("Hunt Activity", "onMapReady");

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

            Log.v("Hunt Activity", "isLocationRequired:"+isLocationRequired);
            Log.v("Hunt Activity", "isCompleted:" + isCompleted);

            if (isPhotoRequired == 0) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    centralButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_room_black_24dp, getApplicationContext().getTheme()));
                } else {
                    centralButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_room_black_24dp));
                }
            }

            if (isLocationRequired == 1  && isCompleted == 0) {
                Log.v("Hunt Activity", "areaLat:"+areaLat);
                Log.v("Hunt Activity", "areaLon:"+areaLon);

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
            Log.v("Hunt Activity", "isPhotoRequired:" + isPhotoRequired);





        }
    }

    public void setStageMap(){
        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {


            Cursor c = db.rawQuery(
                    "SELECT  STAGE.idStage, " +
                            "STAGE.numStage, " +
                            "STAGE.areaLat, " +
                            "STAGE.areaLon, " +
                            "STAGE.lat, " +
                            "STAGE.lon, " +
                            "STAGE.ray, " +
                            "STAGE.name AS nameStage, " +
                            "STAGE.clue, " +
                            "STAGE.isLocationRequired, " +
                            "STAGE.isCheckRequired, " +
                            "STAGE.isPhotoRequired, " +
                            "HUNT.name AS nameHunt, " +
                            "TEAM.isCompleted, " +
                            "TEAM.idTeam " +

                    "FROM    TEAM LEFT JOIN BE ON TEAM.idTeam = BE.idTeam " +
                            "LEFT JOIN USER ON USER.idUser = BE.idUser " +
                            "LEFT JOIN STAGE ON STAGE.idStage = TEAM.idCurrentStage " +
                            "LEFT JOIN HUNT ON HUNT.idHunt = TEAM.idHunt " +

                    "WHERE   TEAM.idHunt = "+idHunt+" AND USER.idUser = " + idUser, null);
            Log.v("Hunt Activity", "info prelevate: " + c.getCount());




            Log.v("Hunt Activity", "idHunt:"+idHunt);
            Log.v("Hunt Activity", "idUser:"+idUser);
            if (c.moveToFirst()) {
                do {
                    idStage = c.getInt(c.getColumnIndex("idStage"));
                    numStage = c.getInt(c.getColumnIndex("numStage"));
                    areaLat = c.getFloat(c.getColumnIndex("areaLat"));
                    areaLon = c.getFloat(c.getColumnIndex("areaLon"));
                    lat = c.getFloat(c.getColumnIndex("lat"));
                    lon = c.getFloat(c.getColumnIndex("lon"));
                    ray = c.getInt(c.getColumnIndex("ray"));
                    nameStage =  c.getString(c.getColumnIndex("nameStage"));
                    clue =  c.getString(c.getColumnIndex("clue"));
                    isLocationRequired = c.getInt(c.getColumnIndex("isLocationRequired"));
                    isCheckRequired = c.getInt(c.getColumnIndex("isCheckRequired"));
                    isPhotoRequired = c.getInt(c.getColumnIndex("isPhotoRequired"));
                    idTeam = c.getInt(c.getColumnIndex("idTeam"));
                    isCompleted = c.getInt(c.getColumnIndex("isCompleted"));
                    nameHunt = c.getString(c.getColumnIndex("nameHunt"));



                    if (isCompleted == 1){
                        Toast toast = Toast.makeText(this, "La caccia è completa!", Toast.LENGTH_SHORT);
                        toast.show();

                    }

                } while (c.moveToNext());

            }

            Log.v("Hunt Activity", "numStage:"+numStage);
            Log.v("Hunt Activity", "lat:"+lat);
            Log.v("Hunt Activity", "lon:"+lon);
            Log.v("Hunt Activity", "nameStage:"+nameStage);
            Log.v("Hunt Activity", "nameHunt:"+nameHunt);

            Log.v("Hunt Activity", "clue:"+clue);

            clueText.setText(clue);




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQ && resultCode == RESULT_OK) {


            boolean upload = uploadImage();
            String res = null;

            if (upload) {
                try {

                    String u = "http://jbossews-treasurehunto.rhcloud.com/StageOperation";

                    String p = "action=setIsPhotoSended&idStage=" + idStage + "&idUser=" + idUser;
                    String url[]= new String [2];
                    url[0] = u;
                    url[1] = p;
                    res = new RetrieveJson().execute(url).get();

                    Log.d("Hunt Activity", "res:"+res);
                    if (res.trim().equals("-1")) {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                    } else if (!res.trim().equals("0")) {




                        JSONObject jsonRes = new JSONObject(res);


                        DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                        SQLiteDatabase db = myHelper.getWritableDatabase();

                        myHelper.setAfterPhotoSended(db, res, idStage, idTeam, idUser,idHunt,nameHunt);


                        //FrameLayout frame = (FrameLayout) findViewById(R.id.rect_map);
                        //CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.coordinator_maps);
                        Log.d("Hunt Activity", "teamIsCompleted:"+jsonRes.getString("teamIsCompleted"));
                        Log.d("Hunt Activity", "userIsCompleted:"+jsonRes.getString("userIsCompleted"));

                        if (jsonRes.getString("teamIsCompleted").equals("1")) {
                            Log.d("Hunt Activity", "teamIsCompleted");
                            //cl.setVisibility(View.INVISIBLE);
                            //frame.setVisibility(View.VISIBLE);

                        } else if (jsonRes.getString("userIsCompleted").equals("1")) {
                            Log.d("Hunt Activity", "userIsCompleted");
                            //cl.setVisibility(View.INVISIBLE);

                            //frame.setVisibility(View.VISIBLE);
                        }





                    } else {
                        //toDo mettere il text di tutti i toast nelle variabili
                        Toast toast = Toast.makeText(this, "Si è verificato un errore. Prova a ricarare la foto.", Toast.LENGTH_SHORT);
                        toast.show();
                    }


                } catch (Exception e) {
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

            String path = idHunt+"/"+idStage+"/"+idUser;
            ArrayList<Object> list = new ArrayList<>();
            list.add(photo);
            list.add(path);
            list.add(nameStage);

            Log.d("Hunt Activity", "list size: " + list.size());

            int res = new SendPhoto(this).execute(list).get();

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

    public void takeClue(View view){



        if(click){
            clueText.setVisibility(View.GONE);
        } else {
            clueText.setVisibility(View.VISIBLE);
            Animation a = AnimationUtils.loadAnimation(this, R.anim.scale);
            a.reset();
            clueText.clearAnimation();
            clueText.startAnimation(a);
        }
        click = !click;
    }

    public void takePosition(View view){
        Log.v("Hunt Activity", "check location");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            // Show rationale and request permission.
        }

        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

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
            Log.v("Hunt Activity", "bestLocation != null");

            // Get latitude of the current location
            double actualLatitude = bestLocation.getLatitude();

            // Get longitude of the current location
            double actualLongitude = bestLocation.getLongitude();

            double distance = DistanceCalculator.distance(actualLatitude, actualLongitude, lat, lon, "K");

            Log.v("Hunt Activity", "actualLatitude: "+actualLatitude);
            Log.v("Hunt Activity", "actualLongitude: "+actualLongitude);
            Log.v("Hunt Activity", "lat: "+lat);
            Log.v("Hunt Activity", "lon: "+lon);

            //todo: mettere distance tra le costanti
            if (distance <= 500) {
                Log.v("Hunt Activity", "distance <= 500");

                if (isPhotoRequired == 0) {
                    String res = null;

                    try{
                        String u = "http://jbossews-treasurehunto.rhcloud.com/StageOperation";

                        JSONObject info = new JSONObject();
                        info.put("lat", actualLatitude);
                        info.put("lon", actualLongitude);
                        String json = java.net.URLEncoder.encode(info.toString(), "UTF-8");
                        Log.v("Hunt Activity", "info lat:" + info.getString("lat"));
                        Log.v("Hunt Activity", "info lon:" + info.getString("lon"));
                        Log.v("Hunt Activity", "info json:"+json);

                        String p = "action=checkLocation&idStage=" + idStage + "&idUser=" + idUser + "&json=" + json;
                        String url[]= new String [2];
                        url[0] = u;
                        url[1] = p;
                        res = new RetrieveJson().execute(url).get();
                        Log.v("Hunt Activity", "res:"+res);

                        if (res.trim().equals("-1")) {
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                        } else if (!res.trim().equals("0")) {



                            Log.v("Hunt Activity", "!res.trim().equals(0)");

                            JSONObject jsonRes = new JSONObject(res);


                            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();

                            myHelper.setAfterPhotoSended(db, res, idStage, idTeam, idUser,idHunt,nameHunt);


                            //FrameLayout frame = (FrameLayout) findViewById(R.id.rect_map);
                            //CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.coordinator_maps);
                            Log.d("Hunt Activity", "teamIsCompleted:"+jsonRes.getString("teamIsCompleted"));
                            Log.d("Hunt Activity", "userIsCompleted:"+jsonRes.getString("userIsCompleted"));

                            if (jsonRes.getString("teamIsCompleted").equals("1")) {
                                Log.d("Hunt Activity", "teamIsCompleted");
                                //cl.setVisibility(View.INVISIBLE);
                                //frame.setVisibility(View.VISIBLE);

                            } else if (jsonRes.getString("userIsCompleted").equals("1")) {
                                Log.d("Hunt Activity", "userIsCompleted");
                                //cl.setVisibility(View.INVISIBLE);

                                //frame.setVisibility(View.VISIBLE);
                            }





                        } else {
                            //toDo mettere il text di tutti i toast nelle variabili
                            Toast toast = Toast.makeText(this, "Si è verificato un errore.", Toast.LENGTH_SHORT);
                            toast.show();
                        }







                    } catch (Exception e){
                        e.printStackTrace();
                    }










                    Toast.makeText(this, "Sono arrivato!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("Hunt Activity", "open camera");
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
            } else {
                Toast.makeText(this, "Ne devo fare di strada ancora! " + distance, Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void takeTeam(View view){

    }

    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        Log.v("Hunt Activity", "File.createTempFile(part, ext, tempDir) : " + File.createTempFile(part, ext, tempDir).toString());

        return File.createTempFile(part, ext, tempDir);
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

}
