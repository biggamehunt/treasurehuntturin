package com.example.andrea22.gamehunt.Database;

public class StageTable {
        public static final String TABLE_NAME = "STAGE";
        public static final String COLUMN_IDSTAGE = "idStage";
        public static final String COLUMN_RAY = "ray";
        public static final String COLUMN_NUMSTAGE = "numStage";
        public static final String COLUMN_AREA_LAT = "areaLat";
        public static final String COLUMN_AREA_LON = "areaLon";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lon";
        public static final String COLUMN_CLUE = "clue";
        public static final String COLUMN_ISLOCATIONREQUIRED = "isLocationRequired";
        public static final String COLUMN_ISCHECKREQUIRED = "isCheckRequired";
        public static final String COLUMN_ISPHOTOREQUIRED = "isPhotoRequired";
        public static final String COLUMN_NUMUSERTOFINISH = "numUserToFinish";
        public static final String COLUMN_USERCOMPLETED = "userCompleted";
        public static final String COLUMN_TEAMCOMPLETED = "teamCompleted";
        public static final String COLUMN_ISPHOTOSENDED = "isPhotoSended";
        public static final String COLUMN_ISPHOTOCHECKED = "isPhotoChecked";
        public static final String COLUMN_IDHUNT = "idHunt";
        public static final String HUNTTABLE = "HUNT";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_IDSTAGE + " INTEGER PRIMARY KEY NOT NULL, " +

                        COLUMN_AREA_LAT + " DOUBLE NOT NULL, " +
                        COLUMN_AREA_LON + " DOUBLE NOT NULL, " +
                        COLUMN_RAY + " INTEGER NOT NULL, " +
                        COLUMN_LAT + " DOUBLE NOT NULL, " +
                        COLUMN_LON + " DOUBLE NOT NULL, " +
                        COLUMN_NUMSTAGE + " INTEGER NOT NULL, " +
                        COLUMN_CLUE + " TEXT, " +
                        COLUMN_ISLOCATIONREQUIRED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_ISPHOTOREQUIRED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_ISCHECKREQUIRED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_NUMUSERTOFINISH + " INTEGER NOT NULL, " +

                        COLUMN_USERCOMPLETED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_TEAMCOMPLETED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_ISPHOTOSENDED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_ISPHOTOCHECKED + " INTEGER NOT NULL DEFAULT 0, " +


                        COLUMN_IDHUNT + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_IDHUNT + ") REFERENCES " + HUNTTABLE + "(" + COLUMN_IDHUNT + "));";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }