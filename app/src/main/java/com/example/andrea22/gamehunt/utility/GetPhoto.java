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
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.andrea22.gamehunt.GalleryActivity;
import com.example.andrea22.gamehunt.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrea22 on 15/06/2016.
 */
public class GetPhoto extends AsyncTask<ArrayList<InfoHuntForCheck>, Void, Integer> {

    private Exception exception;
    private GalleryActivity context;
    private ArrayList<Image> images;

    public GetPhoto (Context context){
        this.context = (GalleryActivity)context;
    }

    @Override
    protected Integer doInBackground(ArrayList<InfoHuntForCheck>... params) {
        try {

            AWSCredentials credentials = new BasicAWSCredentials(context.getResources().getString(R.string.access_key),
                    context.getResources().getString(R.string.secret_key));

            AmazonS3 s3client = new AmazonS3Client(credentials);

            String prefix;
            int currentHunt = 0;
            images = new ArrayList<>();

            java.util.Date expiration = new java.util.Date();
            long msec = expiration.getTime();
            msec += 1000*60; // 1 minute.
            expiration.setTime(msec);

            for (int i = 0; i < params[0].size(); i++){

                if (i == 0 || currentHunt != params[0].get(i).idHunt){
                    currentHunt = params[0].get(i).idHunt;
                    prefix = params[0].get(i).idHunt+"/";
                    ObjectListing listing = s3client.listObjects(new ListObjectsRequest().withBucketName("treasurehuntturin").withPrefix(prefix));

                    for (S3ObjectSummary objectSummary : listing.getObjectSummaries()) {

                        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                                new GeneratePresignedUrlRequest("treasurehuntturin", objectSummary.getKey());




                        generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
                        generatePresignedUrlRequest.setExpiration(expiration);

                        URL s = s3client.generatePresignedUrl(generatePresignedUrlRequest);
                        ObjectMetadata objectMetadata = s3client.getObjectMetadata("treasurehuntturin", objectSummary.getKey());


                        Image image = new Image();
                        image.setName(params[0].get(i).nameHunt + " - " + objectMetadata.getUserMetaDataOf("namestage"));

                        image.setSmall(s.toString());
                        image.setMedium(s.toString());
                        image.setLarge(s.toString());
                        image.setTimestamp(params[0].get(i).timeStart);

                        context.mAdapter.images.add(image);



                    }
                }




            }


            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {

        // TODO: check this.exception
        // TODO: do something with the feed
        if (result == 1) {
            context.mAdapter.notifyDataSetChanged();
        }


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
