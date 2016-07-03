package com.example.andrea22.gamehunt.Database;

public class BeTable {
        public static final String TABLE_NAME = "BE";
        public static final String COLUMN_IDBE = "idBe";
        public static final String COLUMN_IDUSER = "idUser";
        public static final String COLUMN_IDTEAM = "idTeam";
        public static final String USERTABLE = "USER";
        public static final String TEAMTABLE = "TEAM";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_IDBE + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_IDUSER + " INTEGER NOT NULL, " +
                        COLUMN_IDTEAM + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_IDUSER + ") REFERENCES " + USERTABLE + "(" + COLUMN_IDUSER + "), "+

                        "FOREIGN KEY(" + COLUMN_IDTEAM + ") REFERENCES " + TEAMTABLE + "(" + COLUMN_IDTEAM + "));";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }