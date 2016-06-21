package com.example.andrea22.gamehunt.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Simone on 16/06/2016.
 */
public class DBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "GameHunt";
    private static final int DATABASE_VERSION = 1;

    public class UserTable {
        public static final String TABLE_NAME = "USER";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_IDUSER = "idUser";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_PHONE = "phone";

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_USERNAME + " TEXT NOT NULL, " +
                        COLUMN_IDUSER + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_EMAIL + " TEXT NOT NULL, " +
                        COLUMN_PHOTO + " TEXT, " +
                        COLUMN_PHONE + " TEXT);";

        private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
    }

    public class HuntTable {
        public static final String TABLE_NAME = "HUNT";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IDHUNT = "idHunt";
        public static final String COLUMN_MAX_TEAM = "maxTeam";
        public static final String COLUMN_TIME_START = "timeStart";
        public static final String COLUMN_TIME_END = "timeEnd";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ISFINISHED = "isFinished";
        public static final String COLUMN_IDUSER = "idUser";
        public static final String USERTABLE = "USER";


        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_IDHUNT + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_MAX_TEAM + " TEXT NOT NULL, " +
                        COLUMN_TIME_START + " DATETIME NOT NULL, " +
                        COLUMN_TIME_END + " DATETIME, " +
                        COLUMN_DESCRIPTION + " TEXT, " +
                        COLUMN_ISFINISHED + " BOOLEAN NOT NULL, " +
                        COLUMN_IDUSER + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_IDUSER + ") REFERENCES " + USERTABLE + "(" + COLUMN_IDUSER + "));";

        private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context.deleteDatabase(DATABASE_NAME);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserTable.SQL_CREATE_TABLE);
        Log.v("db log", "Create Table User eseguito");
        db.execSQL(HuntTable.SQL_CREATE_TABLE);
        Log.v("db log", "Create Table Hunt eseguito");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(HuntTable.SQL_DROP_TABLE);
        db.execSQL(UserTable.SQL_DROP_TABLE);
        onCreate(db);
    }




}
