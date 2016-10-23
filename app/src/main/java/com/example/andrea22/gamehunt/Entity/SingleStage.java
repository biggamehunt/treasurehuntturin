package com.example.andrea22.gamehunt.Entity;

import java.util.List;

/**
 * Created by Simone on 30/09/2016.
 */
public class SingleStage {
    int numStage, isLoactionRequired, isCheckRequired, isPhotoRequired;
    String name;
    public SingleStage(String name, int numStage, int isLoactionRequired, int isCheckRequired, int isPhotoRequired) {

        this.name = name;
        this.numStage = numStage;
        this.isLoactionRequired = isLoactionRequired;
        this.isCheckRequired = isCheckRequired;
        this.isPhotoRequired = isPhotoRequired;
    }
    public String getName(){
        return name;
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
