package com.example.andrea22.gamehunt.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.andrea22.gamehunt.R;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrea22 on 15/06/2016.
 */
public class GetPhoto extends AsyncTask<ArrayList<Object>, Void, Integer> {

    private Exception exception;
    private Context context;

    public GetPhoto (Context context){
        this.context = context;
    }

    protected Integer doInBackground(ArrayList<Object>... params) {
        try {

            AWSCredentials credentials = new BasicAWSCredentials(context.getResources().getString(R.string.access_key),
                    context.getResources().getString(R.string.secret_key));

            AmazonS3 s3client = new AmazonS3Client(credentials);

            String prefix = "178/106";
            final ListObjectsRequest listObjectRequest = new ListObjectsRequest().
                    withBucketName("treasurehuntturin").
                    withPrefix(prefix);
            final ObjectListing objectListing = s3client.listObjects(listObjectRequest);

            for (final S3ObjectSummary objectSummary: objectListing.getObjectSummaries()) {
                final String key = objectSummary.getKey();
                if (isImmediateDescendant(prefix, key)) {
                    final String relativePath = getRelativePath(prefix, key);
                    Log.v("amazon getphoto", "relativePath:" + relativePath);

                }

            }



            java.util.Date expiration = new java.util.Date();
            long msec = expiration.getTime();
            msec += 1000*60; // 1 hour.
            expiration.setTime(msec);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest("treasurehuntturin", "163/84/1");
            generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
            generatePresignedUrlRequest.setExpiration(expiration);

            URL s = s3client.generatePresignedUrl(generatePresignedUrlRequest);

            Log.v("amazon getphoto", "url:" + s.toString());


            generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest("treasurehuntturin", "163/84");
            generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
            generatePresignedUrlRequest.setExpiration(expiration);

            s = s3client.generatePresignedUrl(generatePresignedUrlRequest);

            Log.v("amazon getphoto", "url:" + s.toString());




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

    public String getRelativePath(final String parent, final String child) {
        if (!child.startsWith(parent)) {
            throw new IllegalArgumentException("Invalid child '" + child
                    + "' for parent '" + parent + "'");
        }
        // a String.replace() also would be fine here
        final int parentLen = parent.length();
        return child.substring(parentLen);
    }

    public boolean isImmediateDescendant(final String parent, final String child) {
        if (!child.startsWith(parent)) {
            // maybe we just should return false
            throw new IllegalArgumentException("Invalid child '" + child
                    + "' for parent '" + parent + "'");
        }
        final int parentLen = parent.length();
        final String childWithoutParent = child.substring(parentLen);
        if (childWithoutParent.contains("/")) {
            return false;
        }
        return true;
    }

}
