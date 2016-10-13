package com.example.andrea22.gamehunt;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;
import com.example.andrea22.gamehunt.utility.RetrieveLoginTask;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ProgressBar spinner;
    public static WebSocketClient mWebSocketClient;

    public Context context;

    int idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spinner=(ProgressBar)findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        context = this;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void login(View view) {


        spinner.setVisibility(View.VISIBLE);



        EditText usernameview = (EditText) findViewById(R.id.username);
        String username = usernameview.getText().toString();

        EditText passwordview = (EditText) findViewById(R.id.password);
        String password = passwordview.getText().toString();
        //Task spinnerTask;
/*
        if(username.length() < 4){
            CharSequence text = getString(R.string.userLength_error);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if(password.length() < 7){
            CharSequence text = getString(R.string.passLength_error);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);

            toast.show();
        } else { */
            try {

                try {
                    String username_ut8 = java.net.URLEncoder.encode(username, "UTF-8");
                    String password_ut8 = java.net.URLEncoder.encode(password, "UTF-8");

                    String u = "http://jbossews-treasurehunto.rhcloud.com/ProfileOperation?action=login&username=" + username_ut8 + "&password=" + password_ut8;
                    String res = new RetrieveLoginTask().execute(u).get();


                    if (!res.equals("0")) {


                        DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                        SQLiteDatabase db = myHelper.getWritableDatabase();
                        idUser = myHelper.createDB(db, res);

                        connectWebSocket();


                        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("idUser", idUser);
                        editor.putString("username", username);

                        editor.commit();
                        Intent intent = new Intent(this, HuntListActivity.class);
                        startActivity(intent);
                    } else {
                        CharSequence text = getString(R.string.login_error);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(this, text, duration);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }


                } catch (Exception e) {
                    Log.d("test debug", "eccezione: " + e.getMessage());
                    e.printStackTrace();
                }


            } catch (Exception e) {
                Log.d("test debug", "eccezione: " + e.getMessage());
                e.printStackTrace();

            }
        }

  //  }
//TODO: inserire questa funzione in tutte le activity che usano tastiera
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }



    public void registration(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    /*@Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.andrea22.gamehunt/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.andrea22.gamehunt/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }*/


    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://ws-treasurehunto.rhcloud.com:8000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("id:"+idUser);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*TextView textView;
                        textView = (TextView)findViewById(R.id.messages);
                        textView.setText(textView.getText() + "\n" + message);*/
                        Log.i("Websocket", "message:"+message);
                        if (message.substring(0,2).equals("up")){ //up=update
                            Log.i("Websocket", "up");

                            String[] firstSplit = message.substring(3).split("-");
                            int idStage = Integer.parseInt(firstSplit[0]);
                            int nextSage = Integer.parseInt(firstSplit[1]);
                            int idTeam = Integer.parseInt(firstSplit[2]);
                            int idHunt = Integer.parseInt(firstSplit[3]);
                            String name = firstSplit[4];
                            name=name.replace("___","-");
                            name=name.replace("£$%","&");
                            boolean res;
                            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();
                            if (myHelper.getHuntIsLoaded(db,idHunt)==1) {
                                res = myHelper.notifyFromTeamStageCompleted(db, idStage, nextSage, idTeam); //todo: questo deve andare PRIMA del notificationcompat builder!
                            } else {
                                res = true;
                            }
                            if (res) {
                                NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                                        .setContentTitle("Stage Completato!")
                                        .setContentText(name)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) //Vibrazione
                                        .setLights(Color.RED, 3000, 3000) //Led
                                        .setSmallIcon(R.mipmap.ic_launcher);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, n.build());
                                Log.i("Websocket", "dopo il notification");
                            }
                        } else if (message.substring(0,2).equals("eh")){ //eh=endhunt
                            Log.i("Websocket", "eh");

                            String[] firstSplit = message.substring(3).split("-");
                            int idStage = Integer.parseInt(firstSplit[0]);
                            int idTeam = Integer.parseInt(firstSplit[1]);
                            int idHunt = Integer.parseInt(firstSplit[2]);
                            String name = firstSplit[3];
                            name=name.replace("___","-");
                            name=name.replace("£$%","&");
                            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();
                            boolean res;
                            if (myHelper.getHuntIsLoaded(db,idHunt)==1) {
                                res = myHelper.notifyFromTeamHuntCompleted(db,idStage,idTeam);
                            } else {
                                res = true;
                            }
                            if (res) {
                                NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                                        .setContentTitle("Caccia Completata!")
                                        .setContentText(name)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) //Vibrazione
                                        .setLights(Color.RED, 3000, 3000) //Led
                                        .setSmallIcon(R.mipmap.ic_launcher);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, n.build());
                                Log.i("Websocket", "dopo il notification");
                            }
                        }

                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };


        mWebSocketClient.connect();
    }














}


