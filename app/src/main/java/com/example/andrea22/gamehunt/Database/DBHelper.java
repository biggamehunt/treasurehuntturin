package com.example.andrea22.gamehunt.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.andrea22.gamehunt.LoginActivity;
import com.example.andrea22.gamehunt.Entity.InfoHuntForCheck;
import com.example.andrea22.gamehunt.Entity.SingleStage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        db.execSQL(TeamTable.SQL_CREATE_TABLE);


    }

    public void resetDatabase(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(BeTable.SQL_DROP_TABLE);
        db.execSQL(TeamTable.SQL_DROP_TABLE);
        db.execSQL(StageTable.SQL_DROP_TABLE);
        db.execSQL(HuntTable.SQL_DROP_TABLE);
        db.execSQL(UserTable.SQL_DROP_TABLE);
        db.execSQL(UserTable.SQL_CREATE_TABLE);
        db.execSQL(HuntTable.SQL_CREATE_TABLE);
        db.execSQL(StageTable.SQL_CREATE_TABLE);
        db.execSQL(TeamTable.SQL_CREATE_TABLE);
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
        db.execSQL(TeamTable.SQL_CREATE_TABLE);
        Log.v("db log", "Create Table Team eseguito");
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
        db.beginTransaction();
        resetDatabase();
        JSONObject user = new JSONObject(res);

        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_IDUSER, user.getString("idUser"));
        values.put(UserTable.COLUMN_USERNAME, user.getString("username"));
        values.put(UserTable.COLUMN_EMAIL, user.getString("email"));
        values.put(UserTable.COLUMN_PHOTO, user.isNull("photo") == false ? user.getString("photo") : "");
        values.put(UserTable.COLUMN_PHONE, user.isNull("phone") == false ? user.getString("phone") : "");

        try {
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
                        values.put(HuntTable.COLUMN_IDUSER, hunt.getString("idUser"));
                        values.put(HuntTable.COLUMN_ISSTAGESEMPTY, hunt.getString("isStagesEmpty"));
                        values.put(HuntTable.COLUMN_ISTEAMSEMPTY, hunt.getString("isTeamsEmpty"));
                        values.put(HuntTable.COLUMN_IDWINNER, hunt.isNull("idWinner") == false ? hunt.getString("idWinner") : "");
                        values.put(HuntTable.COLUMN_NAMEWINNER, hunt.isNull("nameWinner") == false ? hunt.getString("nameWinner") : "");
                        values.put(HuntTable.COLUMN_PHOTOTOCHECK, hunt.getString("photoToCheck"));


                        db.insert(HuntTable.TABLE_NAME, null, values);
                        Log.v("db log", "Insert Hunt eseguito");



                    }
                }
            }


            db.setTransactionSuccessful();
        } catch (Exception e){
            //Error in between database transaction
        } finally {
            db.endTransaction();
        }
        Log.v("iduser dbheper", user.getString("idUser"));
        return Integer.parseInt(user.getString("idUser"));
    }

    public int insertCreateHunt(SQLiteDatabase db, String res) throws JSONException {

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
        values.put(HuntTable.COLUMN_ISSTAGESEMPTY, hunt.getString("isStagesEmpty"));
        values.put(HuntTable.COLUMN_ISTEAMSEMPTY, hunt.getString("isTeamsEmpty"));



        db.insert(HuntTable.TABLE_NAME, null, values);
        Log.v("db log", "Insert Hunt eseguito");

        return Integer.parseInt(hunt.getString("idHunt"));

    }


    public void insertCreateTeams(SQLiteDatabase db, String res) throws JSONException {

        JSONObject hunt = new JSONObject(res);

        JSONArray teams = hunt.getJSONArray("teams");

        ContentValues values;

        for (int i = 0; i < teams.length(); i++) {
            values = new ContentValues();
            JSONObject team = teams.getJSONObject(i);
            values.put(TeamTable.COLUMN_IDTEAM, team.getString("idTeam"));
            values.put(TeamTable.COLUMN_NAME, team.getString("name"));
            values.put(TeamTable.COLUMN_SLOGAN, team.getString("slogan"));
            values.put(TeamTable.COLUMN_IDCURRENTSTAGE, team.getString("idCurrentStage"));
            values.put(TeamTable.COLUMN_IDHUNT, hunt.getString("idHunt"));


            db.insert(TeamTable.TABLE_NAME, null, values);
            Log.v("db log", "Insert Team eseguito, con valore idTeam: "+team.getString("idTeam"));

            if (team.isNull("users") == false) {

                JSONArray users = team.getJSONArray("users");
                JSONObject user = null;
                for (int j = 0; j < users.length(); j++) {

                    user = users.getJSONObject(j);
                    Cursor c = db.rawQuery("SELECT idUser FROM USER WHERE idUser = "+user.getString("idUser"), null);
                    if (c.getCount() == 0 ) {

                        values = new ContentValues();

                        values.put(UserTable.COLUMN_IDUSER, user.getString("idUser"));
                        values.put(UserTable.COLUMN_USERNAME, user.getString("username"));
                        values.put(UserTable.COLUMN_EMAIL, user.getString("email"));
                        values.put(UserTable.COLUMN_PHOTO, user.isNull("photo") == false ? user.getString("photo") : "");
                        values.put(UserTable.COLUMN_PHONE, user.isNull("phone") == false ? user.getString("phone") : "");


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



    }


    public void insertAddStage(SQLiteDatabase db, int idUser, int idHunt, int numStage, String name, String clue, int ray, double areaLat, double areaLon, double  lat, double lon, int isLocationRequired, int isPhotoRequired, int isCheckRequired, int numUserToFinish)  {
        if (idUser != 0){
            ContentValues values = new ContentValues();
            values.put(AddStageTable.COLUMN_IDUSER, idUser);
            values.put(AddStageTable.COLUMN_NUMSTAGE, numStage);

            values.put(AddStageTable.COLUMN_RAY, ray);
            values.put(AddStageTable.COLUMN_AREA_LAT, areaLat);
            values.put(AddStageTable.COLUMN_AREA_LON, areaLon);
            values.put(AddStageTable.COLUMN_LAT, lat);
            values.put(AddStageTable.COLUMN_LON, lon);
            values.put(AddStageTable.COLUMN_CLUE, clue);
            values.put(AddStageTable.COLUMN_NAME, name);

            values.put(AddStageTable.COLUMN_IDUSER, idUser);

            values.put(AddStageTable.COLUMN_ISLOCATIONREQUIRED, isLocationRequired);
            values.put(AddStageTable.COLUMN_ISCHECKREQUIRED, isCheckRequired);
            values.put(AddStageTable.COLUMN_ISPHOTOREQUIRED, isPhotoRequired);
            values.put(AddStageTable.COLUMN_NUMUSERTOFINISH, numUserToFinish);
            values.put(AddStageTable.COLUMN_IDHUNT, idHunt);

            db.insert(AddStageTable.TABLE_NAME,null,values);

            Log.v("db log", "Insert AddStage eseguito");

        }
    }


    public void insertAddTeam(SQLiteDatabase db, int idUser, int idHunt, String name, int numTeam, String users)  {
        if (idUser != 0){

            ContentValues values = new ContentValues();

            values.put(AddTeamTable.COLUMN_IDUSER, idUser);
            values.put(AddTeamTable.COLUMN_IDHUNT, idHunt);
            values.put(AddTeamTable.COLUMN_NAME, name);
            values.put(AddTeamTable.COLUMN_USERS, users);
            values.put(AddTeamTable.COLUMN_NUMTEAM, numTeam);


            db.insert(AddTeamTable.TABLE_NAME, null, values);

            Log.v("db log", "Insert AddTeam eseguito");

        }
    }



    public boolean deleteAddTeam(SQLiteDatabase db, int idUser, int idHunt, int numTeam)  {
        if (idUser != 0){
            try{

                db.execSQL("DELETE FROM ADDTEAM WHERE numTeam =" + numTeam + " AND idUser = "+idUser+" AND idHunt = " + idHunt + ";");

                Log.v("db log", "Delete AddTeam eseguito");

                db.execSQL("UPDATE ADDTEAM SET numTeam = (numTeam - 1) WHERE numTeam > " + numTeam + ";");

                Log.v("db log", "Update numTeams eseguito");


            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;

        }
        return false;

    }

    public String selectUserAddTeam(SQLiteDatabase db, int idHunt, int numTeam)  {
        if (idHunt != 0 && numTeam != 0){
            String users = "";
            Cursor c = db.rawQuery("SELECT users FROM ADDTEAM WHERE numTeam =" + numTeam + " AND idHunt = " + idHunt + ";", null);
            if (c.moveToFirst()) {
                do {
                    users = c.getString(c.getColumnIndex("users"));
                } while (c.moveToNext());
            }

            Log.v("db log", "Select users eseguito");

            return users;

        }
        return null;
    }

    public String selectAllUserAddTeam(SQLiteDatabase db, int idHunt)  {
        if (idHunt != 0){
            String users = "";
            Cursor c = db.rawQuery("SELECT users FROM ADDTEAM WHERE idHunt = " + idHunt + ";", null);
            if (c.moveToFirst()) {
                do {
                    users += c.getString(c.getColumnIndex("users"))+"|";
                } while (c.moveToNext());
            }

            Log.v("db log", "Select all users eseguito");

            return users;

        }
        return null;
    }

    public String[] selectNameSloganTeam(SQLiteDatabase db, int idHunt, int numTeam)  {
        if (idHunt != 0 && numTeam != 0){
            String[] users = new String[2];
            Cursor c = db.rawQuery("SELECT name, slogan FROM ADDTEAM WHERE numTeam =" + numTeam + " AND idHunt = " + idHunt + ";", null);
            if (c.moveToFirst()) {
                do {
                    users[0] = c.getString(c.getColumnIndex("name"));
                    users[1] = c.getString(c.getColumnIndex("slogan"));

                } while (c.moveToNext());
            }

            Log.v("db log", "Select name e slogan eseguito");

            return users;

        }
        return null;
    }

    public void insertUserAddTeam(SQLiteDatabase db, int idHunt, int numTeam, String username)  {
        if (numTeam != 0){
            String oldusername = "";
            Cursor c = db.rawQuery("SELECT users FROM ADDTEAM WHERE numTeam =" + numTeam + " AND idHunt = " + idHunt + ";", null);
            if (c.moveToFirst()) {
                do {
                    oldusername = c.getString(c.getColumnIndex("users"));
                } while (c.moveToNext());
            }
            Log.v("db log", "oldusername:"+oldusername);
            Log.v("db log", "newusername:"+oldusername + username.trim() + "|");

            db.execSQL("UPDATE ADDTEAM SET users = '" + oldusername + username.trim() + "|' WHERE numTeam =" + numTeam + " AND idHunt = " + idHunt + ";");


            Log.v("db log", "Update AddTeam eseguito");

        }
    }

    public void removeUserAddTeam(SQLiteDatabase db, int idHunt, int numTeam, String username)  {
        if (numTeam != 0){
            String oldusername = "";
            Cursor c = db.rawQuery("SELECT users FROM ADDTEAM WHERE numTeam =" + numTeam + " AND idHunt = " + idHunt + ";", null);
            if (c.moveToFirst()) {
                do {
                    oldusername = c.getString(c.getColumnIndex("users"));
                } while (c.moveToNext());
            }
            username = username.trim() + "\\|";
            String newusername = oldusername.replaceAll(username, "");

            Log.v("db log", "oldusername:"+oldusername);
            Log.v("db log", "newusername:" + newusername);

            db.execSQL("UPDATE ADDTEAM SET users = '" + newusername + "' WHERE numTeam =" + numTeam + " AND idHunt = " + idHunt + ";");


            Log.v("db log", "Update AddTeam eseguito");

        }
    }

    public int[] getHuntIsLoadedIsStartedIsEnded(SQLiteDatabase db, int idHunt)  {

        int[] info = new int[3];
        Cursor c = db.rawQuery("SELECT isLoaded, isStarted, isEnded FROM HUNT WHERE idHunt =" + idHunt + ";", null);
        if (c.moveToFirst()) {
            do {
                info[0] = c.getInt(c.getColumnIndex("isLoaded"));
                info[1] = c.getInt(c.getColumnIndex("isStarted"));
                info[2] = c.getInt(c.getColumnIndex("isEnded"));

            } while (c.moveToNext());
        }

        Log.v("db log", "get isLoaded = "+ info[0]);
        Log.v("db log", "get isStarted = "+ info[1]);
        Log.v("db log", "get isEnded = "+ info[2]);


        return info;
    }


    public void setHuntIsLoaded(SQLiteDatabase db, int idHunt)  {

        db.execSQL("UPDATE HUNT SET isLoaded = 1 WHERE idHunt =" + idHunt + ";");

        Log.v("db log", "set isLoaded!");

    }

    public void insertHuntDetail(SQLiteDatabase db, String res, int idUser) throws JSONException {




        ContentValues values;
        JSONObject hunt = new JSONObject(res);
        JSONArray stages = hunt.getJSONArray("stages");
        JSONObject stage = null;
        Log.v("db log", "stages.length():"+stages.length());

        for (int i = 0; i < stages.length(); i++) {

            stage = stages.getJSONObject(i);
            values = new ContentValues();

            values.put(StageTable.COLUMN_IDSTAGE, stage.getString("idStage"));
            values.put(StageTable.COLUMN_RAY, stage.isNull("ray") == false ? stage.getString("ray"):"");
            values.put(StageTable.COLUMN_NUMSTAGE, stage.getString("numStage"));
            values.put(StageTable.COLUMN_CLUE, stage.isNull("clue") == false ? stage.getString("clue"):"");
            values.put(StageTable.COLUMN_NAME, stage.getString("name"));

            values.put(StageTable.COLUMN_ISLOCATIONREQUIRED, stage.getString("isLocationRequired"));
            values.put(StageTable.COLUMN_ISPHOTOREQUIRED, stage.getString("isPhotoRequired"));
            values.put(StageTable.COLUMN_ISCHECKREQUIRED, stage.getString("isCheckRequired"));
            values.put(StageTable.COLUMN_ISPHOTOCHECKED, stage.isNull("isPhotoChecked") == false ? stage.getInt("isPhotoChecked"):0);
            values.put(StageTable.COLUMN_ISPHOTOSENDED, stage.isNull("isPhotoSended") == false ? stage.getInt("isPhotoSended"):0);
            values.put(StageTable.COLUMN_USERCOMPLETED, stage.isNull("userCompleted") == false ? stage.getInt("userCompleted"):0);
            values.put(StageTable.COLUMN_TEAMCOMPLETED, stage.isNull("teamCompleted") == false ? stage.getInt("teamCompleted"):0);

            values.put(StageTable.COLUMN_AREA_LAT, stage.getString("areaLat"));
            values.put(StageTable.COLUMN_AREA_LON, stage.getString("areaLon"));
            values.put(StageTable.COLUMN_LAT, stage.getString("lat"));
            values.put(StageTable.COLUMN_LON, stage.getString("lon"));
            values.put(StageTable.COLUMN_NUMUSERTOFINISH, stage.getString("numUserToFinish"));
            values.put(StageTable.COLUMN_IDHUNT, hunt.getString("idHunt"));



            db.insert(StageTable.TABLE_NAME, null, values);

            Log.v("db log", "Insert Stage eseguito");

        }


        JSONObject team = hunt.getJSONObject("team");
        Log.v("db log", "team:"+team);

        values = new ContentValues();

        values.put(TeamTable.COLUMN_IDTEAM, team.getString("idTeam"));
        values.put(TeamTable.COLUMN_NAME, team.getString("name"));
        values.put(TeamTable.COLUMN_SLOGAN, team.getString("slogan"));
        values.put(TeamTable.COLUMN_IDCURRENTSTAGE, team.getString("idCurrentStage"));
        values.put(TeamTable.COLUMN_IDHUNT, hunt.getString("idHunt"));


        db.insert(TeamTable.TABLE_NAME, null, values);
        Log.v("db log", "Insert Team eseguito");


        JSONObject user;

        if (team.has("users")){
            JSONArray users = team.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {

                user = users.getJSONObject(i);
                values = new ContentValues();

                values.put(UserTable.COLUMN_IDUSER, user.getString("idUser"));
                values.put(UserTable.COLUMN_USERNAME, user.getString("username"));
                values.put(UserTable.COLUMN_EMAIL, user.getString("email"));

                db.insertWithOnConflict(UserTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                Log.v("db log", "Insert User eseguito");

                values = new ContentValues();

                values.put(BeTable.COLUMN_IDTEAM, team.getString("idTeam"));
                values.put(BeTable.COLUMN_IDUSER, user.getString("idUser"));
                values.put(BeTable.COLUMN_ISCOMPLETE, user.getString("stageComplete"));


                db.insert(BeTable.TABLE_NAME, null, values);

                Log.v("db log", "Insert Be eseguito");


            }
        } else {
            values = new ContentValues();

            values.put(BeTable.COLUMN_IDTEAM, team.getString("idTeam"));
            values.put(BeTable.COLUMN_IDUSER, idUser);


            db.insert(BeTable.TABLE_NAME, null, values);
        }





        db.execSQL("UPDATE HUNT SET isStarted = "+hunt.getInt("isStarted")+" AND isEnded = "+hunt.getInt("isEnded")+" WHERE idHunt =" + hunt.getString("idHunt") + ";");




    }

    public void setAfterPhotoSended(SQLiteDatabase db, String out, int idStage, int idTeam, int idUser, int idHunt, String name)  {

        try {
            //todo : devo sistemare i parametri come "isCompleted" su Team o "isPhotoSended" su Stage al login, perché per ora sono tutti in default = 0


            JSONObject json = new JSONObject(out);

            if (json.has("userIsPhotoSended") && json.get("userIsPhotoSended").equals("1")){
                db.execSQL("UPDATE STAGE SET isPhotoSended = 1 WHERE idStage =" + idStage + ";");
                Log.v("db log", "idPhotoSended = 1");
            }

            if (json.has("userIsCompleted") && json.get("userIsCompleted").equals("1")){
                db.execSQL("UPDATE STAGE SET userCompleted = 1 WHERE idStage =" + idStage + ";");

                String users="";
                Cursor c = db.rawQuery("SELECT * FROM BE WHERE idTeam = " + idTeam + ";", null);
                if (c.moveToFirst()) {

                    do {
                        users += c.getString(c.getColumnIndex("idUser"))+"&";
                    } while (c.moveToNext());
                }
                LoginActivity.mWebSocketClient.send("uc:" + users + "-" + idUser + "-" + idTeam + "-" + idHunt);
                Log.v("db log", "userCompleted = 1");

            }
            if (json.has("teamIsCompleted") && json.get("teamIsCompleted").equals("1")){
                db.execSQL("UPDATE STAGE SET teamCompleted = 1 WHERE idStage =" + idStage + ";");
                Log.v("db log", "teamCompleted = 1");

                if (json.has("nextStage")){
                    db.execSQL("UPDATE BE SET isComplete = 0 WHERE idTeam =" + idTeam + ";");

                    //la caccia al tesoro non è ancora finita!
                    db.execSQL("UPDATE TEAM SET idCurrentStage = "+json.get("nextStage")+" WHERE idTeam =" + idTeam + ";");
                    Log.v("db log", "nextstage = "+json.get("nextStage"));
                    if (json.has("users")){
                        JSONArray jUsers = json.getJSONArray("users");
                        String users="";

                        for (int i=0; i<jUsers.length();i++){
                            if (jUsers.getInt(i)!=idUser) {
                                users += jUsers.getInt(i)+"&";
                            }
                        }
                        name=name.replace("-","___");
                        name=name.replace("&","£$%");
                        LoginActivity.mWebSocketClient.send("up:" + users+"-"+idStage+"-"+json.get("nextStage")+"-"+idTeam+"-"+idHunt+"-"+name);

                        //

                    }
                } else {
                    //la caccia al tesoro è finita!
                    db.execSQL("UPDATE TEAM SET idCurrentStage = null WHERE idTeam =" + idTeam + ";");
                    db.execSQL("UPDATE TEAM SET isCompleted = 1 WHERE idTeam =" + idTeam + ";");

                    if (json.has("users")){
                        JSONArray jUsers = json.getJSONArray("users");
                        String users="";

                        for (int i=0; i<jUsers.length();i++){
                            if (jUsers.getInt(i)!=idUser) {
                                users += jUsers.getInt(i)+"&";
                            }
                        }
                        name=name.replace("-","___");
                        name=name.replace("&","£$%");
                        LoginActivity.mWebSocketClient.send("eh:" + users+"-"+idStage+"-"+idTeam+"-"+idHunt+"-"+name);

                    }


                }



            }





        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean notifyFromTeamStageCompleted(SQLiteDatabase db, int idStage, int idNextStage, int idTeam)  {

        try {
                Log.v("db log", "prima del set teamCompleted");
                db.execSQL("UPDATE STAGE SET teamCompleted = 1 WHERE idStage =" + idStage + ";");
                Log.v("db log", "prima del set idCurrentStage");

            db.execSQL("UPDATE BE SET isComplete = 0 WHERE idTeam =" + idTeam + ";");

            db.execSQL("UPDATE TEAM SET idCurrentStage = " + idNextStage + " WHERE idTeam =" + idTeam + ";");


        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

    }


    public boolean notifyCheckFailed(SQLiteDatabase db, int idStage)  {

        try {
            Log.v("db log", "prima del set isPhotoSended");
            db.execSQL("UPDATE STAGE SET isPhotoSended = 0 WHERE idStage =" + idStage + ";");
            Log.v("db log", "dopo il set isPhotoSended");



        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean notifyUserComplete(SQLiteDatabase db, int idStage, int idUser, int idTeam, int idHunt)  {

        try {
            Log.v("db log", "prima del set isPhotoChecked & userCompleted");
            db.execSQL("UPDATE STAGE SET isPhotoChecked = 1, userCompleted = 1  WHERE idStage =" + idStage + ";");


            Log.v("db log", "dopo il set isPhotoChecked & userCompleted");
            String users="";
            Cursor c = db.rawQuery("SELECT * FROM BE WHERE idTeam = " + idTeam + ";", null);
            if (c.moveToFirst()) {

                do {
                    users += c.getString(c.getColumnIndex("idUser"))+"&";
                } while (c.moveToNext());
            }
            LoginActivity.mWebSocketClient.send("uc:" + users + "-" + idUser + "-" + idTeam + "-" + idHunt);


        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean notifyFromTeamHuntCompleted(SQLiteDatabase db, int idStage, int idTeam, int idHunt)  {

        try {
            Log.v("db log", "prima del set teamCompleted");
            db.execSQL("UPDATE STAGE SET teamCompleted = 1 WHERE idStage =" + idStage + ";");
            Log.v("db log", "prima del set idCurrentStage");

            db.execSQL("UPDATE TEAM SET idCurrentStage = null, isCompleted = 1 WHERE idTeam =" + idTeam + ";");

            String nameTeam = "";
            Cursor c = db.rawQuery("SELECT name FROM TEAM WHERE idTeam = " + idTeam + ";", null);
            if (c.moveToFirst()) {

                do {
                    nameTeam += c.getString(c.getColumnIndex("name"));
                } while (c.moveToNext());
            }


            db.execSQL("UPDATE HUNT SET idWinner = " + idTeam +", nameWinner = '" + nameTeam + "' WHERE idHunt =" + idHunt + ";");


        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void notifyUserHasCompleted (SQLiteDatabase db, int idUser, int idTeam){
        try {
            Log.v("db log", "prima del set isComplete");
            db.execSQL("UPDATE BE SET isComplete = 1 WHERE idUser =" + idUser + " AND idTeam = " + idTeam + ";");
            Log.v("db log", "dopo il set isComplete");


        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public boolean updateSlogan(SQLiteDatabase db, String slogan, int numTeam, int idUser, int idHunt)  {

        try {
            if (idUser!=0 && idHunt!=0){
                db.execSQL("UPDATE ADDTEAM SET slogan = '" + slogan + "' WHERE numTeam = " + numTeam + " AND idUser = " + idUser + " AND idHunt = " + idHunt +";");

            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean updateName(SQLiteDatabase db, String name, int numTeam, int idUser, int idHunt)  {

        try {
            if (idUser!=0 && idHunt!=0){
                db.execSQL("UPDATE ADDTEAM SET name = '" + name + "' WHERE numTeam = " + numTeam + " AND idUser = " + idUser + " AND idHunt = " + idHunt +";");

            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean removeAddStage(SQLiteDatabase db, int numStage, int idUser, int idHunt)  {

        try {

            db.execSQL("DELETE FROM ADDSTAGE WHERE numStage = " + numStage + " AND idUser = "+idUser+" AND idHunt = " + idHunt + ";");

            Log.v("db log", "Delete AddStage eseguito");


        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateisStagesEmpty(SQLiteDatabase db, int idHunt, int isEmpty)  {

        try {
            Log.v("db log", "prima di updateisStageEmpty");
            db.execSQL("UPDATE HUNT SET isStagesEmpty = " + isEmpty + " WHERE idHunt =" + idHunt + ";");
            Log.v("db log", "dopo di updateisStageEmpty");


        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public boolean updateNumStages(SQLiteDatabase db, int[][] positions, int idUser, int idHunt)  {

        try {

           /* for (int i = 0; i<positions.length;i++) {
                Log.v("db log", "prima di updateNumStages");
                db.execSQL("UPDATE ADDSTAGE SET numStage =" + positions[i][1] + " WHERE numStage = " + positions[i][0] + " AND idUser = " + idUser + " AND idHunt = " + idHunt +";");
                Log.v("db log", "dopo updateNumStages");

            }*/

            String query = "UPDATE ADDSTAGE SET numStage=CASE ";
            String oldPositions = "";


            for (int i = 0; i<positions.length;i++) {
                query+="WHEN numStage="+positions[i][0]+" THEN "+positions[i][1]+" ";
                oldPositions+=positions[i][0]+",";
            }
            if (oldPositions != null && oldPositions.length() > 0){
                oldPositions = oldPositions.substring(0, oldPositions.length()-1);
            }
            query+=" END WHERE numStage in ("+oldPositions+") AND idUser = " + idUser + " AND idHunt = " + idHunt;

            db.execSQL(query);

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateisTeamsEmpty(SQLiteDatabase db, int idHunt, int isEmpty)  {

        try {
            Log.v("db log", "prima di updateisTeamsEmpty");
            db.execSQL("UPDATE HUNT SET isTeamsEmpty = " + isEmpty + " WHERE idHunt =" + idHunt + ";");
            Log.v("db log", "dopo di updateisTeamsEmpty");


        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<SingleStage> selectAddStage(SQLiteDatabase db, int idHunt, int idUser)  {
        if (idHunt != 0 && idUser != 0){
            List<SingleStage> stages = new ArrayList<>() ;
            Cursor c = db.rawQuery("SELECT * FROM ADDSTAGE WHERE idHunt = " + idHunt + " AND idUser = "+ idUser+" ORDER BY numStage ASC;", null);
            if (c.moveToFirst()) {

                SingleStage singleStage;
                do {

                    singleStage = new SingleStage(c.getString(c.getColumnIndex("name")),c.getInt(c.getColumnIndex("numStage")),c.getInt(c.getColumnIndex("isLocationRequired")),c.getInt(c.getColumnIndex("isCheckRequired")), c.getInt(c.getColumnIndex("isPhotoRequired")));
                    stages.add(singleStage);
                } while (c.moveToNext());
            }

            Log.v("db log", "Select users eseguito");

            return stages;

        }
        return null;
    }


    public ArrayList<InfoHuntForCheck> getHuntsFromUser(SQLiteDatabase db, int idUser)  {
        ArrayList<InfoHuntForCheck> hunts = new ArrayList<>();

        try {
            Cursor c = db.rawQuery( "SELECT * " +
                                    "FROM HUNT " +
                                    "WHERE idUser = "+idUser+" AND isEnded = 0 AND isFinished = 0  ORDER BY idHunt;", null);
            if (c.moveToFirst()) {
                InfoHuntForCheck hunt;


                do {

                    hunt = new InfoHuntForCheck(c.getInt(c.getColumnIndex("idHunt")),
                            c.getString(c.getColumnIndex("name")),c.getString(c.getColumnIndex("timeStart")));

                    hunts.add(hunt);

                } while (c.moveToNext());
            }



        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return hunts;
    }

    public String selectAllMembersTeam(SQLiteDatabase db, int idTeam)  {
        if (idTeam != 0){
            String users = "";
            Cursor c = db.rawQuery("SELECT users FROM TEAM WHERE idTeam = " + idTeam + ";", null);
            if (c.moveToFirst()) {
                do {
                    users += c.getString(c.getColumnIndex("users"))+"|";
                } while (c.moveToNext());
            }

            Log.v("db log", "Select all users eseguito");

            return users;

        }
        return null;
    }

    public boolean updatePhotoToCheck(SQLiteDatabase db, int idHunt){
        try {
            Log.v("db log", "prima di photoToCheck");
            db.execSQL("UPDATE HUNT SET photoToCheck = photoToCheck - 1 WHERE idHunt =" + idHunt + ";");
            Log.v("db log", "dopo di photoToCheck");


        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean notifyPhotoHasArrived(SQLiteDatabase db, int idHunt){
        try {
            Log.v("db log", "prima di photoToCheck");
            db.execSQL("UPDATE HUNT SET photoToCheck = photoToCheck + 1 WHERE idHunt =" + idHunt + ";");
            Log.v("db log", "dopo di photoToCheck");


        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void updateWinner(SQLiteDatabase db, int idHunt, int idTeam, String nameTeam){
        try {
            Log.v("db log", "prima updateWinner");
            db.execSQL("UPDATE HUNT SET idWinner = " + idTeam +", nameWinner = '" + nameTeam + "' WHERE idHunt =" + idHunt + ";");
            Log.v("db log", "dopo updateWinner");


        } catch (Exception e){
            e.printStackTrace();
        }
    }



}
