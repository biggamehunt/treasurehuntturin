package com.example.andrea22.gamehunt.Database;

public class AddTeamTable {
        public static final String TABLE_NAME = "ADDTEAM";
        public static final String COLUMN_IDADDTEAM = "idAddTeam";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SLOGAN = "slogan";
        public static final String COLUMN_NUMTEAM = "numTeam";
        public static final String COLUMN_IDHUNT = "idHunt";
        public static final String COLUMN_USERS = "users";
        public static final String COLUMN_IDUSER = "idUser";
        public static final String HUNTTABLE = "HUNT";
        public static final String USERTABLE = "USER";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_IDADDTEAM + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_SLOGAN + " TEXT, " +
                        COLUMN_USERS + " TEXT, " +
                        COLUMN_IDHUNT + " INTEGER NOT NULL, " +
                        COLUMN_IDUSER + " INTEGER NOT NULL, " +
                        COLUMN_NUMTEAM+ " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_IDUSER + ") REFERENCES " + USERTABLE + "(" + COLUMN_IDUSER + "), "+
                        "FOREIGN KEY(" + COLUMN_IDHUNT + ") REFERENCES " + HUNTTABLE + "(" + COLUMN_IDHUNT + "));";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }