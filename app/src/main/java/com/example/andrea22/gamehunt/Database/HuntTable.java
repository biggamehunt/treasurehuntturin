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
                        COLUMN_ISFINISHED + " BOOLEAN NOT NULL, " +
                        COLUMN_IDUSER + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_IDUSER + ") REFERENCES " + USERTABLE + "(" + COLUMN_IDUSER + "));";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }