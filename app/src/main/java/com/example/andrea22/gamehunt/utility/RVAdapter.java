package com.example.andrea22.gamehunt.utility;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.andrea22.gamehunt.HuntListActivity;
import com.example.andrea22.gamehunt.R;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by Simone on 29/06/2016.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SingleHuntViewHolder>  {


    public static class SingleHuntViewHolder extends RecyclerView.ViewHolder   {


        CardView cv;

        TextView huntTitle;
        TextView huntDate;
        ImageView huntImage;
        TextView description;
        TextView goToHunt;
        TextView modifyHunt;

        SingleHuntViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            huntTitle = (TextView)itemView.findViewById(R.id.single_title);
            huntDate = (TextView)itemView.findViewById(R.id.single_date);
            huntImage = (ImageView)itemView.findViewById(R.id.single_image);
            description = (TextView)itemView.findViewById(R.id.single_description);
            goToHunt = (TextView)itemView.findViewById(R.id.single_goToHunt);
            modifyHunt = (TextView)itemView.findViewById(R.id.modifyHunt);

        }

    }

    public List<SingleHunt> singlehunts;
    int dimStart = -1;
    int targetHeight;

    Context context;
    public RVAdapter(List<SingleHunt> singlehunts, Context context){
        this.singlehunts = singlehunts;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public SingleHuntViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hunt_v21, viewGroup, false);
        SingleHuntViewHolder pvh = new SingleHuntViewHolder(v);
        return pvh;
    }

    int pos;
    @Override
    public void onBindViewHolder(final SingleHuntViewHolder singleHuntViewHolder, int i) {

        singleHuntViewHolder.huntTitle.setText(singlehunts.get(i).title);
        singleHuntViewHolder.huntDate.setText(singlehunts.get(i).date);
        singleHuntViewHolder.huntImage.setImageResource(singlehunts.get(i).imageId);

        singleHuntViewHolder.description.setText(singlehunts.get(i).description);
        singleHuntViewHolder.modifyHunt.setText(this.context.getResources().getString(R.string.modifyHunt));

        pos=i;

        singleHuntViewHolder.cv.measure(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        targetHeight = singleHuntViewHolder.cv.getMeasuredHeightAndState();
        Log.v("dim finale", "" + targetHeight);

        singleHuntViewHolder.description.setVisibility(View.GONE);
        singleHuntViewHolder.goToHunt.setVisibility(View.GONE);
        singleHuntViewHolder.modifyHunt.setVisibility(View.GONE);
        final String idHunt = ""+singlehunts.get(i).idHunt;
        singleHuntViewHolder.modifyHunt.setTag(singlehunts.get(i).idHunt);

        if (dimStart != -1) {
            singleHuntViewHolder.cv.getLayoutParams().height = dimStart;
        }
        singleHuntViewHolder.cv.setTag(singlehunts.get(i).isMine);


        singleHuntViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ((TeamManagementActivity) context).addUser(view);
                Log.v("RVAdapter", "nell'onCLick");

                toggleProductDescriptionHeight(view);

            }
        });

        if (singlehunts.get(i).isStagesEmpty == 0 && singlehunts.get(i).isTeamsEmpty == 0){
            singleHuntViewHolder.goToHunt.setText(this.context.getResources().getString(R.string.goToHunt));
            singleHuntViewHolder.goToHunt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ((TeamManagementActivity) context).addUser(view);
                    Log.v("RVAdapter", "idHunt:"+idHunt);
                    Log.v("db log", "id: " + view.getId());

                    ((HuntListActivity)context).goToHunt(idHunt);
                }
            });
        } else if (singlehunts.get(i).isStagesEmpty == 1 && singlehunts.get(i).isTeamsEmpty == 1){
            singleHuntViewHolder.goToHunt.setText(this.context.getResources().getString(R.string.continueHunt));
            singleHuntViewHolder.goToHunt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ((TeamManagementActivity) context).addUser(view);
                    Log.v("RVAdapter", "idHunt:"+view.getTag());
                    Log.v("db log", "id: " + view.getId());

                    ((HuntListActivity)context).goToStagesCreation(idHunt);
                }
            });
        } else if (singlehunts.get(i).isStagesEmpty == 0 && singlehunts.get(i).isTeamsEmpty == 1){
            singleHuntViewHolder.goToHunt.setText(this.context.getResources().getString(R.string.continueHunt));
            singleHuntViewHolder.goToHunt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ((TeamManagementActivity) context).addUser(view);
                    Log.v("RVAdapter", "idHunt:"+view.getTag());
                    Log.v("db log", "id: " + view.getId());

                    ((HuntListActivity)context).goToTeamsCreation(idHunt);
                }
            });
        }


        singleHuntViewHolder.modifyHunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ((TeamManagementActivity) context).addUser(view);
                Log.v("RVAdapter", "idHunt:"+view.getTag());
                Log.v("db log", "id: " + view.getId());

                ((HuntListActivity)context).modHunt(view.getTag().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return singlehunts.size();
    }

    private View cardPressed;

    public void toggleProductDescriptionHeight(final View view) {

        boolean showmodify = (boolean) view.getTag();

        if (dimStart==-1){
            dimStart = view.getHeight();
        }

        cardPressed=view;

        Log.v("RVAdapter", "height:" + view.getHeight());

        //int descriptionViewMinHeight = descriptionViewFullHeight;

        if (view.getHeight() == dimStart) {
            //EXPAND

            ((TextView)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(3)).setVisibility(View.VISIBLE);
            ((TextView)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(4)).setVisibility(View.VISIBLE);


            if (showmodify == true){
                ((TextView)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(5)).setVisibility(View.VISIBLE);
            }
            Log.v("RVAdapter", "expand h after add child:" + view.getHeight());

            final int targetHeight = view.getMeasuredHeight();

            Animation anim = new Animation(){
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {

                    view.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                    view.requestLayout();

                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // 1dp/ms
            anim.setDuration((int)(targetHeight / view.getContext().getResources().getDisplayMetrics().density));
            view.startAnimation(anim);

        } else {
            // collapse

            Log.v("RVAdapter", "collapse");

            ((TextView)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(3)).setVisibility(View.GONE);
            ((TextView)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(4)).setVisibility(View.GONE);
            ((TextView)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(5)).setVisibility(View.GONE);

            ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeightAndState(),
                    dimStart);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = cardPressed.getLayoutParams();
                    layoutParams.height = val;
                    cardPressed.setLayoutParams(layoutParams);
                }
            });
            anim.start();
        }
    }
}
