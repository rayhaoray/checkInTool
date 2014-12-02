package com.zlei.checkInTool;

import java.util.ArrayList;
import java.util.Random;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.FoursquareVenuesRequestListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.models.Venue;

public class VenuesActivity extends ListActivity implements
        AccessTokenRequestListener, ImageRequestListener, LocationListener, OnRefreshListener {

    private EasyFoursquareAsync async;
    private static ArrayList<Venue> venues;

    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this)
                // Mark All Children as pullable
                .allChildrenArePullable()
                .listener(this)
                // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);
        async = MainActivity.getAsync();
        venues = new ArrayList<Venue>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        //Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
        Venue selectedVenue = venues.get(position);
        if (async != null) {
            Intent intent = new Intent(VenuesActivity.this, CheckInActivity.class);
            intent.putExtra("selectedVenue", position);
            startActivity(intent);
        }
    }

    private void updateData(boolean isAccurate) {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            if (!isAccurate) {
                location.setLatitude(location.getLatitude() + this.generateErrorNumber());
                location.setLongitude(location.getLongitude() + this.generateErrorNumber());
            }
            requestVenuesNearby(location);
        }
    }

    private void requestVenuesNearby(Location location) {
        VenuesCriteria criteria = new VenuesCriteria();
        criteria.setLocation(location);
        async.getVenuesNearby(new FoursquareVenuesRequestListener() {

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(VenuesActivity.this, "Request error!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRefreshStarted(View view) {
        setNearbyVenues(new ArrayList<Venue>());
        /**
         * Simulate Refresh with 4 seconds sleep
         */
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                // Notify PullToRefreshLayout that the refresh has finished
                mPullToRefreshLayout.setRefreshComplete();

            }
        }.execute();
        updateData(false);
    }

    private double generateErrorNumber() {
        double rangeMax = 0.001;
        double rangeMin = -0.001;
        Random r = new Random();
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        return randomValue;
    }
}
