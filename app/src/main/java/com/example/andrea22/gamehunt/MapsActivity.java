package com.example.andrea22.gamehunt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.andrea22.gamehunt.utility.DBHelper;
import com.example.andrea22.gamehunt.utility.DistanceCalculator;
import com.google.android.gms.maps.CameraUpdate;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Circle circle;
    Marker stagelocation = null;
    Marker finallocation = null;
    SeekBar seek;
    ScrollView scrollview;
    ImageView transparentImageView;

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
        setContentView(R.layout.fragment_newstage);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        scrollview = (ScrollView) findViewById(R.id.scrollViewNewStage);
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
                        Log.v("maps","distanza maggiore");
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

    public void TurnToHunt (View view) {

        //todo: inserire su string i toast

        if (stagelocation == null) {
            CharSequence text = " AREA DELLO STAGE MANCANTE!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        } else if (finallocation == null) {
            CharSequence text = "PUNTO DI ARRIVO MANCANTE";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        } else {
            EditText clue = (EditText) findViewById(R.id.clueHunt);
            String clueText =clue.getText().toString();

            Switch islocreq = (Switch) findViewById(R.id.islocreq);
            boolean islocreqText =islocreq.isChecked();

            Switch isphotoreq = (Switch) findViewById(R.id.isphotoreq);
            boolean isphotoreqText =isphotoreq.isChecked();

            Switch ischeckreq = (Switch) findViewById(R.id.ischeckreq);
            boolean ischeckreqText =ischeckreq.isChecked();

            EditText numberComplete = (EditText) findViewById(R.id.numberComplete);
            int numberCompleteText =Integer.parseInt(numberComplete.getText().toString());


            int rayText =seek.getProgress() + 50;

            double areaLat = stagelocation.getPosition().latitude;
            double areaLon = stagelocation.getPosition().longitude;

            double lat = finallocation.getPosition().latitude;
            double lon = finallocation.getPosition().longitude;
            
            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
            SQLiteDatabase db = myHelper.getWritableDatabase();

            myHelper.addStage(db, clueText, rayText, areaLat, areaLon, lat, lon, islocreqText, isphotoreqText, ischeckreqText,numberCompleteText);
            onBackPressed();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
    }






}
