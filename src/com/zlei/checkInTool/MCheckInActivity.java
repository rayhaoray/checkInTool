package com.zlei.checkInTool;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
        int position = intent.getIntExtra("selectedVenue", 0);
        currentVenue = MVenuesActivity.getVenues().get(position);
        TextView venueName_text = (TextView) this.findViewById(R.id.mplaces_venue_name);
        //TextView venueId_text = (TextView) this.findViewById(R.id.mplaces_venue_id);
        TextView venueMajor_text = (TextView) this.findViewById(R.id.mplaces_venue_major);
        TextView venueLocation_text = (TextView) this.findViewById(R.id.mplaces_venue_location);
        TextView venueHereNow_text = (TextView) this.findViewById(R.id.mplaces_venue_herenow);
        TextView venueStats_text = (TextView) this.findViewById(R.id.mplaces_venue_stats);
        ImageView venueImage = (ImageView) this.findViewById(R.id.mplaces_venue_icon);
        Button checkin_btn = (Button) this.findViewById(R.id.mplaces_check_in_button);

        venueName_text.setText(currentVenue.getName());
        //venueId_text.setText("Name: " + currentVenue.getName());
        venueLocation_text.setText("Lat: " + currentVenue.getLat() + "\nLng: " + currentVenue.getLng());
        venueMajor_text.setText("Category: " + currentVenue.getCategory());
        venueHereNow_text.setText("Address: " + currentVenue.getAddress());
        if (!MVenuesActivity.venueIcons.isEmpty() && position < MVenuesActivity.venueIcons.size())
            venueImage.setImageDrawable(MVenuesActivity.venueIcons.get(position));
        venueStats_text.setText("mPOINTS: " + currentVenue.getPoints());
        checkin_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkin();
            }
        });

        // Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_venue)).getMap();

        double coreLat = Double.valueOf(currentVenue.getLat());
        double coreLng = Double.valueOf(currentVenue.getLng());
        LatLng core = new LatLng(coreLat, coreLng);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(core, 15));
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.addMarker(new MarkerOptions()
                .title(currentVenue.getName())
                .snippet("Click to check in here!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(core));
    }

    private void checkin() {
        String url = "https://api.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77" +
                "/mplaces/ads/check_in.json?placement_id=mplaces";
        //String url = "http://m.s.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77" +
        //        "/mplaces/ads/check_in.json?placement_id=mplaces";
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
            if (result != null && isSuccess) {
                Toast.makeText(MCheckInActivity.this, "Success!", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(MCheckInActivity.this, "Success!", Toast.LENGTH_LONG).show();
            pushNotification("");
            finish();
        }
    }

    public void pushNotification(String action) {
        NotificationCompat.Builder mBuilder;
        Intent resultIntent;
        if (action.equals("notifyCheckIn")) {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("CheckIn Available!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText("CheckIn mPLACES!");
            resultIntent = new Intent(this, MCheckInActivity.class);
            resultIntent.putExtra("selectedVenue", MVenuesActivity.venueNames.indexOf(currentVenue.getName()));
        } else {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("New Achievement!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText("Claim your achievement for mPoints!");
            resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("startFromNotification", true);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
