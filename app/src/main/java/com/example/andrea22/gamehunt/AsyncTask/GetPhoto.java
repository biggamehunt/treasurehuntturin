package com.example.andrea22.gamehunt.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.andrea22.gamehunt.GalleryActivity;
import com.example.andrea22.gamehunt.R;
import com.example.andrea22.gamehunt.Entity.Image;
import com.example.andrea22.gamehunt.Entity.InfoHuntForCheck;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrea22 on 15/06/2016.
 */
public class GetPhoto extends AsyncTask<ArrayList<InfoHuntForCheck>, Void, Integer> {

    private GalleryActivity context;
    private ArrayList<Image> images;
    private ProgressDialog progressDialog;

    public GetPhoto (Context context){
        this.context = (GalleryActivity)context;
        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        //todo: mettere in string
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
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

                if (i == 0 || currentHunt != params[0].get(i).getIdHunt()){
                    currentHunt = params[0].get(i).getIdHunt();
                    prefix = params[0].get(i).getIdHunt()+"/";
                    ObjectListing listing = s3client.listObjects(new ListObjectsRequest().withBucketName("treasurehuntturin").withPrefix(prefix));

                    for (S3ObjectSummary objectSummary : listing.getObjectSummaries()) {

                        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                                new GeneratePresignedUrlRequest("treasurehuntturin", objectSummary.getKey());

                        generatePresignedUrlRequest.setExpiration(expiration);

                        URL s = s3client.generatePresignedUrl(generatePresignedUrlRequest);
                        ObjectMetadata objectMetadata = s3client.getObjectMetadata("treasurehuntturin", objectSummary.getKey());


                        Image image = new Image();
                        image.setName(params[0].get(i).getNameHunt() + " - " + objectMetadata.getUserMetaDataOf("namestage"));

                        image.setSmall(s.toString());
                        image.setMedium(s.toString());
                        image.setLarge(s.toString());
                        image.setTimestamp(params[0].get(i).getTimeStart());

                        String[] split = objectSummary.getKey().split("/");

                        image.setIdUser(Integer.parseInt(split[split.length-1]));
                        image.setIdStage(Integer.parseInt(split[split.length - 2]));
                        image.setIdHunt(currentHunt);
                        Log.v("GetPhoto","currentHunt"+currentHunt);

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
    protected void onPreExecute() {
        Log.d("test debug", "onPreExecute");

        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Integer result) {

        // TODO: check this.exception
        // TODO: do something with the feed

        if (progressDialog.isShowing()) {
            Log.d("test debug", "if progressDialog");

            progressDialog.dismiss();


            if (result == 0){

                CharSequence text = "errore di caricamento!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            } else {
                context.mAdapter.notifyDataSetChanged();

            }
    }


    }

}
