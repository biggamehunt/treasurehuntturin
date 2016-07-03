package com.example.andrea22.gamehunt.Database;

public class TeamTable {
        public static final String TABLE_NAME = "TEAM";
        public static final String COLUMN_IDTEAM = "idTeam";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SLOGAN = "slogan";
        public static final String COLUMN_IDHUNT = "idHunt";
        public static final String HUNTTABLE = "HUNT";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_IDTEAM + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_SLOGAN + " TEXT NOT NULL, " +
                        COLUMN_IDHUNT + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_IDHUNT + ") REFERENCES " + HUNTTABLE + "(" + COLUMN_IDHUNT + "));";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }