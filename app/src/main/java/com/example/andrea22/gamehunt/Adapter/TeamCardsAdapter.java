package com.example.andrea22.gamehunt.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrea22.gamehunt.R;
import com.example.andrea22.gamehunt.Entity.SingleTeam;

import java.util.List;

/**
 * Created by Simone on 29/06/2016.
 */

public class TeamCardsAdapter extends RecyclerView.Adapter<TeamCardsAdapter.SingleCardViewHolder> {

    public static class SingleCardViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView teamName;
        TextView numUser;
        ImageView deleteTeam;
        Button goToTeam;
        TextView slogan;

        //LinearLayout teamLayout;

        //ArrayList<TextView> teamPlayer;

        SingleCardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvteam);
            teamName = (TextView)itemView.findViewById(R.id.team_name);
            slogan = (TextView)itemView.findViewById(R.id.slogan);
            goToTeam = (Button)itemView.findViewById(R.id.goToTeam);
            deleteTeam = (ImageView)itemView.findViewById(R.id.deleteTeam);
            numUser = (TextView) itemView.findViewById(R.id.numUser);

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

        SingleCardViewHolder pvh = new SingleCardViewHolder(v);
        cont++;
        return pvh;
    }
    @Override
    public void onBindViewHolder(SingleCardViewHolder singleCardViewHolder, final int i) {
        singleCardViewHolder.teamName.setText(singleTeam.get(i).getName());
        singleCardViewHolder.slogan.setText(singleTeam.get(i).getSlogan());
        singleCardViewHolder.numUser.setText(""+singleTeam.get(i).getNumUsers());


        /*for(int j=0;j<singleCardViewHolder.teamLayout.getChildCount();j++){

            ((TextView)singleCardViewHolder.teamLayout.getChildAt(j)).setText(singleTeam.get(i).player.get(j));
        }*/
        numTeam  = singleTeam.get(i).getNumTeam();
        singleCardViewHolder.goToTeam.setTag(numTeam);
        singleCardViewHolder.deleteTeam.setTag(numTeam);
        singleCardViewHolder.teamName.setTag(i);
        singleCardViewHolder.slogan.setTag(i);


        //for(int j=0;j<singleCardViewHolder.teamPlayer.size();j++){
        //    singleCardViewHolder.teamPlayer.get(j).setText(singleTeam.get(i).player.get(j));
        //}

    }

    @Override
    public int getItemCount() {
        return singleTeam.size();
    }
}
