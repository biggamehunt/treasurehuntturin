package com.example.andrea22.gamehunt.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Simone on 16/06/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    static DBHelper dbhelper = null;

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

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context.deleteDatabase(DATABASE_NAME);

    }

    public static  DBHelper getInstance(Context context){
        if (dbhelper == null){
            dbhelper = new DBHelper(context);
        }
        return dbhelper;
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


    public void createDB(SQLiteDatabase db, String res) throws JSONException {
        JSONObject user = new JSONObject(res);

        ContentValues values = new ContentValues();
        values.put(DBHelper.UserTable.COLUMN_IDUSER, user.getString("idUser"));
        values.put(DBHelper.UserTable.COLUMN_USERNAME, user.getString("username"));
        values.put(DBHelper.UserTable.COLUMN_EMAIL, user.getString("email"));
        values.put(DBHelper.UserTable.COLUMN_PHOTO, user.isNull("photo") == false ? user.getString("photo") : "");
        values.put(DBHelper.UserTable.COLUMN_PHONE, user.isNull("phone" ) == false ? user.getString("phone") : "");

        long newRowId;
        newRowId = db.insert(DBHelper.UserTable.TABLE_NAME,null,values);
        Log.v("db log", "Insert User eseguito");
        if (user.isNull("hunts") == false) {
            JSONArray hunts_create = user.getJSONArray("hunts");
            JSONObject hunt = null;
            if (hunts_create != null || hunts_create.length()==0) {
                for (int i = 0; i < hunts_create.length(); i++) {

                    hunt = hunts_create.getJSONObject(i);
                    values = new ContentValues();
                    values.put(DBHelper.HuntTable.COLUMN_NAME, hunt.getString("name"));
                    values.put(DBHelper.HuntTable.COLUMN_IDHUNT, hunt.getString("idHunt"));
                    values.put(DBHelper.HuntTable.COLUMN_MAX_TEAM, hunt.getString("maxTeam"));
                    values.put(DBHelper.HuntTable.COLUMN_TIME_START, hunt.getString("timeStart"));
                    values.put(DBHelper.HuntTable.COLUMN_TIME_END, hunt.getString("timeEnd"));
                    values.put(DBHelper.HuntTable.COLUMN_DESCRIPTION, hunt.isNull("description") == false ? hunt.getString("description") : "");
                    values.put(DBHelper.HuntTable.COLUMN_ISFINISHED, hunt.getString("isFinished"));
                    values.put(DBHelper.HuntTable.COLUMN_IDUSER, user.getString("idUser"));

                    newRowId = db.insert(DBHelper.HuntTable.TABLE_NAME, null, values);
                    Log.v("db log", "Insert Hunt eseguito");

                }
            }
        }
    }




}
