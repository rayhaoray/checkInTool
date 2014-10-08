package com.zlei.checkInTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends Activity implements GoogleMap.OnInfoWindowClickListener {

    ArrayList<String> venueNames;
    ArrayList<String[]> venueCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        map.setOnInfoWindowClickListener(this);

        if (MVenuesActivity.venueNames != null) {
            venueNames = MVenuesActivity.venueNames;
            venueCoordinates = MVenuesActivity.venueCoordinates;
        }
        String[] coreCoor = venueCoordinates.get(0);
        double coreLat = Double.valueOf(coreCoor[0]);
        double coreLng = Double.valueOf(coreCoor[1]);
        LatLng core = new LatLng(coreLat, coreLng);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(core, 16));

        for (String name : venueNames) {
            int i = venueNames.indexOf(name);
            coreCoor = venueCoordinates.get(i);
            coreLat = Double.valueOf(coreCoor[0]);
            coreLng = Double.valueOf(coreCoor[1]);
            core = new LatLng(coreLat, coreLng);
            if (coreCoor[2].equals("checkable")) {
                map.addMarker(new MarkerOptions()
                        .title(name)
                        .snippet("Click to check in here!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(core));
            } else {
                map.addMarker(new MarkerOptions()
                        .title(name)
                        .position(core));
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int position = venueNames.indexOf(marker.getTitle());
        if (venueCoordinates.get(position)[2].equals("checkable")) {
            Intent i = new Intent(this, MCheckInActivity.class);
            i.putExtra("selectedVenue", position);
            startActivity(i);
        }
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