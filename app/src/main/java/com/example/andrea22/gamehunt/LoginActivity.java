package com.example.andrea22.gamehunt;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;




/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view)  {

        EditText usernameview = (EditText) findViewById(R.id.username);
        String username =usernameview.getText().toString();
        Log.d("test debug", "username:" + username);

        EditText passwordview = (EditText) findViewById(R.id.password);
        String password = passwordview.getText().toString();
        Log.d("test debug", "password:" + password);

        try {

            try {
                String u = "http://jbossews-treasurehunto.rhcloud.com/ProfileOperation?action=login&username=" + username + "&password=" + password;
                String res = new RetrieveFeedTask().execute(u).get();
                Log.d("test debug", "res after:" + res);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            Log.d("test debug", "eccezione: "+e.getMessage());
        }


    }

    public void registration(View view)  {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

}


