package com.zlei.checkInTool;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MVenuesActivity extends ListActivity {

    private static ArrayList<MVenues> venues = new ArrayList<MVenues>();
    public static ArrayList<String> venueNames = new ArrayList<String>();
    public static ArrayList<String[]> venueCoordinates = new ArrayList<String[]>();
    public static ArrayList<Drawable> venueIcons = new ArrayList<Drawable>();
    private String[] venueUrls = new String[100];

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_mplaces);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionM.getInstance().onActivityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionM.getInstance().onActivityResume(this);
        updateData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SessionM.getInstance().onActivityPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SessionM.getInstance().onActivityStop(this);
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
            progressDialog.dismiss();
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
        String url = "https://api.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77/mplaces/ads/fetch" +
                "?coordinates[latitude]=42.3523505&coordinates[longitude]=-71.0692305&coordinates[accuracy]=10";
        //String url = "http://m.s.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77/mplaces/ads/fetch" +
        //        "?coordinates[latitude]=42.3523505&coordinates[longitude]=-71.0692305&coordinates[accuracy]=10";
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            url = "https://api.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77/mplaces/ads/fetch?coordinates" +
                    "[latitude]=" + lat + "&coordinates[longitude]=" + lng + "&coordinates[accuracy]=10";
            //url = "http://m.s.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77/mplaces/ads/fetch?coordinates" +
            //        "[latitude]=" + lat + "&coordinates[longitude]=" + lng + "&coordinates[accuracy]=10";
            Toast.makeText(this, "Update!!", Toast.LENGTH_SHORT).show();
        }
        new getVenuesTask().execute(url);
    }

    private void setNearbyVenues(JSONArray jsonArray) {
        DecimalFormat df = new DecimalFormat("0.000");
        String[] coordinates;
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    coordinates = new String[3];
                    MVenues venue = new MVenues((JSONObject) jsonArray.get(i));
                    venues.add(i, venue);
                    venueNames.add(i, venue.getName());
                    coordinates[0] = df.format(Double.valueOf(venue.getLat()));
                    coordinates[1] = df.format(Double.valueOf(venue.getLng()));
                    coordinates[2] = venue.getState();
                    venueCoordinates.add(i, coordinates);
                    if (venue.getState().equals("checkable"))
                        venueUrls[i] = venue.getIconUrl();
                    else
                        venueUrls[i] = "";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        new getIconsTask().execute(venueUrls);
        MyArrayAdapter adapter = new MyArrayAdapter(this,
                R.layout.venues_list_item, venueNames);
        setListAdapter(adapter);
    }

    public class MyArrayAdapter extends ArrayAdapter<String> {
        private final int resource;
        private final Context context;
        private final ArrayList<String> objects;

        public MyArrayAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            this.resource = resource;
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(resource, parent,
                    false);
            TextView text_name = (TextView) rowView.findViewById(R.id.venue_list_name);
            TextView text_desc = (TextView) rowView.findViewById(R.id.venue_list_desc);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.venue_icon);
            text_name.setText(objects.get(position));
            text_desc.setText(objects.get(position));
            //if(!venueIcons.isEmpty())
            //    imageView.setImageDrawable(venueIcons.get(position));
            return rowView;
        }
    }

    private class getIconsTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            ArrayList<Drawable> icons = new ArrayList<Drawable>();
            int i = 0;
            for (String url : strings) {
                if (url != null)
                    try {
                        InputStream is = (InputStream) new URL(url).getContent();
                        Drawable d = Drawable.createFromStream(is, "src name");
                        icons.add(i, d);
                    } catch (Exception e) {
                    }
                i++;
            }
            venueIcons = icons;
            return null;
        }
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
