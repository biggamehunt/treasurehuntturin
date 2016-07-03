package com.example.andrea22.gamehunt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;
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
    WebSocketClient mWebSocketClient;
    int idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spinner=(ProgressBar)findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
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
        try {

            try {

                String u = "http://jbossews-treasurehunto.rhcloud.com/ProfileOperation?action=login&username=" + username + "&password=" + password;
                String res = new RetrieveFeedTask().execute(u).get();




                if (!res.equals("0")) {


                    DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                    SQLiteDatabase db = myHelper.getWritableDatabase();
                    idUser = myHelper.createDB(db, res);

                    connectWebSocket();


                    SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("idUser", idUser);
                    editor.commit();
                    Intent intent = new Intent(this, HuntListActivity.class);
                    startActivity(intent);
                } else {
                    CharSequence text = getString(R.string.login_error);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(this, text, duration);
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

    @Override
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
    }


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


