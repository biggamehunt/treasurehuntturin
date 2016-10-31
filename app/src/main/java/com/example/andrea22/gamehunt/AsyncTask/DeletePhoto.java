package com.example.andrea22.gamehunt.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.andrea22.gamehunt.Entity.Image;
import com.example.andrea22.gamehunt.Entity.InfoHuntForCheck;
import com.example.andrea22.gamehunt.GalleryActivity;
import com.example.andrea22.gamehunt.R;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrea22 on 15/06/2016.
 */
public class DeletePhoto extends AsyncTask<String, Void, Integer> {

    private Context context;
    public DeletePhoto(Context context){
        this.context = context;

    }

    @Override
    protected Integer doInBackground(String... params) {
        try {

            AWSCredentials credentials = new BasicAWSCredentials(context.getResources().getString(R.string.access_key),
                    context.getResources().getString(R.string.secret_key));

            AmazonS3 s3client = new AmazonS3Client(credentials);

            Log.v("DeletePhoto","key:"+params[0]);
            s3client.deleteObject(new DeleteObjectRequest("treasurehuntturin", params[0]));


            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onPreExecute() {
        Log.d("test debug", "onPreExecute");

    }

    @Override
    protected void onPostExecute(Integer result) {

        // TODO: check this.exception
        // TODO: do something with the feed

    }



}
