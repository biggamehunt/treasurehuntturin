package com.example.andrea22.gamehunt.Entity;

/**
 * Created by Simone on 29/06/2016.
 */
public class SingleHunt {

        int idHunt;
        String title;
        String date;
        int imageId;
        int cropId;
        String txtCount;
        String description;
        int isStagesEmpty;

        int isTeamsEmpty;
        boolean isMine; //creatore della caccia
        int photoToCheck;

        public SingleHunt(int idHunt, String title, String date, int cropId, String txtCount, int isStagesEmpty, int isTeamsEmpty, int imageId,String description, boolean isMine, int photoToCheck) {
                this.idHunt=idHunt;
                this.title = title;
                this.date = date;
                this.imageId = imageId;
                this.description = description;
                this.cropId = cropId;
                this.txtCount = txtCount;
                this.isMine = isMine;
                this.isStagesEmpty = isStagesEmpty;
                this.isTeamsEmpty = isTeamsEmpty;
                this.photoToCheck = photoToCheck;
        }

        public int getIdHunt() {
                return idHunt;
        }

        public void setIdHunt(int idHunt) {
                this.idHunt = idHunt;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public int getImageId() {
                return imageId;
        }

        public void setImageId(int imageId) {
                this.imageId = imageId;
        }

        public int getIsStagesEmpty() {
                return isStagesEmpty;
        }

        public void setIsStagesEmpty(int isStagesEmpty) {
                this.isStagesEmpty = isStagesEmpty;
        }

        public boolean isMine() {
                return isMine;
        }

        public void setIsMine(boolean isMine) {
                this.isMine = isMine;
        }

        public int getIsTeamsEmpty() {
                return isTeamsEmpty;
        }

        public void setIsTeamsEmpty(int isTeamsEmpty) {
                this.isTeamsEmpty = isTeamsEmpty;
        }

        public String getDescription() {
                return description;
        }

        public void setTxtCount(String description) {
                this.txtCount = txtCount;
        }

        public String getTxtCount() {
                return txtCount;
        }

        public void setCropId(int cropId){
                this.cropId = cropId;
        }

        public int getCropId(){
                return cropId;
        }

        public void setDescription(String description) {
                this.description = description;
        }


        public String getDate() {
                return date;
        }

        public void setDate(String date) {
                this.date = date;
        }


        public int getPhotoToCheck() {
                return photoToCheck;
        }

        public void setPhotoToCheck(int photoToCheck) {
                this.photoToCheck = photoToCheck;
        }
}
