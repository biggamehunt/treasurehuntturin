package com.example.andrea22.gamehunt.utility;

import java.util.List;

/**
 * Created by Simone on 01/07/2016.
 */
public class SingleTeam {
    String name;
    List<String> player;

    public SingleTeam(String name, List<String> player) {
        this.name = name;
        this.player = player;

    }

    public int getCountPlayer(){
        return player.size();
    }
}
