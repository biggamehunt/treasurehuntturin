package com.example.andrea22.gamehunt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.DistanceCalculator;
import com.example.andrea22.gamehunt.utility.RetrieveJson;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NewStageActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Circle circle;
    Marker stagelocation = null;
    Marker finallocation = null;
    SeekBar seek;
    ScrollView scrollview;
    ImageView transparentImageView;
    EditText name;

    int numStage;

    @Override
    protected void onResume (){
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stage);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        numStage = getIntent().getIntExtra("numStage",0);
        Log.v("maps", "numStage: " + numStage);

        scrollview = (ScrollView) findViewById(R.id.scrollViewNewStage);
        name =  (EditText) findViewById(R.id.stageName);
        name.setText("Stage " + (numStage+1));

        transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollview.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollview.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollview.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });



        seek = (SeekBar) findViewById(R.id.ray);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (finallocation != null) {
                    double distance = DistanceCalculator.distance(stagelocation.getPosition().latitude, stagelocation.getPosition().longitude, finallocation.getPosition().latitude, finallocation.getPosition().longitude, "K");
                    if (distance > seekBar.getProgress() + 50) {
                        Log.v("maps", "distanza maggiore");
                        finallocation.remove();
                        finallocation = null;
                    }

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                if (circle != null) {
                    circle.setRadius(progress + 50);
                }


                // Here call your code when progress will changes
            }
        });

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




        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                if (stagelocation != null) {
                    double distance = DistanceCalculator.distance(point.latitude, point.longitude, stagelocation.getPosition().latitude, stagelocation.getPosition().longitude, "K");
                    if ((distance) <= (seek.getProgress() + 50)) {
                        if (finallocation != null) {
                            finallocation.remove();
                        }
                        finallocation = mMap.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("Obbiettivo!").snippet("This is the position of the stage"));

                    } else {
                        if (finallocation != null) {
                            finallocation.remove();
                            finallocation = null;
                        }
                        stagelocation.remove();
                        circle.remove();
                        stagelocation = mMap.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("New Stage!").snippet("This is the position of the stage"));
                        stagelocation.setVisible(false);
                        circle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(point.latitude, point.longitude))
                                        //// TODO: fare il get dalla textbox
                                        // TODO: METTERE IL 50 TRA LE COSTANTI
                                .radius(seek.getProgress() + 50)
                                .strokeColor(0x3500ff00)
                                .strokeWidth(3)
                                        //// TODO: inserire sta roba in colors.xml
                                        // 0x represents, this is an hexadecimal code
                                        // 55 represents percentage of transparency. For 100% transparency, specify 00.
                                        // For 0% transparency ( ie, opaque ) , specify ff
                                        // The remaining 6 characters(00ff00) specify the fill color
                                .fillColor(0x2500ff00));
                    }
                } else {

                    stagelocation = mMap.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("New Stage!").snippet("This is the position of the stage"));
                    stagelocation.setVisible(false);
                    circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(point.latitude, point.longitude))
                                    //// TODO: fare il get dalla textbox
                            .radius(seek.getProgress() + 50)
                            .strokeColor(0x3500ff00)
                            .strokeWidth(3)

                                    //// TODO: inserire sta roba in colors.xml
                                    // 0x represents, this is an hexadecimal code
                                    // 55 represents percentage of transparency. For 100% transparency, specify 00.
                                    // For 0% transparency ( ie, opaque ) , specify ff
                                    // The remaining 6 characters(00ff00) specify the fill color
                            .fillColor(0x2500ff00));
                }
                //Do your stuff with LatLng here
                //Then pass LatLng to other activity
            }
        });


    }

    public void turnHunt(View v) {

        //todo: inserire su string i toast

        if (stagelocation == null) {
            CharSequence text = " AREA DELLO STAGE MANCANTE!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (finallocation == null) {
            CharSequence text = "PUNTO DI ARRIVO MANCANTE";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            EditText clue = (EditText) findViewById(R.id.clueHunt);
            String clueText = clue.getText().toString();

            String nameText = name.getText().toString();
            Log.v("maps", "nameText: "+nameText);


            if (nameText == null || nameText.equals("")){
                nameText = "Stage " + (numStage+1);
            }


            CheckBox islocreq = (CheckBox) findViewById(R.id.islocreq);
            CheckBox isphotoreq = (CheckBox) findViewById(R.id.isphotoreq);
            CheckBox ischeckreq = (CheckBox) findViewById(R.id.ischeckreq);

            int islocreqText = 0;
            if (islocreq.isChecked()) {
                islocreqText = 1;
                Log.v("maps", "location a 1");


            }

            int isphotoreqText = 0;
            if (isphotoreq.isChecked()) {
                isphotoreqText = 1;
            }

            int ischeckreqText = 0;
            if (ischeckreq.isChecked()) {
                ischeckreqText = 1;
            }


            EditText numberComplete = (EditText) findViewById(R.id.numberComplete);
            int numberCompleteText = 1;

            try {
                numberCompleteText = Integer.parseInt(numberComplete.getText().toString());
            } catch(Exception e){
                e.printStackTrace();
                numberCompleteText = 1;
            }





            int rayText =seek.getProgress() + 50;

            double areaLat = stagelocation.getPosition().latitude;
            double areaLon = stagelocation.getPosition().longitude;

            double lat = finallocation.getPosition().latitude;
            double lon = finallocation.getPosition().longitude;
            
            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
            SQLiteDatabase db = myHelper.getWritableDatabase();
            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

            Log.v("maps", "idLastHunt: " + pref.getInt("idLastHunt", 0));


            myHelper.insertAddStage(db, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0),numStage, nameText, clueText, rayText, areaLat, areaLon, lat, lon, islocreqText, isphotoreqText, ischeckreqText, numberCompleteText);

            Intent intent = new Intent();

            intent.putExtra("name", nameText);
            intent.putExtra("isCheckRequired", ischeckreqText);
            intent.putExtra("isPhotoRequired", isphotoreqText);
            intent.putExtra("isLocationRequired", islocreqText);


            setResult(RESULT_OK, intent);

            finish();
            overridePendingTransition(R.anim.back_enter, R.anim.back_exit);

        }

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
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void turnBack(View view) {
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }


}
