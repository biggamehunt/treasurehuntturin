package com.example.andrea22.gamehunt.utility;

import android.os.AsyncTask;
import android.util.Log;

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

    private Exception exception;

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
            return res;

        } catch (Exception e) {
            this.exception = e;
            Log.d("test debug", "eccez:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
