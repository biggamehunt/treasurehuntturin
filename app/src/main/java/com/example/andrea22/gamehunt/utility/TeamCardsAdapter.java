package com.example.andrea22.gamehunt.utility;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andrea22.gamehunt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simone on 29/06/2016.
 */

public class TeamCardsAdapter extends RecyclerView.Adapter<TeamCardsAdapter.SingleCardViewHolder> {

    public static class SingleCardViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView teamName;
        LinearLayout teamLayout;
        //ArrayList<TextView> teamPlayer;

        SingleCardViewHolder(View itemView, SingleTeam singleTeam, Context context) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvteam);
            teamName = (TextView)itemView.findViewById(R.id.team_name);
            teamLayout = (LinearLayout) itemView.findViewById(R.id.team_layout);
            TextView playerView;
            //teamPlayer = new ArrayList<TextView>();
            Log.v("",""+singleTeam.getCountPlayer());
            for(int i=0; i<singleTeam.getCountPlayer(); i++){
                playerView = new TextView(context);
                teamLayout.addView(playerView);
                //teamPlayer.add(playerView);


            }

        }
    }

    List<SingleTeam> singleTeam;
    Context context;

    public TeamCardsAdapter(List<SingleTeam> singleTeam, Context context){

        this.context = context;
        this.singleTeam = singleTeam;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public SingleCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_team_v21, viewGroup, false);
        SingleCardViewHolder pvh = new SingleCardViewHolder(v,singleTeam.get(i),context);
        return pvh;
    }

    @Override
    public void onBindViewHolder(SingleCardViewHolder singleCardViewHolder, int i) {
        singleCardViewHolder.teamName.setText(singleTeam.get(i).name);
        Log.v("logogogo",""+singleCardViewHolder.teamLayout.getChildCount());
        for(int j=0;j<singleCardViewHolder.teamLayout.getChildCount();j++){

            ((TextView)singleCardViewHolder.teamLayout.getChildAt(j)).setText(singleTeam.get(i).player.get(j));
        }
        //for(int j=0;j<singleCardViewHolder.teamPlayer.size();j++){
        //    singleCardViewHolder.teamPlayer.get(j).setText(singleTeam.get(i).player.get(j));
        //}

    }

    @Override
    public int getItemCount() {
        return singleTeam.size();
    }
}
