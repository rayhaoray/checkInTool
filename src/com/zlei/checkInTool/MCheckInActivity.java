package com.zlei.checkInTool;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MCheckInActivity extends Activity {

    private MVenues currentVenue;
    private boolean isSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mplaces_check_in);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        currentVenue = MVenuesActivity.getVenues().get(intent.getIntExtra("selectedVenue", 0));
        TextView venueName_text = (TextView) this.findViewById(R.id.mplaces_venue_name);
        TextView venueId_text = (TextView) this.findViewById(R.id.mplaces_venue_id);
        TextView venueMajor_text = (TextView) this.findViewById(R.id.mplaces_venue_major);
        TextView venueLocation_text = (TextView) this.findViewById(R.id.mplaces_venue_location);
        TextView venueHereNow_text = (TextView) this.findViewById(R.id.mplaces_venue_herenow);
        TextView venueStats_text = (TextView) this.findViewById(R.id.mplaces_venue_stats);
        Button checkin_btn = (Button) this.findViewById(R.id.mplaces_check_in_button);

        venueName_text.setText(currentVenue.getName());
        venueId_text.setText(currentVenue.getName());
        venueLocation_text.setText("Lat: " + currentVenue.getLat() + "\nLng: " + currentVenue.getLng());
        //venueMajor_text.setText(currentVenue.getMayor().getUser().getBio());
        venueHereNow_text.setText(currentVenue.getAddress());
        //venueStats_text.setText(getStats(currentVenue));
        checkin_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkin();
            }
        });
    }

    private void checkin() {
        String url = "http://m.s.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77" +
                "/mplaces/ads/check_in.json?placement_id=mplaces";
        new checkInTask().execute(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class checkInTask extends AsyncTask<String, Void, String> {
        String request;
        protected String doInBackground(String... url) {
            try {
                URL u = new URL(url[0]);
                int timeout = 4000;
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Content-type", "application/json");
                c.setUseCaches(true);
                c.setDoInput(true);
                c.setDoOutput(true);
                String cookieString = CookieManager.getInstance().getCookie(url[0]);
                c.setRequestProperty("Cookie", cookieString);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(timeout);
                c.setReadTimeout(timeout);
                JSONObject location = new JSONObject();
                JSONObject venue = new JSONObject();
                JSONObject params = new JSONObject();
                try {
                    location.put("latitude", currentVenue.getLat());
                    location.put("longitude", currentVenue.getLng());
                    location.put("accuracy", 0);
                    venue.put("id", currentVenue.getID());
                    venue.put("state", currentVenue.getState());
                    venue.put("name", currentVenue.getName());
                    venue.put("distance", currentVenue.getDistance());
                    venue.put("brand_id", currentVenue.getBrandID());
                    venue.put("family_id", "");
                    venue.put("primary_category_id", currentVenue.getCategoryID());
                    params.put("app_id", "aba6ba56b63680cad063e987df52a71e620dbc77");
                    params.put("venue", venue);
                    params.put("format", "json");
                    params.put("coordinates", location);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OutputStream os = new BufferedOutputStream(c.getOutputStream());
                os.write((params.toString()).getBytes("UTF-8"));
                request = params.toString();
                os.flush();
                os.close();
                c.connect();
                int status = c.getResponseCode();
                Log.i("SessionM places_code", status + c.getResponseMessage());
                switch (status) {
                    case 200:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));

                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        try {
                            JSONObject response = new JSONObject(sb.toString());
                            Log.i("SessionM response JSON", response.toString());
                            isSuccess = response.get("success").equals(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return sb.toString();
                }
            } catch (MalformedURLException ex) {
                Log.i("SessionM places_error: ", ex.toString());
            } catch (IOException ex) {
                Log.i("SessionM places_error: ", ex.toString());
            }
            return null;
        }

        protected void onPostExecute(String result) {
            Log.i("SessionM places_request: ", request);
            if(result != null && isSuccess)
                Toast.makeText(MCheckInActivity.this, "Done!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(MCheckInActivity.this, "Error!", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
