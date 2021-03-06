package com.example.andrea22.gamehunt.Database;

public class HuntTable {
        public static final String TABLE_NAME = "HUNT";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IDHUNT = "idHunt";
        public static final String COLUMN_MAXTEAM = "maxTeam";
        public static final String COLUMN_TIMESTART = "timeStart";
        public static final String COLUMN_TIMEEND = "timeEnd";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ISFINISHED = "isFinished";
        public static final String COLUMN_ISLOADED = "isLoaded";
        public static final String COLUMN_ISSTAGESEMPTY = "isStagesEmpty";
        public static final String COLUMN_ISTEAMSEMPTY = "isTeamsEmpty";
        public static final String COLUMN_ISSTARTED = "isStarted";
        public static final String COLUMN_ISENDED = "isEnded";
        public static final String COLUMN_IDWINNER = "idWinner";
        public static final String COLUMN_NAMEWINNER = "nameWinner";
        public static final String COLUMN_PHOTOTOCHECK = "photoToCheck";


        public static final String COLUMN_IDUSER = "idUser";
        public static final String USERTABLE = "USER";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_IDHUNT + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_MAXTEAM + " TEXT NOT NULL, " +
                        COLUMN_TIMESTART + " DATETIME NOT NULL, " +
                        COLUMN_TIMEEND + " DATETIME, " +
                        COLUMN_DESCRIPTION + " TEXT, " +
                        COLUMN_ISFINISHED + " INTEGER NOT NULL, " +
                        COLUMN_ISLOADED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_ISSTARTED + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_ISENDED + " INTEGER NOT NULL DEFAULT 0, " +

                        COLUMN_IDWINNER + " INTEGER, " + //non è referenziata come chiave esterna perché il vincitore può essere un team avversario, che quindi non è salvato nel db dell'app
                        COLUMN_NAMEWINNER + " TEXT, " +
                        COLUMN_PHOTOTOCHECK + " INTEGER NOT NULL DEFAULT 0, " +

                        COLUMN_ISSTAGESEMPTY + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_ISTEAMSEMPTY + " INTEGER NOT NULL DEFAULT 0, " +
                        COLUMN_IDUSER + " INTEGER NOT NULL);"/* +
                        "FOREIGN KEY(" + COLUMN_IDUSER + ") REFERENCES " + USERTABLE + "(" + COLUMN_IDUSER + "));"*/;

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }