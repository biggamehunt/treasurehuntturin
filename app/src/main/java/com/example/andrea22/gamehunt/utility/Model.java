package com.example.andrea22.gamehunt.utility;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Simone on 16/06/2016.
 */

public class Model {
    public static final String appLog = "PDM15app3";

    private static Model instance = null;
    private Context applicationContext = null;

    //private ArrayList<Course> courses;

    private DBHelper dbHelper = null;


    private Model (Context context) {
        this.applicationContext = context;

        this.dbHelper = new DBHelper(this.applicationContext);
    }


    public static Model getInstance(Context context) {
        if(instance==null) {
            instance = new Model(context.getApplicationContext());
        }
        return instance;
    }

/**
    public ArrayList<Course> getCourses() {
        ArrayList<Course> corsi = new ArrayList<Course>();

        try {
            String strCorsi =  CoursesNetHelper.getJSON(applicationContext.getResources().getString(R.string.restCourseList), 10000);
            JSONArray jsonCorsi = new JSONArray(strCorsi);

            int count = jsonCorsi.length();

            for(int i=0; i<count; i++) {
                JSONObject obj = jsonCorsi.optJSONObject(i);
                if(obj!=null) {
                    Course course = new Course();
                    course.setNome(obj.optString("nome", ""));
                    course.setCodice(obj.optString("codice", ""));
                    corsi.add(course);
                }
            }
        } catch (JSONException e) {
            Log.e(appLog, "Errore nei dati: " + e);
        }

        return corsi;
    }

/**
    public Course getCourseDetail(String code) {
        Course course = new Course();
        course.setCodice(code);

        try {
            String corso =  CoursesNetHelper.getJSON(applicationContext.getResources().getString(R.string.restCourseDetail)+code, 10000);
            JSONArray jsonArray = new JSONArray(corso);
            JSONObject obj = jsonArray.optJSONObject(0);

            course.setNome(obj.optString("nome", ""));
            course.setCodice(obj.optString("codice", ""));
            course.setMateriale(obj.optString("materiale", ""));
            course.setDescrizione(obj.optString("obiettivi", ""));
        } catch (JSONException e) {
            Log.e(appLog, "Errore nei dati: "+e);
        }

        return course;
    }

 **/
}

