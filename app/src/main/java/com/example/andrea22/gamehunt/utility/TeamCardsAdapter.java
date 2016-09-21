package com.example.andrea22.gamehunt.utility;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andrea22.gamehunt.R;
import com.example.andrea22.gamehunt.TeamManagementActivity;

import java.util.List;

/**
 * Created by Simone on 29/06/2016.
 */

public class TeamCardsAdapter extends RecyclerView.Adapter<TeamCardsAdapter.SingleCardViewHolder> {

    public static class SingleCardViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView teamName;
        TextView slogan;
        ImageButton deleteTeam;
        Button goToTeam;
        //LinearLayout teamLayout;

        //ArrayList<TextView> teamPlayer;

        SingleCardViewHolder(View itemView, SingleTeam singleTeam, Context context) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvteam);
            teamName = (TextView)itemView.findViewById(R.id.team_name);
            slogan = (TextView)itemView.findViewById(R.id.slogan);
            goToTeam = (Button)itemView.findViewById(R.id.goToTeam);
            deleteTeam = (ImageButton)itemView.findViewById(R.id.deleteTeam);

            //teamLayout = (LinearLayout) itemView.findViewById(R.id.team_layout);
            //addUser = (Button) itemView.findViewById(R.id.addUser);

            //TextView playerView;
            //teamPlayer = new ArrayList<TextView>();
            /*Log.v("",""+singleTeam.getCountPlayer());
            Log.v("playercount",""+singleTeam.getCountPlayer());
            for(int i=0; i<singleTeam.getCountPlayer(); i++){
                playerView = new TextView(context);
                teamLayout.addView(playerView);
                //teamPlayer.add(playerView);
            }*/

        }
    }
    int cont=0;
    List<SingleTeam> singleTeam;
    Context context;
    public int numTeam;
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

        SingleCardViewHolder pvh = new SingleCardViewHolder(v,singleTeam.get(cont),context);
        cont++;
        return pvh;
    }
    @Override
    public void onBindViewHolder(SingleCardViewHolder singleCardViewHolder, int i) {
        singleCardViewHolder.teamName.setText(singleTeam.get(i).name);

        /*for(int j=0;j<singleCardViewHolder.teamLayout.getChildCount();j++){

            ((TextView)singleCardViewHolder.teamLayout.getChildAt(j)).setText(singleTeam.get(i).player.get(j));
        }*/
        numTeam  = singleTeam.get(i).numTeam;
        singleCardViewHolder.goToTeam.setTag(numTeam);
        singleCardViewHolder.deleteTeam.setTag(numTeam);
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
        return singleTeam.size();
    }
}
