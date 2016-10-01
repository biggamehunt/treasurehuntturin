package com.example.andrea22.gamehunt.utility;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrea22.gamehunt.R;

import java.util.List;

/**
 * Created by Andrea22 on 30/09/2016.
 */

public class StageCardsAdapter extends RecyclerView.Adapter<StageCardsAdapter.SingleCardViewHolder> {

    public static class SingleCardViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView stageName;
        ImageView isLocationRequired, isPhotoRequired, isCheckRequired;
        //ImageButton deleteStage;

        //ArrayList<TextView> teamPlayer;

        SingleCardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvstage);
            stageName = (TextView)itemView.findViewById(R.id.stage_name);
            isLocationRequired = (ImageView)itemView.findViewById(R.id.isLocationRequired);
            isPhotoRequired = (ImageView)itemView.findViewById(R.id.isPhotoRequired);
            isCheckRequired = (ImageView)itemView.findViewById(R.id.isCheckRequired);


        }
    }
    int cont=0;
    List<SingleStage> stages;
    Context context;
    public int numStage;
    public StageCardsAdapter(List<SingleStage> stages, Context context){

        this.context = context;
        this.stages = stages;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public SingleCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_stage_v21, viewGroup, false);

        SingleCardViewHolder pvh = new SingleCardViewHolder(v);
        cont++;
        return pvh;
    }
    @Override
    public void onBindViewHolder(SingleCardViewHolder singleCardViewHolder, int i) {
        singleCardViewHolder.stageName.setText("Stage " + stages.get(i).getNumStage());


        //todo: sistemare le immagini nella card

        if (stages.get(i).getIsLocationRequired()==1) {
            singleCardViewHolder.isLocationRequired.setImageResource(R.drawable.ic_add_location_black_24dp);
        } else {
            singleCardViewHolder.isLocationRequired.setImageResource(R.drawable.ic_add_white_24dp);

        }

        if (stages.get(i).getIsPhotoRequired()==1) {
            singleCardViewHolder.isPhotoRequired.setImageResource(R.drawable.ic_query_builder_black_24dp);
        } else {
            singleCardViewHolder.isPhotoRequired.setImageResource(R.drawable.ic_add_white_24dp);

        }

        if (stages.get(i).getIsCheckRequired()==1) {
            singleCardViewHolder.isCheckRequired.setImageResource(R.drawable.ic_create_black_24dp);
        } else {
            singleCardViewHolder.isCheckRequired.setImageResource(R.drawable.ic_add_white_24dp);

        }




        /*for(int j=0;j<singleCardViewHolder.teamLayout.getChildCount();j++){

            ((TextView)singleCardViewHolder.teamLayout.getChildAt(j)).setText(singleTeam.get(i).player.get(j));
        }*/
        numStage  = stages.get(i).numStage;
       //toDO: settare le immagini


/*
        singleCardViewHolder.addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TeamManagementActivity) context).addUser(view);
            }
        });
*/
        //for(int j=0;j<singleCardViewHolder.teamPlayer.size();j++){
        //    singleCardViewHolder.teamPlayer.get(j).setText(singleTeam.get(i).player.get(j));
        //}

    }

    @Override
    public int getItemCount() {
        return stages.size();
    }
}
