package com.example.andrea22.gamehunt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.andrea22.gamehunt.Entity.Image;
import com.example.andrea22.gamehunt.AsyncTask.RetrieveJson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Simone on 18/10/2016.
 */

public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    private int idUser;

    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);

        images = (ArrayList<Image>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());

        Image image = images.get(position);
        lblTitle.setText(image.getName());
        lblDate.setText(image.getTimestamp());
        idUser = image.getIdUser();



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            ImageView checkNo = (ImageView) view.findViewById(R.id.checkNo);
            final ImageView checkOk = (ImageView) view.findViewById(R.id.checkOk);
            final Image image = images.get(position);


            checkNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("SlideShowDialogFragment","checkNo");

                    String u = "http://jbossews-treasurehunto.rhcloud.com/StageOperation";

                    String p = "action=checkPhoto&idUser="+image.getIdUser()+"&idStage="+image.getIdStage()+"&check=0";
                    String url[]= new String [2];
                    url[0] = u;
                    url[1] = p;

                    try {
                        String res = new RetrieveJson().execute(url).get();

                        if (res.trim().equals("-1")) {
                            Intent intent = new Intent(v.getContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                        } else if (!res.trim().equals("0")) {



                            Log.v("Slidehow", "!res.trim().equals(0)");

                            JSONObject jsonRes = new JSONObject(res);

/*
                            DBHelper myHelper = DBHelper.getInstance(v.getContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();

                            myHelper.setAfterPhotoSended(db, res, idStage, idTeam, idUser,idHunt,nameHunt);

*/
                            //FrameLayout frame = (FrameLayout) findViewById(R.id.rect_map);
                            //CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.coordinator_maps);
                            Log.d("Hunt Activity", "teamIsCompleted:"+jsonRes.getString("teamIsCompleted"));
                            Log.d("Hunt Activity", "userIsCompleted:"+jsonRes.getString("userIsCompleted"));
                            if (jsonRes.getInt("huntDone")==1) {
                                Log.d("Hunt Activity", "Hunt is Done!");
                                JSONArray jUsers = jsonRes.getJSONArray("users");
                                String users="";

                                for (int i=0; i<jUsers.length();i++){
                                    users += jUsers.getInt(i)+"&";

                                }
                                LoginActivity.mWebSocketClient.send("ha:" + users+"-"+image.getIdStage()+"-"+jsonRes.getInt("team")+"-"+image.getIdHunt()+"-"+image.getName());
                            } else if (jsonRes.getString("teamIsCompleted").equals("1")) {
                                Log.d("Hunt Activity", "teamIsCompleted");

                                JSONArray jUsers = jsonRes.getJSONArray("users");
                                String users="";

                                for (int i=0; i<jUsers.length();i++){
                                    users += jUsers.getInt(i)+"&";

                                }


                                LoginActivity.mWebSocketClient.send("ca:" + users+"-"+image.getIdStage()+"-"+jsonRes.getInt("nextStage")+"-"+jsonRes.getInt("team")+"-"+image.getIdHunt()+"-"+image.getName());


                            } else if (jsonRes.getString("userIsCompleted").equals("1")) {
                                Log.d("Hunt Activity", "User is completed");
                                LoginActivity.mWebSocketClient.send("cd:" + image.getIdUser()+"-"+image.getIdStage()+"-"+jsonRes.getInt("team")+"-"+image.getIdHunt()+"-"+image.getName());

                            } else if (jsonRes.getString("userIsCompleted").equals("0")) {
                                Log.d("Hunt Activity", "User is not completed");
                                Log.d("Hunt Activity", "cf:" + image.getIdUser()+"-"+image.getIdStage()+"-"+image.getIdHunt()+"-"+image.getName());

                                LoginActivity.mWebSocketClient.send("cf:" + image.getIdUser()+"-"+image.getIdStage()+"-"+image.getIdHunt()+"-"+image.getName());
                            }





                        } else {
                            //toDo mettere il text di tutti i toast nelle variabili
                            Toast toast = Toast.makeText(v.getContext(), "Si è verificato un errore.", Toast.LENGTH_SHORT);
                            toast.show();
                        }






                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });

            checkOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("SlideShowDialogFragment","checkOk");

                    String u = "http://jbossews-treasurehunto.rhcloud.com/StageOperation";

                    String p = "action=checkPhoto&idUser="+image.getIdUser()+"&idStage="+image.getIdStage()+"&check=1";
                    String url[]= new String [2];
                    url[0] = u;
                    url[1] = p;

                    try {
                        String res = new RetrieveJson().execute(url).get();

                        if (res.trim().equals("-1")) {
                            Intent intent = new Intent(v.getContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                        } else if (!res.trim().equals("0")) {



                            Log.v("Hunt Activity", "!res.trim().equals(0)");

                            JSONObject jsonRes = new JSONObject(res);

/*
                            DBHelper myHelper = DBHelper.getInstance(v.getContext());
                            SQLiteDatabase db = myHelper.getWritableDatabase();

                            myHelper.setAfterPhotoSended(db, res, idStage, idTeam, idUser,idHunt,nameHunt);

*/
                            //FrameLayout frame = (FrameLayout) findViewById(R.id.rect_map);
                            //CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.coordinator_maps);
                            Log.d("Hunt Activity", "teamIsCompleted:"+jsonRes.getString("teamIsCompleted"));
                            Log.d("Hunt Activity", "userIsCompleted:"+jsonRes.getString("userIsCompleted"));
                            if (jsonRes.getInt("huntDone")==1) {
                                Log.d("Hunt Activity", "Hunt is Done!");
                                JSONArray jUsers = jsonRes.getJSONArray("users");
                                String users="";

                                for (int i=0; i<jUsers.length();i++){
                                    users += jUsers.getInt(i)+"&";

                                }
                                LoginActivity.mWebSocketClient.send("ha:" + users+"-"+image.getIdStage()+"-"+jsonRes.getInt("team")+"-"+image.getIdHunt()+"-"+image.getName());
                            } else if (jsonRes.getString("teamIsCompleted").equals("1")) {
                                Log.d("Hunt Activity", "teamIsCompleted");

                                JSONArray jUsers = jsonRes.getJSONArray("users");
                                String users="";

                                for (int i=0; i<jUsers.length();i++){
                                        users += jUsers.getInt(i)+"&";

                                }


                                LoginActivity.mWebSocketClient.send("ca:" + users+"-"+image.getIdStage()+"-"+jsonRes.getInt("nextStage")+"-"+jsonRes.getInt("team")+"-"+image.getIdHunt()+"-"+image.getName());


                            } else if (jsonRes.getString("userIsCompleted").equals("1")) {
                                Log.d("Hunt Activity", "User is completed");
                                LoginActivity.mWebSocketClient.send("cd:" + image.getIdUser()+"-"+image.getIdStage()+"-"+jsonRes.getInt("team")+"-"+image.getIdHunt()+"-"+image.getName());

                            } else if (jsonRes.getString("userIsCompleted").equals("0")) {
                                Log.d("Hunt Activity", "User is not completed");
                                Log.d("Hunt Activity", "cf:" + image.getIdUser()+"-"+image.getIdStage()+"-"+image.getIdHunt()+"-"+image.getName());

                                LoginActivity.mWebSocketClient.send("cf:" + image.getIdUser()+"-"+image.getIdStage()+"-"+image.getIdHunt()+"-"+image.getName());
                            }





                        } else {
                            //toDo mettere il text di tutti i toast nelle variabili
                            Toast toast = Toast.makeText(v.getContext(), "Si è verificato un errore.", Toast.LENGTH_SHORT);
                            toast.show();
                        }






                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });




            Glide.with(getActivity()).load(image.getLarge())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }





    }
}