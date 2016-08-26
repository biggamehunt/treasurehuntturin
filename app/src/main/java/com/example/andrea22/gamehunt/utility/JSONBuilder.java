package com.example.andrea22.gamehunt.utility;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONBuilder {

	public JSONObject getJSONStage(Cursor stage, int numStage) throws JSONException {


		JSONObject jStage = new JSONObject();
		jStage.put("numStage", numStage);
		jStage.put("ray", stage.getString(stage.getColumnIndex("ray")));
		jStage.put("areaLat", stage.getString(stage.getColumnIndex("areaLat")));
		jStage.put("areaLon", stage.getString(stage.getColumnIndex("areaLon")));
		jStage.put("lat", stage.getString(stage.getColumnIndex("lat")));
		jStage.put("lon", stage.getString(stage.getColumnIndex("lon")));
		jStage.put("clue", stage.getString(stage.getColumnIndex("clue")));
		jStage.put("isLocationRequired", stage.getString(stage.getColumnIndex("isLocationRequired")));
		jStage.put("isPhotoRequired", stage.getString(stage.getColumnIndex("isPhotoRequired")));
		jStage.put("isCheckRequired", stage.getString(stage.getColumnIndex("isCheckRequired")));
		jStage.put("numUserToFinish", stage.getString(stage.getColumnIndex("numUserToFinish")));
		Log.v("JSONBuilder", "isLocationRequired:"+ stage.getString(stage.getColumnIndex("isLocationRequired")));

		return jStage;
	}

	public JSONObject getJSONTeam(Cursor team) throws JSONException {


		JSONObject jTeam = new JSONObject();
		jTeam.put("name",team.getString(team.getColumnIndex("name")));
		jTeam.put("slogan",team.getString(team.getColumnIndex("slogan")));

		String jUsers = team.getString(team.getColumnIndex("users"));

		String[] jUsersSplit = jUsers.split("\\|");
		JSONArray users = new JSONArray();
		JSONObject user;

		for (int i = 0; i < jUsersSplit.length; i ++){
			user = new JSONObject();
			user.put("username",jUsersSplit[i]);

			users.put(user);
		}
		jTeam.put("users",users);


		return jTeam;
	}










	public JSONObject getJSONHunt(Cursor hunt, int idUser) throws JSONException {


		JSONObject jHunt = new JSONObject();
		jHunt.put("name", hunt.getString(hunt.getColumnIndex("name")));
		jHunt.put("description", hunt.getString(hunt.getColumnIndex("description")));
		jHunt.put("maxTeam", hunt.getString(hunt.getColumnIndex("maxTeam")));
		jHunt.put("timeStart", hunt.getString(hunt.getColumnIndex("timeStart")));
		jHunt.put("timeEnd", hunt.getString(hunt.getColumnIndex("timeEnd")));
		jHunt.put("idUser", idUser);

		return jHunt;
	}


}
