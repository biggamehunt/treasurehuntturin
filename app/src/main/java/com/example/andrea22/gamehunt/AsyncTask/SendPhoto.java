package com.example.andrea22.gamehunt.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.andrea22.gamehunt.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Andrea22 on 15/06/2016.
 */
public class SendPhoto extends AsyncTask<ArrayList<Object>, Void, Integer> {

    Context context;
    public SendPhoto (Context context){
        this.context = context;
    }

    protected Integer doInBackground(ArrayList<Object>... params) {
        try {

            AWSCredentials credentials = new BasicAWSCredentials(context.getResources().getString(R.string.access_key),
                    context.getResources().getString(R.string.secret_key));
            File foto = (File)params[0].get(0);
            String namestage = (String)params[0].get(2);
            String path = (String)params[0].get(1);

            AmazonS3 s3client = new AmazonS3Client(credentials);
            PutObjectRequest req = new PutObjectRequest("treasurehuntturin", path, foto);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.addUserMetadata("namestage",namestage);
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
