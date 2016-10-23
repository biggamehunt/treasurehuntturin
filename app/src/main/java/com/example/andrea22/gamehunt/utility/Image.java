package com.example.andrea22.gamehunt.utility;

/**
 * Created by Simone on 18/10/2016.
 */
public class Image {

    private String small;
    private String medium;
    private String large;
    private String name;
    private String timestamp;
    private int idUser;
    private int idStage;
    private int idHunt;

    public Image() {
        super();
    }

    public Image(String small, String medium, String large, String name, String timestamp, int idUser, int idStage, int idHunt) {
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.name = name;
        this.timestamp = timestamp;
        this.idUser = idUser;
        this.idStage = idStage;
        this.idHunt = idHunt;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdStage() {
        return idStage;
    }

    public void setIdStage(int idStage) {
        this.idStage = idStage;
    }

    public int getIdHunt() {
        return idHunt;
    }

    public void setIdHunt(int idHunt) {
        this.idHunt = idHunt;
    }

}