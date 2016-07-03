package com.example.andrea22.gamehunt.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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


    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(UserTable.SQL_CREATE_TABLE);
        db.execSQL(HuntTable.SQL_CREATE_TABLE);
        db.execSQL(StageTable.SQL_CREATE_TABLE);
        db.execSQL(BeTable.SQL_CREATE_TABLE);
        db.execSQL(AddStageTable.SQL_CREATE_TABLE);
        db.execSQL(AddTeamTable.SQL_CREATE_TABLE);

    }

    public void resetDatabase(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(BeTable.SQL_DROP_TABLE);
        db.execSQL(StageTable.SQL_DROP_TABLE);
        db.execSQL(HuntTable.SQL_DROP_TABLE);
        db.execSQL(UserTable.SQL_DROP_TABLE);
        db.execSQL(UserTable.SQL_CREATE_TABLE);
        db.execSQL(HuntTable.SQL_CREATE_TABLE);
        db.execSQL(StageTable.SQL_CREATE_TABLE);
        db.execSQL(BeTable.SQL_CREATE_TABLE);
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
        Log.v("db log", "Create Table AddStage eseguito");
        db.execSQL(AddTeamTable.SQL_CREATE_TABLE);
        Log.v("db log", "Create Table AddTeam eseguito");
        db.execSQL(BeTable.SQL_CREATE_TABLE);
        Log.v("db log", "Create Table Be eseguito");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(StageTable.SQL_DROP_TABLE);
        db.execSQL(HuntTable.SQL_DROP_TABLE);
        db.execSQL(UserTable.SQL_DROP_TABLE);
        db.execSQL(BeTable.SQL_DROP_TABLE);

        onCreate(db);
    }


    public int createDB(SQLiteDatabase db, String res) throws JSONException {

        resetDatabase();
        JSONObject user = new JSONObject(res);

        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_IDUSER, user.getString("idUser"));
        values.put(UserTable.COLUMN_USERNAME, user.getString("username"));
        values.put(UserTable.COLUMN_EMAIL, user.getString("email"));
        values.put(UserTable.COLUMN_PHOTO, user.isNull("photo") == false ? user.getString("photo") : "");
        values.put(UserTable.COLUMN_PHONE, user.isNull("phone") == false ? user.getString("phone") : "");


        db.insert(UserTable.TABLE_NAME, null, values);
        Log.v("db log", "Insert User eseguito");
        if (user.isNull("hunts") == false) {
            JSONArray hunts_create = user.getJSONArray("hunts");
            JSONObject hunt = null;
            if (hunts_create != null) {
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
                        JSONArray stages_create = hunt.getJSONArray("stages");
                        JSONObject stage = null;
                        if (stages_create != null) {
                            for (int j = 0; j < stages_create.length(); j++) {

                                stage = stages_create.getJSONObject(j);
                                values = new ContentValues();


                                values.put(StageTable.COLUMN_IDSTAGE, stage.getString("idStage"));
                                values.put(StageTable.COLUMN_RAY, stage.isNull("ray") == false ? stage.getString("ray"):"");
                                values.put(StageTable.COLUMN_NUMSTAGE, stage.getString("numStage"));
                                values.put(StageTable.COLUMN_CLUE, stage.isNull("clue") == false ? stage.getString("clue"):"");
                                values.put(StageTable.COLUMN_ISLOCATIONREQUIRED, stage.getString("isLocationRequired"));
                                values.put(StageTable.COLUMN_ISPHOTOREQUIRED, stage.getString("isPhotoRequired"));
                                values.put(StageTable.COLUMN_IDHUNT, stage.getString("idHunt"));
                                values.put(StageTable.COLUMN_AREA_LAT, stage.getString("areaLat"));
                                values.put(StageTable.COLUMN_AREA_LON, stage.getString("areaLon"));
                                values.put(StageTable.COLUMN_LAT, stage.getString("lat"));
                                values.put(StageTable.COLUMN_LON, stage.getString("lon"));

                                db.insert(StageTable.TABLE_NAME, null, values);

                                Log.v("db log", "Insert Stage eseguito");

                            }
                        }
                    }



                }
            }
        }
        Log.v("iduser dbheper", user.getString("idUser"));
        return Integer.parseInt(user.getString("idUser"));
    }

    public void insertHunt(SQLiteDatabase db, String res) throws JSONException {

        JSONObject hunt = new JSONObject(res);

        ContentValues values = new ContentValues();
        values.put(HuntTable.COLUMN_IDHUNT, hunt.getString("idHunt"));
        values.put(HuntTable.COLUMN_NAME, hunt.getString("name"));
        values.put(HuntTable.COLUMN_MAXTEAM, hunt.getString("maxTeam"));
        values.put(HuntTable.COLUMN_TIMESTART, hunt.getString("timeStart"));
        values.put(HuntTable.COLUMN_TIMEEND, hunt.getString("timeEnd"));
        values.put(HuntTable.COLUMN_DESCRIPTION, hunt.isNull("description") == false ? hunt.getString("description") : "");
        values.put(HuntTable.COLUMN_ISFINISHED, hunt.getString("isFinished"));
        values.put(HuntTable.COLUMN_IDUSER, hunt.getString("idUser"));



        db.insert(HuntTable.TABLE_NAME, null, values);
        Log.v("db log", "Insert Hunt eseguito");

        if (hunt.isNull("stages") == false) {
            JSONArray stages_create = hunt.getJSONArray("stages");
            JSONObject stage = null;
            for (int i = 0; i < stages_create.length(); i++) {

                stage = stages_create.getJSONObject(i);
                values = new ContentValues();


                values.put(StageTable.COLUMN_IDSTAGE, stage.getString("idStage"));
                values.put(StageTable.COLUMN_RAY, stage.isNull("ray") == false ? stage.getString("ray"):"");
                values.put(StageTable.COLUMN_NUMSTAGE, stage.getString("numStage"));
                values.put(StageTable.COLUMN_CLUE, stage.isNull("clue") == false ? stage.getString("clue"):"");
                values.put(StageTable.COLUMN_ISLOCATIONREQUIRED, stage.getString("isLocationRequired"));
                values.put(StageTable.COLUMN_ISPHOTOREQUIRED, stage.getString("isPhotoRequired"));
                values.put(StageTable.COLUMN_ISCHECKREQUIRED, stage.getString("isCheckRequired"));
                values.put(StageTable.COLUMN_IDHUNT, hunt.getString("idHunt"));
                values.put(StageTable.COLUMN_AREA_LAT, stage.getString("areaLat"));
                values.put(StageTable.COLUMN_AREA_LON, stage.getString("areaLon"));
                values.put(StageTable.COLUMN_LAT, stage.getString("lat"));
                values.put(StageTable.COLUMN_LON, stage.getString("lon"));
                values.put(StageTable.COLUMN_NUMUSERTOFINISH, stage.getString("numUserToFinish"));

                db.insert(StageTable.TABLE_NAME, null, values);

                Log.v("db log", "Insert Stage eseguito");

            }
        }

    }


    public void insertTeam(SQLiteDatabase db, String res) throws JSONException {

        JSONObject team = new JSONObject(res);

        ContentValues values = new ContentValues();
        values.put(TeamTable.COLUMN_IDTEAM, team.getString("idTeam"));
        values.put(TeamTable.COLUMN_NAME, team.getString("name"));
        values.put(TeamTable.COLUMN_SLOGAN, team.getString("slogan"));
        values.put(TeamTable.COLUMN_IDHUNT, team.getString("idHunt"));


        db.insert(TeamTable.TABLE_NAME, null, values);
        Log.v("db log", "Insert Team eseguito");

        if (team.isNull("users") == false) {

            JSONArray users = team.getJSONArray("users");
            JSONObject user = null;
            for (int i = 0; i < users.length(); i++) {

                user = users.getJSONObject(i);
                Cursor c = db.rawQuery("SELECT idUser FROM USER WHERE idUser = "+user.getString("idUser"), null);
                if (c.getCount() == 0 ) {

                    values = new ContentValues();

                    values.put(UserTable.COLUMN_IDUSER, user.getString("idUser"));
                    values.put(UserTable.COLUMN_USERNAME, user.getString("username"));
                    values.put(UserTable.COLUMN_EMAIL, user.getString("email"));
                    values.put(UserTable.COLUMN_PHONE, user.getString("phone"));
                    values.put(UserTable.COLUMN_PHOTO, user.getString("photo"));

                    db.insert(UserTable.TABLE_NAME, null, values);

                    Log.v("db log", "Insert User eseguito");

                }

                values = new ContentValues();

                values.put(BeTable.COLUMN_IDTEAM, team.getString("idTeam"));
                values.put(BeTable.COLUMN_IDUSER, user.getString("idUser"));

                db.insert(BeTable.TABLE_NAME, null, values);

                Log.v("db log", "Insert Be eseguito");


            }

        }

    }


    public void insertAddStage(SQLiteDatabase db, int idUser, String clue, int ray, double areaLat, double areaLon, double  lat, double lon,boolean isLocationRequired, boolean isCheckRequired, boolean isPhotoRequired, int numUserToFinish)  {
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


    public void insertAddTeam(SQLiteDatabase db, int idUser, int idHunt, String name, String users)  {
        if (idUser != 0){

            ContentValues values = new ContentValues();

            values.put(AddTeamTable.COLUMN_IDUSER, idUser);
            values.put(AddTeamTable.COLUMN_IDHUNT, idHunt);
            values.put(AddTeamTable.COLUMN_NAME, name);
            values.put(AddTeamTable.COLUMN_USERS, users);


            db.insert(AddTeamTable.TABLE_NAME,null,values);

            Log.v("db log", "Insert AddTeam eseguito");

        }
    }





}
