package com.example.andrea22.gamehunt.utility;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
        public static final String COLUMN_MAXTEAM = "maxTeam";
        public static final String COLUMN_TIMESTART = "timeStart";
        public static final String COLUMN_TIMEEND = "timeEnd";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ISFINISHED = "isFinished";
        public static final String COLUMN_IDUSER = "idUser";
        public static final String USERTABLE = "USER";


        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_IDHUNT + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_MAXTEAM + " TEXT NOT NULL, " +
                        COLUMN_TIMESTART + " DATETIME NOT NULL, " +
                        COLUMN_TIMEEND + " DATETIME, " +
                        COLUMN_DESCRIPTION + " TEXT, " +
                        COLUMN_ISFINISHED + " BOOLEAN NOT NULL, " +
                        COLUMN_IDUSER + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_IDUSER + ") REFERENCES " + USERTABLE + "(" + COLUMN_IDUSER + "));";

        private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


    public class AddStageTable {
        public static final String TABLE_NAME = "ADDSTAGE";
        public static final String COLUMN_IDSTAGE = "idStage";
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

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_IDSTAGE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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

        private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }




    public class StageTable {
        public static final String TABLE_NAME = "STAGE";
        public static final String COLUMN_IDSTAGE = "idStage";
        public static final String COLUMN_RAY = "ray";
        public static final String COLUMN_NUMSTAGE = "numStage";
        public static final String COLUMN_AREA_LAT = "areaLat";
        public static final String COLUMN_AREA_LON = "areaLon";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lon";
        public static final String COLUMN_CLUE = "clue";
        public static final String COLUMN_ISLOCATIONREQUIRED = "isLocationRequired";
        public static final String COLUMN_ISCHECKREQUIRED = "isCheckRequired";
        public static final String COLUMN_ISPHOTOREQUIRED = "isPhotoRequired";
        public static final String COLUMN_NUMUSERTOFINISH = "numUserToFinish";
        public static final String COLUMN_IDHUNT = "idHunt";
        public static final String HUNTTABLE = "HUNT";

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_IDSTAGE + " INTEGER PRIMARY KEY NOT NULL, " +

                        COLUMN_AREA_LAT + " DOUBLE NOT NULL, " +
                        COLUMN_AREA_LON + " DOUBLE NOT NULL, " +
                        COLUMN_RAY + " INTEGER NOT NULL, " +
                        COLUMN_LAT + " DOUBLE NOT NULL, " +
                        COLUMN_LON + " DOUBLE NOT NULL, " +
                        COLUMN_NUMSTAGE + " INTEGER NOT NULL, " +
                        COLUMN_CLUE + " TEXT, " +
                        COLUMN_ISLOCATIONREQUIRED + " BOOLEAN NOT NULL, " +
                        COLUMN_ISPHOTOREQUIRED + " BOOLEAN NOT NULL, " +
                        COLUMN_ISCHECKREQUIRED + " BOOLEAN NOT NULL, " +
                        COLUMN_NUMUSERTOFINISH + " INTEGER NOT NULL, " +

                        COLUMN_IDHUNT + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_IDHUNT + ") REFERENCES " + HUNTTABLE + "(" + COLUMN_IDHUNT + "));";

        private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }



    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(StageTable.SQL_DROP_TABLE);
        db.execSQL(HuntTable.SQL_DROP_TABLE);
        db.execSQL(UserTable.SQL_DROP_TABLE);
        db.execSQL(UserTable.SQL_CREATE_TABLE);
        db.execSQL(HuntTable.SQL_CREATE_TABLE);
        db.execSQL(StageTable.SQL_CREATE_TABLE);
        //context.deleteDatabase(DATABASE_NAME);

    }

    public static DBHelper getInstance(Context context){
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
        db.execSQL(StageTable.SQL_CREATE_TABLE);
        Log.v("db log", "Create Table Stage eseguito");

        db.execSQL(AddStageTable.SQL_CREATE_TABLE);
        Log.v("db log", "Create Table Stage eseguito");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(StageTable.SQL_DROP_TABLE);
        db.execSQL(HuntTable.SQL_DROP_TABLE);
        db.execSQL(UserTable.SQL_DROP_TABLE);

        db.execSQL(AddStageTable.SQL_DROP_TABLE);

        onCreate(db);
    }


    public int createDB(SQLiteDatabase db, String res) throws JSONException {
        JSONObject user = new JSONObject(res);

        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_IDUSER, user.getString("idUser"));
        values.put(UserTable.COLUMN_USERNAME, user.getString("username"));
        values.put(UserTable.COLUMN_EMAIL, user.getString("email"));
        values.put(UserTable.COLUMN_PHOTO, user.isNull("photo") == false ? user.getString("photo") : "");
        values.put(UserTable.COLUMN_PHONE, user.isNull("phone") == false ? user.getString("phone") : "");


        db.insert(UserTable.TABLE_NAME,null,values);
        Log.v("db log", "Insert User eseguito");
        if (user.isNull("hunts") == false) {
            JSONArray hunts_create = user.getJSONArray("hunts");
            JSONObject hunt = null;
            if (hunts_create != null || hunts_create.length()==0) {
                for (int i = 0; i < hunts_create.length(); i++) {

                    hunt = hunts_create.getJSONObject(i);
                    values = new ContentValues();
                    values.put(HuntTable.COLUMN_NAME, hunt.getString("name"));
                    values.put(HuntTable.COLUMN_IDHUNT, hunt.getString("idHunt"));
                    values.put(HuntTable.COLUMN_MAXTEAM, hunt.getString("maxTeam"));
                    values.put(HuntTable.COLUMN_TIMESTART, hunt.getString("timeStart"));
                    values.put(HuntTable.COLUMN_TIMEEND, hunt.getString("timeEnd"));
                    values.put(HuntTable.COLUMN_DESCRIPTION, hunt.isNull("description") == false ? hunt.getString("description") : "");
                    values.put(HuntTable.COLUMN_ISFINISHED, hunt.getString("isFinished"));
                    values.put(HuntTable.COLUMN_IDUSER, user.getString("idUser"));

                    db.insert(HuntTable.TABLE_NAME, null, values);
                    Log.v("db log", "Insert Hunt eseguito");

                    if (hunt.isNull("stages") == false) {
                        JSONArray stages_create = user.getJSONArray("hunts");
                        JSONObject stage = null;
                        if (hunts_create != null || hunts_create.length()==0) {
                            for (int j = 0; j < hunts_create.length(); j++) {

                                stage = stages_create.getJSONObject(j);
                                values = new ContentValues();


                                values.put(StageTable.COLUMN_IDSTAGE, stage.getString("idStage"));
                                values.put(StageTable.COLUMN_RAY, stage.isNull("ray") == false ? hunt.getString("ray"):"");
                                values.put(StageTable.COLUMN_NUMSTAGE, hunt.getString("numStage"));
                                values.put(StageTable.COLUMN_CLUE, stage.isNull("clue") == false ? hunt.getString("clue"):"");
                                values.put(StageTable.COLUMN_ISLOCATIONREQUIRED, hunt.getString("isLocationRequired"));
                                values.put(StageTable.COLUMN_ISPHOTOREQUIRED, hunt.getString("isPhotoRequired"));
                                values.put(StageTable.COLUMN_IDHUNT, user.getString("idHunt"));
                                values.put(StageTable.COLUMN_AREA_LAT, user.getString("areaLat"));
                                values.put(StageTable.COLUMN_AREA_LON, user.getString("areaLon"));
                                values.put(StageTable.COLUMN_LAT, user.getString("lat"));
                                values.put(StageTable.COLUMN_LON, user.getString("lon"));

                                db.insert(StageTable.TABLE_NAME, null, values);

                                Log.v("db log", "Insert Stage eseguito");

                            }
                        }
                    }



                }
            }
        }
        Log.v("iduser dbheper",user.getString("idUser"));
        return Integer.parseInt(user.getString("idUser"));
    }

    public void addStage(SQLiteDatabase db, int idUser, String clue, int ray, double areaLat, double areaLon, double  lat, double lon,boolean isLocationRequired, boolean isCheckRequired, boolean isPhotoRequired, int numUserToFinish)  {
        if (idUser != 0){
            ContentValues values = new ContentValues();
            values.put(AddStageTable.COLUMN_RAY, ray);
            values.put(AddStageTable.COLUMN_AREA_LAT, areaLat);
            values.put(AddStageTable.COLUMN_AREA_LON, areaLon);
            values.put(AddStageTable.COLUMN_LAT, lat);
            values.put(AddStageTable.COLUMN_LON, lon);
            values.put(AddStageTable.COLUMN_CLUE, clue);
            values.put(AddStageTable.COLUMN_IDUSER, idUser);

            values.put(AddStageTable.COLUMN_ISLOCATIONREQUIRED, isLocationRequired);
            values.put(AddStageTable.COLUMN_ISCHECKREQUIRED, isCheckRequired);
            values.put(AddStageTable.COLUMN_ISPHOTOREQUIRED, isPhotoRequired);
            values.put(AddStageTable.COLUMN_NUMUSERTOFINISH, numUserToFinish);

            db.insert(AddStageTable.TABLE_NAME,null,values);

            Log.v("db log", "Insert AddStage eseguito");

        }
    }




}
