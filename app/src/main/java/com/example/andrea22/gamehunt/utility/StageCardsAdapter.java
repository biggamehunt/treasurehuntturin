package com.example.andrea22.gamehunt.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Andrea22 on 30/09/2016.
 */

public class StageCardsAdapter extends RecyclerView.Adapter<StageCardsAdapter.SingleCardViewHolder> implements ItemTouchHelperAdapter {
    public int beginningPosition;

    public int fromPosition;
    public int toPosition;
    public boolean firstMove = true;

    public class SingleCardViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

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

        @Override
        public void onItemSelected() {
            //cv.setBackgroundColor(Color.LTGRAY);
            itemView.setBackgroundColor(Color.parseColor("#ff9800"));
            //itemView.setBackgroundColor(Color.LTGRAY);
            tmpStages = new ArrayList<SingleStage>();

            beginningPosition = -1;

            for (int i = 0; i < stages.size(); i++){
                tmpStages.add(new SingleStage(stages.get(i).getName(), stages.get(i).getNumStage(),stages.get(i).getIsLocationRequired(),stages.get(i).getIsCheckRequired(),stages.get(i).getIsPhotoRequired()));
            }

            firstMove = true;

            //tmpStages = stages;

        }

        @Override
        public void onItemClear() {

            itemView.setBackgroundColor(0);

            Log.d("notifyData", "toPosition "+": "+toPosition);

            if (beginningPosition == -1) {
                return;
            } else if (beginningPosition < toPosition){


                Log.d("notifyData", "beginningPosition < toPosition " );
                int[][] posChange = new int[(toPosition+1)-beginningPosition][2];

                for (int i = beginningPosition, j=0; i<toPosition+1; i++, j++){
                    posChange[j][0]=stages.get(i).getNumStage();
                    posChange[j][1]=tmpStages.get(i).getNumStage();
                    Log.v("notifyData", "cambiare da "+ posChange[j][0]+" a "+ posChange[j][1]);
                    stages.get(i).setNumStage(posChange[j][1]);

                }

                DBHelper mDbHelper = DBHelper.getInstance(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                SharedPreferences pref = context.getSharedPreferences("session", context.MODE_PRIVATE);

                Log.v("notifyData", "idLastHunt: "+ pref.getInt("idLastHunt", 0));
                Log.v("notifyData", "idUser: "+ pref.getInt("idUser", 0));


                mDbHelper.updateNumStages(db, posChange, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0));

            } else if (beginningPosition > toPosition){
                Log.d("notifyData", "beginningPosition > toPosition " );
                int[][] posChange = new int[(beginningPosition+1)-toPosition][2];



                for (int i = toPosition, j = 0; i<beginningPosition+1; i++, j++){
                    posChange[j][0]=stages.get(i).getNumStage();
                    posChange[j][1]=tmpStages.get(i).getNumStage();
                    Log.v("notifyData", "cambiare da " + posChange[j][0] + " a " + posChange[j][1]);
                    stages.get(i).setNumStage(posChange[j][1]);

                }

                DBHelper mDbHelper = DBHelper.getInstance(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                SharedPreferences pref = context.getSharedPreferences("session", context.MODE_PRIVATE);

                Log.v("notifyData", "idLastHunt: "+ pref.getInt("idLastHunt", 0));
                Log.v("notifyData", "idUser: "+ pref.getInt("idUser", 0));

                mDbHelper.updateNumStages(db,posChange, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0));


            }

            //itemView.setBackgroundColor(0);
        }
    }
    int cont=0;
    List<SingleStage> stages;
    List<SingleStage> tmpStages;

    Context context;
    private final OnStartDragListener mDragStartListener;

    public StageCardsAdapter(List<SingleStage> stages, Context context, OnStartDragListener dragStartListener){
        mDragStartListener = dragStartListener;
        this.context = context;
        this.stages = stages;

    }
    public void notifyData(List<SingleStage> myList) {
        Log.d("notifyData ", myList.size() + "");
        stages = myList;
        //notifyDataSetChanged();
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
    public void onBindViewHolder(final SingleCardViewHolder singleCardViewHolder, int i) {
        singleCardViewHolder.stageName.setText(stages.get(i).getName());

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




        singleCardViewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               // Log.v("StageCardsAdapter", "onLongClickListener");
                mDragStartListener.onStartDrag(singleCardViewHolder);
                return false;
            }


        });



        /*singleCardViewHolder.cv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("RVAdapter", "nell'onTouch");

                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(singleCardViewHolder);
                }
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
                    mDragStartListener.onStartDrag(singleCardViewHolder);
                }
                return false;
            }

        });*/
        /*for(int j=0;j<singleCardViewHolder.teamLayout.getChildCount();j++){

            ((TextView)singleCardViewHolder.teamLayout.getChildAt(j)).setText(singleTeam.get(i).player.get(j));
        }*/
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

    @Override
    public void onItemDismiss(int position) {

        DBHelper mDbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SharedPreferences pref = context.getSharedPreferences("session", context.MODE_PRIVATE);

        int numStage = stages.get(position).getNumStage();
        mDbHelper.removeAddStage(db, numStage, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0));

        if (stages.size()-position-1 != 0){
            int[][] posChange = new int[stages.size()-position-1][2];

            for (int i = 0, j=position+1; i<posChange.length; i++, j++){
                posChange[i][0]=stages.get(j).getNumStage();
                posChange[i][1]=posChange[i][0]-1;
                Log.v("notifyData", "cambiare da "+ posChange[i][0]+" a "+ posChange[i][1]);
                stages.get(j).setNumStage(posChange[i][1]);

            }

            mDbHelper.updateNumStages(db, posChange, pref.getInt("idUser", 0), pref.getInt("idLastHunt", 0));

        }

        stages.remove(position);
        notifyItemRemoved(position);



    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {


        if (firstMove) {
            beginningPosition = fromPosition;
            firstMove = false;

            Log.v("RVAdapter", "beginningPosition" + beginningPosition);

        }

        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        Log.v("RVAdapter", "fromPosition" + fromPosition);
        Log.v("RVAdapter", "toPosition" + toPosition);




        /*

        try {
            DBHelper mDbHelper = DBHelper.getInstance(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();




        } catch (Exception e){
            e.printStackTrace();
        }*/



        Collections.swap(stages, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);


        return true;
    }

}
