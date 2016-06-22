package com.example.andrea22.gamehunt;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.andrea22.gamehunt.utility.DBHelper;
import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;

import org.json.JSONArray;
import org.json.JSONObject;

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
        String username = usernameview.getText().toString();
        Log.d("test debug", "username:" + username);

        EditText passwordview = (EditText) findViewById(R.id.password);
        String password = passwordview.getText().toString();
        Log.d("test debug", "password:" + password);

        try {

            try {
                DBHelper mDbHelper = new DBHelper(getApplicationContext());
                SQLiteDatabase db = mDbHelper.getWritableDatabase();



                String u = "http://jbossews-treasurehunto.rhcloud.com/ProfileOperation?action=login&username=" + username + "&password=" + password;
                String res = new RetrieveFeedTask().execute(u).get();

                if (res != "0"){
                    JSONObject user = new JSONObject(res);

                    ContentValues values = new ContentValues();
                    values.put(DBHelper.UserTable.COLUMN_IDUSER, user.getString("idUser"));
                    values.put(DBHelper.UserTable.COLUMN_USERNAME, user.getString("username"));
                    values.put(DBHelper.UserTable.COLUMN_EMAIL, user.getString("email"));
                    values.put(DBHelper.UserTable.COLUMN_PHOTO, user.isNull("photo") == false ? user.getString("photo") : "");
                    values.put(DBHelper.UserTable.COLUMN_PHONE, user.isNull("phone" ) == false ? user.getString("phone") : "");

                    long newRowId;
                    newRowId = db.insert(DBHelper.UserTable.TABLE_NAME,null,values);
                    Log.v("db log", "Insert User eseguito");

                    JSONArray hunts_create = user.getJSONArray("hunts");
                    JSONObject hunt = null;
                    if (hunts_create!=null) {
                        for (int i = 0; i < hunts_create.length(); i++) {

                            hunt = hunts_create.getJSONObject(i);
                            values = new ContentValues();
                            values.put(DBHelper.HuntTable.COLUMN_NAME, hunt.getString("name"));
                            values.put(DBHelper.HuntTable.COLUMN_IDHUNT, hunt.getString("idHunt"));
                            values.put(DBHelper.HuntTable.COLUMN_MAX_TEAM, hunt.getString("maxTeam"));
                            values.put(DBHelper.HuntTable.COLUMN_TIME_START, hunt.getString("timeStart"));
                            values.put(DBHelper.HuntTable.COLUMN_TIME_END, hunt.getString("timeEnd"));
                            values.put(DBHelper.HuntTable.COLUMN_DESCRIPTION, hunt.isNull("description") == false ? hunt.getString("description") : "");
                            values.put(DBHelper.HuntTable.COLUMN_ISFINISHED, hunt.getString("isFinished"));
                            values.put(DBHelper.HuntTable.COLUMN_IDUSER, user.getString("idUser"));

                            newRowId = db.insert(DBHelper.HuntTable.TABLE_NAME, null, values);
                            Log.v("db log", "Insert Hunt eseguito");

                        }
                    }

                    Log.d("test debug", "res after:" + res);
                    Intent intent = new Intent(this, HuntListActivity.class);
                    startActivity(intent);
                }




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


