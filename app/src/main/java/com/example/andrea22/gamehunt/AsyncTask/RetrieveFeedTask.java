package com.example.andrea22.gamehunt.AsyncTask;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andrea22 on 15/06/2016.
 */
public class RetrieveFeedTask extends AsyncTask<String, Void, String> {

    private Exception exception;

    protected String doInBackground(String... urls) {
        try {
            HttpURLConnection urlConnection = null;
            URL url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();


            if(RetrieveLoginTask.msCookieManager.getCookieStore().getCookies().size() > 0)
            {
                //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                urlConnection.setRequestProperty("Cookie",
                        TextUtils.join(";", RetrieveLoginTask.msCookieManager.getCookieStore().getCookies()));
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
