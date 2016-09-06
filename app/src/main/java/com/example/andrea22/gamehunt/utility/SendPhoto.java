package com.example.andrea22.gamehunt.utility;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrea22 on 15/06/2016.
 */
public class SendPhoto extends AsyncTask<ArrayList<Object>, Void, Integer> {

    private Exception exception;

    protected Integer doInBackground(ArrayList<Object>... params) {
        try {

            AWSCredentials credentials = new BasicAWSCredentials("AKIAJKDTCRLVCHJEQU2A", "9Rhil3q3kL66XZZNUqtBadjT40DDftqW3ZT7JIGs");

            AmazonS3 s3client = new AmazonS3Client(credentials);


            s3client.putObject(new PutObjectRequest("treasurehuntturin", (String)params[0].get(1), (File)params[0].get(0)));
            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }

}
