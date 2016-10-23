package com.example.andrea22.gamehunt.Entity;

import java.util.ArrayList;

/**
 * Created by Andrea22 on 21/10/2016.
 */
public class InfoHuntForCheck {

    String nameHunt;
    int idHunt;
    String timeStart;

    public InfoHuntForCheck(int idHunt, String nameHunt, String timeStart) {
        this.nameHunt = nameHunt;
        this.idHunt = idHunt;
        this.timeStart = timeStart;

    }

    public int getIdHunt(){
        return idHunt;
    }

    public String getNameHunt(){
        return nameHunt;
    }

    public String getTimeStart(){
        return timeStart;
    }
}
