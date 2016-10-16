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
        int isStagesEmpty;
        int isTeamsEmpty;
        boolean isMine; //creatore della caccia

        public SingleHunt(int idHunt, String title, String date, int isStagesEmpty, int isTeamsEmpty, int imageId,String description, boolean isMine) {
                this.idHunt=idHunt;
                this.title = title;
                this.date = date;
                this.imageId = imageId;
                this.description = description;
                this.isMine = isMine;
                this.isStagesEmpty = isStagesEmpty;
                this.isTeamsEmpty = isTeamsEmpty;
        }
}
