package com.example.andrea22.gamehunt.utility;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrea22.gamehunt.R;

import java.util.List;

/**
 * Created by Simone on 29/06/2016.
 */

public class TeamCardsAdapter extends RecyclerView.Adapter<TeamCardsAdapter.SingleCardViewHolder> {

    public static class SingleCardViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView teamName;
        TextView teamPlayer;

        SingleCardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvteam);
            teamName = (TextView)itemView.findViewById(R.id.team_name);
            teamPlayer = (TextView)itemView.findViewById(R.id.team_player);
        }
    }

    List<SingleTeam> singleTeam;

    public TeamCardsAdapter(List<SingleTeam> singleTeam){

        this.singleTeam = singleTeam;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public SingleCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_team_v21, viewGroup, false);
        SingleCardViewHolder pvh = new SingleCardViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(SingleCardViewHolder singleCardViewHolder, int i) {
        singleCardViewHolder.teamName.setText(singleTeam.get(i).name);
        singleCardViewHolder.teamPlayer.setText(singleTeam.get(i).player);
    }

    @Override
    public int getItemCount() {
        return singleTeam.size();
    }
}
