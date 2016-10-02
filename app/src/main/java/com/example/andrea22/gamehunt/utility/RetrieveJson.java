package com.example.andrea22.gamehunt.utility;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by Andrea22 on 15/06/2016.
 */
public class RetrieveJson extends AsyncTask<String, Void, String> {

    private Exception exception;

    protected String doInBackground(String... urls) {
        try {
            HttpURLConnection urlConnection = null;
            URL url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");

            //urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty( "charset", "utf-8");
            urlConnection.setUseCaches( false );




            if(RetrieveLoginTask.msCookieManager.getCookieStore().getCookies().size() > 0)
            {
                //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                urlConnection.setRequestProperty("Cookie",
                        TextUtils.join(";", RetrieveLoginTask.msCookieManager.getCookieStore().getCookies()));
            }
            String urlParameters  = urls[1];
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );

            int    postDataLength = postData.length;
            urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            try( DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream())) {
                Log.d("test debug", "postData:" + postData);
                Log.d("test debug", "postDataLength:" + postDataLength);
                Log.d("test debug", "urls[1]:" + urls[1]);
                wr.write( postData );
                wr.flush();
                wr.close();
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
            e.printStackTrace();
            Log.d("test debug", "eccez:" + e.getMessage());

            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
