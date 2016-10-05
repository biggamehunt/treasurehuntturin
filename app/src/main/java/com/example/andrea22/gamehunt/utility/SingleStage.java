package com.example.andrea22.gamehunt.utility;

import java.util.List;

/**
 * Created by Simone on 30/09/2016.
 */
public class SingleStage {
    int numStage, isLoactionRequired, isCheckRequired, isPhotoRequired;

    public SingleStage(int numStage, int isLoactionRequired, int isCheckRequired, int isPhotoRequired) {

        this.numStage = numStage;
        this.isLoactionRequired = isLoactionRequired;
        this.isCheckRequired = isCheckRequired;
        this.isPhotoRequired = isPhotoRequired;
    }

    public int getNumStage(){
        return numStage;
    }

    public void setNumStage(int numStage){
        this.numStage = numStage;
    }

    public int getIsLocationRequired(){
        return isLoactionRequired;
    }

    public int getIsCheckRequired(){
        return isCheckRequired;
    }

    public int getIsPhotoRequired(){
        return isPhotoRequired;
    }


}
