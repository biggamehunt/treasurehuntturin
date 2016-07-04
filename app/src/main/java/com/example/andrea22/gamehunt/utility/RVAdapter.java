package com.example.andrea22.gamehunt.utility;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.andrea22.gamehunt.R;
import java.util.List;

/**
 * Created by Simone on 29/06/2016.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SingleHuntViewHolder> {

    public static class SingleHuntViewHolder extends RecyclerView.ViewHolder {

        CardView cv;

        TextView huntTitle;
        TextView huntDate;
        ImageView huntImage;
        TextView description;
        Button goToHunt;

        SingleHuntViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            huntTitle = (TextView)itemView.findViewById(R.id.single_title);
            huntDate = (TextView)itemView.findViewById(R.id.single_date);
            huntImage = (ImageView)itemView.findViewById(R.id.single_image);
            description = (TextView)itemView.findViewById(R.id.single_description);
            goToHunt = (Button)itemView.findViewById(R.id.single_goToHunt);



        }


    }

    List<SingleHunt> singlehunts;
    int dimStart = 406;

    TextView description;
    Button goToHunt;


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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        SingleHuntViewHolder pvh = new SingleHuntViewHolder(v);
        Log.v("RVAdapter", "onCreateViewHolder, i:"+i);



        return pvh;
    }

    @Override
    public void onBindViewHolder(SingleHuntViewHolder singleHuntViewHolder, int i) {

        Log.v("RVAdapter", "onBindViewHolder, i:" + i);

        singleHuntViewHolder.huntTitle.setText(singlehunts.get(i).title);
        singleHuntViewHolder.huntDate.setText(singlehunts.get(i).date);
        singleHuntViewHolder.huntImage.setImageResource(singlehunts.get(i).imageId);


        singleHuntViewHolder.description.setText(singlehunts.get(i).description);
        singleHuntViewHolder.goToHunt.setText("VAI ALLA CACCIA");

        singleHuntViewHolder.description.setVisibility(View.GONE);
        singleHuntViewHolder.goToHunt.setVisibility(View.GONE);
        singleHuntViewHolder.cv.getLayoutParams().height=dimStart;
        //dimStart = singleHuntViewHolder.cv.getHeight();//singleHuntViewHolder.cv.getHeight();
       // Log.v("RVAdapter","dimStart:"+dimStart);


        singleHuntViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ((TeamManagment) context).addUser(view);
                Log.v("RVAdapter","nell'onCLick");

                toggleProductDescriptionHeight(view);

            }
        });

    }

    @Override
    public int getItemCount() {
        return singlehunts.size();
    }


    private View a;

    private void toggleProductDescriptionHeight(View view) {

        a=view;

        Log.v("RVAdapter", "height:" + view.getHeight());


        //int descriptionViewMinHeight = descriptionViewFullHeight;
        if (view.getHeight() == dimStart) {
            // expand
            Log.v("RVAdapter", "expand:" + dimStart);

            ((TextView)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(3)).setVisibility(View.VISIBLE);
            ((Button)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(4)).setVisibility(View.VISIBLE);


            //((CardView)((RelativeLayout)goToHunt.getParent()).getParent()).getHeight();

           // Log.v("RVAdapter", "height expand:" + ((CardView)((RelativeLayout)goToHunt.getParent()).getParent()).getHeight());



            ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeightAndState(),
                    600);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = a.getLayoutParams();
                    layoutParams.height = val;
                    a.setLayoutParams(layoutParams);
                }
            });
            Log.v("RVAdapter", "prima di anim.start");

            anim.start();
        } else {
            // collapse
            Log.v("RVAdapter", "collapse");

            ((TextView)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(3)).setVisibility(View.GONE);
            ((Button)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(4)).setVisibility(View.GONE);

            ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeightAndState(),
                    dimStart);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = a.getLayoutParams();
                    layoutParams.height = val;
                    a.setLayoutParams(layoutParams);
                }
            });
            anim.start();
        }
    }

}
