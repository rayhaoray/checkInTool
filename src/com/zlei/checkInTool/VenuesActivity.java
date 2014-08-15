package com.zlei.checkInTool;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.criterias.TipsCriteria;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.FoursquareVenuesRequestListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.listeners.TipsRequestListener;
import br.com.condesales.models.Tip;
import br.com.condesales.models.Venue;

public class VenuesActivity extends ListActivity implements
        AccessTokenRequestListener, ImageRequestListener, LocationListener {

    private EasyFoursquareAsync async;
    private static ArrayList<Venue> venues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues);
        async = MainActivity.getAsync();
        venues = new ArrayList<Venue>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null)
            requestVenuesNearby(location);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
        Venue selectedVenue = venues.get(position);
        Intent intent = new Intent(VenuesActivity.this, CheckInActivity.class);
        intent.putExtra("selectedVenue", position);
        startActivity(intent);
    }

    private void requestVenuesNearby(Location location) {
        VenuesCriteria criteria = new VenuesCriteria();
        criteria.setLocation(location);
        async.getVenuesNearby(new FoursquareVenuesRequestListener() {

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(VenuesActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVenuesFetched(ArrayList<Venue> venues) {
                setNearbyVenues(venues);
            }
        }, criteria);
    }

    private void setNearbyVenues(ArrayList<Venue> venues) {
        this.venues = venues;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getNearbyVenuesInfo());
        setListAdapter(adapter);
    }

    private ArrayList<String> getNearbyVenuesInfo() {
        ArrayList<String> venuesInfo = new ArrayList<String>();
        for (Venue v : venues) {
            venuesInfo.add(v.getName());
        }
        return venuesInfo;
    }

    public static ArrayList<Venue> getNearbyVenues() {
        return venues;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.venues, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onError(String errorMsg) {
    }

    @Override
    public void onImageFetched(Bitmap bmp) {
    }

    @Override
    public void onAccessGrant(String accessToken) {
    }

    @Override
    public void onLocationChanged(Location location) {
            requestVenuesNearby(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
}
