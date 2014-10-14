package com.zlei.checkInTool;

import org.json.JSONException;
import org.json.JSONObject;

public class MVenues extends JSONObject {
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

    public String getID() {
        try {
            return venue.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getIconType() {
        try {
            return venue.getJSONObject("icon").getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getIconUrl() {
        try {
            return venue.getJSONObject("icon").getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getDistance() {
        try {
            return venue.getString("distance");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getDistanceLabel() {
        try {
            return venue.getString("distance_label");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getState() {
        try {
            return venue.getString("state");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getBrandID() {
        try {
            return venue.getString("brand_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getCategory() {
        try {
            return venue.getJSONArray("categories").getJSONObject(0).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPoints() {
        try {
            return venue.getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getAddress() {
        try {
            return venue.getJSONObject("location").getString("address") +
                   venue.getJSONObject("location").getString("city") + ", " +
                   venue.getJSONObject("location").getString("state") ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getCategoryID() {
        try {
            return venue.getString("primary_category_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
