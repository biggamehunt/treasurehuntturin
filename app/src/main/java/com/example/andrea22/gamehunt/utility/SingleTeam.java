package com.example.andrea22.gamehunt.utility;

import java.util.List;

/**
 * Created by Simone on 01/07/2016.
 */
public class SingleTeam {
    String name, slogan;
    int numTeam;
    int numUsers;

    public SingleTeam(String name, String slogan, int numTeam, int numUsers) {
        this.name = name;
        this.slogan = slogan;
        this.numTeam = numTeam;
        this.numUsers = numUsers;

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
    public int getNumUsers(){
        return numUsers;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setSlogan(String slogan){
        this.slogan = slogan;
    }

    public void setNumUsers (int numUsers){
        this.numUsers = numUsers;
    }

}
