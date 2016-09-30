package com.example.andrea22.gamehunt.utility;

import java.util.List;

/**
 * Created by Simone on 30/09/2016.
 */
public class SingleStage {
    String name;
    int numStage;
    boolean isLocReq, isCheckReq, isPhotoReq;

    public SingleStage(int numStage, boolean isLocReq, boolean isCheckReq, boolean isPhotoReq) {
        this.numStage = numStage;
        this.isLocReq = isLocReq;
        this.isCheckReq = isCheckReq;
        this.isPhotoReq = isPhotoReq;
    }

    public int getNumStage(){
        return numStage;
    }

    public void setNumTeam(int numStage){
        this.numStage = numStage;
    }

    public String getName(){
        return name;
    }

}
