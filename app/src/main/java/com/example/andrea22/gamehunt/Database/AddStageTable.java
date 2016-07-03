package com.example.andrea22.gamehunt.Database;

public class AddStageTable {
        public static final String TABLE_NAME = "ADDSTAGE";
        public static final String COLUMN_IDADDSTAGE = "idStage";
        public static final String COLUMN_RAY = "ray";
        public static final String COLUMN_AREA_LAT = "areaLat";
        public static final String COLUMN_AREA_LON = "areaLon";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lon";
        public static final String COLUMN_CLUE = "clue";
        public static final String COLUMN_ISLOCATIONREQUIRED = "isLocationRequired";
        public static final String COLUMN_ISCHECKREQUIRED = "isCheckRequired";
        public static final String COLUMN_ISPHOTOREQUIRED = "isPhotoRequired";
        public static final String COLUMN_NUMUSERTOFINISH = "numUserToFinish";
        public static final String COLUMN_IDUSER = "idUser";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_IDADDSTAGE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_AREA_LAT + " DOUBLE NOT NULL, " +
                        COLUMN_AREA_LON + " DOUBLE NOT NULL, " +
                        COLUMN_RAY + " INTEGER NOT NULL, " +
                        COLUMN_LAT + " DOUBLE NOT NULL, " +
                        COLUMN_LON + " DOUBLE NOT NULL, " +
                        COLUMN_CLUE + " TEXT, " +
                        COLUMN_ISLOCATIONREQUIRED + " BOOLEAN NOT NULL, " +
                        COLUMN_ISPHOTOREQUIRED + " BOOLEAN NOT NULL, " +
                        COLUMN_ISCHECKREQUIRED + " BOOLEAN NOT NULL, " +
                        COLUMN_IDUSER + " INTEGER NOT NULL, " +
                        COLUMN_NUMUSERTOFINISH + " INTEGER NOT NULL );";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }