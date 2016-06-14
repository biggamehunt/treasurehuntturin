package com.example.andrea22.gamehunt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity   {
    URL url;
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view)  {

        EditText usernameview = (EditText) findViewById(R.id.username);
        String username =usernameview.getText().toString();
        EditText passwordview = (EditText) findViewById(R.id.password);
        String password = passwordview.getText().toString();
        try {

            /*URL url = new URL("http://www.android.com/");
            urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                readStream(in);*/

            URL url;

            try {
                String u = "http://jbossews-treasurehunto.rhcloud.com/ProfileOperation?action=login&username=" + username + "&password=" + password;

                String res = new RetrieveFeedTask().execute(u).get();

                Log.d("test debug", "res after:" + res);
            } catch (Exception e) {
                e.printStackTrace();
            } /*finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }*/



           // } else {
               // Log.d("test debug", "NO CONNECTION");
          //  }

/*
            Log.d("test debug:", "username:"+username);
            Log.d("test debug:", "password:"+password);
            String u = "http://jbossews-treasurehunto.rhcloud.com/ProfileOperation?action=login&username=" + username + "&password=" + password;
            url = new URL(u);
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("test debug", "dopo url "+u);
            InputStream isw = new BufferedInputStream(urlConnection.getInputStream());
            Log.d("test debug", "dopo in");
            //InputStreamReader isw = new InputStreamReader(in);
            Log.d("test debug", "dopo isw");

            int data = isw.read();
            Log.d("test debug", "data:"+data);
            if((char)data=='0'){
                Context context = getApplicationContext();
                CharSequence text = "Login non valido";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(
                        Gravity.CENTER, 0, 0);
                toast.show();
            }else {


                Log.d("res", "" + data);
                String current = "";

                //TextView t = (TextView) findViewById(R.id.prova);
                Log.d("test debug", "ci sono!");


                while (data != -1) {
                    current += (char) data;
                    data = isw.read();
                }

            }*/







        } catch (Exception e) {
            Log.d("test debug", "eccezione: "+e.getMessage());
        }


    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}

class RetrieveFeedTask extends AsyncTask<String, Void, String> {

    private Exception exception;

    protected String doInBackground(String... urls) {
        try {
            HttpURLConnection urlConnection = null;
           // String u = "http://jbossews-treasurehunto.rhcloud.com/ProfileOperation?action=login&username=" + username + "&password=" + password;

            URL url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();

            String res="";
            while (data != -1) {
                char current = (char) data;
                res+=current;
                data = isw.read();
            }
            return res;

        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
