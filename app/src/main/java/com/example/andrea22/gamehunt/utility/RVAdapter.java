package com.example.andrea22.gamehunt.utility;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
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

import com.example.andrea22.gamehunt.HuntActivity;
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
    int dimStart = -1;
    int targetHeight;


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
        return pvh;
    }

    private CardView extendedCardView;
    int pos;
    @Override
    public void onBindViewHolder(SingleHuntViewHolder singleHuntViewHolder, int i) {

        singleHuntViewHolder.huntTitle.setText(singlehunts.get(i).title);
        singleHuntViewHolder.huntDate.setText(singlehunts.get(i).date);
        singleHuntViewHolder.huntImage.setImageResource(singlehunts.get(i).imageId);


        singleHuntViewHolder.description.setText(singlehunts.get(i).description);
        //todo: il testo va in string.xml

        singleHuntViewHolder.goToHunt.setText("VAI ALLA CACCIA");


        extendedCardView=singleHuntViewHolder.cv;
        pos=i;

        singleHuntViewHolder.cv.measure(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        targetHeight = singleHuntViewHolder.cv.getMeasuredHeightAndState();
        Log.v("dim finale",""+targetHeight);




        singleHuntViewHolder.description.setVisibility(View.GONE);
        singleHuntViewHolder.goToHunt.setVisibility(View.GONE);

        singleHuntViewHolder.goToHunt.setTag(singlehunts.get(i).idHunt);

        if (dimStart != -1) {
            singleHuntViewHolder.cv.getLayoutParams().height = dimStart;
        }


        singleHuntViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ((TeamManagment) context).addUser(view);
                Log.v("RVAdapter", "nell'onCLick");

                toggleProductDescriptionHeight(view);

            }
        });

        singleHuntViewHolder.goToHunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ((TeamManagment) context).addUser(view);
                Log.v("RVAdapter", "idHunt:"+view.getTag());
                Log.v("db log", "id: " + view.getId());
                Intent intent = new Intent(context, HuntActivity.class);
                intent.putExtra("idHunt",view.getTag().toString());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return singlehunts.size();
    }


    private View cardPressed;

    private void toggleProductDescriptionHeight(View view) {

        if (dimStart==-1){
            dimStart = view.getHeight();
        }

        cardPressed=view;

        Log.v("RVAdapter", "height:" + view.getHeight());


        //int descriptionViewMinHeight = descriptionViewFullHeight;
        if (view.getHeight() == dimStart) {
            // expand
            Log.v("RVAdapter", "expand:" + dimStart);


            ((TextView)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(3)).setVisibility(View.VISIBLE);
            ((Button)((RelativeLayout)((CardView)view).getChildAt(0)).getChildAt(4)).setVisibility(View.VISIBLE);
            Log.v("RVAdapter", "expand h after add child:" + view.getHeight());


            //((CardView)((RelativeLayout)goToHunt.getParent()).getParent()).getHeight();

           // Log.v("RVAdapter", "height expand:" + ((CardView)((RelativeLayout)goToHunt.getParent()).getParent()).getHeight());



            ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeightAndState(),
                    targetHeight);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = cardPressed.getLayoutParams();
                    layoutParams.height = val;
                    cardPressed.setLayoutParams(layoutParams);
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
                    ViewGroup.LayoutParams layoutParams = cardPressed.getLayoutParams();
                    layoutParams.height = val;
                    cardPressed.setLayoutParams(layoutParams);
                }
            });
            anim.start();
        }
    }

}
