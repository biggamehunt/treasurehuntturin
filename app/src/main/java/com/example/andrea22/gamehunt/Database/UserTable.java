package com.example.andrea22.gamehunt.Database;

public class UserTable {
        public static final String TABLE_NAME = "USER";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_IDUSER = "idUser";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_PHONE = "phone";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_USERNAME + " TEXT NOT NULL, " +
                        COLUMN_IDUSER + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_EMAIL + " TEXT NOT NULL, " +
                        COLUMN_PHOTO + " TEXT, " +
                        COLUMN_PHONE + " TEXT);";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
    }