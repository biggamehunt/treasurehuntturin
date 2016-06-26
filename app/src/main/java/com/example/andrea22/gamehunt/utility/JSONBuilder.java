package com.example.andrea22.gamehunt.utility;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONBuilder {

	public JSONObject getJSONStage(Cursor stage) throws JSONException {


		JSONObject jStage = new JSONObject();
		jStage.put("numStage", stage.getString(stage.getColumnIndex("idStage")));
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

		return jStage;
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
