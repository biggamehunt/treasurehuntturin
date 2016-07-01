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

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SingleHuntViewHolder> {

    public static class SingleHuntViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView huntTitle;
        TextView huntDate;
        ImageView huntImage;

        SingleHuntViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            huntTitle = (TextView)itemView.findViewById(R.id.single_title);
            huntDate = (TextView)itemView.findViewById(R.id.single_date);
            huntImage = (ImageView)itemView.findViewById(R.id.single_image);
        }
    }

    List<SingleHunt> singlehunts;

    public RVAdapter(List<SingleHunt> singlehunts){

        this.singlehunts = singlehunts;
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

    @Override
    public void onBindViewHolder(SingleHuntViewHolder singleHuntViewHolder, int i) {
        singleHuntViewHolder.huntTitle.setText(singlehunts.get(i).title);
        singleHuntViewHolder.huntDate.setText(singlehunts.get(i).date);
        singleHuntViewHolder.huntImage.setImageResource(singlehunts.get(i).imageId);
    }

    @Override
    public int getItemCount() {
        return singlehunts.size();
    }
}
