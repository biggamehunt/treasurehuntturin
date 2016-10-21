package com.example.andrea22.gamehunt.utility;

import android.content.Context;
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
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.andrea22.gamehunt.R;

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

    Context context;
    public SendPhoto (Context context){
        this.context = context;
    }

    protected Integer doInBackground(ArrayList<Object>... params) {
        try {

            AWSCredentials credentials = new BasicAWSCredentials(context.getResources().getString(R.string.access_key),
                    context.getResources().getString(R.string.secret_key));

            AmazonS3 s3client = new AmazonS3Client(credentials);

            PutObjectRequest req = new PutObjectRequest("treasurehuntturin", (String)params[0].get(1), (File)params[0].get(0));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.addUserMetadata("namestage",(String)params[0].get(2));
            req.setMetadata(metadata);
            s3client.putObject(req);
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
