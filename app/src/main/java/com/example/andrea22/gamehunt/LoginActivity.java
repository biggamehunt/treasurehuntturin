package com.example.andrea22.gamehunt;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.AsyncTask.RetrieveLoginTask;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;


public class LoginActivity extends AppCompatActivity {

    private GoogleApiClient client;
    public static WebSocketClient mWebSocketClient;
    EditText usernameview;
    EditText passwordview;
    public Button loginButton;

    public Context context;

    public int idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameview = (EditText) findViewById(R.id.username);
        passwordview = (EditText) findViewById(R.id.password);
        loginButton= (Button)findViewById(R.id.sign_in);
        context = this;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void login(View view) {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        String username = usernameview.getText().toString();
        String password = passwordview.getText().toString();


        try {

            try {
                String username_ut8 = java.net.URLEncoder.encode(username, "UTF-8");
                String password_ut8 = java.net.URLEncoder.encode(password, "UTF-8");

                String u = "http://jbossews-treasurehunto.rhcloud.com/ProfileOperation?action=login&username=" + username_ut8 + "&password=" + password_ut8;

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                new RetrieveLoginTask(this,username).execute(u);

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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        return true;
    }

    public void registration(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void connectWebSocket() {
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
                        Log.i("Websocket", "message:" + message);
                        if (message.substring(0,2).equals("up")){ //up=update

                            String[] firstSplit = message.substring(3).split("-");
                            int idStage = Integer.parseInt(firstSplit[0]);
                            int nextStage = Integer.parseInt(firstSplit[1]);
                            int idTeam = Integer.parseInt(firstSplit[2]);
                            int idHunt = Integer.parseInt(firstSplit[3]);
                            String name = firstSplit[4];
                            name=name.replace("___","-");
                            name=name.replace("£$%","&");
                            boolean res;
                            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();
                            if (myHelper.getHuntIsLoadedIsStartedIsEnded(db,idHunt)[0]==1) {
                                res = myHelper.notifyFromTeamStageCompleted(db, idStage, nextStage, idTeam); //todo: questo deve andare PRIMA del notificationcompat builder!
                            } else {
                                res = true;
                            }
                            if (res) {
                                NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                                        .setContentTitle(getResources().getString(R.string.stageCompleted))
                                        .setContentText(name)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) //Vibrazione
                                        .setLights(Color.RED, 3000, 3000) //Led
                                        .setSmallIcon(R.mipmap.ic_launcher);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, n.build());
                                Log.i("Websocket", "dopo il notification");
                            }
                        } else if (message.substring(0,2).equals("eh")){ //eh=endhunt

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
                            if (myHelper.getHuntIsLoadedIsStartedIsEnded(db,idHunt)[0]==1) {
                                res = myHelper.notifyFromTeamHuntCompleted(db,idStage,idTeam);
                            } else {
                                res = true;
                            }
                            if (res) {
                                NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                                        .setContentTitle(getResources().getString(R.string.huntCompleted))
                                        .setContentText(name)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) //Vibrazione
                                        .setLights(Color.RED, 3000, 3000) //Led
                                        .setSmallIcon(R.mipmap.ic_launcher);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, n.build());
                                Log.i("Websocket", "dopo il notification");
                            }
                        } else if (message.substring(0,2).equals("cf")){ //cd=checkfailed

                            String[] firstSplit = message.substring(3).split("-");
                            int idStage = Integer.parseInt(firstSplit[1]);
                            int idHunt = Integer.parseInt(firstSplit[2]);
                            String name = firstSplit[3];
                            name=name.replace("___","-");
                            name=name.replace("£$%","&");
                            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();
                            boolean res;
                            if (myHelper.getHuntIsLoadedIsStartedIsEnded(db,idHunt)[0]==1) {
                                res = myHelper.notifyCheckFailed(db,idStage);
                            } else {
                                res = true;
                            }
                            if (res) {
                                NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                                        .setContentTitle(getResources().getString(R.string.photoRejected))
                                        .setContentText(name)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) //Vibrazione
                                        .setLights(Color.RED, 3000, 3000) //Led
                                        .setSmallIcon(R.mipmap.ic_launcher);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, n.build());
                                Log.i("Websocket", "dopo il notification");
                            }
                        } else if (message.substring(0,2).equals("cd")){ //cd=checkdone

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
                            if (myHelper.getHuntIsLoadedIsStartedIsEnded(db,idHunt)[0]==1) {
                                SharedPreferences pref = context.getSharedPreferences("session", context.MODE_PRIVATE);

                                res = myHelper.notifyUserComplete(db, idStage, pref.getInt("idUser",0),idTeam,idHunt);
                            } else {
                                res = true;
                            }
                            if (res) {
                                NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                                        .setContentTitle(getResources().getString(R.string.photoAccepted))
                                        .setContentText(name)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) //Vibrazione
                                        .setLights(Color.RED, 3000, 3000) //Led
                                        .setSmallIcon(R.mipmap.ic_launcher);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, n.build());
                                Log.i("Websocket", "dopo il notification");
                            }
                        } else if (message.substring(0,2).equals("ca")){ //ca=checkdoneall

                            String[] firstSplit = message.substring(3).split("-");
                            int idStage = Integer.parseInt(firstSplit[0]);
                            int nextStage = Integer.parseInt(firstSplit[1]);
                            int idTeam = Integer.parseInt(firstSplit[2]);
                            int idHunt = Integer.parseInt(firstSplit[3]);
                            String name = firstSplit[4];
                            name=name.replace("___","-");
                            name=name.replace("£$%","&");
                            boolean res;
                            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();
                            if (myHelper.getHuntIsLoadedIsStartedIsEnded(db,idHunt)[0]==1) {
                                res = myHelper.notifyFromTeamStageCompleted(db, idStage, nextStage, idTeam); //todo: questo deve andare PRIMA del notificationcompat builder!
                            } else {
                                res = true;
                            }
                            if (res) {
                                NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                                        .setContentTitle(getResources().getString(R.string.stageCompleted))
                                        .setContentText(name)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) //Vibrazione
                                        .setLights(Color.RED, 3000, 3000) //Led
                                        .setSmallIcon(R.mipmap.ic_launcher);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, n.build());
                                Log.i("Websocket", "dopo il notification");
                            }
                        } else if (message.substring(0,2).equals("ha")){ //ca=huntdoneall

                            String[] firstSplit = message.substring(3).split("-");
                            int idStage = Integer.parseInt(firstSplit[0]);
                            int idTeam = Integer.parseInt(firstSplit[1]);
                            int idHunt = Integer.parseInt(firstSplit[2]);
                            String name = firstSplit[3];
                            name=name.replace("___","-");
                            name=name.replace("£$%","&");
                            boolean res;
                            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();
                            if (myHelper.getHuntIsLoadedIsStartedIsEnded(db,idHunt)[0]==1) {
                                res = myHelper.notifyFromTeamHuntCompleted(db, idStage, idTeam); //todo: questo deve andare PRIMA del notificationcompat builder!
                            } else {
                                res = true;
                            }
                            if (res) {
                                NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                                        .setContentTitle(getResources().getString(R.string.huntCompleted))
                                        .setContentText(name)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) //Vibrazione
                                        .setLights(Color.RED, 3000, 3000) //Led
                                        .setSmallIcon(R.mipmap.ic_launcher);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, n.build());
                                Log.i("Websocket", "dopo il notification");
                            }
                        } else if (message.substring(0,2).equals("uc")){ //uc:userhascomplete

                            String[] firstSplit = message.substring(3).split("-");
                            int idUser = Integer.parseInt(firstSplit[0]);
                            int idTeam = Integer.parseInt(firstSplit[1]);
                            int idHunt = Integer.parseInt(firstSplit[2]);

                            DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();
                            if (myHelper.getHuntIsLoadedIsStartedIsEnded(db,idHunt)[0]==1) {
                                myHelper.notifyUserHasCompleted(db, idUser, idTeam); //todo: questo deve andare PRIMA del notificationcompat builder!
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

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {

        boolean valid = true;
/*
        String usernameText = usernameview.getText().toString();
        String passwordText = passwordview.getText().toString();

        if(usernameText.isEmpty() || usernameText.length() < 4){
            CharSequence text = getString(R.string.userLength_error);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            valid = false;

        } else if(passwordText.isEmpty()|| passwordText.length() < 7){
            CharSequence text = getString(R.string.passLength_error);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            valid = false;
        }
*/
        return valid;
    }


}


