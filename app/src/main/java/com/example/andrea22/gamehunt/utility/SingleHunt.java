package com.example.andrea22.gamehunt.utility;

/**
 * Created by Simone on 29/06/2016.
 */
public class SingleHunt {
        int idHunt;
        String title;
        String date;
        int imageId;
        String description;
        boolean isStarted;
        int idUser; //creatore della caccia

        public SingleHunt(int idHunt, String title, String date, int imageId, String description/*, boolean isStarted, int idUser*/) {
                this.idHunt=idHunt;
                this.title = title;
                this.date = date;
                this.imageId = imageId;
                this.description = description;
                this.isStarted = isStarted;
                this.idUser = idUser;
        }
}
