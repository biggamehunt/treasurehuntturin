package com.example.andrea22.gamehunt.Database;

public class AddStageTable {
        public static final String TABLE_NAME = "ADDSTAGE";
        public static final String COLUMN_IDADDSTAGE = "idAddStage";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_RAY = "ray";
        public static final String COLUMN_AREA_LAT = "areaLat";
        public static final String COLUMN_AREA_LON = "areaLon";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lon";

        public static final String COLUMN_CLUE = "clue";
        public static final String COLUMN_NUMSTAGE = "numStage";

        public static final String COLUMN_ISLOCATIONREQUIRED = "isLocationRequired";
        public static final String COLUMN_ISCHECKREQUIRED = "isCheckRequired";
        public static final String COLUMN_ISPHOTOREQUIRED = "isPhotoRequired";
        public static final String COLUMN_NUMUSERTOFINISH = "numUserToFinish";
        public static final String COLUMN_IDUSER = "idUser";
        public static final String COLUMN_IDHUNT = "idHunt";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_IDADDSTAGE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NUMSTAGE + " INTEGER NOT NULL, " +
                        COLUMN_AREA_LAT + " DOUBLE NOT NULL, " +
                        COLUMN_AREA_LON + " DOUBLE NOT NULL, " +
                        COLUMN_RAY + " INTEGER NOT NULL, " +
                        COLUMN_LAT + " DOUBLE NOT NULL, " +
                        COLUMN_LON + " DOUBLE NOT NULL, " +
                        COLUMN_CLUE + " TEXT, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_ISLOCATIONREQUIRED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_ISPHOTOREQUIRED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_ISCHECKREQUIRED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_IDUSER + " INTEGER NOT NULL, " +
                        COLUMN_IDHUNT + " INTEGER NOT NULL, " +
                        COLUMN_NUMUSERTOFINISH + " INTEGER NOT NULL );";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }