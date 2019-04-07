package com.example.imoto;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String,String> getPlace(JSONObject googlePlaceJson){

        HashMap<String,String> googlePLacesMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        Log.d("DataParser","jsonobject ="+googlePlaceJson.toString());

        //Fetch the data
        try {
            if (!googlePlaceJson.isNull("name")){

                placeName = googlePlaceJson.getString("name");

            }
            if (!googlePlaceJson.isNull("vicinity")){

                vicinity = googlePlaceJson.getString("vicinity");

            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            googlePLacesMap.put("place_name", placeName);
            googlePLacesMap.put("vicinity", vicinity);
            googlePLacesMap.put("lat", latitude);
            googlePLacesMap.put("lng", longitude);
            googlePLacesMap.put("reference", reference);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePLacesMap;
    }

    //Method to return hashmap of found places
    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray){

        int count = jsonArray.length();
        List<HashMap<String,String>> placesList = new ArrayList<>();
        HashMap<String,String> placeMap = null;

        for (int i = 0; i<count; i++){

            //Fetch one place and add to hasmap
            try {
                placeMap = getPlace((JSONObject)jsonArray.get(i));
                placesList.add(placeMap);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;

    }

    //Parse the Json data and send it to getPlaces method
    public List<HashMap<String,String>> parse(String jsonData){

        JSONArray jsonArray =null;
        JSONObject jsonObject;

        Log.d("json data", jsonData);

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }
}
