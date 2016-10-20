package com.example.andrea22.gamehunt.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.HuntListActivity;
import com.example.andrea22.gamehunt.LoginActivity;
import com.example.andrea22.gamehunt.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrea22 on 15/06/2016.
 */
public class RetrieveLoginTask extends AsyncTask<String, Void, String> {
    public static java.net.CookieManager msCookieManager = new java.net.CookieManager();
    static final String COOKIES_HEADER = "Set-Cookie";
    private boolean error;
    private Exception exception;
    private ProgressDialog progressDialog;
    private LoginActivity context;
    private String username;

    public RetrieveLoginTask(LoginActivity activity, String username) {
        this.context = activity;
        this.username=username;
        progressDialog = new ProgressDialog (activity, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        error = false;
    }
    @Override
    protected String doInBackground(String... urls) {
        try {
            HttpURLConnection urlConnection = null;
            URL url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();


            Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
            if(cookiesHeader != null)
            {
                for (String cookie : cookiesHeader)
                {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);
            int data = isw.read();
            Log.d("test debug", "data:" + data);

            String res="";
            while (data != -1) {
                char current = (char) data;
                res+=current;
                data = isw.read();
            }

            if (!res.equals("0")) {


                DBHelper myHelper = DBHelper.getInstance(context);
                SQLiteDatabase db = myHelper.getWritableDatabase();
                context.idUser = myHelper.createDB(db, res);

                context.connectWebSocket();


                SharedPreferences pref = context.getSharedPreferences("session", context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("idUser", context.idUser);
                editor.putString("username", username);

                editor.commit();
                error = false;

                Intent intent = new Intent(context, HuntListActivity.class);
                context.startActivity(intent);

            } else {
                error = true;

            }







            return res;



        } catch (Exception e) {
            this.exception = e;
            Log.d("test debug", "eccez:" + e.getMessage());
            e.printStackTrace();


            return null;
        }
    }
    @Override
    protected void onPreExecute() {
        Log.d("test debug", "onPreExecute");

        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("test debug", "onPostExecute");

        if (progressDialog.isShowing()) {
            Log.d("test debug", "if progressDialog");

            progressDialog.dismiss();
            context.loginButton.setEnabled(true);


            if (error){

                CharSequence text = context.getString(R.string.login_error);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        }
    }
}
