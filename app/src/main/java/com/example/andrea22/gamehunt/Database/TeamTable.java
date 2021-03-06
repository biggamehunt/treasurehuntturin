package com.example.andrea22.gamehunt.Database;

public class TeamTable {
        public static final String TABLE_NAME = "TEAM";
        public static final String COLUMN_IDTEAM = "idTeam";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SLOGAN = "slogan";
        public static final String COLUMN_IDHUNT = "idHunt";
        public static final String COLUMN_IDCURRENTSTAGE = "idCurrentStage";
        public static final String ISCOMPLETED = "isCompleted";
        public static final String HUNTTABLE = "HUNT";
        public static final String STAGETABLE = "STAGE";
        public static final String COLUMN_IDSTAGE = "idStage";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_IDTEAM + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_SLOGAN + " TEXT NOT NULL, " +
                        COLUMN_IDCURRENTSTAGE + " INTEGER, " +
                        COLUMN_IDHUNT + " INTEGER NOT NULL, " +
                        ISCOMPLETED + " INTEGER NOT NULL DEFAULT 0, " +

                        "FOREIGN KEY(" + COLUMN_IDCURRENTSTAGE + ") REFERENCES " + STAGETABLE + "(" + COLUMN_IDSTAGE + "), " +
                        "FOREIGN KEY(" + COLUMN_IDHUNT + ") REFERENCES " + HUNTTABLE + "(" + COLUMN_IDHUNT + "));";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }