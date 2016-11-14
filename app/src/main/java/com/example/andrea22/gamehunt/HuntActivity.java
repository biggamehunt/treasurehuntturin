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
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.DistanceCalculator;
import com.example.andrea22.gamehunt.AsyncTask.RetrieveJson;
import com.example.andrea22.gamehunt.AsyncTask.SendPhoto;
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
    private int idHunt, idTeam, idStage, idUser, idWinner;
    private static HuntActivity parent;

    File photo;
    final int TAKE_PHOTO_REQ = 100;

    private String clue, nameHunt, nameStage, nameTeam, sloganTeam, nameWinner;
    private int numStage, ray, isLocationRequired, isCheckRequired, isPhotoRequired, isCompleted, isPhotoSended, isPhotoChecked, userCompleted, teamCompleted;
    private int isStarted, isEnded;
    private float areaLat, areaLon, lat, lon;

    ImageButton centralButton, clueButton, teamButton;
    TextView clueText, teamTitle, teamSlogan, stageTitle, tvFinal, winTeam;
    LinearLayout containerMembers;
    FrameLayout flFinal;
    Toolbar bottomBar;

    Bitmap resized;
    //WebSocketClient mWebSocketClient;

    private ViewGroup hiddenPanel, hiddenTeam;
    private boolean isPanelShown, isTeamShown;

    ArrayList<String> members;
    ArrayList<Integer> membersDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt);
        parent = this;
        Intent intent = getIntent();

        idHunt = intent.getIntExtra("idHunt",-1);

        isStarted = intent.getIntExtra("isStarted", -1);
        isEnded = intent.getIntExtra("isEnded",-1);

        Log.v("Hunt Activity", "isStarted:"+isStarted);
        Log.v("Hunt Activity", "isEnded:"+isEnded);

        bottomBar = (Toolbar) findViewById(R.id.bot);
        centralButton = (ImageButton)findViewById(R.id.central);
        clueButton = (ImageButton)findViewById(R.id.clue);
        teamButton = (ImageButton)findViewById(R.id.team);
        clueText = (TextView)findViewById(R.id.clueText);
        stageTitle = (TextView)findViewById(R.id.stageTitle);

        teamTitle = (TextView)findViewById(R.id.teamTitle);
        teamSlogan = (TextView)findViewById(R.id.teamSlogan);
        hiddenPanel = (ViewGroup)findViewById(R.id.hidden_panel);
        hiddenPanel.setVisibility(View.GONE);
        hiddenTeam = (ViewGroup)findViewById(R.id.hidden_panel_team);
        hiddenTeam.setVisibility(View.GONE);
        isPanelShown = isTeamShown = false;

        containerMembers = (LinearLayout) findViewById(R.id.membersContainer);
        members = new ArrayList<>();
        membersDone = new ArrayList<>();

        flFinal = (FrameLayout) findViewById(R.id.flFinal);
        tvFinal = (TextView) findViewById(R.id.tvFinal);
        winTeam = (TextView) findViewById(R.id.winTeam);

        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        idUser = pref.getInt("idUser", 0);
        boolean checkComplete = completeHunt();

        if(checkComplete == false) {

            if (isStarted == 1 && isEnded == 0) {
                Log.v("Hunt Activity", "in corso!");


                setStageMap();
                setTeamList();

                teamTitle.setText(nameTeam);
                teamSlogan.setText(sloganTeam);
                stageTitle.setText(nameStage);
                if (clue == null || clue.isEmpty() || clue.equals("")) {
                    clueText.setText(getResources().getString(R.string.clueEmpty));
                } else {
                    clueText.setText(clue);
                }

                Log.v("Hunt Activity", "members" + members);
                Log.v("Hunt Activity", "members" + membersDone);

                for (int i = 0; i < members.size(); i++) {
                    LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.content_member_team, null, true);
                    ((TextView) ll.getChildAt(1)).setText(members.get(i));

                    if (membersDone.get(i) == 1) {
                        Log.v("Hunt Activity", "membersDone.get(i) == 1");

                        (ll.getChildAt(2)).setVisibility(View.VISIBLE);
                    }

                    containerMembers.addView(ll);

                }
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

            } else if (isStarted == 0 && isEnded == 0) {
                Log.v("Hunt Activity", "deve partire ancora...");

                String timeStart = intent.getStringExtra("timeStart");
                Log.v("Hunt Activity", "timeStart:" + timeStart);

                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.dialogTitle))
                        .setMessage(getResources().getString(R.string.dialogMessage) + " " + timeStart)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                bottomBar.setVisibility(View.GONE);

            } else if (isStarted == 1 && isEnded == 1) {
                Log.v("Hunt Activity", "è finita...");

                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.dialogTitle1))
                        .setMessage(getResources().getString(R.string.dialogMessage1))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                bottomBar.setVisibility(View.GONE);
            } else {
                //todo da gestire
            }
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

    public void setTeamList(){
        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {
            Cursor c = db.rawQuery(
                    "SELECT USER.username, BE.isComplete FROM " +
                            "TEAM LEFT JOIN BE ON TEAM.idTeam = BE.idTeam " +
                            "LEFT JOIN USER ON USER.idUser = BE.idUser " +
                            "WHERE TEAM.idTeam = " + idTeam, null);

            Log.v("Hunt Activity", "info prelevate: " + c.getCount());
            Log.v("Hunt Activity", "ID TEAM: " + idTeam);
            if (c.moveToFirst()) {
                do {
                    members.add(c.getString(c.getColumnIndex("username")));
                    membersDone.add(c.getInt(c.getColumnIndex("isComplete")));
                    Log.v("Hunt Activity", "username: " + c.getString(c.getColumnIndex("username"))+", isComplete: "+c.getInt(c.getColumnIndex("isComplete")));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                            "STAGE.isPhotoSended, " +
                            "STAGE.isPhotoChecked, " +
                            "STAGE.userCompleted, " +
                            "STAGE.teamCompleted, " +

                            "HUNT.name AS nameHunt, " +
                            "TEAM.isCompleted, " +
                            "TEAM.idTeam, " +
                            "TEAM.name, " +
                            "TEAM.slogan " +

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
                    nameStage = c.getString(c.getColumnIndex("nameStage"));
                    clue =  c.getString(c.getColumnIndex("clue"));
                    isLocationRequired = c.getInt(c.getColumnIndex("isLocationRequired"));
                    isCheckRequired = c.getInt(c.getColumnIndex("isCheckRequired"));
                    isPhotoRequired = c.getInt(c.getColumnIndex("isPhotoRequired"));
                    idTeam = c.getInt(c.getColumnIndex("idTeam"));
                    nameTeam = c.getString(c.getColumnIndex("name"));
                    sloganTeam = c.getString(c.getColumnIndex("slogan"));
                    isCompleted = c.getInt(c.getColumnIndex("isCompleted"));
                    nameHunt = c.getString(c.getColumnIndex("nameHunt"));

                    isPhotoSended = c.getInt(c.getColumnIndex("isPhotoSended"));
                    isPhotoChecked = c.getInt(c.getColumnIndex("isPhotoChecked"));
                    teamCompleted = c.getInt(c.getColumnIndex("teamCompleted"));
                    userCompleted = c.getInt(c.getColumnIndex("userCompleted"));

                    Log.v("Hunt Activity", "isPhotoSended:"+isPhotoSended);
                    Log.v("Hunt Activity", "isPhotoChecked:"+isPhotoChecked);
                    Log.v("Hunt Activity", "teamCompleted:"+teamCompleted);
                    Log.v("Hunt Activity", "userCompleted:"+userCompleted);

                    //todo: nella socket fare un instanceof per capire se l'user si trova in quest'activity e in questo stage
                    if (isCompleted == 1){
                        /*
                        Log.v("Hunt Activity", "isCompleted == 1");

                        //todo: sistemare i toast
                        centralButton.setClickable(false);
                        Toast toast = Toast.makeText(this, "La caccia è completa!", Toast.LENGTH_SHORT);
                        toast.show();
                        */
                        tvFinal.setText("La caccia è completa!");
                        flFinal.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        flFinal.setVisibility(View.VISIBLE);

                    } else if (teamCompleted == 1){
                        Log.v("Hunt Activity", "teamCompleted == 1");

                        /*

                        //todo: fare il go to next stage
                        // questo if dovrebbe essere inverificabie...
                        centralButton.setClickable(false);

                        Toast toast = Toast.makeText(this, "Il team ha completato lo stage!", Toast.LENGTH_SHORT);
                        toast.show();
                        */
                        tvFinal.setText("Il team ha completato lo stage!");
                        flFinal.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        flFinal.setVisibility(View.VISIBLE);

                    } else if (userCompleted == 1){
                        Log.v("Hunt Activity", "userCompleted == 1");

                        centralButton.setClickable(false);
                        Toast toast = Toast.makeText(this, "Hai completato lo stage, ma aspetti ancora i tuoi compagni!", Toast.LENGTH_SHORT);
                        toast.show();


                    } else if (isPhotoSended == 1 && isCheckRequired == 1){
                        Log.v("Hunt Activity", "isPhotoSended == 1 && isCheckRequired == 1");

                        final Toast toast = Toast.makeText(this, "Devi aspettare la conferma del creatore della caccia per poter andare avanti!", Toast.LENGTH_SHORT);
                        toast.show();

                        centralButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toast.show();

                            }
                        });
                    }
                } while (c.moveToNext());
            }
            Log.v("Hunt Activity", "numStage:"+numStage);
            Log.v("Hunt Activity", "lat:" + lat);
            Log.v("Hunt Activity", "lon:"+lon);
            Log.v("Hunt Activity", "nameStage:"+nameStage);
            Log.v("Hunt Activity", "nameHunt:"+nameHunt);
            Log.v("Hunt Activity", "clue:"+clue);
            Log.v("Hunt Activity", "members:"+members);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean completeHunt(){
        DBHelper mDbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {
            Cursor c = db.rawQuery(
                    "SELECT TEAM.idTeam, " +
                            "HUNT.idWinner, " +
                            "HUNT.nameWinner, " +
                            "HUNT.name " +

                            "FROM TEAM LEFT JOIN BE ON TEAM.idTeam = BE.idTeam " +
                            "LEFT JOIN USER ON USER.idUser = BE.idUser " +
                            "LEFT JOIN STAGE ON STAGE.idStage = TEAM.idCurrentStage " +
                            "LEFT JOIN HUNT ON HUNT.idHunt = TEAM.idHunt " +

                            "WHERE   TEAM.idHunt = "+idHunt+" AND USER.idUser = " + idUser, null);
            Log.v("Hunt Activity", "info prelevate: " + c.getCount());

            if (c.moveToFirst()) {
                do {
                    idTeam = c.getInt(c.getColumnIndex("idTeam"));
                    idWinner = c.getInt(c.getColumnIndex("idWinner"));
                    nameWinner = c.getString(c.getColumnIndex("nameWinner"));
                    nameHunt = c.getString(c.getColumnIndex("name"));


                }while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("Hunt Activity", "idUser:"+idUser);
        Log.v("Hunt Activity", "idHunt:"+idHunt);
        Log.v("Hunt Activity", "idTeam:"+idTeam);
        Log.v("Hunt Activity", "idWinner:"+idWinner);
        Log.v("Hunt Activity", "nameWinner:"+nameWinner);
        Log.v("Hunt Activity", "nameHunt:"+nameHunt);

        if(idWinner == 0){
            return false;
        }

        if(idTeam == idWinner){
            tvFinal.setText(getResources().getString(R.string.winner));
            flFinal.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tvFinal.setText(getResources().getString(R.string.loser));
            flFinal.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        winTeam.setText(getResources().getString(R.string.winTeam) + " " + nameWinner);
        flFinal.setVisibility(View.VISIBLE);
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQ && resultCode == RESULT_OK) {

            Log.d("Hunt Activity", "mImageUri:"+mImageUri);
            /*Uri imageUri = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            boolean upload = false;
            try {
                upload = uploadImage();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

                        if (jsonRes.getInt("teamIsCompleted")==1) {
                            Log.d("Hunt Activity", "teamIsCompleted");
                            centralButton.setClickable(false);
                            //cl.setVisibility(View.INVISIBLE);
                            //frame.setVisibility(View.VISIBLE);

                        } else if (jsonRes.getInt("userIsCompleted")==1) {
                            Log.d("Hunt Activity", "userIsCompleted");
                            centralButton.setClickable(false);
                            //frame.setVisibility(View.VISIBLE);
                        } else if (jsonRes.getInt("userIsCompleted")==0 && jsonRes.getInt("userIsPhotoSended")==1){
                            final Toast toast = Toast.makeText(this, "Devi aspettare la conferma del creatore della caccia per poter andare avanti!", Toast.LENGTH_SHORT);
                            centralButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    toast.show();

                                }
                            });
                            LoginActivity.mWebSocketClient.send("ps:" + jsonRes.getInt("idAdmin") + "-" + idHunt);
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

    public boolean uploadImage() throws ExecutionException, InterruptedException {

        Log.d("Hunt Activity", "mImageUri:" + mImageUri);
        Log.d("Hunt Activity", "getContentResolver:" + getContentResolver());
        Uri url = mImageUri;
        File file = photo;
        if (url == null){
            Log.v("HuntActivity","mImageUri == null");

            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
            url = Uri.parse(pref.getString("mImageUri",""));
        }
        if (file == null){
            Log.v("HuntActivity","photo == null");

            file = new File(url.getPath());
        }
        Log.v("HuntActivity", idHunt+"/"+idStage+"/"+idUser);

        Log.v("HuntActivity","nuuuuuu::::"+url);
        getContentResolver().notifyChange(url, null);

        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, url);

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
            fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            String path = idHunt+"/"+idStage+"/"+idUser;
            ArrayList<Object> list = new ArrayList<>();
            list.add(file);
            list.add(path);
            list.add(nameStage);

            Log.d("Hunt Activity", "list size: " + list.size());

            int res = new SendPhoto(this).execute(list).get();

            if (res == 1) {
                Toast.makeText(this, getResources().getString(R.string.toastMessage), Toast.LENGTH_SHORT).show();
                return true;

            } else {

                Toast.makeText(this, getResources().getString(R.string.toastMessage1), Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.toastMessage1), Toast.LENGTH_SHORT).show();
            Log.d("Hunt Activity", "Failed to load", e);
            return false;
        }
    }

    public void takeTeam(final View view){
        if(!isTeamShown) {
            if(isPanelShown){
                // Hide the Clue Panel
                Animation bottomDown = AnimationUtils.loadAnimation(HuntActivity.this, R.anim.bottom_down);
                ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.hidden_panel);
                hiddenPanel.startAnimation(bottomDown);
                hiddenPanel.setVisibility(View.GONE);
                isPanelShown = false;
            }
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(HuntActivity.this, R.anim.bottom_up);
            ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.hidden_panel_team);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            isTeamShown = true;
        }
        else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(HuntActivity.this, R.anim.bottom_down);
            ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.hidden_panel_team);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
            isTeamShown = false;
        }
    }

    public void takeClue(final View view) {
        if(!isPanelShown) {
            if(isTeamShown){
                // Hide the Team Panel
                Animation bottomDown = AnimationUtils.loadAnimation(HuntActivity.this, R.anim.bottom_down);
                ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.hidden_panel_team);
                hiddenPanel.startAnimation(bottomDown);
                hiddenPanel.setVisibility(View.GONE);
                isTeamShown = false;
            }
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(HuntActivity.this, R.anim.bottom_up);
            ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.hidden_panel);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            isPanelShown = true;
        }
        else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(HuntActivity.this, R.anim.bottom_down);
            ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.hidden_panel);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
            isPanelShown = false;
        }
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

            //todo: mettere distance tra le costanti e riportarlo a 50
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
                            Toast toast = Toast.makeText(this, getResources().getString(R.string.errorToast), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    tvFinal.setText(getResources().getString(R.string.arrivedToast));
                    flFinal.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    flFinal.setVisibility(View.VISIBLE);

                    //Toast.makeText(this, getResources().getString(R.string.arrivedToast), Toast.LENGTH_SHORT).show();
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
                        Log.v("hunt open camera", "Can't create file to take picture!");

                    }

                    mImageUri = Uri.fromFile(photo);
                    Log.v("hunt open camera", "mImageUri:" + mImageUri);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("mImageUri", mImageUri.toString());
                    editor.commit();

                    // start camera activity
                    startActivityForResult(intent, TAKE_PHOTO_REQ);

                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.notArrivedToast) + " " + (int)distance + " m", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void goToNextStage(View view) {

        Intent intent = new Intent(this, HuntActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("idHunt", idHunt);
        intent.putExtra("isStarted", isStarted);
        intent.putExtra("isEnded", isEnded);

        startActivity(intent);

       // overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HuntListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
    }
}
