package com.zlei.checkInTool;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MPLACESActivity extends ListActivity {

    private static ArrayList<MVenues> venues = new ArrayList<MVenues>();
    public static ArrayList<String> venueNames = new ArrayList<String>();
    public static ArrayList<String[]> venueCoordinates = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_mplaces);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    private class getVenuesTask extends AsyncTask<String, Void, String> {
        JSONArray venuesJA;

        protected String doInBackground(String... url) {
            try {
                URL u = new URL(url[0]);
                int timeout = 4000;
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Content-length", "0");
                c.setUseCaches(true);
                String cookieString = CookieManager.getInstance().getCookie(url[0]);
                c.setRequestProperty("Cookie", cookieString);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(timeout);
                c.setReadTimeout(timeout);
                c.connect();
                int status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));

                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        try {
                            JSONObject response = new JSONObject(sb.toString());
                            venuesJA = (JSONArray) response.get("venues");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return sb.toString();
                }
            } catch (MalformedURLException ex) {
            } catch (IOException ex) {
            }
            return null;
        }

        protected void onPostExecute(String result) {
            Log.i("places", venuesJA.toString());
            setNearbyVenues(venuesJA);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        //Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
        if (venues.get(position).getState().equals("checkable")) {
            Intent intent = new Intent(this, MCheckInActivity.class);
            intent.putExtra("selectedVenue", position);
            startActivity(intent);
        } else
            Toast.makeText(this, "Too far! Cannot check in here!", Toast.LENGTH_SHORT).show();
    }

    private void updateData() {
        requestVenuesNearby();
    }

    private void requestVenuesNearby() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        //String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //Location location = locationManager.getLastKnownLocation(provider);
        String url = "http://m.s.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77/mplaces/ads/fetch" +
                "?coordinates[latitude]=42.3523505&coordinates[longitude]=-71.0692305&coordinates[accuracy]=10";
        if (location == null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            url = "http://m.s.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77/mplaces/ads/fetch?coordinates" +
                    "[latitude]=" + lat + "&coordinates[longitude]=" + lng + "&coordinates[accuracy]=10";
            Toast.makeText(this, "Update!!", Toast.LENGTH_SHORT).show();
        }
        new getVenuesTask().execute(url);
    }

    private void setNearbyVenues(JSONArray jsonArray) {
        DecimalFormat df=new DecimalFormat("0.000");
        String[] coordinates;
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    coordinates = new String[2];
                    MVenues venue = new MVenues((JSONObject) jsonArray.get(i));
                    venues.add(i, venue);
                    venueNames.add(i, venue.getName());
                    coordinates[0] = df.format(Double.valueOf(venue.getLat()));
                    coordinates[1] = df.format(Double.valueOf(venue.getLng()));
                    venueCoordinates.add(i, coordinates);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, venueNames);
        setListAdapter(adapter);
    }

    public static ArrayList<MVenues> getVenues() {
        return venues;
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
}
