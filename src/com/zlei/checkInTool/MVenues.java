package com.zlei.checkInTool;

import org.json.JSONException;
import org.json.JSONObject;

public class MVenues extends JSONObject{
    JSONObject venue;

    public MVenues(JSONObject venue) {
        this.venue = venue;
    }

    public String getName() {
        try {
            return venue.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getLat() {
        try {
            return venue.getJSONObject("location").getString("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getLng() {
        try {
            return venue.getJSONObject("location").getString("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getAddress() {
        try {
            return venue.getJSONObject("location").getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
