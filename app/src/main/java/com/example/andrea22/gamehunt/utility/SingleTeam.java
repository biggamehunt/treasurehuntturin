package com.example.andrea22.gamehunt.utility;

import java.util.List;

/**
 * Created by Simone on 01/07/2016.
 */
public class SingleTeam {
    String name;
    List<String> player;
    int numTeam;

    public SingleTeam(String name, List<String> player, int numTeam) {
        this.name = name;
        this.player = player;
        this.numTeam = numTeam;

    }

    public int getCountPlayer(){
        return player.size();
    }

    public List<String> getPlayer () { return player; }
}
