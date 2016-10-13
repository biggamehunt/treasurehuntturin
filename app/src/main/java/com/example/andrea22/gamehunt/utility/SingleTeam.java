package com.example.andrea22.gamehunt.utility;

import java.util.List;

/**
 * Created by Simone on 01/07/2016.
 */
public class SingleTeam {
    String name, slogan;
    List<String> player;
    int numTeam;


    public SingleTeam(String name, String slogan, List<String> player, int numTeam) {
        this.name = name;
        this.slogan = slogan;
        this.player = player;
        this.numTeam = numTeam;

    }

    public int getCountPlayer(){
        return player.size();
    }

    public int getNumTeam(){
        return numTeam;
    }
    public void setNumTeam(int numTeam){
        this.numTeam=numTeam;
    }

    public String getName(){
        return name;
    }
    public String getSlogan(){
        return slogan;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setSlogan(String slogan){
        this.slogan = slogan;
    }

    public List<String> getPlayer () { return player; }
}
